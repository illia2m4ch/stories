package space.dam.stories.ui.screens.story;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import space.dam.stories.R;
import space.dam.stories.models.Story;
import space.dam.stories.ui.screens.BaseActivity;
import space.dam.stories.ui.screens.profile.ProfileActivity;
import space.dam.stories.utils.StoryUtils;
import space.dam.stories.utils.ViewUtils;
import space.dam.stories.viewmodels.StoryViewModel;

/**
 * Активность, отобрадающая детали истории
 */
public class StoryActivity extends BaseActivity {

    public static String EXTRA_UID = "StoryActivity.EXTRA_UID";
    // Нужно ли вызвать метод finish() при закрытии
    public static String EXTRA_FINISH_ACTIVITY = "StoryActivity.EXTRA_FINISH_ACTIVITY";

    /**
     * View model
     */
    private StoryViewModel viewModel;

    /**
     * Views
     */
    private CircleImageView creatorPhoto;
    private TextView creatorName;
    private TextView creationDate;
    private TextView storyType;
    private TextView title;
    private TextView description;

    private LinearLayout contentLayout;
    private ProgressBar progressBar;

    private RelativeLayout profileLayout;
    private View.OnClickListener onClickProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openCreatorProfile();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        setDarkSystemBar();
        initAllViews();
        initViewModel();
    }

    /**
     * Инициализация всех view
     */
    private void initAllViews() {
        creatorPhoto = findViewById(R.id.creatorPhoto);
        creatorName = findViewById(R.id.creatorName);
        creationDate = findViewById(R.id.creationDate);
        storyType = findViewById(R.id.storyType);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);

        progressBar = findViewById(R.id.progressBar);
        contentLayout = findViewById(R.id.content);

        profileLayout = findViewById(R.id.profileLayout);
        profileLayout.setOnClickListener(onClickProfileListener);
    }

    /**
     * Инициализация view model
     */
    private void initViewModel() {
        ViewModelProvider provider = new ViewModelProvider(this);
        viewModel = provider.get(StoryViewModel.class);
        viewModel.init();
        viewModel.getStory().observe(this, new Observer<Story>() {
            @Override
            public void onChanged(Story story) {
                setContentLayout(story);
            }
        });
        viewModel.getStatus().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer status) {
                if (status == StoryViewModel.STATUS_LOADING) {
                    progressBar.setVisibility(View.VISIBLE);
                    contentLayout.setVisibility(View.INVISIBLE);
                }
                else {
                    progressBar.setVisibility(View.INVISIBLE);
                    contentLayout.setVisibility(View.VISIBLE);
                    Animation anim = AnimationUtils.loadAnimation(
                            getApplicationContext(),
                            android.R.anim.fade_in
                    );
                    contentLayout.startAnimation(anim);
                }

                if (status == StoryViewModel.STATUS_ERROR) {
                    ViewUtils.showToast(StoryActivity.this, R.string.error);
                }
            }
        });
        loadStory();
    }

    /**
     * Передает полученный uid в viewModel
     */
    private void loadStory() {
        Intent intent = getIntent();
        String uid = intent.getStringExtra(EXTRA_UID);
        if (uid == null) {
            ViewUtils.showToast(this, R.string.error);
            return;
        }
        viewModel.loadStory(uid);
    }

    /**
     * Отображение контента
     */
    private void setContentLayout(Story story) {
        if (story == null) {
            title.setText(R.string.this_story_was_deleted);
            creatorPhoto.setVisibility(View.GONE);
            creatorName.setVisibility(View.GONE);
            creationDate.setVisibility(View.GONE);
            storyType.setVisibility(View.GONE);
            return;
        }
        ViewUtils.updateProfilePhoto(story.getCreatorPhoto(), creatorPhoto);
        creatorName.setText(story.getCreatorName());
        creationDate.setText(StoryUtils.getFormatedCreationDate(story.getCreationDate()));
        StoryUtils.setStoryTypeStyle(StoryActivity.this, storyType, story.getType());
        title.setText(story.getTitle());
        description.setText(story.getDescription());
    }

    /**
     * Открывает профиль создателя истории
     */
    private void openCreatorProfile() {
        if (getIntent().getBooleanExtra(EXTRA_FINISH_ACTIVITY, false)) {
            finish();
            return;
        }
        Intent intent = new Intent(this, ProfileActivity.class);
        String uid = viewModel.getStory().getValue().getCreatorUid();
        intent.putExtra(ProfileActivity.EXTRA_UID, uid);
        startActivity(intent);
    }
}

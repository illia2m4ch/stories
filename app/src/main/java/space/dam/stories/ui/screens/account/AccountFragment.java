package space.dam.stories.ui.screens.account;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import space.dam.stories.R;
import space.dam.stories.models.Story;
import space.dam.stories.models.User;
import space.dam.stories.repositories.Auth;
import space.dam.stories.repositories.Database;
import space.dam.stories.ui.adapters.MyStoriesAdapter;
import space.dam.stories.ui.screens.createStory.CreateStoryActivity;
import space.dam.stories.utils.ViewUtils;
import space.dam.stories.viewmodels.UserInfoViewModel;

public class AccountFragment extends Fragment {

    /**
     * ViewModel
     */
    private UserInfoViewModel viewModel;

    /**
     * Views
     */
    private TextView name;
    private TextView totalStories;
    private TextView totalSubscribers;
    private FloatingActionButton buttonSettings;
    private View.OnClickListener buttonSettingsOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openSettings();
        }
    };
    private Button buttonCreateStory;
    private View.OnClickListener buttonCreateStoryOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openCreateStoryActivity();
        }
    };
    private RecyclerView stories;
    private MyStoriesAdapter adapter;
    private MyStoriesAdapter.MenuListener storyMenuListener = new MyStoriesAdapter.MenuListener() {
        @Override
        public void onItemClick(String uid, int itemId) {
            switch (itemId) {
                case R.id.delete:
                    showConfirmDeleteStoryPopup(uid);
                    break;
                default:
                    break;
            }
        }
    };
    private AppBarLayout appBarLayout;
    private AppBarLayout.OnOffsetChangedListener onOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            swipeRefreshLayout.setEnabled(verticalOffset == 0);
        }
    };

    private ImageView profilePhoto;

    /**
     * Swipe to refresh layout
     */
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            viewModel.refresh();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);
        initAllViews(v);
        initViewModel();
        return v;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (viewModel== null) return;
        int status = viewModel.getStatus().getValue();
        if (!hidden && status != UserInfoViewModel.STATUS_LOADED) viewModel.refresh();
    }

    private void initAllViews(View v) {
        name = v.findViewById(R.id.name);
        totalStories = v.findViewById(R.id.totalStories);
        totalSubscribers = v.findViewById(R.id.totalSubscribers);

        buttonSettings = v.findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(buttonSettingsOnClickListener);

        buttonCreateStory = v.findViewById(R.id.buttonCreateStory);
        buttonCreateStory.setOnClickListener(buttonCreateStoryOnClickListener);

        stories = v.findViewById(R.id.stories);
        adapter = new MyStoriesAdapter();
        adapter.setMenuListener(storyMenuListener);
        stories.setAdapter(adapter);

        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        appBarLayout = v.findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(onOffsetChangedListener);

        profilePhoto = v.findViewById(R.id.profilePhoto);

        // (не удержался)
        if (Auth.isAppCreator(Auth.getCurrentUserUid())) {
            v.findViewById(R.id.textAppCreator).setVisibility(View.VISIBLE);
        }
    }

    private void initViewModel() {
        ViewModelProvider provider = new ViewModelProvider(this);
        viewModel = provider.get(UserInfoViewModel.class);
        viewModel.init(Auth.getCurrentUserUid());
        viewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                displayUserInfo(user);
            }
        });
        viewModel.getStories().observe(getViewLifecycleOwner(), new Observer<List<Story>>() {
            @Override
            public void onChanged(List<Story> stories) {
                displayStories(stories);
            }
        });
        viewModel.getStatus().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer status) {
                if (status == UserInfoViewModel.STATUS_LOADING ||
                        status == UserInfoViewModel.STATUS_USER_INFO_LOADED) {
                    swipeRefreshLayout.setRefreshing(true);
                }
                else {
                    swipeRefreshLayout.setRefreshing(false);
                }

                if (status == UserInfoViewModel.STATUS_ERROR) {
                    ViewUtils.showToast(getContext(), R.string.error);
                }
            }
        });
    }

    /**
     * Отображение информаци о пользователе
     */
    private void displayUserInfo(User user) {
        if (user == null) return;
        name.setText(user.getName());
        totalStories.setText(String.valueOf(user.getTotalStories()));
        totalSubscribers.setText(String.valueOf(user.getTotalSubscribers()));
        ViewUtils.updateProfilePhoto(user.getPhoto(), profilePhoto);
    }

    /**
     * Отображение историй
     */
    private void displayStories(List<Story> stories) {
        adapter.setStories(stories);
    }

    /**
     * Открывает окно с подтверждением удаления истории
     */
    private void showConfirmDeleteStoryPopup(final String uid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.confirm_delete_story)
                .setPositiveButton(R.string.cancel, null)
                .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Database.deleteStory(uid).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    ViewUtils.showToast(getContext(), R.string.story_deleted_successfully);
                                }
                                else ViewUtils.showToast(getContext(), R.string.error);
                            }
                        });
                    }
                });
        builder.show()
                .getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(getContext(), R.color.colorDanger)
        );
    }

    /**
     * Открывает окно настроек
     */
    private void openSettings() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Открывает CreateStoryActivity
     */
    private void openCreateStoryActivity() {
        Intent intent = new Intent(getContext(), CreateStoryActivity.class);
        startActivity(intent);
    }
}

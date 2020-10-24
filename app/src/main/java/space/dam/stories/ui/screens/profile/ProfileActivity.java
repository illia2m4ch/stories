package space.dam.stories.ui.screens.profile;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;

import java.util.List;

import space.dam.stories.R;
import space.dam.stories.models.Story;
import space.dam.stories.models.User;
import space.dam.stories.repositories.Auth;
import space.dam.stories.ui.adapters.UserStoriesAdapter;
import space.dam.stories.ui.screens.BaseActivity;
import space.dam.stories.utils.ViewUtils;
import space.dam.stories.viewmodels.SubscriptionViewModel;
import space.dam.stories.viewmodels.UserInfoViewModel;

public class ProfileActivity extends BaseActivity {

    public static String EXTRA_UID = "ProfileActivity.EXTRA_UID";

    private String uid;

    /**
     * View model
     */
    private UserInfoViewModel userInfoViewModel;
    private SubscriptionViewModel subscriptionViewModel;

    /**
     * Views
     */
    private TextView name;
    private TextView totalStories;
    private TextView totalSubscribers;
    private RecyclerView stories;
    private UserStoriesAdapter adapter;
    private AppBarLayout appBarLayout;
    private AppBarLayout.OnOffsetChangedListener onOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            swipeRefreshLayout.setEnabled(verticalOffset == 0);
        }
    };

    private Button buttonSubscription;
    private View.OnClickListener onSubscriptionClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            subscriptionViewModel.updateSubscription();
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
            userInfoViewModel.refresh();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setDarkSystemBar();
        uid = getIntent().getStringExtra(EXTRA_UID);
        initAllViews();
        initViewModels();
    }

    /**
     * Инициализация всех view
     */
    private void initAllViews() {
        name = findViewById(R.id.name);
        totalStories = findViewById(R.id.totalStories);
        totalSubscribers = findViewById(R.id.totalSubscribers);

        stories = findViewById(R.id.stories);
        adapter = new UserStoriesAdapter();
        stories.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(onOffsetChangedListener);

        buttonSubscription = findViewById(R.id.buttonSubscription);
        buttonSubscription.setOnClickListener(onSubscriptionClick);

        profilePhoto = findViewById(R.id.profilePhoto);

        // (не удержался)
        if (Auth.isAppCreator(uid)) {
            findViewById(R.id.textAppCreator).setVisibility(View.VISIBLE);
        }
    }

    /**
     * Инициализация ViewModels
     */
    private void initViewModels() {
        ViewModelProvider provider = new ViewModelProvider(this);

        userInfoViewModel = provider.get(UserInfoViewModel.class);
        initUserInfoViewModel();

        subscriptionViewModel = provider.get(SubscriptionViewModel.class);
        initSubscriptionViewModel();

    }

    private void initUserInfoViewModel() {
        userInfoViewModel.init(uid);
        userInfoViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                displayUserInfo(user);
            }
        });
        userInfoViewModel.getStories().observe(this, new Observer<List<Story>>() {
            @Override
            public void onChanged(List<Story> stories) {
                displayStories(stories);
            }
        });
        userInfoViewModel.getStatus().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer status) {
                if (status != UserInfoViewModel.STATUS_LOADED &&
                        status != UserInfoViewModel.STATUS_ERROR) {
                    swipeRefreshLayout.setRefreshing(true);
                }
                else {
                    swipeRefreshLayout.setRefreshing(false);
                }

                if (status == UserInfoViewModel.STATUS_ERROR) {
                    ViewUtils.showToast(ProfileActivity.this, R.string.error);
                }
            }
        });
        userInfoViewModel.refresh();
    }

    private void initSubscriptionViewModel() {
        subscriptionViewModel.init(uid);
        subscriptionViewModel.getSubscription().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean subscription) {
                if (subscription) {
                    ViewUtils.Button.setDefault(ProfileActivity.this, buttonSubscription);
                    buttonSubscription.setText(R.string.unsubscribe);
                }
                else {
                    ViewUtils.Button.setAccent(ProfileActivity.this, buttonSubscription);
                    buttonSubscription.setText(R.string.subscribe);
                }
            }
        });
        subscriptionViewModel.getStatus().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer status) {

                switch (status) {
                    case (SubscriptionViewModel.STATUS_ERROR):
                        ViewUtils.showToast(ProfileActivity.this, R.string.error);
                        break;
                    case (SubscriptionViewModel.STATUS_CAN_NOT_SUBSCRIBE):
                        buttonSubscription.setVisibility(View.GONE);
                        break;
                    case (SubscriptionViewModel.STATUS_SUBSCRIBED):
                        ViewUtils.showSnackbar(buttonSubscription, R.string.subscribed);
                        break;
                    case (SubscriptionViewModel.STATUS_UNSUBSCRIBED):
                        ViewUtils.showSnackbar(buttonSubscription, R.string.unsubscribed);
                        break;
                    case (SubscriptionViewModel.STATUS_SUBSCRIPTION_LIMIT):
                        ViewUtils.showSnackbar(buttonSubscription, R.string.subscription_limit_10);
                        break;
                }
            }
        });
    }

    /**
     * Отображение информаци о пользователе
     */
    private void displayUserInfo(User user) {
        if (user == null) return;
        ViewUtils.updateProfilePhoto(user.getPhoto(), profilePhoto);
        name.setText(user.getName());
        totalStories.setText(String.valueOf(user.getTotalStories()));
        totalSubscribers.setText(String.valueOf(user.getTotalSubscribers()));
    }

    /**
     * Отображение историй
     */
    private void displayStories(List<Story> stories) {
        adapter.setStories(stories);
    }
}

package space.dam.stories.ui.screens.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;

import space.dam.stories.R;
import space.dam.stories.repositories.Auth;
import space.dam.stories.ui.screens.BaseActivity;
import space.dam.stories.ui.screens.account.AccountFragment;
import space.dam.stories.ui.screens.createStory.CreateStoryActivity;
import space.dam.stories.ui.screens.search.SearchFragment;
import space.dam.stories.ui.screens.start.LogInActivity;
import space.dam.stories.ui.screens.stories.StoriesFragment;
import space.dam.stories.ui.screens.subscribtions.SubscriptionsFragment;


public class MainActivity extends BaseActivity {

    /**
     * Для специфической навигации необходимы следующие поля:
     * Code - для запуска CreateStoryActivity и контролирования его завершения
     * Результат
     */
    public static final int CREATE_STORY_REQUEST_CODE = 0;

    /**
     * Navigation
     */
    private FragmentManager fragmentManager;
    private SparseArray<Fragment> pages;
    public int currentPageId;

    private BottomNavigationView bottomNavigation;
    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    switch (itemId) {
                        case R.id.account_nav:
                            displayPage(R.id.account_nav);
                            return true;
                        case R.id.subscriptions_nav:
                            displayPage(R.id.subscriptions_nav);
                            return true;
                        case R.id.create_story_nav:
                            openCreateStoryActivity();
                            return true;
                        case R.id.stories_nav:
                            displayPage(R.id.stories_nav);
                            return true;
                        case R.id.search_nav:
                            displayPage(R.id.search_nav);
                            return true;
                        default:
                            return false;
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDarkSystemBar();
        checkUser();
        initAllViews();
        initNavigation();
    }

    /**
     * Инициализация навигации
     */
    private void initNavigation() {

        Fragment accountPage = new AccountFragment();
        Fragment subscriptionsPage = new SubscriptionsFragment();
        Fragment storiesPage = new StoriesFragment();
        Fragment searchPage = new SearchFragment();

        pages = new SparseArray<>(4);
        pages.append(R.id.account_nav, accountPage);
        pages.append(R.id.subscriptions_nav, subscriptionsPage);
        pages.append(R.id.stories_nav, storiesPage);
        pages.append(R.id.search_nav, searchPage);

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.container, accountPage).hide(accountPage)
                .add(R.id.container, subscriptionsPage).hide(subscriptionsPage)
                .add(R.id.container, storiesPage).hide(storiesPage)
                .add(R.id.container, searchPage).hide(searchPage)
                .commit();

        currentPageId = R.id.account_nav;
        bottomNavigation.setSelectedItemId(currentPageId);
    }

    /**
     * Установка страницы
     */
    private void displayPage(int id) {
        fragmentManager.beginTransaction()
                .hide(pages.get(currentPageId)).show(pages.get(id))
                .commit();

        currentPageId = id;
    }

    /**
     * Проверка на то, залогинен ли юзер
     */
    private void checkUser() {
        FirebaseUser currentUser = Auth.getCurrentUser();
        if (currentUser == null) openLogInActivity();
    }

    /**
     * Открывает logInActivity
     */
    private void openLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Инициализация всех views
     */
    private void initAllViews() {
        bottomNavigation = findViewById(R.id.navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(bottomNavigationListener);
    }

    /**
     * Открывает CreateStoryActivity
     */
    private void openCreateStoryActivity() {
        Intent intent = new Intent(this, CreateStoryActivity.class);
        startActivityForResult(intent, CREATE_STORY_REQUEST_CODE);
    }

    /**
     * Результат создания истории
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_STORY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                bottomNavigation.setSelectedItemId(currentPageId);
            }
            else if (resultCode == Activity.RESULT_OK){
                bottomNavigation.setSelectedItemId(R.id.account_nav);
            }
        }
    }
}

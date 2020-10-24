package space.dam.stories.ui.screens.main;

import android.util.SparseArray;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.Stack;

import space.dam.stories.ui.screens.account.AccountFragment;

/**
 * Обработчик навигации для MainActivity
 */
@Deprecated
public class MainNavigationHandler implements LifecycleObserver {

    public class MyServer implements LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void connect() {

        }

    }


    /**
     * Все возможные фрагменты и ключи к ним
     */
    private SparseArray<Stack<Fragment>> pages;

    private static final int PAGE_ACCOUNT = 0;
    private static final int PAGE_SUBSCRIPTIONS = 10;
    private static final int PAGE_CREATE_STORY = 20; // является новой activity
    private static final int PAGE_STORIES = 30;
    private static final int PAGE_SEARCH = 40;
    private static final int PAGE_SEARCH_RESULTS = 41;

    /**
     * Коллбек для отображения нужного фрагмента
     */
    public interface Callback {
        void onChanged();
    }

    private Callback callback;

    /**
     * Конструктор
     */
    public MainNavigationHandler() {
        Stack<Fragment> accountStack = new Stack<>();
        accountStack.push(new AccountFragment());
        pages.append(PAGE_ACCOUNT, accountStack);
    }

    /**
     * Свойства
     */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * Открыть окно
     */
    public void openPage(int page) {
        switch (page) {
            case PAGE_ACCOUNT:

        }
    }

}

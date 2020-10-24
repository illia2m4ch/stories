package space.dam.stories.viewmodels;

import androidx.lifecycle.ViewModel;

import space.dam.stories.repositories.Auth;

public class SettingsViewModel extends ViewModel {

    /**
     * Вызван ли метод init()
     */
    private boolean initialized = false;

    /**
     * Коллбек
     */
    public interface Callback {
        void logOut();
    }

    private Callback callback;

    /**
     * Инициализация
     */
    public void init(int step) {
        if (initialized) return;

        initialized = true;
    }

    /**
     * Свойства
     */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * Нажата кнопка выйти
     */
    public void onClickLogOut() {
        Auth.logOut();
        if (callback != null) callback.logOut();
    }
}

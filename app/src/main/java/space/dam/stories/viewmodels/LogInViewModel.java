package space.dam.stories.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import space.dam.stories.repositories.Auth;
import space.dam.stories.utils.Validator;

/**
 * Используется в LogInActivity
 */

public class LogInViewModel extends ViewModel {

    /**
     * Вызван ли метод init()
     */
    private boolean initialized = false;

    private MutableLiveData<Boolean> canLogin;
    private Callback callback;

    /**
     * Инициализация
     */
    public void init() {
        if (initialized) return;

        canLogin = new MutableLiveData<>();
        canLogin.setValue(false);

        initialized = true;
    }

    /**
     * Проверка входных данных
     */
    public void updateCanLogin(String email, String password) {
        boolean isValidate = Validator.validateEmail(email) == Validator.SUCCESS &&
                Validator.validatePassword(password) == Validator.SUCCESS;
        this.canLogin.setValue(isValidate);
    }

    public LiveData<Boolean> getCanLogin() {
        return canLogin;
    }

    /**
     * Попытка войти в аккаунт
     */
    public void login(String email, String password) {
        if (canLogin.getValue() == true) {
            Auth.logIn(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (callback != null) {
                        if (task.isSuccessful()) callback.onLogIn(true);
                        else callback.onLogIn(false);
                    }
                }
            });
        }
    }

    /**
     * Коллбек
     */
    public interface Callback {
        /**
         * Функция, вызываемая в методе onLogIn
         * @param isLoggedIn - залогинился ли юзер
         */
        void onLogIn(boolean isLoggedIn);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

}

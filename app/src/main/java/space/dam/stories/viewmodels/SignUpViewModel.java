package space.dam.stories.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import space.dam.stories.repositories.Auth;
import space.dam.stories.repositories.Database;
import space.dam.stories.utils.Validator;

public class SignUpViewModel extends ViewModel {

    /**
     * Вызван ли метод init()
     */
    private boolean initialized = false;

    /**
     * Введенная пользователем информация
     */
    private String email;
    private String password;
    private String name;

    private MutableLiveData<Boolean> canGoNext;

    /**
     * Шаги регистрации
     */
    private int step;
    public static final int STEP_EMAIL = 0;
    public static final int STEP_PASSWORD = 1;
    public static final int STEP_NAME = 2;

    /**
     * Коллбек
     */
    public interface Callback {
        void onClickSignUp();
        void completeSignUp(boolean isSuccessful, String errorMessage);
        void onStepChanged(int step, boolean isNext);
        void closeSignUp();
    }

    private Callback callback;

    /**
     * Инициализация
     */
    public void init(int step) {
        if (initialized) return;

        this.canGoNext = new MutableLiveData<>();
        this.canGoNext.setValue(false);

        this.step = step;

        initialized = true;
    }

    /**
     * Свойства
     */
    public void setCallback(Callback callback) {
        this.callback = callback;
        callback.onStepChanged(step, true);
    }

    public boolean isLastStep() {
        return step == STEP_NAME;
    }

    public LiveData<Boolean> getCanGoNext() {
        return canGoNext;
    }

    public void updateCanGoNext(String input) {
        switch (step) {
            case STEP_EMAIL: {
                boolean isEmailValid = Validator.validateEmail(input) == Validator.SUCCESS;
                canGoNext.setValue(isEmailValid);
                if (isEmailValid) email = input;
                break;
            }
            case STEP_PASSWORD: {
                boolean isPasswordValid = Validator.validatePassword(input) == Validator.SUCCESS;
                canGoNext.setValue(isPasswordValid);
                if (isPasswordValid) password = input;
                break;
            }
            case STEP_NAME: {
                boolean isNameValid = Validator.validateName(input) == Validator.SUCCESS;
                canGoNext.setValue(isNameValid);
                if (isNameValid) name = input;
                break;
            }
        }
    }

    public void goNext() {
        if (canGoNext.getValue() == false) return;

        if (step == STEP_NAME) {
            signUp();
            return;
        }
        if (callback != null) callback.onStepChanged(++step, true);
        canGoNext.setValue(false);
    }

    public void goBack() {
        if (step == STEP_EMAIL) {
            if (callback != null) callback.closeSignUp();
            return;
        }
        if (callback != null) callback.onStepChanged(--step, false);
        canGoNext.setValue(true);
    }

    /**
     * Попытка зарегестрировать пользователя
     */
    private void signUp() {
        if (callback != null) callback.onClickSignUp();
        Auth.signUp(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (callback != null) {
                    if (task.isSuccessful()) {
                        Database.createUser(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    callback.completeSignUp(true, null);
                                }
                                else callback.completeSignUp(false, task.getException().getMessage());
                            }
                        });
                    }
                    else callback.completeSignUp(false, task.getException().getMessage());

                }
            }
        });
    }
}

package space.dam.stories.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import space.dam.stories.repositories.Database;
import space.dam.stories.utils.Validator;

public class CreateStoryViewModel extends ViewModel {

    /**
     * Вызван ли метод init()
     */
    private boolean initialized = false;

    /**
     * Параметры истории
     */
    private String storyType;

    private MutableLiveData<Integer> lastValidStep;

    /**
     * Коллбек
     */
    public interface Callback {
        void onComplete(boolean storyCreated);
    }

    private Callback callback;


    /**
     * Инициализация
     */
    public void init() {
        if (initialized) return;

        lastValidStep = new MutableLiveData<>();
        lastValidStep.setValue(-1);

        initialized = true;
    }

    public void updateLastValidStep(String name, String description) {
        if (Validator.validateStoryName(name) != Validator.SUCCESS) {
            lastValidStep.setValue(0); // 0 - шаг выбора типа истории
        }
        else if (Validator.validateStoryDescription(description) != Validator.SUCCESS) {
            lastValidStep.setValue(1);
        }
        else {
            lastValidStep.setValue(2);
        }
    }

    /**
     * Свойства
     */
    public LiveData<Integer> getLastValidStep() {
        return lastValidStep;
    }

    public void setStoryType(String storyType) {
        this.storyType = storyType;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * Методы
     */
    public void createStory(final String title, final String description) {

        Database.createStory(storyType, title, description).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) callback.onComplete(true);
                else callback.onComplete(false);
            }
        });
    }
}

package space.dam.stories.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import space.dam.stories.models.Story;
import space.dam.stories.repositories.Database;

public class StoryViewModel extends ViewModel {

    /**
     * Вызван ли метод init()
     */
    private boolean initialized = false;

    /**
     * Переменные
     */
    private MutableLiveData<Story> story;
    private MutableLiveData<Integer> status;

    public static int STATUS_ERROR = -1;
    public static int STATUS_INIT = 0;
    public static int STATUS_LOADING = 1;
    public static int STATUS_LOADED = 2;


    /**
     * Инициализация
     */
    public void init() {
        if (initialized) return;

        story = new MutableLiveData<>();
        status = new MutableLiveData<>();
        status.setValue(STATUS_INIT);

        initialized = true;
    }

    public LiveData<Story> getStory() {
        return story;
    }

    public LiveData<Integer> getStatus() {
        return status;
    }

    public void loadStory(String uid) {
        status.setValue(STATUS_LOADING);
        Database.getStory(uid).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    assert task.getResult() != null;
                    story.setValue(task.getResult().toObject(Story.class));
                    status.setValue(STATUS_LOADED);
                }
                else status.setValue(STATUS_ERROR);
            }
        });
    }
}

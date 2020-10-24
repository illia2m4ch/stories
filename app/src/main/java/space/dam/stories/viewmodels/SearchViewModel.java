package space.dam.stories.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import space.dam.stories.models.Story;
import space.dam.stories.repositories.Database;

public class SearchViewModel extends ViewModel {

    /**
     * Вызван ли метод init()
     */
    private boolean initialized = false;

    /**
     * Переменные
     */
    private MutableLiveData<List<Story>> stories;
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

        stories = new MutableLiveData<>();
        status = new MutableLiveData<>();
        status.setValue(STATUS_INIT);

        initialized = true;
    }

    public LiveData<List<Story>> getStories() {
        return stories;
    }

    public LiveData<Integer> getStatus() {
        return status;
    }

    public void loadStories() {
        status.setValue(STATUS_LOADING);
        Database.getAllStories()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    assert task.getResult() != null;

                    List<Story> temp = task.getResult().toObjects(Story.class);

                    stories.setValue(temp);
                    status.setValue(STATUS_LOADED);
                }
                else status.setValue(STATUS_ERROR);
            }
        });
    }

    /**
     * Обновление страницы
     */
    public void refresh() {
        loadStories();
    }

}

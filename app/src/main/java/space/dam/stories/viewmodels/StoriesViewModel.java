package space.dam.stories.viewmodels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import space.dam.stories.models.Story;
import space.dam.stories.repositories.Database;

public class StoriesViewModel extends ViewModel {

    /**
     * Вызван ли метод init()
     */
    private boolean initialized = false;

    /**
     * Variables
     */
    private MutableLiveData<List<Story>> stories;
    private MutableLiveData<Integer> status;

    public static int STATUS_ERROR = -1;
    public static int STATUS_INIT = 0;
    public static int STATUS_LOADING = 1;
    public static int STATUS_LOADED = 2;
    public static int STATUS_NO_SUBSCRIPTIONS = 3;

    /**
     * Initialization
     */
    public void init() {
        if (initialized) return;

        stories = new MutableLiveData<>();
        status = new MutableLiveData<>();
        status.setValue(STATUS_INIT);

        initListener();

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
        Database.getSubscriptionStories()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Exception exception = task.getException();
                if (exception != null && exception.getMessage().equals(Database.E_NO_SUBSCRIPTIONS)) {
                    stories.setValue(new ArrayList<Story>());
                    status.setValue(STATUS_NO_SUBSCRIPTIONS);
                    return;
                }

                if (task.isSuccessful()) {
                    List<Story> temp = task.getResult().toObjects(Story.class);

                    stories.setValue(temp);
                    status.setValue(STATUS_LOADED);
                }
                else status.setValue(STATUS_ERROR);
            }
        });
    }

    private void initListener() {
        Database.getDocumentUserPrivateInfo().addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    status.setValue(STATUS_ERROR);
                    return;
                }

                loadStories();
            }
        });
    }

    /**
     * Updating the page
     */
    public void refresh() {
        loadStories();
    }

}

package space.dam.stories.viewmodels;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import space.dam.stories.models.Story;
import space.dam.stories.models.User;
import space.dam.stories.repositories.Database;

public class UserInfoViewModel extends ViewModel {

    /**
     * Вызван ли метод init()
     */
    private boolean initialized = false;

    /**
     * Переменные
     */
    private String uid;

    private MutableLiveData<User> user;
    private MutableLiveData<List<Story>> stories;
    private MutableLiveData<Integer> status;

    public static int STATUS_ERROR = -1;
    public static int STATUS_INIT = 0;
    public static int STATUS_LOADING = 1;
    public static int STATUS_USER_INFO_LOADED = 2;
    public static int STATUS_STORIES_LOADED = 3;
    public static int STATUS_LOADED = 3;

    /**
     * Инициализация
     */
    public void init(String uid) {
        if (initialized) return;

        this.uid = uid;

        user = new MutableLiveData<>();
        stories = new MutableLiveData<>();
        status = new MutableLiveData<>();
        status.setValue(STATUS_INIT);

        initListener();

        initialized = true;
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<List<Story>> getStories() {
        return stories;
    }

    public LiveData<Integer> getStatus() {
        return status;
    }

    private void load() {
        if (TextUtils.isEmpty(uid)) {
            status.setValue(STATUS_ERROR);
            return;
        }
        status.setValue(STATUS_LOADING);
        Database.getUser(uid).continueWithTask(new Continuation<DocumentSnapshot, Task<QuerySnapshot>>() {
            @Override
            public Task<QuerySnapshot> then(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    assert task.getResult() != null;
                    user.setValue(task.getResult().toObject(User.class));
                    status.setValue(STATUS_USER_INFO_LOADED);
                    return Database.getUserStories(uid);
                }
                return Tasks.forCanceled();
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    assert task.getResult() != null;
                    stories.setValue(task.getResult().toObjects(Story.class));
                    status.setValue(STATUS_STORIES_LOADED);
                }
                else status.setValue(STATUS_ERROR);
            }
        });
    }

    private void initListener() {
        Database.getDocumentUser(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    status.setValue(STATUS_ERROR);
                    return;
                }

                if (documentSnapshot != null) {
                    User updatedUser = documentSnapshot.toObject(User.class);
                    user.setValue(updatedUser);
                    status.setValue(STATUS_LOADED);
                }
            }
        });
        Database.getDocumentsUserStories(uid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    status.setValue(STATUS_ERROR);
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    List<Story> updatedStories = queryDocumentSnapshots.toObjects(Story.class);
                    stories.setValue(updatedStories);
                    status.setValue(STATUS_LOADED);
                }
            }
        });
    }

    public void refresh() {
        load();
    }

}

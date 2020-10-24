package space.dam.stories.viewmodels;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import space.dam.stories.models.User;
import space.dam.stories.repositories.Database;
import space.dam.stories.repositories.Storage;

public class AccountSettingsViewModel extends ViewModel {

    /**
     * Вызван ли метод init()
     */
    private boolean initialized = false;

    /**
     * Параметры
     */
    private MutableLiveData<Integer> status;
    public static int STATUS_ERROR = -1;
    public static int STATUS_INIT = 0;
    public static int STATUS_LOADING = 1;
    public static int STATUS_LOADED = 3;
    public static int STATUS_SAVING = 4;
    public static int STATUS_SAVED = 5;

    private MutableLiveData<User> currentUser;

    /**
     * Поля, отвечающие за то, обновил ли пользователь данные
     */
    private boolean profilePhotoChanged = false;
    private boolean nameChanged = false;

    /**
     * Инициализация
     */
    public void init() {
        if (initialized) return;

        status = new MutableLiveData<>();
        status.setValue(STATUS_INIT);
        currentUser = new MutableLiveData<>();

        loadCurrentUser();
        initialized = true;
    }

    public LiveData<Integer> getStatus() {
        return status;
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void nameChanged() {
        nameChanged = true;
    }

    public void profilePhotoChanged() {
        profilePhotoChanged = true;
    }

    private void loadCurrentUser() {
        status.setValue(STATUS_LOADING);
        Database.getCurrentUser().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    currentUser.setValue(user);
                    status.setValue(STATUS_LOADED);
                }
                else status.setValue(STATUS_ERROR);
            }
        });
    }

    private Task<Void> uploadUserPhoto(final Uri uri) {
        return Storage.updateUserPhoto(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    assert task.getResult() != null;
                    return task.getResult().getStorage().getDownloadUrl();
                }
                return Tasks.forCanceled();
            }
        }).continueWithTask(new Continuation<Uri, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    assert task.getResult() != null;
                    String url = task.getResult().toString();
                    return Database.updateUserPhoto(url);
                }
                return Tasks.forCanceled();
            }
        });
    }

    private Task<Void> deleteUserPhoto() {
        return Storage.deleteUserPhoto().continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    return Database.updateUserPhoto(null);
                }
                else return Tasks.forCanceled();
            }
        });
    }

    public void save(final String name, Uri uri) {
        status.setValue(STATUS_SAVING);
        List<Task<Void>> tasks = new ArrayList<>();
        if (nameChanged) tasks.add(Database.updateCurrentUserName(name));
        if (profilePhotoChanged) {
            if (uri == null) tasks.add(deleteUserPhoto());
            else uploadUserPhoto(uri);
        }
        Tasks.whenAllComplete(tasks).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<?>>> task) {
                if (task.isSuccessful()) {
                    status.setValue(STATUS_SAVED);
                }
                else status.setValue(STATUS_ERROR);
            }
        });
    }

}

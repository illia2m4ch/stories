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

import space.dam.stories.models.User;
import space.dam.stories.repositories.Database;

public class SubscriptionsViewModel extends ViewModel {

    /**
     * Вызван ли метод init()
     */
    private boolean initialized = false;

    /**
     * Переменные
     */
    private MutableLiveData<Integer> status;
    public static int STATUS_ERROR = -1;
    public static int STATUS_INIT = 0;
    public static int STATUS_LOADING = 1;
    public static int STATUS_LOADED = 2;
    public static int STATUS_NO_SUBSCRIPTIONS = 3;

    private MutableLiveData<List<User>> subscriptions;

    /**
     * Инициализация
     */
    public void init() {
        if (initialized) return;

        status = new MutableLiveData<>();
        status.setValue(STATUS_INIT);

        subscriptions = new MutableLiveData<>();

        initListener();

        initialized = true;
    }

    public LiveData<Integer> getStatus() {
        return status;
    }

    public LiveData<List<User>> getSubscriptions() {
        return subscriptions;
    }

    private void load() {
        status.setValue(STATUS_LOADING);
        Database.getSubscriptions().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Exception exception = task.getException();
                if (exception != null && exception.getMessage().equals(Database.E_NO_SUBSCRIPTIONS)) {
                    subscriptions.setValue(new ArrayList<User>());
                    status.setValue(STATUS_NO_SUBSCRIPTIONS);
                    return;
                }

                if (task.isSuccessful()) {
                    List<User> users = task.getResult().toObjects(User.class);
                    subscriptions.setValue(users);
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

                load();
            }
        });
    }

    public void refresh() {
        load();
    }
}

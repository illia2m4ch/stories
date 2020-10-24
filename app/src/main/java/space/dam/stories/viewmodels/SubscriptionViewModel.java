package space.dam.stories.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import space.dam.stories.models.UserPrivateInfo;
import space.dam.stories.repositories.Auth;
import space.dam.stories.repositories.Database;

public class SubscriptionViewModel extends ViewModel {

    /**
     * Вызван ли метод init()
     */
    private boolean initialized = false;

    /**
     * Переменные
     */
    private String uid;

    private MutableLiveData<Integer> status;
    public static final int STATUS_ERROR = -1;
    public static final int STATUS_INIT = 0;
    public static final int STATUS_LOADING = 1;
    public static final int STATUS_SUBSCRIBED = 2;
    public static final int STATUS_UNSUBSCRIBED = 3;
    public static final int STATUS_CAN_NOT_SUBSCRIBE = 4;
    public static final int STATUS_SUBSCRIPTION_LIMIT = 5;

    private MutableLiveData<Boolean> subscription;

    /**
     * Инициализация
     */
    public void init(String uid) {
        if (initialized) return;

        status = new MutableLiveData<>();
        status.setValue(STATUS_INIT);

        subscription = new MutableLiveData<>();

        this.uid = uid;

        // Подписаться на самого себя нельзя
        if (uid.equals(Auth.getCurrentUserUid())) {
            status.setValue(STATUS_CAN_NOT_SUBSCRIBE);
            return;
        }

        setSubscription();

        initialized = true;
    }

    public LiveData<Integer> getStatus() {
        return status;
    }

    public LiveData<Boolean> getSubscription() {
        return subscription;
    }

    private void setSubscription() {
        Database.getUserPrivateInfo().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    assert task.getResult() != null;

                    UserPrivateInfo privateInfo = task.getResult().toObject(UserPrivateInfo.class);
                    if (privateInfo == null) {
                        subscription.setValue(false);
                        return;
                    }
                    List<String> subscriptions = privateInfo.getSubscriptions();
                    if (subscriptions == null) {
                        subscription.setValue(false);
                        return;
                    }
                    boolean subscriptionStatus = subscriptions.contains(uid);
                    subscription.setValue(subscriptionStatus);
                }
                else status.setValue(STATUS_ERROR);
            }
        });
    }

    public void updateSubscription() {
        Boolean subscription = this.subscription.getValue();
        if (subscription == null) return;

        if (uid.isEmpty()) {
            status.setValue(STATUS_ERROR);
            return;
        }
        if (status.getValue() == STATUS_LOADING) return;

        status.setValue(STATUS_LOADING);
        if (!subscription) subscribe();
        else unsubscribe();
    }

    private void subscribe() {
        Database.subscribeTo(uid).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.getException() != null && task.getException().getMessage() != null &&
                        task.getException().getMessage().equals(Database.E_SUBSCRIPTIONS_LIMIT)) {
                    status.setValue(STATUS_SUBSCRIPTION_LIMIT);
                    return;
                }
                if (task.isSuccessful()) {
                    status.setValue(STATUS_SUBSCRIBED);
                    subscription.setValue(true);
                }
                else {
                    status.setValue(STATUS_ERROR);
                }
            }
        });
    }

    private void unsubscribe() {
        Database.unsubscribeTo(uid).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    status.setValue(STATUS_UNSUBSCRIBED);
                    subscription.setValue(false);
                }
                else {
                    status.setValue(STATUS_ERROR);
                }
            }
        });
    }
}

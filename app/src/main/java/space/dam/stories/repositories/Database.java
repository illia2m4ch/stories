package space.dam.stories.repositories;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

import space.dam.stories.models.Story;
import space.dam.stories.models.User;
import space.dam.stories.models.UserPrivateInfo;

public class Database {

    /**
     * Exceptions
     */
    public static final String E_NO_SUBSCRIPTIONS = "No subscriptions";
    public static final String E_SUBSCRIPTIONS_LIMIT = "Subscriptions limit";

    private static String COLLECTION_USERS = "users";
    private static String COLLECTION_STORIES = "stories";
    private static String COLLECTION_USER_PRIVATE_INFO = "privateInfo";

    private static CollectionReference getCollectionUsers() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    }

    private static CollectionReference getCollectionStories() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_STORIES);
    }

    private static FirebaseFirestore getInstance() {
        return FirebaseFirestore.getInstance();
    }

    /**
     * Запрос на создание пользователя
     */
    public static Task<Void> createUser(String name) {
        String uid = Auth.getCurrentUserUid();
        User user = new User(uid, name, "", 0, 0);
        return getCollectionUsers().document(user.getUid()).set(user).continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    UserPrivateInfo privateInfo = new UserPrivateInfo(new ArrayList<String>());
                    return getDocumentUserPrivateInfo().set(privateInfo);
                }
                return Tasks.forCanceled();
            }
        });
    }

    /**
     * Запрос текущего пользователя
     */
    public static Task<DocumentSnapshot> getCurrentUser() {
        return getCollectionUsers().document(Auth.getCurrentUserUid()).get();
    }

    /**
     * Запрос пользователя
     */
    public static Task<DocumentSnapshot> getUser(String uid) {
        return getCollectionUsers().document(uid).get();
    }

    /**
     * Запрос на создание истории
     */
    public static Task<Void> createStory(
            final String type, final String title, final String description) {

        return getCurrentUser().continueWithTask(new Continuation<DocumentSnapshot, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    WriteBatch batch = getInstance().batch();

                    User user = task.getResult().toObject(User.class);

                    DocumentReference document = getCollectionStories().document();
                    Story story = new Story(
                            document.getId(), type, title, description, Timestamp.now(),
                            user.getUid(), user.getName(), user.getPhoto()
                    );
                    batch.set(document, story);

                    DocumentReference documentUser = getCollectionUsers().document(user.getUid());
                    batch.update(documentUser, "totalStories", FieldValue.increment(1));
                    return batch.commit();
                }
                return Tasks.forCanceled();
            }
        });
    }

    /**
     * Запрос на удаление истории
     */
    public static Task<Void> deleteStory(final String uid) {
        return getCurrentUser().continueWithTask(new Continuation<DocumentSnapshot, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    WriteBatch batch = getInstance().batch();

                    DocumentReference story = getCollectionStories().document(uid);
                    batch.delete(story);

                    DocumentReference documentUser = getCollectionUsers().document(Auth.getCurrentUserUid());
                    batch.update(documentUser, "totalStories", FieldValue.increment(-1));
                    return batch.commit();
                }
                return Tasks.forCanceled();
            }
        });
    }

    /**
     * Запрос абсолютно всех историй
     */
    public static Task<QuerySnapshot> getAllStories() {
        return getCollectionStories().get();
    }

    /**
     * Запрос списка историй, созданных текущим пользователем
     */
    public static Task<QuerySnapshot> getCurrentUserStories() {
        return getCollectionStories().whereEqualTo(
                "creatorUid", Auth.getCurrentUserUid()
        ).get();
    }

    /**
     * Запрос списка историй определенного пользователя
     */
    public static Task<QuerySnapshot> getUserStories(String uid) {
        return getCollectionStories().whereEqualTo("creatorUid", uid).get();
    }

    /**
     * Запрос на получение истории по uid
     */
    public static Task<DocumentSnapshot> getStory(String uid) {
        return getCollectionStories().document(uid).get();
    }

    /**
     * Запрос списка историй от людей, на которых подписан текущий пользователь
     */
    public static Task<QuerySnapshot> getSubscriptionStories() {

        return getUserPrivateInfo().continueWithTask(new Continuation<DocumentSnapshot, Task<QuerySnapshot>>() {
            @Override
            public Task<QuerySnapshot> then(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    assert task.getResult() != null;

                    UserPrivateInfo privateInfo = task.getResult().toObject(UserPrivateInfo.class);
                    if (privateInfo == null) {
                        return Tasks.forCanceled();
                    }
                    List<String> subscriptions = privateInfo.getSubscriptions();
                    if (subscriptions == null || subscriptions.size() == 0) {
                        return Tasks.forException(new Exception(E_NO_SUBSCRIPTIONS));
                    }
                    return getCollectionStories().whereIn("creatorUid", subscriptions).get();
                }
                else return Tasks.forCanceled();
            }
        });
    }

    /**
     * Запрос приватной информации пользователя (в нее входит список подписчиков)
     */
    public static Task<DocumentSnapshot> getUserPrivateInfo() {
        return getDocumentUserPrivateInfo().get();
    }

    /**
     * Запрос на подписку
     */
    public static Task<Void> subscribeTo(final String uid) {
        return getUserPrivateInfo().continueWithTask(new Continuation<DocumentSnapshot, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    UserPrivateInfo privateInfo = task.getResult().toObject(UserPrivateInfo.class);

                    if (privateInfo.getSubscriptions().size() >= 10) {
                        return Tasks.forException(new Exception(E_SUBSCRIPTIONS_LIMIT));
                    }

                    WriteBatch batch = getInstance().batch();

                    // Увеличиваем счетчик подписчиков
                    DocumentReference user = getCollectionUsers().document(uid);
                    batch.update(user, "totalSubscribers", FieldValue.increment(1));

                    // Добавляем подписку для текущего пользователя
                    DocumentReference userPrivateInfo = getDocumentUserPrivateInfo();
                    batch.update(userPrivateInfo, "subscriptions", FieldValue.arrayUnion(uid));
                    return batch.commit();
                }
                else return Tasks.forCanceled();
            }
        });
    }

    /**
     * Запрос на отписку
     */
    public static Task<Void> unsubscribeTo(String uid) {
        WriteBatch batch = getInstance().batch();

        // Увеличиваем счетчик подписчиков
        DocumentReference user = getCollectionUsers().document(uid);
        batch.update(user, "totalSubscribers", FieldValue.increment(-1));

        // Добавляем подписку для текущего пользователя
        DocumentReference userPrivateInfo = getDocumentUserPrivateInfo();
        batch.update(userPrivateInfo, "subscriptions", FieldValue.arrayRemove(uid));

        return batch.commit();
    }

    /**
     * Запрос на подписки
     */
    public static Task<QuerySnapshot> getSubscriptions() {
        return getUserPrivateInfo().continueWithTask(new Continuation<DocumentSnapshot, Task<QuerySnapshot>>() {
            @Override
            public Task<QuerySnapshot> then(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    assert task.getResult() != null;

                    UserPrivateInfo privateInfo = task.getResult().toObject(UserPrivateInfo.class);
                    if (privateInfo == null) {
                        return Tasks.forCanceled();
                    }
                    List<String> subscriptions = privateInfo.getSubscriptions();
                    if (subscriptions == null || subscriptions.size() == 0) {
                        return Tasks.forException(new Exception(E_NO_SUBSCRIPTIONS));
                    }
                    return getCollectionUsers().whereIn("uid", subscriptions).get();
                }
                else return Tasks.forCanceled();
            }
        });
    }

    /**
     * Запрос на изменение имени пользователя
     */
    public static Task<Void> updateCurrentUserName(final String name) {

        final String uid = Auth.getCurrentUserUid();

        return getCollectionStories().whereEqualTo("creatorUid", uid).get()
                .continueWithTask(new Continuation<QuerySnapshot, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    WriteBatch batch = getInstance().batch();

                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        batch.update(document.getReference(), "creatorName", name);
                    }
                    batch.update(getCollectionUsers().document(uid), "name", name);

                    return batch.commit();
                }

                return Tasks.forCanceled();
            }
        });
    }

    public static Task<Void> updateUserPhoto(final String path) {
        final String uid = Auth.getCurrentUserUid();

        return getCollectionStories().whereEqualTo("creatorUid", uid).get()
                .continueWithTask(new Continuation<QuerySnapshot, Task<Void>>() {
                    @Override
                    public Task<Void> then(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            WriteBatch batch = getInstance().batch();

                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                batch.update(document.getReference(), "creatorPhoto", path);
                            }
                            batch.update(getCollectionUsers().document(uid), "photo", path);

                            return batch.commit();
                        }

                        return Tasks.forCanceled();
                    }
                });
    }

    /**
     * Documents
     */
    public static DocumentReference getDocumentUserPrivateInfo() {
        String uid = Auth.getCurrentUserUid();
        return FirebaseFirestore.getInstance().document(
                COLLECTION_USERS + "/" + uid + "/" + COLLECTION_USER_PRIVATE_INFO + "/" + uid
        );
    }

    public static DocumentReference getDocumentUser(String uid) {
        return getCollectionUsers().document(uid);
    }

    public static Query getDocumentsUserStories(String uid) {
        return getCollectionStories().whereEqualTo("creatorUid", uid);
    }
}

/**
 * https://firebase.google.com/docs/firestore/query-data/queries
 * CONTINUE WITH TASK ОЧЕНЬ ГОДНАЯ ШТУКА!!!!!!!!!!!!!!!!!!!!!
 */
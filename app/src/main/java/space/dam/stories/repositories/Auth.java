package space.dam.stories.repositories;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Класс Auth отвечает за авторизацию пользователя
 */
public class Auth {

    private static String APP_CREATOR_UID = "Epjh7ul1V1gObqLtyvMZ8XTkOVk1";

    public static boolean isAppCreator(String uid) {
        return TextUtils.equals(uid, APP_CREATOR_UID);
    }

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    public static String getCurrentUserUid() {
        FirebaseUser user = getCurrentUser();
        return user == null ? "" : user.getUid();
    }

    /**
     * Вход в аккаунт
     */
    public static Task<AuthResult> logIn(String email, String password) {
        return FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email,
                password
        );
    }

    /**
     * Выход из аккаунта
     */
    public static void logOut() {
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * Регистрация
     */
    public static Task<AuthResult> signUp(String email, String password) {
        return FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                email,
                password
        );
    }
}

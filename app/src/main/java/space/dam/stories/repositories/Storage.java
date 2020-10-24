package space.dam.stories.repositories;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

public class Storage {

    private static final String PATH_USERS = "users/";

    private static FirebaseStorage getInstance() {
        return FirebaseStorage.getInstance();
    }

    private static String getProfilePhotoPath() {
        return PATH_USERS + Auth.getCurrentUserUid() + "/profilePhoto";
    }

    public static UploadTask updateUserPhoto(Uri uri) {
        return getInstance().getReference(getProfilePhotoPath()).putFile(uri);
    }

    public static Task<Void> deleteUserPhoto() {
        return getInstance().getReference(getProfilePhotoPath()).delete();
    }

}

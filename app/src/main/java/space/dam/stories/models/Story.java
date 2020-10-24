package space.dam.stories.models;

import com.google.firebase.Timestamp;

public class Story {

    private String uid;

    private String type;
    private String title;
    private String description;
    private Timestamp creationDate;

    private String creatorUid;
    private String creatorName;
    private String creatorPhoto;

    public Story() {
        // Необходимый конструктор по умолчанию
    }

    public Story(String uid, String type, String title, String description, Timestamp creationDate, String creatorUid, String creatorName, String creatorPhoto) {
        this.uid = uid;
        this.type = type;
        this.title = title;
        this.description = description;
        this.creationDate = creationDate;
        this.creatorUid = creatorUid;
        this.creatorName = creatorName;
        this.creatorPhoto = creatorPhoto;
    }

    public String getUid() {
        return uid;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public String getCreatorUid() {
        return creatorUid;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getCreatorPhoto() {
        return creatorPhoto;
    }
}

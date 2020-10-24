package space.dam.stories.models;

public class User {

    private String uid;
    private String name;
    private String photo;
    private int totalStories;
    private int totalSubscribers;

    public User() {
        // Необходимый конструктор по умолчанию
    }

    public User(String uid, String name, String photo, int totalStories, int totalSubscribers) {
        this.uid = uid;
        this.name = name;
        this.photo = photo;
        this.totalStories = totalStories;
        this.totalSubscribers = totalSubscribers;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public int getTotalStories() {
        return totalStories;
    }

    public int getTotalSubscribers() {
        return totalSubscribers;
    }
}

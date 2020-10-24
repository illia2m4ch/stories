package space.dam.stories.models;

import java.util.List;

public class UserPrivateInfo {

    // Список подписок (uid)
    private List<String> subscriptions;

    public UserPrivateInfo() {
        // Необходимый конструктор по умолчанию
    }

    public UserPrivateInfo(List<String> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<String> getSubscriptions() {
        return subscriptions;
    }
}

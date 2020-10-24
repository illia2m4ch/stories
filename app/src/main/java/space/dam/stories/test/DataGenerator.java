package space.dam.stories.test;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

import space.dam.stories.models.Story;

public class DataGenerator {

    public static List<Story> generateListOfStories(int size) {
        List<Story> stories = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Story story = new Story("","historical", "Test " + i, "Description", Timestamp.now(), "","Test", "");
            stories.add(story);
        }
        return stories;
    }

}

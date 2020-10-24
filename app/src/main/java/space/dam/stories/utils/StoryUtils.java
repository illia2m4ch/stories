package space.dam.stories.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Locale;

import space.dam.stories.R;

/**
 * Класс, отвечающий за бизнесс-логику историй
 */
public class StoryUtils {

    /**
     * Типы историй
     */
    public static final String STORY_TYPE_HISTORICAL = "historical";
    public static final String STORY_TYPE_FUNNY = "funny";
    public static final String STORY_TYPE_SCARY = "scary";
    public static final String STORY_TYPE_LIFE = "life";
    public static final String STORY_TYPE_TALE = "tale";

    /**
     * Устанавливает стиль для отображения типа истории
     * @param context - для получения цветов из ресурсов
     * @param type - тип истории
     */
    public static void setStoryTypeStyle(Context context, TextView text, String type) {
        switch (type) {
            case STORY_TYPE_HISTORICAL:
                applyStyle(context, text, R.color.colorHistoricalStory, R.string.historical);
                break;
            case STORY_TYPE_FUNNY:
                applyStyle(context, text, R.color.colorFunnyStory, R.string.funny);
                break;
            case STORY_TYPE_SCARY:
                applyStyle(context, text, R.color.colorScaryStory, R.string.scary);
                break;
            case STORY_TYPE_LIFE:
                applyStyle(context, text, R.color.colorLifeStory, R.string.life);
                break;
            case STORY_TYPE_TALE:
                applyStyle(context, text, R.color.colorTale, R.string.tale);
                break;
        }
    }

    private static void applyStyle(Context context, TextView textView, int idColor, int idText) {
        int color = ContextCompat.getColor(context, idColor);
        int bgColor = ColorUtils.blendARGB(color, Color.WHITE, 0.9f); // 10% цвета оставляем

        ColorStateList tint = ColorStateList.valueOf(bgColor);

        String text = context.getString(idText).toLowerCase();
        textView.setText(text);
        textView.setTextColor(color);
        ViewCompat.setBackgroundTintList(textView, tint);
    }

    /**
     * Возвращает отформатированную дату создания истории
     */
    public static String getFormatedCreationDate(Timestamp timestamp) {
        Locale locale = new Locale("ru");
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM", locale);
        return format.format(timestamp.toDate());
    }
}

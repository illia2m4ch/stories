package space.dam.stories.utils;

import android.text.TextUtils;

/**
 * Класс, отвечающий за валидацию всех данных
 */
public class Validator {

    /**
     * Patterns
     */
    private static final String PATTERN_USER_NAME = "[^\\d\\s_-]*";
    private static final String PATTERN_WHITE_SPACES = "^\\s*$";

    /**
     * Типы ответа
     */
    public static final int SUCCESS = 0;
    public static final int EMPTY_INPUT = 1;
    public static final int SMALL_INPUT = 2;
    public static final int LARGE_INPUT = 3;
    public static final int WHITE_SPACES_INPUT = 4;
    // validateEmail
    public static final int INVALID_EMAIL = 5;
    public static final int EMAIL_ALREADY_EXISTS = 6;
    // validatePassword
    public static final int PASSWORD_TOO_SMALL = 7;
    // validateName
    public static final int INVALID_NAME = 8;


    /**
     * Проверка мейла
     */
    public static int validateEmail(String email) {
        if (TextUtils.isEmpty(email)) return EMPTY_INPUT;
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return INVALID_EMAIL;
        return SUCCESS;
    }

    /**
     * Проверка пароля
     */
    public static int validatePassword(String password) {
        if (TextUtils.isEmpty(password)) return EMPTY_INPUT;
        if (password.length() < 6) return PASSWORD_TOO_SMALL;
        return SUCCESS;
    }

    /**
     * Проверка имени
     */
    public static int validateName(String name) {
        if (TextUtils.isEmpty(name)) return EMPTY_INPUT;
        if (name.length() < 3) return SMALL_INPUT;
        if (name.length() > 12) return LARGE_INPUT;

        if (name.matches(PATTERN_USER_NAME)) return SUCCESS;
        else return INVALID_NAME;
    }

    /**
     * Проверка названия истории
     */
    public static int validateStoryName(String name) {
        String trimmed = name.trim();
        if (TextUtils.isEmpty(trimmed)) return EMPTY_INPUT;
        if (trimmed.length() > 80) return LARGE_INPUT;
        if (trimmed.matches(PATTERN_WHITE_SPACES)) return WHITE_SPACES_INPUT;
        return SUCCESS;
    }

    /**
     * Проверка описания истории
     */
    public static int validateStoryDescription(String description) {
        String trimmed = description.trim();
        if (TextUtils.isEmpty(trimmed)) return EMPTY_INPUT;
        if (trimmed.length() > 2_000) return LARGE_INPUT;
        if (trimmed.matches(PATTERN_WHITE_SPACES)) return WHITE_SPACES_INPUT;
        return SUCCESS;
    }
}

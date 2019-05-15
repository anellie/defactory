package xyz.angm.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.EnumMap;
import java.util.Locale;

/** Class for getting a locale-appropriate string. Fully static. */
public class Localization {

    private static final FileHandle localizationFile = Gdx.files.internal("localization");
    private static final EnumMap<Language, Locale> locales = new EnumMap<>(Language.class);
    private static I18NBundle locale;
    private static Language currentLocale;

    static {
        locales.put(Language.English, new Locale("en"));
        locales.put(Language.Deutsch, new Locale("de"));
        setLocale(Language.English);
    }

    /** Sets the locale.
     * @param lang The new locale to set. */
    public static void setLocale(Language lang) {
        locale = I18NBundle.createBundle(localizationFile, locales.get(lang));
        currentLocale = lang;
    }

    public static Language getCurrentLocale() {
        return currentLocale;
    }

    /** Returns a localized string.
     * @param name The name of the string in the I18N file.
     * @param args Any additional context needed for formatting the string.
     * @return The formatted locale string. */
    public static String get(String name, Object... args) {
        return locale.format(name, args);
    }

    /** All languages in the game. */
    @SuppressWarnings("JavaDoc")
    public enum Language {
        English,
        Deutsch
    }
}

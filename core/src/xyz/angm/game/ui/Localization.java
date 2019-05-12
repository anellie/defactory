package xyz.angm.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Locale;

/** Class for getting a locale-appropriate string. Fully static. */
class Localization {

    private static FileHandle baseFileHandle = Gdx.files.internal("localization");
    private static EnumMap<Language, Locale> locales = new EnumMap<>(Language.class);
    private static I18NBundle locale;
    private static Language currentLocale;

    static {
        locales.put(Language.ENGLISH, new Locale("en"));
        locales.put(Language.GERMAN, new Locale("de", "GER", "VAR1"));
        setLocale(Language.ENGLISH);
    }

    /** Cycles to the next locale. */
    static void cycleLocale() {
        int nextIndex = Arrays.asList(Language.values()).indexOf(currentLocale) + 1;
        if (nextIndex == Language.values().length) nextIndex = 0;
        setLocale(Language.values()[nextIndex]);
    }

    private static void setLocale(Language lang) {
        locale = I18NBundle.createBundle(baseFileHandle, locales.get(lang));
        currentLocale = lang;
    }

    /** Returns a localized string.
     * @param name The name of the string in the I18N file.
     * @param args Any additional context needed for formatting the string.
     * @return The formatted locale string. */
    static String get(String name, Object... args) {
        return locale.format(name, args);
    }

    /** All languages in the game. */
    enum Language {
        ENGLISH,
        GERMAN
    }
}

package xyz.angm.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

public class localization {

    private static FileHandle baseFileHandle = Gdx.files.internal("localization");
    private static Locale english = new Locale("en");
    private static Locale german = new Locale("de", "GER", "VAR1");
    public static I18NBundle locals = I18NBundle.createBundle(baseFileHandle, english);
    public static int langInt = 1;



    public static void setLocalization(int local) {

        switch (local) {
            case 1:
                locals = I18NBundle.createBundle(baseFileHandle, english);
                break;
            case 2:
                locals = I18NBundle.createBundle(baseFileHandle, german);
                break;
            default:
                locals = I18NBundle.createBundle(baseFileHandle, english);
                break;
        }
    }
    static void localUpdater() {

    }
}

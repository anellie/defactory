package xyz.angm.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import xyz.angm.game.Defactory;

/** Entry class on desktop. */
public class DesktopLauncher {

    /** Create a new game instance with the LWJGL backend.
     * @param arg Not used. */
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.resizable = true;
        config.title = "Defactory";
        config.allowSoftwareMode = true;

        new LwjglApplication(new Defactory(), config);
    }
}

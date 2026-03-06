package com.cosmonaut.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.cosmonaut.MyGdxGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Cosmonaut");
        config.setWindowedMode(1920, 1080);
        config.useVsync(true);
        config.setForegroundFPS(120);
        config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 0);
        config.setWindowIcon(Files.FileType.Internal, "Icone/icone.png");

        new Lwjgl3Application(new MyGdxGame(new ActionResolverDesktop()), config);
    }
}

package com.cosmonaut.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cosmonaut.MyGdxGame;
import com.badlogic.gdx.Files;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Cosmonaut";
	    config.width = 1422;
	    config.height = 800;
	    config.samples = 8;
	    config.addIcon("Icone/icone.png", Files.FileType.Internal);
		/*
	    config.width = 1920;
	    config.height = 1080;
	    config.fullscreen = true;
	    config.vSyncEnabled = true;
	    */
	    config.width = 1280;
	    config.height = 720;
		new LwjglApplication(new MyGdxGame(new ActionResolverDesktop()), config);
	}
}

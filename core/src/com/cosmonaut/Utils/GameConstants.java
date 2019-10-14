package com.cosmonaut.Utils;

import com.badlogic.gdx.Gdx;
import com.cosmonaut.Data;

public class GameConstants {
	//World constants
	public static float MPP = 0.05f;					//Meter/Pixel
	public static float PPM = 1/MPP;					//Pixel/Meter
	public static float BOX_STEP = 1/60f; 
	public static int BOX_VELOCITY_ITERATIONS = 6;
	public static int BOX_POSITION_ITERATIONS = 2;
	public static float GRAVITY = 0;
	public static float DENSITY = 2.0f;
	public static int LIGHT_RAY_MULTIPLICATOR = 1;
	
	//Tiled Map constants
	public static int PPT = 100;						//Pixel/Tile
	public static float MPT = PPT*MPP;					//Meter/Tile
	
	//Screen constants
	public static int NB_HORIZONTAL_TILE = 15;
	public static float SCREEN_WIDTH = MPP * NB_HORIZONTAL_TILE * PPT;
	public static float SCREEN_RATIO = (float)Gdx.graphics.getHeight()/(float)Gdx.graphics.getWidth();
	public static float SCREEN_HEIGHT = SCREEN_WIDTH * SCREEN_RATIO;
	public static float OVER_CACHE = 0.5f;
	public static String SCREEN_RESOLUTION;
	
	//Hero constants
	public static float HERO_HEIGHT = 0.66f *  PPT * MPP / 2;
	public static float HERO_WIDTH = 35 * HERO_HEIGHT / 100;
	public static float JETPACK_IMPULSE = 108;
	public static float TOM_ROTATION = 5;
	//public static float MAX_OXYGEN = 150 + Data.getOxygenLevel()*(10 + Data.getOxygenLevel());
	//public static float MAX_FUEL = 100 + Data.getFuelLevel()*(7 + Data.getFuelLevel());
	public static float MAX_OXYGEN = 150 + 15 * (int)(Data.getOxygenLevel() * 1.2f);
	public static float MAX_FUEL = 100 + 10 * (int)(Data.getFuelLevel() * 1.2f);
	public static float FUEL_CONSUMPTION = 6.8f;
	public static float CRUSH_IMPULSE = 1000;
	
	//Obstacles and Items constants
	public static float DEFAULT_LEAK_FORCE = 20f;
	public static float FUEL_REFILL = 0.55f * MAX_FUEL;
	public static float OXYGEN_REFILL = 0.55f * MAX_OXYGEN;
	public static float SOUND_DISTANCE_LIMITE = (NB_HORIZONTAL_TILE) * MPT;
	
	//Game variables (Yeah, I know, this is the GameConstants.java)
	public static String GAME_VERSION = "desktop";
	public static boolean LEVEL_INTRO = true;
	public static boolean TUTORIAL = false;
	public static boolean LEVEL_FINISHED = false;
	public static boolean UPDATE_STATE = false;
	public static boolean GAME_PAUSED = false;
	public static boolean GAME_FINISHED = false;
	public static boolean PLAY_INTRO = false;
	public static boolean GAME_LOST = false;
	public static boolean UPGRADE_1 = false;
	public static boolean UPGRADE_2 = false;
	public static boolean UPGRADE_3 = false;
	public static float ANIM_TIME = 0;
	public static float LEVEL_TIME = 0;
	public static float LEVEL_PIXEL_WIDTH, LEVEL_PIXEL_HEIGHT;
	public static int NUMBER_OF_LEVEL = 24;
	public static int SELECTED_LEVEL = 1;
	public static int UPGRADE_POINT = 0;
	public static int OXYGEN_UPGRADE_COST = 2;
	public static int FUEL_UPGRADE_COST = 3;
	public static int POWER_UPGRADE_COST = 1;
	public static String LOSE_MESSAGE = "You lost !";
	public static int CHECKPOINT = 0;
	
	//Game controls
	public static int DESKTOP_KEYBOARD_CONTROLS_QWERTY = 1;
	public static int DESKTOP_GAMEPAD_CONTROLS = 2;
	public static int ANDROID_BUTTONS_CONTROLS = 3;
	public static int ANDROID_GESTURE_CONTROLS = 4;
	public static int DESKTOP_KEYBOARD_CONTROLS_AZERTY = 5;
	public static int GAME_CONTROLS;
	public static float CONTROL_BUTTONS_SIZE = 0.15f * Gdx.graphics.getWidth();
	
	//TEST
	public static boolean FIRST_CONTACT = true;
	public static boolean ZOOM_ACTIF = false;
	
	//URL
	public static final String GOOGLE_PLAY_GAME_URL = "https://play.google.com/store/apps/details?id=com.cosmonaut.android";
	public static final String ITCH_IO_GAME_URL = "https://apprenticesoft.itch.io/cosmonaut";
	public static final String GOOGLE_PLAY_STORE_URL = "https://play.google.com/store/apps/developer?id=Apprentice+Soft";
	public static final String ITCH_IO_STORE_URL = "https://apprenticesoft.itch.io/";
	
	//Version Premium
	public static int INTERSTITIAL_TRIGGER = 3;
	public static int FREE_LEVELS = 5;
}

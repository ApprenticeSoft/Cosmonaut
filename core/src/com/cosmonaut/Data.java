package com.cosmonaut;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Data {
	
	public static Preferences prefs;
	
	public static void Load(){
		prefs = Gdx.app.getPreferences("Cosmonaut.Data");
		
		if (!prefs.contains("Upgrade Point")) {
		    prefs.putInteger("Upgrade Point", 0);
		}
		
		if (!prefs.contains("OxygenLevel")) {
		    prefs.putInteger("OxygenLevel", 0);
		}
		
		if (!prefs.contains("FuelLevel")) {
		    prefs.putInteger("FuelLevel", 0);
		}
		
		if (!prefs.contains("PowerLevel")) {
		    prefs.putInteger("PowerLevel", 0);
		}
		
		if (!prefs.contains("GameControls")) {
			if(Gdx.app.getType() == ApplicationType.Desktop){
				prefs.putInteger("GameControls", 0);
			}
			else if(Gdx.app.getType() == ApplicationType.Android)
				prefs.putInteger("GameControls", 3);
		}
		
		if (!prefs.contains("ControlOpacity")) {
		    prefs.putFloat("ControlOpacity", 1.0f);
		}
		
		if (!prefs.contains("ControlSize")) {
		    prefs.putFloat("ControlSize", 1.0f);
		}
		
		if (!prefs.contains("Manual Language")) {
		    prefs.putBoolean("Manual Language", false);
		}
		
		if (!prefs.contains("Language")) {
		    prefs.putString("Language", "EN");
		}
		
		if (!prefs.contains("RateCount")) {
		    prefs.putInteger("RateCount", 2);
		}
		
		if (!prefs.contains("Rate")) {
		    prefs.putBoolean("Rate", false);
		}
		
		if (!prefs.contains("FullVersion")) {
		    prefs.putBoolean("FullVersion", false);
		}

		if (!prefs.contains("IntroPlayed")) {
		    prefs.putBoolean("IntroPlayed", false);
		}
		
		/*
		 * Morts
		 */	
		if (!prefs.contains("nbElectrocuted")) {
		    prefs.putInteger("nbElectrocuted", 0);
		}
		if (!prefs.contains("nbCrushed")) {
		    prefs.putInteger("nbCrushed", 0);
		}
		if (!prefs.contains("nbSuffocated")) {
		    prefs.putInteger("nbSuffocated", 0);
		}
	}
	
	public static void resetData(){
		setIntroPlayed(false);
		setUpgradePoint(0);
		setOxygenLevel(0);
		setFuelLevel(0);
		setPowerLevel(0);
		setNbCrushed(0);
		setNbSuffocated(0);
		setNbElectrocuted(0);
	}
	
	public static void setUpgradePoint(int val) {
	    prefs.putInteger("Upgrade Point", val);
	    prefs.flush();							//Mandatory to save the data
	}

	public static int getUpgradePoint() {
	    return prefs.getInteger("Upgrade Point");
	}
	
	public static void setOxygenLevel(int val) {
	    prefs.putInteger("OxygenLevel", val);
	    prefs.flush();							//Mandatory to save the data
	}

	public static int getOxygenLevel() {
	    return prefs.getInteger("OxygenLevel");
	}
	
	public static void setFuelLevel(int val) {
	    prefs.putInteger("FuelLevel", val);
	    prefs.flush();							//Mandatory to save the data
	}

	public static int getFuelLevel() {
	    return prefs.getInteger("FuelLevel");
	}
	
	public static void setPowerLevel(int val) {
	    prefs.putInteger("PowerLevel", val);
	    prefs.flush();							//Mandatory to save the data
	}

	public static int getPowerLevel() {
	    return prefs.getInteger("PowerLevel");
	}
	
	public static void setGameControls(int val) {
	    prefs.putInteger("GameControls", val);
	    prefs.flush();							//Mandatory to save the data
	}
	
	public static int getGameControls() {
	    return prefs.getInteger("GameControls");
	}
	
	public static void setControlOpacity(float val) {
	    prefs.putFloat("ControlOpacity", val);
	    prefs.flush();							//Mandatory to save the data
	}

	public static float getControlOpacity() {
	    return prefs.getFloat("ControlOpacity");
	}
	
	public static void setControlSize(float val) {
	    prefs.putFloat("ControlSize", val);
	    prefs.flush();							//Mandatory to save the data
	}

	public static float getControlSize() {
	    return prefs.getFloat("ControlSize");
	}
	
	public static void setManualLanguage(boolean val){
		prefs.putBoolean("Manual Language", val);
		prefs.flush();
	}

	public static boolean getManualLanguage(){
		return prefs.getBoolean("Manual Language");
	}
	
	public static void setLanguage(String val){
		prefs.putString("Language", val);
		prefs.flush();
	}

	public static String getLanguage(){
		return prefs.getString("Language");
	}
	
	public static void setRateCount(int val) {
	    prefs.putInteger("RateCount", val);
	    prefs.flush();							//Sert à sauvegarder
	}

	public static int getRateCount() {
	    return prefs.getInteger("RateCount");
	}
	
	public static void setRate(boolean val) {
	    prefs.putBoolean("Rate", val);
	    prefs.flush();							//Sert à sauvegarder
	}

	public static boolean getRate() {
	    return prefs.getBoolean("Rate");
	}
	
	public static void setFullVersion(boolean val) {
	    prefs.putBoolean("FullVersion", val);
	    prefs.flush();							//Sert à sauvegarder
	}

	public static boolean getFullVersion() {
	    return prefs.getBoolean("FullVersion");
	}
	
	public static void setIntroPlayed(boolean val) {
	    prefs.putBoolean("IntroPlayed", val);
	    prefs.flush();							//Sert à sauvegarder
	}

	public static boolean getIntroPlayed() {
	    return prefs.getBoolean("IntroPlayed");
	}
	
	/*
	 * Morts
	 */
	public static void setNbElectrocuted(int val) {
	    prefs.putInteger("nbElectrocuted", val);
	    prefs.flush();							
	}
	public static int getNbElectrocuted() {
	    return prefs.getInteger("nbElectrocuted");
	}
	
	public static void setNbCrushed(int val) {
	    prefs.putInteger("nbCrushed", val);
	    prefs.flush();							
	}
	public static int getNbCrushed() {
	    return prefs.getInteger("nbCrushed");
	}
	
	public static void setNbSuffocated(int val) {
	    prefs.putInteger("nbSuffocated", val);
	    prefs.flush();							
	}
	public static int getNbSuffocated() {
	    return prefs.getInteger("nbSuffocated");
	}
}

package com.cosmonaut.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class LevelHandler {

	private Array<String> niveaux;
	private FileHandle file;
	private Preferences preferences;
	private String storageKey;
	private String[] fileContent;
	
	public LevelHandler(String fileName){
		niveaux = new Array<String>();	
		storageKey = fileName + ".dat";
		
		/*
		 * TEST
		 */
		if(Gdx.app.getType() == ApplicationType.WebGL){
			preferences = Gdx.app.getPreferences("cosmonaut_level_handler");
		}
		else{
			file = Gdx.files.local(storageKey);
		}
		if(readStorageText().trim().isEmpty()){
			writeDefaultState();
		}
		refreshFileContent();
	}
	
	public void setState(int levelNb){
		refreshFileContent();
		niveaux.clear();
		
		//Pour chaque ligne du fichier, on vérifie si elle correspond au format d'un niveau
		for(int i = 0; i < fileContent.length; i++) {
			String[] blockData = fileContent[i].split(",");
			if (blockData.length == 5) {
				niveaux.add(fileContent[i]);
			}
		}
		//On vérifie si le nombre de niveau dans le fichier correspond au nombre de niveau dans le jeu (en cas d'update avec ajout de niveau)
		//Si des niveaux ont été ajoutés dans une update, on rajoutes des niveaux non complétés dans le fichier
		if(niveaux.size < levelNb){
			for(int i = niveaux.size + 1; i < levelNb + 1; i++){
				niveaux.add(i + ",false,false,false,false");
			}
			file.writeString("", false);
			for(int i = 0; i < niveaux.size; i++){
				file.writeString(niveaux.get(i) + "\n", true);
			}
			refreshFileContent();
		}		
	}
	
	public boolean isLevelUnlocked(int i){
		String[] blockData = fileContent[i-1].split(",");
		return Boolean.valueOf(blockData[1]);
	}
	
	public boolean isUpgradePicked(int level, int upgrade){
		String[] blockData = fileContent[level].split(",");
		return Boolean.valueOf(blockData[upgrade + 1]);
	}
	
	public void checkUpgrades(int i){
		String[] blockData = fileContent[i-1].split(",");
		GameConstants.UPGRADE_1 = Boolean.valueOf(blockData[2]);
		GameConstants.UPGRADE_2 = Boolean.valueOf(blockData[3]);
		GameConstants.UPGRADE_3 = Boolean.valueOf(blockData[4]);
	}
	
	public void setUpgrades(int i){
		for(int j = 0; j < fileContent.length; j++) {
			String[] blockData = fileContent[j].split(",");
			if(blockData.length != 5){
				continue;
			}
			
			if(Integer.valueOf(blockData[0]) == i){
				fileContent[j] = blockData[0] + "," + blockData[1] + "," + GameConstants.UPGRADE_1 + "," + GameConstants.UPGRADE_2 + "," + GameConstants.UPGRADE_3;
			}
		}
		
		persistFileContent();
	}
	
	public void setLevelUnlocked(int i){
		for(int j = 0; j < fileContent.length; j++) {
			String[] blockData = fileContent[j].split(",");
			if(blockData.length != 5){
				continue;
			}
			
			if(Integer.valueOf(blockData[0]) == i){
				fileContent[j] = blockData[0] + ",true," + blockData[2] + "," + blockData[3] + "," + blockData[4];
			}
		}
		
		persistFileContent();
	}
	
	public void resetGame(){
		fileContent[0] = "1,true,false,false,false";
		for(int j = 1; j < fileContent.length; j++) {
			String[] blockData = fileContent[j].split(",");
			if(blockData.length != 5){
				continue;
			}
			fileContent[j] = blockData[0] + ",false,false,false,false";
		}
		
		persistFileContent();
	}
	
	public String toString(){
		return readStorageText();
	}

	private void refreshFileContent(){
		String fileText = readStorageText();
		if(fileText.trim().isEmpty()){
			fileContent = new String[0];
		}
		else{
			fileContent = fileText.trim().split("\n");
		}
	}

	private void persistFileContent(){
		StringBuilder builder = new StringBuilder();
		for(int j = 0; j < fileContent.length; j++){
			String line = fileContent[j].trim();
			if(!line.isEmpty()){
				builder.append(line).append('\n');
			}
		}
		writeStorageText(builder.toString());
		refreshFileContent();
	}

	private void writeDefaultState(){
		StringBuilder builder = new StringBuilder();
		builder.append("1,true,false,false,false\n");
		for(int i = 2; i < 25; i++){
			builder.append(i).append(",false,false,false,false\n");
		}
		writeStorageText(builder.toString());
	}

	private String readStorageText(){
		if(preferences != null){
			return preferences.getString(storageKey, "");
		}
		if(file == null || !file.exists()){
			return "";
		}
		return file.readString();
	}

	private void writeStorageText(String value){
		if(preferences != null){
			preferences.putString(storageKey, value);
			preferences.flush();
			return;
		}
		if(file != null){
			file.writeString(value, false);
		}
	}
}

package com.cosmonaut.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class LevelHandler {

	private Array<String> niveaux;
	private String fileName;
	private FileHandle file;
	private String[] fileContent;
	
	public LevelHandler(String fileName){
		this.fileName = fileName;
		niveaux = new Array<String>();	
		
		/*
		 * TEST
		 */
		file = Gdx.files.local(fileName + ".dat");
		if(!file.exists()){
			file.writeString("1,true,false,false,false\n", true);
			for(int i = 2; i < 25; i++){
				file.writeString(i + ",false,false,false,false\n", true);
			}
		}
		fileContent = file.readString().split("\n");
	}
	
	public void setState(int levelNb){
		//FileHandle file = Gdx.files.local(fileName + ".lvl");
		//Si le fichier n'existe pas, on le crée, avec un niveau non complété
		//if(!file.exists())
		//	file.writeString("1,true,false,false,false\n", true);

		//String[] fileContent = file.readString().split("\n");
		
		System.out.println("Nombre de ligne dans le fichier : " + fileContent.length);
		
		//Pour chaque ligne du fichier, on vérifie si elle correspond au format d'un niveau
		for(int i = 0; i < fileContent.length; i++) {
			String[] blockData = fileContent[i].split(",");
			if (blockData.length == 5) {

				niveaux.add(fileContent[i]);
				
				System.out.println("Nombre de niveaux dans le fichier : " + niveaux.size);

				try {
					
				} catch (NumberFormatException nfe) {
					System.out.println(nfe);				// malformed block data. ignore this block
				}
			}
		}
		//On vérifie si le nombre de niveau dans le fichier correspond au nombre de niveau dans le jeu (en cas d'update avec ajout de niveau)
		//Si des niveaux ont été ajoutés dans une update, on rajoutes des niveaux non complétés dans le fichier
		if(niveaux.size < levelNb){
			for(int i = niveaux.size + 1; i < levelNb + 1; i++){
				niveaux.add(i + ",false,false,false,false\n");
				file.writeString(niveaux.get(i-1), true);
			}
			System.out.println("Nombre de niveaux dans le fichier : " + niveaux.size);
		}		
	}
	
	public boolean isLevelUnlocked(int i){
		//FileHandle file = Gdx.files.local(fileName + ".lvl");
		/*String[] *///fileContent = file.readString().split("\n");
		String[] blockData = fileContent[i-1].split(",");
		return Boolean.valueOf(blockData[1]);
	}
	
	public boolean isUpgradePicked(int level, int upgrade){
		//FileHandle file = Gdx.files.local(fileName + ".lvl");
		/*String[] *///fileContent = file.readString().split("\n");
		String[] blockData = fileContent[level].split(",");
		return Boolean.valueOf(blockData[upgrade + 1]);
	}
	
	public void checkUpgrades(int i){
		//FileHandle file = Gdx.files.local(fileName + ".lvl");
		/*String[] *///fileContent = file.readString().split("\n");
		String[] blockData = fileContent[i-1].split(",");
		GameConstants.UPGRADE_1 = Boolean.valueOf(blockData[2]);
		GameConstants.UPGRADE_2 = Boolean.valueOf(blockData[3]);
		GameConstants.UPGRADE_3 = Boolean.valueOf(blockData[4]);
	}
	
	public void setUpgrades(int i){
		//FileHandle file = Gdx.files.local(fileName + ".lvl");
		/*String[] *///fileContent = file.readString().split("\n");
		
		for(int j = 0; j < fileContent.length; j++) {
			String[] blockData = fileContent[j].split(",");
			
			if(Integer.valueOf(blockData[0]) == i){
				fileContent[j] = blockData[0] + "," + blockData[1] + "," + GameConstants.UPGRADE_1 + "," + GameConstants.UPGRADE_2 + "," + GameConstants.UPGRADE_3;
			}
		}
		
		//Écriture du nouveau fichier
		file.writeString(fileContent[0] + "\n", false);		
		for(int j = 1; j < fileContent.length; j++)
			file.writeString(fileContent[j] + "\n", true);
	}
	
	public void setLevelUnlocked(int i){
		//FileHandle file = Gdx.files.local(fileName + ".lvl");
		/*String[] *///fileContent = file.readString().split("\n");
		
		for(int j = 0; j < fileContent.length; j++) {
			String[] blockData = fileContent[j].split(",");
			
			if(Integer.valueOf(blockData[0]) == i){
				fileContent[j] = blockData[0] + ",true," + blockData[2] + "," + blockData[3] + "," + blockData[4];
			}
		}
		
		//Écriture du nouveau fichier
		file.writeString(fileContent[0] + "\n", false);		
		for(int j = 1; j < fileContent.length; j++)
			file.writeString(fileContent[j] + "\n", true);
	}
	
	public void resetGame(){
		System.out.println("Données du jeu réinitialisées");
		//FileHandle file = Gdx.files.local(fileName + ".lvl");
		//String[] fileContent = file.readString().split("\n");
		
		fileContent[0] = "1,true,false,false,false";
		for(int j = 1; j < fileContent.length; j++) {
			String[] blockData = fileContent[j].split(",");
			fileContent[j] = blockData[0] + ",false,false,false,false";
		}
		
		//Écriture du nouveau fichier		
		file.writeString("", false);	
		for(int j = 0; j < fileContent.length; j++)
			file.writeString(fileContent[j] + "\n", true);
	}
	
	public String toString(){
		//FileHandle file = Gdx.files.local(fileName + ".lvl");
		return file.readString();
	}
}

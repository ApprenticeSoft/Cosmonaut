package com.cosmonaut.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.MyGdxGame;

public class TextBox {

	final MyGdxGame game;
	private Skin skin;
	private TextureAtlas textureAtlas;
	private FileHandle file;
	private StringBuilder stringBuilder;
	public int posChar = 0, posLine = 1, lineOffset = 0, posParagraph = 0;
	private String[] fileContent, strings;
	private String textFile, speaker, tempString;
	public boolean skipLine = false, nextLine = false, foundText = false, write = false, dialogueFinished = false, timeControl = false, touchControl = true;
	private Label label;
	private LabelStyle labelStyle;
	private Image imageBox;
	private Vector2 imageBoxSize, imageBoxPosition, interpolatedImageBoxSize, interpolatedImageBoxPosition;
	private float bordure, labelPosX, labelPosY, timer = 0.0f, timeLimit, baseTimeLimit = 1.2f, factorTimeLimit = 0.035f;
	private int textNum;
	private String splitStringParagraph = "Text ", splitStringLine = ";";

	public TextBox(final MyGdxGame game, Stage stage, String textFile){
		this.game = game;	
		this.textFile = textFile;
		create(game, textFile);	
		addToStage(stage);
	}
	
	public TextBox(final MyGdxGame game, Stage stage, String textFile, String splitStringParagraph, String splitStringLine){
		this.game = game;	
		this.textFile = textFile;
		this.splitStringParagraph = splitStringParagraph;
		this.splitStringLine = splitStringLine;
		create(game, textFile);	
		addToStage(stage);
	}
	
	public void create(final MyGdxGame game, String textFile){	
		textureAtlas = game.assets.get("Images/" + GameConstants.SCREEN_RESOLUTION + "/Images.pack", TextureAtlas.class);
		skin = new Skin();
		skin.addRegions(textureAtlas);
		imageBox = new Image(skin.getDrawable("DialogueImage"));
		imageBox.addAction(Actions.sequence(Actions.scaleTo(0, 0), Actions.alpha(0)));
		imageBox.setColor(0.85f,0.85f,0.85f,1);
		
		file = Gdx.files.internal(textFile);
		stringBuilder = new StringBuilder();

		fileContent = file.readString().split(splitStringParagraph);
		
		labelStyle = new LabelStyle(game.assets.get("fontDialogue.ttf", BitmapFont.class), Color.WHITE);
		label = new Label("", labelStyle);
		label.setWidth(Gdx.graphics.getWidth()/4);
		label.setWrap(true);
		label.setX(Gdx.graphics.getWidth()/20);
		label.setY(3*Gdx.graphics.getHeight()/4);

		bordure = 0.035f*Gdx.graphics.getWidth();
		imageBoxSize = Pools.obtain(Vector2.class).set(label.getWidth() + bordure, label.getPrefHeight() + bordure);
		imageBoxPosition = Pools.obtain(Vector2.class).set(label.getX() - bordure/2, label.getY() - label.getPrefHeight()/2 - bordure/2);
		imageBox.setX(-imageBoxPosition.x);
		imageBox.setY(-imageBoxPosition.y);
		interpolatedImageBoxSize = Pools.obtain(Vector2.class);
		interpolatedImageBoxPosition = Pools.obtain(Vector2.class);
	}
	
	public void dialogue(){
		if(write){
			strings = fileContent[posParagraph].split(splitStringLine);
			
			/*
			 * Clavier AZERTY
			 */
			if(GameConstants.GAME_CONTROLS == GameConstants.DESKTOP_KEYBOARD_CONTROLS_AZERTY)
				for(int i = 0; i < strings.length; i++){
					strings[i] = strings[i].replace("'A'", "'Q'");
					strings[i] = strings[i].replace("'W'", "'Z'");
				}
			
			//Écrit tant qu'il y a des lignes dans le paragraphe		
			if(posLine < strings.length){
				imageBox.addAction(Actions.alpha(1));
				//Écrit les characters un par un
				if(posChar < strings[posLine].length()){
					buildString2();
					labelPosition();						
				}
				//Si la ligne est complètement écrite
				else if(posChar == strings[posLine].length())
					lineWritten();
				
				resizeBox();
			}
			else
				paragraphWritten();
		}		
	}
	
	public void dialogueTimer(){
		if(write){
			strings = fileContent[posParagraph].split(splitStringLine);
	
			/*
			 * Clavier AZERTY
			 */
			if(GameConstants.GAME_CONTROLS == GameConstants.DESKTOP_KEYBOARD_CONTROLS_AZERTY)
				for(int i = 0; i < strings.length; i++){
					strings[i] = strings[i].replace("'A'", "'Q'");
					strings[i] = strings[i].replace("'W'", "'Z'");
				}
			
			//Écrit tant qu'il y a des lignes dans le paragraphe		
			if(posLine < strings.length){
				imageBox.addAction(Actions.alpha(1));
				//Durée d'affichage du text
				timeLimit = baseTimeLimit + strings[posLine].length()*factorTimeLimit;
				//Écrit les characters un par un
				if(posChar < strings[posLine].length()){
					buildString2();
					labelPosition();			
				}
				//Si la ligne est complètement écrite
				else if(posChar == strings[posLine].length()){		
					lineWritten();	
					timerNextLine();
				}		
				resizeBox();
			}
			else
				paragraphWritten();
		}	

		timer += Gdx.graphics.getDeltaTime();
	}
	
	public void dialogueTimer(Array<Interlocutor> interlocutors){
		if(write){
			strings = fileContent[posParagraph].split(splitStringLine);
		
			/*
			 * Clavier AZERTY
			 */
			if(GameConstants.GAME_CONTROLS == GameConstants.DESKTOP_KEYBOARD_CONTROLS_AZERTY)
				for(int i = 0; i < strings.length; i++){
					strings[i] = strings[i].replace("'A'", "'Q'");
					strings[i] = strings[i].replace("'W'", "'Z'");
				}
			
			//Écrit tant qu'il y a des lignes dans le paragraphe		
			if(posLine < strings.length){
				imageBox.addAction(Actions.alpha(1));
				//Durée d'affichage du text
				timeLimit = baseTimeLimit + strings[posLine].length()*factorTimeLimit;
				//Écrit les characters un par un
				if(posChar < strings[posLine].length()){
					buildString2();
					labelPosition(interlocutors);		
				}
				//Si la ligne est complètement écrite
				else if(posChar == strings[posLine].length()){
					lineWritten();
					timerNextLine();
				}
				resizeBox();
			}
			else
				paragraphWritten();
		}	

		timer += Gdx.graphics.getDeltaTime();
	}

	public void addToStage(Stage stage){
		stage.addActor(imageBox);
		stage.addActor(label);
	}
	
	public void writeDialogue(){
		if(!write){
			imageBox.addAction(Actions.parallel(Actions.alpha(1, 0.1f), Actions.scaleTo(1, 1, 0.1f)));
			write = true;
		}
	}
	
	public void writeParagraph(int paragraph){
		if(!dialogueFinished){
			imageBox.addAction(Actions.parallel(Actions.alpha(1, 0.1f), Actions.scaleTo(1, 1, 0.1f)));
			//posLine = 1;
		}
		
		strings = fileContent[paragraph-1].split(splitStringLine);
		
		/*
		 * Clavier AZERTY
		 */
		if(GameConstants.GAME_CONTROLS == GameConstants.DESKTOP_KEYBOARD_CONTROLS_AZERTY)
			for(int i = 0; i < strings.length; i++){
				strings[i] = strings[i].replace("'A'", "'Q'");
				strings[i] = strings[i].replace("'W'", "'Z'");
			}
		
		//Écrit tant qu'il y a des lignes dans le paragraphe		
		if(posLine < strings.length){
			imageBox.addAction(Actions.alpha(1));
			timeLimit = baseTimeLimit + strings[posLine].length()*factorTimeLimit;
			//Écrit les characters un par un
			if(posChar < strings[posLine].length()){
				buildString2();
				labelPosition();						
			}
			//Si la ligne est complètement écrite
			else if(posChar == strings[posLine].length())
				lineWritten();
			
			resizeBox();
		}
		else
			dialogueFinished();
		
		timer += Gdx.graphics.getDeltaTime();
	}
	
	public void writeLine(int paragraph, int line){
		if(!dialogueFinished){
			imageBox.addAction(Actions.parallel(Actions.alpha(1, 0.1f), Actions.scaleTo(1, 1, 0.1f)));	
			posLine = line;	
		}

		strings = fileContent[paragraph-1].split(splitStringLine);
		
		/*
		 * Clavier AZERTY
		 */
		if(GameConstants.GAME_CONTROLS == GameConstants.DESKTOP_KEYBOARD_CONTROLS_AZERTY)
			for(int i = 0; i < strings.length; i++){
				strings[i] = strings[i].replace("'A'", "'Q'");
				strings[i] = strings[i].replace("'W'", "'Z'");
			}
		
		//Écrit tant qu'il y a des lignes dans le paragraphe		
		if(posLine == line){
			imageBox.addAction(Actions.alpha(1));
			//Écrit les characters un par un
			if(posChar < strings[posLine].length()){
				buildString2();
				labelPosition();						
			}
			//Si la ligne est complètement écrite
			else if(posChar == strings[posLine].length())
				lineWritten();
			
			resizeBox();
		}

		if(posLine != line)
			dialogueFinished();

		timer += Gdx.graphics.getDeltaTime();
	}
	
	private void lineWritten(){
		if(touchControl){
			if(!Gdx.input.justTouched())
				skipLine = false;
			if(Gdx.input.justTouched() && !skipLine){
				nextLine();
			}
		}
		
		if(timeControl){
			if(timer >= timeLimit){
				nextLine();
			}
		}
	}
	
	private void paragraphWritten(){
		if(posParagraph < fileContent.length - 1){
			posParagraph++;
			posChar = 0;
			posLine = 1;
			strings = fileContent[posParagraph].split(splitStringLine);
		}
		else{
			dialogueFinished();
		}
	}
	
	public void dialogueFinished(){
		if(!dialogueFinished){
			label.setText("");
			imageBox.addAction(Actions.alpha(0, 0.15f));
			dialogueFinished = true;
			//posChar = 0;
			//posLine = 1;
		}
	}
	
	private void timerNextLine(){
		if(timer >= timeLimit){
			nextLine = true;
			posLine++;
			posChar = 0;
			stringBuilder.delete(0, stringBuilder.length());

			imageBoxPosition.set(label.getX() - bordure/2, label.getY() - label.getPrefHeight()/2 - bordure/2);
			timer = 0;
		}
	}
	/*
	private void buildString(){
		stringBuilder.append(strings[posLine].charAt(posChar));
		posChar++;
	
		if(!Gdx.input.justTouched()){
			nextLine = false;
			
			imageBox.addAction(Actions.alpha(1));
		}
		if(Gdx.input.justTouched() && !nextLine){
			skipLine = true;
			stringBuilder.append(strings[posLine].substring(posChar, (int)strings[posLine].length()));
			posChar = (int) strings[posLine].length();
		}
		
		label.setText(stringBuilder);
		label.setY(labelPosY - label.getPrefHeight()/2);
	}
	*/
	private void buildString2(){
		posChar += 1+(int)(60*Gdx.graphics.getDeltaTime());
		if(posChar >= strings[posLine].length()){
			posChar = strings[posLine].length();
			tempString = strings[posLine];
		}
		else
			tempString = strings[posLine].substring(0, posChar+1);
		
		label.setText(tempString);
		label.setY(labelPosY - label.getPrefHeight()/2);
	}
	
	private void labelPosition(){
		if(!strings[0].equals(speaker)){
			speaker = strings[0];
			
			if(strings[0].equals("Control")){
				setLabelPos(Gdx.graphics.getWidth()/20, 3*Gdx.graphics.getHeight()/4);
			}
			else if(strings[0].equals("Tom")){
				setLabelPos(19*Gdx.graphics.getWidth()/20 - label.getWidth(), Gdx.graphics.getHeight()/4);
			}

			imageBoxPosition.set(label.getX() - bordure/2, label.getY() - label.getPrefHeight()/2 - bordure/2);
			imageBox.setWidth(0);
			imageBox.setHeight(0);
		}	
	}
	
	private void labelPosition(Array<Interlocutor> interlocutors){
		if(!strings[0].equals(speaker)){
			speaker = strings[0];
			
			for(Interlocutor interlocutor : interlocutors){
				if(strings[0].equals(interlocutor.getName())){
					setLabelPos(interlocutor.getPosition());
				}
			}

			imageBoxPosition.set(label.getX() - bordure/2, label.getY() - label.getPrefHeight()/2 - bordure/2);
			imageBox.setWidth(0);
			imageBox.setHeight(0);
		}	
	}
	
	public void initiate(){
		nextLine = true;
		posLine = 1;
		posChar = 0;
		stringBuilder.delete(0, stringBuilder.length());
		timer = 0;
	}
	
	public void nextLine(){
		nextLine = true;
		posLine++;
		posChar = 0;
		stringBuilder.delete(0, stringBuilder.length());
		timer = 0;
	}
	
	public void resizeBox(){
		interpolatedImageBoxSize.set(label.getWidth() + bordure, label.getPrefHeight() + bordure);
		imageBoxSize.interpolate(interpolatedImageBoxSize, 0.45f, Interpolation.fade);
		interpolatedImageBoxPosition.set(label.getX() - bordure/2, label.getY() - label.getPrefHeight()/2 - bordure/2);
		imageBoxPosition.interpolate(interpolatedImageBoxPosition, 0.45f, Interpolation.fade);
		
		imageBox.setWidth(imageBoxSize.x);
		imageBox.setHeight(imageBoxSize.y);
		imageBox.setX(imageBoxPosition.x);
		imageBox.setY(imageBoxPosition.y);
	}
	
	public float getTimer(){
		return timer;
	}
	
	public void setTimer(float time){
		timer = time;
	}
	
	
	public float getTimeLimit(){
		return timeLimit;
	}
	
	public void setTimeLimit(float time){
		timeLimit = time;
	}
	
	public float getBaseTimeLimit(){
		return baseTimeLimit;
	}
	
	public void setBaseTimeLimit(float time){
		baseTimeLimit = time;
	}
	
	public float getFactorTimeLimit(){
		return factorTimeLimit;
	}
	
	public void setFactorTimeLimit(float time){
		factorTimeLimit = time;
	}
	
	public void setLabelPos(float X, float Y){
		label.setX(X);
		labelPosY = Y;
		label.setY(labelPosY - label.getPrefHeight()/2);
	}
	
	public void setLabelPos(Vector2 position){
		label.setX(position.x);
		labelPosY = position.y;
		label.setY(labelPosY - label.getPrefHeight()/2);
	}
	
	public void setTextFile(String textFile){
		this.textFile = textFile;
	}
	
	public String getTextFile(){
		return textFile;
	}
	
	public void newTextFile(String textFile){
		setTextFile(textFile);
		file = Gdx.files.internal(textFile);
		fileContent = file.readString().split(splitStringParagraph);
		dialogueFinished = false;
		skipLine = false;
		nextLine = false;
		foundText = false;
		write = false;
		posChar = 0;
		posLine = 1;
		posParagraph = 0;
		
		imageBox.setX(-imageBoxPosition.x);
		imageBox.setY(-imageBoxPosition.y);
		imageBox.addAction(Actions.alpha(1));
	}
	
	public float getTextBoxWidth(){
		return imageBoxSize.x;
	}
	
	public float getTextBoxHeight(){
		return imageBoxSize.y;
	}
	
	public void setVisible(boolean visible){
		imageBox.setVisible(visible);
		label.setVisible(visible);
	}
	
	public void dispose(){
		Pools.free(imageBoxSize);
		Pools.free(imageBoxPosition);
		Pools.free(interpolatedImageBoxSize);
		Pools.free(interpolatedImageBoxPosition);
	}
}

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
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.MyGdxGame;
/*
 * Class that displays a text in a box.
 * The text, extracted from a file, is divided into paragraphs and strings.
 * A background image represents the box.
 * A paragraph is displayed one line at a time.
 * A line is displayed letter by letter at a constant speed, independently from the framerate.
 * The box size is adjusted in real time in order to fit the text.
 */
public class TextBoxSample {

	final MyGdxGame game;
	private Skin skin;
	private TextureAtlas textureAtlas;
	private FileHandle file;
	private LabelStyle labelStyle;
	private Label label;
	private Image backgroundImage;
	public int posChar = 0, posLine = 1, lineOffset = 0, posParagraph = 0;
	private String[] fileContent, strings;
	private String tempString, splitStringParagraph = "\n", splitStringLine = ";";
	private boolean initiate = false;
	public boolean dialogueFinished = false;
	private Vector2 imageSize, imagePosition, interpolatedImageSize, interpolatedImagePosition;
	private float border, timer = 0.0f, timeLimit, baseTimeLimit = 1.2f, factorTimeLimit = 0.035f;
	
	/*
	 * Constructor of the TextBox
	 * game: The main class of the game where all the assets are loaded. Needed to load the background image of the box.
	 * stage: Where the TextBox will be displayed.
	 * textFile: The location the file that will be read.
	 * splitStringParagraph: The separator between paragraphs .
	 * splitStringLine: The separator between lines.
	 */
	public TextBoxSample(final MyGdxGame game, Stage stage, String textFile, String splitStringParagraph, String splitStringLine){
		this.game = game;	
		this.splitStringParagraph = splitStringParagraph;
		this.splitStringLine = splitStringLine;
		create(game, textFile);	
		addToStage(stage);
	}
	
	/*
	 * create function.
	 * Define the caracteristics of the TextBox such as:
	 * the background image and color,
	 * the label font, color and size and position
	 */
	public void create(final MyGdxGame game, String textFile){
		// Loading and creaction of the background image
		textureAtlas = game.assets.get("Images/" + GameConstants.SCREEN_RESOLUTION + "/Images.pack", TextureAtlas.class);
		skin = new Skin();
		skin.addRegions(textureAtlas);
		backgroundImage = new Image(skin.getDrawable("DialogueImage"));
		backgroundImage.addAction(Actions.sequence(Actions.scaleTo(0, 0), Actions.alpha(0)));
		backgroundImage.setColor(0.85f,0.85f,0.85f,1);
		// Loading of the text file
		file = Gdx.files.internal(textFile);
		fileContent = file.readString().split(splitStringParagraph);
		// Creation of the label
		labelStyle = new LabelStyle(game.assets.get("fontDialogue.ttf", BitmapFont.class), Color.WHITE);
		label = new Label("", labelStyle);
		label.setWidth(Gdx.graphics.getWidth()/4);
		label.setWrap(true);
		label.setX(Gdx.graphics.getWidth()/20);
		label.setY(3*Gdx.graphics.getHeight()/4);
		// definition of the size of the box according to the size of the text. The border makes the box a little bit larger than the text
		border = 0.035f*Gdx.graphics.getWidth();
		imageSize = Pools.obtain(Vector2.class).set(label.getWidth() + border, label.getPrefHeight() + border);
		imagePosition = Pools.obtain(Vector2.class).set(label.getX() - border/2, label.getY() - label.getPrefHeight()/2 - border/2);
		backgroundImage.setX(imagePosition.x);
		backgroundImage.setY(imagePosition.y);
		
		// These vectors will be used to smoothly resize the box when it adjusts to the text
		interpolatedImageSize = Pools.obtain(Vector2.class);
		interpolatedImagePosition = Pools.obtain(Vector2.class);
	}
	
	/*
	 * addToStage function.
	 * Adds the background image and text to the stage that is displayed on the screen.
	 */
	public void addToStage(Stage stage){
		stage.addActor(backgroundImage);
		stage.addActor(label);
	}
	
	/*
	 * writeParagraph function.
	 * Display a chosen paragraph from the text file
	 * and displays it at a chosen position
	 */
	public void writeParagraph(int paragraph, float posX, float posY){
		// Initiation of the TextBox: the textbox fades in and scales to it's original size in 0.1 second
		if(!initiate){
			backgroundImage.addAction(Actions.parallel(Actions.alpha(1, 0.1f), Actions.scaleTo(1, 1, 0.1f)));
			strings = fileContent[paragraph-1].split(splitStringLine);
			initiate = true;
		}
			
		// Writes as long as there are lines in the paragraph
		if(posLine < strings.length){
			backgroundImage.addAction(Actions.alpha(1));
			// Calculates the display duration of the line according to the number of characters 
			timeLimit = baseTimeLimit + strings[posLine].length()*factorTimeLimit;
			// Write the characters one by one
			if(posChar < strings[posLine].length()){
				// Add characters to the label
				buildString();
				// Recalculates the position of the label, based on it's new size after character addition
				labelPosition(posX, posY);						
			}
			// If the line is completely written
			else if(posChar == strings[posLine].length())
				lineWritten();
			// The box is resized after each new character is displayed
			resizeBox();
		}
		else
			// If the paragraph is completely written
			dialogueFinished();
		// The timer to decide when to switch to the next line
		timer += Gdx.graphics.getDeltaTime();
	}
	
	/*
	 * buildString function.
	 * Adds 60 characters/seconds to a temporary string, independently from the framerate.
	 * The temporary string is displayed in the label.
	 */
	private void buildString(){
		posChar += 1+(int)(60*Gdx.graphics.getDeltaTime());
		// If the framerate is < 60 frame/seconds, the posChar will be > string length at one point
		// Thus posChar is adjusted to the string length if that happens
		if(posChar >= strings[posLine].length()){
			posChar = strings[posLine].length();
			tempString = strings[posLine];
		}
		else
			tempString = strings[posLine].substring(0, posChar+1);
		
		label.setText(tempString);
	}
	
	/*
	 * labelPosition function
	 * Adjusts the position of the label after each new character
	 */
	private void labelPosition(float posX, float posY){
		label.setX(posX);
		label.setY(posY);
	}
	
	/*
	 * lineWritter function
	 * If the display time limit is reached, calls the nextLine function
	 */
	private void lineWritten(){
		if(timer >= timeLimit)
			nextLine();
	}

	/*
	 * nextLine function.
	 * Increases the posLine to read the next line.
	 * Resets the posChar and the timer before starting to display the new line
	 */
	public void nextLine(){
		posLine++;
		posChar = 0;
		timer = 0;
	}

	/*
	 * resizeBox function.
	 * Interpolates the new size and position of the box in order to have a smooth animation
	 */
	public void resizeBox(){
		interpolatedImageSize.set(label.getWidth() + border, label.getPrefHeight() + border);
		imageSize.interpolate(interpolatedImageSize, 0.45f, Interpolation.fade);
		interpolatedImagePosition.set(label.getX() - border/2, label.getY() - label.getPrefHeight()/2 - border/2);
		imagePosition.interpolate(interpolatedImagePosition, 0.45f, Interpolation.fade);
		
		backgroundImage.setWidth(imageSize.x);
		backgroundImage.setHeight(imageSize.y);
		backgroundImage.setX(imagePosition.x);
		backgroundImage.setY(imagePosition.y);
	}
	
	/*
	 * dialogueFinished function
	 * After the text has been completely displayed,
	 * clears the the label, fades out the box.
	 */
	public void dialogueFinished(){
		if(!dialogueFinished){
			label.setText("");
			backgroundImage.addAction(Actions.alpha(0, 0.15f));
			dialogueFinished = true;
			initiate = false;
		}
	}
}
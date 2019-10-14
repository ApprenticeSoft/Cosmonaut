package com.cosmonaut.Screens;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.cosmonaut.MyGdxGame;

public class HomeScreen implements Screen{

	final MyGdxGame game;
	private OrthographicCamera camera;
	private Stage stage;
	private Texture backgroundTexture, transitionTexture;
	private Image backgroundImage, transitionImage;
	private LabelStyle labelStyle;
	private String pressAnyKeyString;
	private Label label;
	private boolean nextScreen = false;
	
	public HomeScreen(final MyGdxGame game){
		this.game = game;
		
		if(!game.music.isPlaying())
			game.music.play();
		
		if(Gdx.app.getType() == ApplicationType.Desktop)
			pressAnyKeyString = game.text.get("PressAnyKey");
		else if(Gdx.app.getType() == ApplicationType.Android)
			pressAnyKeyString = game.text.get("TouchScreen");
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		//Background
		backgroundTexture = new Texture(Gdx.files.internal("Images/Logo.jpg"), true);
		backgroundTexture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.MipMapLinearNearest);
		backgroundImage = new Image(backgroundTexture);
		backgroundImage.setWidth(Gdx.graphics.getWidth());
		backgroundImage.setHeight(backgroundTexture.getHeight() * backgroundImage.getWidth()/backgroundTexture.getWidth());
		backgroundImage.setX(Gdx.graphics.getWidth()/2 - backgroundImage.getWidth()/2);
		backgroundImage.setY(Gdx.graphics.getHeight()/2 - backgroundImage.getHeight()/2);
		
		//Transition image
		transitionTexture = new Texture(Gdx.files.internal("Images/LevelScreenBackground.jpg"), true);
		transitionTexture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.MipMapLinearNearest);
		transitionImage = new Image(transitionTexture);
		transitionImage.setWidth(Gdx.graphics.getWidth());
		transitionImage.setHeight(transitionTexture.getHeight() * transitionImage.getWidth()/transitionTexture.getWidth());
		transitionImage.setX(Gdx.graphics.getWidth()/2 - transitionImage.getWidth()/2);
		transitionImage.setY(Gdx.graphics.getHeight()/2 - transitionImage.getHeight()/2);
		transitionImage.addAction(Actions.alpha(0));
		transitionImage.setVisible(false);
		
		labelStyle = new LabelStyle(game.assets.get("fontUpgrade.ttf", BitmapFont.class), Color.WHITE);
		label = new Label(pressAnyKeyString, labelStyle);
		label.setX(Gdx.graphics.getWidth()/2 - label.getWidth()/2);
		label.setY(0.25f * Gdx.graphics.getHeight() - label.getHeight()/2);

		stage = new Stage();			
		stage.addActor(backgroundImage);
		stage.addActor(label);
		stage.addActor(transitionImage);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
		game.batch.setProjectionMatrix(camera.combined);
		
		stage.act();
		stage.draw();	
		
		if((Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Keys.ANY_KEY)) && !nextScreen){
			nextScreen = true;
			
			transitionImage.setVisible(true);
			transitionImage.addAction(Actions.sequence(Actions.alpha(1, 0.2f),	 
														Actions.run(new Runnable() {
												            @Override
												            public void run() {
												            	dispose();
																game.setScreen(new MainMenuScreen(game));
												            }})));
		}
		
		 /*
	     * TEST FIREBASE
	     */
	    /*
		if(game.testTime > 5){
			game.testTime = 0;
			dispose();
			//game.setScreen(new MainMenuScreen(game));
			game.music.stop();
			//game.setScreen(new EndScreen(game));
			game.setScreen(new IntroScreen(game));
		}
		*/
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		System.out.println("Main Menu Screen disposed");
		stage.dispose();
		backgroundTexture.dispose(); 
		transitionTexture.dispose();
	}
}

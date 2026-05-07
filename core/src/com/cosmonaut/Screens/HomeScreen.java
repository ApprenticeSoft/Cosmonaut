package com.cosmonaut.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;
import com.cosmonaut.Utils.LaunchConfig;

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

		boolean touchControls = GameConstants.GAME_CONTROLS == GameConstants.ANDROID_BUTTONS_CONTROLS
				|| GameConstants.GAME_CONTROLS == GameConstants.ANDROID_GESTURE_CONTROLS
				|| GameConstants.GAME_CONTROLS == 12;
		if(touchControls)
			pressAnyKeyString = game.text.get("TouchScreen");
		else
			pressAnyKeyString = game.text.get("PressAnyKey");

		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		//Background
		backgroundTexture = game.loadScreenTexture("Images/Logo.jpg");
		backgroundImage = new Image(backgroundTexture);
		backgroundImage.setWidth(Gdx.graphics.getWidth());
		backgroundImage.setHeight(backgroundTexture.getHeight() * backgroundImage.getWidth()/backgroundTexture.getWidth());
		backgroundImage.setX(Gdx.graphics.getWidth()/2 - backgroundImage.getWidth()/2);
		backgroundImage.setY(Gdx.graphics.getHeight()/2 - backgroundImage.getHeight()/2);

		//Transition image
		transitionTexture = game.loadScreenTexture("Images/LevelScreenBackground.jpg");
		transitionImage = new Image(transitionTexture);
		transitionImage.setWidth(Gdx.graphics.getWidth());
		transitionImage.setHeight(transitionTexture.getHeight() * transitionImage.getWidth()/transitionTexture.getWidth());
		transitionImage.setX(Gdx.graphics.getWidth()/2 - transitionImage.getWidth()/2);
		transitionImage.setY(Gdx.graphics.getHeight()/2 - transitionImage.getHeight()/2);
		transitionImage.addAction(Actions.alpha(0));
		transitionImage.setVisible(false);

		labelStyle = new LabelStyle(game.getFont("fontUpgrade.ttf"), Color.WHITE);
		label = new Label(pressAnyKeyString, labelStyle);
		label.setX(Gdx.graphics.getWidth()/2 - label.getWidth()/2);
		label.setY(0.25f * Gdx.graphics.getHeight() - label.getHeight()/2);

		if(Gdx.app.getType() == com.badlogic.gdx.Application.ApplicationType.WebGL)
			stage = new Stage(new com.badlogic.gdx.utils.viewport.ScreenViewport(), game.batch);
		else
			stage = new Stage();
		stage.addActor(backgroundImage);
		stage.addActor(label);
		stage.addActor(transitionImage);
	}

	@Override
	public void render(float delta) {
		
		GameConstants.FRAME_DELTA = Math.min(delta, 1f/15f);
Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		game.batch.setProjectionMatrix(camera.combined);

		stage.act(GameConstants.FRAME_DELTA);
		stage.draw();

		if(!nextScreen
				&& LaunchConfig.startLevelOverride >= 1
				&& LaunchConfig.startLevelOverride <= GameConstants.NUMBER_OF_LEVEL
				&& game.blackImage != null
				&& game.fullVersionWindow != null){
			nextScreen = true;
			GameConstants.SELECTED_LEVEL = LaunchConfig.startLevelOverride;
			LaunchConfig.startLevelOverride = -1;
			game.music.stop();
			dispose();
			game.setScreen(new GameScreen(game));
			return;
		}

		if((Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Keys.ANY_KEY)) && !nextScreen){
			nextScreen = true;

			transitionImage.setVisible(true);
			transitionImage.addAction(Actions.sequence(Actions.alpha(1, 0.2f),
														Actions.run(new Runnable() {
												            @Override
												            public void run() {
												            	Gdx.app.postRunnable(new Runnable() {
												            		@Override
												            		public void run() {
												            			dispose();
												            			game.setScreen(new MainMenuScreen(game));
												            		}
												            	});
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
		stage.dispose();
		backgroundTexture.dispose();
		transitionTexture.dispose();
	}
}

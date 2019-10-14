package com.cosmonaut.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.Data;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;
import com.cosmonaut.Utils.UIWindow;

public class DesktopControlScreen implements Screen{

	final MyGdxGame game;
	private Image backgroundImage;
	private Texture backgroundTexture;
	private Stage stage;
	private Label screenTitle, zoomLabel, displacementLabel, pauseLabel;
	private UIWindow screenTitleWindow;
	private LabelStyle labelStyle, screenTitleStyle;
	private Button backButton;
	private Image displacementImage, zoomImage, pauseImage;
	private float écart;
	private TextureAtlas textureAtlas;
	private Skin skin;
	private String displacementString;
	private TextButtonStyle textButtonStyle;
	private TextButton QWERTYButton, AZERTYButton;
	private ButtonGroup<TextButton> buttonGroup;
	
	public DesktopControlScreen(final MyGdxGame game){
		this.game = game;

		stage = new Stage();	
		
		textureAtlas = game.assets.get("Images/Desktop_Controls.pack", TextureAtlas.class);
		skin = new Skin();
		skin.addRegions(textureAtlas);
		
		if(GameConstants.GAME_CONTROLS == GameConstants.DESKTOP_KEYBOARD_CONTROLS_AZERTY)
			displacementString = "AZERTY";
		else
			displacementString = "QWERTY";
		
		//Background
		backgroundTexture = new Texture(Gdx.files.internal("Images/LevelScreenBackground.jpg"), true);
		backgroundTexture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.MipMapLinearNearest);
		backgroundImage = new Image(backgroundTexture);
		backgroundImage.setColor(1, 1, 1, 0.7f);
		backgroundImage.setWidth(Gdx.graphics.getWidth());
		backgroundImage.setHeight(backgroundTexture.getHeight() * backgroundImage.getWidth()/backgroundTexture.getWidth());
		backgroundImage.setX(Gdx.graphics.getWidth()/2 - backgroundImage.getWidth()/2);
		backgroundImage.setY(Gdx.graphics.getHeight()/2 - backgroundImage.getHeight()/2);
		
		//Titre de l'écran
		Color colorTitle = Pools.obtain(Color.class);
		colorTitle.set(2/256f, 165/256f, 200/256f, 1);
		screenTitleStyle = new LabelStyle(game.assets.get("fontMenu.ttf", BitmapFont.class), colorTitle);
		screenTitle = new Label(game.text.get("Controls"), screenTitleStyle);
		screenTitle.setAlignment(Align.center);
		
		Pools.free(colorTitle);
		
		float screenTitleWindowDimension = 0.465f*Gdx.graphics.getWidth();
		screenTitleWindow = new UIWindow(	game.skin.getDrawable("ScreenTitle"), 
											screenTitleWindowDimension, 
											screenTitleWindowDimension * game.skin.getRegion("ScreenTitle").getRegionHeight()/game.skin.getRegion("ScreenTitle").getRegionWidth(),
											Gdx.graphics.getWidth() - screenTitleWindowDimension,
											Gdx.graphics.getHeight() - screenTitleWindowDimension * game.skin.getRegion("ScreenTitle").getRegionHeight()/game.skin.getRegion("ScreenTitle").getRegionWidth());
		screenTitleWindow.addActorRelativeCentered(screenTitle, 0.525f, 0.59f);
		
		//Boutons de sélection du mode
		textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = game.skin.getDrawable("Button");
		textButtonStyle.down = game.skin.getDrawable("ButtonCheck");
		textButtonStyle.checked = game.skin.getDrawable("ButtonCheck");
		textButtonStyle.font = game.assets.get("fontUpgrade.ttf", BitmapFont.class);
		textButtonStyle.fontColor = Color.WHITE;
		textButtonStyle.downFontColor = Color.BLACK;
		textButtonStyle.checkedFontColor = Color.BLACK;
		
		//Gesture button
		QWERTYButton = new TextButton("QWERTY", textButtonStyle);
		QWERTYButton.setHeight(0.11f*Gdx.graphics.getHeight());
		QWERTYButton.setWidth(0.17f*Gdx.graphics.getWidth());
		QWERTYButton.setX(0.07f * Gdx.graphics.getWidth());
		QWERTYButton.setY(0.5f * Gdx.graphics.getHeight());
		
		//Buttons button
		AZERTYButton = new TextButton("AZERTY", textButtonStyle);
		AZERTYButton.setHeight(QWERTYButton.getHeight());
		AZERTYButton.setWidth(QWERTYButton.getWidth());
		AZERTYButton.setX(QWERTYButton.getX());
		AZERTYButton.setY(QWERTYButton.getY() - QWERTYButton.getHeight() - 0.02f * Gdx.graphics.getHeight());
		
		//Button group
		buttonGroup = new ButtonGroup<TextButton>();
		buttonGroup.setMaxCheckCount(1);
		buttonGroup.setMinCheckCount(1);
		buttonGroup.add(QWERTYButton);
		buttonGroup.add(AZERTYButton);
		
		if(Data.getGameControls() == GameConstants.DESKTOP_KEYBOARD_CONTROLS_QWERTY)
			QWERTYButton.setChecked(true);
		else if(Data.getGameControls() == GameConstants.DESKTOP_KEYBOARD_CONTROLS_AZERTY)
			AZERTYButton.setChecked(true);
		
		//Back button
		backButton = new Button(game.skin.getDrawable("BackButtonIcon"), game.skin.getDrawable("BackButtonIconCheck"));
		backButton.setWidth(Gdx.graphics.getWidth()/10);
		backButton.setHeight(Gdx.graphics.getWidth()/10);
		backButton.setX(Gdx.graphics.getWidth()/50);
		backButton.setY(0.98f * Gdx.graphics.getHeight() - backButton.getHeight());

		//Images clavier
		écart = 0.09f * Gdx.graphics.getWidth();
		
		displacementImage = new Image(skin.getDrawable(displacementString));
		displacementImage.setWidth(0.24f * Gdx.graphics.getWidth());
		displacementImage.setHeight(displacementImage.getWidth() * skin.getRegion(displacementString).getRegionHeight() / skin.getRegion(displacementString).getRegionWidth());
		displacementImage.setX(0.59f * Gdx.graphics.getWidth() - (5*displacementImage.getWidth()/3 + écart)/2);
		displacementImage.setY(0.55f * Gdx.graphics.getHeight() - displacementImage.getHeight()/2);
		
		zoomImage = new Image(skin.getDrawable("Zoom"));
		zoomImage.setWidth(2 * displacementImage.getWidth() / 3);
		zoomImage.setHeight(zoomImage.getWidth() * skin.getRegion("Zoom").getRegionHeight() / skin.getRegion("Zoom").getRegionWidth());
		zoomImage.setY(displacementImage.getY());
		zoomImage.setX(0.59f * Gdx.graphics.getWidth() + (5*displacementImage.getWidth()/3 + écart)/2 - zoomImage.getWidth());
		
		pauseImage = new Image(skin.getDrawable("Esc"));
		pauseImage.setWidth(displacementImage.getWidth() / 3);
		pauseImage.setHeight(pauseImage.getWidth() * skin.getRegion("Esc").getRegionHeight() / skin.getRegion("Esc").getRegionWidth());
		pauseImage.setX(displacementImage.getX() + displacementImage.getWidth()/2 - pauseImage.getWidth()/2);
		pauseImage.setY(displacementImage.getY() - 2*pauseImage.getHeight());
		
		//Texte
		labelStyle = new LabelStyle(game.assets.get("fontUpgrade.ttf", BitmapFont.class), Color.WHITE);
		displacementLabel = new Label(game.text.get("Displacement"), labelStyle);
		displacementLabel.setX(displacementImage.getX() + displacementImage.getWidth()/2 - displacementLabel.getWidth()/2);
		displacementLabel.setY(displacementImage.getY() - 1.5f * displacementLabel.getHeight());

		zoomLabel = new Label(game.text.get("Zoom"), labelStyle);
		zoomLabel.setX(zoomImage.getX() + zoomImage.getWidth()/2 - zoomLabel.getWidth()/2);
		zoomLabel.setY(zoomImage.getY() - 1.5f * zoomLabel.getHeight());

		pauseLabel = new Label(game.text.get("Pause"), labelStyle);
		pauseLabel.setX(pauseImage.getX() + pauseImage.getWidth()/2 - pauseLabel.getWidth()/2);
		pauseLabel.setY(pauseImage.getY() - 1.5f * pauseLabel.getHeight());
		
		stage.addActor(backgroundImage);
		screenTitleWindow.addToStage(stage);
		stage.addActor(QWERTYButton);
		stage.addActor(AZERTYButton);
		stage.addActor(backButton);
		stage.addActor(displacementImage);
		stage.addActor(zoomImage);
		stage.addActor(pauseImage);
		stage.addActor(zoomLabel);
		stage.addActor(displacementLabel);
		stage.addActor(pauseLabel);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	
	    stage.act();
	    stage.draw();
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		
		QWERTYButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				Data.setGameControls(GameConstants.DESKTOP_KEYBOARD_CONTROLS_QWERTY);
				GameConstants.GAME_CONTROLS = Data.getGameControls();
				displacementString = "QWERTY";
				displacementImage.setDrawable(skin.getDrawable(displacementString));
			}
		});
		
		AZERTYButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				Data.setGameControls(GameConstants.DESKTOP_KEYBOARD_CONTROLS_AZERTY);
				GameConstants.GAME_CONTROLS = Data.getGameControls();
				displacementString = "AZERTY";
				displacementImage.setDrawable(skin.getDrawable(displacementString));
			}
		});	
		
		backButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
            	dispose();
				game.setScreen(new OptionScreen(game));
			}
		});
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
		// TODO Auto-generated method stub
		
	}
}

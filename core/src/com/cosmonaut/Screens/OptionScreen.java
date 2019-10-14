package com.cosmonaut.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.Data;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.UIWindow;

public class OptionScreen implements Screen{
	
	final MyGdxGame game;
	private Image backgroundImage;
	private Texture backgroundTexture;
	private Stage stage;
	private UIWindow screenTitleWindow, resetWarningWindow, dataErasedWindow;
	private LabelStyle labelStyle, screenTitleStyle;
	private Label screenTitle, resetLabel, dataErasedLabel;
	private Button backButton;
	private ButtonGroup<TextButton> buttonGroup, languageButtonGroup;
	private TextButton controlButton, languageButton, resetButton, englishButton, francaisButton, espanolButton, deutschButton, yesButton, noButton, okButton;
	private TextButtonStyle textButtonStyle, resetTextButtonStyle, languageTextButtonStyle;
	private Table languageTable, resetTable, dataErasedTable;
	
	public OptionScreen(final MyGdxGame game){
		this.game = game;

		stage = new Stage();	
		
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
		screenTitle = new Label(game.text.get("Options"), screenTitleStyle);
		screenTitle.setAlignment(Align.center);

		float screenTitleWindowDimension = 0.465f*Gdx.graphics.getWidth();
		screenTitleWindow = new UIWindow(	game.skin.getDrawable("ScreenTitle"), 
											screenTitleWindowDimension, 
											screenTitleWindowDimension * game.skin.getRegion("ScreenTitle").getRegionHeight()/game.skin.getRegion("ScreenTitle").getRegionWidth(),
											Gdx.graphics.getWidth() - screenTitleWindowDimension,
											Gdx.graphics.getHeight() - screenTitleWindowDimension * game.skin.getRegion("ScreenTitle").getRegionHeight()/game.skin.getRegion("ScreenTitle").getRegionWidth());
		screenTitleWindow.addActorRelativeCentered(screenTitle, 0.525f, 0.59f);
		
		//Back button
		backButton = new Button(game.skin.getDrawable("BackButtonIcon"), game.skin.getDrawable("BackButtonIconCheck"));
		backButton.setWidth(Gdx.graphics.getWidth()/10);
		backButton.setHeight(Gdx.graphics.getWidth()/10);
		backButton.setX(Gdx.graphics.getWidth()/50);
		backButton.setY(0.98f * Gdx.graphics.getHeight() - backButton.getHeight());
		

		Color colorFont2 = Pools.obtain(Color.class);
		colorFont2.set(1/256f, 82/256f, 100/256f, .9f);
		
		textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = game.skin.getDrawable("Button");
		textButtonStyle.down = game.skin.getDrawable("ButtonCheck");
		textButtonStyle.checked = game.skin.getDrawable("ButtonCheck");
		textButtonStyle.font = game.assets.get("fontUpgrade.ttf", BitmapFont.class);
		textButtonStyle.fontColor = Color.WHITE;
		textButtonStyle.downFontColor = Color.BLACK;
		textButtonStyle.checkedFontColor = Color.BLACK;
		
		resetTextButtonStyle = new TextButtonStyle();
		resetTextButtonStyle.up = game.skin.getDrawable("LinearButton");
		resetTextButtonStyle.down = game.skin.getDrawable("LinearButtonCheck");
		resetTextButtonStyle.checked = game.skin.getDrawable("LinearButtonCheck");
		resetTextButtonStyle.font = game.assets.get("fontHUD.ttf", BitmapFont.class);
		resetTextButtonStyle.fontColor = Color.WHITE;
		resetTextButtonStyle.downFontColor = Color.BLACK;
		
		languageTextButtonStyle = new TextButtonStyle();
		languageTextButtonStyle.up = game.skin.getDrawable("LinearButton");
		languageTextButtonStyle.down = game.skin.getDrawable("LinearButtonCheck");
		languageTextButtonStyle.checked = game.skin.getDrawable("LinearButtonCheck");
		languageTextButtonStyle.font = game.assets.get("fontUpgrade.ttf", BitmapFont.class);
		languageTextButtonStyle.fontColor = colorFont2;
		languageTextButtonStyle.downFontColor = Color.WHITE;
		languageTextButtonStyle.checkedFontColor = Color.WHITE;
		
		//Controls
		controlButton = new TextButton(game.text.get("Controls"), textButtonStyle);
		controlButton.setHeight(0.13f*Gdx.graphics.getHeight());
		controlButton.setWidth(0.22f*Gdx.graphics.getWidth());
		controlButton.setX(0.07f * Gdx.graphics.getWidth());
		controlButton.setY(0.55f * Gdx.graphics.getHeight());

		//Languages
		languageButton = new TextButton(game.text.get("Language"), textButtonStyle);
		languageButton.setHeight(controlButton.getHeight());
		languageButton.setWidth(controlButton.getWidth());
		languageButton.setX(0.07f * Gdx.graphics.getWidth());
		languageButton.setY(controlButton.getY() - controlButton.getHeight() - 0.02f * Gdx.graphics.getHeight());

		//Reset
		resetButton = new TextButton(game.text.get("Reset"), textButtonStyle);
		resetButton.setHeight(controlButton.getHeight());
		resetButton.setWidth(controlButton.getWidth());
		resetButton.setX(0.07f * Gdx.graphics.getWidth());
		resetButton.setY(languageButton.getY() - languageButton.getHeight() - 0.02f * Gdx.graphics.getHeight());

		labelStyle = new LabelStyle(game.assets.get("fontDialogue.ttf", BitmapFont.class),Color.WHITE);
		resetLabel = new Label(game.text.get("ResetWarning") + "\n\n" + game.text.get("DoYouWantToContinue"), labelStyle);
		resetLabel.setWrap(true);
		resetLabel.setAlignment(Align.center);

		yesButton = new TextButton(game.text.get("Yes"), resetTextButtonStyle);
		noButton = new TextButton(game.text.get("No"), resetTextButtonStyle);
		
		resetTable = new Table();
		resetTable.add(resetLabel).width(0.33f*Gdx.graphics.getWidth()).colspan(2).row();
		resetTable.add(yesButton).width(0.08f*Gdx.graphics.getWidth()).height(0.052f*Gdx.graphics.getWidth()).pad(0.023f*Gdx.graphics.getWidth());
		resetTable.add(noButton).width(0.08f*Gdx.graphics.getWidth()).height(0.052f*Gdx.graphics.getWidth()).pad(0.023f*Gdx.graphics.getWidth());
		
		resetWarningWindow = new UIWindow(game.skin.getDrawable("DialogueImage"), 0.45f*Gdx.graphics.getWidth(), 0.45f*Gdx.graphics.getHeight());
		resetWarningWindow.addActorRelativeCentered(resetTable, 0.5f, 0.5f);
		resetWarningWindow.alfaZero(0);	
		
		//Data erased
		dataErasedLabel = new Label(game.text.get("ResetComplete"), labelStyle);
		dataErasedLabel.setWrap(true);
		dataErasedLabel.setAlignment(Align.center);
		okButton = new TextButton(game.text.get("OK"), resetTextButtonStyle);
		dataErasedTable = new Table();
		dataErasedTable.add(dataErasedLabel).width(0.33f*Gdx.graphics.getWidth()).row();
		dataErasedTable.add(okButton).width(0.08f*Gdx.graphics.getWidth()).height(0.052f*Gdx.graphics.getWidth()).padTop(0.085f*Gdx.graphics.getWidth());
		
		dataErasedWindow = new UIWindow(game.skin.getDrawable("DialogueImage"), 0.45f*Gdx.graphics.getWidth(), 0.45f*Gdx.graphics.getHeight());
		dataErasedWindow.addActorRelativeCentered(dataErasedTable, 0.5f, 0.5f);
		dataErasedWindow.alfaZero(0);	
		
		//Button group
		buttonGroup = new ButtonGroup<TextButton>();
		buttonGroup.setMaxCheckCount(1);
		buttonGroup.setMinCheckCount(0);
		buttonGroup.add(controlButton);
		buttonGroup.add(languageButton);
		buttonGroup.add(resetButton);

		//Languages
		englishButton = new TextButton("English", languageTextButtonStyle);	
		francaisButton = new TextButton("Français", languageTextButtonStyle);
		espanolButton = new TextButton("Español", languageTextButtonStyle);
		deutschButton = new TextButton("Deutsch", languageTextButtonStyle);
		
		languageButtonGroup = new ButtonGroup<TextButton>();
		languageButtonGroup.setMaxCheckCount(1);
		languageButtonGroup.setMinCheckCount(1);
		languageButtonGroup.add(englishButton);
		languageButtonGroup.add(francaisButton);
		languageButtonGroup.add(espanolButton);
		languageButtonGroup.add(deutschButton);
		
		languageTable = new Table();
		languageTable.defaults().width(0.17f*Gdx.graphics.getWidth()).height(0.11f*Gdx.graphics.getHeight()).space(0.02f*Gdx.graphics.getWidth());
		languageTable.add(englishButton);
		languageTable.add(francaisButton).row();
		languageTable.add(espanolButton);
		languageTable.add(deutschButton);
		languageTable.setX(languageButton.getX() + languageButton.getPrefWidth() + languageTable.getPrefWidth());
		languageTable.setY(languageButton.getY() + languageButton.getPrefHeight()/2);
		languageTable.addAction(Actions.alpha(0));
		
		if(Data.getLanguage().equals("EN"))
			englishButton.setChecked(true);
		else if(Data.getLanguage().equals("FR"))
				francaisButton.setChecked(true);
		else if(Data.getLanguage().equals("ES"))
			espanolButton.setChecked(true);
		else if(Data.getLanguage().equals("DE"))
			deutschButton.setChecked(true);
		
		stage.addActor(backgroundImage);
		screenTitleWindow.addToStage(stage);
		stage.addActor(backButton);
		stage.addActor(controlButton);
		stage.addActor(languageButton);
		stage.addActor(resetButton);
		stage.addActor(languageTable);
		stage.addActor(game.blackImage);
		resetWarningWindow.addToStage(stage);
		dataErasedWindow.addToStage(stage);

		Pools.free(colorTitle);
		Pools.free(colorFont2);
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
		
		backButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
            	dispose();
				game.setScreen(new MainMenuScreen(game));
			}
		});

		controlButton.addListener(new ClickListener(){
			 @Override
		        public void clicked(InputEvent event, float x, float y) {
	            	dispose();            	
	            	if(Gdx.app.getType() == ApplicationType.Desktop)
	            		game.setScreen(new DesktopControlScreen(game));
	            	else if(Gdx.app.getType() == ApplicationType.Android)
	            		game.setScreen(new AndroidControlScreen(game));
			 }
		});
		
		languageButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				if(languageButton.isChecked()){
					languageTable.setTouchable(Touchable.enabled);
					languageTable.addAction(Actions.alpha(1,.3f, Interpolation.pow2));
				}
				else{
					languageTable.setTouchable(Touchable.disabled);
					languageTable.addAction(Actions.alpha(0,.3f));
				}
			}
		});
		
		resetButton.addListener(new ClickListener(){
			 @Override
		        public void clicked(InputEvent event, float x, float y) {
					languageTable.setTouchable(Touchable.disabled);
					languageTable.addAction(Actions.alpha(0,.3f));
					game.blackImage.addAction(Actions.alpha(0.5f, 0.2f));
					game.blackImage.setTouchable(Touchable.enabled);
					resetWarningWindow.alfaOne(.2f);
			 }
		});
		
		englishButton.addListener(new ClickListener(){
			 @Override
		        public void clicked(InputEvent event, float x, float y) {
				 Data.setLanguage("EN");
				 Data.setManualLanguage(true);
				 setLanguage();
			 }
		});
		
		francaisButton.addListener(new ClickListener(){
			 @Override
		        public void clicked(InputEvent event, float x, float y) {
				 Data.setLanguage("FR");
				 Data.setManualLanguage(true);
				 setLanguage();
			 }
		});
		
		espanolButton.addListener(new ClickListener(){
			 @Override
		        public void clicked(InputEvent event, float x, float y) {
				 Data.setLanguage("ES");
				 Data.setManualLanguage(true);
				 setLanguage();
			 }
		});
		
		deutschButton.addListener(new ClickListener(){
			 @Override
		        public void clicked(InputEvent event, float x, float y) {
				 Data.setLanguage("DE");
				 Data.setManualLanguage(true);
				 setLanguage();
			 }
		});
		
		yesButton.addListener(new ClickListener(){
			 @Override
		        public void clicked(InputEvent event, float x, float y) {
				 	Data.resetData();
					game.levelHandler.resetGame();
					
					//blackImage.addAction(Actions.alpha(0, 0.2f));
					//blackImage.setTouchable(Touchable.disabled);
					resetWarningWindow.alfaZero(.2f);
					dataErasedWindow.alfaOne(.2f);
			 }
		});

		noButton.addListener(new ClickListener(){
			 @Override
		        public void clicked(InputEvent event, float x, float y) {
					game.blackImage.addAction(Actions.alpha(0, 0.2f));
					game.blackImage.setTouchable(Touchable.disabled);
					resetWarningWindow.alfaZero(.2f);
					resetButton.setChecked(false);
			 }
		});

		okButton.addListener(new ClickListener(){
			 @Override
		        public void clicked(InputEvent event, float x, float y) {
					game.blackImage.addAction(Actions.alpha(0, 0.2f));
					game.blackImage.setTouchable(Touchable.disabled);
					dataErasedWindow.alfaZero(.2f);
					resetButton.setChecked(false);
			 }
		});

		game.blackImage.addListener(new ClickListener(){
			 @Override
		        public void clicked(InputEvent event, float x, float y) {
					game.blackImage.addAction(Actions.alpha(0, 0.2f));
					game.blackImage.setTouchable(Touchable.disabled);
					resetWarningWindow.alfaZero(.2f);
					dataErasedWindow.alfaZero(.2f);
					resetButton.setChecked(false);
			 }
		});
	}
	
	public void setLanguage(){
		screenTitle.setText(game.text.get("Options"));
		resetLabel.setText(game.text.get("ResetWarning") + "\n\n" + game.text.get("DoYouWantToContinue"));
		dataErasedLabel.setText(game.text.get("ResetComplete"));
		controlButton.setText(game.text.get("Controls"));
		languageButton.setText(game.text.get("Language"));
		resetButton.setText(game.text.get("Reset"));
		yesButton.setText(game.text.get("Yes"));
		noButton.setText(game.text.get("No"));
		okButton.setText(game.text.get("OK"));
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
	}

}

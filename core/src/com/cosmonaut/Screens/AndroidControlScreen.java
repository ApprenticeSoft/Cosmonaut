package com.cosmonaut.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.Data;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;
import com.cosmonaut.Utils.UIWindow;

public class AndroidControlScreen implements Screen{

	final MyGdxGame game;
	private Image backgroundImage, imageTableBackground;
	private Texture backgroundTexture;
	private Stage stage;
	private Label screenTitle, labelSize, labelOpacity;
	private UIWindow screenTitleWindow;
	private LabelStyle labelStyle, screenTitleStyle;
	private ButtonGroup<TextButton> buttonGroup;
	private Button backButton;
	private TextButton gestureButton, buttonsButton;
	private TextButtonStyle textButtonStyle;
	
	//Animation
	private float animTime = 0;
	private Animation controlsAnimation;
	private TextureAtlas controlsAtlas;
	
	//Sliders
	private Table tableSlider;
	private SliderStyle sliderStyle;
	private Slider sliderSize, sliderOpacity;
	
	//Boutons Controls
	private Button buttonLeft, buttonRight, buttonJetPack;
	
	public AndroidControlScreen(final MyGdxGame game){
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
		
		textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = game.skin.getDrawable("Button");
		textButtonStyle.down = game.skin.getDrawable("ButtonCheck");
		textButtonStyle.checked = game.skin.getDrawable("ButtonCheck");
		textButtonStyle.font = game.assets.get("fontUpgrade.ttf", BitmapFont.class);
		textButtonStyle.fontColor = Color.WHITE;
		textButtonStyle.downFontColor = Color.BLACK;
		textButtonStyle.checkedFontColor = Color.BLACK;
		
		//Gesture button
		gestureButton = new TextButton(game.text.get("Gesture"), textButtonStyle);
		gestureButton.setHeight(0.11f*Gdx.graphics.getHeight());
		gestureButton.setWidth(0.17f*Gdx.graphics.getWidth());
		gestureButton.setX(0.07f * Gdx.graphics.getWidth());
		gestureButton.setY(0.5f * Gdx.graphics.getHeight());
		
		//Buttons button
		buttonsButton = new TextButton(game.text.get("Buttons"), textButtonStyle);
		buttonsButton.setHeight(gestureButton.getHeight());
		buttonsButton.setWidth(gestureButton.getWidth());
		buttonsButton.setX(gestureButton.getX());
		buttonsButton.setY(gestureButton.getY() - gestureButton.getHeight() - 0.02f * Gdx.graphics.getHeight());
		
		//Button group
		buttonGroup = new ButtonGroup<TextButton>();
		buttonGroup.setMaxCheckCount(1);
		buttonGroup.setMinCheckCount(1);
		buttonGroup.add(gestureButton);
		buttonGroup.add(buttonsButton);
		
		//Back button
		backButton = new Button(game.skin.getDrawable("BackButtonIcon"), game.skin.getDrawable("BackButtonIconCheck"));
		backButton.setWidth(Gdx.graphics.getWidth()/10);
		backButton.setHeight(Gdx.graphics.getWidth()/10);
		backButton.setX(Gdx.graphics.getWidth()/50);
		backButton.setY(0.98f * Gdx.graphics.getHeight() - backButton.getHeight());
		
		//Fenetre affichant les options
		imageTableBackground = new Image(game.skin.getDrawable("UIWindow"));
		imageTableBackground.setWidth(Gdx.graphics.getWidth() * 0.3f);
		imageTableBackground.setHeight(Gdx.graphics.getHeight() * 0.4f);
		imageTableBackground.setX(0.94f * Gdx.graphics.getWidth() - imageTableBackground.getWidth());
		imageTableBackground.setY(0.1f * Gdx.graphics.getHeight());
		
		//Animation des gesture control
        controlsAtlas = game.assets.get("Images/Animations/Controls_Animation.pack", TextureAtlas.class);
		controlsAnimation = new Animation(0.03f, controlsAtlas.findRegions("Controls_Animation"), Animation.PlayMode.LOOP);
		
		//Réglages des controls avec boutons		
		labelStyle = new LabelStyle(game.assets.get("fontUpgrade.ttf", BitmapFont.class), Color.WHITE);
		sliderStyle = new SliderStyle(game.skin.getDrawable("SliderBackground"), game.skin.getDrawable("SliderKnob"));
		
		labelSize = new Label(game.text.get("Size"), labelStyle);
		
		sliderSize = new Slider(0.5f, 1.5f, 0.01f, false, sliderStyle);
		sliderSize.getStyle().knob.setMinHeight(2f * sliderSize.getStyle().background.getMinHeight());
		sliderSize.getStyle().knob.setMinWidth(sliderSize.getStyle().knob.getMinHeight() * game.skin.getRegion("SliderKnob").getRegionWidth() / game.skin.getRegion("SliderKnob").getRegionHeight());
		sliderSize.setValue(Data.getControlSize());
		
		labelOpacity = new Label(game.text.get("Opacity"), labelStyle);
			
		sliderOpacity = new Slider(0.05f, 1f, 0.01f, false, sliderStyle);
		sliderOpacity.getStyle().knob.setMinHeight(2f * sliderOpacity.getStyle().background.getMinHeight());
		sliderOpacity.getStyle().knob.setMinWidth(sliderOpacity.getStyle().knob.getMinHeight() * game.skin.getRegion("SliderKnob").getRegionWidth() / game.skin.getRegion("SliderKnob").getRegionHeight());
		sliderOpacity.setValue(Data.getControlOpacity());

		tableSlider = new Table();
		tableSlider.setFillParent(true);
		tableSlider.defaults().width(0.3f * Gdx.graphics.getWidth()).space(0.01f * Gdx.graphics.getHeight());
		tableSlider.add(labelSize).row();
		tableSlider.add(sliderSize).spaceBottom(0.06f * Gdx.graphics.getHeight()).row();
		tableSlider.add(labelOpacity).row();
		tableSlider.add(sliderOpacity);
		
		//Boutons Controls
		buttonLeft = new Button(game.skin.getDrawable("LeftButton"));
		buttonLeft.setWidth(GameConstants.CONTROL_BUTTONS_SIZE * Data.getControlSize());
		buttonLeft.setHeight(buttonLeft.getWidth() * game.skin.getRegion("LeftButton").getRegionHeight()/game.skin.getRegion("LeftButton").getRegionWidth());
		buttonLeft.setX(Gdx.graphics.getWidth()/100);
		buttonLeft.setY(Gdx.graphics.getWidth()/100);
		buttonLeft.addAction(Actions.alpha(Data.getControlOpacity()));

		buttonRight = new Button(game.skin.getDrawable("RightButton"));
		buttonRight.setWidth(buttonLeft.getWidth());
		buttonRight.setHeight(buttonLeft.getHeight());
		buttonRight.setX(buttonLeft.getX() + buttonLeft.getWidth() + 0.01f * Gdx.graphics.getWidth());
		buttonRight.setY(buttonLeft.getY());
		buttonRight.addAction(Actions.alpha(Data.getControlOpacity()));

		buttonJetPack = new Button(game.skin.getDrawable("JetPackButton"));
		buttonJetPack.setWidth(buttonLeft.getWidth());
		buttonJetPack.setHeight(buttonLeft.getWidth());
		buttonJetPack.setX(Gdx.graphics.getWidth() - buttonJetPack.getWidth() - buttonLeft.getX());
		buttonJetPack.setY(buttonLeft.getY());
		buttonJetPack.addAction(Actions.alpha(Data.getControlOpacity()));
		
		if(Data.getGameControls() == GameConstants.ANDROID_BUTTONS_CONTROLS)
			buttonsButton.setChecked(true);
		else if(Data.getGameControls() == GameConstants.ANDROID_GESTURE_CONTROLS)
			gestureButton.setChecked(true);
		
		if(buttonsButton.isChecked()){
			tableSlider.setVisible(true);
			buttonLeft.setVisible(true);
			buttonRight.setVisible(true);
			buttonJetPack.setVisible(true);
		}
		else{
			tableSlider.setVisible(false);
			buttonLeft.setVisible(false);
			buttonRight.setVisible(false);
			buttonJetPack.setVisible(false);
		}

		stage.addActor(backgroundImage);
		stage.addActor(gestureButton);
		stage.addActor(buttonsButton);
		screenTitleWindow.addToStage(stage);
		stage.addActor(backButton);
		stage.addActor(tableSlider);
		stage.addActor(buttonLeft);
		stage.addActor(buttonRight);
		stage.addActor(buttonJetPack);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	
	    stage.act();
	    stage.draw();
	    
	    if(gestureButton.isChecked()){
	    	game.batch.begin();
			animTime += Gdx.graphics.getDeltaTime();
			//Nombre de points upgrade
		    game.batch.setColor(1, 1, 1, 1);
		    game.batch.draw(controlsAnimation.getKeyFrame(animTime), 
				    		0.4f * Gdx.graphics.getWidth(),
				    		0.25f * Gdx.graphics.getHeight(), 
				    		0.3f * Gdx.graphics.getWidth(),
				    		0.3f * Gdx.graphics.getWidth() * controlsAnimation.getKeyFrame(0, true).getRegionHeight() / controlsAnimation.getKeyFrame(0, true).getRegionWidth());	 
	    	game.batch.end();
	    }
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		
		backButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
            	dispose();
				game.setScreen(new OptionScreen(game));
			}
		});
		
		gestureButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				Data.setGameControls(GameConstants.ANDROID_GESTURE_CONTROLS);
				GameConstants.GAME_CONTROLS = Data.getGameControls();
				animTime = 0;
				tableSlider.setVisible(false);
				buttonLeft.setVisible(false);
				buttonRight.setVisible(false);
				buttonJetPack.setVisible(false);
			}
		});
		
		buttonsButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				Data.setGameControls(GameConstants.ANDROID_BUTTONS_CONTROLS);
				GameConstants.GAME_CONTROLS = Data.getGameControls();
				tableSlider.setVisible(true);
				buttonLeft.setVisible(true);
				buttonRight.setVisible(true);
				buttonJetPack.setVisible(true);
			}
		});	
		
		sliderSize.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Data.setControlSize(sliderSize.getValue());		

				buttonLeft.setWidth(GameConstants.CONTROL_BUTTONS_SIZE * Data.getControlSize());
				buttonLeft.setHeight(buttonLeft.getWidth() * game.skin.getRegion("LeftButton").getRegionHeight()/game.skin.getRegion("LeftButton").getRegionWidth());
				buttonLeft.setX(Gdx.graphics.getWidth()/100);
				buttonLeft.setY(Gdx.graphics.getWidth()/100);
				
				buttonRight.setWidth(buttonLeft.getWidth());
				buttonRight.setHeight(buttonLeft.getHeight());
				buttonRight.setX(buttonLeft.getX() + buttonLeft.getWidth() + 0.01f * Gdx.graphics.getWidth());
				buttonRight.setY(buttonLeft.getY());
				
				buttonJetPack.setWidth(buttonLeft.getWidth());
				buttonJetPack.setHeight(buttonLeft.getWidth());
				buttonJetPack.setX(Gdx.graphics.getWidth() - buttonJetPack.getWidth() - buttonLeft.getX());
				buttonJetPack.setY(buttonLeft.getY());
			}
		});	
		
		sliderOpacity.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Data.setControlOpacity(sliderOpacity.getValue());
				buttonLeft.addAction(Actions.alpha(Data.getControlOpacity()));
				buttonRight.addAction(Actions.alpha(Data.getControlOpacity()));
				buttonJetPack.addAction(Actions.alpha(Data.getControlOpacity()));
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

package com.cosmonaut.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.Data;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.ButtonAction;
import com.cosmonaut.Utils.GameConstants;
import com.cosmonaut.Utils.UIWindow;

public class LevelSelectionScreen implements Screen{

	final MyGdxGame game;
	private Image backgroundImage;
	private Texture backgroundTexture;
	private Stage stage;	
	private TextButtonStyle textButtonStyleActive, textButtonStyleInactive;
	private Array<TextButton> levels;
	private Button backButton;
	private Table tableLevels;
	private ButtonAction buttonAction;
	private Label screenTitle;
	private UIWindow screenTitleWindow;
	private LabelStyle screenTitleStyle;
	private Color colorTitle, colorFont;
	
	public LevelSelectionScreen(final MyGdxGame game){
		this.game = game;
		game.blackImage.setTouchable(Touchable.disabled);
		game.blackImage.addAction(Actions.alpha(0));
		game.setFullVersionWindow(	game.text.get("FreeVersion") + GameConstants.FREE_LEVELS + game.text.get("FirstLevels"),
									game.text.get("Features") + "\n- " + game.text.get("MoreLevels") + "\n- " + game.text.get("LongerLevels") + "\n- " + game.text.get("RemoveAds") + "\n\n" + game.text.get("GetFullVersion"), 
									game.fullVersionWindow.getWidth(), 
									0.72f*Gdx.graphics.getHeight(), 
									0.5f, 
									0.5f);
		
		buttonAction = new ButtonAction();
		
		backgroundTexture = new Texture(Gdx.files.internal("Images/LevelScreenBackground.jpg"), true);
		backgroundTexture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.MipMapLinearNearest);
		backgroundImage = new Image(backgroundTexture);
		backgroundImage.setWidth(Gdx.graphics.getWidth());
		backgroundImage.setHeight(backgroundTexture.getHeight() * backgroundImage.getWidth()/backgroundTexture.getWidth());
		backgroundImage.setX(Gdx.graphics.getWidth()/2 - backgroundImage.getWidth()/2);
		backgroundImage.setY(Gdx.graphics.getHeight()/2 - backgroundImage.getHeight()/2);
		
		stage = new Stage();
		
		colorTitle = new Color(); /*Pools.obtain(Color.class)*/;
		colorTitle.set(2/256f, 165/256f, 200/256f, 1);
		
		screenTitleStyle = new LabelStyle(game.assets.get("fontMenu.ttf", BitmapFont.class), colorTitle /*new Color(2/256f, 165/256f, 200/256f, 1)*/);
		screenTitle = new Label(game.text.get("LevelSelection"), screenTitleStyle);
		screenTitle.setAlignment(Align.center);
		
		float screenTitleWindowDimension = 0.465f*Gdx.graphics.getWidth();
		screenTitleWindow = new UIWindow(	game.skin.getDrawable("ScreenTitle"), 
											screenTitleWindowDimension, 
											screenTitleWindowDimension * game.skin.getRegion("ScreenTitle").getRegionHeight()/game.skin.getRegion("ScreenTitle").getRegionWidth(),
											Gdx.graphics.getWidth() - screenTitleWindowDimension,
											Gdx.graphics.getHeight() - screenTitleWindowDimension * game.skin.getRegion("ScreenTitle").getRegionHeight()/game.skin.getRegion("ScreenTitle").getRegionWidth());
		screenTitleWindow.addActorRelativeCentered(screenTitle, 0.525f, 0.59f);
		
		textButtonStyleActive = new TextButtonStyle();
		textButtonStyleActive.up = game.skin.getDrawable("LevelButton");
		textButtonStyleActive.down = game.skin.getDrawable("LevelButtonCheck");
		textButtonStyleActive.font = game.assets.get("fontMenu.ttf", BitmapFont.class);
		textButtonStyleActive.fontColor = colorTitle;
		
		colorFont = Pools.obtain(Color.class);
		colorFont.set(3/256f, 108/256f, 130/256f, 0.65f);
		textButtonStyleInactive = new TextButtonStyle();
		textButtonStyleInactive.up = game.skin.getDrawable("LevelButtonInactive");
		textButtonStyleInactive.font = game.assets.get("fontMenu.ttf", BitmapFont.class);
		textButtonStyleInactive.fontColor = colorFont;
		
		Pools.free(colorTitle);
		Pools.free(colorFont);
		
		//Displaying levels
		tableLevels = new Table();
		tableLevels.defaults().	width(0.083f*Gdx.graphics.getWidth()).
								height(0.083f*Gdx.graphics.getWidth() * game.skin.getRegion("LevelButton").getRegionHeight()/game.skin.getRegion("LevelButton").getRegionWidth()).
								space(0.013f*Gdx.graphics.getWidth());
		
		levels = new Array<TextButton>();
		
		for(int i = 1; i <= GameConstants.NUMBER_OF_LEVEL; i++){
			TextButton textButton = new TextButton("" + (i), textButtonStyleActive);
			levels.add(textButton);
			if((i)%8 == 0) 
				tableLevels.add(textButton).row();
			else 
				tableLevels.add(textButton);
			
			if(!game.levelHandler.isLevelUnlocked(i)){
				textButton.setStyle(textButtonStyleInactive);
				textButton.setTouchable(Touchable.disabled);
			}
		}

		stage.addActor(backgroundImage);
		stage.addActor(tableLevels);
	    stage.draw();
		
		screenTitleWindow.addToStage(stage);
		
		tableLevels.setX(Gdx.graphics.getWidth()/2);
		tableLevels.setY(45*Gdx.graphics.getHeight()/100);		

		//Back button
		backButton = new Button(game.skin.getDrawable("BackButtonIcon"), game.skin.getDrawable("BackButtonIconCheck"));
		backButton.setWidth(Gdx.graphics.getWidth()/10);
		backButton.setHeight(Gdx.graphics.getWidth()/10);
		backButton.setX(0.015f*Gdx.graphics.getWidth());
		backButton.setY(backButton.getX());
		
		//Création des icones upgrade sur les boutons
		Vector2 coordinateVector = Pools.obtain(Vector2.class);
		coordinateVector.set(0,0);
		
		for(int niveau = 0; niveau < levels.size; niveau++){
			for(int upgrade = 1; upgrade < 4; upgrade++){					
				Image image = new Image(new TextureRegion(game.skin.getRegion("WhiteSquare")));
				image.setColor(224/256f, 208/256f, 25/256f, 1);
				image.setHeight(levels.get(niveau).getHeight()/10);
				image.setWidth(image.getHeight()/2);
				image.setX(levels.get(niveau).localToStageCoordinates(coordinateVector.set(0, 0)).x + 0.6f*image.getHeight());
				image.setY(levels.get(niveau).localToStageCoordinates(coordinateVector.set(0, 0)).y + levels.get(niveau).getHeight() - image.getHeight()*(0.5f + 1.1f*upgrade));
				image.setTouchable(Touchable.disabled);
				stage.addActor(image);
				//Couleur des upgrades non ramassées
				if(!game.levelHandler.isUpgradePicked(niveau, upgrade)){
					image.setColor(199/256f, 183/256f, 12/256f, 0.25f);
				}

				//Nombre d'upgrade par niveau
				if(niveau+1 == 1){
					if(upgrade == 3)
						image.setColor(0, 0, 0, 0);
				}
				else if(niveau+1 == 2){
					if(upgrade == 2 || upgrade == 3)
						image.setColor(0, 0, 0, 0);
				}
				else if(niveau+1 == 5){
					if(upgrade == 2 || upgrade == 3)
						image.setColor(0, 0, 0, 0);
				}
				else if(niveau+1 == 8){
					if(upgrade == 3)
						image.setColor(0, 0, 0, 0);
				}
			}
		}
		Pools.free(coordinateVector);
		
		stage.addActor(screenTitle);
		stage.addActor(backButton);
		stage.addActor(game.blackImage);	
		
		if(!Data.getFullVersion())
			game.fullVersionWindow.addToStage(stage);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	    stage.act();
	    stage.draw();
	    
	    if(game.fullVersionButtonDelay > 0)
			game.fullVersionButtonDelay -= Gdx.graphics.getDeltaTime();
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);

		for(int i = 0; i < levels.size; i++){
			if(levels.get(i).getStyle() == textButtonStyleActive)
				buttonAction.levelListener(game, levels.get(i), (i+1));
		}
		
		backButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				game.blackImage.addAction(Actions.sequence(Actions.alpha(1, 0.2f),	 
															Actions.run(new Runnable() {
													            @Override
													            public void run() {
													            	dispose();
																	game.setScreen(new MainMenuScreen(game));
													            }})));
			}
		});
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		System.out.println("Level Selection Screen disposed");
		backgroundTexture.dispose();
		stage.dispose();
		
		//System.gc();
	}
}

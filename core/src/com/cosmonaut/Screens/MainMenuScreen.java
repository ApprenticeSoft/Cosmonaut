package com.cosmonaut.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.cosmonaut.Data;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;
import com.cosmonaut.Utils.UIWindow;

public class MainMenuScreen implements Screen{

	final MyGdxGame game;
	private OrthographicCamera camera;
	private Stage stage;
	private Texture backgroundTexture;
	private Image backgroundImage, transitionImage;
	private TextButtonStyle textButtonStyle;
	private TextButton playButton, upgradeButton, upgradeButton2, optionButton, quitButton;
	private ImageButton facebookButton, twitterButton, moreGamesButton, rateButton, fullVersionButton;
	private Table iconeTable, rateTable;
	private UIWindow rateWindow;
	private LabelStyle rateLabelStyle;
	private Label rateLabel;
	private TextButtonStyle rateButtonStyle;
	private TextButton yesButton, laterButton, neverButton;
	
	private float alpha = 0;
		
	//Test
	//private TextBox textBox;
	//private boolean loadEnd = false;
	private boolean loadIntro = false;
	
	public MainMenuScreen(final MyGdxGame game){
		this.game = game;
		Gdx.input.setCursorCatched(false);
		game.blackImage.setTouchable(Touchable.disabled);
		game.blackImage.addAction(Actions.alpha(0));
		game.setFullVersionWindow(	game.text.get("GetFullVersion"),
									game.text.get("Features") + "\n- " + game.text.get("MoreLevels") + "\n- " + game.text.get("LongerLevels") + "\n- " + game.text.get("RemoveAds"), 
									game.fullVersionWindow.getWidth(), 
									0.61f*Gdx.graphics.getHeight(), 
									0.5f, 
									0.5f);
		
		if(!game.music.isPlaying())
			game.music.play();
		
		GameConstants.GAME_FINISHED = false;
		GameConstants.SELECTED_LEVEL = 1;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		//Background
		backgroundTexture = new Texture(Gdx.files.internal("Images/MainMenuScreenBackground.jpg"), true);
		backgroundTexture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear);
		backgroundImage = new Image(backgroundTexture);
		backgroundImage.setWidth(Gdx.graphics.getWidth());
		backgroundImage.setHeight(backgroundTexture.getHeight() * backgroundImage.getWidth()/backgroundTexture.getWidth());
		backgroundImage.setX(Gdx.graphics.getWidth()/2 - backgroundImage.getWidth()/2);
		backgroundImage.setY(Gdx.graphics.getHeight()/2 - backgroundImage.getHeight()/2);
		
		transitionImage = new Image(backgroundTexture);
		transitionImage.setWidth(Gdx.graphics.getWidth());
		transitionImage.setHeight(backgroundTexture.getHeight() * transitionImage.getWidth()/backgroundTexture.getWidth());
		transitionImage.setX(Gdx.graphics.getWidth()/2 - transitionImage.getWidth()/2);
		transitionImage.setY(Gdx.graphics.getHeight()/2 - transitionImage.getHeight()/2);
		transitionImage.addAction(Actions.alpha(0));
		transitionImage.setVisible(false);
		
		stage = new Stage();
			
		textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = game.skin.getDrawable("Button");
		textButtonStyle.down = game.skin.getDrawable("ButtonCheck");
		textButtonStyle.font = game.assets.get("fontTable.ttf", BitmapFont.class);
		textButtonStyle.fontColor = Color.WHITE;
		textButtonStyle.downFontColor = Color.BLACK;
		
		playButton = new TextButton(game.text.get("Play"), textButtonStyle);
		playButton.setHeight(Gdx.graphics.getHeight()/8);
		playButton.setWidth(new GlyphLayout(game.assets.get("fontTable.ttf", BitmapFont.class), game.text.get("Upgrades")).width * 1.2f);
		playButton.setX(Gdx.graphics.getWidth() - 0.17f * Gdx.graphics.getWidth() - playButton.getWidth()/2);
		playButton.setY(0.6f * Gdx.graphics.getHeight());

		upgradeButton = new TextButton(game.text.get("Upgrades"), textButtonStyle);
		upgradeButton.setWidth(playButton.getWidth());
		upgradeButton.setHeight(playButton.getHeight());
		upgradeButton.setX(playButton.getX());
		upgradeButton.setY(playButton.getY() - upgradeButton.getHeight() - Gdx.graphics.getHeight()/100);

		/*
		 * Upgrade button glow
		 */
		TextButtonStyle textButtonStyle2 = new TextButtonStyle();
		textButtonStyle2.up = game.skin.getDrawable("ButtonCheck");
		textButtonStyle2.down = game.skin.getDrawable("ButtonCheck");
		textButtonStyle2.font = game.assets.get("fontTable.ttf", BitmapFont.class);
		textButtonStyle2.fontColor = Color.WHITE;
		textButtonStyle2.downFontColor = Color.BLACK;
		
		upgradeButton2 = new TextButton(game.text.get("Upgrades"), textButtonStyle2);
		upgradeButton2.setWidth(playButton.getWidth());
		upgradeButton2.setHeight(playButton.getHeight());
		upgradeButton2.setX(playButton.getX());
		upgradeButton2.setY(playButton.getY() - upgradeButton.getHeight() - Gdx.graphics.getHeight()/100);
		upgradeButton2.addAction(Actions.alpha(0));
		/*
		 * ******************************************
		 */
		
		
		optionButton = new TextButton(game.text.get("Options"), textButtonStyle);
		optionButton.setWidth(playButton.getWidth());
		optionButton.setHeight(playButton.getHeight());
		optionButton.setX(playButton.getX());
		optionButton.setY(upgradeButton.getY() - optionButton.getHeight() - Gdx.graphics.getHeight()/100);
		
		quitButton = new TextButton(game.text.get("Quit"), textButtonStyle);
		quitButton.setWidth(playButton.getWidth());
		quitButton.setHeight(playButton.getHeight());
		quitButton.setX(playButton.getX());
		quitButton.setY(optionButton.getY() - quitButton.getHeight() - Gdx.graphics.getHeight()/100);
		
		/*
		 * Réseaux sociaux
		 */
		iconeTable = new Table();
		iconeTable.defaults().width(0.085f*Gdx.graphics.getHeight()).height(0.085f*Gdx.graphics.getHeight()).space(0.04f*Gdx.graphics.getHeight());
		
		facebookButton = new ImageButton(game.skin.getDrawable("Facebook"));		
		twitterButton = new ImageButton(game.skin.getDrawable("Twitter"));
		moreGamesButton = new ImageButton(game.skin.getDrawable("MoreGames_" + Data.getLanguage()));		
		rateButton = new ImageButton(game.skin.getDrawable("Rate"));
		fullVersionButton = new ImageButton(game.skin.getDrawable("FullVersion_" + Data.getLanguage()));	

		iconeTable.add(facebookButton);
		iconeTable.add(twitterButton);
		iconeTable.add(moreGamesButton);
		iconeTable.add(rateButton);
		if(!Data.getFullVersion())
			iconeTable.add(fullVersionButton).width(0.1f*Gdx.graphics.getHeight()).height(0.1f*Gdx.graphics.getHeight());
		else
			fullVersionButton.setTouchable(Touchable.disabled);

		iconeTable.setX(iconeTable.getPrefWidth()/2 + Gdx.graphics.getWidth()/30);
		iconeTable.setY(iconeTable.getPrefHeight()/2 + Gdx.graphics.getWidth()/30);
		
		/*
		 * Incitation à noter
		 */
		rateLabelStyle = new LabelStyle(game.assets.get("fontOption.ttf", BitmapFont.class),Color.WHITE);
		rateLabel = new Label(game.text.get("DoYouLike") + "\n\n" + game.text.get("DoYouRate"), rateLabelStyle);
		rateLabel.setWrap(true);
		rateLabel.setAlignment(Align.center);
		
		rateButtonStyle = new TextButtonStyle();
		rateButtonStyle.up = game.skin.getDrawable("LinearButton");
		rateButtonStyle.down = game.skin.getDrawable("LinearButtonCheck");
		rateButtonStyle.font = game.assets.get("fontHUD.ttf", BitmapFont.class);
		rateButtonStyle.fontColor = Color.WHITE;
		rateButtonStyle.downFontColor = Color.BLACK;
		
		yesButton = new TextButton(game.text.get("Yes", Data.getLanguage()), rateButtonStyle);
		laterButton = new TextButton(game.text.get("Later", Data.getLanguage()), rateButtonStyle);
		laterButton.getLabel().setWrap(true);
		neverButton = new TextButton(game.text.get("Never", Data.getLanguage()), rateButtonStyle);
		
		rateTable = new Table();
		rateTable.add(rateLabel).width(0.33f*Gdx.graphics.getWidth()).colspan(3).row();
		rateTable.add(yesButton).width(0.08f*Gdx.graphics.getWidth()).height(0.052f*Gdx.graphics.getWidth()).padTop(0.05f*Gdx.graphics.getWidth());
		rateTable.add(laterButton).width(0.08f*Gdx.graphics.getWidth()).height(0.052f*Gdx.graphics.getWidth()).padTop(0.05f*Gdx.graphics.getWidth());
		rateTable.add(neverButton).width(0.08f*Gdx.graphics.getWidth()).height(0.052f*Gdx.graphics.getWidth()).padTop(0.05f*Gdx.graphics.getWidth());
		
		rateWindow = new UIWindow(game.skin.getDrawable("DialogueImage"), 0.45f*Gdx.graphics.getWidth(), 0.45f*Gdx.graphics.getHeight());
		rateWindow.addActorRelativeCentered(rateTable, 0.5f, 0.5f);
		rateWindow.alfaZero(0);	
		
		if(Data.getRateCount() < 1){
			rateWindow.alfaOne(0.2f);	
			game.blackImage.setTouchable(Touchable.enabled);
			game.blackImage.addAction(Actions.alpha(0.7f, 0.2f));
		}
		
		/*
		 * Fenêtre achat version complète
		 */
		game.fullVersionWindow.alfaZero(0);	
		
		stage.addActor(backgroundImage);
		stage.addActor(playButton);
		stage.addActor(upgradeButton2);
		stage.addActor(upgradeButton);
		stage.addActor(optionButton);
		stage.addActor(quitButton);
		stage.addActor(iconeTable);
		stage.addActor(transitionImage);
		stage.addActor(game.blackImage);
		rateWindow.addToStage(stage);
		game.fullVersionWindow.addToStage(stage);		
		
		if(!Data.getIntroPlayed())
			game.assets.load("Images/Intro/Images_Intro.pack", TextureAtlas.class);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//Upgrade button glow
		if(Data.getUpgradePoint() > 0){
			if((Data.getFuelLevel() + Data.getOxygenLevel() + Data.getPowerLevel()) == 0){
				alpha += 4*Gdx.graphics.getDeltaTime();
				upgradeButton2.addAction(Actions.alpha((float)(1 + Math.cos(alpha))/2));
			}
		}
		
		game.batch.setProjectionMatrix(camera.combined);
		
		stage.act();
		stage.draw();	
		
		/*
		textBox.activity();		
		if(Gdx.input.isTouched())
			textBox.write(1);
		 */

		if(Data.getFullVersion()){
			fullVersionButton.setTouchable(Touchable.disabled);
			fullVersionButton.setVisible(false);
		}
		else if(game.fullVersionButtonDelay > 0)
			game.fullVersionButtonDelay -= Gdx.graphics.getDeltaTime();

		if(!Data.getIntroPlayed())
			game.assets.update();
		
		/*
		 * TEST FIN
		 */
		/*
		if(loadEnd){
			if(game.assets.update()){
				dispose();
	            game.setScreen(new EndScreen(game));
			}
		}
		*/
		if(loadIntro){
			if(game.assets.update()){
				dispose();
				game.music.stop();
	            game.setScreen(new IntroScreen(game));
			}
		}
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		
		playButton.addListener(new ClickListener(){
			 @Override
		        public void clicked(InputEvent event, float x, float y) {
				transitionImage.setVisible(true);
				transitionImage.addAction(Actions.sequence(Actions.alpha(1, 0.2f),	 
															Actions.run(new Runnable() {
													            @Override
													            public void run() {
													            	if(Data.getIntroPlayed()){
													        			dispose();
													            		game.setScreen(new LevelSelectionScreen(game));
													            	}
													            	else
													            		loadIntro = true;
													            }})));
			 }
		});
		
		upgradeButton.addListener(new ClickListener(){
			 @Override
		        public void clicked(InputEvent event, float x, float y) {
				transitionImage.setVisible(true);
				transitionImage.addAction(Actions.sequence(Actions.alpha(1, 0.2f),	 
															Actions.run(new Runnable() {
													            @Override
													            public void run() {
													            	dispose();
																	game.setScreen(new UpgradeScreen(game));
													            }})));
			 }
		});
		
		optionButton.addListener(new ClickListener(){
			 @Override
		        public void clicked(InputEvent event, float x, float y) {
				transitionImage.setVisible(true);
				transitionImage.addAction(Actions.sequence(Actions.alpha(1, 0.2f),	 
															Actions.run(new Runnable() {
													            @Override
													            public void run() {
													            	dispose();
													            	game.setScreen(new OptionScreen(game));
													            }})));
			 }
		});
		
		quitButton.addListener(new ClickListener(){
			 @Override
		        public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			 }
		});

		facebookButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				
				if(Gdx.app.getType() == ApplicationType.Desktop)
					Gdx.net.openURI("https://facebook.com/profile.php?id=157533514581396");
				else
					Gdx.net.openURI("https://m.facebook.com/profile.php?id=157533514581396");
				
				/*
				game.assets.load("Images/Intro/Images_Intro.pack", TextureAtlas.class);
				loadIntro = true;
				game.music.stop();
				*/
			}
		});	

		twitterButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
		       	Gdx.net.openURI("https://twitter.com/ApprenticeSoft");
		       	/*
				game.assets.load("Images/Fin/Images_Fin.pack", TextureAtlas.class);
				loadEnd = true;
				game.music.stop();
				*/
			}
		});
		
		rateButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				Data.setRate(true);
				Data.setRateCount(3);

				if(Gdx.app.getType() == ApplicationType.Desktop)
					Gdx.net.openURI(GameConstants.ITCH_IO_GAME_URL);
				else
					Gdx.net.openURI(GameConstants.GOOGLE_PLAY_GAME_URL);
				
			}
		});
		
		moreGamesButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				if(Gdx.app.getType() == ApplicationType.Desktop)
					Gdx.net.openURI(GameConstants.ITCH_IO_STORE_URL);
				else
					Gdx.net.openURI(GameConstants.GOOGLE_PLAY_STORE_URL);
			}
		});
		
		yesButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				game.blackImage.addAction(Actions.alpha(0, 0.2f));
				game.blackImage.setTouchable(Touchable.disabled);
				rateWindow.alfaZero(.2f);
		       	Data.setRate(true);
				Data.setRateCount(3);

				if(Gdx.app.getType() == ApplicationType.Desktop)
					Gdx.net.openURI(GameConstants.ITCH_IO_GAME_URL);
				else
					Gdx.net.openURI(GameConstants.GOOGLE_PLAY_GAME_URL);
			}
		});

		laterButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				game.blackImage.addAction(Actions.alpha(0, 0.2f));
				game.blackImage.setTouchable(Touchable.disabled);
				rateWindow.alfaZero(.2f);	
				Data.setRateCount(3);
			}
		});

		neverButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				game.blackImage.addAction(Actions.alpha(0, 0.2f));
				game.blackImage.setTouchable(Touchable.disabled);
				rateWindow.alfaZero(.2f);
				Data.setRate(true);	
				Data.setRateCount(3);
			}
		});

		fullVersionButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				game.fullVersionWindow.alfaOne(0.2f);	
				game.blackImage.setTouchable(Touchable.enabled);
				game.blackImage.addAction(Actions.alpha(0.7f, 0.2f));
			}
		});
		
		game.noFVButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				game.fullVersionWindow.alfaZero(0.2f);	
				game.blackImage.setTouchable(Touchable.disabled);
				game.blackImage.addAction(Actions.alpha(0, 0.2f));
				
				if(game.getScreen().getClass().toString().equals("class com.cosmonaut.Screens.GameScreen")){
					game.fullVersionWindow.alfaZero(0);	
					game.getScreen().dispose();
					game.setScreen(new MainMenuScreen(game));
				}
			}
		});
		
		game.yesFVButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				if(game.fullVersionButtonDelay <= 0){
					game.fullVersionButtonDelay = 0.5f;
					game.fullVersionWindow.alfaZero(0.2f);	
					game.blackImage.setTouchable(Touchable.disabled);
					game.blackImage.addAction(Actions.alpha(0, 0.2f));
					
					game.actionResolver.removeAds();
				}
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
		stage.dispose();
		backgroundTexture.dispose();
		//System.gc();
		//game.skin.dispose();
	}

}

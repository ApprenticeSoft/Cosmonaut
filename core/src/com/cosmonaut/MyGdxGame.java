package com.cosmonaut;

import java.nio.IntBuffer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.Bodies.CheckPoint;
import com.cosmonaut.Bodies.Dialogue;
import com.cosmonaut.Bodies.Exit;
import com.cosmonaut.Bodies.ItemSwitch;
import com.cosmonaut.Bodies.Leak;
import com.cosmonaut.Bodies.Obstacle;
import com.cosmonaut.Bodies.ObstacleLightning;
import com.cosmonaut.Bodies.Polygone;
import com.cosmonaut.Bodies.Wall;
import com.cosmonaut.Items.Gyrophare;
import com.cosmonaut.Items.Item;
import com.cosmonaut.Lights.MyLight;
import com.cosmonaut.Screens.LoadingScreen;
import com.cosmonaut.Utils.GameConstants;
import com.cosmonaut.Utils.LevelHandler;
import com.cosmonaut.Utils.MyPools;
import com.cosmonaut.Utils.TextFromXML;
import com.cosmonaut.Utils.UIWindow;

public class MyGdxGame extends Game implements ApplicationListener{
	public SpriteBatch batch;
	public AssetManager assets;
	public Music music;
	public LevelHandler levelHandler;
	public TextFromXML text;
	public ActionResolver actionResolver;
	public int hauteureBanniere;
	
	/*
	 * Nouvelle gestion 
	 */
	public Skin skin;
	public TextureAtlas textureAtlas;
	public Image blackImage;
	public UIWindow fullVersionWindow;
	private LabelStyle textLabelStyle, titleLabelStyle;
	public Label fullVersionLabel, titleLabel;
	private TextButtonStyle textButtonStyle;
	public TextButton yesFVButton, noFVButton;
	public Table fullVersionTable;
	private boolean loadingFinished = false;
	public float fullVersionButtonDelay = 0;
	public Array<Music> musics;
	public MyPools pools;
	
	/*
	 * TEST TABLEAUX
	 */
	public Array<Obstacle> obstacles, obstaclesWithNinePatch, activableObstacles, obstaclesWithSound;
	public Array<Wall> walls;
	public Array<Polygone> polygones;
	public Array<Leak> leaks;
	public Array<ItemSwitch> switchs;
	public Array<Item> items;
	public Array<MapObject> pistons;
	public Array<Exit> exits;
	public Array<MyLight> myLights;
	public Array<Gyrophare> gyrophares;
	public Array<ObstacleLightning> lightnings;
	public Array<Dialogue> dialogues;
	public Array<CheckPoint> checkpoints;
	
	public MyGdxGame(ActionResolver actionResolver){
		this.actionResolver = actionResolver;
	}
	
	@Override
	public void create () {
		setFullScreen(false);
		Data.Load();
		
		pools = new MyPools(this);
		Pools.get(Vector2.class, 1000);	
		
		/*
		 * Tableaux divers
		 */
		obstacles = new Array<Obstacle>(); 
        obstaclesWithNinePatch = new Array<Obstacle>(false, 16); 
        obstaclesWithSound = new Array<Obstacle>(false, 16);    
        activableObstacles = new Array<Obstacle>(false, 16);    
        walls = new Array<Wall>(false, 16);  
        polygones = new Array<Polygone>(false, 16);
        pistons = new Array<MapObject>(false, 16);
        switchs = new Array<ItemSwitch>(false, 16);
        items = new Array<Item>(false, 16);
        leaks = new Array<Leak>(false, 16);
        exits = new Array<Exit>(false, 16);
        myLights = new Array<MyLight>(false, 16);
        gyrophares = new Array<Gyrophare>(false, 16);
        lightnings = new Array<ObstacleLightning>(false, 16);
        dialogues = new Array<Dialogue>(false, 16);
        checkpoints = new Array<CheckPoint>(false, 16);
		
		/*
		 * Controls du jeu et qualité des lumière en fonction du système utilisé
		 */   
		if(Gdx.app.getType() == ApplicationType.Desktop){
			if(Data.getGameControls() != 1 && Data.getGameControls() != 5){
				if(java.util.Locale.getDefault().getCountry().equals("FR"))
					Data.setGameControls(5);
				else
					Data.setGameControls(1);	
			}
			GameConstants.LIGHT_RAY_MULTIPLICATOR = 3;
			GameConstants.GAME_VERSION = "desktop";
			Data.setFullVersion(true);
			GameConstants.SOUND_DISTANCE_LIMITE *= 2.5f;
			
			

			/*
			//Curseur
			Pixmap cursorPM = new Pixmap(Gdx.files.internal("Images/Curseur.png"));
			cursorPM.setFilter(Filter.BiLinear);
			Cursor customCursor =  Gdx.graphics.newCursor(cursorPM, 6, 6);
			Gdx.graphics.setCursor(customCursor);
			*/
		}
		else if(Gdx.app.getType() == ApplicationType.Android){
			if(Data.getGameControls() < 3 || Data.getGameControls() > 4){
				Data.setGameControls(3);
			}
			GameConstants.LIGHT_RAY_MULTIPLICATOR = 1;
			GameConstants.GAME_VERSION = "android";
		}
		
		GameConstants.GAME_CONTROLS = Data.getGameControls();	

		/*
		 * Résolution de l'écran
		 */
		if(Gdx.graphics.getHeight() < 720)
			GameConstants.SCREEN_RESOLUTION = "SD";
		else
			GameConstants.SCREEN_RESOLUTION = "HD";
				
		/*
		 * Détermination de la langue de jeu
		 */
		if(!Data.getManualLanguage()){
			if(java.util.Locale.getDefault().toString().startsWith("en"))
				Data.setLanguage("EN");
			else if(java.util.Locale.getDefault().toString().startsWith("fr"))
				Data.setLanguage("FR");
			else if(java.util.Locale.getDefault().toString().startsWith("es"))
				Data.setLanguage("ES");
			else if(java.util.Locale.getDefault().toString().startsWith("de"))
				Data.setLanguage("DE");
			else 	
				Data.setLanguage("EN");
		}

		/*
		 * Chargement des texts
		 */
		text = new TextFromXML("Texts/Text.xml");
		
		batch = new SpriteBatch();
		assets = new AssetManager();
		
		levelHandler = new LevelHandler("game");
		levelHandler.setState(GameConstants.NUMBER_OF_LEVEL);
		/*
		Data.setUpgradePoint(100);
		for(int i = 1; i < 25; i++)
			levelHandler.setLevelUnlocked(i);
		
		Data.setIntroPlayed(true);
		*/
		music = Gdx.audio.newMusic(Gdx.files.internal("Sounds/Space.ogg"));
		music.setLooping(true);
		music.play();
		
		/*
		 * Musiques
		 */
		musics = new Array<Music>();
		
		/*
		 * Table notation
		 */
		if(!Data.getRate())
			Data.setRateCount(Data.getRateCount() - 1);
		
		/*
		 * Publicités
		 */
		if(!Data.getFullVersion())
			actionResolver.LoadInterstital();
			
		//Taille max des textures
		IntBuffer intBuffer = BufferUtils.newIntBuffer(16);
		Gdx.gl20.glGetIntegerv(GL20.GL_MAX_TEXTURE_SIZE, intBuffer);
		/*
		System.out.println("------------------------------------------MAX TEXTURE SIZE = " + intBuffer.get());
		System.out.println("------------------------------------------FPS = " + Gdx.app.getGraphics().getFramesPerSecond());
		System.out.println("------------------------------------------OpenGl 3.0 = " + Gdx.app.getGraphics().isGL30Available());
		System.out.println("------------------------------------------Buffer Format = " + Gdx.app.getGraphics().getBufferFormat());
		System.out.println("------------------------------------------Display modes = " + Gdx.app.getGraphics().getDisplayModes());
		*/
		this.setScreen(new LoadingScreen(this));
		
		//Compte des points upgrade par niveau
		/*
		int totalUpgrade = 0;
		for(int i = 1; i <= GameConstants.NUMBER_OF_LEVEL; i++){
			int nbUpgrade = 0;
			TiledMap tiledMap = new TmxMapLoader().load("Levels/HD/Level " + i + ".tmx");
			for(int j = 0; j < tiledMap.getLayers().get("Spawn").getObjects().getCount(); j++){
				if(tiledMap.getLayers().get("Spawn").getObjects().get(j).getProperties().get("type") != null){	
					if(tiledMap.getLayers().get("Spawn").getObjects().get(j).getProperties().get("type").equals("Upgrade"))
						nbUpgrade++;
				}
			}	
			System.out.println("Nombre d'upgrade dans le niveau " + i + " = " + nbUpgrade);
			totalUpgrade += nbUpgrade;
		}
		System.out.println("Nombre total d'upgrade = " + totalUpgrade);
		*/

		//Data.setUpgradePoint(100);
		//Data.setFuelLevel(0);
		//Data.setOxygenLevel(0);
	}

	@Override
	public void render () {
		super.render();
		//pools.write();
		
		/*
		 * Utilisation de la mémoire
		 */
		//System.out.println("***************Utilisation de la mémoire***************");
		//System.out.println("Gdx.app.getJavaHeap() = " + ((float)Gdx.app.getJavaHeap()/1000000));
		//System.out.println("Gdx.app.getNativeHeap() = " + Gdx.app.getNativeHeap());
		//System.out.println("Pools.get(Vector2.class).peak = " + Pools.get(Vector2.class).peak);
		//System.out.println("Pools.get(Vector2.class).getFree() = " + Pools.get(Vector2.class).getFree());
		
		/*
		 * Publicité interstitielle
		 */	
		if(!Data.getFullVersion()){
			if(GameConstants.INTERSTITIAL_TRIGGER < 1){
				GameConstants.INTERSTITIAL_TRIGGER = MathUtils.random(1,2);
				actionResolver.showOrLoadInterstital();
			}
		}
		
		if(!loadingFinished)
			if(assets.update()){
	    		loadingFinished = true;

	    		/*
	    		 * Fenêtre achat version complète
	    		 */
	    		skin = new Skin();
	    		
	    		textureAtlas = assets.get("Images/" + GameConstants.SCREEN_RESOLUTION + "/Images.pack", TextureAtlas.class);
	    		skin.addRegions(textureAtlas);
	    		
	    		textLabelStyle = new LabelStyle(assets.get("fontOption.ttf", BitmapFont.class),Color.WHITE);
	    		titleLabelStyle = new LabelStyle(assets.get("fontUpgrade.ttf", BitmapFont.class),new Color(2/256f, 165/256f, 200/256f, 1));
	    		
	    		textButtonStyle = new TextButtonStyle();
	    		textButtonStyle.up = skin.getDrawable("LinearButton");
	    		textButtonStyle.down = skin.getDrawable("LinearButtonCheck");
	    		textButtonStyle.font = assets.get("fontHUD.ttf", BitmapFont.class);
	    		textButtonStyle.fontColor = new Color(1, 1, 1, 1);
	    		textButtonStyle.downFontColor = new Color(0, 0, 0, 1);
	    		
	    		fullVersionWindow = new UIWindow(skin.getDrawable("DialogueImage"), 0.6f*Gdx.graphics.getWidth(), 0.61f*Gdx.graphics.getHeight());
	    		
	    		titleLabel = new Label(text.get("GetFullVersion"), titleLabelStyle);
	    		titleLabel.setWrap(true);
	    		titleLabel.setAlignment(Align.center);
	    		
	    		fullVersionLabel = new Label("- " + text.get("MoreLevels") + "\n- " + text.get("RemoveAds"), textLabelStyle);
	    		fullVersionLabel.setWrap(true);
	    		fullVersionLabel.setAlignment(Align.left);
	    		
	    		yesFVButton = new TextButton(text.get("Yes", Data.getLanguage()), textButtonStyle);
	    		noFVButton = new TextButton(text.get("No", Data.getLanguage()), textButtonStyle);
	    		
	    		fullVersionTable = new Table();
	    		fullVersionTable.add(titleLabel).width(0.9f*fullVersionWindow.getWidth()).colspan(2).row();
	    		fullVersionTable.add(fullVersionLabel).width(0.9f*fullVersionWindow.getWidth()).colspan(2).space(0.05f*Gdx.graphics.getHeight()).row();
	    		fullVersionTable.add(yesFVButton).width(0.08f*Gdx.graphics.getWidth()).height(0.052f*Gdx.graphics.getWidth());
	    		fullVersionTable.add(noFVButton).width(0.08f*Gdx.graphics.getWidth()).height(0.052f*Gdx.graphics.getWidth());
	    		
	    		fullVersionWindow.addActorRelativeCentered(fullVersionTable, 0.5f, 0.5f);
	    		
	    		/*
	    		 * Image de transition
	    		 */
	    		blackImage = new Image(skin.getDrawable("WhiteSquare"));
	    		blackImage.setColor(0, 0, 0, 0.5f);
	    		blackImage.setWidth(Gdx.graphics.getWidth());
	    		blackImage.setHeight(Gdx.graphics.getHeight());
	    		blackImage.setX(0);
	    		blackImage.setY(0);
	    		blackImage.setTouchable(Touchable.disabled);
	    		blackImage.addAction(Actions.alpha(0));    		
			}
	}
	
	public void setFullVersionWindow(String labelTitle, String labelText, float width, float height, float x, float y){
		yesFVButton.setText(text.get("Yes", Data.getLanguage()));
		noFVButton.setText(text.get("No", Data.getLanguage()));
		titleLabel.setText(labelTitle);
		fullVersionLabel.setText(labelText);
		fullVersionWindow.setWidth(width);
		fullVersionWindow.setHeight(height);
		fullVersionWindow.setActorPositionRelativeCentered(fullVersionTable, x, y);
	}
	
	public void setFullScreen(boolean isFullScreen){
		// set resolution to HD ready (1280 x 720) and set full-screen to true
		//Gdx.graphics.setDisplayMode(1280, 720, true);
		
		if(isFullScreen){
			// set resolution to default and set full-screen to true
			Gdx.graphics.setDisplayMode(  Gdx.graphics.getDesktopDisplayMode().width,
							              Gdx.graphics.getDesktopDisplayMode().height, 
							              true);
		}
	}
	
	public void clearArrays(){
		obstacles.clear();
        obstaclesWithNinePatch.clear(); 
        obstaclesWithSound.clear();    
        activableObstacles.clear();    
        walls.clear();  
        polygones.clear();
        pistons.clear();
        switchs.clear();
        items.clear();
        leaks.clear();
        exits.clear();
        myLights.clear();
        gyrophares.clear();
        lightnings.clear();
        dialogues.clear();
        checkpoints.clear();
	}
}

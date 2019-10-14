package com.cosmonaut.Screens;

import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.Data;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Bodies.ItemSwitch;
import com.cosmonaut.Bodies.Leak;
import com.cosmonaut.Bodies.Obstacle;
import com.cosmonaut.Items.Item;
import com.cosmonaut.Utils.GameConstants;
import com.cosmonaut.Utils.HUD;
import com.cosmonaut.Utils.MyCamera;
import com.cosmonaut.Utils.MyGestureListener;
import com.cosmonaut.Utils.TiledMapReader;

public class GameScreen implements Screen{

	final MyGdxGame game;
	protected MyCamera camera;
	TiledMap tiledMap;
	TiledMapRenderer tiledMapRenderer;
	//TEST
	OrthoCachedTiledMapRenderer orthoCachedTiledMapRenderer;
	protected TiledMapReader mapReader;
	protected World world;
	Fixture fixtureA, fixtureB;
    private Box2DDebugRenderer debugRenderer;
    
    //Graphics
    protected HUD hud;
    protected Stage stage;
    private int[] background = {0,1};
    private int[] walls = {2};
    
    //Background
    private Texture backgroundTexture;
    protected float backgroundTime;
    
    //Background sound
    private Music backgroundSound;
    
    //Box2dLights
	private RayHandler rayHandler;
	private int lightAlpha;
	
	//Gesture Listener
	protected MyGestureListener gestureListener;
	protected InputMultiplexer inputMultiplexer;
	
	public GameScreen(final MyGdxGame game){
		this.game= game;
		game.blackImage.setTouchable(Touchable.disabled);
		game.blackImage.addAction(Actions.alpha(0));
		game.setFullVersionWindow(	game.text.get("ThankYou"),
									game.text.get("Features") + "\n- " + game.text.get("MoreLevels") + "\n- " + game.text.get("LongerLevels") + "\n- " + game.text.get("RemoveAds") + "\n\n" + game.text.get("GetFullVersion"), 
									game.fullVersionWindow.getWidth(), 
									0.67f*Gdx.graphics.getHeight(), 
									0.5f, 
									0.5f);

		GameConstants.BOX_STEP = 1/60f;
		GameConstants.TUTORIAL = false;
		GameConstants.LEVEL_FINISHED = false;
		GameConstants.GAME_PAUSED = false;
		GameConstants.GAME_LOST = false;
		GameConstants.UPDATE_STATE = false;
		GameConstants.ANIM_TIME = 0;
		GameConstants.UPGRADE_POINT = 0;
		GameConstants.LEVEL_TIME = 0;
		checkUpgrades();
		
		backgroundSound = game.assets.get("Sounds/Background.ogg", Music.class);
		backgroundSound.setLooping(true);
		try{
			backgroundSound.play();
		}catch(Exception e){
			System.out.println("Exception: " + e.getMessage());
		}
		backgroundSound.setVolume(0.15f);

		Vector2 gravity = Pools.obtain(Vector2.class).set(0, GameConstants.GRAVITY);
        world = new World(gravity, true);
        Pools.free(gravity);
        World.setVelocityThreshold(0.0f);
        debugRenderer = new Box2DDebugRenderer();
        
        //Test Box2DLight
        RayHandler.useDiffuseLight(true); 

        rayHandler = new RayHandler(world); 
        rayHandler.resizeFBO(Gdx.graphics.getWidth()/5, Gdx.graphics.getHeight()/5);   
        rayHandler.setBlur(true);
        //rayHandler.setBlurNum(1);
        //rayHandler.setShadows(true);
            
		//Gdx.gl.glEnable(GL20.GL_DITHER);

        //TEST filter for Tiled Map
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.generateMipMaps = true;
        params.textureMagFilter = Texture.TextureFilter.MipMapLinearNearest;
        params.textureMinFilter = Texture.TextureFilter.MipMapLinearNearest;
        
        //tiledMap = new TmxMapLoader().load("Levels/HD/Level " + GameConstants.SELECTED_LEVEL + ".tmx", params);
        //tiledMap = new TmxMapLoader().load("Levels/New Level.tmx", params);
        
        //Test asset manager
        game.assets.load("Levels/Level " + GameConstants.SELECTED_LEVEL + ".tmx", TiledMap.class);
        game.assets.finishLoading();
        tiledMap = game.assets.get("Levels/Level " + GameConstants.SELECTED_LEVEL + ".tmx", TiledMap.class);
        
        //Test nouvelle caméra
        camera = new MyCamera();
		camera.setToOrtho(false, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        camera.update(); 
        
        /*****************Tiled Map Renderer*******************/
        //tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(tiledMap, GameConstants.MPP, game.batch);
        orthoCachedTiledMapRenderer = new OrthoCachedTiledMapRenderer(tiledMap, GameConstants.MPP);
        orthoCachedTiledMapRenderer.setBlending(true);
        //orthoCachedTiledMapRenderer.setOverCache(GameConstants.OVER_CACHE);
        /********************************************************/

        //mapReader = new TiledMapReader(game, tiledMap, world, polygonShape, fixtureDef, bodyDef, camera, rayHandler); 
        mapReader = new TiledMapReader(game, tiledMap, world, camera, rayHandler); 
        //rayHandler.setAmbientLight(new Color(0, 0, 0, mapReader.ambiantLightMin));
        //rayHandler.setAmbientLight(new Color(0.3f, 0.3f, 0.3f, mapReader.ambiantLightMin)); 
        Color colorAmbiantLight = Pools.obtain(Color.class);
        colorAmbiantLight.set(mapReader.ambiantLightMin, mapReader.ambiantLightMin, mapReader.ambiantLightMin, 0.1f);
        rayHandler.setAmbientLight(colorAmbiantLight); 
        Pools.free(colorAmbiantLight);
             
        //Graphics
        stage = new Stage(); 
		hud = new HUD(game, stage, game.skin, mapReader.hero); 
		stage.addActor(game.blackImage);	
		
		if(!Data.getFullVersion())
			game.fullVersionWindow.addToStage(stage);

        //Background 
		backgroundTexture = game.assets.get("Images/Stars.jpg", Texture.class);
		backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		
		//Gesture Listener
        gestureListener = new MyGestureListener(game, world, camera, mapReader.hero);
      	inputMultiplexer = new InputMultiplexer();     
        /*
    	//Dernier niveau
        if(GameConstants.SELECTED_LEVEL == 24){
        	System.out.println("Dernier niveau");
			game.assets.load("Images/Fin/Images_Fin.pack", TextureAtlas.class);
        }
        */
	}

	@Override
	public void render(float delta) {  
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_BLEND); 
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		cameraActivity();     
        //tiledMapRenderer.setView(camera); 
        /****************TEST Tiled Map Renderer with cache**************/
        orthoCachedTiledMapRenderer.setView(camera);
        game.batch.setProjectionMatrix(camera.combined);
        /****************************************************************/      
 
        //Level finished
        if(GameConstants.LEVEL_FINISHED)
        	finishLevel();
        
		if(!GameConstants.GAME_PAUSED){
			Gdx.input.setCursorCatched(true);
	        //Animation
	        GameConstants.ANIM_TIME += Gdx.graphics.getDeltaTime();
	        backgroundTime += Gdx.graphics.getDeltaTime();
	        GameConstants.LEVEL_TIME += Gdx.graphics.getDeltaTime();
	        
			world.step(GameConstants.BOX_STEP, GameConstants.BOX_VELOCITY_ITERATIONS, GameConstants.BOX_POSITION_ITERATIONS);
			mapReader.active();
	        
	        if(Gdx.input.isKeyJustPressed(Keys.ESCAPE))
	        	hud.pause();
	        
	        //Out of fuel
	        //if (mapReader.hero.getFuelLevel() <= 0)
	        //	hud.outOfFuel();	

	        if(GameConstants.GAME_LOST)
	        	hud.lose();

			hud.update();
		}
		else{
			Gdx.input.setCursorCatched(false);
			mapReader.soundPause();
			
	        if(Gdx.input.isKeyJustPressed(Keys.ESCAPE))
	        	if(!GameConstants.GAME_LOST && !GameConstants.LEVEL_FINISHED)
	        		hud.resume();
		}
		
		stage.act();
		
		//Drawing graphics
		//Background	
		game.batch.begin();
		game.batch.draw(backgroundTexture, 
						0, 
						0, 
						GameConstants.LEVEL_PIXEL_WIDTH, 
						GameConstants.LEVEL_PIXEL_HEIGHT,  
						(int)(backgroundTime * 8), 
						0, 
						(int)(GameConstants.LEVEL_PIXEL_WIDTH * 30), 
						(int)(GameConstants.LEVEL_PIXEL_HEIGHT * 30), 
						false, 
						false);
		game.batch.end();
		
		//Game map
        //tiledMapRenderer.render(background);
        orthoCachedTiledMapRenderer.render(background);
		    
		//HUD and hero
		game.batch.begin();
		mapReader.draw(game.batch, game.textureAtlas, backgroundTime);
		game.batch.end();
  		
		//tiledMapRenderer.render(walls);
        orthoCachedTiledMapRenderer.render(walls);     	
		
		//Test Box2DLight
		rayHandler.setCombinedMatrix(camera);
		rayHandler.updateAndRender();

		//Lightnings
		game.batch.begin();
		mapReader.drawLightnings(game.batch, game.textureAtlas);
		hud.draw(camera);
		game.batch.end();
		
		//Light flickering
		if(mapReader.lightFlicker){
			lightAlpha = MathUtils.random(1,100);		
			if(lightAlpha < mapReader.flickerFactor)
				rayHandler.setAmbientLight(mapReader.ambiantLightMax, mapReader.ambiantLightMax, mapReader.ambiantLightMax, 0.1f);
			else
				rayHandler.setAmbientLight(mapReader.ambiantLightMin, mapReader.ambiantLightMin, mapReader.ambiantLightMin, 0.1f);
		}

		stage.draw();
		
		//Dernier niveau	
		if(GameConstants.SELECTED_LEVEL == 24){
			//game.assets.update();

			if(GameConstants.GAME_FINISHED && game.assets.update()){
				if(game.blackImage.getColor().a > 0.95f){
					game.getScreen().dispose();
					game.setScreen(new EndScreen(game));
				}
			}
        }     
		//if(Gdx.input.isKeyPressed(Input.Keys.L))
			//debugRenderer.render(world, camera.combined);	
		//System.out.println("FPS : " + Gdx.graphics.getFramesPerSecond());		
	}

	@Override
	public void show() {
		inputMultiplexer.addProcessor(new GestureDetector(gestureListener));
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inputMultiplexer);
		hud.buttonListener();
		
		world.setContactListener(new ContactListener(){
			@Override
			public void beginContact(Contact contact) {
				fixtureA = contact.getFixtureA();
				fixtureB = contact.getFixtureB();
			    
			    if(fixtureA.getUserData() != null && fixtureB.getUserData() != null) {
			    	//Finish the level
			    	if(fixtureA.getUserData().equals("Tom") && fixtureB.getUserData().equals("Exit")){
			    		mapReader.exit.open = true;
			    		mapReader.exit.heroContact = true;
			    	}    		
			    	else if(fixtureB.getUserData().equals("Tom") && fixtureA.getUserData().equals("Exit")){
			    		mapReader.exit.open = true;
			    		mapReader.exit.heroContact = true;
			    	}    
			    	
			    	//Leak
				    if (fixtureA.getUserData().equals("Leak") && fixtureB.getBody().getType() == BodyType.DynamicBody) {
				    	for(Obstacle obstacle : game.leaks){
				    		if(obstacle.body.getFixtureList().get(0) == fixtureA){
				    			Leak leak = (Leak) obstacle;
				    			leak.addBody(fixtureB);
				    		}
				    	}
					} 
				    else if (fixtureB.getUserData().equals("Leak") && fixtureA.getBody().getType() == BodyType.DynamicBody) {
				    	for(Obstacle obstacle : game.leaks){
				    		if(obstacle.body.getFixtureList().get(0) == fixtureB){
				    			Leak leak = (Leak) obstacle;
				    			leak.addBody(fixtureA);
				    		}
				    	}
					}
				    
				    //Lightning
				    if(fixtureA.getUserData().equals("Tom") && fixtureB.getUserData().equals("Lightning")){
				    	mapReader.hero.death(game.text.get("Electrocuted").toUpperCase());
			    	}
			    	else if(fixtureB.getUserData().equals("Tom") && fixtureA.getUserData().equals("Lightning")){
				    	mapReader.hero.death(game.text.get("Electrocuted").toUpperCase());
			    	}
				    
				    //Switch
			    	if(fixtureA.getUserData().equals("Tom") && fixtureB.getUserData().equals("Switch")){
			    		for(ItemSwitch itemSwitch : game.switchs){
			    			if(itemSwitch.switchBody == fixtureB.getBody())
			    				itemSwitch.active(game.activableObstacles);
			    		}
			    	}
			    	else if(fixtureB.getUserData().equals("Tom") && fixtureA.getUserData().equals("Switch")){
			    		for(ItemSwitch itemSwitch : game.switchs){
			    			if(itemSwitch.switchBody == fixtureA.getBody())
			    				itemSwitch.active(game.activableObstacles);
			    		}
			    	}
				    
			    	//Items
			    	if(fixtureA.getUserData().equals("Tom") && fixtureB.getUserData().equals("Item")){
			    		for(Item item : game.items){
			    			if(item.body == fixtureB.getBody())
			    				item.activate();
			    		}
			    	}
			    	if(fixtureB.getUserData().equals("Tom") && fixtureA.getUserData().equals("Item")){
			    		for(Item item : game.items){
			    			if(item.body == fixtureA.getBody())
			    				item.activate();
			    		}
			    	}
				}  
			}

			@Override
			public void endContact(Contact contact) {
				fixtureA = contact.getFixtureA();
				fixtureB = contact.getFixtureB();
				
				if(fixtureA.getUserData() != null && fixtureB.getUserData() != null) {
			    	//Leak
				    if (fixtureA.getUserData().equals("Leak") && fixtureB.getBody().getType() == BodyType.DynamicBody) {
				    	for(Obstacle obstacle : game.leaks){
				    		if(obstacle.body.getFixtureList().get(0) == fixtureA){
				    			Leak leak = (Leak) obstacle;
				    			leak.removeBody(fixtureB);
				    		}
				    	}
					} 
				    else if (fixtureB.getUserData().equals("Leak") && fixtureA.getBody().getType() == BodyType.DynamicBody) {
				    	for(Obstacle obstacle : game.leaks){
				    		if(obstacle.body.getFixtureList().get(0) == fixtureB){
				    			Leak leak = (Leak) obstacle;
				    			leak.removeBody(fixtureA);
				    		}
				    	}
					}
			    	//Finish the level
			    	if(fixtureA.getUserData().equals("Tom") && fixtureB.getUserData().equals("Exit")){
			    		mapReader.exit.heroContact = false;
			    	}    		
			    	else if(fixtureB.getUserData().equals("Tom") && fixtureA.getUserData().equals("Exit")){
			    		mapReader.exit.heroContact = false;
			    	}  
				}
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				fixtureA = contact.getFixtureA();
				fixtureB = contact.getFixtureB();
				
				if((fixtureA.getUserData() != null && fixtureA.getUserData().equals("Obstacle")) && (fixtureB.getUserData() != null && fixtureB.getUserData().equals("Obstacle"))) {
			    	contact.setEnabled(false);
				}
				
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				fixtureA = contact.getFixtureA();
				fixtureB = contact.getFixtureB();
			    
				//Hero death by crushing
			    if(fixtureA.getBody().getUserData().equals("Tom") || fixtureB.getBody().getUserData().equals("Tom")){ 
			    	for(int i = 0; i < impulse.getNormalImpulses().length; i++){
				    	if(impulse.getNormalImpulses()[i] > GameConstants.CRUSH_IMPULSE){
				    		mapReader.hero.death(game.text.get("Crushed").toUpperCase());
				    	}
			    	}
			    }
			}
		});
	}

	@Override
	public void resize(int width, int height) {
		GameConstants.SCREEN_RATIO = (float)Gdx.graphics.getHeight()/(float)Gdx.graphics.getWidth();
		GameConstants.SCREEN_WIDTH = GameConstants.MPP * GameConstants.NB_HORIZONTAL_TILE * GameConstants.PPT;
		GameConstants.SCREEN_HEIGHT = GameConstants.SCREEN_WIDTH * GameConstants.SCREEN_RATIO;

		camera.setToOrtho(false, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        camera.update();  
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
	
	public void finishLevel(){
		if(Data.getFullVersion()){
	    	if(GameConstants.SELECTED_LEVEL == GameConstants.NUMBER_OF_LEVEL){
	    		hud.gameComplete();
		    	game.fullVersionWindow.alfaZero(0);	
				game.blackImage.setTouchable(Touchable.disabled);
				game.blackImage.setColor(	game.blackImage.getColor().r,
											game.blackImage.getColor().g,
											game.blackImage.getColor().b,
											game.blackImage.getColor().a += Gdx.graphics.getDeltaTime());
	    	}
	    	else{
	    		hud.win();
		    	game.fullVersionWindow.alfaZero(0);	
				game.blackImage.setTouchable(Touchable.disabled);
				game.blackImage.addAction(Actions.alpha(0));
	    	}
	    	
		}
		else{
			if(GameConstants.SELECTED_LEVEL == GameConstants.FREE_LEVELS){
				GameConstants.GAME_PAUSED = true;
				game.fullVersionWindow.alfaOne(0.2f);	
				game.blackImage.setTouchable(Touchable.enabled);
				game.blackImage.addAction(Actions.alpha(0.7f, 0.2f));
			}
	    	else
	    		hud.win();
		}
    	
		if(!GameConstants.UPDATE_STATE){
			GameConstants.UPDATE_STATE = true;
	    	game.levelHandler.setLevelUnlocked(GameConstants.SELECTED_LEVEL + 1);
	    	game.levelHandler.setUpgrades(GameConstants.SELECTED_LEVEL);
	    	Data.setUpgradePoint(Data.getUpgradePoint() + GameConstants.UPGRADE_POINT);
			GameConstants.INTERSTITIAL_TRIGGER--;
		}	
	}
	
	public void checkUpgrades(){
		game.levelHandler.checkUpgrades(GameConstants.SELECTED_LEVEL);
	}
	
	public void cameraActivity(){
		camera.displacement(mapReader.hero, mapReader, tiledMap);
        camera.update();    
	}

	@Override
	public void dispose() {
		GameConstants.TUTORIAL = false;
		//Stop sounds
		backgroundSound.stop();
		mapReader.stopSound();
		
		stage.dispose();
		backgroundSound.dispose();
		rayHandler.dispose();
		mapReader.dispose();
		world.dispose();
		tiledMap.dispose();
		
		//System.gc();
		
		hud.dispose();
		camera.dispose();
	}
	
}

package com.cosmonaut.Utils;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Bodies.CheckPoint;
import com.cosmonaut.Bodies.Dialogue;
import com.cosmonaut.Bodies.Exit;
import com.cosmonaut.Bodies.Hero;
import com.cosmonaut.Bodies.ItemSwitch;
import com.cosmonaut.Bodies.Leak;
import com.cosmonaut.Bodies.Obstacle;
import com.cosmonaut.Bodies.ObstacleDoor;
import com.cosmonaut.Bodies.ObstacleGlass;
import com.cosmonaut.Bodies.ObstacleLight;
import com.cosmonaut.Bodies.ObstacleLightning;
import com.cosmonaut.Bodies.ObstacleMoving;
import com.cosmonaut.Bodies.ObstaclePiston;
import com.cosmonaut.Bodies.ObstacleRevolving;
import com.cosmonaut.Bodies.Polygone;
import com.cosmonaut.Bodies.Wall;
import com.cosmonaut.Items.FuelRefill;
import com.cosmonaut.Items.Gyrophare;
import com.cosmonaut.Items.Item;
import com.cosmonaut.Items.OxygenRefill;
import com.cosmonaut.Items.SoundTrigger;
import com.cosmonaut.Items.Upgrade;
import com.cosmonaut.Lights.LinearLight;
import com.cosmonaut.Lights.MyConeLight;
import com.cosmonaut.Lights.MyLight;

public class TiledMapReader {
	
	final MyGdxGame game;
    private MapObjects objects;
	public Hero hero;
	public Exit exit;
	public Vector2[] cameraPath;
	
	//Box2dLights
	public float ambiantLightMin, ambiantLightMax;
	public int flickerFactor;
	public boolean lightFlicker;
    
	public TiledMapReader(final MyGdxGame game, TiledMap tiledMap, World world, OrthographicCamera camera, RayHandler rayHandler){
		this.game = game;
		//Read the map properties
		/*
		Iterator<String> iter = tiledMap.getProperties().getKeys();
		while(iter.hasNext()){
			System.out.println("TILED MAP PROPERTIES KEY : " + iter.next());
		}
		*/
		GameConstants.PPT =  Integer.parseInt(tiledMap.getProperties().get("tileheight").toString());
		updateSizes();
	     
        GameConstants.LEVEL_PIXEL_WIDTH = ((float)(tiledMap.getProperties().get("width", Integer.class)*GameConstants.PPT))*GameConstants.MPP;
        GameConstants.LEVEL_PIXEL_HEIGHT = Float.parseFloat(tiledMap.getProperties().get("height").toString()) * GameConstants.PPT * GameConstants.MPP;	
		
		hero = new Hero(game, world, camera, tiledMap, rayHandler);	
		objects = tiledMap.getLayers().get("Objects").getObjects();
        cameraPath = new Vector2[0];
        
        //Box2DLights
        //Does the light flicker ?
        if(tiledMap.getProperties().get("Light") != null){
        	if(tiledMap.getProperties().get("Light").equals("Random"))
        		lightFlicker = true;
        	else
        		lightFlicker = false;
        }
    	else
    		lightFlicker = false;
        //Minimum intensity of the light
        if(tiledMap.getProperties().get("Ambiant Light Min") != null){
        	ambiantLightMin = Float.parseFloat(tiledMap.getProperties().get("Ambiant Light Min").toString());
        }
        else
        	ambiantLightMin = 0.4f;
        //Maximum intensity of the light
        if(tiledMap.getProperties().get("Ambiant Light Max") != null){
        	ambiantLightMax = Float.parseFloat(tiledMap.getProperties().get("Ambiant Light Max").toString());
        }
        else
        	ambiantLightMax = 0.9f;
        //Probability of flickering
        if(tiledMap.getProperties().get("Flicker Factor") != null){
        	flickerFactor = Integer.parseInt(tiledMap.getProperties().get("Flicker Factor").toString());
        }
        else
        	flickerFactor = 2;
        
        //Reading objects       
        for (RectangleMapObject rectangleObject : objects.getByType(RectangleMapObject.class)) {
        	if(rectangleObject.getProperties().get("type") != null){
        		//End of the level
        		if(rectangleObject.getProperties().get("type").equals("Exit")){
        			//exit = new Exit(game, world, camera, rectangleObject, rayHandler);
        			exit = game.pools.exitPool.obtain();
        			exit.init(world, camera, rectangleObject, rayHandler);
                    game.exits.add(exit);
        		}
        		//Light obstacles
        		else if(rectangleObject.getProperties().get("type").equals("Light")){
	            	//ObstacleLight obstacle = new ObstacleLight(game, world, camera, rectangleObject);
        			ObstacleLight obstacle = game.pools.obtainObstacleLight();
        			obstacle.init(world, camera, rectangleObject);
	                game.obstacles.add(obstacle);
            	}
        		//Doors
        		else if(rectangleObject.getProperties().get("type").equals("Door")){
	            	//ObstacleDoor obstacle = new ObstacleDoor(game, world, camera, rectangleObject);
	            	ObstacleDoor obstacle = game.pools.doorPool.obtain();
	            	obstacle.init(world, camera, rectangleObject);
	                game.obstacles.add(obstacle);
	                game.activableObstacles.add(obstacle);
            	}
            	//Pistons
            	else if(rectangleObject.getProperties().get("type").equals("Piston")){
	            	game.pistons.add(rectangleObject);
            	}
            	//Revolving obstacles
            	else if(rectangleObject.getProperties().get("type").equals("Revolving")){
	            	//ObstacleRevolving obstacle = new ObstacleRevolving(game, world, camera, rectangleObject);
	            	ObstacleRevolving obstacle = game.pools.revolvingObstaclePool.obtain();
	            	obstacle.init(world, camera, rectangleObject);
	                game.obstacles.add(obstacle);
	                game.activableObstacles.add(obstacle);
            	}
            	//Leaks
            	else if(rectangleObject.getProperties().get("type").equals("Leak")){
	            	//Leak leak = new Leak(game, world, camera, rectangleObject);
            		Leak leak = game.pools.obtainLeak();
            		leak.init(world, camera, rectangleObject);
	                game.leaks.add(leak);
	                game.obstaclesWithSound.add(leak);
            	}
            	//Lightning
            	else if(rectangleObject.getProperties().get("type").equals("Lightning")){
	            	//ObstacleLightning lightning = new ObstacleLightning(game, world, camera, rectangleObject);
            		ObstacleLightning lightning = game.pools.lightningPool.obtain();
            		lightning.init(world, camera, rectangleObject);
	            	game.lightnings.add(lightning);
	                game.activableObstacles.add(lightning);
            	}
            	//Glass
            	else if(rectangleObject.getProperties().get("type").equals("Glass")){
            		ObstacleGlass glass = new ObstacleGlass(game, world, camera, rectangleObject);
            		game.obstacles.add(glass);
            	}
            	//Dialogue
            	else if(rectangleObject.getProperties().get("type").equals("Dialogue")){
            		Dialogue dialogue = new Dialogue(game, world, camera, rectangleObject);
            		game.dialogues.add(dialogue);
            	}
            	//Checkpoint
            	else if(rectangleObject.getProperties().get("type").equals("Checkpoint")){
            		CheckPoint checkpoint = new CheckPoint(game, world, camera, rectangleObject);
            		game.checkpoints.add(checkpoint);
            	}
            	//SoundTrigger
            	else if(rectangleObject.getProperties().get("type").equals("SoundTrigger")){
            		SoundTrigger soundTrigger = new SoundTrigger(game, world, rectangleObject, rayHandler);
            		game.items.add(soundTrigger);
            	}
        		
        	}
        	else{
        		//Wall obstacle = new Wall(game, world, camera, rectangleObject, game.assets.get("Images/" + GameConstants.SCREEN_RESOLUTION + "/Images.pack", TextureAtlas.class));
        		Wall wall = game.pools.wallPool.obtain();
        		wall.init(world, camera, rectangleObject);
        		game.walls.add(wall);
        	}
        }

        //Création de polygones
        for(PolygonMapObject polygonObject : objects.getByType(PolygonMapObject.class)){
        	//Polygone polygone = new Polygone(game, world, camera, polygonObject);
        	Polygone polygone = game.pools.polygonePool.obtain();
        	polygone.init(world, camera, polygonObject);
        	game.polygones.add(polygone);
        }
        
        //Pistons creation
        for(int i = game.pistons.size - 1; i > -1; i--){
        	if(game.pistons.get(i).getProperties().get("Group") != null){
        		for(int j = 0; j < game.pistons.size; j++){
        			if(Integer.parseInt(game.pistons.get(i).getProperties().get("Group").toString()) == Integer.parseInt(game.pistons.get(j).getProperties().get("Group").toString()) &&
        					i != j){  				
        				//ObstaclePiston piston = new ObstaclePiston(game, world, camera, game.pistons.get(i), game.assets.get("Images/" + GameConstants.SCREEN_RESOLUTION + "/Images.pack", TextureAtlas.class), game.pistons.get(j));
        				ObstaclePiston piston = game.pools.obtainPiston();
        				piston.init(world, camera, game.pistons.get(i), game.assets.get("Images/" + GameConstants.SCREEN_RESOLUTION + "/Images.pack", TextureAtlas.class), game.pistons.get(j));
        				game.obstacles.add(piston);
    	                game.activableObstacles.add(piston);
    	                game.obstaclesWithSound.add(piston);
        				
        				game.pistons.removeIndex(i);
        				game.pistons.removeIndex(j);
        				i--;
        			}
        		}
        	}	
        	else
    			System.out.println("Piston creation failed");
        }
        
        //Moving obstacles/Camera introduction/Lights     
        for(PolylineMapObject polylineObject : objects.getByType(PolylineMapObject.class)){
        	if(polylineObject.getProperties().get("type") != null){
        		//Moving obstacles
        		if(polylineObject.getProperties().get("type").equals("Moving")){
		        	//ObstacleMoving obstacleMoving = new ObstacleMoving(game, world, camera, polylineObject);
		        	ObstacleMoving obstacleMoving = game.pools.obtainObstacleMoving();
		        	obstacleMoving.init(world, camera, polylineObject);
		        	game.obstacles.add(obstacleMoving); 	
		            game.activableObstacles.add(obstacleMoving);
        		}
        		//Lights
        		else if(polylineObject.getProperties().get("type").equals("LinearLight")){
        			if(polylineObject.getProperties().get("Association Number") != null){
        				for(Obstacle obstacle : game.obstacles){
		        			if(Integer.parseInt((String) polylineObject.getProperties().get("Association Number")) == obstacle.associationNumber){
		    		        	LinearLight linearLight = new LinearLight(polylineObject, rayHandler, obstacle);
		    		        	game.myLights.add(linearLight);
		        			}
		        		}
        			}
        			else{
    		        	LinearLight linearLight = new LinearLight(polylineObject, rayHandler);
    		        	game.myLights.add(linearLight);
        			}	        	
        		}
        		else if(polylineObject.getProperties().get("type").equals("ConeLight")){
        			if(polylineObject.getProperties().get("Association Number") != null){
        				for(Obstacle obstacle : game.obstacles){
		        			if(Integer.parseInt((String) polylineObject.getProperties().get("Association Number")) == obstacle.associationNumber){
		    		        	MyConeLight myConeLight = new MyConeLight(polylineObject, rayHandler, obstacle);
		    		        	game.myLights.add(myConeLight);
		        			}
		        		}
        			}
        			else{
        				MyConeLight myConeLight = new MyConeLight(polylineObject, rayHandler);
    		        	game.myLights.add(myConeLight);
        			}	        	
        		}
        		//Camera path
        		else if(polylineObject.getProperties().get("type").equals("Camera")){
        			cameraPath = new Vector2[polylineObject.getPolyline().getTransformedVertices().length/2];
        	    	for(int i = 0; i < cameraPath.length; i++){
        	    		cameraPath[i] = Pools.obtain(Vector2.class).set(polylineObject.getPolyline().getTransformedVertices()[i*2]*GameConstants.MPP, polylineObject.getPolyline().getTransformedVertices()[i*2 + 1]*GameConstants.MPP);
        	    	}   
        		}
        	}	
        }
              
        //Spawned items
        for(int i = 0; i < tiledMap.getLayers().get("Spawn").getObjects().getCount(); i++){
        	if(tiledMap.getLayers().get("Spawn").getObjects().get(i).getProperties().get("type") != null){	
        		//Switches
        		if(tiledMap.getLayers().get("Spawn").getObjects().get(i).getProperties().get("type").equals("Switch")){
        			//ItemSwitch itemSwitch = new ItemSwitch(game, world, camera, tiledMap.getLayers().get("Spawn").getObjects().get(i), rayHandler);
        			ItemSwitch itemSwitch = game.pools.switchPool.obtain();
        			itemSwitch.init(world, camera, tiledMap.getLayers().get("Spawn").getObjects().get(i), rayHandler);
        			game.switchs.add(itemSwitch);
        		}
        		//Oxygen Refill
        		else if(tiledMap.getLayers().get("Spawn").getObjects().get(i).getProperties().get("type").equals("Oxygen")){
        			//OxygenRefill oxygenRefill = new OxygenRefill(game, world, tiledMap.getLayers().get("Spawn").getObjects().get(i), rayHandler, hero);
        			OxygenRefill oxygenRefill = game.pools.oxygenPool.obtain();
        			oxygenRefill.init(world, tiledMap.getLayers().get("Spawn").getObjects().get(i), rayHandler, hero);
        			game.items.add(oxygenRefill);
        		}
        		//Fuel Refill
        		else if(tiledMap.getLayers().get("Spawn").getObjects().get(i).getProperties().get("type").equals("Fuel")){
        			//FuelRefill fuelRefill = new FuelRefill(game, world, tiledMap.getLayers().get("Spawn").getObjects().get(i), rayHandler, hero);
        			FuelRefill fuelRefill = game.pools.fuelPool.obtain();
        			fuelRefill.init(world, tiledMap.getLayers().get("Spawn").getObjects().get(i), rayHandler, hero);
        			game.items.add(fuelRefill);
        		}
        		//Gyrophare
        		else if(tiledMap.getLayers().get("Spawn").getObjects().get(i).getProperties().get("type").equals("Gyrophare")){
        			//Gyrophare gyrophare = new Gyrophare(game, tiledMap.getLayers().get("Spawn").getObjects().get(i), rayHandler);
        			Gyrophare gyrophare = game.pools.gyropharePool.obtain();
        			gyrophare.init(tiledMap.getLayers().get("Spawn").getObjects().get(i), rayHandler);
        			game.gyrophares.add(gyrophare);
        		}
        		//Upgrade
        		else if(tiledMap.getLayers().get("Spawn").getObjects().get(i).getProperties().get("type").equals("Upgrade")){
        			if(Integer.parseInt((String)tiledMap.getLayers().get("Spawn").getObjects().get(i).getProperties().get("ID")) == 1 && !GameConstants.UPGRADE_1){
	        			//Upgrade upgrade = new Upgrade(game, world, tiledMap.getLayers().get("Spawn").getObjects().get(i), rayHandler);
        				Upgrade upgrade = game.pools.upgradePool.obtain();
        				upgrade.init(world, tiledMap.getLayers().get("Spawn").getObjects().get(i), rayHandler);
	        			game.items.add(upgrade);
        			}
        			else if(Integer.parseInt((String)tiledMap.getLayers().get("Spawn").getObjects().get(i).getProperties().get("ID")) == 2 && !GameConstants.UPGRADE_2){
	        			//Upgrade upgrade = new Upgrade(game, world, tiledMap.getLayers().get("Spawn").getObjects().get(i), rayHandler);
	        			Upgrade upgrade = game.pools.upgradePool.obtain();
        				upgrade.init(world, tiledMap.getLayers().get("Spawn").getObjects().get(i), rayHandler);
	        			game.items.add(upgrade);
        			}
        			else if(Integer.parseInt((String)tiledMap.getLayers().get("Spawn").getObjects().get(i).getProperties().get("ID")) == 3 && !GameConstants.UPGRADE_3){
	        			//Upgrade upgrade = new Upgrade(game, world, tiledMap.getLayers().get("Spawn").getObjects().get(i), rayHandler);
        				Upgrade upgrade = game.pools.upgradePool.obtain();
        				upgrade.init(world, tiledMap.getLayers().get("Spawn").getObjects().get(i), rayHandler);
	        			game.items.add(upgrade);
        			}
        		}
        	}
        }    
        
        //Obstacle organization
	    for(int i = game.obstacles.size - 1; i > -1; i--){
	    	if(game.obstacles.get(i).getClass().toString().equals("class com.libgdx.jam.Bodies.ObstaclePiston")){
	    		game.obstaclesWithNinePatch.add(game.obstacles.get(i));
	    		game.obstacles.removeIndex(game.obstacles.indexOf(game.obstacles.get(i), true));
	    	}
	    }

	}
	
	public void active(){
		if(!GameConstants.LEVEL_INTRO && !GameConstants.TUTORIAL)
			hero.displacement();

        for(Leak leak : game.leaks)
        	leak.active(hero);		
        for(Obstacle obstacle : game.obstacles)
        	obstacle.active(hero);
        for(Obstacle obstacle : game.obstaclesWithNinePatch)
        	obstacle.active(hero);       
        for(Item item : game.items)
        	item.active(this);              
        for(Exit exit : game.exits)
        	exit.active();
		for(Gyrophare gyrophare : game.gyrophares)
			gyrophare.active();
		for(ObstacleLightning lightning : game.lightnings)
			lightning.active(hero);
		for(Dialogue dialogue : game.dialogues)
			dialogue.active(hero);
        for(MyLight myLight : game.myLights)
        	myLight.active();
	}
	
	public void draw(SpriteBatch batch, TextureAtlas textureAtlas, float animTime){ 
		for(Gyrophare gyrophare : game.gyrophares)
			gyrophare.draw(batch, textureAtlas);   
        for(Exit exit : game.exits)
        	exit.draw(batch);
		for(ItemSwitch itemSwitch : game.switchs)
			itemSwitch.draw(batch, textureAtlas);
        for(Item item : game.items)
        	item.draw(batch, textureAtlas);   
        for(MyLight myLight : game.myLights)
        	myLight.draw(batch, textureAtlas);  
		hero.draw(batch, GameConstants.ANIM_TIME); 
		for(Obstacle obstacle : game.obstacles)
			obstacle.draw(batch, textureAtlas);
		for(Obstacle obstacle : game.obstaclesWithNinePatch)
			obstacle.draw(batch);
		for(Leak leak : game.leaks)
			leak.draw(batch, animTime);
	}
	
	public void drawLightnings(SpriteBatch batch, TextureAtlas textureAtlas){ 
		for(ObstacleLightning lightning : game.lightnings)
			lightning.draw(batch, textureAtlas);
	}
	
	public void soundPause(){
		hero.soundPause();
        for(Leak leak : game.leaks)
        	leak.soundPause();		
		for(Obstacle obstacle : game.obstacles)
			obstacle.soundPause();
		for(Obstacle obstacle : game.obstaclesWithNinePatch)
			obstacle.soundPause();
	}
	
	public void soundResume(){
		hero.soundResume();
        for(Leak leak : game.leaks)
        	leak.soundResume();	
		for(Obstacle obstacle : game.obstacles)
			obstacle.soundResume();
		for(Obstacle obstacle : game.obstaclesWithNinePatch)
			obstacle.soundResume();
	}
	
	public void dispose(){
		exit.dispose();
		hero.dispose();
		
		for(Wall wall : game.walls)
			wall.dispose();
		
		for(ItemSwitch itemSwitch : game.switchs)
			itemSwitch.dispose();
		
		for(Item item : game.items)
			item.dispose();
		
		for(Leak leak : game.leaks)
			leak.dispose();
		
		for(Polygone polygone : game.polygones)
			polygone.dispose();
		
		for(ObstacleLightning lightning : game.lightnings)
			lightning.dispose();
		
		for(Obstacle obstacle : game.obstacles)
			obstacle.dispose();
		
		for(Gyrophare gyrophare : game.gyrophares)
			gyrophare.dispose();	

        for(MyLight myLight : game.myLights)
        	myLight.dispose();
		
		game.clearArrays();
		
		/*
		 * Camera
		 */
		for(int i = 0; i < cameraPath.length; i++){
    		Pools.free(cameraPath[i]); 
    	} 
	}
	
	public void stopSound(){
        for(Leak leak : game.leaks)
        	leak.sound.stop();
	}

	public void updateSizes(){
		GameConstants.MPT = GameConstants.PPT*GameConstants.MPP;	
		GameConstants.SCREEN_WIDTH = GameConstants.MPP * GameConstants.NB_HORIZONTAL_TILE * GameConstants.PPT;
		GameConstants.HERO_HEIGHT = 0.66f *  GameConstants.PPT * GameConstants.MPP / 2;
	}
}

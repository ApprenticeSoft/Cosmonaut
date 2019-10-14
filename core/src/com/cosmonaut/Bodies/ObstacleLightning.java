package com.cosmonaut.Bodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;

public class ObstacleLightning extends Obstacle{
	
	private Vector2[] lightning1Portions, lightning2Portions;
	private Vector2 extremityA, extremityB, lightningCenter;
	private int portions1, portions2, portionsMin, fixtureCount;
	private float lastLightningTime, cooldownTime, activeTime;
	private float alpha = 1f, cooldown, activityDuration, delay = 0, dist, rad;
	private boolean strike = false;
	private World world;
	private static float lightningThickness = 0.001f*GameConstants.PPT;
	
	private String[] cooldowns;
	
	//Test optimisation
	private PolygonShape newPolygonShape;
	private FixtureDef newFixtureDef;
	private Vector2 distanceLightning;
	OrthographicCamera camera;

	public ObstacleLightning(MyGdxGame game, World world, OrthographicCamera camera, MapObject rectangleObject) {
		super(game, world, camera, rectangleObject);
		this.world = world;
		this.camera = camera;
		categoryBits = 0010;
		stringTextureRegion = "WhiteSquare";
		newPolygonShape = Pools.obtain(PolygonShape.class);
		newFixtureDef = Pools.obtain(FixtureDef.class);
		
		create(world, camera, rectangleObject);
		body.getFixtureList().get(0).setSensor(true);
		body.getFixtureList().get(0).setUserData("Lightning");
		body.setUserData("Lightning");
		
		extremityA = Pools.obtain(Vector2.class).set((float)(body.getPosition().x - width * MathUtils.cos(angle)), (float)(-width * MathUtils.sin(angle) + body.getPosition().y));
		extremityB = Pools.obtain(Vector2.class).set((float)(body.getPosition().x + width * MathUtils.cos(angle)), (float)(width * MathUtils.sin(angle) + body.getPosition().y));
		distanceLightning = Pools.obtain(Vector2.class).set(camera.position.x, camera.position.y).sub(extremityA);
		
		portionsMin = (int)(1.8f*width*GameConstants.PPM/GameConstants.PPT);
		if(width <= GameConstants.MPT)
			portionsMin = (int)(2.7f*portionsMin);
		else if(width > 3*GameConstants.MPT)
			portionsMin = (int)(0.5f*portionsMin);
		if(portionsMin < 2) portionsMin = 2;
		
		//Lightning cooldown
		if(rectangleObject.getProperties().get("Cooldown") != null){
			cooldowns = rectangleObject.getProperties().get("Cooldown").toString().split(",");
			
			cooldown = Float.valueOf(cooldowns[0]);		
			if(cooldowns.length > 1)
				activityDuration = Float.valueOf(cooldowns[1]);		
		}
		else cooldown = 0;
		
		//Delay before activation
		if(rectangleObject.getProperties().get("Delay") != null){
			delay = Float.parseFloat((String) rectangleObject.getProperties().get("Delay"));
		}
		else delay = 0;
		
		//Is the lightning active ?
		if(rectangleObject.getProperties().get("On") != null){
			active = Boolean.parseBoolean((String) rectangleObject.getProperties().get("On"));
		}
		else
			active = true;
		
		//1st bolt
		portions1 = MathUtils.random(portionsMin, (int)(1.4f*portionsMin));
		lightning1Portions = new Vector2[portions1+1];
		for(int i = 0; i < portions1 + 1; i++)
			lightning1Portions[i] = Pools.obtain(Vector2.class);
		
		lightning1Portions[0].set(extremityA);
		lightning1Portions[portions1].set(extremityB);
		
		//2nd bolt
		portions2 = MathUtils.random((int)(1.1f*portionsMin), (int)(1.9f*portionsMin));
		lightning2Portions = new Vector2[portions2+1];
		for(int i = 0; i < portions2 + 1; i++)
			lightning2Portions[i] = Pools.obtain(Vector2.class);
		
		lightning2Portions[0].set(extremityA);
		lightning2Portions[portions2].set(extremityB);
		
		lightningCenter = Pools.obtain(Vector2.class);
				
		randomize(world);
	}

	public ObstacleLightning(MyGdxGame game){
		super(game);
		categoryBits = 0010;
		stringTextureRegion = "WhiteSquare";
		newPolygonShape = Pools.obtain(PolygonShape.class);
		newFixtureDef = Pools.obtain(FixtureDef.class);
		
	}
	
	public void init(World world, OrthographicCamera camera, MapObject rectangleObject){
		this.camera = camera;
		categoryBits = 0010;
		lastLightningTime = 0;
		cooldownTime = 0;
		activeTime = 0;
		super.init(world, camera, rectangleObject);
		
		body.getFixtureList().get(0).setSensor(true);
		body.getFixtureList().get(0).setUserData("Lightning");
		body.setUserData("Lightning");
		
		extremityA = Pools.obtain(Vector2.class).set((float)(body.getPosition().x - width * MathUtils.cos(angle)), (float)(-width * MathUtils.sin(angle) + body.getPosition().y));
		extremityB = Pools.obtain(Vector2.class).set((float)(body.getPosition().x + width * MathUtils.cos(angle)), (float)(width * MathUtils.sin(angle) + body.getPosition().y));
		distanceLightning = Pools.obtain(Vector2.class).set(camera.position.x, camera.position.y).sub(posX, posY);
		
		portionsMin = (int)(1.8f*width*GameConstants.PPM/GameConstants.PPT);
		if(width <= GameConstants.MPT)
			portionsMin = (int)(2.7f*portionsMin);
		else if(width > 3*GameConstants.MPT)
			portionsMin = (int)(0.5f*portionsMin);
		if(portionsMin < 2) portionsMin = 2;
		
		//Lightning cooldown
		if(rectangleObject.getProperties().get("Cooldown") != null){
			cooldowns = rectangleObject.getProperties().get("Cooldown").toString().split(",");
			
			cooldown = Float.valueOf(cooldowns[0]);		
			if(cooldowns.length > 1)
				activityDuration = Float.valueOf(cooldowns[1]);		
		}
		else cooldown = 0;
		
		//Delay before activation
		if(rectangleObject.getProperties().get("Delay") != null){
			delay = Float.parseFloat((String) rectangleObject.getProperties().get("Delay"));
		}
		else delay = 0;
		
		//Is the lightning active ?
		if(rectangleObject.getProperties().get("On") != null){
			active = Boolean.parseBoolean((String) rectangleObject.getProperties().get("On"));
		}
		else
			active = true;
		
		//1st bolt
		portions1 = MathUtils.random(portionsMin, (int)(1.4f*portionsMin));
		lightning1Portions = new Vector2[portions1+1];
		for(int i = 0; i < portions1 + 1; i++)
			lightning1Portions[i] = Pools.obtain(Vector2.class);
		
		lightning1Portions[0].set(extremityA);
		lightning1Portions[portions1].set(extremityB);
		
		//2nd bolt
		portions2 = MathUtils.random((int)(1.1f*portionsMin), (int)(1.9f*portionsMin));
		lightning2Portions = new Vector2[portions2+1];
		for(int i = 0; i < portions2 + 1; i++)
			lightning2Portions[i] = Pools.obtain(Vector2.class);
		
		lightning2Portions[0].set(extremityA);
		lightning2Portions[portions2].set(extremityB);
		
		lightningCenter = Pools.obtain(Vector2.class);
				
		randomize(world);
	}
	
	@Override
	public void draw(SpriteBatch batch, TextureAtlas textureAtlas){		
		//1st bolt
		for(int i = 0; i < portions1; i++){
			dist = (float)Math.sqrt((lightning1Portions[i+1].x - lightning1Portions[i].x)*(lightning1Portions[i+1].x - lightning1Portions[i].x) + (lightning1Portions[i+1].y - lightning1Portions[i].y)*(lightning1Portions[i+1].y - lightning1Portions[i].y));
		    rad = (float)MathUtils.atan2((lightning1Portions[i+1].y - lightning1Portions[i].y), (lightning1Portions[i+1].x - lightning1Portions[i].x));
		    
		    batch.setColor(14f/255f, MathUtils.random(81, 144)/255f, MathUtils.random(156, 249)/255f, alpha);
		    batch.draw(	textureAtlas.findRegion(stringTextureRegion), 
			    		lightning1Portions[i].x, 
			    		lightning1Portions[i].y - lightningThickness/2,
			    		0,
			    		0,
						1.05f*dist,
						lightningThickness,
						1,
						1,
						rad*MathUtils.radiansToDegrees);
		}
		
		//2nd bolt
		for(int i = 0; i < portions2; i++){
			dist = (float)Math.sqrt((lightning2Portions[i+1].x - lightning2Portions[i].x)*(lightning2Portions[i+1].x - lightning2Portions[i].x) + (lightning2Portions[i+1].y - lightning2Portions[i].y)*(lightning2Portions[i+1].y - lightning2Portions[i].y));
		    rad = (float)MathUtils.atan2((lightning2Portions[i+1].y - lightning2Portions[i].y), (lightning2Portions[i+1].x - lightning2Portions[i].x));
		    
		    batch.setColor(14f/255f, MathUtils.random(80, 145)/255f, MathUtils.random(155, 250)/255f, alpha);
		    batch.draw(	textureAtlas.findRegion(stringTextureRegion), 
			    		lightning2Portions[i].x, 
			    		lightning2Portions[i].y - lightningThickness/2,
			    		0,
						0,
						1.05f*dist,
						lightningThickness,
						1,
						1,
						rad*MathUtils.radiansToDegrees);
		}
		
		batch.setColor(1, 1, 1, 1);

	}
	
	@Override
	public void active(Hero hero){
		//distanceA = Pools.obtain(Vector2.class).set(camera.position.x, camera.position.y).sub(extremityA);
		distanceLightning = Pools.obtain(Vector2.class).set(camera.position.x, camera.position.y).sub(body.getPosition());
		if(active)
			animate();	
		else
			alpha = 0;	
		
		if(alpha < 0.1f)
			body.setActive(false);
		else
			body.setActive(true);
	}
	
	public void randomize(World world){
		//Destroy fixture from the previous lightnings
		body.setActive(false);
		fixtureCount = body.getFixtureList().size;
		for(int i = 0; i<fixtureCount; i++)
		    body.destroyFixture(body.getFixtureList().get(0));		

		//Define the different portions of the 1st bolt
		for(int i = 1; i < portions1; i++)
			lightning1Portions[i].set(	extremityA.x + (extremityB.x - extremityA.x)*i/portions1 + MathUtils.random(-0.8f*height, 0.8f*height), 
										extremityA.y + (extremityB.y - extremityA.y)*i/portions1 + MathUtils.random(-0.8f*height, 0.8f*height));		
			
		//Create the fixtures for the 1st bolt
		for(int i = 0; i < portions1; i++){	
			dist = (float)Math.sqrt((lightning1Portions[i+1].x - lightning1Portions[i].x)*(lightning1Portions[i+1].x - lightning1Portions[i].x) + (lightning1Portions[i+1].y - lightning1Portions[i].y)*(lightning1Portions[i+1].y - lightning1Portions[i].y));
		    rad = (float)MathUtils.atan2((lightning1Portions[i+1].y - lightning1Portions[i].y), (lightning1Portions[i+1].x - lightning1Portions[i].x));
		    		
		    newPolygonShape.setAsBox(	dist/2,
	        							lightningThickness, 
	        							lightningCenter.set(	(float)(((lightning1Portions[i+1].x + lightning1Portions[i].x)/2 - body.getPosition().x) * MathUtils.cos(-angle) - ((lightning1Portions[i+1].y + lightning1Portions[i].y)/2 - body.getPosition().y)* MathUtils.sin(-angle)),
	        													(float)(((lightning1Portions[i+1].x + lightning1Portions[i].x)/2 - body.getPosition().x) * MathUtils.sin(-angle) + ((lightning1Portions[i+1].y + lightning1Portions[i].y)/2 - body.getPosition().y)* MathUtils.cos(-angle))),
	        							rad - angle);
	        newFixtureDef.shape = newPolygonShape;
	        newFixtureDef.isSensor = true;
			newFixtureDef.filter.categoryBits = (short) 0010;
	        body.createFixture(newFixtureDef).setUserData("Lightning");
	        body.setUserData("Lightning");
		}

		//Define the different portions of the 2nd bolt
		for(int i = 1; i < portions2; i++)
			lightning2Portions[i].set(	extremityA.x + (extremityB.x - extremityA.x)*i/portions2 + MathUtils.random(-0.8f*height, 0.8f*height), 
													extremityA.y + (extremityB.y - extremityA.y)*i/portions2 + MathUtils.random(-0.8f*height, 0.8f*height));
		
		//Create the fixtures for the 2nd bolt
		for(int i = 0; i < portions2; i++){
			dist = (float)Math.sqrt((lightning2Portions[i+1].x - lightning2Portions[i].x)*(lightning2Portions[i+1].x - lightning2Portions[i].x) + (lightning2Portions[i+1].y - lightning2Portions[i].y)*(lightning2Portions[i+1].y - lightning2Portions[i].y));
		   	rad = (float)MathUtils.atan2((lightning2Portions[i+1].y - lightning2Portions[i].y), (lightning2Portions[i+1].x - lightning2Portions[i].x));
		    	
		   	newPolygonShape.setAsBox(	dist/2,
	        							lightningThickness, 
	        							lightningCenter.set((float)(((lightning2Portions[i+1].x + lightning2Portions[i].x)/2 - body.getPosition().x) * MathUtils.cos(-angle) - ((lightning2Portions[i+1].y + lightning2Portions[i].y)/2 - body.getPosition().y)* MathUtils.sin(-angle)),
	        										(float)(((lightning2Portions[i+1].x + lightning2Portions[i].x)/2 - body.getPosition().x) * MathUtils.sin(-angle) + ((lightning2Portions[i+1].y + lightning2Portions[i].y)/2 - body.getPosition().y)* MathUtils.cos(-angle))),
	        							rad - angle);
	        newFixtureDef.shape = newPolygonShape;
	        newFixtureDef.isSensor = true;
	        body.createFixture(newFixtureDef).setUserData("Lightning");
	        body.setUserData("Lightning");
		}
	}
	
	public void animate(){
		if (cooldown == 0){
			if(GameConstants.LEVEL_TIME - lastLightningTime > 0.12f){
				lastLightningTime = GameConstants.LEVEL_TIME;
				if(distanceLightning.len() < width + camera.viewportWidth)
					randomize(world);
				alpha = 1;
			}
		}
		else if(cooldowns.length == 1){
			if(delay > 0){
				delay -= Gdx.graphics.getDeltaTime();
			}
			else if(GameConstants.LEVEL_TIME - lastLightningTime > cooldown){
				if(!strike){
					if(distanceLightning.len() < width + camera.viewportWidth)
						randomize(world);
					alpha = 1;
					strike = true;
				}
				else {
					alpha -= 1.4f * Gdx.graphics.getDeltaTime();
					
					if(alpha < 0){
						alpha =0;
						strike = false;
						lastLightningTime = GameConstants.LEVEL_TIME;
					}
				}
			}
		}
		else if(cooldowns.length == 2){
			if(delay > 0){
				delay -= Gdx.graphics.getDeltaTime();
			}
			else if(GameConstants.LEVEL_TIME - activeTime > (cooldown + activityDuration)){
				if(!strike){
					if(distanceLightning.len() < width + camera.viewportWidth)
						randomize(world);
					alpha = 1;
					strike = true;
				}
				else {
					alpha -= 1.4f * Gdx.graphics.getDeltaTime();
					
					if(alpha < 0){
						alpha = 0;
						cooldownTime = GameConstants.LEVEL_TIME;
						activeTime = GameConstants.LEVEL_TIME;
						strike = false;
						lastLightningTime = GameConstants.LEVEL_TIME;
					}
				}
			}
			else if(GameConstants.LEVEL_TIME - cooldownTime > cooldown ){
				alpha = 1;
				if(GameConstants.LEVEL_TIME - lastLightningTime > 0.12f){
					lastLightningTime = GameConstants.LEVEL_TIME;
					if(distanceLightning.len() < width + camera.viewportWidth)
						randomize(world);
				}
			}
		}		
	}
	
	@Override
	public void activate(){
		active = !active;
	}

	@Override
	public void dispose(){
		game.pools.free(this);
		Pools.free(extremityA);
		Pools.free(extremityB);
		Pools.free(lightningCenter);
		for(int i = 0; i < lightning1Portions.length; i++)
			Pools.free(lightning1Portions[i]);		
		for(int i = 0; i < lightning2Portions.length; i++)
			Pools.free(lightning2Portions[i]);
		
	}

}

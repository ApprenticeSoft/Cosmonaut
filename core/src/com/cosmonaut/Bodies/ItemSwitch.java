package com.cosmonaut.Bodies;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;

public class ItemSwitch {

	final MyGdxGame game;
	public Body switchBody;
	private BodyDef bodyDef;
	private FixtureDef fixtureDef;
	private PolygonShape switchShape;
	private float width, height;
	private boolean isOn;
	private String[] associationNumbers;
	private Sound soundOn, soundOff;
	private PointLight light;
	private float cooldown;
	
	public ItemSwitch(final MyGdxGame game, World world,  OrthographicCamera camera, MapObject mapObject, RayHandler rayHandler){
		this.game = game;
		soundOn = game.assets.get("Sounds/Button On.ogg", Sound.class);
		soundOff = game.assets.get("Sounds/Button Off.ogg", Sound.class);
		cooldown = 0;
		
		create(world, camera, mapObject);  
		
		Color color = Pools.obtain(Color.class).set(1, 0, 0, 0.6f);
		light = new PointLight(rayHandler, 40, color, 5, 0, 0);
        light.attachToBody(switchBody, 0, 0);  
        light.setXray(true);
        Pools.free(color);
	}
	
	public ItemSwitch(final MyGdxGame game){
		this.game = game;
		soundOn = game.assets.get("Sounds/Button On.ogg", Sound.class);
		soundOff = game.assets.get("Sounds/Button Off.ogg", Sound.class);
	}
	
	public void init(World world,  OrthographicCamera camera, MapObject mapObject, RayHandler rayHandler){
		cooldown = 0;
		
		create(world, camera, mapObject); 
		
		Color color = Pools.obtain(Color.class).set(1, 0, 0, 0.6f);
		light = new PointLight(rayHandler, 40, color, 5, 0, 0);
        light.attachToBody(switchBody, 0, 0);  
        light.setXray(true);
        Pools.free(color);
	}
	
	public void create(/*final MyGdxGame game,*/ World world,  OrthographicCamera camera, MapObject mapObject){
		
		//Is the switch on ?
		if(mapObject.getProperties().get("On") != null){
			isOn = Boolean.parseBoolean((String) mapObject.getProperties().get("On"));
		}
		else
			isOn = false;
		
		//Association Numbers
		if(mapObject.getProperties().get("Association Number") != null){
			associationNumbers = mapObject.getProperties().get("Association Number").toString().split(",");
		}
		
		width = 0.25f * mapObject.getProperties().get("width", float.class)/2 * GameConstants.MPP;
		height = 0.25f * mapObject.getProperties().get("height", float.class)/2 * GameConstants.MPP;

		bodyDef = Pools.obtain(BodyDef.class);
		fixtureDef = Pools.obtain(FixtureDef.class);
		switchShape = Pools.obtain(PolygonShape.class);	
		
		bodyDef.type = BodyType.StaticBody;

		bodyDef.position.set((mapObject.getProperties().get("x", float.class) + mapObject.getProperties().get("width", float.class)/2) * GameConstants.MPP,
							(mapObject.getProperties().get("y", float.class) + 1.5f*mapObject.getProperties().get("height", float.class)) * GameConstants.MPP);
		
		switchShape.setAsBox(width, height);
		
		fixtureDef.shape = switchShape;
		fixtureDef.density = 0;  
        fixtureDef.friction = 0.2f;  
        fixtureDef.restitution = 0f;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = (short) 0010;
		
        switchBody = world.createBody(bodyDef);
        switchBody.createFixture(fixtureDef).setUserData("Switch");
        switchBody.setUserData("Switch"); 
        
        Pools.free(bodyDef);
        Pools.free(fixtureDef);
        Pools.free(switchShape);
	}
	
	public void active(Array<Obstacle> obstacles){	
		if(cooldown <= 0){
			cooldown = 2f;
			
			if(isOn){
				soundOff.play();
				light.setColor(1, 0, 0, 0.6f);
			}
			else{
				soundOn.play();
				light.setColor(0, 1, 0, 0.6f);
			}
			
			isOn = !isOn;
			
			for(String number : associationNumbers){
				for(Obstacle obstacle : obstacles)
					if(obstacle.associationNumber == Integer.valueOf(number))
						obstacle.activate();
			}
		}
	}
	
	public void draw(SpriteBatch batch, TextureAtlas textureAtlas){
		cooldown -= Gdx.graphics.getDeltaTime();
		
		batch.setColor(1, 1, 1, 1);
		if(isOn){
			batch.draw(	textureAtlas.findRegion("SwitchOn"),
						switchBody.getPosition().x - width, 
						switchBody.getPosition().y - height,
						2 * width,
						2 * height); 
		}
		else{
			batch.draw(	textureAtlas.findRegion("SwitchOff"),
						switchBody.getPosition().x - width, 
						switchBody.getPosition().y - height,
						2 * width,
						2 * height); 
		}
	}
	
	public void dispose(){
		game.pools.free(this);
	}
}

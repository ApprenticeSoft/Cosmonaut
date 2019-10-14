package com.cosmonaut.Items;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;
import com.cosmonaut.Utils.TiledMapReader;

public class Item {
	
	final MyGdxGame game;
	protected static World world;
	public Body body;
	protected BodyDef bodyDef;
	protected FixtureDef fixtureDef;
	protected PolygonShape polygonShape;
	protected float width, height, ratio = 19/50f;
	public boolean used;
	protected String stringTextureRegion;
	
	//Sound
	protected Sound sound;
	
	//Box2DLights
	protected short categoryBits = 0010;
	protected PointLight light;
	protected Color lightColor;
	
	public Item(final MyGdxGame game){	
		this.game = game;
        lightColor = Pools.obtain(Color.class).set(1,1,1,1);
	}
	
	public void create(World world, MapObject mapObject, RayHandler rayHandler){
		this.world = world;
		used = false;
		
		height = mapObject.getProperties().get("height", float.class)/2 * GameConstants.MPP/3;
		width = ratio * height;
		
		polygonShape = Pools.obtain(PolygonShape.class);
		bodyDef = Pools.obtain(BodyDef.class);	
		fixtureDef = Pools.obtain(FixtureDef.class);
		
		bodyDef.type = BodyType.DynamicBody;

		bodyDef.position.set((mapObject.getProperties().get("x", float.class) + mapObject.getProperties().get("width", float.class)/2) * GameConstants.MPP,
							(mapObject.getProperties().get("y", float.class) + 1.5f*mapObject.getProperties().get("height", float.class)) * GameConstants.MPP);
		
		polygonShape.setAsBox(width, height);
		
		fixtureDef.shape = polygonShape;
		fixtureDef.density = 0.1f;  
        fixtureDef.friction = 0.2f;  
        fixtureDef.restitution = 0.05f;
        fixtureDef.isSensor = false;
        fixtureDef.filter.groupIndex = (short) 0000;
        fixtureDef.filter.categoryBits = categoryBits;
		
        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData("Item");
        body.setUserData("Item"); 
        
        //Light
        light = new PointLight(rayHandler, 30, lightColor, 3.6f*height, 0, 0);
        light.attachToBody(body, 0, 0);
        //Pools.free(lightColor);
        
        //Impulse
		if(mapObject.getProperties().get("Impulse") != null){
			Vector2 impulseForce = Pools.obtain(Vector2.class).set(	MathUtils.random(-5, 5) * body.getFixtureList().get(0).getDensity(), 
																	MathUtils.random(-5, 5) * body.getFixtureList().get(0).getDensity());
			Vector2 impulseCenter = Pools.obtain(Vector2.class).set(body.getPosition().x + MathUtils.random(-0.9f * width, 0.9f * width), 
																	body.getPosition().y + MathUtils.random(-0.9f * height, 0.9f * height));

			if(Boolean.parseBoolean((String) mapObject.getProperties().get("Impulse")))
				body.applyLinearImpulse(impulseForce, impulseCenter, true);
			
			Pools.free(impulseForce);
			Pools.free(impulseCenter);
		}	

        Pools.free(bodyDef);
        Pools.free(fixtureDef);
        Pools.free(polygonShape);
	}
	
	public void init(World world, MapObject mapObject, RayHandler rayHandler){
		create(world, mapObject, rayHandler);
	}
	
	public void activate(){
		//Called when Major Tom collides with the item
	}
	
	public void active(TiledMapReader tiledMapReader){
		if(used)
			destroy(tiledMapReader);
	}
	
	public void draw(SpriteBatch batch, TextureAtlas textureAtlas){		
		batch.setColor(1, 1, 1, 1);
		batch.draw(	textureAtlas.findRegion(stringTextureRegion), 
					body.getPosition().x - width, 
					body.getPosition().y - height,
					width,
					height,
					2 * width,
					2 * height,
					1,
					1,
					body.getAngle()*MathUtils.radiansToDegrees);
	}
	
	public void destroy(TiledMapReader tiledMapReader){
		body.setActive(false);
		world.destroyBody(body);
		game.items.removeIndex(game.items.indexOf(this, true));
		dispose();
	}
	
	public float getX(){
		return body.getPosition().x;
	}
	
	public float getY(){
		return body.getPosition().y;
	}
	
	public void dispose(){
		
	}
}

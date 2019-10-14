package com.cosmonaut.Bodies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.MyGdxGame;

public class ObstacleLight extends Obstacle{

	public ObstacleLight(final MyGdxGame game, World world, OrthographicCamera camera, MapObject rectangleObject) {
		super(game, world, camera, rectangleObject);
		
		restitution = 0.42f;
		
		create(world, camera, rectangleObject);
		
		body.setUserData("ObstacleLight");
		body.getFixtureList().get(0).setUserData("ObstacleLight");
		
		//Weight
		if(rectangleObject.getProperties().get("Weight") != null){
			body.getFixtureList().get(0).setDensity(
					body.getFixtureList().get(0).getDensity() * Float.parseFloat(rectangleObject.getProperties().get("Weight").toString())
			);
			body.resetMassData();
		}
		
		//Texture
		if(rectangleObject.getProperties().get("Texture") != null){
			stringTextureRegion = rectangleObject.getProperties().get("Texture").toString();
		}
		else
			stringTextureRegion = "CrateSquare";
		
		
		//Impulse	
		if(rectangleObject.getProperties().get("Impulse") != null){		
			Vector2 impulseForce = Pools.obtain(Vector2.class).set(	MathUtils.random(-15, 15) * body.getFixtureList().get(0).getDensity(), 
																	MathUtils.random(-15, 15) * body.getFixtureList().get(0).getDensity());
			Vector2 impulseCenter = Pools.obtain(Vector2.class).set(body.getPosition().x + MathUtils.random(-0.9f * width, 0.9f * width), 
																	body.getPosition().y + MathUtils.random(-0.9f * height, 0.9f * height));
			
			if(Boolean.parseBoolean((String) rectangleObject.getProperties().get("Impulse")))
				body.applyLinearImpulse(impulseForce, impulseCenter, true);
			
			Pools.free(impulseForce);
			Pools.free(impulseCenter);
		}
	}
	
	public ObstacleLight(final MyGdxGame game){
		super(game);
		restitution = 0.42f;
	}
	
	@Override
	public void init(World world, OrthographicCamera camera, MapObject rectangleObject){
		categoryBits = 0001;
		super.init(world, camera, rectangleObject);
		
		body.setUserData("ObstacleLight");
		body.getFixtureList().get(0).setUserData("ObstacleLight");
		
		//Weight
		if(rectangleObject.getProperties().get("Weight") != null){
			body.getFixtureList().get(0).setDensity(
					body.getFixtureList().get(0).getDensity() * Float.parseFloat(rectangleObject.getProperties().get("Weight").toString())
			);
			body.resetMassData();
		}
		
		//Texture
		if(rectangleObject.getProperties().get("Texture") != null){
			stringTextureRegion = rectangleObject.getProperties().get("Texture").toString();
		}
		else
			stringTextureRegion = "CrateSquare";
		
		
		//Impulse	
		if(rectangleObject.getProperties().get("Impulse") != null){		
			Vector2 impulseForce = Pools.obtain(Vector2.class).set(	MathUtils.random(-15, 15) * body.getFixtureList().get(0).getDensity(), 
																	MathUtils.random(-15, 15) * body.getFixtureList().get(0).getDensity());
			Vector2 impulseCenter = Pools.obtain(Vector2.class).set(body.getPosition().x + MathUtils.random(-0.9f * width, 0.9f * width), 
																	body.getPosition().y + MathUtils.random(-0.9f * height, 0.9f * height));
			
			if(Boolean.parseBoolean((String) rectangleObject.getProperties().get("Impulse")))
			body.applyLinearImpulse(impulseForce, impulseCenter, true);
			
			Pools.free(impulseForce);
			Pools.free(impulseCenter);
		}
	}
	
	@Override
	public BodyType getBodyType(){
		return BodyType.DynamicBody;
	}
	
	@Override
	public void dispose(){
		game.pools.free(this);
	}
}

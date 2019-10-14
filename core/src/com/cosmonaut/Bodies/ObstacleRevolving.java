package com.cosmonaut.Bodies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.cosmonaut.MyGdxGame;

public class ObstacleRevolving extends Obstacle{
	
	private float speed = 90;

	public ObstacleRevolving(final MyGdxGame game, World world, OrthographicCamera camera, MapObject rectangleObject) {
		super(game, world, camera, rectangleObject);
		categoryBits = 0001;
		create(world, camera, rectangleObject);
		
		if(width <= 6*height)
			stringTextureRegion = "RevolvingObject";
		else if(width <= 12*height)
			stringTextureRegion = "RevolvingObjectMedium";
		else
			stringTextureRegion = "RevolvingObjectLarge";
		
		//Rotation speed
		if(rectangleObject.getProperties().get("Speed") != null)
			speed = Float.parseFloat((String) rectangleObject.getProperties().get("Speed"));
		else speed = 90;
		
		body.setFixedRotation(false);
		body.setAngularVelocity(speed*MathUtils.degreesToRadians);
	}
	
	public ObstacleRevolving(final MyGdxGame game){
		super(game);
		categoryBits = 0001;
	}
	
	public void init(World world, OrthographicCamera camera, MapObject rectangleObject){
		categoryBits = 0001;
		super.init(world, camera, rectangleObject);
		
		if(width <= 6*height)
			stringTextureRegion = "RevolvingObject";
		else if(width <= 12*height)
			stringTextureRegion = "RevolvingObjectMedium";
		else
			stringTextureRegion = "RevolvingObjectLarge";
		
		//Rotation speed
		if(rectangleObject.getProperties().get("Speed") != null)
			speed = Float.parseFloat((String) rectangleObject.getProperties().get("Speed"));
		else speed = 90;
		
		body.setFixedRotation(false);
		body.setAngularVelocity(speed*MathUtils.degreesToRadians);
	}
	
	@Override
	public BodyType getBodyType(){
		return BodyType.KinematicBody;
	}
	
	@Override
	public void activate(){	
		active = !active;
		
		if(active)
			body.setAngularVelocity(speed*MathUtils.degreesToRadians);
		else
			body.setAngularVelocity(0);
	}

	@Override
	public void dispose(){
		game.pools.free(this);
	}
}

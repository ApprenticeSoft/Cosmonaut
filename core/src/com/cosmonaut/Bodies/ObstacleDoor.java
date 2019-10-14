package com.cosmonaut.Bodies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;

public class ObstacleDoor extends Obstacle{
	
	private float speed = 5;
	private float doorAngle, doorScale;
	private Vector2 initialPosition, finalPosition, position;
	private boolean outOfOrder = false, alreadyOpen = false;

	public ObstacleDoor(final MyGdxGame game, World world, OrthographicCamera camera,	MapObject rectangleObject) {
		super(game, world, camera, rectangleObject);
		categoryBits = 0001;
		initialPosition = Pools.obtain(Vector2.class);
		finalPosition = Pools.obtain(Vector2.class); 
		position = Pools.obtain(Vector2.class);
		create(world, camera, rectangleObject);
		
		sound = game.assets.get("Sounds/Door.ogg", Sound.class);
		stringTextureRegion = "Door";
		
		doorScale = 1;
		
		//Is the door out of order ?
		if(rectangleObject.getProperties().get("Out of order") != null){
			outOfOrder = Boolean.parseBoolean((String) rectangleObject.getProperties().get("Out of order"));
		}
		else outOfOrder = false;
		
		//Motion speed
		if(rectangleObject.getProperties().get("Speed") != null){
			speed = Float.parseFloat((String) rectangleObject.getProperties().get("Speed"));
		}
		else speed = 5;
		
		if(outOfOrder)
			speed = 1f;
		
		initialPosition.set(posX, posY);
		
		if(width > height){
			if(outOfOrder)
				finalPosition.set(posX + Math.signum(speed) * 1.98f * GameConstants.HERO_WIDTH, posY);
			else
				finalPosition.set(posX + Math.signum(speed) * 1.9f*width, posY);
			doorAngle = 0;
		}
		else{
			if(outOfOrder)
				finalPosition.set(posX, posY + Math.signum(speed) * 1.98f * GameConstants.HERO_WIDTH);
			else
				finalPosition.set(posX, posY + Math.signum(speed) * 1.9f*height);
			doorAngle = 90;
			doorScale = height/width;
		}
		
		//Is the door already open ?
		if(rectangleObject.getProperties().get("Open") != null){
			alreadyOpen = Boolean.parseBoolean((String) rectangleObject.getProperties().get("Open"));
		}
		else alreadyOpen = false;
		
		if(alreadyOpen){
			active = false;
			position.set(finalPosition);
		}
		else{
			active = true;
			position.set(initialPosition);
		}
	}

	
	public ObstacleDoor(final MyGdxGame game){
		super(game);
		initialPosition = Pools.obtain(Vector2.class);
		finalPosition = Pools.obtain(Vector2.class); 
		position = Pools.obtain(Vector2.class);
		categoryBits = 0001;
		
	}
	
	public void init(World world, OrthographicCamera camera, MapObject rectangleObject){
		super.init(world, camera, rectangleObject);
		
		sound = game.assets.get("Sounds/Door.ogg", Sound.class);
		stringTextureRegion = "Door";
		
		doorScale = 1;
		
		//Is the door out of order ?
		if(rectangleObject.getProperties().get("Out of order") != null){
			outOfOrder = Boolean.parseBoolean((String) rectangleObject.getProperties().get("Out of order"));
		}
		else outOfOrder = false;
		
		//Motion speed
		if(rectangleObject.getProperties().get("Speed") != null){
			speed = Float.parseFloat((String) rectangleObject.getProperties().get("Speed"));
		}
		else speed = 5;
		
		if(outOfOrder)
			speed = 1f;
		
		initialPosition.set(posX, posY);
		
		if(width > height){
			if(outOfOrder)
				finalPosition.set(posX + Math.signum(speed) * 1.98f * GameConstants.HERO_WIDTH, posY);
			else
				finalPosition.set(posX + Math.signum(speed) * 1.9f*width, posY);
			doorAngle = 0;
		}
		else{
			if(outOfOrder)
				finalPosition.set(posX, posY + Math.signum(speed) * 1.98f * GameConstants.HERO_WIDTH);
			else
				finalPosition.set(posX, posY + Math.signum(speed) * 1.9f*height);
			doorAngle = 90;
			doorScale = height/width;
		}
		
		//Is the door already open ?
		if(rectangleObject.getProperties().get("Open") != null){
			alreadyOpen = Boolean.parseBoolean((String) rectangleObject.getProperties().get("Open"));
		}
		else alreadyOpen = false;
		
		if(alreadyOpen){
			active = false;
			position.set(finalPosition);
		}
		else{
			active = true;
			position.set(initialPosition);
		}
	}
	
	@Override
	public BodyType getBodyType(){
		return BodyType.KinematicBody;
	}
	
	@Override
	public void active(Hero hero){
		interpolatedActivity();
	}
	
	@Override
	public void activate(){
		active = !active;
		sound.play();
	}
	
	@Override
	public void draw(SpriteBatch batch, TextureAtlas textureAtlas){		
		batch.setColor(1, 1, 1, 1);
		batch.draw(	textureAtlas.findRegion(stringTextureRegion), 
					body.getPosition().x - width, 
					body.getPosition().y - height,
					width,
					height,
					2 * width,
					2 * height,
					doorScale,
					1/doorScale,
					doorAngle);
	}
	
	public void normalActivity(){
		if(active)
			body.setLinearVelocity(	Math.signum(speed) * (initialPosition.x - body.getPosition().x) * speed, 
									Math.signum(speed) * (initialPosition.y - body.getPosition().y) * speed
									);
		else
			body.setLinearVelocity(	Math.signum(speed) * (finalPosition.x - body.getPosition().x) * speed,
									Math.signum(speed) * (finalPosition.y - body.getPosition().y) * speed
									);
		if(outOfOrder)
			if(body.getPosition() == finalPosition)
				active = false;
	}
	
	public void interpolatedActivity(){
		if(!active){				
			position.interpolate(finalPosition, 0.2f, Interpolation.fade);
			body.setTransform(position, body.getAngle());
		}
		else{				
			position.interpolate(initialPosition, 0.2f, Interpolation.fade);
			body.setTransform(position, body.getAngle());
		}
		if(outOfOrder)
			if(body.getPosition() == finalPosition)
				active = false;
	}

	@Override
	public void dispose(){
		game.pools.free(this);
	}
}

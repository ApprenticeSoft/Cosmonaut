package com.cosmonaut.Bodies;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;

public class Leak extends Obstacle{

	private Set<Fixture> fixtures;
	private Vector2 leakForce, leakOrigin, soundDistance;
	private float force, leakSize, leakAngle, leakScale, leakSpeed, fixtureDistanceX, fixtureDistanceY;
	private Animation leakAnimation;
	private long soundId;
	
	//Test
	OrthographicCamera camera;
	
	public Leak(final MyGdxGame game, World world, OrthographicCamera camera,	MapObject rectangleObject) {
		super(game, world, camera, rectangleObject);
		this.camera = camera;
		
		leakForce = Pools.obtain(Vector2.class);
		leakOrigin = Pools.obtain(Vector2.class);
		soundDistance = Pools.obtain(Vector2.class);	
		categoryBits = 0010;
		leakScale = 1;
		
		sound = game.assets.get("Sounds/Gas Leak.ogg", Sound.class);
		soundId = sound.loop(0.1f, MathUtils.random(0.98f, 1.02f), 0);
		
		fixtures = new HashSet<Fixture>();
		
		leakAnimation = new Animation(1, game.assets.get("Images/Animations/Leak_Animation.pack", TextureAtlas.class).findRegions("Leak_Animation"), Animation.PlayMode.LOOP);
		
		create(world, camera, rectangleObject);
					
		body.getFixtureList().get(0).setSensor(true);
		body.getFixtureList().get(0).setUserData("Leak");
		body.setUserData("Leak");
		
		//Leak force
		if(rectangleObject.getProperties().get("Force") != null){
			force = Float.parseFloat(rectangleObject.getProperties().get("Force").toString()) * GameConstants.DEFAULT_LEAK_FORCE;
		}
		else
			force = GameConstants.DEFAULT_LEAK_FORCE;
		
		//Leak animation
		leakSpeed = 0.3f * GameConstants.DEFAULT_LEAK_FORCE/Math.abs(force);
		leakSpeed = MathUtils.clamp(leakSpeed, 0.01f, 0.1f);
		leakAnimation.setFrameDuration(leakSpeed);
		
		//Leak direction and leak origine
		if(rectangle.width > rectangle.height){
			leakForce.set(force, 0);
			leakSize = rectangle.width * GameConstants.MPP;
			
			if(force > 0){
				leakOrigin.set(posX - width, posY);
				leakAngle = 0;
			}
			else{
				leakOrigin.set(posX + width, posY);
				leakAngle = 180;
			}
		}
		else{
			leakForce.set(0, force);
			leakSize = rectangle.height * GameConstants.MPP;
			leakScale = height/width;
			
			if(force > 0){
				leakOrigin.set(posX, posY - height);
				leakAngle = 90;
			}
			else{
				leakOrigin.set(posX, posY + height);
				leakAngle = 270;
			}
		}
		
		soundDistance.set(camera.position.x, camera.position.y).sub(posX, posY);
	}
	
	public Leak(final MyGdxGame game){
		super(game);
		leakForce = Pools.obtain(Vector2.class);
		leakOrigin = Pools.obtain(Vector2.class);
		soundDistance = Pools.obtain(Vector2.class);	
		categoryBits = 0010;
		
		sound = game.assets.get("Sounds/Gas Leak.ogg", Sound.class);
		soundId = sound.loop(0.1f, MathUtils.random(0.98f, 1.02f), 0);
		
		fixtures = new HashSet<Fixture>();
		leakAnimation = new Animation(1, game.assets.get("Images/Animations/Leak_Animation.pack", TextureAtlas.class).findRegions("Leak_Animation"), Animation.PlayMode.LOOP);
	}
	
	public void init(World world, OrthographicCamera camera, MapObject rectangleObject){
		this.camera = camera;
		leakScale = 1;
		soundId = sound.loop(0.1f, MathUtils.random(0.98f, 1.02f), 0);
		super.init(world, camera, rectangleObject);
		
		body.getFixtureList().get(0).setSensor(true);
		body.getFixtureList().get(0).setUserData("Leak");
		body.setUserData("Leak");
		
		//Leak force
		if(rectangleObject.getProperties().get("Force") != null){
			force = Float.parseFloat(rectangleObject.getProperties().get("Force").toString()) * GameConstants.DEFAULT_LEAK_FORCE;
		}
		else
			force = GameConstants.DEFAULT_LEAK_FORCE;
		
		//Leak animation
		leakSpeed = 0.3f * GameConstants.DEFAULT_LEAK_FORCE/Math.abs(force);
		leakSpeed = MathUtils.clamp(leakSpeed, 0.01f, 0.1f);
		leakAnimation.setFrameDuration(leakSpeed);
		
		//Leak direction and leak origine
		if(rectangle.width > rectangle.height){
			leakForce.set(force, 0);
			leakSize = rectangle.width * GameConstants.MPP;
			
			if(force > 0){
				leakOrigin.set(posX - width, posY);
				leakAngle = 0;
			}
			else{
				leakOrigin.set(posX + width, posY);
				leakAngle = 180;
			}
		}
		else{
			leakForce.set(0, force);
			leakSize = rectangle.height * GameConstants.MPP;
			leakScale = height/width;
			
			if(force > 0){
				leakOrigin.set(posX, posY - height);
				leakAngle = 90;
			}
			else{
				leakOrigin.set(posX, posY + height);
				leakAngle = 270;
			}
		}
		
		soundDistance.set(camera.position.x, camera.position.y).sub(posX, posY);
	}
	
	public void addBody(Fixture fixture) {
		PolygonShape polygon = (PolygonShape) fixture.getShape();
		if (polygon.getVertexCount() > 2) 
			fixtures.add(fixture);
	}

	public void removeBody(Fixture fixture) {
		fixtures.remove(fixture);
	}
	
	public void active(Hero hero){
		soundDistance.set(camera.position.x, camera.position.y).sub(posX, posY);
		
		if(soundDistance.len() < GameConstants.SOUND_DISTANCE_LIMITE){
			sound.resume(soundId);
			sound.setVolume(soundId, 8/soundDistance.len());
		}
		else
			sound.pause(soundId);
		
		for(Fixture fixture : fixtures){
			fixtureDistanceX = Math.abs(fixture.getBody().getPosition().x - leakOrigin.x);
			fixtureDistanceY = Math.abs(fixture.getBody().getPosition().y - leakOrigin.y);
			
			fixture.getBody().applyForceToCenter(	
													leakForce.x * Math.abs(leakSize - fixtureDistanceX)/leakSize, 
													leakForce.y * Math.abs(leakSize - fixtureDistanceY)/leakSize,
													true
												);
		}
	}
	
	public void draw(SpriteBatch batch, float animTime){		
		batch.setColor(1, 1, 1, 1);
		batch.draw(	leakAnimation.getKeyFrame(animTime), 
					body.getPosition().x - width, 
					body.getPosition().y - height,
					width,
					height,
					2 * width,
					2 * height,
					leakScale,
					1/leakScale,
					leakAngle);
	}
	
	@Override
	public void dispose(){
		game.pools.free(this);
	}
}

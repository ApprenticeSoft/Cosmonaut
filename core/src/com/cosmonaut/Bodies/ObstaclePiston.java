package com.cosmonaut.Bodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;

public class ObstaclePiston extends Obstacle{

	private PolygonShape shapeAxis;
	private float widthAxis, heightAxis, posXAxis, posYAxis, 
					speed = 10, speedReference = 10, delay = 0, pause = 0, pauseReference = 0;
	private Vector2 initialPosition, finalPosition, direction, posAxisSprite, soundDistance, currentPosition;
	private Vector2[] travel;
	private int step = 1;
	private Rectangle rectangleAxis;
	private long soundId, soundChockId;
	private Sound soundChock;
	private boolean soundPlay = true, soundChockPlay = true;
	
	private NinePatch ninePatchAxis;

	//Test
	OrthographicCamera camera;
	
	public ObstaclePiston(final MyGdxGame game, World world, OrthographicCamera camera, MapObject rectangleObject1, TextureAtlas textureAtlas, MapObject rectangleObject2) {
		super(game, world, camera, rectangleObject1, textureAtlas);
		this.camera = camera;
		categoryBits = 0001;

		sound = game.assets.get("Sounds/Piston_Motor.ogg", Sound.class);
		sound.stop();
        //soundId = sound.play(0.8f);
        
        soundChock = game.assets.get("Sounds/Piston_Bang.ogg", Sound.class);
        soundChock.stop();
		
		if(rectangleObject1.getProperties().get("Part").equals("Head")){
			create(world, camera, rectangleObject1);
			rectangleAxis = ((RectangleMapObject) rectangleObject2).getRectangle();
		}
		else{
			create(world, camera, rectangleObject2);
			rectangleAxis = ((RectangleMapObject) rectangleObject1).getRectangle();
		}
			
		//Delay before activation
		if(rectangleObject1.getProperties().get("Delay") != null){
			delay = Float.parseFloat((String) rectangleObject1.getProperties().get("Delay"));
		}
		else if(rectangleObject2.getProperties().get("Delay") != null){
			delay = Float.parseFloat((String) rectangleObject2.getProperties().get("Delay"));
		}
		
		//Motion speed
		if(rectangleObject1.getProperties().get("Speed") != null){
			speedReference = Float.parseFloat((String) rectangleObject1.getProperties().get("Speed"));
		}
		else if(rectangleObject2.getProperties().get("Speed") != null){
			speedReference = Float.parseFloat((String) rectangleObject2.getProperties().get("Speed"));
		}
		speed = speedReference;
		
		//Pause
		if(rectangleObject1.getProperties().get("Pause") != null){
			pauseReference = Float.parseFloat((String) rectangleObject1.getProperties().get("Pause"));
		}
		else if(rectangleObject2.getProperties().get("Pause") != null){
			pauseReference = Float.parseFloat((String) rectangleObject2.getProperties().get("Pause"));
		}
		else pauseReference = 0;
		
		//Creation of the second Fixture		
		widthAxis = (rectangleAxis.width/2) * GameConstants.MPP;
		heightAxis = (rectangleAxis.height/2) * GameConstants.MPP;
		posXAxis = (rectangleAxis.x + rectangleAxis.width/2) * GameConstants.MPP;
		posYAxis = (rectangleAxis.y + rectangleAxis.height/2) * GameConstants.MPP;
		
		shapeAxis = Pools.obtain(PolygonShape.class);
		Vector2 position = Pools.obtain(Vector2.class).set(posXAxis - posX, posYAxis - posY);
		shapeAxis.setAsBox(widthAxis, heightAxis, position, 0);
		Pools.free(position);
        
		bodyDef = Pools.obtain(BodyDef.class);
		bodyDef.position.set((rectangleAxis.x + rectangleAxis.width/2) * GameConstants.MPP, (rectangleAxis.y + rectangleAxis.height/2) * GameConstants.MPP);
		
		fixtureDef = Pools.obtain(FixtureDef.class);
		fixtureDef.shape = shapeAxis;
        fixtureDef.density = 0;  
        fixtureDef.friction = 0.5f;  
        fixtureDef.restitution = 0.5f;
        
		body.createFixture(fixtureDef);  
		body.setUserData("ObstaclePiston");	

		body.getFixtureList().get(0).setUserData("ObstaclePiston");
		body.getFixtureList().get(1).setUserData("Obstacle");
		
		initialPosition = Pools.obtain(Vector2.class).set(posX, posY);
		posAxisSprite = Pools.obtain(Vector2.class).set(0,0);
		if(posX == posXAxis){
			//Drawing
			ninePatch = new NinePatch(textureAtlas.findRegion("PistonHead"), 24, 24, 24, 24);
			ninePatch.scale(0.5f*GameConstants.MPP, 0.5f*GameConstants.MPP);
			ninePatchAxis = new NinePatch(textureAtlas.findRegion("PistonAxis"), 24, 24, 24, 24);
			ninePatchAxis.scale(0.5f*GameConstants.MPP, 0.5f*GameConstants.MPP);
			
			posAxisSprite.x = - widthAxis;
			finalPosition = Pools.obtain(Vector2.class).set(initialPosition.x, initialPosition.y + rectangleAxis.height * Math.signum(posYAxis - posY) * GameConstants.MPP);
			
			if(posY < posYAxis)
				posAxisSprite.y = height;
			else
				posAxisSprite.y = - height - 2 * heightAxis;
		}
		else{
			//Drawing
			ninePatch = new NinePatch(textureAtlas.findRegion("PistonHeadHorizontal"), 24, 24, 24, 24);
			ninePatch.scale(0.5f*GameConstants.MPP, 0.5f*GameConstants.MPP);
			ninePatchAxis = new NinePatch(textureAtlas.findRegion("PistonAxisHorizontal"), 24, 24, 24, 24);
			ninePatchAxis.scale(0.5f*GameConstants.MPP, 0.5f*GameConstants.MPP);
			
			posAxisSprite.y = - heightAxis;
			finalPosition = Pools.obtain(Vector2.class).set(initialPosition.x + rectangleAxis.width * Math.signum(posXAxis - posX) * GameConstants.MPP, initialPosition.y);
			
			if(posX < posXAxis)
				posAxisSprite.x = width;
			else
				posAxisSprite.x = - width - 2 * widthAxis;
		}
          
        //Start position
		if(rectangleObject1.getProperties().get("Position") != null){
			if(rectangleObject1.getProperties().get("Position").toString().equals("High"))
		        body.setTransform(finalPosition, body.getAngle());
		}

		travel = new Vector2[2];
		travel[0] = Pools.obtain(Vector2.class).set(initialPosition);
		travel[1] = Pools.obtain(Vector2.class).set(finalPosition);

        direction = Pools.obtain(Vector2.class).set(travel[step].x - body.getPosition().x, travel[step].y - body.getPosition().y);

        soundDistance = Pools.obtain(Vector2.class).set(camera.position.x, camera.position.y).sub(posX, posY);
		currentPosition = Pools.obtain(Vector2.class).set(travel[step].x - body.getPosition().x, travel[step].y - body.getPosition().y);

		Pools.free(bodyDef);
		Pools.free(fixtureDef);
		Pools.free(shapeAxis);
	}
	
	public ObstaclePiston(final MyGdxGame game){
		super(game);
		categoryBits = 0001;
		
	}
	
	public void init(World world, OrthographicCamera camera, MapObject rectangleObject1, TextureAtlas textureAtlas, MapObject rectangleObject2){
		//super.init(world, camera, rectangleObject1);
		this.camera = camera;
		step = 1;
		
		sound = game.assets.get("Sounds/Piston_Motor.ogg", Sound.class);
		sound.stop();
        //soundId = sound.play(0.8f);
        
        soundChock = game.assets.get("Sounds/Piston_Bang.ogg", Sound.class);
        soundChock.stop();
		
		if(rectangleObject1.getProperties().get("Part").equals("Head")){
			create(world, camera, rectangleObject1);
			rectangleAxis = ((RectangleMapObject) rectangleObject2).getRectangle();
		}
		else{
			create(world, camera, rectangleObject2);
			rectangleAxis = ((RectangleMapObject) rectangleObject1).getRectangle();
		}
			
		//Delay before activation
		if(rectangleObject1.getProperties().get("Delay") != null){
			delay = Float.parseFloat((String) rectangleObject1.getProperties().get("Delay"));
		}
		else if(rectangleObject2.getProperties().get("Delay") != null){
			delay = Float.parseFloat((String) rectangleObject2.getProperties().get("Delay"));
		}
		else delay = 0;
		
		//Motion speed
		if(rectangleObject1.getProperties().get("Speed") != null){
			speedReference = Float.parseFloat((String) rectangleObject1.getProperties().get("Speed"));
		}
		else if(rectangleObject2.getProperties().get("Speed") != null){
			speedReference = Float.parseFloat((String) rectangleObject2.getProperties().get("Speed"));
		}
		else speedReference = 10;
		
		speed = speedReference;
		
		//Pause
		if(rectangleObject1.getProperties().get("Pause") != null){
			pauseReference = Float.parseFloat((String) rectangleObject1.getProperties().get("Pause"));
		}
		else if(rectangleObject2.getProperties().get("Pause") != null){
			pauseReference = Float.parseFloat((String) rectangleObject2.getProperties().get("Pause"));
		}
		else pauseReference = 0;
		pause = 0;
		
		//Creation of the second Fixture		
		widthAxis = (rectangleAxis.width/2) * GameConstants.MPP;
		heightAxis = (rectangleAxis.height/2) * GameConstants.MPP;
		posXAxis = (rectangleAxis.x + rectangleAxis.width/2) * GameConstants.MPP;
		posYAxis = (rectangleAxis.y + rectangleAxis.height/2) * GameConstants.MPP;
		
		shapeAxis = Pools.obtain(PolygonShape.class);
		Vector2 position = Pools.obtain(Vector2.class).set(posXAxis - posX, posYAxis - posY);
		shapeAxis.setAsBox(widthAxis, heightAxis, position, 0);
		Pools.free(position);

		bodyDef = Pools.obtain(BodyDef.class);
		bodyDef.position.set((rectangleAxis.x + rectangleAxis.width/2) * GameConstants.MPP, (rectangleAxis.y + rectangleAxis.height/2) * GameConstants.MPP);
		
		fixtureDef = Pools.obtain(FixtureDef.class);
		fixtureDef.shape = shapeAxis;
        fixtureDef.density = 0;  
        fixtureDef.friction = 0.5f;  
        fixtureDef.restitution = 0.5f;
        
		body.createFixture(fixtureDef);  
		body.setUserData("ObstaclePiston");		

		body.getFixtureList().get(0).setUserData("ObstaclePiston");
		body.getFixtureList().get(1).setUserData("Obstacle");
		
		initialPosition = Pools.obtain(Vector2.class).set(posX, posY);
		posAxisSprite = Pools.obtain(Vector2.class).set(0,0);
		if(posX == posXAxis){
			//Drawing
			ninePatch = new NinePatch(textureAtlas.findRegion("PistonHead"), 24, 24, 24, 24);
			ninePatch.scale(0.5f*GameConstants.MPP, 0.5f*GameConstants.MPP);
			ninePatchAxis = new NinePatch(textureAtlas.findRegion("PistonAxis"), 24, 24, 24, 24);
			ninePatchAxis.scale(0.5f*GameConstants.MPP, 0.5f*GameConstants.MPP);
			
			posAxisSprite.x = - widthAxis;
			finalPosition = Pools.obtain(Vector2.class).set(initialPosition.x, initialPosition.y + rectangleAxis.height * Math.signum(posYAxis - posY) * GameConstants.MPP);
			
			if(posY < posYAxis)
				posAxisSprite.y = height;
			else
				posAxisSprite.y = - height - 2 * heightAxis;
		}
		else{
			//Drawing
			ninePatch = new NinePatch(textureAtlas.findRegion("PistonHeadHorizontal"), 24, 24, 24, 24);
			ninePatch.scale(0.5f*GameConstants.MPP, 0.5f*GameConstants.MPP);
			ninePatchAxis = new NinePatch(textureAtlas.findRegion("PistonAxisHorizontal"), 24, 24, 24, 24);
			ninePatchAxis.scale(0.5f*GameConstants.MPP, 0.5f*GameConstants.MPP);
			
			posAxisSprite.y = - heightAxis;
			finalPosition = Pools.obtain(Vector2.class).set(initialPosition.x + rectangleAxis.width * Math.signum(posXAxis - posX) * GameConstants.MPP, initialPosition.y);
			
			if(posX < posXAxis)
				posAxisSprite.x = width;
			else
				posAxisSprite.x = - width - 2 * widthAxis;
		}
          
        //Start position
		if(rectangleObject1.getProperties().get("Position") != null){
			if(rectangleObject1.getProperties().get("Position").toString().equals("High"))
		        body.setTransform(finalPosition, body.getAngle());
		}

		travel = new Vector2[2];
		travel[0] = Pools.obtain(Vector2.class).set(initialPosition);
		travel[1] = Pools.obtain(Vector2.class).set(finalPosition);

        direction = Pools.obtain(Vector2.class).set(travel[step].x - body.getPosition().x, travel[step].y - body.getPosition().y);

        soundDistance = Pools.obtain(Vector2.class).set(camera.position.x, camera.position.y).sub(posX, posY);
		currentPosition = Pools.obtain(Vector2.class).set(travel[step].x - body.getPosition().x, travel[step].y - body.getPosition().y);

		Pools.free(bodyDef);
		Pools.free(fixtureDef);
		Pools.free(shapeAxis);
	}
	
	@Override
	public BodyType getBodyType(){
		return BodyType.KinematicBody;
	}

	@Override
	public void active(Hero hero){
		if(active){
			if(delay > 0){
				delay -= Gdx.graphics.getDeltaTime();
			}
			else{	
				soundDistance.set(camera.position.x, camera.position.y).sub(posX, posY);
				currentPosition.set(travel[step].x - body.getPosition().x, travel[step].y - body.getPosition().y);
				
				if(!currentPosition.hasSameDirection(direction)){	
					body.setLinearVelocity(0, 0); 
					
					if(soundDistance.len() < GameConstants.SOUND_DISTANCE_LIMITE){
						sound.stop(soundId);
						soundPlay = false;
					
					
						if(!soundChockPlay){
							soundChockPlay = true;
							soundChockId = soundChock.play(9/soundDistance.len(), MathUtils.random(0.98f, 1.02f), 0);
						}
					}
					else
						sound.pause(soundId);
					
					if(pause > 0){
						pause -= Gdx.graphics.getDeltaTime();
						speed = 0;
					}
					else{
						if(step > 0)
							step = 0;
						else step = 1;
						
				        direction.set(travel[step].x - body.getPosition().x, travel[step].y - body.getPosition().y);
				        pause = pauseReference;
				        speed = speedReference;
					}
				}
				else{
					body.setLinearVelocity(direction.clamp(speed, speed)); 
					soundChockPlay = false;
					
					if(soundDistance.len() < GameConstants.SOUND_DISTANCE_LIMITE){
						if(!soundPlay){
							soundPlay = true;
							soundId = sound.play(9/soundDistance.len(), MathUtils.random(0.96f, 1.04f), 0);
						}
					}
					else
						sound.pause(soundId);
				}
			}
			
			sound.setVolume(soundId, 9/soundDistance.len());
			
		}
		else{
			body.setLinearVelocity(0, 0); 	
			sound.pause(soundId);
		}			
	}
	
	@Override
	public void activate(){
		active = !active;
	}
	
	@Override	
	public void draw(SpriteBatch batch, TextureAtlas textureAtlas){	
		batch.setColor(1, 1, 1, 1);
		ninePatch.draw(	batch, 
						body.getPosition().x - width,
						body.getPosition().y - height, 
						2 * width, 
						2 * height);
	
		ninePatchAxis.draw(	batch, 
							body.getPosition().x + posAxisSprite.x,
							body.getPosition().y + posAxisSprite.y, 
							2 * widthAxis, 
							2 * heightAxis);	
	}
	
	@Override	
	public void draw(SpriteBatch batch){
		batch.setColor(1, 1, 1, 1);
		ninePatch.draw(	batch, 
						body.getPosition().x - width,
						body.getPosition().y - height, 
						2 * width, 
						2 * height);
	
		ninePatchAxis.draw(	batch, 
							body.getPosition().x + posAxisSprite.x,
							body.getPosition().y + posAxisSprite.y, 
							2 * widthAxis, 
							2 * heightAxis);
	}
	
	@Override
	public void dispose(){
		Pools.free(travel[0]);
		Pools.free(travel[1]);
		game.pools.free(this);
	}
	
}

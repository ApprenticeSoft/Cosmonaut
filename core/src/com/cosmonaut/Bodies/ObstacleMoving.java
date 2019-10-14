package com.cosmonaut.Bodies;

import box2dLight.ChainLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;

public class ObstacleMoving extends Obstacle{

	private float speed, delay = 0, pause = 0, pauseCooldown;
	private boolean backward, loop;
	private Vector2 direction, currentPosition;
	private Vector2[] path;
	private int step = 0;
	private float widthFactor = 1;
	private float heightFactor = 1;
	
	private ChainLight chainLight;
	
	public ObstacleMoving(final MyGdxGame game, World world, OrthographicCamera camera, PolylineMapObject polylineObject/*, TextureAtlas textureAtlas*/) {
		super(game, world, camera, polylineObject);
		
		direction = Pools.obtain(Vector2.class);
		currentPosition = Pools.obtain(Vector2.class);
		categoryBits = 0001;
		stringTextureRegion = "MovingObstacle";
		active = true;
	
		create(world, camera, polylineObject);
	}
	
	public ObstacleMoving(final MyGdxGame game){
		super(game);
		direction = Pools.obtain(Vector2.class);
		currentPosition = Pools.obtain(Vector2.class);
		categoryBits = 0001;
		stringTextureRegion = "MovingObstacle";
		active = true;
	}
	

	public void init(World world, OrthographicCamera camera, PolylineMapObject polylineObject){
		categoryBits = 0001;
		step = 0;
		create(world, camera, polylineObject);
	}
	
	public void create(World world, OrthographicCamera camera, PolylineMapObject polylineObject){
		setInitialState(polylineObject);
		
		//Delay before activation
		if(polylineObject.getProperties().get("Delay") != null)
			delay = Float.parseFloat((String) polylineObject.getProperties().get("Delay"));
		else delay = 0;
		
		//Pause between each step
		if(polylineObject.getProperties().get("Pause") != null)
			pause = Float.parseFloat((String) polylineObject.getProperties().get("Pause"));
		else pause = 0;
		pauseCooldown = pause;
		
		//SPEED
		if(polylineObject.getProperties().get("Speed") != null)
			speed = Float.parseFloat((String) polylineObject.getProperties().get("Speed"));
		else speed = 5;
		
		//DOES THE PATH MAKE A LOOP ?
		if(polylineObject.getProperties().get("Loop") != null){
			loop =  Boolean.parseBoolean((String) polylineObject.getProperties().get("Loop"));
		}
		else loop = false;
		
		//WIDTH OF THE MOVING OBJECT		
		if(polylineObject.getProperties().get("Width") != null)
			widthFactor = Float.parseFloat((String) polylineObject.getProperties().get("Width"));
		else widthFactor = 1;
		
		width = GameConstants.PPT * GameConstants.MPP/2;
		width *= widthFactor;
		
		//HEIGHT OF THE MOVING OBJECT
		if(polylineObject.getProperties().get("Height") != null)
			heightFactor = Float.parseFloat((String) polylineObject.getProperties().get("Height"));
		else heightFactor = 1;
		
		height = GameConstants.PPT * GameConstants.MPP/2;
		height *= heightFactor;

		path = new Vector2[polylineObject.getPolyline().getTransformedVertices().length/2];
    	for(int i = 0; i < path.length; i++){
    		path[i] = Pools.obtain(Vector2.class).set(polylineObject.getPolyline().getTransformedVertices()[i*2]*GameConstants.MPP, polylineObject.getPolyline().getTransformedVertices()[i*2 + 1]*GameConstants.MPP);
    	}  
    	
    	polygonShape = Pools.obtain(PolygonShape.class);
    	polygonShape.setAsBox(width, height);

    	bodyDef = Pools.obtain(BodyDef.class);
    	bodyDef.type = getBodyType();
    	bodyDef.position.set(path[0]);
    	
    	fixtureDef = Pools.obtain(FixtureDef.class);
		fixtureDef.shape = polygonShape;
        fixtureDef.density = 0.0f;  
        fixtureDef.friction = 0.0f;  
        fixtureDef.restitution = 0f;
        fixtureDef.isSensor = false;
        fixtureDef.filter.categoryBits = categoryBits;

    	body = world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData("Objet");
        body.setUserData("Objet");

        Pools.free(bodyDef);
        Pools.free(fixtureDef);
        Pools.free(polygonShape);

        direction.set(path[step].x - body.getPosition().x, path[step].y - body.getPosition().y);
        currentPosition.set(path[step].x - body.getPosition().x, path[step].y - body.getPosition().y);
        body.setLinearVelocity(direction.clamp(speed, speed));
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
				currentPosition.set(path[step].x - body.getPosition().x, path[step].y - body.getPosition().y);
				
				if(!loop){
					if(!backward){

						if(!currentPosition.hasSameDirection(direction)){    
					        pauseCooldown -= Gdx.graphics.getDeltaTime();
							body.setTransform(path[step], 0);
							step++;
							
					        if(step == path.length){
					        	backward = true;
					        	step = path.length - 2;
					        }
					        
							direction.set(path[step].x - body.getPosition().x, path[step].y - body.getPosition().y);
						}
					}
					else{
						if(!currentPosition.hasSameDirection(direction)){
							pauseCooldown -= Gdx.graphics.getDeltaTime();
							body.setTransform(path[step], 0);
							step--;
							
					        if(step < 0){
					        	backward = false;
					        	step = 1;
					        }
					        
							direction.set(path[step].x - body.getPosition().x, path[step].y - body.getPosition().y);
						}
					}	
				}
				else{
					if(!currentPosition.hasSameDirection(direction)){
						pauseCooldown -= Gdx.graphics.getDeltaTime();
						body.setTransform(path[step], 0);
						step++;
						
				        if(step == path.length)
				        	step = 0;
				        
						direction.set(path[step].x - body.getPosition().x, path[step].y - body.getPosition().y);
					}
				}
				
				if(pauseCooldown < 0 || pauseCooldown == pause){
					body.setLinearVelocity(direction.clamp(speed, speed)); 
					pauseCooldown = pause;
				}
				else{
					body.setLinearVelocity(0,0);
					pauseCooldown -= Gdx.graphics.getDeltaTime();
				}
			}
		}
		else{
			body.setLinearVelocity(0, 0); 		
		}
	}
	
	@Override
	public void activate(){
		active = !active;
	}
	/*
	public void setLight(RayHandler rayHandler){
		Color color = Pools.obtain(Color.class).set(0.9f, 0.1f, 0.3f, 1f);
		chainLight = new ChainLight(rayHandler, 50, color, 20, -1, new float[]{-width,width, -width,-width});
		chainLight.attachToBody(body,0);
		Pools.free(color);
	}
	*/
	@Override
	public void dispose(){
		game.pools.free(this);	

		for(int i = 0; i < path.length; i++){
    		Pools.free(path[i]); 
    	} 
	}
}

package com.cosmonaut.Bodies;

import box2dLight.Light;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;

public class Obstacle {
	
	final MyGdxGame game;
	public Body body;
	protected BodyDef bodyDef;
	protected FixtureDef fixtureDef;
	protected PolygonShape polygonShape;
	protected Rectangle rectangle;
	public float posX, posY, width, height, angle;
	public int associationNumber;
	public boolean active;
	
	//Graphs
	protected NinePatch ninePatch;
	protected String stringTextureRegion;
	
	protected float restitution = 0.08f;
	
	//Sound
	public Sound sound;
	
	//Box2DLight
	protected short categoryBits = 0001;
	
	public Obstacle(final MyGdxGame game){
		this.game = game;
	}
	
	public Obstacle(final MyGdxGame game, World world, OrthographicCamera camera, MapObject rectangleObject){	
		this.game = game;
	}
	
	public Obstacle(final MyGdxGame game, World world, OrthographicCamera camera, MapObject rectangleObject, TextureAtlas textureAtlas){	
		this.game = game;	
	}
	
	public Obstacle(final MyGdxGame game, World world, OrthographicCamera camera, PolylineMapObject polylineObject){
		this.game = game;
		setInitialState(polylineObject);
	}
	
	public void create(World world, OrthographicCamera camera, MapObject rectangleObject){
		setInitialState(rectangleObject);
		
		stringTextureRegion = "WhiteSquare";
		
		rectangle = ((RectangleMapObject) rectangleObject).getRectangle();
			
		this.posX = (rectangle.x + rectangle.width/2) * GameConstants.MPP;
		this.posY = (rectangle.y + rectangle.height/2) * GameConstants.MPP;
		this.width = (rectangle.width/2) * GameConstants.MPP;
		this.height = (rectangle.height/2) * GameConstants.MPP;
		
		if(rectangleObject.getProperties().get("rotation") != null)
			this.angle = -Float.parseFloat(rectangleObject.getProperties().get("rotation").toString())*MathUtils.degreesToRadians;
		else angle = 0;
		
		polygonShape = Pools.obtain(PolygonShape.class);
		polygonShape.setAsBox(width, height);

		bodyDef = Pools.obtain(BodyDef.class);
		bodyDef.position.set(posX, posY);
    	bodyDef.type = getBodyType();
		body = world.createBody(bodyDef);
		
		fixtureDef = Pools.obtain(FixtureDef.class);
		fixtureDef.shape = polygonShape;
        fixtureDef.density = (float)(GameConstants.DENSITY/(width * height));  
        fixtureDef.friction = 0.5f;  
        fixtureDef.restitution = restitution;
        fixtureDef.filter.groupIndex = (short) 0000;
        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.isSensor = false;
   
        body.createFixture(fixtureDef).setUserData("Obstacle");
        body.setUserData("Obstacle");
        body.setFixedRotation(false);
        
        if(rectangleObject.getProperties().get("rotation") != null){
            /*
             * To obtain x' et y' positions from x et y positions after a rotation of an angle A
             * around the origine (0, 0) :
             * x' = x*cos(A) - y*sin(A)
             * y' = x*sin(A) + y*cos(A)
             */
        	float X = (float)(body.getPosition().x - width + width * MathUtils.cos(angle) + height * MathUtils.sin(angle));
        	float Y = (float)(width * MathUtils.sin(angle) + body.getPosition().y + height - height * MathUtils.cos(angle));
        	body.setTransform(X, Y, this.angle);
        }
        
        Pools.free(bodyDef);
        Pools.free(fixtureDef);
        Pools.free(polygonShape);
	}
	
	public float getWidth(){
		return width;
	}
	
	public float getHeight(){
		return height;
	}
	
	public float getX(){
		return body.getPosition().x;
	}
	
	public float getY(){
		return body.getPosition().y;
	}
	
	public BodyType getBodyType(){
		return BodyType.StaticBody;
	}

	public void setX( float X){
		posX = X;
	}

	public void setY( float Y){
		posY = Y;
	}
	
	public void setInitialState(MapObject mapObject){
		//Association Number
		if(mapObject.getProperties().get("Association Number") != null){
			associationNumber = Integer.parseInt((String) mapObject.getProperties().get("Association Number"));
		}
		else associationNumber = 666;
		
		//Is the Obstacle active ?
		if(mapObject.getProperties().get("Active") != null){
			if(Integer.parseInt((String) mapObject.getProperties().get("Active")) == 1)
				active = true;
			else 
				active = false;
		}
		else
			active = true;		
	}

	public void active(Hero hero){
		
	}
	
	public void activate(){
		
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
	
	public void draw(SpriteBatch batch){
		batch.setColor(1, 1, 1, 1);
		ninePatch.draw(	batch, 
						body.getPosition().x - width,
						body.getPosition().y - height, 
						2 * width, 
						2 * height);
	}
	
	public void soundPause(){
		if(sound != null)
			sound.pause();
	}
	
	public void soundResume(){
		if(sound != null)
			sound.resume();
	}
	
	public void init(World world, OrthographicCamera camera, MapObject rectangleObject){
		create(world, camera, rectangleObject);
	}
	
	public void attacheLight(Light light){
		light.attachToBody(body);
	}
	
	public void dispose(){	
	}
}

package com.cosmonaut.Bodies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;

public class Polygone extends Obstacle{

	//private PolygonSprite polySprite;
	float coordPoly[];
	
	public Polygone(final MyGdxGame game,World world, OrthographicCamera camera, MapObject Object) {
		super(game, world, camera, Object);
		categoryBits = 0001;
		create(world, camera, Object);
	}
	
	public Polygone(final MyGdxGame game) {
		super(game);
		categoryBits = 0001;
	}
	
	@Override
	public void init(World world, OrthographicCamera camera, MapObject Object){
		create(world, camera, Object);
	}
	
	@Override
	public void create(World world, OrthographicCamera camera, MapObject Object){
		coordPoly = new float[((PolygonMapObject) Object).getPolygon().getTransformedVertices().length];
    	for(int i = 0; i < ((PolygonMapObject) Object).getPolygon().getTransformedVertices().length; i++){
    		coordPoly[i] = ((PolygonMapObject) Object).getPolygon().getTransformedVertices()[i]*GameConstants.MPP;
    	}
    	
    	//polygonShape = new PolygonShape();
    	polygonShape = Pools.obtain(PolygonShape.class);
    	polygonShape.set(coordPoly);
    	
    	bodyDef = new BodyDef();
    	//bodyDef = Pools.obtain(BodyDef.class);
    	bodyDef.type = getBodyType();
    	
		body = world.createBody(bodyDef);
		
		//fixtureDef = new FixtureDef();
		fixtureDef = Pools.obtain(FixtureDef.class);
		fixtureDef.shape = polygonShape;
        fixtureDef.density = 2.0f;  
        fixtureDef.friction = 0f;  
        fixtureDef.restitution = 0.8f;
        fixtureDef.isSensor = false;
   
        body.createFixture(fixtureDef).setUserData("Objet");
        body.setUserData("Objet");
        body.setFixedRotation(false);
        
        //polygonShape.dispose();
        Pools.free(polygonShape);
        Pools.free(fixtureDef);
        //Pools.free(bodyDef);
	}
	/*
	public void setPos(float X, float Y){
        polySprite.setX(X);
        polySprite.setY(Y);
	}*/

	@Override
	public void dispose(){
		game.pools.free(this);
	}
}

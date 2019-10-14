package com.cosmonaut.Lights;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.Bodies.Obstacle;
import com.cosmonaut.Utils.GameConstants;

import box2dLight.ChainLight;
import box2dLight.RayHandler;

public class LinearLight extends MyLight{

	private int direction;
	private float positionAngle;
	private Vector2 position, angleVector, widthVector;
	private float width, height = (30 * GameConstants.MPP/4) * 5/17;
	
	public LinearLight(PolylineMapObject polylineObject, RayHandler rayHandler){
		super(polylineObject, rayHandler);
	}
	
	public LinearLight(PolylineMapObject polylineObject, RayHandler rayHandler, Obstacle obstacle){
		super(polylineObject, rayHandler, obstacle);
	}
	
	@Override
	public void lightDefinition(RayHandler rayHandler){
		angleVector = Pools.obtain(Vector2.class).set(coordinates[2] - coordinates[0], coordinates[3] - coordinates[1]);
		positionAngle = angleVector.angle();
		Pools.free(angleVector);
		
		position = Pools.obtain(Vector2.class).set((coordinates[2] + coordinates[0])/2, (coordinates[3] + coordinates[1])/2);
		
		widthVector = Pools.obtain(Vector2.class).set(coordinates[2] - coordinates[0], coordinates[3] - coordinates[1]);
		width = widthVector.len()/2;
		Pools.free(widthVector);
		
		nbRay = (int)(15*2*width/GameConstants.MPT);
		Color color = Pools.obtain(Color.class).set(r, g, b, a);
		light = new ChainLight(rayHandler, nbRay, color, distance, direction, coordinates);
        light.setContactFilter((short) 0010, (short)1000, (short)0001);
        Pools.free(color);
	}
	
	public void initialState(MapObject mapObject){  
		super.initialState(mapObject);
		
		//Direction
		if(mapObject.getProperties().get("Direction") != null){
			direction = Integer.parseInt((String) mapObject.getProperties().get("Direction"));
		}
		else direction = -1;
		
		nbRay = (int)((20*2*width/GameConstants.MPT) * GameConstants.LIGHT_RAY_MULTIPLICATOR);
	}
	
	@Override
	public void draw(SpriteBatch batch, TextureAtlas textureAtlas){
		batch.setColor(r, g, b, 1);
		if(light.getBody() == null)
			batch.draw(	textureAtlas.findRegion("ConeLight"), 
						position.x - width, 
						position.y - height,
						width,
						height,
						2 * width,
						2 * height,
						1,
						1,
						positionAngle);	
		else
			batch.draw(	textureAtlas.findRegion("ConeLight"), 
						(float)(light.getBody().getPosition().x - width + position.x*MathUtils.cos(light.getBody().getAngle()) - position.y*MathUtils.sin(light.getBody().getAngle())), 
						(float)(light.getBody().getPosition().y - height + position.x*MathUtils.sin(light.getBody().getAngle()) + position.y*MathUtils.cos(light.getBody().getAngle())),
						width,
						height,
						2 * width,
						2 * height,
						1,
						1,
						light.getBody().getAngle()*MathUtils.radiansToDegrees + positionAngle);	
		

        /*
         * To obtain x' et y' positions from x et y positions after a rotation of an angle A
         * around the origine (0, 0) :
         * x' = x*cos(A) - y*sin(A)
         * y' = x*sin(A) + y*cos(A)
         */
	}
	
	@Override
	public void dispose(){
		Pools.free(position);
	}
}

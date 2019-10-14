package com.cosmonaut.Lights;

import box2dLight.ConeLight;
import box2dLight.RayHandler;

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

public class MyConeLight extends MyLight{
	
	private float coneAngle, positionAngle;
	private Vector2 position, angleVector;
	private float width = 30 * GameConstants.MPP/4, height = width * 5/17;
	
	public MyConeLight(PolylineMapObject polylineObject, RayHandler rayHandler){
		super(polylineObject, rayHandler);
	}
	
	public MyConeLight(PolylineMapObject polylineObject, RayHandler rayHandler, Obstacle obstacle){
		super(polylineObject, rayHandler, obstacle);
	}
	
	@Override
	public void lightDefinition(RayHandler rayHandler){
		angleVector = Pools.obtain(Vector2.class).set(coordinates[2] - coordinates[0], coordinates[3] - coordinates[1]);
		positionAngle = angleVector.angle();
		Pools.free(angleVector);
		
		Color color = Pools.obtain(Color.class).set(r, g, b, a);
		position = Pools.obtain(Vector2.class).set(coordinates[0], coordinates[1]);
		coneLight = new ConeLight(rayHandler, nbRay, color, distance, coordinates[0], coordinates[1], positionAngle, coneAngle);	
        coneLight.setContactFilter((short) 0010, (short)1000, (short)0001);
        Pools.free(color);
	}
	
	@Override
	public void attachToObstacle(Obstacle obstacle){
		coneLight.attachToBody(obstacle.body, coordinates[0], coordinates[1], positionAngle);
	}
	
	@Override
	public void active(){
		if(flicker)
			flicker(coneLight, flickerFactorA);
		else if(flickerRGBA)
			flickerRGBA(coneLight, flickerFactorR, flickerFactorG, flickerFactorB, flickerFactorA);
		else if(pulse)
			pulse(coneLight, pulseFactorA);
		else if(pulseRGBA)
			pulseRGBA(coneLight, pulseFactorR, pulseFactorG, pulseFactorB, pulseFactorA);
		else if(alternate)
			alternate(coneLight, alternateFactor);
	}
	
	public void initialState(MapObject mapObject){  
		super.initialState(mapObject);
		
		//Cone angle
		if(mapObject.getProperties().get("Angle") != null)
			coneAngle = Float.parseFloat((String) mapObject.getProperties().get("Angle"));
		else coneAngle = 45;
		
		nbRay = (int)(coneAngle * 2 * GameConstants.LIGHT_RAY_MULTIPLICATOR);
	}

	@Override
	public void draw(SpriteBatch batch, TextureAtlas textureAtlas){
		batch.setColor(r, g, b, 1);
		if(coneLight.getBody() == null)
			batch.draw(	textureAtlas.findRegion("ConeLight"), 
						coneLight.getX() - width, 
						coneLight.getY() - height,
						width,
						height,
						2 * width,
						2 * height,
						1,
						1,
						positionAngle + 90);	
		else
			batch.draw(	textureAtlas.findRegion("ConeLight"), 
						(float)(coneLight.getBody().getPosition().x - width + position.x*MathUtils.cos(coneLight.getBody().getAngle()) - position.y*MathUtils.sin(coneLight.getBody().getAngle())), 
						(float)(coneLight.getBody().getPosition().y - height + position.x*MathUtils.sin(coneLight.getBody().getAngle()) + position.y*MathUtils.cos(coneLight.getBody().getAngle())),
						width,
						height,
						2 * width,
						2 * height,
						1,
						1,
						coneLight.getBody().getAngle()*MathUtils.radiansToDegrees + positionAngle + 90);	
	}
	
	@Override
	public void dispose(){
		Pools.free(position);
	}
}

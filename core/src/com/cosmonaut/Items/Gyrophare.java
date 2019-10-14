package com.cosmonaut.Items;

import box2dLight.ConeLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;

public class Gyrophare {
	
	final MyGdxGame game;
	protected String[] color;
	private int rotationAngle = 0, speed, nbRay = 50;
	protected float r, g, b, a, angle, distance;
	private ConeLight coneLight, coneLight2;
	private float x, y, size;
	
	public Gyrophare(final MyGdxGame game, MapObject mapObject, RayHandler rayHandler){
		this.game = game;
		x = (mapObject.getProperties().get("x", float.class) + mapObject.getProperties().get("width", float.class)/2) * GameConstants.MPP;
		y = (mapObject.getProperties().get("y", float.class) + 1.5f*mapObject.getProperties().get("height", float.class)) * GameConstants.MPP;
		size = mapObject.getProperties().get("height", float.class)/2 * GameConstants.MPP/4;

		initialState(mapObject);
		Color color = Pools.obtain(Color.class).set(r, g, b, a);
		coneLight = new ConeLight(rayHandler, nbRay, color, distance, x, y, rotationAngle, angle);
        coneLight.setContactFilter((short) 0010, (short)1000, (short)0001);
        Pools.free(color);
        
		//coneLight2 = new ConeLight(rayHandler, 50, new Color(r, g, b, a), distance, x, y, rotationAngle, angle);
        //coneLight2.setContactFilter((short) 0010, (short)1000, (short)0001);
	}
	
	public Gyrophare(final MyGdxGame game){
		this.game = game;
	}
	
	public void init(MapObject mapObject, RayHandler rayHandler){
		rotationAngle = 0;
		nbRay = 50;
		x = (mapObject.getProperties().get("x", float.class) + mapObject.getProperties().get("width", float.class)/2) * GameConstants.MPP;
		y = (mapObject.getProperties().get("y", float.class) + 1.5f*mapObject.getProperties().get("height", float.class)) * GameConstants.MPP;
		size = mapObject.getProperties().get("height", float.class)/2 * GameConstants.MPP/4;

		initialState(mapObject);
		
		Color color = Pools.obtain(Color.class).set(r, g, b, a);
		coneLight = new ConeLight(rayHandler, nbRay, color, distance, x, y, rotationAngle, angle);
        coneLight.setContactFilter((short) 0010, (short)1000, (short)0001);
        Pools.free(color);
	}
	
	public void initialState(MapObject mapObject){  	
    	r = 1;
    	g = 0;
    	b = 0;
    	a = 1;
    	
		//Distance
		if(mapObject.getProperties().get("Distance") != null)
			distance = Float.parseFloat((String) mapObject.getProperties().get("Distance"));
		else distance = 60;
		
		//Color
		if(mapObject.getProperties().get("Color") != null){
			color = mapObject.getProperties().get("Color").toString().split(",");
			if(color.length != 4)
				System.out.println("Erreur dans la définition de la couleur de la lumière");
			else{
				r = Float.valueOf(color[0]);
				g = Float.valueOf(color[1]); 
				b = Float.valueOf(color[2]);
				a = Float.valueOf(color[3]);
			}
		}

		//Cone angle
		if(mapObject.getProperties().get("Angle") != null)
			angle = Float.parseFloat((String) mapObject.getProperties().get("Angle"));
		else angle = 35;
		
		nbRay = (int)(angle * 2 * GameConstants.LIGHT_RAY_MULTIPLICATOR);

		//Speed
		if(mapObject.getProperties().get("Speed") != null)
			speed = Integer.parseInt((String) mapObject.getProperties().get("Speed"));
		else speed = 10;
	}
	
	public void active(){
		rotationAngle += speed;
		coneLight.setDirection(rotationAngle);
		//coneLight2.setDirection(rotationAngle + 180);
	}
	
	public void draw(SpriteBatch batch, TextureAtlas textureAtlas){
		batch.setColor(1, 1, 1, 1);
		batch.draw(	textureAtlas.findRegion("Gyrophare"), 
					x - size, 
					y - size,
					size,
					size,
					2 * size,
					2 * size,
					1,
					1,
					rotationAngle);	
	}
	
	public void dispose(){
		game.pools.free(this);
	}
}

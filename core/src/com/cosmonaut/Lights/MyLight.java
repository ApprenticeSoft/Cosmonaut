package com.cosmonaut.Lights;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.cosmonaut.Bodies.Obstacle;
import com.cosmonaut.Utils.GameConstants;

public class MyLight {

	protected float[] coordinates;
	protected String[] color, flickerColors, pulseColors;
	public Light light;
	public ConeLight coneLight;
	protected boolean flicker = false, pulse = false, flickerRGBA = false, pulseRGBA = false, alternate = false;
	protected int nbRay = 200, flickerFactorR, flickerFactorG, flickerFactorB, flickerFactorA, alternateFactor;
	protected float r, g, b, a, distance, pulseFactorR, pulseFactorG, pulseFactorB, pulseFactorA, red, green, blue, alpha;
	public int associationNumber;
	
	public MyLight(){
	
	}

	public MyLight(PolylineMapObject polylineObject, RayHandler rayHandler){
		calculateCoordinate(polylineObject);
    	initialState(polylineObject);

    	lightDefinition(rayHandler);
	}
	
	public MyLight(PolylineMapObject polylineObject, RayHandler rayHandler, Obstacle obstacle){
		calculateCoordinate(polylineObject);
    	initialState(polylineObject);
    	
    	for(int i = 0; i < coordinates.length; i++){
    		if(i%2 == 0)
    			coordinates[i] = coordinates[i] - obstacle.getX();
    		else
    			coordinates[i] = coordinates[i] - obstacle.getY();
    	} 
    	
    	lightDefinition(rayHandler);
		attachToObstacle(obstacle);
	}
	
	public void lightDefinition(RayHandler rayHandler){
		//Define the light
	}
	
	public void calculateCoordinate(PolylineMapObject polylineObject){
		coordinates = new float[polylineObject.getPolyline().getTransformedVertices().length];
    	for(int i = 0; i < coordinates.length; i++)
    		coordinates[i] = polylineObject.getPolyline().getTransformedVertices()[i]*GameConstants.MPP;
	}
	
	public void attachToObstacle(Obstacle obstacle){
		light.attachToBody(obstacle.body);
	}
	
	public void initialState(MapObject mapObject){  	
    	r = 1;
    	g = 1;
    	b = 1;
    	a = 1;
    	
		//Association Number
		if(mapObject.getProperties().get("Association Number") != null)
			associationNumber = Integer.parseInt((String) mapObject.getProperties().get("Association Number"));
		else associationNumber = 666;

		//Distance
		if(mapObject.getProperties().get("Distance") != null)
			distance = Float.parseFloat((String) mapObject.getProperties().get("Distance"));
		else distance = 20;
		
		//Color
		if(mapObject.getProperties().get("Color") != null && !mapObject.getProperties().get("Color").toString().equals("")){
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

		//Flicker
		if(mapObject.getProperties().get("Flicker") != null && !mapObject.getProperties().get("Flicker").toString().equals("")){
			flickerColors = mapObject.getProperties().get("Flicker").toString().split(",");
			if(flickerColors.length == 1){
				flicker = true;
				flickerFactorA = Integer.parseInt(mapObject.getProperties().get("Flicker").toString());
			}
			else if(flickerColors.length == 4){
				flickerRGBA = true;
				flickerFactorR = Integer.valueOf(flickerColors[0]);
				flickerFactorG = Integer.valueOf(flickerColors[1]);
				flickerFactorB = Integer.valueOf(flickerColors[2]);
				flickerFactorA = Integer.valueOf(flickerColors[3]);
			}
			else
				System.out.println("Erreur dans la définition du clignotement de la lumière");
		}
		else {
			flicker = false;
			flickerRGBA = false;
		}
		
		//Pulse
		if(mapObject.getProperties().get("Pulse") != null && !mapObject.getProperties().get("Pulse").toString().equals("")){
			pulseColors = mapObject.getProperties().get("Pulse").toString().split(",");
			if(pulseColors.length == 1){
				pulse = true;
				pulseFactorA = Float.parseFloat(mapObject.getProperties().get("Pulse").toString());
			}
			else if(pulseColors.length == 4){
				pulseRGBA = true;
				pulseFactorR = Float.valueOf(pulseColors[0]);
				pulseFactorG = Float.valueOf(pulseColors[1]);
				pulseFactorB = Float.valueOf(pulseColors[2]);
				pulseFactorA = Float.valueOf(pulseColors[3]);
			}
			else
				System.out.println("Erreur dans la définition de la pulsation de la lumière");
		}
		else pulse = false;
		
		//Alternate
		if(mapObject.getProperties().get("Alternate") != null && !mapObject.getProperties().get("Alternate").toString().equals("")){
			alternate = true;
			alternateFactor = Integer.parseInt(mapObject.getProperties().get("Alternate").toString());
		}
		else alternate = false;
	}
	
	public void active(){
		if(flicker)
			flicker(light, flickerFactorA);
		else if(flickerRGBA)
			flickerRGBA(light, flickerFactorR, flickerFactorG, flickerFactorB, flickerFactorA);
		else if(pulse)
			pulse(light, pulseFactorA);
		else if(pulseRGBA)
			pulseRGBA(light, pulseFactorR, pulseFactorG, pulseFactorB, pulseFactorA);
		else if(alternate)
			alternate(light, alternateFactor);
	}
	
	public void flicker(Light light, int flickerFactorA){
		int alpha = MathUtils.random(0,100);		
		if(alpha < flickerFactorA)
			light.setColor(r, g, b, 0);
		else
			light.setColor(r, g, b, a);	
	}
	
	public void flickerRGBA(Light light, int flickerFactorR, int flickerFactorG, int flickerFactorB, int flickerFactorA){
		red = MathUtils.random(0,100);
		green = MathUtils.random(0,100);
		blue = MathUtils.random(0,100);
		alpha = MathUtils.random(0,100);		
		
		if(alpha < flickerFactorA)
			alpha = 0;
		else alpha = a;	
		if(red < flickerFactorR)
			red = 0;
		else red = r;	
		if(green < flickerFactorG)
			green = 0;
		else green = g;	
		if(blue < flickerFactorB)
			blue = 0;
		else blue = b;
		
		light.setColor(red, green, blue, alpha);	
	}
	
	public void pulse(Light light, float pulseFactor){
		a += pulseFactor * Gdx.graphics.getDeltaTime();		
		light.setColor(r, g, b, (float)(1 + MathUtils.cos(a))/2);	
	}
	
	public void pulseRGBA(Light light, float pulseFactorR, float pulseFactorG, float pulseFactorB, float pulseFactorA){
		r += pulseFactorR * Gdx.graphics.getDeltaTime();	
		g += pulseFactorG * Gdx.graphics.getDeltaTime();	
		b += pulseFactorB * Gdx.graphics.getDeltaTime();	
		a += pulseFactorA * Gdx.graphics.getDeltaTime();		
		light.setColor((float)(1 + MathUtils.cos(r))/2, (float)(1 + MathUtils.cos(g))/2, (float)(1 + MathUtils.cos(b))/2, (float)(1 + MathUtils.cos(a))/2);
	}
	
	public void alternate(Light light, int alternateFactor){
		int alpha = MathUtils.random(0,100);		
		if(alpha < alternateFactor)
			light.setActive(!light.isActive());
	}
	
	public void draw(SpriteBatch batch, TextureAtlas textureAtlas){
		
	}
	
	public void dispose(){
	}
}

package com.cosmonaut.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.Bodies.Hero;

public class MyCamera extends OrthographicCamera{
	
	private float posX, posY, speed, angleCamera, angleHero;
	private int introductionStep = 0;
	private Vector3 previousPos, interpolatedPosition3;
	private Vector2 distance, interpolatedPosition2;
	
	public MyCamera(){
		super();		
		GameConstants.LEVEL_INTRO = true;
		distance = Pools.obtain(Vector2.class);
		interpolatedPosition2 = Pools.obtain(Vector2.class);
		interpolatedPosition3 = Pools.obtain(Vector3.class);
		previousPos = Pools.obtain(Vector3.class);
	}
	
	public void displacement(Hero hero, TiledMapReader tiledMapReader, TiledMap tiledMap){
		if(GameConstants.TUTORIAL){
			
		}
		else if(GameConstants.LEVEL_INTRO){
			introduction(hero, tiledMapReader, tiledMap);
			//introductionTest(hero, tiledMapReader, tiledMap);
		}
		else{
			previousPos.set(position);
			positionToHero(hero, tiledMap);	
			cameraZoom();
			
			distance.set(previousPos.x - position.x, previousPos.y - position.y);
			speed = distance.len2()/(Gdx.graphics.getDeltaTime() * Gdx.graphics.getDeltaTime());
		}
	}
	
	public void introduction(Hero hero, TiledMapReader tiledMapReader, TiledMap tiledMap){
		previousPos.set(position);
		
		//Different steps of the camera motion
		if(introductionStep == 0){
			positionToHero(hero, tiledMap);
		}
		else if(introductionStep == tiledMapReader.cameraPath.length + 1){
			positionToHero(hero, tiledMap);
		}		
		else {
			for(int i = 0; i < tiledMapReader.cameraPath.length; i++){
				if(i == introductionStep - 1){
					positionToCoordinate(tiledMapReader.cameraPath[i], tiledMap);
				}
			}
		}

		distance.set(previousPos.x - position.x, previousPos.y - position.y);
		speed = distance.len2()/(Gdx.graphics.getDeltaTime() * Gdx.graphics.getDeltaTime());
		
		//Condition to go to the next step	
		if(speed < 1.5f){
			introductionStep++;
			//End of the intro
			if(introductionStep > tiledMapReader.cameraPath.length + 1)
				GameConstants.LEVEL_INTRO = false;
		}
		
		//Skip the intro
		if(Gdx.input.isTouched())
			GameConstants.LEVEL_INTRO = false;
		else if(Gdx.input.isKeyPressed(Keys.ANY_KEY))
			GameConstants.LEVEL_INTRO = false;
	}
	
	public void introductionTest(Hero hero, TiledMapReader tiledMapReader, TiledMap tiledMap){
		previousPos.set(position);
		
		//Different steps of the camera motion
		if(introductionStep == 0){
			positionToHero(hero, tiledMap);
		}
		else if(introductionStep == tiledMapReader.cameraPath.length + 1){
			positionToHero(hero, tiledMap);
		}		
		else {
			for(int i = 0; i < tiledMapReader.cameraPath.length; i++){
				if(i == introductionStep - 1){
					interpolatedPosition2.set(tiledMapReader.cameraPath[introductionStep - 1].x - position.x, tiledMapReader.cameraPath[introductionStep - 1].y - position.y);
					translate(interpolatedPosition2.clamp(0.55f, 0.55f));
				}
			}
		}

		distance.set(previousPos.x - position.x, previousPos.y - position.y);
		speed = distance.len2()/(Gdx.graphics.getDeltaTime() * Gdx.graphics.getDeltaTime());
		
	
		if(introductionStep == 0){
			if(speed < 1.5f)	introductionStep++;
		}
		else{
			if(	position.x < (tiledMapReader.cameraPath[introductionStep - 1].x + 2*GameConstants.MPT) &&
				position.x > (tiledMapReader.cameraPath[introductionStep - 1].x - 2*GameConstants.MPT) &&
				position.y < (tiledMapReader.cameraPath[introductionStep - 1].y + 2*GameConstants.MPT) &&
				position.y > (tiledMapReader.cameraPath[introductionStep - 1].y - 2*GameConstants.MPT)){
				introductionStep++;
				//End of the intro
				if(introductionStep >= tiledMapReader.cameraPath.length + 1){
					GameConstants.LEVEL_INTRO = false;
				}
			}
		}
		
		//Skip the intro
		if(Gdx.input.isTouched())
			GameConstants.LEVEL_INTRO = false;
		else if(Gdx.input.isKeyPressed(Keys.ANY_KEY))
			GameConstants.LEVEL_INTRO = false;
	}
	
	public void positionToHero(Hero hero, TiledMap tiledMap){
		//Positioning relative to the hero
		if(this.position.x < hero.getX() - Gdx.graphics.getWidth() * GameConstants.MPP/10)
			posX = hero.getX() - Gdx.graphics.getWidth() * GameConstants.MPP/10;
		else if(this.position.x > hero.getX() + Gdx.graphics.getWidth() * GameConstants.MPP/10)
			posX = hero.getX() + Gdx.graphics.getWidth() * GameConstants.MPP/10;
		if(this.position.y < hero.getY() - Gdx.graphics.getHeight() * GameConstants.MPP/10)
			posY = hero.getY() - Gdx.graphics.getHeight() * GameConstants.MPP/10;
		else if(this.position.y > hero.getY() + Gdx.graphics.getHeight() * GameConstants.MPP/10)
			posY = hero.getY() + Gdx.graphics.getHeight() * GameConstants.MPP/10;
		
		cameraMotion();
		displacementLimit(tiledMap);
	}
	
	public void positionToCoordinate(float coordX, float coordY, TiledMap tiledMap){
		//Positioning relative to a point
		if(this.position.x < coordX - Gdx.graphics.getWidth() * GameConstants.MPP/10)
			posX = coordX - Gdx.graphics.getWidth() * GameConstants.MPP/10;
		else if(this.position.x > coordX + Gdx.graphics.getWidth() * GameConstants.MPP/10)
			posX = coordX + Gdx.graphics.getWidth() * GameConstants.MPP/10;
		if(this.position.y < coordY - Gdx.graphics.getHeight() * GameConstants.MPP/10)
			posY = coordY - Gdx.graphics.getHeight() * GameConstants.MPP/10;
		else if(this.position.y > coordY + Gdx.graphics.getHeight() * GameConstants.MPP/10)
			posY = coordY + Gdx.graphics.getHeight() * GameConstants.MPP/10;
		
		cameraMotionIntro();
		displacementLimit(tiledMap);
	}
	
	public void positionToCoordinate(Vector2 coordinate, TiledMap tiledMap){
		positionToCoordinate(coordinate.x, coordinate.y, tiledMap);
	}
	
	public void rotation(Hero hero){
		angleHero = hero.heroBody.getAngle() * MathUtils.radiansToDegrees;
		while(angleHero < 0)
			angleHero += 360;
		while(angleHero > 360)
			angleHero -= 360;
		
		angleCamera = (float)Math.atan2(this.up.x, this.up.y)*MathUtils.radiansToDegrees;
		
		this.rotate(- angleCamera - angleHero);
	}
	
	public void cameraZoom(){
		//Zoom-in/Zoom-out
		if (Gdx.input.isKeyPressed(Input.Keys.O)) {
			zoomIn();
        }
		else if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            zoomOut();
        }		
	}
	
	public void zoomOut(){
        viewportWidth *= 1.01f;
        viewportHeight *= 1.01f;
        zoomLimit();
	}
	
	public void zoomIn(){
        viewportWidth *= 0.99f;
        viewportHeight *= 0.99f;
        zoomLimit();
	}
	
	public void cameraZoom(float initialDistance, float distance){
        viewportWidth -= 0.1f*(distance - initialDistance);
        viewportHeight = viewportWidth*GameConstants.SCREEN_RATIO;
        zoomLimit();
	}
	
	public void zoomLimit(){
		if(viewportWidth > /*GameConstants.LEVEL_PIXEL_WIDTH*/ (1 + 0.9f * GameConstants.OVER_CACHE) * GameConstants.SCREEN_WIDTH){
			viewportWidth = /*GameConstants.LEVEL_PIXEL_WIDTH*/ (1 + 0.9f * GameConstants.OVER_CACHE) * GameConstants.SCREEN_WIDTH;
			viewportHeight = viewportWidth * GameConstants.SCREEN_RATIO;
		}
		else if(viewportWidth < GameConstants.SCREEN_WIDTH/2){
			viewportWidth = GameConstants.SCREEN_WIDTH/2;
			viewportHeight = viewportWidth * GameConstants.SCREEN_RATIO;
		}
		else if(viewportHeight > /*GameConstants.LEVEL_PIXEL_HEIGHT*/(1 + 0.9f * GameConstants.OVER_CACHE) * GameConstants.SCREEN_HEIGHT){
			viewportHeight = /*GameConstants.LEVEL_PIXEL_HEIGHT*/(1 + 0.9f * GameConstants.OVER_CACHE) * GameConstants.SCREEN_HEIGHT;
			viewportWidth = viewportHeight / GameConstants.SCREEN_RATIO;
		}
		else if(viewportHeight < GameConstants.SCREEN_HEIGHT/2){
			viewportHeight = GameConstants.SCREEN_HEIGHT/2;
			viewportWidth = viewportHeight / GameConstants.SCREEN_RATIO;
		}
	}
	
	public void displacementLimit(TiledMap tiledMap){
		//Positioning relative to the level map limits
		if(position.x + viewportWidth/2 > GameConstants.LEVEL_PIXEL_WIDTH)
			position.set(GameConstants.LEVEL_PIXEL_WIDTH - viewportWidth/2, position.y, 0);
		else if(position.x - viewportWidth/2 < 0)
			position.set(viewportWidth/2, position.y, 0);
		if(position.y + viewportHeight/2 > GameConstants.LEVEL_PIXEL_HEIGHT)
			position.set(position.x, GameConstants.LEVEL_PIXEL_HEIGHT - viewportHeight/2, 0);
		else if(position.y - viewportHeight/2 < 0)
			position.set(position.x, viewportHeight/2, 0);	
	}
	
	public void cameraMotion(){
		//Camera smooth motion
		interpolatedPosition3.set(posX,posY,0);
		this.position.interpolate(interpolatedPosition3, 0.45f, Interpolation.fade);
	}
	
	public void cameraMotionIntro(){
		//Camera smooth motion for the level intro
		interpolatedPosition3.set(posX,posY,0);
		this.position.interpolate(interpolatedPosition3, 0.13f, Interpolation.fade);
	}
	
	public void dispose(){
		Pools.free(distance);
		Pools.free(interpolatedPosition2);
		Pools.free(interpolatedPosition3);
		Pools.free(previousPos);
	}
}

package com.cosmonaut.Utils;

import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Bodies.Hero;

public class MyGestureListener implements GestureListener{

	final MyGdxGame game;
	float X, Y;
	World world;
	Hero hero;
	MyCamera camera;
	private float zoomInitialDistance;
	private float zoomDistance, previousZoomDistance;
	
	//Detection du geste zoom
	private long contactTime1, contactTime2;
	private int contactInterval;
	private boolean firstContact = true, zoomActif = false;
	
	public MyGestureListener(final MyGdxGame game, World world, MyCamera camera, Hero hero){
		this.game = game;
		this.world = world;
		this.camera = camera;
		this.hero = hero;

		zoomInitialDistance = 0;
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {			
		if(firstContact){
			contactTime1 = TimeUtils.nanoTime();
			firstContact = !firstContact;
		}
		else{
			contactTime2 = TimeUtils.nanoTime();
			firstContact = !firstContact;
		}
		
		contactInterval = Math.abs((int)((contactTime2 - contactTime1)/1000000));
		
		if(contactInterval < 70)
			zoomActif = true;
		else
			zoomActif = false;
		
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		/*
		if(GameConstants.GAME_CONTROLS == GameConstants.ANDROID_GESTURE_CONTROLS && x <= Gdx.graphics.getWidth()/2){
			if(deltaX > 0)
				hero.rotateCounterClockwise();
			else if(deltaX < 0)
				hero.rotateClockwise();
		}
		*/
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		/*
		if(GameConstants.GAME_CONTROLS == GameConstants.ANDROID_GESTURE_CONTROLS){
			hero.heroBody.setAngularVelocity(0);
		}
		*/
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		if(zoomActif){
			if(zoomInitialDistance != initialDistance){
				zoomInitialDistance = initialDistance;
				previousZoomDistance = initialDistance;
			}
			else{
				zoomDistance = distance;
				camera.cameraZoom(previousZoomDistance, zoomDistance);
				previousZoomDistance = zoomDistance;
			}
		}
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}

}

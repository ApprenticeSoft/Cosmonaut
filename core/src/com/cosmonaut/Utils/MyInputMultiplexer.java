package com.cosmonaut.Utils;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Bodies.Hero;

public class MyInputMultiplexer extends InputMultiplexer{

	//Detection du geste zoom
	private long contactTime1, contactTime2;
	private int contactInterval;
	private GestureDetector gestureDetectorLeft, gestureDetectorRight;
	
	public MyInputMultiplexer(final MyGdxGame game, World world, MyCamera camera, Hero hero){
		gestureDetectorLeft = new GestureDetector(new MyGestureListener(game, world, camera, hero));
		gestureDetectorRight = new GestureDetector(new MyGestureListener(game, world, camera, hero));
		
	}

	public MyInputMultiplexer(){
		
	}
	
	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		for (int i = 0, n = getProcessors().size; i < n; i++){
			if (getProcessors().get(i).touchDown(screenX, screenY, pointer, button)) return true;
			//System.out.println(getProcessors().get(i).getClass());

			if(GameConstants.FIRST_CONTACT){
				contactTime1 = TimeUtils.nanoTime();
				GameConstants.FIRST_CONTACT = !GameConstants.FIRST_CONTACT;
			}
			else{
				contactTime2 = TimeUtils.nanoTime();
				GameConstants.FIRST_CONTACT = !GameConstants.FIRST_CONTACT;
			}
			
			contactInterval = Math.abs((int)((contactTime2 - contactTime1)/1000000));
			
			if(contactInterval < 70)
				GameConstants.ZOOM_ACTIF = true;
			else{
				GameConstants.ZOOM_ACTIF = false;
				
				/*
				if(screenX < Gdx.graphics.getWidth()/2){
					if (gestureDetectorLeft.touchDown(screenX, screenY, pointer, button)) return true;
				}
				else
					if (gestureDetectorRight.touchDown(screenX, screenY, pointer, button)) return true;
				*/
			}

		}
		return false;
	}
}

package com.cosmonaut.Bodies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;

public class CheckPoint extends Obstacle{

	private int referenceNumber;
	public boolean used = false;
	
	public CheckPoint(MyGdxGame game, World world, OrthographicCamera camera, MapObject rectangleObject) {
		super(game, world, camera, rectangleObject);

		categoryBits = 0010;
		
		create(world, camera, rectangleObject);
		
		body.getFixtureList().get(0).setSensor(true);
		body.getFixtureList().get(0).setUserData("Checkpoint");
		body.setUserData("Checkpoint");
		
		//Reference Number
		if(rectangleObject.getProperties().get("Number") != null){
			referenceNumber = Integer.parseInt((String) rectangleObject.getProperties().get("Number"));
		}
	}
	
	@Override
	public void activate(){
		if(!used){
			used = true;
			GameConstants.CHECKPOINT = referenceNumber;
		}
	}
	
	@Override
	public void active(Hero hero){

	}
	
	public int getReferenceNumber(){
		return referenceNumber;
	}

	
}

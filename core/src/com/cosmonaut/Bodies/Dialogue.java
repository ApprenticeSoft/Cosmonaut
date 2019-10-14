package com.cosmonaut.Bodies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.cosmonaut.MyGdxGame;

public class Dialogue extends Obstacle{
	
	private int paragraphNumber = 1, lineNumber = 1, referenceNumber;
	private String fileName;
	public boolean singleUse = false, used = false;

	public Dialogue(MyGdxGame game, World world, OrthographicCamera camera, MapObject rectangleObject) {
		super(game, world, camera, rectangleObject);

		categoryBits = 0010;
		
		create(world, camera, rectangleObject);
		
		body.getFixtureList().get(0).setSensor(true);
		body.getFixtureList().get(0).setUserData("Dialogue");
		body.setUserData("Dialogue");

		active = false;
		
		//Reference Number
		if(rectangleObject.getProperties().get("Number") != null){
			referenceNumber = Integer.parseInt((String) rectangleObject.getProperties().get("Number"));
		}
		
		//Paragraph Number
		if(rectangleObject.getProperties().get("Paragraph") != null){
			paragraphNumber = Integer.parseInt((String) rectangleObject.getProperties().get("Paragraph"));
		}
		
		//Line Number
		if(rectangleObject.getProperties().get("Line") != null){
			lineNumber = Integer.parseInt((String) rectangleObject.getProperties().get("Line"));
		}
		
		//File Name
		if(rectangleObject.getProperties().get("File") != null){
			fileName = (String) rectangleObject.getProperties().get("File");
		}
		
		//Is the switch on ?
		if(rectangleObject.getProperties().get("SingleUse") != null){
			singleUse = Boolean.parseBoolean((String) rectangleObject.getProperties().get("SingleUse"));
		}
	}
	
	@Override
	public void activate(){
		active = true;
		System.out.println("Le dialogue est activé !");
		
		if(singleUse)
			used = true;
	}
	
	@Override
	public void active(Hero hero){

	}
	
	public int getReferenceNumber(){
		return referenceNumber;
	}

}

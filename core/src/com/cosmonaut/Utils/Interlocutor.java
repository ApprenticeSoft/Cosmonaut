package com.cosmonaut.Utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;

public class Interlocutor {

	private String name;
	private Vector2 position;
	
	public Interlocutor(String name, Vector2 position){
		this.name = name;
		this.position = position;
	}
	
	public Interlocutor(String name, float posX, float posY){
		this.name = name;
		position = Pools.obtain(Vector2.class).set(posX, posY);
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setPosition(Vector2 position){
		this.position = position;
	}
	
	public void setPosition(float posX, float posY){
		position.set(posX, posY);
	}
	
	public Vector2 getPosition(){
		return position;
	}
	
	public void dispose(){
		Pools.free(position);
	}
}

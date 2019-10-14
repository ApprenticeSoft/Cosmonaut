package com.cosmonaut.Bodies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.cosmonaut.MyGdxGame;

public class Wall extends Obstacle{

	public Wall(final MyGdxGame game, World world, OrthographicCamera camera, MapObject rectangleObject, TextureAtlas textureAtlas) {
		super(game, world, camera, rectangleObject, textureAtlas);	
		create(world, camera, rectangleObject);
	}
	
	public Wall(final MyGdxGame game){
		super(game);
	}
	
	@Override
	public void dispose(){
		game.pools.free(this);
	}
}

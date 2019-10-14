package com.cosmonaut.Bodies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.cosmonaut.MyGdxGame;

public class ObstacleGlass extends Obstacle{

	public ObstacleGlass(MyGdxGame game, World world, OrthographicCamera camera, MapObject rectangleObject) {
		super(game, world, camera, rectangleObject);

		categoryBits = 0010;
		stringTextureRegion = "WhiteSquare";
	
		create(world, camera, rectangleObject);
					
		body.getFixtureList().get(0).setUserData("Glass");
		body.setUserData("Glass");
	}
	
	@Override
	public void draw(SpriteBatch batch, TextureAtlas textureAtlas){		
		batch.setColor(67/256f, 159/256f, 217/256f, 0.3f);
		batch.draw(	textureAtlas.findRegion(stringTextureRegion), 
					this.body.getPosition().x - width, 
					this.body.getPosition().y - height,
					2 * width,
					2 * height);	
		batch.setColor(1, 1, 1, 1);
	}
}

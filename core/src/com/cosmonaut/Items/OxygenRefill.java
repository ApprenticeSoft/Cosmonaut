package com.cosmonaut.Items;

import box2dLight.RayHandler;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Bodies.Hero;
import com.cosmonaut.Utils.GameConstants;

public class OxygenRefill extends Item{
	
	private static Hero hero;
	
	public OxygenRefill(final MyGdxGame game, World world, MapObject mapObject, RayHandler rayHandler, Hero hero){
		super(game);
		this.hero = hero;
		
		stringTextureRegion = "OxygenRefill";
		sound = game.assets.get("Sounds/Oxygen Refill.ogg", Sound.class);
		lightColor.set(0, 0, 1, 0.42f);
		
		create(world, mapObject, rayHandler);		
	}
	
	public OxygenRefill(final MyGdxGame game){
		super(game);
		stringTextureRegion = "OxygenRefill";
		sound = game.assets.get("Sounds/Oxygen Refill.ogg", Sound.class); 
	}
	
	public void init(World world, MapObject mapObject, RayHandler rayHandler, Hero hero){
		this.hero = hero;
		categoryBits = 0001;
		lightColor.set(0, 0, 1, 0.42f);
		super.init(world, mapObject, rayHandler);
		light.setActive(true);
	}
	
	@Override
	public void activate(){
		used = true;
		sound.play();
		light.setActive(false);
		
		hero.setOxygenLevel(hero.getOxygenLevel() + GameConstants.OXYGEN_REFILL);
		
		if(hero.getOxygenLevel() > GameConstants.MAX_OXYGEN)
			hero.setOxygenLevel(GameConstants.MAX_OXYGEN);
	}
	
	@Override
	public void dispose(){
		game.pools.free(this);
	}
}

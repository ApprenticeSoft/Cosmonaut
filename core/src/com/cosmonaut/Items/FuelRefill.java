package com.cosmonaut.Items;

import box2dLight.RayHandler;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Bodies.Hero;
import com.cosmonaut.Utils.GameConstants;

public class FuelRefill extends Item{

	private static Hero hero;
	
	public FuelRefill(final MyGdxGame game, World world, MapObject mapObject, RayHandler rayHandler, Hero hero){
		super(game);
		this.hero = hero;

		stringTextureRegion = "FuelRefill";
		sound = game.assets.get("Sounds/Fuel Refill.ogg", Sound.class); 
		lightColor.set(1, 0, 0, 0.42f);
		
		create(world, mapObject, rayHandler);	
	}
	
	public FuelRefill(final MyGdxGame game){
		super(game);
		stringTextureRegion = "FuelRefill";
		sound = game.assets.get("Sounds/Fuel Refill.ogg", Sound.class); 
	}
	
	public void init(World world, MapObject mapObject, RayHandler rayHandler, Hero hero){
		this.hero = hero;
		categoryBits = 0001;
		lightColor.set(1, 0, 0, 0.42f);
		super.init(world, mapObject, rayHandler);
		light.setActive(true);
	}
	
	@Override
	public void activate(){
		used = true;
		sound.play();
		light.setActive(false);
		
		hero.setFuelLevel(hero.getFuelLevel() + GameConstants.FUEL_REFILL);
		
		if(hero.getFuelLevel() > GameConstants.MAX_FUEL)
			hero.setFuelLevel(GameConstants.MAX_FUEL);
	}
	
	@Override
	public void dispose(){
		game.pools.free(this);
	}
	

}

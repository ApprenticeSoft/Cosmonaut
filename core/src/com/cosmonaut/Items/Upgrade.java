package com.cosmonaut.Items;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;
import com.cosmonaut.Utils.TiledMapReader;

public class Upgrade extends Item{
	
	private int ID;
	private float animTime = 0, spriteWidth, spriteHeight;
	private Animation upgradeAnimation;
	private TextureAtlas upgradeAtlas;
	
	public Upgrade(final MyGdxGame game, World world, MapObject mapObject, RayHandler rayHandler){
		super(game);
		categoryBits = 0010;
		ratio = 1;
		stringTextureRegion = "Upgrade";
		sound = game.assets.get("Sounds/Upgrade.ogg", Sound.class);
		lightColor.set(14/256f, 151/256f, 237/256f, 0.98f);

        upgradeAtlas = game.assets.get("Images/Animations/Upgrade_Animation.pack", TextureAtlas.class);
		upgradeAnimation = new Animation(0.08f, upgradeAtlas.findRegions("Upgrade_Animation"), Animation.PlayMode.LOOP);
		
		if(mapObject.getProperties().get("ID") != null)
			ID = Integer.parseInt((String) mapObject.getProperties().get("ID"));

		create(world, mapObject, rayHandler);

        spriteWidth = 3 * width;
        spriteHeight = spriteWidth * upgradeAnimation.getKeyFrame(0, true).getRegionHeight() / upgradeAnimation.getKeyFrame(0, true).getRegionWidth();
	}
	
	public Upgrade(final MyGdxGame game){
		super(game);
		ratio = 1;
		stringTextureRegion = "Upgrade";
		sound = game.assets.get("Sounds/Upgrade.ogg", Sound.class);
		lightColor.set(14/256f, 151/256f, 237/256f, 0.98f);	
		
        upgradeAtlas = game.assets.get("Images/Animations/Upgrade_Animation.pack", TextureAtlas.class);
		upgradeAnimation = new Animation(0.08f, upgradeAtlas.findRegions("Upgrade_Animation"), Animation.PlayMode.LOOP);
	}
	
	@Override
	public void init(World world, MapObject mapObject, RayHandler rayHandler){
		categoryBits = 0010;
		lightColor.set(14/256f, 151/256f, 237/256f, 0.98f);	
		
		if(mapObject.getProperties().get("ID") != null)
			ID = Integer.parseInt((String) mapObject.getProperties().get("ID"));

		create(world, mapObject, rayHandler);

        spriteWidth = 3 * width;
        spriteHeight = spriteWidth * upgradeAnimation.getKeyFrame(0, true).getRegionHeight() / upgradeAnimation.getKeyFrame(0, true).getRegionWidth();
		light.setActive(true);
	}
	
	@Override
	public void activate(){
		used = true;
		sound.play();
		light.setActive(false);
		GameConstants.UPGRADE_POINT++;
		
		if(ID == 1)
			GameConstants.UPGRADE_1 = true;
		else if(ID == 2)
			GameConstants.UPGRADE_2 = true;
		else if(ID == 3)
			GameConstants.UPGRADE_3 = true;
	}

	public void active(TiledMapReader tiledMapReader){
		super.active(tiledMapReader);
		animTime += Gdx.graphics.getDeltaTime();
	}

	@Override
	public void draw(SpriteBatch batch, TextureAtlas textureAtlas){			
		batch.draw(	upgradeAnimation.getKeyFrame(animTime), 
					this.body.getPosition().x - spriteWidth/2, 
					this.body.getPosition().y - spriteHeight/2,
	    			spriteWidth, 
					spriteHeight);
	}
	
	public int getID(){
		return ID;
	}
	
	public void dispose(){
		//light.dispose();
		game.pools.free(this);
	}
}

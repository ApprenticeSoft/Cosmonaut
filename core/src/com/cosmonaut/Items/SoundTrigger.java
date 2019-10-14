package com.cosmonaut.Items;

import net.dermetfan.utils.math.MathUtils;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;
import com.cosmonaut.Utils.TiledMapReader;

public class SoundTrigger extends Item{

	final MyGdxGame game;
	private Music music;
	private float volume = 0, fading;
	
	public SoundTrigger(final MyGdxGame game, World world, MapObject mapObject, RayHandler rayHandler){
		super(game);
		this.game = game;
		
		if(mapObject.getProperties().get("Fading") != null)
			fading = Float.parseFloat((String) mapObject.getProperties().get("Fading"));
		else fading = 0.1f;
		music = Gdx.audio.newMusic(Gdx.files.internal("Sounds/" + (String) mapObject.getProperties().get("MusicFile") +".ogg"));
		music.setVolume(volume);
		game.musics.add(music);

		create(world, mapObject, rayHandler);	
	}
	
	@Override
	public void create(World world, MapObject mapObject, RayHandler rayHandler){
		this.world = world;
		used = false;
		
		Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
		
		float posX = (rectangle.x + rectangle.width/2) * GameConstants.MPP;
		float posY = (rectangle.y + rectangle.height/2) * GameConstants.MPP;
		width = (rectangle.width/2) * GameConstants.MPP;
		height = (rectangle.height/2) * GameConstants.MPP;
		
		polygonShape = Pools.obtain(PolygonShape.class);
		bodyDef = Pools.obtain(BodyDef.class);	
		fixtureDef = Pools.obtain(FixtureDef.class);
		
		polygonShape.setAsBox(width, height);

		bodyDef.position.set(posX, posY);
    	bodyDef.type = BodyType.StaticBody;
		body = world.createBody(bodyDef);
		
		fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.isSensor =  true;
		
        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef).setUserData("Item");
        body.setUserData("Item"); 

        Pools.free(bodyDef);
        Pools.free(fixtureDef);
        Pools.free(polygonShape);
	}
	
	@Override
	public void activate(){
		//used = !used;
		used = true;
		System.out.println("Activated !!"); 
	}
	
	@Override
	public void active(TiledMapReader tiledMapReader){
		if(used){
			//System.out.println("Play music");
			if(!music.isPlaying())
				music.play();
			
			if(volume < 0.9f)
				music.setVolume(MathUtils.clamp(volume += Gdx.graphics.getDeltaTime()/fading, 0, 0.9f));
		}
		else{
			//System.out.println("Stop music");
			if(music.isPlaying())
				music.stop();
		}
	}
	
	@Override
	public void draw(SpriteBatch batch, TextureAtlas textureAtlas){		
	}
	
}

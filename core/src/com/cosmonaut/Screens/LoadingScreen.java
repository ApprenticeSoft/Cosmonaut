package com.cosmonaut.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;

public class LoadingScreen implements Screen{

	final MyGdxGame game;
	OrthographicCamera camera;
	private Texture textureLogo;
	private Image imageLogo;
	private Stage stage;
	
	//Progress Bar
	private ProgressBar progressBar;
	private ProgressBarStyle progressBarStyle;
	private NinePatchDrawable ninePatchKnob, ninePatchKnobBefore, ninePatchBar;
	
	public LoadingScreen(final MyGdxGame game){
		this.game = game;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		textureLogo = new Texture(Gdx.files.internal("Images/Logo.jpg"), true);
		textureLogo.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.MipMapLinearNearest);
		imageLogo = new Image(textureLogo);
		imageLogo.setWidth(Gdx.graphics.getWidth());
		imageLogo.setHeight(textureLogo.getHeight() * imageLogo.getWidth()/textureLogo.getWidth());
		imageLogo.setX(Gdx.graphics.getWidth()/2 - imageLogo.getWidth()/2);
		imageLogo.setY(Gdx.graphics.getHeight()/2 - imageLogo.getHeight()/2);
		stage = new Stage();
		

		//Progress Bar
		ninePatchBar = new NinePatchDrawable(
												new NinePatch(
																new Texture(Gdx.files.internal("Images/Bar.png"), true), 7, 7, 7, 7));
		ninePatchKnob = new NinePatchDrawable(
												new NinePatch(
																new Texture(Gdx.files.internal("Images/Knob.png"), true), 1, 7, 9, 9));
		ninePatchKnobBefore = new NinePatchDrawable(
													new NinePatch(
																new Texture(Gdx.files.internal("Images/KnobBefore.png"), true), 1, 1, 9, 9));	
		
		progressBarStyle = new ProgressBarStyle(ninePatchBar, ninePatchKnob);
		progressBarStyle.knobBefore = ninePatchKnobBefore;
		//for the knobBefore
		progressBarStyle.knobBefore.setLeftWidth(0);
		progressBarStyle.knobBefore.setRightWidth(0);
		//for the background
		progressBarStyle.background.setLeftWidth(2);
		progressBarStyle.background.setRightWidth(0);
		
		progressBar = new ProgressBar(0, 100, .1f, false, progressBarStyle);
		progressBar.setWidth(Gdx.graphics.getWidth()/3);
		progressBar.setHeight(3*Gdx.graphics.getHeight()/100);
		progressBar.setX(Gdx.graphics.getWidth()/2 - progressBar.getWidth()/2);
		progressBar.setY(Gdx.graphics.getHeight()/5);
		
		//Loading of the sounds
		game.assets.load("Sounds/Alarm.ogg", Sound.class);
		game.assets.load("Sounds/Piston_Motor.ogg", Sound.class);
		game.assets.load("Sounds/Piston_Bang.ogg", Sound.class);
		game.assets.load("Sounds/Electrocution.ogg", Sound.class);
		game.assets.load("Sounds/Jetpack.ogg", Sound.class);
		game.assets.load("Sounds/Impact.ogg", Sound.class);
		game.assets.load("Sounds/Door.ogg", Sound.class);
		game.assets.load("Sounds/Fuel Refill.ogg", Sound.class);
		game.assets.load("Sounds/Oxygen Refill.ogg", Sound.class);
		game.assets.load("Sounds/Button On.ogg", Sound.class);
		game.assets.load("Sounds/Button Off.ogg", Sound.class);
		game.assets.load("Sounds/Exit.ogg", Sound.class);
		game.assets.load("Sounds/Upgrade.ogg", Sound.class);
		game.assets.load("Sounds/Gas Leak.ogg", Sound.class);
		game.assets.load("Sounds/Background.ogg", Music.class);
		
		//Textures
		game.assets.load("Images/Stars.jpg", Texture.class);
		game.assets.load("Images/Barre.png", Texture.class);
		
		//Loading of the TextureAtlas
		game.assets.load("Images/" + GameConstants.SCREEN_RESOLUTION + "/Images.pack", TextureAtlas.class);
		game.assets.load("Images/Animations/Leak_Animation.pack", TextureAtlas.class);
		game.assets.load("Images/Animations/Exit_Animation.pack", TextureAtlas.class);
		game.assets.load("Images/Animations/Exit_End_Animation.pack", TextureAtlas.class);
		game.assets.load("Images/Animations/Upgrade_Animation.pack", TextureAtlas.class);
		game.assets.load("Images/Animations/Fleche_Animation.pack", TextureAtlas.class);
		
		if(Gdx.app.getType() == ApplicationType.Android){
			game.assets.load("Images/Animations/Tom_Animation.pack", TextureAtlas.class);
			game.assets.load("Images/Animations/Controls_Animation.pack", TextureAtlas.class);
			
			if(GameConstants.SELECTED_LEVEL == 1 && !game.levelHandler.isLevelUnlocked(2)){
				game.assets.load("Images/Animations/Rotation_Control_Animation.pack", TextureAtlas.class);
				game.assets.load("Images/Animations/Jetpack_Control_Animation.pack", TextureAtlas.class);
			}
		}
		else{
			game.assets.load("Images/Animations/Tom_Animation_HD.pack", TextureAtlas.class);
			game.assets.load("Images/Desktop_Controls.pack", TextureAtlas.class);
		}
		
		//Loading of the Freetype Fonts
		FileHandleResolver resolver = new InternalFileHandleResolver();
		game.assets.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		game.assets.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
		
		FreeTypeFontLoaderParameter size1Params = new FreeTypeFontLoaderParameter();
		size1Params.fontFileName = "Fonts/good times rg.ttf";			
		size1Params.fontParameters.genMipMaps = true;					
		size1Params.fontParameters.minFilter = TextureFilter.Linear;
		size1Params.fontParameters.magFilter = TextureFilter.Linear;
		size1Params.fontParameters.size = (int)(0.04f*Gdx.graphics.getWidth());
		game.assets.load("fontMenu.ttf", BitmapFont.class, size1Params);
		
		FreeTypeFontLoaderParameter size2Params = new FreeTypeFontLoaderParameter();
		size2Params.fontFileName = "Fonts/good times rg.ttf";			
		size2Params.fontParameters.genMipMaps = true;					
		size2Params.fontParameters.minFilter = TextureFilter.Linear;
		size2Params.fontParameters.magFilter = TextureFilter.Linear;						
		size2Params.fontParameters.size = 24*Gdx.graphics.getWidth()/1000;
		game.assets.load("fontTable.ttf", BitmapFont.class, size2Params);
		
		FreeTypeFontLoaderParameter size3Params = new FreeTypeFontLoaderParameter();
		size3Params.fontFileName = "Fonts/good times rg.ttf";			
		size3Params.fontParameters.genMipMaps = true;					
		size3Params.fontParameters.minFilter = TextureFilter.Linear;
		size3Params.fontParameters.magFilter = TextureFilter.Linear;						
		size3Params.fontParameters.size = 13 * Gdx.graphics.getWidth()/1000 ;
		game.assets.load("fontHUD.ttf", BitmapFont.class, size3Params);
		
		FreeTypeFontLoaderParameter size4Params = new FreeTypeFontLoaderParameter();
		size4Params.fontFileName = "Fonts/good times rg.ttf";			
		size4Params.fontParameters.genMipMaps = true;					
		size4Params.fontParameters.minFilter = TextureFilter.Linear;
		size4Params.fontParameters.magFilter = TextureFilter.Linear;						
		size4Params.fontParameters.size = 19 * Gdx.graphics.getWidth()/1000 ;
		game.assets.load("fontUpgrade.ttf", BitmapFont.class, size4Params);
		
		FreeTypeFontLoaderParameter size5Params = new FreeTypeFontLoaderParameter();
		size5Params.fontFileName = "Fonts/calibri.ttf";			
		size5Params.fontParameters.genMipMaps = true;					
		size5Params.fontParameters.minFilter = TextureFilter.Linear;
		size5Params.fontParameters.magFilter = TextureFilter.Linear;
		if(19 * Gdx.graphics.getWidth()/1000 > 20)
			size5Params.fontParameters.size = 19 * Gdx.graphics.getWidth()/1000;
		else
			size5Params.fontParameters.size = 20;
		game.assets.load("fontDialogue.ttf", BitmapFont.class, size5Params);
		
		FreeTypeFontLoaderParameter size6Params = new FreeTypeFontLoaderParameter();
		size6Params.fontFileName = "Fonts/calibri.ttf";			
		size6Params.fontParameters.genMipMaps = true;					
		size6Params.fontParameters.minFilter = TextureFilter.Linear;
		size6Params.fontParameters.magFilter = TextureFilter.Linear;						
		size6Params.fontParameters.size = 27 * Gdx.graphics.getWidth()/1000 ;
		game.assets.load("fontOption.ttf", BitmapFont.class, size6Params);
		
		FreeTypeFontLoaderParameter size7Params = new FreeTypeFontLoaderParameter();
		size7Params.fontFileName = "Fonts/fonarto.ttf";			
		size7Params.fontParameters.genMipMaps = true;					
		size7Params.fontParameters.minFilter = TextureFilter.Linear;
		size7Params.fontParameters.magFilter = TextureFilter.Linear;						
		size7Params.fontParameters.size = 100 * Gdx.graphics.getWidth()/1000 ;
		game.assets.load("fontCosmonaut.ttf", BitmapFont.class, size7Params);
		
		FreeTypeFontLoaderParameter size8Params = new FreeTypeFontLoaderParameter();
		size8Params.fontFileName = "Fonts/fonarto.ttf";			
		size8Params.fontParameters.genMipMaps = true;					
		size8Params.fontParameters.minFilter = TextureFilter.Linear;
		size8Params.fontParameters.magFilter = TextureFilter.Linear;						
		size8Params.fontParameters.size = 50 * Gdx.graphics.getWidth()/1000 ;
		game.assets.load("fontCredit.ttf", BitmapFont.class, size8Params);
		
		//Tile Map
		game.assets.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		//game.assets.load("Levels/" + GameConstants.SCREEN_RESOLUTION + "/Level " + 1 + ".tmx", TiledMap.class);
	
		stage.addActor(imageLogo);		
		imageLogo.addAction(Actions.sequence(Actions.alpha(0)
                ,Actions.fadeIn(0.1f),Actions.delay(1.5f)));
		
		stage.addActor(progressBar);
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	    
	    camera.update();
		game.batch.setProjectionMatrix(camera.combined);
	
	    stage.act();
	    stage.draw();
	    
	    progressBar.setValue(100*game.assets.getProgress());
	    
		if(game.assets.update()){
			dispose();
			((Game)Gdx.app.getApplicationListener()).setScreen(new HomeScreen(game));	    		
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		stage.dispose();
		textureLogo.dispose();
	}

}

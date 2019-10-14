package com.cosmonaut.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.ShaderUtils.Bloom;
import com.cosmonaut.ShaderUtils.PostProcessor;
import com.cosmonaut.ShaderUtils.ShaderLoader;
import com.cosmonaut.Utils.GameConstants;

public class EndScreen implements Screen{
	
	final MyGdxGame game;
	private OrthographicCamera camera;
	private Skin skin;
	private TextureAtlas atlas;
	private Stage stage;
	private float 	porteWidth, porteHeight, porteX, porteY, 
					porteGaucheX, porteDroiteX, porteHautY, porteBasY,
					murGaucheWidth, murHautHeight, murBasHeight, murDroiteX,
					porteGaucheLimite, porteDroiteLimite, porteHautLimite, porteBasLimite,
					personnageX, personnageY, personnageAngle,
					personnageFaceX, personnageFaceY, personnageFaceWidth, personnageFaceAngle,
					couloirWidth, couloirHeight,
					vaisseauFinWidth, vaisseauFinHeight, vaisseauFinReacteurAlpha = 0, vaisseauFinAngle = 0,
					spaceshipPosX = 0, spaceshipHeight, spaceshipWidth,
					animationSpeed = 40,
					timer = 0, creditTimer = 0, stepTime;
	private int creditLine = 0;
	private Vector2 vaisseauFinPosition;
	private Vector3 colorYellow;
	private TextureRegion regionMurGauche, regionMurDroite, regionMurHaut, regionMurBas;
	
    //Background
    private Texture backgroundTexture;
   	private float backgroundPosX = 0, backgroundPosY = 0, speedFactor = 1;
   	
   	//Credits
   	private Label creditLabel, nameLabel;
   	private LabelStyle creditLabelStyle, creditLabelStyle2, nameLabelStyle;
	
	/*
	 * Test shader
	 */
	//PostProcessor postProcessor;
   	
   	/*******************SHADERS**********************/
    String vertexShader;
    String fragmentShader;
    ShaderProgram shaderProgram, colorReplacementProgram;
    //Vignettage
    float vignettePosX, vignettePosY;
	
	public EndScreen(final MyGdxGame game){
		this.game= game;
		Gdx.input.setCursorCatched(true);

		GameConstants.GAME_FINISHED = false;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		atlas = game.assets.get("Images/Fin/Images_Fin.pack", TextureAtlas.class);
		skin = new Skin(atlas);
		stage = new Stage();
		stage.addActor(game.blackImage);
		
        //Background  
		backgroundTexture = new Texture(Gdx.files.internal("Images/Space.jpg"), true);
		backgroundTexture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.MipMapLinearNearest);
		
		/*
		 * Test shader
		 */
		/*
		ShaderLoader.BasePath = "Shaders/";
        postProcessor = new PostProcessor( false, false, false);
        Bloom bloom = new Bloom( (int)(Gdx.graphics.getWidth() * 0.6f), (int)(Gdx.graphics.getHeight() * 0.6f) );
        postProcessor.addEffect( bloom );
        */
		
		/*
		 * Constants
		 */
		porteHeight = 0.75f*Gdx.graphics.getHeight();
		porteWidth = porteHeight * skin.getRegion("Porte_Gauche").getRegionWidth()/skin.getRegion("Porte_Gauche").getRegionHeight();
		
		porteX = 0.7f*Gdx.graphics.getWidth() - porteWidth/2;
		porteY = 0.5f*Gdx.graphics.getHeight() - porteHeight/2;
		porteGaucheX = porteX;
		porteDroiteX = porteX;
		porteHautY = porteY;
		porteBasY = porteY;
		porteGaucheLimite = porteX - 0.19f*porteWidth; 
		porteDroiteLimite = porteX + 0.19f*porteWidth;  
		porteHautLimite = porteY + 0.23f*porteHeight; 
		porteBasLimite =  porteY - 0.2f*porteHeight;
		
		murGaucheWidth = porteX + 0.05f*porteWidth;
		murDroiteX = porteX + 0.95f*porteWidth;
		murHautHeight = Gdx.graphics.getHeight() - porteY - 0.95f*porteHeight;
		murBasHeight = porteY + 0.05f*porteHeight;
		
		personnageX = porteX - 0.45f*porteWidth;
		personnageY = porteY - 0.5f*porteHeight;
		personnageAngle = 0;
		
		personnageFaceWidth = 0.4f * Gdx.graphics.getWidth();
		personnageFaceX = 0.5f * Gdx.graphics.getWidth() - 0.75f * personnageFaceWidth;
		personnageFaceY = 0.61f * Gdx.graphics.getHeight() - 0.75f * personnageFaceWidth * skin.getRegion("Fin_Personnage_Face").getRegionHeight()/skin.getRegion("Fin_Personnage_Face").getRegionWidth();
		personnageFaceAngle = -5;	
		
		couloirWidth = Gdx.graphics.getWidth();
		couloirHeight = couloirWidth * skin.getRegion("Fin_Couloir2").getRegionHeight()/skin.getRegion("Fin_Couloir2").getRegionWidth();

		spaceshipHeight = 0.66f * Gdx.graphics.getHeight();
		spaceshipWidth = spaceshipHeight * skin.getRegion("Vaisseau_Mere").getRegionWidth()/skin.getRegion("Vaisseau_Mere").getRegionHeight();
		spaceshipPosX = - 0.47f*spaceshipWidth;
		
		vaisseauFinWidth = 0.85f * Gdx.graphics.getWidth();
		vaisseauFinHeight = vaisseauFinWidth * skin.getRegion("Vaisseau_Fin").getRegionHeight()/skin.getRegion("Vaisseau_Fin").getRegionWidth();
		vaisseauFinPosition = Pools.obtain(Vector2.class).set(	-0.25f * Gdx.graphics.getWidth() + vaisseauFinWidth, 
																-0.34f * Gdx.graphics.getHeight() + vaisseauFinHeight);
		
		/*
		 * Murs
		 */    	
      	regionMurGauche = new TextureRegion(skin.getRegion("Fin_Mur"));
      	regionMurGauche.setRegionWidth((int)(regionMurGauche.getRegionWidth() * murGaucheWidth/Gdx.graphics.getWidth()));
      	
      	regionMurDroite = new TextureRegion(skin.getRegion("Fin_Mur"));
      	int regionWidth = (int)(regionMurDroite.getRegionWidth() * (Gdx.graphics.getWidth() - murDroiteX)/Gdx.graphics.getWidth());
      	regionMurDroite.setRegionX(regionMurDroite.getRegionX() + regionMurDroite.getRegionWidth() - regionWidth);
      	regionMurDroite.setRegionWidth(regionWidth);
      	

      	regionMurHaut = new TextureRegion(skin.getRegion("Fin_Mur"));
      	regionMurHaut.setRegionHeight((int)(regionMurHaut.getRegionHeight() * murHautHeight/Gdx.graphics.getHeight()));  	

      	regionMurBas = new TextureRegion(skin.getRegion("Fin_Mur"));
      	int regionHeight = (int)(regionMurBas.getRegionHeight() * (murBasHeight/Gdx.graphics.getHeight()));
      	regionMurBas.setRegionY(regionMurBas.getRegionY() + regionMurBas.getRegionHeight() - regionHeight);
      	regionMurBas.setRegionHeight(regionHeight);
      		
      	/*
      	 * Transition écran noir
      	 */
		game.blackImage.addAction(Actions.alpha(1));
		game.blackImage.addAction(Actions.alpha(0,1));
		
		/*******************TEST SHADERS**********************/
		ShaderProgram.pedantic = false;	//Important pour pouvoir modifier les variables uniformes
      	vertexShader = Gdx.files.internal("Shaders/Vignette-vertex.glsl").readString();
      	fragmentShader = Gdx.files.internal("Shaders/Vignette-fragment.glsl").readString();
      	shaderProgram = new ShaderProgram(vertexShader,fragmentShader);
      	game.batch.setShader(shaderProgram);

      	vignettePosX = 0.7f;
      	vignettePosY = 0.5f;
      	shaderProgram.begin();
      	shaderProgram.setUniformf("u_resolution", camera.viewportWidth, camera.viewportHeight);
      	shaderProgram.setUniformf("u_PosX", vignettePosX);
      	shaderProgram.setUniformf("u_PosY", vignettePosY);
      	shaderProgram.setUniformf("outerRadius", 1.0f);
      	shaderProgram.setUniformf("innerRadius", 0.25f);
      	shaderProgram.setUniformf("intensity", 0.75f);
      	shaderProgram.end();
      	
      	Vector3 colorBlack = Pools.obtain(Vector3.class).set(0, 0, 0);
      	colorReplacementProgram = new ShaderProgram(vertexShader,Gdx.files.internal("Shaders/ColorReplacement-fragment.glsl").readString()); 
      	colorReplacementProgram.begin();
      	colorReplacementProgram.setUniformf("u_output_color", colorBlack);
      	colorReplacementProgram.end();
      	Pools.free(colorBlack);
      	
      	colorYellow = Pools.obtain(Vector3.class).set(0.668f, 0.617f, 0.188f);
      	
      	/*
      	 * Credits
      	 */
      	creditLabelStyle = new LabelStyle(game.assets.get("fontCosmonaut.ttf", BitmapFont.class), Color.WHITE);
      	creditLabelStyle2 = new LabelStyle(game.assets.get("fontOption.ttf", BitmapFont.class), Color.WHITE);
      	nameLabelStyle = new LabelStyle(game.assets.get("fontCredit.ttf", BitmapFont.class), Color.WHITE);
      	creditLabel = new Label("COSMONAUT", creditLabelStyle);
      	creditLabel.setX(Gdx.graphics.getWidth()/2 - creditLabel.getPrefWidth()/2);
      	creditLabel.setY(Gdx.graphics.getHeight()/2 - creditLabel.getPrefHeight()/2);
      	
      	nameLabel = new Label("", nameLabelStyle);
      	nameLabel.setX(Gdx.graphics.getWidth()/2 - nameLabel.getPrefWidth()/2);
      	nameLabel.setY(creditLabel.getY());

      	/*
      	stage.addActor(nameLabel);
      	stage.addActor(creditLabel);
      	nameLabel.debug();
      	creditLabel.debug();
      	*/
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.batch.setProjectionMatrix(camera.combined);
		
		if(game.blackImage.getColor().a > 0.1f)
			openDoor();
		else if (timer < 6){
			timer += Gdx.graphics.getDeltaTime();
			
			doorMotion();
			openDoor();
			
			if(timer >= 6){
				stepTime = timer;
				regionMurGauche.setRegionWidth((int)(regionMurGauche.getRegionWidth() * Gdx.graphics.getWidth()/murGaucheWidth));
				System.out.println("Étape suivante !");
				System.out.println("stepTime = " + stepTime);
			}
		}
		else if (timer < 15){
			timer += Gdx.graphics.getDeltaTime();
			
			//Shader
			vignettePosX = 0.5f;
	      	vignettePosY = 0.5f;
	      	shaderProgram.begin();
	      	shaderProgram.setUniformf("u_resolution", camera.viewportWidth, camera.viewportHeight);
	      	shaderProgram.setUniformf("u_PosX", vignettePosX);
	      	shaderProgram.setUniformf("u_PosY", vignettePosY);
	      	shaderProgram.setUniformf("outerRadius", 0.6f);
	      	shaderProgram.setUniformf("innerRadius", 0.05f);
	      	shaderProgram.setUniformf("intensity", 0.8f);
	      	shaderProgram.end();
	      	
			porteHeight = 0.43f*Gdx.graphics.getHeight();
			porteWidth = porteHeight * skin.getRegion("Porte_Gauche").getRegionWidth()/skin.getRegion("Porte_Gauche").getRegionHeight();
			
			porteX = 0.45f*Gdx.graphics.getWidth() - porteWidth/2;
			porteY = 0.55f*Gdx.graphics.getHeight() - porteHeight/2;
			porteGaucheX = porteX;
			porteDroiteX = porteX;
			porteHautY = porteY;
			porteBasY = porteY;
			porteGaucheLimite = porteX - 0.19f*porteWidth; 
			porteDroiteLimite = porteX + 0.19f*porteWidth;  
			porteHautLimite = porteY + 0.23f*porteHeight; 
			porteBasLimite =  porteY - 0.2f*porteHeight;
			
			enterShip();
			
			if(timer >= 15){
				stepTime = timer;
				System.out.println("stepTime = " + stepTime);
				game.batch.setShader(null);
			}
		}
		else if(timer < 26){
			timer += Gdx.graphics.getDeltaTime();	
			spaceshipMoving();
			
			if(timer >= 26){
				stepTime = timer;
				System.out.println("stepTime = " + stepTime);
				game.batch.setShader(null);
			}
		}
		else if(vaisseauFinPosition.x < 1.1f*Gdx.graphics.getWidth()){
			timer += Gdx.graphics.getDeltaTime();
			escape();

			if(vaisseauFinPosition.x >= 1.1f*Gdx.graphics.getWidth()){
				stepTime = timer;
				vaisseauFinReacteurAlpha = 0;
				System.out.println("stepTime = " + stepTime);
				game.batch.setShader(null);
			}
		}
		else if(creditLine > 7){
			end();
		}
		else {	
			credits();
		}
		
		stage.act();
		stage.draw();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
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
		//postProcessor.rebind();
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}
	
	public void openDoor(){	
		//postProcessor.capture();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.batch.begin();
		//Background
		backgroundPosX -= 8 * Gdx.graphics.getDeltaTime();
		for(int i = 0; i < 3; i++){
		    game.batch.draw(backgroundTexture, 
		    				backgroundPosX + i * Gdx.graphics.getHeight()*backgroundTexture.getWidth()/backgroundTexture.getHeight(), 
		    				0, 
		    				Gdx.graphics.getHeight() * backgroundTexture.getWidth()/backgroundTexture.getHeight(), 
		    				Gdx.graphics.getHeight());
		}
		//Couloir
		game.batch.setColor(0.65f, 0.65f, 0.65f, 1);
		game.batch.draw(skin.getRegion("Fin_Couloir"), 
						porteX, 
						porteY - 0.3f*porteHeight, 
						1.5f*porteHeight * skin.getRegion("Fin_Couloir").getRegionWidth()/skin.getRegion("Fin_Couloir").getRegionHeight(), 
						1.5f*porteHeight);
		game.batch.setColor(1, 1, 1, 1);
		//Porte
		game.batch.draw(skin.getRegion("Porte_Gauche"), 
						porteGaucheX, 
						porteY, 
						porteWidth, 
						porteHeight);
		game.batch.draw(skin.getRegion("Porte_Droite"), 
						porteDroiteX, 
						porteY, 
						porteWidth, 
						porteHeight);
		game.batch.draw(skin.getRegion("Porte_Haut"), 
						porteX, 
						porteHautY, 
						porteWidth, 
						porteHeight);
		game.batch.draw(skin.getRegion("Porte_Bas"), 
						porteX, 
						porteBasY,  
						porteWidth, 
						porteHeight);

		game.batch.setColor(0.6f, 0.65f, 0.65f, 1);
		//Mur gauche
		game.batch.draw(regionMurGauche,
						0,
						0,
						murGaucheWidth, 
						Gdx.graphics.getHeight());
		//Mur droite
		game.batch.draw(regionMurDroite,
						murDroiteX,
						0,
						Gdx.graphics.getWidth() - murDroiteX, 
						Gdx.graphics.getHeight());
		//Mur haut
		game.batch.draw(regionMurHaut,
						0,
						Gdx.graphics.getHeight() - murHautHeight,
						Gdx.graphics.getWidth(),
						murHautHeight);
		//Mur bas
		
		game.batch.draw(regionMurBas,
						0,
						0,
						Gdx.graphics.getWidth(), 
						murBasHeight);
		game.batch.setColor(1, 1, 1, 1);
		//Cadre de porte
		game.batch.draw(skin.getRegion("Porte_Cadre"), 
						porteX, 
						porteY, 
						porteWidth, 
						porteHeight);
		//Personnage
		game.batch.draw(skin.getRegion("Fin_Personnage_Dos"), 
						personnageX += 7*Gdx.graphics.getDeltaTime(), 
						personnageY += 1*Gdx.graphics.getDeltaTime(), 
						0,
						0,
						1.3f*porteHeight * skin.getRegion("Fin_Personnage_Dos").getRegionWidth()/skin.getRegion("Fin_Personnage_Dos").getRegionHeight(), 
						1.3f*porteHeight,
						1,
						1,
						personnageAngle += 0.3f*Gdx.graphics.getDeltaTime());
		game.batch.end();
		//postProcessor.render();
	}
	
	public void doorMotion(){
		if(porteGaucheX > porteGaucheLimite)
			porteGaucheX -= animationSpeed*Gdx.graphics.getDeltaTime();
		else porteGaucheX = porteGaucheLimite;
		if(porteDroiteX < porteDroiteLimite)
			porteDroiteX += animationSpeed*Gdx.graphics.getDeltaTime();
		else porteDroiteX = porteDroiteLimite;
		if(porteHautY < porteHautLimite)
			porteHautY += animationSpeed*Gdx.graphics.getDeltaTime();
		else porteHautY = porteHautLimite;
		if(porteBasY > porteBasLimite)
			porteBasY -= animationSpeed*Gdx.graphics.getDeltaTime();
		else porteBasY = porteBasLimite;
	}
	
	public void enterShip(){
		porteX = 0.355f * couloirWidth;
		porteY = 0.377f * couloirHeight - 0.5f*(couloirHeight - Gdx.graphics.getHeight());
		porteHeight = 0.331f * couloirHeight;
		porteWidth = porteHeight * skin.getRegion("Porte_Ouverte_Fin").getRegionWidth()/skin.getRegion("Porte_Ouverte_Fin").getRegionHeight();
		
		//postProcessor.capture();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		game.batch.begin();
		//Background
		backgroundPosX -= 8 * Gdx.graphics.getDeltaTime();
		for(int i = 0; i < 3; i++){
		    game.batch.draw(backgroundTexture, 
		    				backgroundPosX + i * Gdx.graphics.getHeight()*backgroundTexture.getWidth()/backgroundTexture.getHeight(), 
		    				0, 
		    				Gdx.graphics.getHeight() * backgroundTexture.getWidth()/backgroundTexture.getHeight(), 
		    				Gdx.graphics.getHeight());
		}
		//Couloir
		game.batch.draw(skin.getRegion("Fin_Couloir2"),
						0,
						Gdx.graphics.getHeight()/2 - 0.5f * couloirHeight, 
    					couloirWidth, 
    					couloirHeight);
		game.batch.setColor(0.75f, 0.75f, 0.75f, 1);
		//Porte
		game.batch.draw(skin.getRegion("Porte_Ouverte_Fin"), 
						porteX, 
						porteY, 
						porteWidth, 
						porteHeight);
		game.batch.setColor(1, 1, 1, 1);
		
		//Personnage
		game.batch.draw(skin.getRegion("Fin_Personnage_Face"), 
						personnageFaceX, 
						personnageFaceY, 
						0,
						0,
						personnageFaceWidth += 7*Gdx.graphics.getDeltaTime(), 
						personnageFaceWidth * skin.getRegion("Fin_Personnage_Face").getRegionHeight()/skin.getRegion("Fin_Personnage_Face").getRegionWidth(),
						1,
						1,
						personnageFaceAngle += 0.75f*Gdx.graphics.getDeltaTime());
		
		game.batch.end();
		//postProcessor.render();
	}
	
	public void spaceshipMoving(){
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(timer - stepTime > 4){
			colorReplacementProgram.begin();
	      	colorReplacementProgram.setUniformf("u_output_color", colorYellow);
	      	colorReplacementProgram.end();
		}
		
		game.batch.begin();
		//Background
		backgroundPosX -= 8 * Gdx.graphics.getDeltaTime();
		for(int i = 0; i < 3; i++){
		    game.batch.draw(backgroundTexture, 
		    				backgroundPosX + i * Gdx.graphics.getHeight()*backgroundTexture.getWidth()/backgroundTexture.getHeight(), 
		    				0, 
		    				Gdx.graphics.getHeight() * backgroundTexture.getWidth()/backgroundTexture.getHeight(), 
		    				Gdx.graphics.getHeight());
		}
		
		 game.batch.draw(	skin.getRegion("Vaisseau_Mere"), 
			 				spaceshipPosX += 10 * Gdx.graphics.getDeltaTime(), 
			 				0.3f*Gdx.graphics.getHeight() - 0.5f*spaceshipHeight, 
			 				spaceshipWidth, 
			 				spaceshipHeight);
		 game.batch.setShader(colorReplacementProgram);
		 game.batch.draw(	skin.getRegion("Vaisseau_Survie"), 
		 					spaceshipPosX + 0.65f*spaceshipWidth, 
		 					0.3f*Gdx.graphics.getHeight() + 0.31f*spaceshipHeight, 
		 					0.25f*spaceshipHeight * skin.getRegion("Vaisseau_Survie").getRegionWidth()/skin.getRegion("Vaisseau_Survie").getRegionHeight(), 
		 					0.25f*spaceshipHeight);
		 game.batch.setShader(null);
		 game.batch.end();
	}
	
	public void escape(){
		//postProcessor.capture();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
		if(timer - stepTime > 2)
			vaisseauFinReacteurAlpha = MathUtils.clamp(vaisseauFinReacteurAlpha += 0.42f * Gdx.graphics.getDeltaTime(), 0f , 1f);	
		if(vaisseauFinReacteurAlpha > 0.9f){

			speedFactor = MathUtils.clamp(speedFactor -= 0.11f* Gdx.graphics.getDeltaTime(), 0, 1);	
			backgroundPosY -= speedFactor * speedFactor * Gdx.graphics.getHeight() * Gdx.graphics.getDeltaTime();
			
			Vector2 vaisseauDirection = Pools.obtain(Vector2.class);
			if(vaisseauFinWidth > 0.015f * Gdx.graphics.getWidth()){
				vaisseauFinPosition.add(vaisseauDirection.set(0.1f, 0.03f).setLength(0.001f * vaisseauFinPosition.len()));
				vaisseauFinWidth -= 0.025f * vaisseauFinWidth;
				vaisseauFinHeight -= 0.025f * vaisseauFinHeight;
			}
			else{
				vaisseauFinPosition.add(vaisseauDirection.set(0.1f, 0.03f).setLength(0.0018f * vaisseauFinPosition.len()));
				vaisseauFinWidth -= 0.006f * vaisseauFinWidth;
				vaisseauFinHeight -= 0.006f * vaisseauFinHeight;
			}
			Pools.free(vaisseauDirection);
			
			if(vaisseauFinAngle > -72)
				vaisseauFinAngle -= -0.02f * vaisseauFinAngle +  Gdx.graphics.getDeltaTime();
			else
				vaisseauFinAngle -= 3*Gdx.graphics.getDeltaTime();
		}
		
		game.batch.begin();
		//Background
		backgroundPosX -= 8 * Gdx.graphics.getDeltaTime();
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 4; j++){
			    game.batch.draw(backgroundTexture, 
			    				backgroundPosX + i * Gdx.graphics.getHeight()*backgroundTexture.getWidth()/backgroundTexture.getHeight(), 
			    				backgroundPosY + j * Gdx.graphics.getHeight(), 
			    				Gdx.graphics.getHeight() * backgroundTexture.getWidth()/backgroundTexture.getHeight(), 
			    				Gdx.graphics.getHeight());
			}
		}
		game.batch.draw(skin.getRegion("Surface_Vaisseau"),
						0,
						backgroundPosY,
						Gdx.graphics.getWidth(),
						Gdx.graphics.getWidth() * skin.getRegion("Surface_Vaisseau").getRegionHeight() / skin.getRegion("Surface_Vaisseau").getRegionWidth());
		
		game.batch.draw(skin.getRegion("Vaisseau_Fin"), 
						vaisseauFinPosition.x - vaisseauFinWidth, 
						vaisseauFinPosition.y - vaisseauFinHeight, 
						vaisseauFinWidth/2, 
						vaisseauFinHeight/2,
						vaisseauFinWidth, 
						vaisseauFinHeight,
						1,
						1,
						vaisseauFinAngle);
		
		game.batch.setColor(1, 1, 1, vaisseauFinReacteurAlpha);
		game.batch.draw(skin.getRegion("Vaisseau_Fin_Reacteur"), 
						vaisseauFinPosition.x - vaisseauFinWidth, 
						vaisseauFinPosition.y - vaisseauFinHeight,
						vaisseauFinWidth/2, 
						vaisseauFinHeight/2,
						vaisseauFinWidth, 
						vaisseauFinHeight,
						1,
						1,
						vaisseauFinAngle);
		game.batch.setColor(1, 1, 1, 1);
		game.batch.end();
	}
	
	public void credits(){
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		game.batch.begin();
		//Background
		speedFactor = MathUtils.clamp(speedFactor -= 0.11f* Gdx.graphics.getDeltaTime(), 0, 1);	
		backgroundPosX -= 8 * Gdx.graphics.getDeltaTime();
		backgroundPosY -= speedFactor * speedFactor * Gdx.graphics.getHeight() * Gdx.graphics.getDeltaTime();
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 5; j++){
			    game.batch.draw(backgroundTexture, 
			    				backgroundPosX + i * Gdx.graphics.getHeight()*backgroundTexture.getWidth()/backgroundTexture.getHeight(), 
			    				backgroundPosY + j * Gdx.graphics.getHeight(), 
			    				Gdx.graphics.getHeight() * backgroundTexture.getWidth()/backgroundTexture.getHeight(), 
			    				Gdx.graphics.getHeight());
			}
		}
		
		if(vaisseauFinReacteurAlpha < 1 && creditTimer == 0)
			vaisseauFinReacteurAlpha = MathUtils.clamp(vaisseauFinReacteurAlpha += 0.65f * Gdx.graphics.getDeltaTime(), 0f , 1f);
		else if(vaisseauFinReacteurAlpha == 1){
			creditTimer += Gdx.graphics.getDeltaTime();
			
			if(creditTimer > 4){
				vaisseauFinReacteurAlpha = MathUtils.clamp(vaisseauFinReacteurAlpha -= 0.55f * Gdx.graphics.getDeltaTime(), 0f , 1f);
			}
		}
		else{
			vaisseauFinReacteurAlpha = MathUtils.clamp(vaisseauFinReacteurAlpha -= 0.55f * Gdx.graphics.getDeltaTime(), 0f , 1f);
			
			if(vaisseauFinReacteurAlpha == 0){
				creditTimer = 0;
				creditLine++;
				switch(creditLine){
				case 1:
					creditLabel.setStyle(creditLabelStyle2);
					creditLabel.setText(game.text.get("GameBy"));
					nameLabel.setText("Marc Vidal");
					break;
				case 2:
					creditLabel.setText(game.text.get("CodeBy"));
					nameLabel.setText("Marc Vidal");
					break;
				case 3:
					creditLabel.setText(game.text.get("GraphicsBy"));
					nameLabel.setText("Marc Vidal");
					break;
				case 4:
					creditLabel.setText(game.text.get("ArtBy"));
					nameLabel.setText("Geneviève Milette");
					break;
				case 5:
					creditLabel.setText(game.text.get("SoundsBy"));
					nameLabel.setText("Marc Vidal");
					break;
				case 6:
					creditLabel.setText(game.text.get("MusicBy"));
					nameLabel.setText("Kevin MacLeod\nJaksanapong Tilapornputt\nSpinningMerkaba");
					nameLabel.setWrap(true);
					nameLabel.setAlignment(Align.center);
					break;
				case 7:
					creditLabel.setText(game.text.get("GermanVersionBy"));
					nameLabel.setText("Maximilian Ebert");
					break;
				}
		      	creditLabel.setX(Gdx.graphics.getWidth()/2 - creditLabel.getPrefWidth()/2);
		      	creditLabel.setY(Gdx.graphics.getHeight()/2 - creditLabel.getPrefHeight()/2);
		      	nameLabel.setX(Gdx.graphics.getWidth()/2 - nameLabel.getPrefWidth()/2);
		      	nameLabel.setY(creditLabel.getY() - nameLabel.getPrefHeight()/2 + 0.25f * creditLabel.getPrefHeight()); 	
			}		
		}
			

		creditLabel.draw(game.batch, vaisseauFinReacteurAlpha);
		nameLabel.draw(game.batch, vaisseauFinReacteurAlpha);
		game.batch.end();
	}
	
	public void end(){
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		vaisseauFinReacteurAlpha = MathUtils.clamp(vaisseauFinReacteurAlpha += 0.33f * Gdx.graphics.getDeltaTime(), 0f , 1f);
		for(int i = 0; i < game.musics.size; i++)
			game.musics.get(i).setVolume(MathUtils.clamp(1 - vaisseauFinReacteurAlpha, 0, 0.9f));
		
		game.batch.begin();
		game.batch.setColor(1 - vaisseauFinReacteurAlpha, 1 - vaisseauFinReacteurAlpha, 1 - vaisseauFinReacteurAlpha, 1 - vaisseauFinReacteurAlpha);
		//Background
		speedFactor = MathUtils.clamp(speedFactor -= 0.11f* Gdx.graphics.getDeltaTime(), 0, 1);	
		backgroundPosX -= 8 * Gdx.graphics.getDeltaTime();
		backgroundPosY -= speedFactor * speedFactor * Gdx.graphics.getHeight() * Gdx.graphics.getDeltaTime();
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 5; j++){
			    game.batch.draw(backgroundTexture, 
			    				backgroundPosX + i * Gdx.graphics.getHeight()*backgroundTexture.getWidth()/backgroundTexture.getHeight(), 
			    				backgroundPosY + j * Gdx.graphics.getHeight(), 
			    				Gdx.graphics.getHeight() * backgroundTexture.getWidth()/backgroundTexture.getHeight(), 
			    				Gdx.graphics.getHeight());
			}
		}
		game.batch.end();
		
		if(vaisseauFinReacteurAlpha == 1){
			dispose();
			game.setScreen(new MainMenuScreen(game));
		}
	}
	
	public void stopMusic(){		
		for(int i = 0; i < game.musics.size; i++){
			game.musics.get(i).stop();
			game.musics.removeIndex(i);
		}
	}

	@Override
	public void dispose() {
		//postProcessor.dispose();
		//skin.dispose();
		game.batch.setShader(null);
		game.assets.unload("Images/Fin/Images_Fin.pack");
		for(int i = 0; i < game.musics.size; i++){
			game.musics.get(i).stop();
			game.musics.removeIndex(i);
		}
		Pools.free(vaisseauFinPosition);
		Pools.free(colorYellow);
	}

}

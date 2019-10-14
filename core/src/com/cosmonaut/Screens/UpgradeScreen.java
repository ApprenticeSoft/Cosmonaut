package com.cosmonaut.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.cosmonaut.Data;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;
import com.cosmonaut.Utils.UIWindow;

public class UpgradeScreen implements Screen{

	final MyGdxGame game;
	private Stage stage;
	private Image backgroundImage, imageBuyWindow;
	private Texture backgroundTexture;
	private Label oxygenLabel, fuelLabel, powerLabel, upgradeQuantityLabel, labelDescription, screenTitle;
	private LabelStyle labelStyle, titleLabelStyle, quantityLabelStyle, screenTitleStyle;
	private Array<Image> upgradesOxygen, upgradesFuel, upgradesPower;
	private String stringOxygen, stringFuel, stringPower;
	private int oxygenCost, fuelCost, powerCost;
	private float itemWindowWidth;
	private UIWindow oxygenWindow, fuelWindow, powerWindow, screenTitleWindow;
	private TextButton buyButton;
	private TextButtonStyle textButtonStyle;
	private Button backButton;
	private ButtonGroup<Button> buttonGroup;
	private Color inactiveUpgradeColor, activeUpgradeColor;

	//Animation
	private float animTime = 0, imagePrixPosX, imageUpgradePointX, imageUpgradePointY, imageUpgradePointWidth, imageUpgradePointHeight, prixAlpha;
	private Animation upgradeAnimation;
	private TextureAtlas upgradeAtlas;
	
	public UpgradeScreen(final MyGdxGame game){
		this.game = game;

		stage = new Stage();	
		
		if(Data.getOxygenLevel() < 5)
			prixAlpha = 1;
		else
			prixAlpha = 0;
		
		if(Data.getLanguage().equals("EN"))
			itemWindowWidth = 30*Gdx.graphics.getWidth()/100;
		else
			itemWindowWidth = new GlyphLayout(game.assets.get("fontTable.ttf", BitmapFont.class), game.text.get("Fuel").toUpperCase()).width + 0.2f*Gdx.graphics.getWidth();
		
		Color colorScreenTitle = Pools.obtain(Color.class).set(2/256f, 165/256f, 200/256f, 1);	
		//Titre de l'écran
		screenTitleStyle = new LabelStyle(game.assets.get("fontMenu.ttf", BitmapFont.class), colorScreenTitle);
		screenTitle = new Label(game.text.get("Upgrades"), screenTitleStyle);
		screenTitle.setAlignment(Align.center);
		
		float screenTitleWindowDimension = 0.465f*Gdx.graphics.getWidth();
		screenTitleWindow = new UIWindow(	game.skin.getDrawable("ScreenTitle"), 
											screenTitleWindowDimension, 
											screenTitleWindowDimension * game.skin.getRegion("ScreenTitle").getRegionHeight()/game.skin.getRegion("ScreenTitle").getRegionWidth(),
											Gdx.graphics.getWidth() - screenTitleWindowDimension,
											Gdx.graphics.getHeight() - screenTitleWindowDimension * game.skin.getRegion("ScreenTitle").getRegionHeight()/game.skin.getRegion("ScreenTitle").getRegionWidth());
		screenTitleWindow.addActorRelativeCentered(screenTitle, 0.525f, 0.59f);

		//Icone représentant le nombre de point d'upgrade
			//Animation
		imagePrixPosX = new GlyphLayout(game.assets.get("fontUpgrade.ttf", BitmapFont.class), game.text.get("Cost").toUpperCase() + " : " + oxygenCost + " ").width;
        upgradeAtlas = game.assets.get("Images/Animations/Upgrade_Animation.pack", TextureAtlas.class);
		upgradeAnimation = new Animation(0.08f, upgradeAtlas.findRegions("Upgrade_Animation"), Animation.PlayMode.LOOP);
			//Position et dimensions de l'icone
		imageUpgradePointWidth = 0.05f * Gdx.graphics.getWidth();
		imageUpgradePointHeight = imageUpgradePointWidth * upgradeAnimation.getKeyFrame(0, true).getRegionHeight() / upgradeAnimation.getKeyFrame(0, true).getRegionWidth();
		imageUpgradePointX = 0.02f * Gdx.graphics.getWidth();
		imageUpgradePointY = Gdx.graphics.getHeight() - imageUpgradePointX - imageUpgradePointHeight;
		
		//Gestion des upgrades
		inactiveUpgradeColor = Pools.obtain(Color.class).set(1/256f, 40/256f, 48/256f, 0.75f);
		activeUpgradeColor = Pools.obtain(Color.class).set(224/256f, 208/256f, 25/256f, 1);
		
		//Prix des upgrades
		updatePrices();
		
		if(Data.getOxygenLevel() >= 5)
			stringOxygen = game.text.get("OxygenDescription") + "\n\n" +  game.text.get("Cost") + " : " + game.text.get("Maximum").toUpperCase() + " ";
		else
			stringOxygen = game.text.get("OxygenDescription") + "\n\n" +  game.text.get("Cost") + " : " + oxygenCost + " ";
		if(Data.getFuelLevel() >= 5)
			stringFuel = game.text.get("FuelDescription") + "\n\n" +  game.text.get("Cost") + " : " + game.text.get("Maximum").toUpperCase() + " ";
		else
			stringFuel = game.text.get("FuelDescription") + "\n\n" +  game.text.get("Cost") + " : " + fuelCost + " ";
		if(Data.getPowerLevel() >= 5)
			stringPower = game.text.get("PowerDescription") + "\n\n" +  game.text.get("Cost") + " : " + game.text.get("Maximum").toUpperCase() + " ";
		else
			stringPower = game.text.get("PowerDescription") + "\n\n" +  game.text.get("Cost") + " : " + powerCost + " ";
		
		//Background
		backgroundTexture = new Texture(Gdx.files.internal("Images/LevelScreenBackground.jpg"), true);
		backgroundTexture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.MipMapLinearNearest);
		backgroundImage = new Image(backgroundTexture);
		backgroundImage.setColor(1, 1, 1, 0.7f);
		backgroundImage.setWidth(Gdx.graphics.getWidth());
		backgroundImage.setHeight(backgroundTexture.getHeight() * backgroundImage.getWidth()/backgroundTexture.getWidth());
		backgroundImage.setX(Gdx.graphics.getWidth()/2 - backgroundImage.getWidth()/2);
		backgroundImage.setY(Gdx.graphics.getHeight()/2 - backgroundImage.getHeight()/2);

		//Texts	
		quantityLabelStyle = new LabelStyle(game.assets.get("fontMenu.ttf", BitmapFont.class), Color.WHITE);
		titleLabelStyle = new LabelStyle(game.assets.get("fontTable.ttf", BitmapFont.class), Color.WHITE);
		labelStyle = new LabelStyle(game.assets.get("fontUpgrade.ttf", BitmapFont.class), Color.WHITE);
		
		upgradeQuantityLabel = new Label("X " + Data.getUpgradePoint(), quantityLabelStyle);
		upgradeQuantityLabel.setX(imageUpgradePointX + imageUpgradePointWidth + 0.009f * Gdx.graphics.getWidth());
		upgradeQuantityLabel.setY(imageUpgradePointY + imageUpgradePointHeight/2 - upgradeQuantityLabel.getHeight()/2);	
		
		//Nouvelle UI
		//Oxygen upgrade
		oxygenLabel = new Label(game.text.get("Oxygen").toUpperCase(), titleLabelStyle);
		upgradesOxygen = new Array<Image>();
		oxygenWindow = createButton(upgradesOxygen, 
									oxygenLabel,
									Data.getOxygenLevel(),
									new Button(game.skin.getDrawable("MenuButton"), game.skin.getDrawable("MenuButtonCheck"), game.skin.getDrawable("MenuButtonCheck")),
									itemWindowWidth,
									0.085f*Gdx.graphics.getWidth(),
									0.06f * Gdx.graphics.getWidth(),
									0.55f * Gdx.graphics.getHeight());

		fuelLabel = new Label(game.text.get("Fuel").toUpperCase(), titleLabelStyle);
		upgradesFuel = new Array<Image>();
		fuelWindow = createButton(	upgradesFuel, 
									fuelLabel, 
									Data.getFuelLevel(),
									new Button(game.skin.getDrawable("MenuButton"), game.skin.getDrawable("MenuButtonCheck"), game.skin.getDrawable("MenuButtonCheck")),
									itemWindowWidth,
									0.085f*Gdx.graphics.getWidth(),
									oxygenWindow.getX(),
									oxygenWindow.getY() - oxygenWindow.getHeight());

		powerLabel = new Label(game.text.get("Power").toUpperCase(), titleLabelStyle);
		upgradesPower = new Array<Image>();
		powerWindow = createButton(	upgradesPower, 
									powerLabel, 
									Data.getPowerLevel(),
									new Button(game.skin.getDrawable("MenuButton"), game.skin.getDrawable("MenuButtonCheck"), game.skin.getDrawable("MenuButtonCheck")),
									itemWindowWidth,
									0.085f*Gdx.graphics.getWidth(),
									fuelWindow.getX(),
									fuelWindow.getY() - fuelWindow.getHeight());

		//Fenêtre permettant d'acheter les upgrades
		imageBuyWindow = new Image(game.skin.getDrawable("UIWindow"));
		imageBuyWindow.setWidth(Gdx.graphics.getWidth() * 0.3f);
		imageBuyWindow.setHeight(Gdx.graphics.getHeight() * 0.45f);
		imageBuyWindow.setX(0.94f * Gdx.graphics.getWidth() - imageBuyWindow.getWidth());
		imageBuyWindow.setY(oxygenWindow.getY() + oxygenWindow.getHeight() - imageBuyWindow.getHeight());
		
		labelDescription = new Label(stringOxygen, labelStyle);
		labelDescription.setWidth(0.85f * imageBuyWindow.getWidth());
		labelDescription.setX(imageBuyWindow.getX() + imageBuyWindow.getWidth()/2 - labelDescription.getWidth()/2);
		labelDescription.setY(oxygenWindow.getY());
		labelDescription.setWrap(true);
		labelDescription.setAlignment(Align.topLeft);

		Color colorFont = Pools.obtain(Color.class).set(2/256f, 165/256f, 200/256f, 1);	
		textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = game.skin.getDrawable("Button");
		textButtonStyle.down = game.skin.getDrawable("ButtonCheck");
		textButtonStyle.font = game.assets.get("fontUpgrade.ttf", BitmapFont.class);
		textButtonStyle.fontColor = colorFont;
		textButtonStyle.downFontColor = Color.BLACK;
		
		//Buy button
		buyButton = new TextButton(game.text.get("Buy"), textButtonStyle);
		buyButton.setHeight(0.065f*Gdx.graphics.getWidth());
		buyButton.setWidth(new GlyphLayout(game.assets.get("fontTable.ttf", BitmapFont.class), game.text.get("Buy")).width + 0.03f*Gdx.graphics.getWidth());
		buyButton.setX(imageBuyWindow.getX() + imageBuyWindow.getWidth()/2 - buyButton.getWidth()/2);
		buyButton.setY(imageBuyWindow.getY() + 0.5f * buyButton.getPrefHeight());
		
		//Back button
		backButton = new Button(game.skin.getDrawable("BackButtonIcon"), game.skin.getDrawable("BackButtonIconCheck"));
		backButton.setWidth(Gdx.graphics.getWidth()/10);
		backButton.setHeight(Gdx.graphics.getWidth()/10);
		backButton.setX(Gdx.graphics.getWidth()/50);
		backButton.setY(Gdx.graphics.getWidth()/50);
		
		stage.addActor(backgroundImage);
		stage.addActor(imageBuyWindow);
		stage.addActor(upgradeQuantityLabel);
		stage.addActor(oxygenLabel);
		stage.addActor(fuelLabel);
		stage.addActor(powerLabel);	
		oxygenWindow.addToStage(stage);
		fuelWindow.addToStage(stage);
		powerWindow.addToStage(stage);
		stage.addActor(labelDescription);
		stage.addActor(buyButton);
		screenTitleWindow.addToStage(stage);
		stage.addActor(backButton);
			
		buttonGroup = new ButtonGroup<Button>();
		buttonGroup.add(oxygenWindow.button);
		buttonGroup.add(fuelWindow.button);
		buttonGroup.add(powerWindow.button);
		buttonGroup.setMinCheckCount(1);
		buttonGroup.setMaxCheckCount(1);	

		Pools.free(colorFont);
		Pools.free(colorScreenTitle);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	
	    stage.act();
	    stage.draw();
	    
	    //Animation
		animTime += Gdx.graphics.getDeltaTime();
		game.batch.begin();
		//Nombre de points upgrade
	    game.batch.setColor(1, 1, 1, 1);
	    game.batch.draw(upgradeAnimation.getKeyFrame(animTime), 
			    		imageUpgradePointX, 
			    		imageUpgradePointY,
			    		imageUpgradePointWidth, 
			    		imageUpgradePointHeight);	    
	    //Prix
	    game.batch.setColor(1, 1, 1, prixAlpha);
	    game.batch.draw(upgradeAnimation.getKeyFrame(animTime), 
			    		labelDescription.getX() + imagePrixPosX, 
			    		labelDescription.getY() + labelDescription.getHeight() - labelDescription.getPrefHeight() - 0.004f * Gdx.graphics.getWidth(),
			    		0.025f * Gdx.graphics.getWidth(), 
			    		0.025f * Gdx.graphics.getWidth() * upgradeAnimation.getKeyFrame(0, true).getRegionHeight() / upgradeAnimation.getKeyFrame(0, true).getRegionWidth());
	    game.batch.end();
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		
		oxygenWindow.actors.get(0).addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
		    	labelDescription.setText(stringOxygen);
				labelDescription.addAction(Actions.alpha(1, 0.25f));
				imagePrixPosX = new GlyphLayout(game.assets.get("fontUpgrade.ttf", BitmapFont.class), game.text.get("Cost").toUpperCase() + " : " + oxygenCost + " ").width;
				if(Data.getOxygenLevel() < 5){
					buyButton.addAction(Actions.alpha(1, 0.25f));
					prixAlpha = 1;
				}
				else{
					buyButton.addAction(Actions.alpha(0));
					prixAlpha = 0;
				}
			}
		});
		
		fuelWindow.actors.get(0).addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
		    	labelDescription.setText(stringFuel);
				labelDescription.addAction(Actions.alpha(1, 0.25f));
				imagePrixPosX = new GlyphLayout(game.assets.get("fontUpgrade.ttf", BitmapFont.class), game.text.get("Cost").toUpperCase() + " : " + fuelCost + " ").width;
				if(Data.getFuelLevel() < 5){
					buyButton.addAction(Actions.alpha(1, 0.25f));
					prixAlpha = 1;
				}
				else{
					buyButton.addAction(Actions.alpha(0));
					prixAlpha = 0;
				}
			}
		});
		
		powerWindow.actors.get(0).addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
		    	labelDescription.setText(stringPower);
				labelDescription.addAction(Actions.alpha(1, 0.25f));
				imagePrixPosX = new GlyphLayout(game.assets.get("fontUpgrade.ttf", BitmapFont.class), game.text.get("Cost").toUpperCase() + " : " + powerCost + " ").width;
				if(Data.getPowerLevel() < 5){
					buyButton.addAction(Actions.alpha(1, 0.25f));
					prixAlpha = 1;
				}
				else{
					buyButton.addAction(Actions.alpha(0));
					prixAlpha = 0;
				}
			}
		});
		
		buyButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
		    	if(oxygenWindow.button.isChecked()){
		    		if(Data.getOxygenLevel() < 5 && Data.getUpgradePoint() >= oxygenCost){
						Data.setUpgradePoint(Data.getUpgradePoint() - oxygenCost);
						Data.setOxygenLevel(Data.getOxygenLevel() + 1);
						GameConstants.MAX_OXYGEN = 150 + Data.getOxygenLevel()*(10 + Data.getOxygenLevel());
						
						upgradeQuantityLabel.setText("X " + Data.getUpgradePoint());
						updatePrices();
						if(Data.getOxygenLevel() < 5){
							stringOxygen = game.text.get("OxygenDescription") + "\n\n" +  game.text.get("Cost") + " : " + oxygenCost + " ";
							imagePrixPosX = new GlyphLayout(game.assets.get("fontUpgrade.ttf", BitmapFont.class), game.text.get("Cost").toUpperCase() + " : " + oxygenCost + " ").width;
						}
						else{
							stringOxygen = game.text.get("OxygenDescription") + "\n\n" +  game.text.get("Cost") + " : " + game.text.get("Maximum").toUpperCase() + " ";
							prixAlpha = 0;
						}
										
						for(int i = 0; i < upgradesOxygen.size; i++){
							if(i < Data.getOxygenLevel()){
								upgradesOxygen.get(i).setColor(activeUpgradeColor);
							}
						}
						
						labelDescription.setText(stringOxygen);
					}
		    	}
		    	else if(fuelWindow.button.isChecked()){
		    		if(Data.getFuelLevel() < 5 && Data.getUpgradePoint() >= fuelCost){
						Data.setUpgradePoint(Data.getUpgradePoint() - fuelCost);
						Data.setFuelLevel(Data.getFuelLevel() + 1);
						GameConstants.MAX_FUEL = 100 + Data.getFuelLevel()*(7 + Data.getFuelLevel());
						
						upgradeQuantityLabel.setText("X " + Data.getUpgradePoint());
						updatePrices();
						if(Data.getFuelLevel() < 5){
							stringFuel = game.text.get("FuelDescription") + "\n\n" +  game.text.get("Cost") + " : " + fuelCost + " ";
							imagePrixPosX = new GlyphLayout(game.assets.get("fontUpgrade.ttf", BitmapFont.class), game.text.get("Cost").toUpperCase() + " : " + fuelCost + " ").width;
						}
						else{
							stringFuel = game.text.get("FuelDescription") + "\n\n" +  game.text.get("Cost") + " : " + game.text.get("Maximum").toUpperCase() + " ";
							prixAlpha = 0;
						}
										
						for(int i = 0; i < upgradesFuel.size; i++){
							if(i < Data.getFuelLevel()){
								upgradesFuel.get(i).setColor(activeUpgradeColor);
							}
						}
						
						labelDescription.setText(stringFuel);
					}	
		    	}
		    	else if(powerWindow.button.isChecked()){
		    		if(Data.getPowerLevel() < 5 && Data.getUpgradePoint() >= powerCost){
						Data.setUpgradePoint(Data.getUpgradePoint() - powerCost);
						Data.setPowerLevel(Data.getPowerLevel() + 1);
						
						upgradeQuantityLabel.setText("X " + Data.getUpgradePoint());
						updatePrices();
						if(Data.getPowerLevel() < 5){
							stringPower = game.text.get("PowerDescription") + "\n\n" +  game.text.get("Cost") + " : " + powerCost + " ";
							imagePrixPosX = new GlyphLayout(game.assets.get("fontUpgrade.ttf", BitmapFont.class), game.text.get("Cost").toUpperCase() + " : " + powerCost + " ").width;
						}
						else{
							stringPower = game.text.get("PowerDescription") + "\n\n" +  game.text.get("Cost") + " : " + game.text.get("Maximum").toUpperCase() + " ";
							prixAlpha = 0;
						}
										
						for(int i = 0; i < upgradesPower.size; i++){
							if(i < Data.getPowerLevel()){
								upgradesPower.get(i).setColor(activeUpgradeColor);
							}
						}
					}
		    		
					labelDescription.setText(stringPower);
		    	}
			}
		});
		
		backButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
            	dispose();
				game.setScreen(new MainMenuScreen(game));
			}
		});
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
	
	private UIWindow createButton(Array<Image> upgradeImages, Label label, int upgradeLevel, Button button, float width, float height, float X, float Y){	
		for(int i = 0; i < 5; i++){
			Image image = new Image(new TextureRegion(game.skin.getRegion("WhiteSquare")));
			image.setHeight(Gdx.graphics.getWidth()/30);
			image.setWidth((i*i+1)*image.getHeight()/10);
			image.setColor(inactiveUpgradeColor);
			upgradeImages.add(image);
		}
		
		UIWindow uiWindow = new UIWindow(button, width, height, X, Y);
		uiWindow.addActor(label, uiWindow.getWidth()/50, uiWindow.getHeight()/2 - label.getPrefHeight()/2);
		
		for(int i =0; i < upgradeImages.size; i++){
			float posX = 0;
			for(int j = i; j < upgradeImages.size; j++)
				posX += upgradeImages.get(j).getWidth() + uiWindow.getWidth()/50;
			
			uiWindow.addActor(	upgradeImages.get(i), 
									49*uiWindow.getWidth()/50 - posX, 
									uiWindow.getHeight()/2 - upgradeImages.get(i).getHeight()/2);
		}
		//Image upgrade achetée
		for(int i = 0; i < upgradeImages.size; i++){
			if(i < upgradeLevel){
				upgradeImages.get(i).setColor(activeUpgradeColor);
			}
		}
		//Les éléments par dessus le bouton ne sont pas touchables pour ne pas géner les interactions avec le bouton
		for(int i = 1; i < uiWindow.actors.size; i++)
			uiWindow.actors.get(i).setTouchable(Touchable.disabled);
		
		return uiWindow;
	}
	
	public void updatePrices(){
		oxygenCost = (int)(GameConstants.OXYGEN_UPGRADE_COST*(1 + .2f*Data.getOxygenLevel()*Data.getOxygenLevel()));
		fuelCost = (int)(GameConstants.FUEL_UPGRADE_COST*(1 + .22f*Data.getFuelLevel()*Data.getFuelLevel()));
		powerCost = (int)(GameConstants.POWER_UPGRADE_COST*(1 + .25f*Data.getPowerLevel()*Data.getPowerLevel()));
	}

	@Override
	public void dispose() {
		System.out.println("Upgrade Screen disposed");
		backgroundTexture.dispose();
		stage.dispose();
		game.skin.dispose();
		Pools.free(inactiveUpgradeColor);
		Pools.free(activeUpgradeColor);
	}
}

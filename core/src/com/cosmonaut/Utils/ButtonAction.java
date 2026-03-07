package com.cosmonaut.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.cosmonaut.Data;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Screens.GameScreen;
import com.cosmonaut.Screens.TutorialScreen;

public class ButtonAction {
	
	public ButtonAction(){
	}

	private void transitionToScreen(MyGdxGame game, Screen nextScreen, boolean stopMenuMusic){
		if(stopMenuMusic){
			game.music.stop();
		}
		Screen previousScreen = game.getScreen();
		game.setScreen(nextScreen);
		if(previousScreen != null && previousScreen != nextScreen){
			previousScreen.dispose();
		}
	}

	private void openLevel(final MyGdxGame game, int level){
		final int previousLevel = GameConstants.SELECTED_LEVEL;
		try{
			GameConstants.SELECTED_LEVEL = level;
			if(level == 24 && !game.assets.isLoaded("Images/Fin/Images_Fin.pack", TextureAtlas.class)){
				game.assets.load("Images/Fin/Images_Fin.pack", TextureAtlas.class);
				game.assets.finishLoading();
	        }
			transitionToScreen(game, new GameScreen(game), true);
		}
		catch(RuntimeException runtimeException){
			GameConstants.SELECTED_LEVEL = previousLevel;
			Gdx.app.error("Cosmonaut", "Unable to open level " + level, runtimeException);
		}
	}
	
	public void levelListener(final MyGdxGame game, TextButton bouton, final int niveau){
		bouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				if(niveau == 1){
					if(game.levelHandler.isLevelUnlocked(2))
						openLevel(game, niveau);
					else
						transitionToScreen(game, new TutorialScreen(game), true);
				}
				else if(Data.getFullVersion()){
					openLevel(game, niveau);
				}
				else if(niveau <= GameConstants.FREE_LEVELS){
					openLevel(game, niveau);
				}
				else{
					game.fullVersionWindow.alfaOne(0.2f);	
					game.blackImage.setTouchable(Touchable.enabled);
					game.blackImage.addAction(Actions.alpha(0.7f, 0.2f));
				}
			}
		});
	}
}

package com.cosmonaut.Utils;

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
	
	public void levelListener(final MyGdxGame game, TextButton bouton, final int niveau){
		bouton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				GameConstants.SELECTED_LEVEL = niveau;
				
				if(niveau == 1){
					game.music.stop();
					game.getScreen().dispose();
					
					if(game.levelHandler.isLevelUnlocked(2))
						game.setScreen(new GameScreen(game));
					else
						game.setScreen(new TutorialScreen(game));	
				}
				else if(Data.getFullVersion()){
					if(GameConstants.SELECTED_LEVEL == 24){
			        	System.out.println("Dernier niveau");
						game.assets.load("Images/Fin/Images_Fin.pack", TextureAtlas.class);
						game.assets.finishLoading();
			        }
					game.music.stop();
					game.getScreen().dispose();
					game.setScreen(new GameScreen(game));
				}
				else if(niveau <= GameConstants.FREE_LEVELS){
						game.music.stop();
						game.getScreen().dispose();
						game.setScreen(new GameScreen(game));
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

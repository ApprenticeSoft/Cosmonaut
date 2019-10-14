package com.cosmonaut.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

public class UIWindow {

	public Image backgroundImage;
	public Array<Actor> actors;
	public Button button;
	
	public UIWindow(Drawable backgroundDrawable, float width, float height){
		backgroundImage = new Image(backgroundDrawable);
		backgroundImage.setWidth(width);
		backgroundImage.setHeight(height);
		backgroundImage.setX(Gdx.graphics.getWidth()/2 - backgroundImage.getWidth()/2);
		backgroundImage.setY(Gdx.graphics.getHeight()/2 - backgroundImage.getHeight()/2);
				
		actors = new Array<Actor>();
		actors.add(backgroundImage);
	}
	
	public UIWindow(Drawable backgroundDrawable, float width, float height, float X, float Y){
		backgroundImage = new Image(backgroundDrawable);
		backgroundImage.setWidth(width);
		backgroundImage.setHeight(height);
		backgroundImage.setX(X);
		backgroundImage.setY(Y);
				
		actors = new Array<Actor>();
		actors.add(backgroundImage);
	}
	
	public UIWindow(Button button, float width, float height, float X, float Y){
		this.button = button;
		button.setWidth(width);
		button.setHeight(height);
		button.setX(X);
		button.setY(Y);
				
		actors = new Array<Actor>();
		actors.add(button);
	}
	
	public void addActor(Actor actor, float X, float Y){
		actor.setX(actors.get(0).getX() + X);
		actor.setY(actors.get(0).getY() + Y);
		actors.add(actor);
	}
	
	public void addActor(Actor actor, float width, float height, float X, float Y){
		actor.setWidth(width);
		actor.setHeight(height);
		actor.setX(actors.get(0).getX() + X);
		actor.setY(actors.get(0).getY() + Y);
		actors.add(actor);
	}
	
	public void addActorRelative(Actor actor, float X, float Y){
		actor.setX(actors.get(0).getX() + X * actors.get(0).getWidth());
		actor.setY(actors.get(0).getY() + Y * actors.get(0).getHeight());
		actors.add(actor);
	}
	
	public void addActorRelative(Actor actor, float width, float height, float X, float Y){
		actor.setWidth(width);
		actor.setHeight(height);
		actor.setX(actors.get(0).getX() + X * actors.get(0).getWidth());
		actor.setY(actors.get(0).getY() + Y * actors.get(0).getHeight());
		actors.add(actor);
	}
	
	public void addActorRelativeCentered(Actor actor, float X, float Y){
		actor.setX(actors.get(0).getX() + X * actors.get(0).getWidth() - actor.getWidth()/2);
		actor.setY(actors.get(0).getY() + Y * actors.get(0).getHeight() - actor.getHeight()/2);
		actors.add(actor);
	}
	
	public void addActorRelativeCentered(Actor actor, float width, float height, float X, float Y){
		actor.setWidth(width);
		actor.setHeight(height);
		actor.setX(actors.get(0).getX() + X * actors.get(0).getWidth() - actor.getWidth()/2);
		actor.setY(actors.get(0).getY() + Y * actors.get(0).getHeight() - actor.getHeight()/2);
		actors.add(actor);
	}
	
	public void setActorPositionRelativeCentered(Actor actor, float X, float Y){
		actor.setX(actors.get(0).getX() + X * actors.get(0).getWidth() - actor.getWidth()/2);
		actor.setY(actors.get(0).getY() + Y * actors.get(0).getHeight() - actor.getHeight()/2);
	}
	
	public void setActorPositionRelative(Actor actor, float X, float Y){
		actor.setX(actors.get(0).getX() + X * actors.get(0).getWidth());
		actor.setY(actors.get(0).getY() + Y * actors.get(0).getHeight());
	}
	
	public void addToStage(Stage stage){
		for(Actor actor : actors)
			stage.addActor(actor);
	}
	
	public void alfaZero(float durationSecond){
		for(int i = 0; i < actors.size; i++){
			actors.get(i).addAction(Actions.alpha(0, durationSecond));
			actors.get(i).setTouchable(Touchable.disabled);
		}
	}
	
	public void alfaOne(float durationSecond){
		for(int i = 0; i < actors.size; i++){
			actors.get(i).addAction(Actions.alpha(1, durationSecond));
			actors.get(i).setTouchable(Touchable.enabled);
		}
	}
	
	public float getWidth(){
		return actors.get(0).getWidth();
	}
	
	public void setWidth(float width){
		actors.get(0).setWidth(width);
	}
	
	public float getHeight(){
		return actors.get(0).getHeight();
	}
	
	public void setHeight(float height){
		actors.get(0).setHeight(height);
	}
	
	public float getX(){
		return actors.get(0).getX();
	}
	
	public void setX(float X){
		for(int i = 1; i < actors.size; i++)
			actors.get(i).setX(actors.get(i).getX() + (X - actors.get(0).getX()));	
		actors.get(0).setX(X);
	}
	
	public float getY(){
		return actors.get(0).getY();
	}
	
	public void setY(float Y){
		for(int i = 1; i < actors.size; i++)
			actors.get(i).setY(actors.get(i).getY() + (Y - actors.get(0).getY()));	
		actors.get(0).setY(Y);
	}
	
	public void setVisible(boolean visible){
		for(Actor actor : actors)
			actor.setVisible(visible);
	}
	
	public void dispose(){
		for(Actor actor : actors)
			Pools.free(actor);
	}
}

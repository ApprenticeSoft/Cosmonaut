package com.cosmonaut.Utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class MyTiledTexture {

	private Texture texture;
	//private float totalWidth, totalHeight;
	
	public MyTiledTexture(Texture texture){
		this.texture = texture;
		this.texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		this.texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}
	
	public MyTiledTexture(FileHandle file, boolean useMipMaps){
		texture = new Texture(file, useMipMaps);
		texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		this.texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}
	
	public void drawHorizontal(SpriteBatch batch, float x, float y, float width, float height){
		//Textures pleines
		batch.draw(	texture,
					x, 
					y, 
					0, 										//float originX
					0, 										//float originY
					width - width%height,									//float width
					height, 								//float height
					1, 										//float scaleX
					1, 										//float scaleY
					0, 										//float rotation
					0, 										//int srcX
					0, 										//int srcY
					(int)(texture.getHeight() * (int)(width/height)), //int srcWidth
					(int) texture.getHeight(), 				//int srcHeight
					false, 									//boolean flipX
					false); 								//boolean flipY
		
		//Texture partielle
		batch.draw(	texture,
					x + width - width%height, 
					y, 
					0, 										//float originX
					0, 										//float originY
					width%height,							//float width
					height, 								//float height
					1, 										//float scaleX
					1, 										//float scaleY
					0, 										//float rotation
					0, 										//int srcX
					0, 										//int srcY
					(int)(texture.getHeight() * (width%height)/height), //int srcWidth
					(int) texture.getHeight(), 				//int srcHeight
					false, 									//boolean flipX
					false); 
	}
	
	public void drawVertical(SpriteBatch batch, float x, float y, float width, float height){
		//Textures pleines
		batch.draw(	texture,
					x, 
					y, 
					0, 										//float originX
					0, 										//float originY
					width,									//float width
					height - height%width, 					//float height
					1, 										//float scaleX
					1, 										//float scaleY
					0, 										//float rotation
					0, 										//int srcX
					0, 										//int srcY
					(int) texture.getWidth(), 				//int srcWidth
					(int)(texture.getWidth() * (int)(height/width)), //int srcHeight
					false, 									//boolean flipX
					false); 								//boolean flipY
		
		//Texture partielle
		batch.draw(	texture,
					x, 
					y + height - height%width, 
					0, 										//float originX
					0, 										//float originY
					width,									//float width
					height%width, 							//float height
					1, 										//float scaleX
					1, 										//float scaleY
					0, 										//float rotation
					0, 										//int srcX
					(int)MathUtils.round(-height%width), 	//int srcY
					(int) texture.getHeight(), 				//int srcWidth
					(int)(texture.getHeight() * (height%width)/width), 	//int srcHeight
					false, 									//boolean flipX
					false); 
	}
}

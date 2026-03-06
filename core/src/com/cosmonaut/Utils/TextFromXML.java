package com.cosmonaut.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.cosmonaut.Data;

public class TextFromXML {

	Element root;
	
	public TextFromXML(String filePath){
		root = new XmlReader().parse(Gdx.files.internal(filePath));
	}
	
	public String get(String childName, String language){
		return root.getChildByName(childName).get(language);
	}
	
	public String get(String childName){
		return root.getChildByName(childName).get(Data.getLanguage());
	}
}

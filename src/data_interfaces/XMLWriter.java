package data_interfaces;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import components.entityComponents.LocationComponent;
import components.entityComponents.SpriteComponent;
import data_interfaces.LocalClassLoader;
import entity.Entity;
import entity.IEntity;

public class XMLWriter extends GameSavingDataTool implements Writer {

	private void createFile(String fileName, String data) {
		try {
			File f = new File(getPrefix() + fileName + getSuffix());
			BufferedWriter b = new BufferedWriter(new FileWriter(f));
			b.write(data.toString());
			b.close();
		} catch (IOException e) {
			// TODO call the alert that they built
		}
	}

	/**
	 * writes an XML file which saves game data
	 * 
	 * @param fileName
	 *            the desired name for the file
	 * @param gameData
	 *            the data which should be saved
	 */
	public void writeFile(String fileName, Collection gameData) {
		ClassLoader loader = new LocalClassLoader();
		XStream serializer = new XStream(new DomDriver());
		String ret;

		serializer.setClassLoader(loader);

		ret = serializer.toXML(gameData);
		createFile(fileName, ret);
	}

	

}

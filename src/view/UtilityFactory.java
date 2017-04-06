package view;

import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UtilityFactory {
	
    public static final String DEFAULT_RESOURCE_PACKAGE = "resources/";
	
	private ResourceBundle myResources;
	
	public UtilityFactory(String language){
		myResources = ResourceBundle.getBundle(language);
	}
	
	public Tab buildTab(){
		Tab myTab = new Tab();
		return myTab;
	}
	
	public Button buildButton(String property, String eventname){
		// represent all supported image suffixes
        final String IMAGEFILE_SUFFIXES =
                String.format(".*\\.(%s)", String.join("|", ImageIO.getReaderFileSuffixes()));

        Button result = new Button();
        String label = myResources.getString(property);
        if (label.matches(IMAGEFILE_SUFFIXES)) {
            result.setGraphic(new ImageView(
                                  new Image(getClass().getResourceAsStream(DEFAULT_RESOURCE_PACKAGE + label))));
        } else {
            result.setText(label);
        }
        EventFactory evfac = new EventFactory();
        EventHandler handler = evfac.getEvent(eventname);
        result.setOnAction(handler);
        return result;
	}
	
	public MenuItem builtMenuItem(String name, EventHandler<ActionEvent> event){
		MenuItem myMenuItem = new MenuItem(name);
		myMenuItem.setOnAction(event);
		return myMenuItem;
	}

}

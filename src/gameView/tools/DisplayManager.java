package gameView.tools;

import gameView.displayComponents.UIDisplayComponent;
import gameView.gameScreen.GameScreen;
import gamedata.IRestrictedGameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import voogasalad.util.reflection.Reflection;

public class DisplayManager implements IDisplayManager {

	private static final String COMPONENT_LOCATION = "gameView.displayComponents.";
	private static final String BUNDLE_KEY = DisplayManager.class.getSimpleName();
	
	private HashMap<String, UIDisplayComponent> myAllDisplays;
	private HashMap<String, UIDisplayComponent> myActiveDisplays;
	private GameScreen myScreen;
	private ReadOnlyDoubleProperty myWidthBound;
	private ReadOnlyDoubleProperty myHeightBound;
	private IRestrictedGameData myGameData;
	
	public DisplayManager(GameScreen screen, String filePath, ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height, 
			IRestrictedGameData gameData) {
		myGameData = gameData;
		myScreen = screen;
		setBounds(width, height);
		myAllDisplays = makeComponents(filePath);
		myActiveDisplays = new HashMap<String, UIDisplayComponent>();
		copyMap(myAllDisplays);
	}
	
	public void add(String toAdd) {
		if (!myActiveDisplays.containsKey(toAdd)) {
			myActiveDisplays.put(toAdd, myAllDisplays.get(toAdd));
		}
		myScreen.addComponent(myAllDisplays.get(toAdd));
	}
	
	public void remove(String toRemove) {
		myActiveDisplays.remove(toRemove);
		myScreen.removeComponent(myAllDisplays.get(toRemove));
	}
	
	public Collection<String> getNames() {
		return myAllDisplays.keySet();
	}
	
	public boolean checkIfActive(String key) {
		return myActiveDisplays.containsKey(key);
	}
	
	public void addAllActive() {
		myActiveDisplays.keySet().stream()
			.forEach(c -> add(c));
	}
	
	private HashMap<String, UIDisplayComponent> makeComponents(String filePath) {
		HashMap<String, UIDisplayComponent> displays = new HashMap<String, UIDisplayComponent>();
		ResourceBundle bundle = ResourceBundle.getBundle(filePath);
		String comps = bundle.getString(BUNDLE_KEY);
		String[] allComps = comps.split(", ");
		for (String each: allComps) {
			UIDisplayComponent newDisplay = (UIDisplayComponent) Reflection.createInstance(COMPONENT_LOCATION+each+"Component", each, myGameData);
			updateX(newDisplay, myWidthBound.doubleValue());
			updateY(newDisplay, myHeightBound.doubleValue());
			if (newDisplay.getDisplay() != null) {
				displays.put(each, newDisplay);
			}
		}
		return displays;
	}
	
	private void copyMap(HashMap<String, UIDisplayComponent> toCopy) {
		for (String key: toCopy.keySet()) {
			add(key);
		}
	}
	
	private void setBounds(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
		myWidthBound = width;
		myHeightBound = height;
		myWidthBound.addListener(new ChangeListener<Number>() {
		      public void changed(ObservableValue<? extends Number> o, Number oldVal, Number newVal) {
		        myAllDisplays.values().stream()
		        	.forEach(c -> updateX(c, (Double) newVal));
		      }
		    });
		myHeightBound.addListener(new ChangeListener<Number>() {
		      @Override
		      public void changed(ObservableValue<? extends Number> o, Number oldVal, Number newVal) {
		    	  myAllDisplays.values().stream()
		        	.forEach(c -> updateY(c, (Double) newVal));
		      }
		    });
	}
	
	private void updateX(UIDisplayComponent width, Double newVal) {
		double x = (width.getPos().x())/100;
		if (x!= 0) {
			width.getDisplay().setTranslateX(newVal*x-(width.getSize().getWidth()*x));
		}
	}
	
	private void updateY(UIDisplayComponent height, Double newVal) {
		double y = (height.getPos().y())/100;
		if (y!= 0) {
			height.getDisplay().setTranslateY(newVal*y-(height.getSize().getHeight()*y));
		}
	}
}

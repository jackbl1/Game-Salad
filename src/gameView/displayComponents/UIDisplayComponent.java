package gameView.displayComponents;


import gameObject.GameConfig;
import gameView.tools.DisplayEnum;
import gamedata.IRestrictedGameData;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Dimension2D;
import javafx.scene.layout.Region;

public abstract class UIDisplayComponent implements IDisplayComponent {

	private String myDisplayName;
	private GameConfig gameConfig;
	private IRestrictedGameData myGameData;
	
	public UIDisplayComponent(String name, IRestrictedGameData gameData) {
		myGameData = gameData;
		myDisplayName = name;
		gameConfig = new GameConfig(1, 3);
		setID();
	}
	
	public String getName() {
		return myDisplayName;
	}
	
	public Dimension2D getSize() {
		return new Dimension2D(getDisplay().getPrefWidth(), getDisplay().getPrefHeight());
	}
	
	protected IRestrictedGameData getData() {
		return myGameData;
	}
	
	protected GameConfig getConfig() {
		return gameConfig;
	}
	
	protected ReadOnlyDoubleProperty setValue(ReadOnlyDoubleProperty value) {
		value.addListener(new ChangeListener<Number>(){
		        public void changed(ObservableValue<? extends Number> o,Number oldVal, 
		                 Number newVal){
		             changedValue();
		        }
		      });
		return value;
	}
	 
	
	protected abstract void changedValue();
	public abstract Region getDisplay();
	public abstract DisplayEnum getPos();
	protected abstract void setID();
	
}

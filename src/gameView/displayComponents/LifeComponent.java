package gameView.displayComponents;

import gameView.UIView;
import gameView.tools.DisplayEnum;
import gamedata.IRestrictedGameData;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class LifeComponent extends UIDisplayComponent {

	private HBox myLives;
	private ReadOnlyDoubleProperty myLifeNumber;
	
	public LifeComponent(String name, IRestrictedGameData gameData) {
		super(name, gameData);
	}

	@Override
	public Region getDisplay() {
		if (myLifeNumber == null) {
			return null;
		}
		return myLives;
	}
	
	public DisplayEnum getPos() {
		return DisplayEnum.TOP_LEFT;
	}
	
	protected void setID() {
		myLifeNumber = setValue(getData().getLives());
		myLives = new HBox();
		addLifeImages();
		myLives.setId(getName().toLowerCase());
	} 
	
	private void addLifeImages() {
		myLives.getChildren().clear();
		myLives.setPrefSize((UIView.DEFAULT_SIZE.width/20)*myLifeNumber.doubleValue(), (UIView.DEFAULT_SIZE.width/10)*0.5);
		for (int i = 0; i < myLifeNumber.doubleValue(); i++) {
			myLives.getChildren().add(makeLabel());
		}
	}

	
	private ImageView makeLabel() {
		ImageView lifeLabel = new ImageView();
		lifeLabel.setId("lifelabel");
		lifeLabel.setFitHeight(myLives.getPrefHeight());
		lifeLabel.setFitWidth(myLives.getPrefWidth()/getConfig().getLives());
		return lifeLabel;
	}

	@Override
	protected void changedValue() {
		addLifeImages();
	} 

}

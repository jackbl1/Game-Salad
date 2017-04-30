package gamedata;

import java.util.ArrayList;

import com.sun.javafx.UnmodifiableArrayList;

import components.entityComponents.LocationComponent;
import entity.restricted.IRestrictedEntityManager;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.collections.ObservableList;

public interface IRestrictedGameData {
	public ReadOnlyDoubleProperty getPoints();
	
	public ReadOnlyIntegerProperty getLives();

	public IRestrictedEntityManager getRestrictedEntityManager();

	public ReadOnlyIntegerProperty getLevel();

	public LocationComponent getMainLocation();

	public String getMusic();

	public ObservableList<String> getAchievement();
}

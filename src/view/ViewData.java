package view;

import entity.Entity;
import entity.LevelEntity;
import entity.SplashEntity;

import java.util.HashMap;
import java.util.Observable;
import components.*;

/**
 * Casting takes place to be able to edit the component because we are using enums to choose the specific component
 * we feel comfortable to cast the component to its specific component
 *
 * @author Jonathan
 * @author Justin
 * @author Jack
 *
 */
public class ViewData extends Observable {
	
	private static final int STARTINGROWS = 10;
	private static final int STARTINGCOLS = 10;
	
	private HashMap<Integer, Entity> definedEntityMap;
	private HashMap<Integer, Entity> placedEntityMap;
	private LevelEntity myLevelEntity;
	private SplashEntity mySplashEntity;
	private Entity userSelectedEntity;
	private String gameName;

	public ViewData() {
		definedEntityMap = new HashMap<Integer, Entity>();
		placedEntityMap = new HashMap<Integer, Entity>();
		myLevelEntity = new LevelEntity(0, STARTINGROWS, STARTINGCOLS, "");
		mySplashEntity = new SplashEntity(1, "The game", "Don't lose", "images/background1.png");
		userSelectedEntity = null;
		gameName = "";
	}

	public void setUserSelectedEntity(Entity entity) {
		userSelectedEntity = entity;
	}

	public Entity getUserSelectedEntity() {
		return userSelectedEntity;
	}

	public void setEntityLocation(int entityID, int row, int col) {

		LocationComponent locComp = new LocationComponent(row, col);

		placedEntityMap.get(entityID).addComponent(locComp);
		/*	LocationComponent locComp = (LocationComponent) definedEntityList.get(entityID).getComponent(ComponentType.Location);
		locComp.setX(row);
		locComp.setY(col); */
	}

	public void defineEntity(Entity entity) {
		definedEntityMap.put(entity.getID(), entity);
	}

	public void placeEntity(Entity entity) {
		placedEntityMap.put(entity.getID(), entity);
	}

	public void undefineEntity(Entity entity) {
		definedEntityMap.remove(entity.getID());
	}

	public void unplaceEntity(Entity entity) {
		definedEntityMap.remove(entity.getID());
	}

	public HashMap<Integer, Entity> getDefinedEntityMap() {
		return definedEntityMap;
	}
	
	public HashMap<Integer, Entity> getPlacedEntityMap() {
		return placedEntityMap;
	}
	
	public LevelEntity getLevelEntity () {
		return myLevelEntity;
	}
	
	public void setLevelEntity(LevelEntity l) {
		myLevelEntity = l;
	}
	
	public SplashEntity getSplashEntity() {
		return mySplashEntity;
	}
	
	public void setSplashEntity(SplashEntity s) {
		mySplashEntity = s;
	}

	public void setGameName(String s) {
		gameName = s;
	}

	public String getGameName() {
		return gameName;
	}
	
	public void clearData(){
		definedEntityMap.clear();
		placedEntityMap.clear();
		
	}
	public void refresh(){
		setChanged();
		notifyObservers("refresh");
	}
}

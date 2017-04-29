package gamedata;

import java.util.ArrayList;
import java.util.List;

import components.entityComponents.LocationComponent;
import entity.Entity;
import entity.EntityManager;
import entity.IEntity;
import entity.restricted.IRestrictedEntity;
import entity.restricted.IRestrictedEntityManager;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class GameDataFactory {
	private MediaPlayer player;
	public GameDataFactory(){
	}
	public GameData blankEntityData(IRestrictedGameData currentData){
		double points = currentData.getPoints();
		double lives = currentData.getLives();
		double lvl = currentData.getLevel();
		LocationComponent lc = currentData.getMainLocation();
		String ac = currentData.getAchievement().toString();
		String music = currentData.getMusic().toString();
		
		return new GameData(points,lives,(IRestrictedEntityManager) new EntityManager(), lvl, lc, ac, music);
	}
	public void updateGameData(IGameData gameData, IRestrictedGameData updatedData){
		gameData.setPoints(updatedData.getPoints());
		gameData.setLives(updatedData.getPoints());
		gameData.setMainLocation(updatedData.getMainLocation());
		gameData.setLevel(updatedData.getLevel());
		if (gameData.getMusic().toString()!= updatedData.getMusic().toString()){
			gameData.setMusic(updatedData.getMusic().toString());
		}
		//		List<Entity> newEntities = new ArrayList<Entity>();
		//		for (IRestrictedEntity re : updatedData.getRestrictedEntityManager().getRestrictedEntities()){
		//			newEntities.add(re.clone());
		//		}
		//		gameData.setRestrictedEntityManager((IRestrictedEntityManager) new EntityManager(newEntities));
	}
}
package actions;

import class_annotations.BottomAction;
import class_annotations.LeftAction;
import class_annotations.RightAction;
import class_annotations.TopAction;
import components.entityComponents.ComponentType;
import components.entityComponents.LocationComponent;
import entity.IEntity;
import entity.IEntityManager;
import gamedata.IRestrictedGameData;

@TopAction()
@LeftAction()
@BottomAction()
@RightAction()
public class Teleport extends AbstractAction  implements IAction {
	private double teleportXLocation;
	private double teleportYLocation;
	
	public Teleport(double newX, double newY) {
		teleportXLocation = newX;
		teleportYLocation = newY;
	}

	@Override
	public IRestrictedGameData executeAction(IEntity player, IEntity npc, IEntityManager myEM, IRestrictedGameData currentGameData) {
		((LocationComponent) player.getComponent(ComponentType.Location)).setXY(teleportXLocation, teleportYLocation);
		player.changed(player);
		return getGameDataFactory().blankEntityData(currentGameData);
	}

	

}

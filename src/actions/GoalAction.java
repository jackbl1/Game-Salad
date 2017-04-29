package actions;

import components.entityComponents.ComponentType;
import components.entityComponents.GoalComponent;
import entity.IEntity;
import entity.IEntityManager;
import gamedata.IRestrictedGameData;

public class GoalAction  extends AbstractAction  implements IAction{

	@Override
	public IRestrictedGameData executeAction(IEntity other, IEntity self, IEntityManager myEM, IRestrictedGameData currentGameData) {
		
		for(IEntity e : myEM.getEntities()){
			if(e.getComponent(ComponentType.Goal) != null){
				GoalComponent gc = (GoalComponent) e.getComponent(ComponentType.Goal);
				gc.satisfyGoal();
			}
		}
		return getGameDataFactory().blankEntityData(currentGameData);
	}

}
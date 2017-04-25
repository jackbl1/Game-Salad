package actions;

import java.util.ArrayList;
import java.util.List;

import class_annotations.BottomAction;
import class_annotations.LeftAction;
import class_annotations.RightAction;
import class_annotations.TopAction;
import components.entityComponents.ComponentType;
import components.entityComponents.ImagePropertiesComponent;
import components.entityComponents.SpriteComponent;
import entity.Entity;
import entity.IEntity;
import entity.IEntityManager;

@TopAction()
@LeftAction()
@BottomAction()
@RightAction()
public class Explosion implements IAction {
	
	public static final String EXPLOSION_IMAGE = "Feuer46.GIF";

	@Override
	public List<IEntity> executeAction(IEntity e, IEntity e2, IEntityManager myEM) {
		ImagePropertiesComponent ic = (ImagePropertiesComponent) e.getComponent(ComponentType.ImageProperties);
		return new ArrayList<IEntity>();
	}

}
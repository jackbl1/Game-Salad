package entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import components.ComponentType;
import components.IComponent;
import components.SpriteComponent;
import components.XYComponent;
import entity.restricted.IRestrictedEntity;
import entity.restricted.RestrictedEntity;
import entity.restricted.RestrictedEntityManager;
import gameView.Coordinate;

public class EntityManager implements IEntityManager{
	private Collection<Entity> myEntities;
	EntityManager(Collection<Entity> entities){
		myEntities = entities;
	}
	@Override
	public List<IComponent> getCertainComponents(ComponentType certainComponent) {
		List<IComponent> certainComponents = new ArrayList<IComponent>();
		for (Entity e : myEntities){
			certainComponents.add(e.getComponent(certainComponent));
		}
		return certainComponents;
	}

	@Override
	public RestrictedEntityManager getRestricted() {
		Collection<RestrictedEntity> certainComponents = new ArrayList<RestrictedEntity>();
		for (Entity e : myEntities){
			certainComponents.add( new RestrictedEntity(new Coordinate((XYComponent) e.getComponent(ComponentType.Location)),
					((SpriteComponent) e.getComponent(ComponentType.Sprite)).getClassPath()));
		}
		return new RestrictedEntityManager(certainComponents);
	}
	@Override
	public Map<IEntity, IRestrictedEntity> getEntityMap() {
		Map<IEntity, IRestrictedEntity> entityToRestricted = new HashMap<IEntity,IRestrictedEntity>();

		for (Entity e : myEntities){
			entityToRestricted.put(e, new RestrictedEntity(new Coordinate((XYComponent) e.getComponent(ComponentType.Location)),
					((SpriteComponent) e.getComponent(ComponentType.Sprite)).getClassPath()));
		};
		return entityToRestricted;
	}
	

}
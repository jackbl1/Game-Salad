package engines;

import java.util.List;

import components.ComponentType;
import entity.IEntityManager;

/**
 * The ISubEngine interface is the interface for engines that are put into action when a collision occurs. Each subEngine will need to have a public method handleCollision that the CollisionHandler can call
 * @author Vardhaan
 *
 */
public interface ISubEngine {

	
	/**
	 * Entity manager is necessary to get the necessary Components
	 * @param entManager
	 */
	public void addEntityManager(IEntityManager entManager);
	
	/**
	 * Each subengine will handle a collision in a different way by checking different components and acting on them.
	 */
	public void handleCollision();
	
	/**
	 * This method will return the ComponentType that the subEngine needs so that it receives that component, and nothing more.
	 * @return
	 */
	public List<ComponentType> getNecessaryComponents();
	
}

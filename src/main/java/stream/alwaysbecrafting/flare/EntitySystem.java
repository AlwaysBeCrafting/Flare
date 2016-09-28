package stream.alwaysbecrafting.flare;

//==============================================================================

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>Specialized {@link GameSystem} that handles a set of
 * {@link Entity Entities} matching some constraints
 *
 * <p>At some point before being added to a {@link GameEngine} (usually in the
 * constructor), a subclass of this class should call one or more of
 * {@link EntitySystem#requireAll(Class[])},
 * {@link EntitySystem#requireOne(Class[])}, or
 * {@link EntitySystem#forbid(Class[])} to describe which {@link Entity Entities} should be
 * given to its {@link EntitySystem#onHandleEntity(Entity, double)} method.
 *
 * <p>After the system is added, {@link EntitySystem#onHandleEntity(Entity, double)} will
 * be called each game loop for every {@link Entity} that matches the given
 * constraints.
 */
public abstract class EntitySystem extends GameSystem {
	//--------------------------------------------------------------------------

	private Set<Class<?>> requireAllTypes = new LinkedHashSet<>();
	private Set<Class<?>> requireOneTypes = new LinkedHashSet<>();
	private Set<Class<?>> forbidTypes     = new LinkedHashSet<>();

	// A flag to ensure implementors don't forget super.onUpdate()
	private boolean onUpdateCalled = false;

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	@Override public void onUpdate( GameEngine engine, double deltaTime ) {
		onUpdateCalled = true;

		engine.ENTITIES.stream()
				.filter( entity -> entity.hasAll( requireAllTypes ))
				.filter( entity -> entity.hasAny( requireOneTypes ))
				.filter( entity -> entity.hasNone( forbidTypes ))
				.forEach( entity -> onHandleEntity( entity, deltaTime ));
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Supply a list of components that must all be present in entities that
	 * will be handled by this system
	 *
	 * <p>A good place to call this is in a constructor
	 *
	 * @param componentTypes The components to include
	 */
	protected void requireAll( Class<?>... componentTypes ) {
		Collections.addAll( requireAllTypes, componentTypes );
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Supply a list of components from which at least one must be present in
	 * entities that will be handled by this system
	 *
	 * <p>May only be called before the system is added to a {@link GameEngine},
	 * and may not be called more than once
	 *
	 * <p>A good place to call this is in a constructor
	 *
	 * @param componentTypes The components to include
	 * @throws IllegalStateException If {@code requireOne()} has been called before, or if this system is already added to an engine
	 */
	protected void requireOne( Class<?>... componentTypes ) {
		Collections.addAll( requireOneTypes, componentTypes );
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Supply a list of components that must not be present in entities that will
	 * be handled by this system
	 *
	 * <p>May only be called before the system is added to a {@link GameEngine},
	 * and may not be called more than once
	 *
	 * <p>A good place to call this is in a constructor
	 *
	 * @param componentTypes The components to exclude
	 * @throws IllegalStateException If {@code forbid()} has been called before, or if this system is already added to an engine
	 */
	protected void forbid( Class<?>... componentTypes ) {
		Collections.addAll( forbidTypes, componentTypes );
	}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	/**
	 * <p>Override to do something with the entities that match this system's
	 * filter parameters
	 *
	 * @param entity A matching {@code Entity}
	 * @param deltaTime The time given to {@link GameEngine#update(double)} for
	 *                  this iteration of the game loop; ostensibly, the time
	 *                  between the previous loop and the current one
	 */
	protected abstract void onHandleEntity( Entity entity, double deltaTime );

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	@Override void update( GameEngine engine, double deltaTime ) {
		onUpdateCalled = false;
		super.update( engine, deltaTime );

		if ( !onUpdateCalled ) throw new IllegalStateException( "Need to call super.onUpdate() to handle entities in an EntitySystem" );
	}

	//--------------------------------------------------------------------------
}
//------------------------------------------------------------------------------

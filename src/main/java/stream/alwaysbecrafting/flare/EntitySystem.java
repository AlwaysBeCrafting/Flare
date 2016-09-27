package stream.alwaysbecrafting.flare;

//==============================================================================
/**
 * <p>Specialized {@link GameSystem} that handles a set of
 * {@link Entity Entities} matching some constraints
 *
 * <p>At some point before being added to a {@link GameEngine} (usually in the
 * constructor), a subclass of this class should call one or more of
 * {@link this#requireAll(Class[])}, {@link this#requireOne(Class[])}, or
 * {@link this#forbid(Class[])} to describe which {@link Entity Entities} should be
 * given to its {@link this#onHandleEntity(Entity, float)} method.
 *
 * <p>After the system is added, {@link this#onHandleEntity(Entity, float)} will
 * be called each game loop for every {@link Entity} that matches the given
 * constraints.
 */
public abstract class EntitySystem extends GameSystem {
	//--------------------------------------------------------------------------

	private EntityFilter componentFilter;

	private Class<?>[] requireAllTypes = new Class<?>[0];
	private Class<?>[] requireOneTypes = new Class<?>[0];
	private Class<?>[] forbidTypes = new Class<?>[0];

	// A flag to ensure implementors don't forget super.onUpdate()
	private boolean onUpdateCalled = false;

	//--------------------------------------------------------------------------

	/**
	 * <p>Supply a list of components that must all be present in entities that
	 * will be handled by this system
	 *
	 * <p>May only be called before the system is added to a {@link GameEngine},
	 * and may not be called more than once
	 *
	 * <p>A good place to call this is in a constructor
	 *
	 * @param componentTypes The components to include
	 * @throws IllegalStateException If {@code requreAll()} has been called before, or if this system is already added to an engine
	 */
	protected void requireAll( Class<?>... componentTypes )
	throws IllegalStateException {
		if ( requireAllTypes.length != 0 || componentFilter != null ) throw new IllegalStateException();
		requireAllTypes = componentTypes;
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
	protected void requireOne( Class<?>... componentTypes )
	throws IllegalStateException {
		if ( requireOneTypes.length != 0 || componentFilter != null ) throw new IllegalStateException();
		requireOneTypes = componentTypes;
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
	protected void forbid( Class<?>... componentTypes )
	throws IllegalStateException {
		if ( forbidTypes.length != 0 || componentFilter != null ) throw new IllegalStateException();
		forbidTypes = componentTypes;
	}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	@Override public void onUpdate( GameEngine engine, float deltaTime ) {
		onUpdateCalled = true;

		for ( Entity entity : getFilter() ) {
			onHandleEntity( entity, deltaTime );
		}
	}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	/**
	 * <p>Override to do something with the entities that match this system's
	 * filter parameters
	 *
	 * @param entity A matching {@code Entity}
	 * @param deltaTime The time given to {@link GameEngine#update(float)} for
	 *                  this iteration of the game loop; ostensibly, the time
	 *                  between the previous loop and the current one
	 */
	protected abstract void onHandleEntity( Entity entity, float deltaTime );

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	@Override void update( GameEngine engine, float deltaTime ) {
		onUpdateCalled = false;
		super.update( engine, deltaTime );

		if ( !onUpdateCalled ) throw new IllegalStateException( "Need to call super.onUpdate() to handle entities in an EntitySystem" );
	}

	//--------------------------------------------------------------------------

	@Override EntityFilter getFilter() {
		if ( componentFilter == null ){
			componentFilter = new EntityFilter(
					requireAllTypes,
					requireOneTypes,
					forbidTypes );
		}

		return componentFilter;
	}

	//--------------------------------------------------------------------------
}
//------------------------------------------------------------------------------

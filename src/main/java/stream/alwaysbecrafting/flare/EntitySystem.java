package stream.alwaysbecrafting.flare;

//==============================================================================
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
	 * <p>A good place to call this is from a subclass's constructor
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
	 * <p>A good place to call this is from a subclass's constructor
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
	 * <p>A good place to call this is from a subclass's constructor
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

		for ( long entityId : getFilter() ) {
			onHandleEntity( engine, entityId, deltaTime );
		}
	}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	protected abstract void onHandleEntity( GameEngine engine, long entityId, float deltaTime );

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	@Override void update( GameEngine engine, float deltaTime ) {
		onUpdateCalled = false;
		super.update( engine, deltaTime );

		if ( !onUpdateCalled ) throw new IllegalStateException( "Need to call super.onUpdate() to handle entities in an EntitySystem" );
	}

	//--------------------------------------------------------------------------

	EntityFilter getFilter() {
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

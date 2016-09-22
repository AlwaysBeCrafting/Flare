package stream.alwaysbecrafting.ecs;

//==============================================================================
public abstract class EntitySystem extends GameSystem {
	//--------------------------------------------------------------------------

	private EntityFilter componentFilter;

	private Class<?>[] includeAllTypes = new Class<?>[0];
	private Class<?>[] includeAnyTypes = new Class<?>[0];
	private Class<?>[] excludeTypes = new Class<?>[0];

	// A flag to ensure implementors don't forget super.onUpdate()
	private boolean onUpdateCalled = false;

	//--------------------------------------------------------------------------

	/**
	 * Supply a list of components that must all be present in entities that
	 * will be handled by this system
	 *
	 * May only be called before the system is added to a {@link GameEngine},
	 * and may not be called more than once
	 *
	 * A good place to call this is from a subclass's constructor
	 *
	 * @param componentTypes The components to include
	 * @throws IllegalStateException If {@code includeAll()} has been called before, or if this system is already added to an engine
	 */
	protected void includeAll( Class<?>... componentTypes )
	throws IllegalStateException {
		if ( includeAllTypes.length != 0 || componentFilter != null ) throw new IllegalStateException();
		includeAllTypes = componentTypes;
	}

	//--------------------------------------------------------------------------

	/**
	 * Supply a list of components from which at least one must be present in
	 * entities that will be handled by this system
	 *
	 * May only be called before the system is added to a {@link GameEngine},
	 * and may not be called more than once
	 *
	 * A good place to call this is from a subclass's constructor
	 *
	 * @param componentTypes The components to include
	 * @throws IllegalStateException If {@code includeAny()} has been called before, or if this system is already added to an engine
	 */
	protected void includeAny( Class<?>... componentTypes )
	throws IllegalStateException {
		if ( includeAnyTypes.length != 0 || componentFilter != null ) throw new IllegalStateException();
		includeAnyTypes = componentTypes;
	}

	//--------------------------------------------------------------------------

	/**
	 * Supply a list of components that must not be present in entities that will
	 * be handled by this system
	 *
	 * May only be called before the system is added to a {@link GameEngine},
	 * and may not be called more than once
	 *
	 * A good place to call this is from a subclass's constructor
	 *
	 * @param componentTypes The components to exclude
	 * @throws IllegalStateException If {@code exclude()} has been called before, or if this system is already added to an engine
	 */
	protected void exclude( Class<?>... componentTypes )
	throws IllegalStateException {
		if ( excludeTypes.length != 0 || componentFilter != null ) throw new IllegalStateException();
		excludeTypes = componentTypes;
	}

	//--------------------------------------------------------------------------

	/**
	 * @return The filter used to select entities for this system to handle
	 */
	EntityFilter getFilter() {
		if ( componentFilter == null ){
			componentFilter = new EntityFilter(
					includeAllTypes,
					includeAnyTypes,
					excludeTypes );
		}

		return componentFilter;
	}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	@Override public void update( GameEngine engine, float deltaTime ) {
		onUpdateCalled = false;
		super.update( engine, deltaTime );

		if ( !onUpdateCalled ) throw new IllegalStateException( "Need to call super.onUpdate() to handle entities in an EntitySystem" );
	}

	//--------------------------------------------------------------------------

	@Override public void onUpdate( GameEngine engine, float deltaTime ) {
		for ( long entityId : getFilter() ) {
			onHandleEntity( engine, entityId, deltaTime );
		}

		onUpdateCalled = true;
	}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	protected abstract void onHandleEntity( GameEngine engine, long entityId, float deltaTime );

	//--------------------------------------------------------------------------
}
//------------------------------------------------------------------------------

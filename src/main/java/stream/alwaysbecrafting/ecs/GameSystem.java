package stream.alwaysbecrafting.ecs;

//==============================================================================
/**
 * <p>Superclass for game systems which do some work on the main loop
 *
 * <p>If you need to iterate over a group of game entities each cycle, consider
 * subclassing {@link EntitySystem} instead.
 */
public abstract class GameSystem {
	//--------------------------------------------------------------------------

	/**
	 * <p>Set the priority order for this system to run this system in
	 *
	 * <p>In the future, {@link GameEngine} may implement concurrency that allows
	 * systems at the same priority to run in parallel
	 *
	 * <p>May only be called before the system is added to a {@link GameEngine}
	 *
	 * <p>A good place to call this is from a subclass's constructor
	 *
	 * @param priority The priority to run at (lower is earlier)
	 * @throws IllegalStateException If this system is already added to an engine
	 */
	public final void setPriority( int priority )
	throws IllegalStateException {
		throw new UnsupportedOperationException( "setPriority() is not yet implemented" );
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Override to respond when this system is added to an engine and initialized
	 *
	 * @param engine the {@link GameEngine} the system is added to
	 */
	public void onStart( GameEngine engine ) {}

	//--------------------------------------------------------------------------

	/**
	 * <p>Override to respond when this system is resumed from a paused state
	 *
	 * <p>Systems are considered paused upon initialization, and
	 * {@code onResume()} will always be called immediately after
	 * {@link GameSystem#onStart( GameEngine )} when initializing
	 */
	public void onResume( GameEngine engine ) {}

	//--------------------------------------------------------------------------

	/**
	 * <p>Override to implement this system's main game loop behavior. This is
	 * where the system will do most of its work, and is called once for each
	 * time {@link GameEngine#update(float)} is called on the attached
	 * {@link GameEngine}.
	 *
	 * @param deltaTime The amount of time between the {@code Engine}'s last
	 *                  call to {@code update()} and the current one. Note that
	 *                  this is not the same as the time between this method's
	 *                  own calls.
	 */
	public void onUpdate( GameEngine engine, float deltaTime ) {}

	//--------------------------------------------------------------------------

	/**
	 * <p>Override to respond when this system is paused
	 *
	 * <p>{@code onPause()} will always be called immediately before
	 *
	 * {@link GameSystem#onStop( GameEngine )} when shutting down
	 */
	public void onPause( GameEngine engine ) {}

	//--------------------------------------------------------------------------

	/**
	 * <p>Override to respond when this system is removed from an engine and shut
	 * down
	 *
	 * @param engine the {@link GameEngine} the system is removed from
	 */
	public void onStop( GameEngine engine ) {}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	void update( GameEngine engine, float deltaTime ) {
		onUpdate( engine, deltaTime );
	}

	//--------------------------------------------------------------------------
}
//------------------------------------------------------------------------------

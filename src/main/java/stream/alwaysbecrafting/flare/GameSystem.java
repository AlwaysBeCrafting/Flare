package stream.alwaysbecrafting.flare;

//==============================================================================
/**
 * <p>Superclass for game systems which do some work on the main loop
 *
 * <p>If you need to iterate over a group of game entities each cycle, consider
 * subclassing {@link EntitySystem} instead.
 */
public abstract class GameSystem implements Comparable<GameSystem> {
	//--------------------------------------------------------------------------

	int priority = Integer.MIN_VALUE;
	boolean isPaused = true;

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	@Override public int compareTo( GameSystem other ) {
		return Integer.compare( priority, other.priority );
	}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	/**
	 * <p>Resume a paused system
	 */
	public void resume() {
		if ( isPaused ) onResume();
		isPaused = false;
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Pause a running system
	 */
	public void pause() {
		if ( !isPaused ) onPause();
		isPaused = true;
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
	 * {@link GameSystem#onStart(GameEngine)} when initializing
	 */
	public void onResume() {}

	//--------------------------------------------------------------------------

	/**
	 * <p>Called when this system should execute its main game loop behavior.
	 * This is where the system will do most of its work, and is called once for
	 * each time {@link GameEngine#update(float)} is called on the attached
	 * {@link GameEngine}.
	 *
	 * @param deltaTime The time given to {@link GameEngine#update(float)} for
	 *                  this iteration of the game loop; ostensibly, the time
	 *                  between the previous loop and the current one
	 */
	public void onUpdate( GameEngine engine, float deltaTime ) {}

	//--------------------------------------------------------------------------

	/**
	 * <p>Override to respond when this system is paused
	 *
	 * <p>{@code onPause()} will always be called immediately before
	 * {@link GameSystem#onStop(GameEngine)} when shutting down
	 */
	public void onPause() {}

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

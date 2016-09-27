package stream.alwaysbecrafting.flare;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

//==============================================================================
/**
 * <p>The provider for the game's main loop logic
 *
 * <p>{@code GameEngine} should usually not be extended; instead, users of this
 * class should create a {@code new GameEngine()} and add subclasses of
 * {@link GameSystem} to handle the implementation.
 */
public class GameEngine {
	//--------------------------------------------------------------------------

	final Set<Entity> ENTITIES = new HashSet<>();


	private final SortedMap<GameSystem,Class<? extends GameSystem>> SYSTEMS = new ConcurrentSkipListMap<>();

	private boolean isPaused = false;

	//--------------------------------------------------------------------------

	/**
	 * <p>Add a {@link GameSystem} to the engine, calling its
	 * {@link GameSystem#onStart(GameEngine)} method and setting it to the
	 * lowest priority.
	 *
	 * @param system The system to add
	 */
	public void add( GameSystem system ) {
		if ( SYSTEMS.containsValue( system.getClass() )) {
			throw new IllegalStateException(
					system.getClass().getName() + " already exists in engine" );
		} else {
			if ( system.priority == Integer.MIN_VALUE ) {
				if ( SYSTEMS.isEmpty() ) system.priority = 0;
				else system.priority = SYSTEMS.lastKey().priority + 1;
			}
			SYSTEMS.put( system, system.getClass() );
		}

		system.onStart( this );
		system.resume();
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Add a {@link GameSystem} to the engine, calling its
	 * {@link GameSystem#onStart(GameEngine)} method and setting it to the given
	 * priority.
	 *
	 * @param system The system to add
	 * @param priority The priority to set. In future releases, systems at the
	 *                 same priority will be eligible for concurrent execution.
	 */
	public void add( GameSystem system, int priority ) {
		system.priority = priority;
		add( system );
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Tell all {@link GameSystem}s in this engine to execute.
	 *
	 * <p>Call this from your main game loop to perform all entity updates and
	 * rendering.
	 *
	 * @param deltaTime The time, in seconds, since the last update
	 */
	public void update( float deltaTime ) {
		SYSTEMS.keySet().forEach( system -> {
			system.update( this, deltaTime );
		} );
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Remove a {@link GameSystem} from the engine
	 * @param system The system to remove
	 */
	public void remove( GameSystem system ) {
		if ( SYSTEMS.remove( system, system.getClass() )) {
			system.pause();
			system.onStop( this );
		}
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Remove a {@link GameSystem} from the engine
	 * @param systemType The type of the system to remove
	 */
	public void remove( Class<? extends GameSystem> systemType ) {
		SYSTEMS.entrySet().stream()
				.filter( entry -> entry.getValue() == systemType )
				.map( Map.Entry::getKey )
				.forEach( this::remove );
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Add one or more {@link Entity Entities} to the engine
	 * @param entities Entities to add
	 */
	public void add( Entity... entities ) {
		Collections.addAll( ENTITIES, entities );
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Remove one or more {@link Entity Entities} from the engine
	 * @param entity Entity to remove
	 */
	public void remove( Entity entity ) {
		ENTITIES.remove( entity );
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Temporarily stop <i>all</i> processing on this engine, until a
	 * subsequent call to {@link GameEngine#resume()} is made. Calling
	 * {@link GameEngine#update(float)} while paused does nothing.
	 *
	 * <p>Also calls {@link GameSystem#onPause()} on all attached systems which
	 * are not already paused
	 */
	public void pause() {
		if ( !isPaused ) {
			SYSTEMS.keySet().forEach( system -> {
				if ( !system.isPaused ) system.onPause();
			} );
		}
		isPaused = true;
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Resume handling update calls, and call {@link GameSystem#onResume()}
	 * on all attached systems which were running before the engine was paused.
	 */
	public void resume() {
		if ( isPaused ) {
			SYSTEMS.keySet().forEach( system -> {
				if ( !system.isPaused ) system.onResume();
			} );
		}
		isPaused = false;
	}

	//--------------------------------------------------------------------------
}
//------------------------------------------------------------------------------

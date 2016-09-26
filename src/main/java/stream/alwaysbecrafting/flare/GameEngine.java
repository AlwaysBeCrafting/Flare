package stream.alwaysbecrafting.flare;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

//==============================================================================
/**
 * <p>The class providing the main loop of the game.
 *
 * <p>{@code GameEngine} should usually not be extended; instead, users of this
 * class should create a {@code new GameEngine()} and add subclasses of
 * {@link GameSystem} to handle the implementation.
 */
public class GameEngine {
	//--------------------------------------------------------------------------

	private final SortedMap<GameSystem,Class<? extends GameSystem>> SYSTEMS = new ConcurrentSkipListMap<>();

	private final Set<Entity> ENTITIES = new HashSet<>();


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
	 * @param priority The priority to set. Systems at the same priority are
	 *                 eligible for concurrent execution.
	 */
	public void add( GameSystem system, int priority ) {
		system.priority = priority;
		add( system );
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Add an {@link EntitySystem} to the engine, calling its
	 * {@link GameSystem#onStart(GameEngine)} method and placing it at the end
	 * of the system queue.
	 *
	 * <p>If some entities already exist in this engine, they
	 * are assigned to the new system as defined in its filter parameters.
	 *
	 * @param system The system to add
	 * @see EntitySystem#requireAll(Class[])
	 * @see EntitySystem#requireOne(Class[])
	 * @see EntitySystem#forbid(Class[])
	 */
	public void add( EntitySystem system ) {
		ENTITIES.forEach( system.getFilter()::offer );
		add( (GameSystem)system );
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Tell all {@link GameSystem}s in this engine to execute.<br/><br/>
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

	public void remove( Class<? extends GameSystem> systemType ) {
		SYSTEMS.entrySet().stream()
				.filter( entry -> entry.getValue() == systemType )
				.map( Map.Entry::getKey )
				.forEach( this::remove );
	}

	//--------------------------------------------------------------------------

	public void add( Entity... entities ) {
		List<Entity> entityList = Arrays.asList( entities );
		ENTITIES.addAll( entityList );

		SYSTEMS.forEach(( system, systemType ) -> {
			entityList.forEach( system.getFilter()::offer );
		} );
	}

	//--------------------------------------------------------------------------

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

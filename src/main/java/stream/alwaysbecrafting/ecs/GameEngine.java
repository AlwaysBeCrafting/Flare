package stream.alwaysbecrafting.ecs;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.sun.istack.internal.NotNull;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
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

	// Systems
	private final SortedMap<GameSystem,Class<? extends GameSystem>> SYSTEMS = new ConcurrentSkipListMap<>();

	// Entities
	private final SortedSet<Long> ENTITIES = new TreeSet<>();

	// Components
	private final Table<Long,Class<?>,Object> COMPONENTS = HashBasedTable.create();


	private boolean isPaused = false;

	//--------------------------------------------------------------------------

	/**
	 * <p>Retrieve a component of a certain type from a given entity. This is best
	 * used within an {@link EntitySystem}, where filtering can guarantee that
	 * certain components will be available for a given ID.
	 *
	 * @param entityId The ID of the entity to retrieve the component from
	 * @param componentType The class of the component to retrieve
	 * @return A component matching the ID and type provided, or {@code null} if none exists
	 */
	@NotNull public final <T> T getComponent( long entityId, Class<T> componentType ) {
		try {
			return (T)COMPONENTS.get( entityId, componentType );

		} catch ( ClassCastException e ) { // Something went really wrong here
			return null;
		}
	}

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
		COMPONENTS.rowKeySet().forEach( entityId -> {
			system.getFilter().offer( entityId, COMPONENTS.row( entityId ).keySet() );
		} );
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

	/**
	 * <p>Create a new entity from a list of components
	 *
	 * @param components The components to give to the new entity
	 * @return The ID of the newly created component
	 */
	public long createEntity( Object... components ) {
		long entityId = 0;

		try {
			entityId = ENTITIES.last() + 1;

		} catch ( NoSuchElementException ex ) { /* This is fine, entityId = 0 */ }

		ENTITIES.add( entityId );

		for ( Object component : components ) {
			COMPONENTS.put( entityId, component.getClass(), component );
		}

		stream.alwaysbecrafting.ecs.Log.d( "" + ENTITIES.size() + " entities" );

		offerToAll( entityId );

		return entityId;
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Add a component to an existing entity
	 *
	 * @param entityId The ID of the entity to add the component to
	 * @param component The component to add
	 * @throws IllegalArgumentException When the ID provided does not belong to an existing entity
	 */
	public void add( long entityId, Object component ) throws IllegalArgumentException {
		if ( !ENTITIES.contains( entityId )) {
			throw new IllegalArgumentException(
					"Tried to add component " +
							component +
							" to non-existent entity " +
							entityId );
		}

		COMPONENTS.put( entityId, component.getClass(), component );

		offerToAll( entityId );
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Remove an entity and all its components
	 *
	 * @param entityId The ID of the entity to remove
	 */
	public void remove( long entityId ) {
		ENTITIES.remove( entityId );
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
	//--------------------------------------------------------------------------

	private void offerToAll( long entityId ) {
		SYSTEMS.keySet().stream()
				.filter( system -> system instanceof EntitySystem )
				.map( system -> ( (EntitySystem)system ).getFilter()  )
				.forEach( filter -> {
					filter.offer( entityId, COMPONENTS.row( entityId ).keySet() );
				} );
	}

	//--------------------------------------------------------------------------
}
//------------------------------------------------------------------------------

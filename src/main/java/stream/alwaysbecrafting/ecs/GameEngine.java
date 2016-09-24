package stream.alwaysbecrafting.ecs;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.common.collect.Table;
import com.sun.istack.internal.NotNull;

import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
	private final Set<Class<? extends GameSystem>> SYSTEM_TYPES = new LinkedHashSet<>();
	private final ClassToInstanceMap<GameSystem> SYSTEMS = MutableClassToInstanceMap.create();


	// Entities
	private final SortedSet<Long> ENTITIES = new TreeSet<>();


	// Components
	private final Table<Long,Class<?>,Object> COMPONENTS = HashBasedTable.create();

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
	 * {@link GameSystem#onStart(GameEngine)} method and placing it at the end
	 * of the system queue.
	 *
	 * @param system The system to add
	 */
	public void add( GameSystem system ) {
		if ( !SYSTEM_TYPES.add( system.getClass() )) {
			throw new IllegalStateException(
					system.getClass().getName() + " already exists in engine" );
		}

		SYSTEMS.put( system.getClass(), system );
		system.onStart( this );
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
		for ( long entityId : COMPONENTS.rowKeySet() ) {
			system.getFilter().offer( entityId, COMPONENTS.row( entityId ).keySet() );
		}
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
		for ( Class<? extends GameSystem> type : SYSTEM_TYPES ) {
			SYSTEMS.getInstance( type ).update( this, deltaTime );
		}
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Remove a {@link GameSystem} from the engine
	 * @param system The system to remove
	 */
	public void remove( GameSystem system ) {
		if ( SYSTEMS.remove( system.getClass(), system )) {
			SYSTEM_TYPES.remove( system.getClass() );
		}
	}

	//--------------------------------------------------------------------------

	public void remove( Class<? extends GameSystem> systemType ) {
		if ( SYSTEMS.containsKey( systemType )) {
			remove( SYSTEMS.get( systemType ));
		}
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
	//--------------------------------------------------------------------------

	private void offerToAll( long entityId ) {
		for ( Class<? extends GameSystem> systemType : SYSTEM_TYPES ) {
			try {
				final EntitySystem system = (EntitySystem)SYSTEMS.get( systemType );
				system.getFilter().offer( entityId, COMPONENTS.row( entityId ).keySet() );

			} catch ( ClassCastException ex ) { continue; }
		}
	}

	//--------------------------------------------------------------------------
}
//------------------------------------------------------------------------------

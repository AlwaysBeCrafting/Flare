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

	@NotNull public final <T> T getComponent( long entityId, Class<T> componentType ) {
		try {
			return (T)COMPONENTS.get( entityId, componentType );

		} catch ( ClassCastException e ) { // Something went really wrong here
			return null;
		}
	}

	//--------------------------------------------------------------------------

	public void add( GameSystem system ) {
		if ( !SYSTEM_TYPES.add( system.getClass() )) {
			throw new IllegalStateException(
					system.getClass().getName() + " already exists in engine" );
		}

		SYSTEMS.put( system.getClass(), system );
		system.onStart( this );
	}

	//--------------------------------------------------------------------------

	public void add( EntitySystem system ) {
		for ( long entityId : COMPONENTS.rowKeySet() ) {
			system.getFilter().offer( entityId, COMPONENTS.row( entityId ).keySet() );
		}
		add( (GameSystem)system );
	}
	//--------------------------------------------------------------------------

	public void update( float deltaTime ) {
		for ( Class<? extends GameSystem> type : SYSTEM_TYPES ) {
			SYSTEMS.getInstance( type ).update( this, deltaTime );
		}
	}

	//--------------------------------------------------------------------------

	public long createEntity( Object... components ) {
		long entityId = 0;
		try {
			entityId = ENTITIES.last() + 1;

		} catch ( NoSuchElementException ex ) {
			ENTITIES.add( entityId );
		}

		for ( Object component : components ) {
			COMPONENTS.put( entityId, component.getClass(), component );
		}

		offerToAll( entityId );

		return entityId;
	}

	//--------------------------------------------------------------------------

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

	private void offerToAll( long entityId ) {
		for ( Class<? extends GameSystem> systemType : SYSTEM_TYPES ) {
			try {
				final EntitySystem system = (EntitySystem)SYSTEMS.get( systemType );
				system.getFilter().offer( entityId, COMPONENTS.row( entityId ).keySet() );

			} catch ( ClassCastException ex ) { continue; }
		}
	}

	//--------------------------------------------------------------------------

	public void remove( long entityId ) {
		ENTITIES.remove( entityId );
	}

	//--------------------------------------------------------------------------
}
//------------------------------------------------------------------------------

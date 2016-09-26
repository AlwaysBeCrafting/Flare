package stream.alwaysbecrafting.flare;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

//==============================================================================
class EntityFilter implements Iterable<Entity> {
	//--------------------------------------------------------------------------

	static final EntityFilter MATCH_NONE = new EntityFilter() {
		@Override public Iterator<Entity> iterator() { return Collections.emptyIterator(); }
		@Override boolean offer( Entity entity ) { return false; }
	};

	//--------------------------------------------------------------------------

	private final Set<Class<?>> REQUIRE_ALL_TYPES = new HashSet<>();
	private final Set<Class<?>> REQUIRE_ONE_TYPES = new HashSet<>();
	private final Set<Class<?>> FORBID_TYPES      = new HashSet<>();

	private final Set<Entity> MATCHING_ENTITIES = new HashSet<>();

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	private EntityFilter() {}

	//--------------------------------------------------------------------------

	EntityFilter(
			Class<?>[] requireAllTypes,
			Class<?>[] requireOneTypes,
			Class<?>[] forbidTypes ) {
		Collections.addAll( REQUIRE_ALL_TYPES, requireAllTypes );
		Collections.addAll( REQUIRE_ONE_TYPES, requireOneTypes );
		Collections.addAll( FORBID_TYPES,      forbidTypes     );
	}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	@Override public Iterator<Entity> iterator() {
		return new ComponentIterator();
	}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	boolean offer( Entity entity ) {
		if ( matches( entity )) {
			MATCHING_ENTITIES.add( entity );
			return true;

		} else {
			MATCHING_ENTITIES.remove( entity );
			return false;
		}
	}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	private boolean matches( Entity entity ) {
		return hasAllComponents( entity, REQUIRE_ALL_TYPES )
		&&     hasAnyComponent(  entity, REQUIRE_ONE_TYPES )
		&&     hasNoComponent(   entity, FORBID_TYPES      );
	}

	//--------------------------------------------------------------------------

	private boolean hasAllComponents( Entity entity, Set<Class<?>> types ) {
		return entity.getComponentTypes().containsAll( types );
	}

	//--------------------------------------------------------------------------

	private boolean hasAnyComponent( Entity entity, Set<Class<?>> types ) {
		return types.isEmpty()
		|| entity.getComponentTypes().stream().anyMatch( types::contains );
	}

	//--------------------------------------------------------------------------

	private boolean hasNoComponent( Entity entity, Set<Class<?>> types ) {
		return entity.getComponentTypes().stream().noneMatch( types::contains );
	}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	//==========================================================================
	private class ComponentIterator implements Iterator<Entity> {
		//----------------------------------------------------------------------

		private Iterator<Entity> INDEX = MATCHING_ENTITIES.iterator();

		//----------------------------------------------------------------------

		@Override public boolean hasNext() {
			return INDEX.hasNext();
		}

		//----------------------------------------------------------------------

		@Override public Entity next() {
			return INDEX.next();
		}

		//----------------------------------------------------------------------
	}
	//--------------------------------------------------------------------------
}
//------------------------------------------------------------------------------

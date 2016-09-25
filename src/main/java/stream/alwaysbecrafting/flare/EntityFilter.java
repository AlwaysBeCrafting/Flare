package stream.alwaysbecrafting.flare;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

//==============================================================================
class EntityFilter implements Iterable<Long> {
	//--------------------------------------------------------------------------

	private final Class<?>[] REQUIRE_ALL_TYPES;
	private final Class<?>[] REQUIRE_ONE_TYPES;
	private final Class<?>[] FORBID_TYPES;

	private final Set<Long> MATCHING_ENTITIES = new HashSet<>();

	//--------------------------------------------------------------------------

	EntityFilter(
			Class<?>[] requireAllTypes,
			Class<?>[] requireOneTypes,
			Class<?>[] forbidTypes ) {
		REQUIRE_ALL_TYPES = requireAllTypes;
		REQUIRE_ONE_TYPES = requireOneTypes;
		FORBID_TYPES = forbidTypes;
	}

	//--------------------------------------------------------------------------

	@Override public Iterator<Long> iterator() {
		return new ComponentIterator();
	}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	boolean offer( long entityId, Set<Class<?>> componentTypes ) {
		if ( matches( componentTypes )) {
			MATCHING_ENTITIES.add( entityId );
			return true;

		} else {
			MATCHING_ENTITIES.remove( entityId );
			return false;
		}
	}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	private boolean matches( Set<Class<?>> componentTypes ) {
		for ( Class<?> type : REQUIRE_ALL_TYPES ) {
			if ( !componentTypes.contains( type )) return false;
		}

		for ( Class<?> type : FORBID_TYPES ) {
			if ( componentTypes.contains( type )) return false;
		}

		if ( REQUIRE_ONE_TYPES == null || REQUIRE_ONE_TYPES.length == 0 ) return true;
		for ( Class<?> type : REQUIRE_ONE_TYPES ) {
			if ( componentTypes.contains( type )) return true;
		}

		return false;
	}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	//==========================================================================
	private class ComponentIterator implements Iterator<Long> {
		//----------------------------------------------------------------------

		private Iterator<Long> INDEX = MATCHING_ENTITIES.iterator();

		//----------------------------------------------------------------------

		@Override public boolean hasNext() {
			return INDEX.hasNext();
		}

		//----------------------------------------------------------------------

		@Override public Long next() {
			return INDEX.next();
		}

		//----------------------------------------------------------------------
	}
	//--------------------------------------------------------------------------
}
//------------------------------------------------------------------------------

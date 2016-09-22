package stream.alwaysbecrafting.ecs;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

//==============================================================================
class EntityFilter implements Iterable<Long> {
	//--------------------------------------------------------------------------

	private final Class<?>[] INCLUDE_ALL_TYPES;
	private final Class<?>[] INCLUDE_ANY_TYPES;
	private final Class<?>[] EXCLUDE_TYPES;

	private final Set<Long> MATCHING_ENTITIES = new HashSet<>();

	//--------------------------------------------------------------------------

	EntityFilter(
			Class<?>[] includeAllTypes,
			Class<?>[] includeAnyTypes,
			Class<?>[] excludeTypes ) {
		INCLUDE_ALL_TYPES = includeAllTypes;
		INCLUDE_ANY_TYPES = includeAnyTypes;
		EXCLUDE_TYPES     = excludeTypes;
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
		for ( Class<?> type : INCLUDE_ALL_TYPES ) {
			if ( !componentTypes.contains( type )) return false;
		}

		for ( Class<?> type : EXCLUDE_TYPES ) {
			if ( componentTypes.contains( type )) return false;
		}

		if ( INCLUDE_ANY_TYPES == null || INCLUDE_ANY_TYPES.length == 0 ) return true;
		for ( Class<?> type : INCLUDE_ANY_TYPES ) {
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

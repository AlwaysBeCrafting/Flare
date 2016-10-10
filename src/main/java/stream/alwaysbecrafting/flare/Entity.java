package stream.alwaysbecrafting.flare;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

//==============================================================================
/**
 * <p>A group of components that exist and are processed together
 *
 * <p>{@code Entity} should usually not be extended; instead, users of this
 * class should create new {@code Entities}, give them components in
 * {@link Entity#add(Object)}, and then add the {@code Entity} to the engine
 * with {@link GameEngine#add(Entity)}.
 */
public class Entity {
	//--------------------------------------------------------------------------

	private final Map<Class<?>,Object> COMPONENTS = new HashMap<>();

	GameEngine engine;

	//--------------------------------------------------------------------------

	public Entity( Object... components ) {
		Arrays.asList( components ).forEach( component -> {
			COMPONENTS.put( component.getClass(), component );
		} );
	}

	//--------------------------------------------------------------------------

	public GameEngine getEngine() { return engine; }

	//--------------------------------------------------------------------------

	Collection<Class<?>> getComponentTypes() { return COMPONENTS.keySet(); }

	//--------------------------------------------------------------------------

	Collection getComponents() { return COMPONENTS.values(); }

	//--------------------------------------------------------------------------

	/**
	 * <p>Add a component to this {@code Entity} if a component of the same type
	 * does not already exist
	 *
	 * @param component The component to add
	 *
	 * @return {@code true} if the component was added, else {@code false}
	 */
	public boolean add( Object component ) {
		return COMPONENTS.putIfAbsent( component.getClass(), component ) == null;
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Retrieves a component from this {@code Entity}
	 *
	 * @param componentType The class of the component to retrieve
	 *
	 * @return If it exists, the component of the given class, else {@code null}
	 */
	public <T> T get( Class<T> componentType ) {
		return (T)COMPONENTS.get( componentType );
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Removes a component from this {@code Entity}
	 *
	 * @param componentType The class of the component to retrieve
	 *
	 * @return {@code true} if a component was removed, else {@code false}
	 */
	public boolean remove( Class<?> componentType ) {
		return COMPONENTS.remove( componentType ) != null;
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Removes a component from this {@code Entity}
	 *
	 * @param component The class of the component to retrieve
	 *
	 * @return {@code true} if a component was removed, else {@code false}
	 */
	public boolean remove( Object component ) {
		return COMPONENTS.remove( component.getClass(), component );
	}

	//--------------------------------------------------------------------------

	/**
	 * @param componentTypes Classes to match against this entity's components
	 *
	 * @return {@code true} if there is a component of every listed type, else {@code false}
	 */
	public boolean hasAll( Collection<Class<?>> componentTypes ) {
		return COMPONENTS.keySet().containsAll( componentTypes );
	}

	//--------------------------------------------------------------------------

	/**
	 * @param componentTypes Classes to match against this entity's components
	 *
	 * @return {@code true} if there is a component of every listed type, else {@code false}
	 */
	public boolean hasAll( Class<?>... componentTypes ) {
		return Stream.of( componentTypes ).allMatch( COMPONENTS::containsKey );
	}

	//--------------------------------------------------------------------------

	/**
	 * @param componentTypes Classes to match against this entity's components
	 *
	 * @return {@code true} if there is a component of at least one listed type, else {@code false}
	 */
	public boolean hasAny( Collection<Class<?>> componentTypes ) {
		return componentTypes.isEmpty()
				|| COMPONENTS.keySet().stream()
				.anyMatch( componentTypes::contains );
	}

	//--------------------------------------------------------------------------

	/**
	 * @param componentTypes Classes to match against this entity's components
	 *
	 * @return {@code true} if there is a component of at least one listed type, else {@code false}
	 */
	public boolean hasAny( Class<?>... componentTypes ) {
		return componentTypes.length == 0
		|| Stream.of( componentTypes ).anyMatch( COMPONENTS::containsKey );
	}

	//--------------------------------------------------------------------------

	/**
	 * @param componentTypes Classes to match against this entity's components
	 *
	 * @return {@code true} if there are no components of the listed types, else {@code false}
	 */
	public boolean hasNone( Collection<Class<?>> componentTypes ) {
		return COMPONENTS.keySet().stream()
				.noneMatch( componentTypes::contains );
	}

	//--------------------------------------------------------------------------

	/**
	 * @param componentTypes Classes to match against this entity's components
	 *
	 * @return {@code true} if there are no components of the listed types, else {@code false}
	 */
	public boolean hasNone( Class<?>... componentTypes ) {
		return Stream.of( componentTypes )
				.noneMatch( COMPONENTS::containsKey );
	}

	//--------------------------------------------------------------------------

	/**
	 * @param componentType A single class to match against this entity's components
	 *
	 * @return {@code true} if there is a component of the given type, else {@code false}
	 */
	public boolean has( Class<?> componentType ) {
		return COMPONENTS.containsKey( componentType );
	}

	//--------------------------------------------------------------------------
}
//------------------------------------------------------------------------------

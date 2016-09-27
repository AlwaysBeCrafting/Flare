package stream.alwaysbecrafting.flare;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

	private GameEngine engine;

	//--------------------------------------------------------------------------

	public Entity( Object... components ) {
		Arrays.asList( components ).forEach( component -> {
			COMPONENTS.put( component.getClass(), component );
		} );
	}

	//--------------------------------------------------------------------------

	public void setEngine( GameEngine engine ) {
		this.engine = engine;
	}

	//--------------------------------------------------------------------------

	public GameEngine getEngine() { return engine; }

	//--------------------------------------------------------------------------

	Collection<Class<?>> getComponentTypes() { return COMPONENTS.keySet(); }

	//--------------------------------------------------------------------------

	Collection getComponents() { return COMPONENTS.values(); }

	//--------------------------------------------------------------------------

	/**
	 * Add a component to this {@code Entity} if a component of the same type
	 * does not already exist
	 * @param component The component to add
	 * @return {@code true} if the component was added, else {@code false}
	 */
	public boolean add( Object component ) {
		return COMPONENTS.putIfAbsent( component.getClass(), component ) == null;
	}

	//--------------------------------------------------------------------------

	/**
	 * Retrieves a component from this {@code Entity}
	 * @param componentType The class of the component to retrieve
	 * @return If it exists, the component of the given class, else {@code null}
	 */
	public <T> T get( Class<T> componentType ) {
		return (T)COMPONENTS.get( componentType );
	}

	//--------------------------------------------------------------------------

	/**
	 * Removes a component from this {@code Entity}
	 * @param componentType The class of the component to retrieve
	 * @return {@code true} if a component was removed, else {@code false}
	 */
	public boolean remove( Class<?> componentType ) {
		return COMPONENTS.remove( componentType ) != null;
	}

	//--------------------------------------------------------------------------

	/**
	 * @param componentTypes Classes to match against this entity's components
	 * @return {@code true} if there is a component of every listed type, else {@code false}
	 */
	public boolean hasAll( Collection<Class<?>> componentTypes ) {
		return COMPONENTS.keySet().containsAll( componentTypes );
	}

	//--------------------------------------------------------------------------

	/**
	 * @param componentTypes Classes to match against this entity's components
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
	 * @return {@code true} if there are no components of the listed types, else {@code false}
	 */
	public boolean hasNone( Collection<Class<?>> componentTypes ) {
		return COMPONENTS.keySet().stream()
				.noneMatch( componentTypes::contains );
	}

	//--------------------------------------------------------------------------
}
//------------------------------------------------------------------------------

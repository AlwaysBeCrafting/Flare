package stream.alwaysbecrafting.flare;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

//==============================================================================
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

	public boolean add( Object component ) {
		return COMPONENTS.putIfAbsent( component.getClass(), component ) == null;
	}

	//--------------------------------------------------------------------------

	public <T> T get( Class<T> componentType ) {
		return (T)COMPONENTS.get( componentType );
	}

	//--------------------------------------------------------------------------

	public boolean remove( Class<?> componentType ) {
		return COMPONENTS.remove( componentType ) != null;
	}

	//--------------------------------------------------------------------------
}
//------------------------------------------------------------------------------

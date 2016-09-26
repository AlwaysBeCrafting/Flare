package stream.alwaysbecrafting.flare;

//==============================================================================
public class Entity {
	//--------------------------------------------------------------------------

	private final GameEngine ENGINE;

	//--------------------------------------------------------------------------

	public Entity( GameEngine engine ) {
		ENGINE = engine;
	}

	//--------------------------------------------------------------------------

	public GameEngine getEngine() { return ENGINE; }

	//--------------------------------------------------------------------------

	public <T> T getComponent( Class<T> componentType ) {
		return getEngine().getComponent( this, componentType );
	}

	//--------------------------------------------------------------------------
}
//------------------------------------------------------------------------------
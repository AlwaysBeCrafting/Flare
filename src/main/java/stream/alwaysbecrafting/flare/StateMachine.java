package stream.alwaysbecrafting.flare;

import java.util.HashMap;
import java.util.Map;

//==============================================================================
public class StateMachine {
	//--------------------------------------------------------------------------

	private final Map<String,State> STATES = new HashMap<>();

	private State currentState = new State();

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	public void add( String name, State state ) {
		STATES.put( name, state );
	}

	//--------------------------------------------------------------------------

	public void remove( String name ) {
		STATES.remove( name );
	}

	//--------------------------------------------------------------------------

	public void clear() {
		STATES.clear();
	}

	//--------------------------------------------------------------------------

	public void update( double deltaTime ) {
		currentState.onUpdate( deltaTime );
	}

	//--------------------------------------------------------------------------

	public void change( String name, Object... params ) {
		if ( !STATES.containsKey( name )) throw new IllegalStateException( "No state \"" + name + "\" found" );

		currentState.onExit();
		currentState = STATES.get( name );
		currentState.onEnter();
	}

	//--------------------------------------------------------------------------
}
//------------------------------------------------------------------------------

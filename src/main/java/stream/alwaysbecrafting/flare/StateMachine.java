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

	/**
	 * <p>Put a new state in this machine
	 *
	 * @param name The string to identify the new state by, used in
	 * {@link StateMachine#change(String, Object...)}
	 *
	 * @param state The new state to assign this name
	 */
	public void add( String name, State state ) {
		STATES.put( name, state );
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Remove a state from this machine
	 *
	 * @param name The name of the state to remove, as given in {@link StateMachine#add(String, State)}
	 */
	public void remove( String name ) {
		STATES.remove( name );
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Remove all states from this machine
	 */
	public void clear() {
		STATES.clear();
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Update the machine, calling {@link State#onUpdate(double)} on the
	 * assigned state
	 *
	 * @param deltaTime The time factor to give to the assigned state. Should
	 *                  usually be the time since the game loop's last
	 *                  iteration.
	 */
	public void update( double deltaTime ) {
		currentState.onUpdate( deltaTime );
	}

	//--------------------------------------------------------------------------

	/**
	 * <p>Assigns the state with the given name, calling {@link State#onExit()}
	 * on the previous one and {@link State#onEnter(Object...)} on the new one.
	 *
	 * @param name The name of the new state to assign, as given in {@link StateMachine#add(String, State)}
	 *
	 * @param params The parameters to pass on to the new state's {@link State#onEnter(Object...)}
	 */
	public void change( String name, Object... params ) {
		if ( !STATES.containsKey( name )) throw new IllegalStateException( "No state \"" + name + "\" found" );

		currentState.onExit();
		currentState = STATES.get( name );
		currentState.onEnter();
	}

	//--------------------------------------------------------------------------
}
//------------------------------------------------------------------------------

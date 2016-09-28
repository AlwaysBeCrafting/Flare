package stream.alwaysbecrafting.flare;

//==============================================================================
public class State {
	//--------------------------------------------------------------------------

	/**
	 * A state with no operations defined
	 */
	public static final State EMPTY = new State() {};

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	/**
	 * Override to handle a {@link StateMachine} switching to this state
	 *
	 * @param params The parameters given to {@link StateMachine#change(String, Object...)}
	 */
	public void onEnter( Object... params ) {}

	//--------------------------------------------------------------------------

	/**
	 * Override to handle update calls on a {@link StateMachine}
	 *
	 * @param deltaTime The time given to {@link StateMachine#update(double)}; ostensibly, the time
	 *                  between the previous loop and the current one
	 */
	public void onUpdate( double deltaTime ) {}

	//--------------------------------------------------------------------------

	/**
	 * Override to handle input.
	 */
	public void onHandleInput() {}

	//--------------------------------------------------------------------------

	/**
	 * Override to handle a {@link StateMachine} switching to another state from
	 * this one
	 */
	public void onExit() {}

	//--------------------------------------------------------------------------
}
//------------------------------------------------------------------------------

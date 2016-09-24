package stream.alwaysbecrafting.ecs;

import org.junit.Assert;
import org.junit.Test;

//==============================================================================
public class GameEngineTest {
	//--------------------------------------------------------------------------

	@Test public void AddedSystem_CallsStartBeforeResume() {
		GameEngine engine = new GameEngine();
		StringBuilder output = new StringBuilder();
		GameSystem system = new GameSystem() {
			@Override public void onStart( GameEngine engine ) {
				output.append( "start," );
			}

			@Override public void onResume( GameEngine engine ) {
				output.append( "resume" );
			}
		};


		engine.add( system );


		Assert.assertEquals( "start,resume", output.toString() );
	}

	//--------------------------------------------------------------------------

	@Test public void RemovedSystem_CallsPauseBeforeStop() {
		GameEngine engine = new GameEngine();
		StringBuilder output = new StringBuilder();
		GameSystem system = new GameSystem() {
			@Override public void onPause( GameEngine engine ) {
				output.append( "pause," );
			}

			@Override public void onStop( GameEngine engine ) {
				output.append( "stop" );
			}
		};


		engine.add( system );
		engine.remove( system );


		Assert.assertEquals( "pause,stop", output.toString() );
	}

	//--------------------------------------------------------------------------

	@Test public void RemovedSystem_BySystemReference_StopsExecution() {
		GameEngine engine = new GameEngine();
		StringBuilder output = new StringBuilder( "" );
		GameSystem system = new GameSystem() {
			@Override public void onUpdate( GameEngine engine, float deltaTime ) {
				output.append( "executed" );
			}
		};


		engine.add( system );
		engine.remove( system );
		engine.update( 0 );


		Assert.assertEquals( "", output.toString() );
	}

	//--------------------------------------------------------------------------

	@Test public void RemovedSystem_ByClassReference_StopsExecution() {
		GameEngine engine = new GameEngine();
		StringBuilder output = new StringBuilder( "" );
		GameSystem system = new GameSystem() {
			@Override public void onUpdate( GameEngine engine, float deltaTime ) {
				output.append( "executed" );
			}
		};


		engine.add( system );
		engine.remove( system.getClass() );
		engine.update( 0 );


		Assert.assertEquals( "", output.toString() );
	}

	//--------------------------------------------------------------------------

	@Test public void MultipleAddedSystems_ExecuteInOrderAdded() {
		GameEngine engine = new GameEngine();
		StringBuilder output = new StringBuilder( "" );

		GameSystem system1 = new GameSystem() {
			@Override public void onUpdate( GameEngine engine, float deltaTime ) {
				output.append( "1" );
			}
		};
		GameSystem system2 = new GameSystem() {
			@Override public void onUpdate( GameEngine engine, float deltaTime ) {
				output.append( "2" );
			}
		};
		GameSystem system3 = new GameSystem() {
			@Override public void onUpdate( GameEngine engine, float deltaTime ) {
				output.append( "3" );
			}
		};
		GameSystem system4 = new GameSystem() {
			@Override public void onUpdate( GameEngine engine, float deltaTime ) {
				output.append( "4" );
			}
		};
		GameSystem system5 = new GameSystem() {
			@Override public void onUpdate( GameEngine engine, float deltaTime ) {
				output.append( "5" );
			}
		};


		engine.add( system1 );
		engine.add( system2 );
		engine.add( system3 );
		engine.add( system4 );
		engine.add( system5 );
		engine.update( 0 );


		Assert.assertEquals( "12345", output.toString() );
	}

	//--------------------------------------------------------------------------
}
//------------------------------------------------------------------------------
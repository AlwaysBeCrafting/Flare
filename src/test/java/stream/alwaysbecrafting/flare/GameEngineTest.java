package stream.alwaysbecrafting.flare;

import org.junit.Assert;
import org.junit.Test;

//==============================================================================
public class GameEngineTest {
	//--------------------------------------------------------------------------

	@Test public void Add_WithSingleSystem_CallsStartBeforeResume() {
		GameEngine engine = new GameEngine();
		StringBuilder output = new StringBuilder();
		GameSystem system = new GameSystem() {
			@Override public void onStart( GameEngine engine ) {
				output.append( "start," );
			}

			@Override public void onResume() {
				output.append( "resume" );
			}
		};


		engine.add( system );


		Assert.assertEquals( "start,resume", output.toString() );
	}

	//--------------------------------------------------------------------------

	@Test public void Remove_WithSingleSystem_CallsPauseBeforeStop() {
		GameEngine engine = new GameEngine();
		StringBuilder output = new StringBuilder();
		GameSystem system = new GameSystem() {
			@Override public void onPause() {
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
			@Override public void onUpdate( GameEngine engine, double deltaTime ) {
				output.append( "executed" );
			}
		};


		engine.add( system );
		engine.remove( system );
		engine.update( 0 );


		Assert.assertEquals( "", output.toString() );
	}

	//--------------------------------------------------------------------------

	@Test public void Remove_ByClassReference_StopsExecution() {
		GameEngine engine = new GameEngine();
		StringBuilder output = new StringBuilder( "" );
		GameSystem system = new GameSystem() {
			@Override public void onUpdate( GameEngine engine, double deltaTime ) {
				output.append( "executed" );
			}
		};


		engine.add( system );
		engine.remove( system.getClass() );
		engine.update( 0 );


		Assert.assertEquals( "", output.toString() );
	}

	//--------------------------------------------------------------------------

	@Test public void Add_WithMultipleSystems_ExecuteInOrderAdded() {
		GameEngine engine = new GameEngine();
		StringBuilder output = new StringBuilder( "" );

		GameSystem system1 = new GameSystem() {
			@Override public void onUpdate( GameEngine engine, double deltaTime ) {
				output.append( "1" );
			}
		};
		GameSystem system2 = new GameSystem() {
			@Override public void onUpdate( GameEngine engine, double deltaTime ) {
				output.append( "2" );
			}
		};
		GameSystem system3 = new GameSystem() {
			@Override public void onUpdate( GameEngine engine, double deltaTime ) {
				output.append( "3" );
			}
		};
		GameSystem system4 = new GameSystem() {
			@Override public void onUpdate( GameEngine engine, double deltaTime ) {
				output.append( "4" );
			}
		};
		GameSystem system5 = new GameSystem() {
			@Override public void onUpdate( GameEngine engine, double deltaTime ) {
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

	@Test public void OnPauseAndResume_WithAlreadyPausedSystems_DontDuplicatePauseOrResume() {
		GameEngine engine = new GameEngine();
		StringBuilder builder = new StringBuilder( "" );

		GameSystem system_playing = new GameSystem() {
			@Override public void onPause() {
				builder.append( "[p1]" );
			}
			@Override public void onResume() {
				builder.append( "[r1]" );
			}
		};
		GameSystem system_paused = new GameSystem() {
			@Override public void onPause() {
				builder.append( "[p2]" );
			}
			@Override public void onResume() {
				builder.append( "[r2]" );
			}
		};

		engine.add( system_playing );
		engine.add( system_paused );

		builder.delete( 0, builder.length() );


		system_paused.pause();

		engine.pause();
		engine.resume();

		system_paused.resume();


		Assert.assertEquals( "[p2][p1][r1][r2]", builder.toString() );
	}

	//--------------------------------------------------------------------------

	@Test public void Update_WithPrioritizedSystems_RunInPriorityOrder() {
		GameEngine engine = new GameEngine();
		StringBuilder builder = new StringBuilder( "" );

		GameSystem system1 = new GameSystem() {
			@Override public void onUpdate( GameEngine engine, double deltaTime ) {
				builder.append( "[1]" );
			}
		};
		GameSystem system2 = new GameSystem() {
			@Override public void onUpdate( GameEngine engine, double deltaTime ) {
				builder.append( "[2]" );
			}
		};
		GameSystem system3 = new GameSystem() {
			@Override public void onUpdate( GameEngine engine, double deltaTime ) {
				builder.append( "[3]" );
			}
		};
		engine.add( system3, 3 );
		engine.add( system2, 2 );
		engine.add( system1, 1 );


		engine.update( 0 );


		Assert.assertEquals( "[1][2][3]", builder.toString() );
	}

	//--------------------------------------------------------------------------
}
//------------------------------------------------------------------------------
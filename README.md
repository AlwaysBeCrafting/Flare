# Flare
A tiny engine framework for simple games

[Watch it being used on Twitch](http://alwaysbecrafting.stream)

## Usage

Extend `GameSystem` or `EntitySystem`:

	public class SpriteRenderSystem extends EntitySystem {
		public SpriteRenderSystem() {
			requireAll(
				LocationComponent.class,
				SpriteComponent.class );
		}
		
		@Override
		protected void onHandleEntity( Entity entity, float deltaTime ) {
			LocationComponent location = entity.get( LocationComponent.class );
			SpriteComponent sprite = entity.get( SpriteComponent.class );
			
			// Do something with your components
		}
	}

Add it to a `GameEngine`:

	myEngine = new GameEngine();
	myEngine.add( new SpriteRenderSystem() );

Update your `GameEngine` every frame:

	double currentFrameTime = System.currentTimeMillis() / 1000.0;
	myEngine.update( (float)( currentFrameTime - lastFrameTime ));
	lastFrameTime = currentFrameTime;
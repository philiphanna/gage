package uk.ac.qub.eeecs.game.spaceDemo;

import android.graphics.Color;
import android.widget.Space;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.AssetManager;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.audio.AudioManager;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.particle.ParticleSystemManager;
import uk.ac.qub.eeecs.gage.ui.Bar;
import uk.ac.qub.eeecs.gage.ui.ThumbStick;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;

/**
 * Simple steering game world
 * <p>
 * Note: See the course documentation for extension/refactoring stories
 * for this class.
 *
 * @version 1.0
 */
public class SpaceshipDemoScreen extends GameScreen {

    // /////////////////////////////////////////////////////////////////////////
    // Properties: Space Related
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Width and height of the level
     */
    private final float LEVEL_WIDTH = 1000.0f;
    private final float LEVEL_HEIGHT = 1000.0f;

    /**
     * Define a viewport for the game objects (spaceships, asteroids)
     */
    private LayerViewport mSpaceLayerViewport;

    /**
     * Define the background star scape
     */
    private GameObject mSpaceBackground;

    /**
     * Define the player's spaceship
     */
    private PlayerSpaceship mPlayerSpaceship;

    /**
     * Define the number of objects in the game world
     */
    private final int NUM_ASTEROIDS = 20;
    private final int NUM_SEEKERS = 20;
    private final int NUM_TURRETS = 10;

    /**
     * Define storage for the space entities (non-player)
     */
    private List<SpaceEntity> mSpaceEntities;

    /**
     * Define a particle system manager
     */
    private ParticleSystemManager mParticleSystemManager;

    // /////////////////////////////////////////////////////////////////////////
    // Properties: HUD related
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the movement controls
     */
    private ThumbStick mMovementThumbStick;

    /**
     * Define HUD elements
     */
    private Bar mMovementSpeedBar;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a simple steering game world
     *
     * @param game Game to which this screen belongs
     */
    public SpaceshipDemoScreen(Game game) {
        super("SpaceshipDemoScreen", game);

        // There are two types of object defined within the spaceship demo screen.
        // Firstly, game objects such as spaceships, asteroids, etc. that are defined
        // within a large area. Secondly, GUI elements such as the thumb controls
        // that are located on a defined region of the screen. The default layer
        // viewport for the game layer is used to position the GUI controls, whilst
        // a separate layer viewport is defined for the game objects. In both cases,
        // a full screen screen viewport is defined.

        setupViewports();

        // Create space related game objects
        setupSpaceGameObjects();

        // Create HUD/control objects
        setupControlHUD();
    }

    /**
     * Setup a full screen viewport for drawing to the entire screen and then
     * a resized HUD viewport (for drawing controls, etc.). Finally setup
     * a space game viewport into the 'world' of the created gamae objects.
     */
    private void setupViewports() {
        // Setup the screen viewport to use the full screen.
        mDefaultScreenViewport.set( 0, 0, mGame.getScreenWidth(), mGame.getScreenHeight());

        // Calculate the layer height that will preserved the screen aspect ratio
        // given an assume 480 layer width.
        float layerHeight = mGame.getScreenHeight() * (480.0f / mGame.getScreenWidth());

        mDefaultLayerViewport.set(240.0f, layerHeight/2.0f, 240.0f, layerHeight/2.0f);
        mSpaceLayerViewport = new LayerViewport(240.0f, layerHeight/2.0f, 240.0f, layerHeight/2.0f);
    }

    /**
     * Create and position the various game objects, including the player's ship
     *
     * Note: All the game objects area created within a LEVEL_WIDTH x LEVEL_HEIGHT
     * game area.
     */
    private void setupSpaceGameObjects() {
        // Load in the assets used by the steering demo
        mGame.getAssetManager().loadAssets("txt/assets/SpaceShipDemoSpaceAssets.JSON");

        // Create the particle system manager
        mParticleSystemManager = new ParticleSystemManager(this.getGame());

        // Create the space background
        mSpaceBackground = new GameObject(LEVEL_WIDTH / 2.0f,
                LEVEL_HEIGHT / 2.0f, LEVEL_WIDTH, LEVEL_HEIGHT, getGame()
                .getAssetManager().getBitmap("SpaceBackground"), this);

        // Create the player spaceship
        mPlayerSpaceship = new PlayerSpaceship(100, 100, this);

        // Create storage for the space entities
        mSpaceEntities = new ArrayList<>(NUM_ASTEROIDS+NUM_SEEKERS+NUM_TURRETS);

        // Create a number of randomly positioned asteroids
        Random random = new Random();
        for (int idx = 0; idx < NUM_ASTEROIDS; idx++)
            mSpaceEntities.add(new Asteroid(random.nextFloat() * LEVEL_WIDTH,
                    random.nextFloat() * LEVEL_HEIGHT, this));

        // Create a number of randomly positioned AI controlled seekers
        for (int idx = 0; idx < NUM_SEEKERS; idx++)
            mSpaceEntities.add(new Seeker(random.nextFloat() * LEVEL_WIDTH,
                    random.nextFloat() * LEVEL_HEIGHT, this));

        // Create a number of randomly positioned AI controlled turrets
        for (int idx = 0; idx < NUM_TURRETS; idx++)
            mSpaceEntities.add(new Turret(random.nextFloat() * LEVEL_WIDTH,
                    random.nextFloat() * LEVEL_HEIGHT, this));
    }

    /**
     * Create the HUD/control objects associated with the demo.
     *
     * Note: All the following controls are sized and positioned relative to the
     * default 480x320 layer viewport. This means they can be location/displayed
     * at a fixed screen location, whilst the space viewports change position.
     */
    private void setupControlHUD() {

        // Load in the assets used
        AssetManager assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("BarInner", "img/BarInner.png");
        assetManager.loadAndAddBitmap("BarOuter", "img/BarOuter.png");

        // Create the movement control - located to a region within the bottom,
        // left quadrant of the screen.
        mMovementThumbStick = new ThumbStick(
                mDefaultLayerViewport.x / 2.0f, mDefaultLayerViewport.y / 2.0f,
                mDefaultLayerViewport.halfWidth, mDefaultLayerViewport.halfHeight,
                75.0f, 30.0f, true, this);
        // Provide transparency to both circle (with the inner circle more visible).
        mMovementThumbStick.setOuterCircleColour(0x55FFFFFF);
        mMovementThumbStick.setInnerCircleColour(0x88FFFFFF);

        // Create a bar to show the ship's movement speed
        mMovementSpeedBar = new Bar(
                60.0f, mDefaultLayerViewport.getTop() - 30.0f, 90.f, 30.0f,
                getGame().getAssetManager().getBitmap("BarInner"),
                getGame().getAssetManager().getBitmap("BarOuter"),
                new Vector2(2.0f, 2.0f),
                Bar.Orientation.Horizontal, this);
        mMovementSpeedBar.forceValue(0);
    }


    // /////////////////////////////////////////////////////////////////////////
    // Support methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Return the player spaceship
     *
     * @return Player spaceship
     */
    public PlayerSpaceship getPlayerSpaceship() {
        return mPlayerSpaceship;
    }

    /**
     * Return a list of the non-player space entities in the level
     *
     * @return List of space entities (non-player)
     */
    public List<SpaceEntity> getSpaceEntities() { return mSpaceEntities; }

    /**
     * Return the particle system manager
     *
     * @return Particle system manager used by this screen
     */
    public ParticleSystemManager getParticleSystemManager() {
        return mParticleSystemManager;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Update methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Update the spaceship demo screen
     *
     * @param elapsedTime Elapsed time information
     */
    @Override
    public void update(ElapsedTime elapsedTime) {

        // Ensure background music is playing
        playBackgroundMusic();

        // Consider any user provided input
        mMovementThumbStick.update(elapsedTime, mDefaultLayerViewport, mDefaultScreenViewport);

        // Update the space game objects
        updateSpaceGameObjects(elapsedTime);

        // Update all active particle systems
        mParticleSystemManager.update(elapsedTime);

        // Update the GUI elements
        updateGUIGameObjects(elapsedTime);
    }

    /**
     * Play background music,
     */
    private void playBackgroundMusic() {
        AudioManager audioManager = getGame().getAudioManager();
        if(!audioManager.isMusicPlaying())
            audioManager.playMusic(
                    getGame().getAssetManager().getMusic("SpaceBackgroundMusic"));
    }

    /**
     * Update the space game object
     *
     * @param elapsedTime Elapsed time information
     */
    private void updateSpaceGameObjects(ElapsedTime elapsedTime) {
        // Update the player spaceship
        mPlayerSpaceship.update(elapsedTime, mMovementThumbStick);

        // Ensure the player cannot leave the confines of the world
        BoundingBox playerBound = mPlayerSpaceship.getBound();
        if (playerBound.getLeft() < 0)
            mPlayerSpaceship.position.x -= playerBound.getLeft();
        else if (playerBound.getRight() > LEVEL_WIDTH)
            mPlayerSpaceship.position.x -= (playerBound.getRight() - LEVEL_WIDTH);

        if (playerBound.getBottom() < 0)
            mPlayerSpaceship.position.y -= playerBound.getBottom();
        else if (playerBound.getTop() > LEVEL_HEIGHT)
            mPlayerSpaceship.position.y -= (playerBound.getTop() - LEVEL_HEIGHT);

        // Focus the layer viewport on the player
        mSpaceLayerViewport.x = mPlayerSpaceship.position.x;
        mSpaceLayerViewport.y = mPlayerSpaceship.position.y;

        // Ensure the viewport cannot leave the confines of the world
        if (mSpaceLayerViewport.getLeft() < 0)
            mSpaceLayerViewport.x -= mSpaceLayerViewport.getLeft();
        else if (mSpaceLayerViewport.getRight() > LEVEL_WIDTH)
            mSpaceLayerViewport.x -= (mSpaceLayerViewport.getRight() - LEVEL_WIDTH);

        if (mSpaceLayerViewport.getBottom() < 0)
            mSpaceLayerViewport.y -= mSpaceLayerViewport.getBottom();
        else if (mSpaceLayerViewport.getTop() > LEVEL_HEIGHT)
            mSpaceLayerViewport.y -= (mSpaceLayerViewport.getTop() - LEVEL_HEIGHT);

        // Update each of the space entities
        for (SpaceEntity spaceEntity : mSpaceEntities)
            spaceEntity.update(elapsedTime);

        // Check for and resolve collisions between the space entities
        for(int entityIdx = 0; entityIdx < mSpaceEntities.size(); entityIdx++) {
            SpaceEntity entity = mSpaceEntities.get(entityIdx);
            checkForAndResolveCollisions(entity, mPlayerSpaceship);
            for(int entityIdxOther = entityIdx+1;
                    entityIdxOther < mSpaceEntities.size(); entityIdxOther++) {
                checkForAndResolveCollisions(entity, mSpaceEntities.get(entityIdxOther));
            }
        }
    }

    /**
     * Internal variable create to avoid object creation costs
     */
    private Vector2 separation = new Vector2();

    /**
     * Check for collisions between space entities. If detected then
     * move the objects apart.
     */
    private void checkForAndResolveCollisions(SpaceEntity entityOne, SpaceEntity entityTwo) {
        // Define the separation vector from the first to the second entity
        separation.set(entityTwo.position.x - entityOne.position.x,
                entityTwo.position.y - entityOne.position.y);

        // Separate if the collision bounds overlap
        if(separation.lengthSquared() <
            (entityOne.mRadius+entityTwo.mRadius) * (entityOne.mRadius+entityTwo.mRadius)) {
            // Determine the amount of overlap (to be corrected) and the separating vector
            float overlap = entityOne.mRadius + entityTwo.mRadius - separation.length();
            separation.normalise();

            // Move the ship with the amount of movement based on the mass of the entities
            float entityOneMovePercentage = 1.0f - entityOne.mMass / (entityOne.mMass+entityTwo.mMass);
            entityOne.position.add( -overlap * separation.x * entityOneMovePercentage,
                                -overlap * separation.y * entityOneMovePercentage);
            entityTwo.position.add( overlap * separation.x * (1.0f-entityOneMovePercentage),
                    overlap * separation.y * (1.0f-entityOneMovePercentage));
        }
    }

    /**
     * Update the GUI elements
     *
     * @param elapsedTime Elapsed time information
     */
    private void updateGUIGameObjects(ElapsedTime elapsedTime) {
        // Update the bar's value
        float spaceShipPercentageSpeed =
                mPlayerSpaceship.velocity.length() / mPlayerSpaceship.maxVelocity;
        mMovementSpeedBar.setValue(Math.round(
                mMovementSpeedBar.getMaxValue() * spaceShipPercentageSpeed));

        // Update the bar's displayed value
        mMovementSpeedBar.update(elapsedTime);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Draw methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Draw the space ship demo screen
     *
     * @param elapsedTime Elapsed time information
     * @param graphics2D  Graphics instance
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {

        // Create the screen to black and define a clip based on the viewport
        graphics2D.clear(Color.BLACK);
        graphics2D.clipRect(mDefaultScreenViewport.toRect());

        // Draw the background first of all
        mSpaceBackground.draw(elapsedTime, graphics2D, mSpaceLayerViewport,
                mDefaultScreenViewport);

        // Draw each of the space entities
        for (SpaceEntity spaceEntity : mSpaceEntities)
            spaceEntity.draw(elapsedTime, graphics2D, mSpaceLayerViewport,
                    mDefaultScreenViewport);

        // Draw the player
        mPlayerSpaceship.draw(elapsedTime, graphics2D, mSpaceLayerViewport,
                mDefaultScreenViewport);

        // Draw any live particle effects
        mParticleSystemManager.draw(elapsedTime, graphics2D, mSpaceLayerViewport,
                mDefaultScreenViewport);

        // Draw the GUI elements
        mMovementSpeedBar.draw(elapsedTime, graphics2D,
                mDefaultLayerViewport, mDefaultScreenViewport);

        // Draw the touch controls
        mMovementThumbStick.draw(elapsedTime, graphics2D,
                mDefaultLayerViewport, mDefaultScreenViewport);
    }
}
package uk.ac.qub.eeecs.game.miscDemos;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.AssetManager;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameScreen;

/**
 * Asset demo showing how assets can be loaded
 *
 * @version 1.0
 */
public class AssetDemoScreen extends GameScreen {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the back button to return to the demo menu
     */
    private PushButton mBackButton;

    /**
     * The asset demo loads and displays three bitmaps, a space ship,
     * arrow and a sprite sheet, defined as follows:
     */
    private Bitmap mShip;
    private Bitmap mArrow;
    private Bitmap mSpritesheet;

    /**
     * The spaceship bitmap will be rotated when it is drawn. A variable
     * is defined to hold the current rotation.
     */
    private float mRotation = 0.0f;

    /**
     * The sprite sheet bitmap will have a colour tint applied when it is
     * drawn. The colour tint will be selected by cycling through an array
     * of different colours. Define the colours choices and the current colour.
     */
    private int[] mColourChoices =
            new int[]{Color.RED, Color.CYAN, Color.GREEN, Color.YELLOW};
    private int mColour = Color.WHITE;


    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create the asset demo
     *
     * @param game Game to which this screen belongs
     */
    public AssetDemoScreen(Game game) {
        super("AssetDemoScreen", game);

        // Create and position a small back button in the lower-right hand corner
        // of the screen. Also, enable click sounds on press/release interactions.
        mBackButton = new PushButton(
                mDefaultLayerViewport.getWidth() * 0.95f, mDefaultLayerViewport.getHeight() * 0.10f,
                mDefaultLayerViewport.getWidth() * 0.075f, mDefaultLayerViewport.getHeight() * 0.10f,
                "BackArrow", "BackArrowSelected", this);
        mBackButton.setPlaySounds(true, true);

        // Load in the assets used by this demo using the asset manager this is
        // available within the game. Different asset types and ways of loading
        // asset are demonstrated.

        // Aside: We did not need to load in the BackArrow and BackArrowSelected
        // images used above to create the back button as it is assumed the images
        // will have previously been loaded in (as part of creating the demo menu
        // screen). If you wanted to improve the portability of this class you could
        // remove this assumption by asking the asset manager to load in the bitmaps.

        AssetManager assetManager = mGame.getAssetManager();

        // Load and add a specified bitmaps and font into the asset manager. The
        // assets can be retrieved from the asset manager using the specified name.

        assetManager.loadAndAddBitmap(
                "AdventurerJumping", "img/AdventurerJumping.png");
        assetManager.loadAndAddBitmap(
                "Spaceship", "img/Spaceship3.png");
        assetManager.loadAndAddFont(
                "AudiowideFont", "font/Audiowide.ttf");

        // Load in two more bitmaps that are speciied within a JSON file

        assetManager.loadAssets(
                "txt/assets/AssetDemoScreenAssets.JSON");

        // Retrieve the assets to be used in this demo.

        mShip = assetManager.getBitmap("Spaceship");
        mArrow = assetManager.getBitmap("UpArrow");
        mSpritesheet = assetManager.getBitmap("AdventurerJumping");
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Update the asset demo
     *
     * @param elapsedTime Elapsed time information
     */
    @Override
    public void update(ElapsedTime elapsedTime) {

        // Update the back button. If triggered then return to the demo menu.
        mBackButton.update(elapsedTime);
        if (mBackButton.isPushTriggered())
            mGame.getScreenManager().removeScreen(this);

        // Change the bitmap associated with the arrow reference every second.
        // Note: the code is inefficient as it gets the bitmap each frame.
        mArrow = mGame.getAssetManager().getBitmap(
                ((int) (elapsedTime.totalTime / 2.0f)) % 2 == 0 ?
                        "UpArrow" : "UpArrowSelected");

        // Select the colour tint to be applied to the sprite sheet
        mColour = mColourChoices[(int) (elapsedTime.totalTime % mColourChoices.length)];

        // Update the rotation to be applied to the spaceship
        mRotation += elapsedTime.stepTime * 10.0f;
        if (mRotation > 360.0f) mRotation -= 360.0f;
    }

    /**
     * Draw the assets
     *
     * @param elapsedTime Elapsed time information
     * @param graphics2D  Graphics instance
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {

        // Clear the screen
        graphics2D.clear(Color.WHITE);

        // As we're not using viewports in this demo (and drawing directly
        // to the screen), get the screen size for positioning and sizing
        // the drawn bitmaps.

        int width = graphics2D.getSurfaceWidth();
        int height = graphics2D.getSurfaceHeight();

        // Note: for reasons for simplicity, the following code is somewhat
        // inefficient as it creates Paint, Rect, ColorFilter, etc. objects
        // each frame. This means many objects will be created and then
        // shortly discarded every second. It would be better to create an
        // external variable that can be reused.

        // Draw the spritesheet using a colour tint. To do this a Paint object
        // is used that will apply a colour tint. A Porter Duff multiply filter
        // used to apply the tint (multiplying each pixel by the tint).

        Paint bitmapPaint = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(mColour, PorterDuff.Mode.MULTIPLY);
        bitmapPaint.setColorFilter(filter);

        // Draw all of the spritesheet bitmap (specified using the source rect) to
        // a wide region near the top of the screen (specified using the dest rect).

        Rect sourceRect = new Rect(
                0, 0, mSpritesheet.getWidth(), mSpritesheet.getHeight());
        Rect destRect = new Rect(
                (int) (width * 0.1f), (int) (height * 0.1f), (int) (width * 0.9f), (int) (height * 0.35f));
        graphics2D.drawBitmap(mSpritesheet, sourceRect, destRect, bitmapPaint);

        // Render some text using the font that was loaded. To do this another
        // Paint instance is created to use the loaded font with a determined
        // size and horizontal alignment.

        Paint textPaint = new Paint();
        textPaint.setTypeface(mGame.getAssetManager().getFont("AudiowideFont"));
        textPaint.setTextSize(height / 6);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // The text is drawn centered near the middle of the screen
        graphics2D.drawText(
                "Bitmaps and Fonts", width * 0.5f, height * 0.55f, textPaint);

        // Draw the spaceship. Because the spaceship is to be rotated a matrix
        // is created to apply a rotational transform and translation. By default
        // the matrix will apply to the entire bitmap (a source rectangle is not
        // needed) and drawn directly to the screen. Scaling can be applied through
        // the matrix to control the size of the drawn image if desired.

        Matrix drawMatrix = new Matrix();
        drawMatrix.setRotate(mRotation, mShip.getWidth() / 2.0f, mShip.getHeight() / 2.0f);
        drawMatrix.postTranslate((int) (width * 0.25f), (int) (height * 0.7f));
        graphics2D.drawBitmap(mShip, drawMatrix, null);

        // Draw the arrow bitmap. This is a simple draw with no paint or transformation applied.

        sourceRect.set(0, 0, mArrow.getWidth(), mArrow.getHeight());
        destRect.set((int) (width * 0.6), (int) (height * 0.65f),
                (int) (width * 0.8f), (int) (height * 0.95f));
        graphics2D.drawBitmap(mArrow, sourceRect, destRect, null);

        // Finally, draw the back button
        mBackButton.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
    }
}
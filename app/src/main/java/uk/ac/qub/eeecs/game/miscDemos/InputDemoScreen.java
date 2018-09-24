package uk.ac.qub.eeecs.game.miscDemos;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.LinkedList;
import java.util.List;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameScreen;

/**
 * Input demo showing how input can be received and processed
 *
 * @version 1.0
 */
public class InputDemoScreen extends GameScreen {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the back button to return to the demo menu
     */
    private PushButton mBackButton;

    /**
     * Define storage for holding the device's acceleration
     */
    private float[] mAcceleration = new float[3];

    /**
     * Define storage of touch points. Up to 5 simultaneous touch
     * events are tested (an arbitrary value that displays well on ]
     * screen). An array of booleans is used to determine if a given
     * touch point exists, alongside this a corresponding 2D array of
     * x/y points is maintained to hold the location of touch points
     */
    private static final int mTouchIdToDisplay = 5;
    private boolean[] mTouchIdExists = new boolean[mTouchIdToDisplay];
    private float[][] mTouchLocation = new float[mTouchIdExists.length][2];

    /**
     * A history of touch events will be maintained - this is held
     * within a list that will be trimmed to ensure it doesn't exceed
     * the history maximum length.
     */
    private static final int TOUCH_EVENT_HISTORY_SIZE = 30;
    private List<String> mTouchEventsInfo = new LinkedList<>();


    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create the input demo
     *
     * @param game Game to which this screen belongs
     */
    public InputDemoScreen(Game game) {
        super("InputDemoScreen", game);

        // Create and position a small back button in the lower-right hand corner
        // of the screen. Also, enable click sounds on press/release interactions.
        mBackButton = new PushButton(
                mDefaultLayerViewport.getWidth() * 0.95f, mDefaultLayerViewport.getHeight() * 0.10f,
                mDefaultLayerViewport.getWidth() * 0.075f, mDefaultLayerViewport.getHeight() * 0.10f,
                "BackArrow", "BackArrowSelected", this);
        mBackButton.setPlaySounds(true, true);
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Update the input demo
     *
     * @param elapsedTime Elapsed time information
     */
    @Override
    public void update(ElapsedTime elapsedTime) {

        // Update the back button. If triggered then return to the demo menu.
        mBackButton.update(elapsedTime);
        if (mBackButton.isPushTriggered())
            mGame.getScreenManager().removeScreen(this);

        // Get access to the input manager held by the game engine
        Input input = mGame.getInput();

        // Store acceleration information for the device
        mAcceleration[0] = input.getAccelX();
        mAcceleration[1] = input.getAccelY();
        mAcceleration[2] = input.getAccelZ();

        // Store touch point information.
        for (int pointerId = 0; pointerId < mTouchIdExists.length; pointerId++) {
            mTouchIdExists[pointerId] = input.existsTouch(pointerId);
            if (mTouchIdExists[pointerId]) {
                mTouchLocation[pointerId][0] = input.getTouchX(0);
                mTouchLocation[pointerId][1] = input.getTouchY(0);
            }
        }

        // Get any touch events that have occurred since the last update
        List<TouchEvent> touchEvents = input.getTouchEvents();
        if (touchEvents.size() > 0) {

            // Store the touch event information
            for (TouchEvent touchEvent : touchEvents) {
                // Collection information on the touch event
                String touchEventInfo = touchEventTypeToString(touchEvent.type) +
                        String.format(" [%.0f,%.0f,ID=%d]",
                                touchEvent.x, touchEvent.y, touchEvent.pointer);

                // Additional information is available if the touch event is of
                // type TOUCH_SCROLL or TOUCH_FLING - in both cases the touchEvent.dx
                // and touchEvent.dy values specify how much movement (scroll or
                // fling) is associated with the touch event. For reasons for brevity
                // these values are no displayed din this demo.

                // Add the information to the history
                mTouchEventsInfo.add(touchEventInfo);
                if (mTouchEventsInfo.size() > TOUCH_EVENT_HISTORY_SIZE)
                    mTouchEventsInfo.remove(0);
            }
        }
    }

    /**
     * Return a string that holds the corresponding label for the specified
     * type of touch event.
     *
     * @param type Touch event type
     * @return Touch event label
     */
    private String touchEventTypeToString(int type) {
        switch (type) {
            case 0:
                return "TOUCH_DOWN";
            case 1:
                return "TOUCH_UP";
            case 2:
                return "TOUCH_DRAGGED";
            case 3:
                return "TOUCH_SHOW_PRESS";
            case 4:
                return "TOUCH_LONG_PRESS";
            case 5:
                return "TOUCH_SINGLE_TAP";
            case 6:
                return "TOUCH_SCROLL";
            case 7:
                return "TOUCH_FLING";
            default:
                return "ERROR: Unknown Touch Event Type";
        }
    }

    /**
     * Internal paint variable, defined externally to reduce object creation costs
     */
    private Paint textPaint = new Paint();

    /**
     * Draw the menu screen
     *
     * @param elapsedTime Elapsed time information
     * @param graphics2D  Graphics instance
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {

        // Clear the screen
        graphics2D.clear(Color.WHITE);

        // As we're drawing text directly to the screen in this demo get the screen size
        int screenWidth = graphics2D.getSurfaceWidth();
        int screenHeight = graphics2D.getSurfaceHeight();

        // Display a message to the user
        textPaint.setTextSize(screenHeight / 16.0f);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(Typeface.MONOSPACE);
        graphics2D.drawText("Interact with the screen",
                screenWidth / 20.0f, screenHeight / 2.0f, textPaint);

        // Set font values for drawing the touch information
        float lineHeight = screenHeight / 30.0f;
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(lineHeight);

        // Draw the current acceleration values
        graphics2D.drawText("Acceleration [x,y,z] =", 0.0f, lineHeight, textPaint);
        graphics2D.drawText(String.format("[%.2f, %.2f, %.2f]",
                mAcceleration[0], mAcceleration[1], mAcceleration[2]),
                0.0f, 2.0f * lineHeight, textPaint);

        // Draw the touch point information
        int lineNumber = 1;
        for (int pointerIdx = 0; pointerIdx < mTouchIdExists.length; pointerIdx++) {
            if (mTouchIdExists[pointerIdx]) {
                graphics2D.drawText("Pointer Id " + pointerIdx + ": Detected [" +
                                String.format("%.2f, %.2f]", mTouchLocation[pointerIdx][0], mTouchLocation[pointerIdx][1]),
                        0.0f, screenHeight - lineHeight * mTouchIdExists.length + lineHeight * lineNumber++, textPaint);
            } else {
                graphics2D.drawText("Pointer Id " + pointerIdx + ": Not detected.",
                        0.0f, screenHeight - lineHeight * mTouchIdExists.length + lineHeight * lineNumber++, textPaint);
            }
        }

        // Draw the touch event history
        lineNumber = 1;
        textPaint.setTextAlign(Paint.Align.RIGHT);
        for (int eventIdx = 0; eventIdx < mTouchEventsInfo.size(); eventIdx++) {
            graphics2D.drawText(mTouchEventsInfo.get(eventIdx),
                    screenWidth, lineHeight * lineNumber++, textPaint);
        }

        // Draw the back button
        mBackButton.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
    }
}

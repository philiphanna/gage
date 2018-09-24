package uk.ac.qub.eeecs.gage.engine.input;

import android.util.Log;
import android.view.View;
import android.view.View.OnKeyListener;

import java.util.ArrayList;
import java.util.List;

import uk.ac.qub.eeecs.gage.R;
import uk.ac.qub.eeecs.gage.util.Pool;

public class KeyHandler implements OnKeyListener {

    // /////////////////////////////////////////////////////////////////////////
    // Methods: Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Array of key down boolean flags - indexed by
     * android.view.KeyEvent.KEYCODE_XXX (ranged from 0-127)
     */
    private boolean[] mPressedKeys = new boolean[128];

    /**
     * Key event pool and lists of current (for this frame) and unconsumed
     * (occurring since the frame started) key events.
     */
    private Pool<KeyEvent> mKeyEventPool;
    private List<KeyEvent> mUnconsumedKeyEvents = new ArrayList<>();
    private List<KeyEvent> mKeyEvents = new ArrayList<>();

    /**
     * Define the maximum number of key events that can be retained in the touch
     * store.
     */
    private final int KEY_POOL_SIZE = 100;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a new key handler instance for the specified view
     *
     * @param view View whose key events shoudl be captured by this handler
     */
    public KeyHandler(View view) {

        mKeyEventPool = new Pool<>(new Pool.ObjectFactory<KeyEvent>() {
            public KeyEvent createObject() {
                return new KeyEvent();
            }
        }, KEY_POOL_SIZE);

        view.setOnKeyListener(this);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods: Key Events
    // /////////////////////////////////////////////////////////////////////////

    /**
     * (non-Javadoc)
     *
     * @see android.view.View.OnKeyListener#onKey(android.view.View, int,
     * android.view.KeyEvent)
     */
    @Override
    public boolean onKey(View v, int keyCode, android.view.KeyEvent event) {
        // Multi key events are not supported - returned if encountered
        if (event.getAction() == android.view.KeyEvent.ACTION_MULTIPLE) {
            String warningTag = v.getContext().getApplicationContext()
                    .getResources().getString(R.string.WARNING_TAG);
            String warningMessage = "ACTION_MULTIPLE event type encountered within"
                    + this.getClass().toString();
            Log.w(warningTag, warningMessage);
            return false;
        }

        // Store details of the key events (synchronised as a non-UI thread may
        // request access to the event list.
        synchronized (this) {
            // Retrieve and populate a key event
            KeyEvent keyEvent = mKeyEventPool.get();
            keyEvent.keyCode = keyCode;
            keyEvent.keyChar = (char) event.getUnicodeChar();

            if (event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                keyEvent.type = KeyEvent.KEY_DOWN;
                if (keyCode > 0 && keyCode < 127)
                    mPressedKeys[keyCode] = true;
            }
            if (event.getAction() == android.view.KeyEvent.ACTION_UP) {
                keyEvent.type = KeyEvent.KEY_UP;
                if (keyCode > 0 && keyCode < 127)
                    mPressedKeys[keyCode] = false;
            }

            // Add the event to the list of unconsumed key events
            mUnconsumedKeyEvents.add(keyEvent);
        }
        return false;
    }

    /**
     * Indicate if the specified key is current down.
     * <p>
     * Note: A value of false is automatically returned for any keycode < 0 or >
     * 127.
     *
     * @param keyCode Key to test
     * @return Boolean true if the key is currently down, otherwise false
     */
    public boolean isKeyPressed(int keyCode) {
        if (keyCode < 0 || keyCode > 127)
            return false;
        return mPressedKeys[keyCode];
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods: Event Accumulation
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Return the list of key events accumulated for the current frame.
     * <p>
     * IMPORTANT: A shared list of key events is returned. The list should be
     * considered read only.
     *
     * @return List of key events accumulated for the current frame
     */
    public List<KeyEvent> getKeyEvents() {
        synchronized (this) {
            return mKeyEvents;
        }
    }

    /**
     * Reset the accumulator - update the current set of frame key events to
     * those accumulated since the last time the accumulator was reset.
     * <p>
     * Note: It is assumed that this method will be called once per frame.
     */
    public void resetAccumulator() {
        synchronized (this) {
            // Release all existing key events
            int len = mKeyEvents.size();
            for (int i = 0; i < len; i++) {
                mKeyEventPool.add(mKeyEvents.get(i));
            }
            mKeyEvents.clear();
            // Copy across accumulated events
            mKeyEvents.addAll(mUnconsumedKeyEvents);
            mUnconsumedKeyEvents.clear();
        }
    }
}

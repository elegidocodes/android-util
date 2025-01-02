package com.elegidocodes.android.util.animation;

import android.animation.ValueAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * A custom {@link AppCompatTextView} that can display text with a typewriter-like animation
 * or a marquee-style scrolling animation (single line).
 *
 * <p>This view can animate text by "typing" each character one by one at a specified speed,
 * and can also be configured to use Android's built-in marquee for scrolling if desired.</p>
 *
 * @author Fernando Ismael Canul Caballero
 * @version 1.0.0
 */
public class TypingTextView extends AppCompatTextView {

    /**
     * Animator that handles typing effect updates.
     */
    private ValueAnimator typingAnimator;

    /**
     * Default constructor required when inflating from XML.
     *
     * @param context The {@link Context} the view is running in,
     *                through which it can access the current theme, resources, etc.
     */
    public TypingTextView(Context context) {
        super(context);
    }

    /**
     * Constructor that is called when inflating a view from XML, allowing
     * it to parse XML attributes.
     *
     * @param context The {@link Context} the view is running in.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public TypingTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor that allows default style attributes to be applied.
     *
     * @param context      The {@link Context} the view is running in.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a reference to a
     *                     style resource to apply to this view.
     */
    public TypingTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Enables Android's built-in marquee (scroll) effect for a single line of text.
     * The text will scroll continuously based on the specified repeat count.
     *
     * @param repeatCount The number of times the marquee animation will repeat.
     *                    Use -1 to loop indefinitely.
     */
    public void enableMarqueeScroll(int repeatCount) {
        setSelected(true);
        setSingleLine(true);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(repeatCount);
    }

    /**
     * Starts a typewriter-like animation that gradually displays the provided text
     * over the specified duration.
     *
     * @param animText            The text to animate character by character.
     * @param duration            The total animation duration in milliseconds.
     * @param onAnimationListener An optional listener to receive callbacks
     *                            for animation start and end events.
     */
    public void startTypingAnimation(CharSequence animText,
                                     int duration,
                                     @Nullable OnAnimationListener onAnimationListener) {
        if (animText == null) {
            Log.w(getClass().getSimpleName(), "startTypingAnimation called with null text.");
            return;
        }

        // If there's an existing animator running, cancel it first.
        if (typingAnimator != null && typingAnimator.isRunning()) {
            typingAnimator.cancel();
        }

        int numberOfLetters = animText.length();
        Log.d(getClass().getSimpleName(), "Number of letters to animate: " + numberOfLetters);

        // Notify start
        if (onAnimationListener != null) {
            onAnimationListener.onStartAnimation();
        }

        // Clear the text to start from an empty view
        setText("");

        // Create and configure the animator
        typingAnimator = ValueAnimator.ofInt(0, numberOfLetters);
        typingAnimator.setDuration(duration);
        typingAnimator.addUpdateListener(animation -> {
            int index = (int) animation.getAnimatedValue();
            // Subsequence up to index characters
            setText(animText.subSequence(0, index));

            // If reached the last character, notify the end
            if (index == numberOfLetters && onAnimationListener != null) {
                onAnimationListener.onEndAnimation();
            }
        });
        typingAnimator.start();
    }

    /**
     * Stops any currently running typing animation if present.
     */
    public void stopTypingAnimation() {
        if (typingAnimator != null && typingAnimator.isRunning()) {
            typingAnimator.cancel();
        }
    }

    /**
     * Overridden lifecycle method that is called when this view is being removed
     * from the window. Ensures any active animation is cancelled to prevent leaks.
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopTypingAnimation();
    }

    /**
     * Interface definition for a callback to be invoked when a typing animation starts or ends.
     */
    public interface OnAnimationListener {
        /**
         * Called when the typing animation starts.
         */
        void onStartAnimation();

        /**
         * Called when the typing animation ends.
         */
        void onEndAnimation();
    }
}


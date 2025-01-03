package com.elegidocodes.android.util.animation;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

/**
 * A custom page transformer for ViewPager2 that applies a "depth" effect when swiping between pages.
 *
 * <p>Pages in the ViewPager2 are transformed with a fade effect and a scale-down effect as they move
 * further away from the center. This creates a smooth depth transition between pages.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * ViewPager2 viewPager = findViewById(R.id.view_pager);
 * float minScale = 0.85f; // Adjust the minimum scale as desired
 * viewPager.setPageTransformer(new DepthPageTransformer(minScale));
 * }</pre>
 */
public class DepthPageTransformer implements ViewPager2.PageTransformer {

    private final float minScale;

    /**
     * Constructs a DepthPageTransformer with the specified minimum scale.
     *
     * @param minScale The minimum scale factor to apply to pages as they move away from the center.
     *                 Must be between 0 (completely collapsed) and 1 (no scaling).
     */
    public DepthPageTransformer(float minScale) {
        if (minScale <= 0 || minScale > 1) {
            throw new IllegalArgumentException("minScale must be between 0 and 1 (exclusive)");
        }
        this.minScale = minScale;
    }

    /**
     * Transforms the page with a depth effect as it moves in or out of focus.
     *
     * @param view     The page view being transformed.
     * @param position The position of the page relative to the current front-and-center position of the ViewPager2.
     *                 <ul>
     *                     <li>Position -1: Page is fully off-screen to the left.</li>
     *                     <li>Position 0: Page is fully in view.</li>
     *                     <li>Position 1: Page is fully off-screen to the right.</li>
     *                     <li>Position between -1 and 0: Page is moving into view from the left.</li>
     *                     <li>Position between 0 and 1: Page is moving out of view to the right.</li>
     *                 </ul>
     */
    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();

        if (position < -1) {
            // Page is way off-screen to the left
            view.setAlpha(0f);

        } else if (position <= 0) { // [-1,0]
            // Fully visible page
            view.setAlpha(1f);
            view.setTranslationX(0f);
            view.setTranslationZ(0f);
            view.setScaleX(1f);
            view.setScaleY(1f);

        } else if (position <= 1) { // (0,1]
            // Fade the page out
            view.setAlpha(1 - position);

            // Counteract the default slide transition
            view.setTranslationX(pageWidth * -position);

            // Move it behind the left page
            view.setTranslationZ(-1f);

            // Scale the page down (between minScale and 1)
            float scaleFactor = minScale + (1 - minScale) * (1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

        } else { // (1,+Infinity]
            // Page is way off-screen to the right
            view.setAlpha(0f);
        }
    }

}


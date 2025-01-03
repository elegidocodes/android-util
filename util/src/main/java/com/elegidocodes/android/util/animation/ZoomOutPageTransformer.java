package com.elegidocodes.android.util.animation;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

/**
 * A custom page transformer for ViewPager2 that applies a "zoom out" effect when swiping between pages.
 *
 * <p>Pages in the ViewPager2 are transformed with a zoom-out effect, shrinking and fading as they
 * move away from the center. This creates a smooth transition with depth perception.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * ViewPager2 viewPager = findViewById(R.id.view_pager);
 * float minScale = 0.85f; // Minimum scale factor
 * float minAlpha = 0.5f; // Minimum alpha value
 * viewPager.setPageTransformer(new ZoomOutPageTransformer(minScale, minAlpha));
 * }</pre>
 */
public class ZoomOutPageTransformer implements ViewPager2.PageTransformer {

    private final float minScale;
    private final float minAlpha;

    /**
     * Constructs a ZoomOutPageTransformer with specified scale and alpha values.
     *
     * @param minScale The minimum scale factor to apply to pages as they move away from the center.
     *                 Must be between 0 (completely collapsed) and 1 (no scaling).
     * @param minAlpha The minimum alpha (transparency) value to apply to pages as they move away.
     *                 Must be between 0 (fully transparent) and 1 (fully opaque).
     * @throws IllegalArgumentException If minScale or minAlpha are outside their valid ranges.
     */
    public ZoomOutPageTransformer(float minScale, float minAlpha) {
        if (minScale <= 0 || minScale > 1) {
            throw new IllegalArgumentException("minScale must be between 0 and 1 (exclusive)");
        }
        if (minAlpha < 0 || minAlpha > 1) {
            throw new IllegalArgumentException("minAlpha must be between 0 and 1 (inclusive)");
        }
        this.minScale = minScale;
        this.minAlpha = minAlpha;
    }

    /**
     * Transforms the page with a zoom-out effect as it moves in or out of focus.
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
        int pageHeight = view.getHeight();

        if (position < -1) { // [-Infinity, -1)
            // This page is way off-screen to the left.
            view.setAlpha(0f);

        } else if (position <= 1) { // [-1, 1]
            // Modify the default slide transition to shrink the page as well.
            float scaleFactor = Math.max(minScale, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }

            // Scale the page down (between minScale and 1).
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            view.setAlpha(minAlpha + (scaleFactor - minScale) / (1 - minScale) * (1 - minAlpha));

        } else { // (1, +Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0f);
        }
    }

}


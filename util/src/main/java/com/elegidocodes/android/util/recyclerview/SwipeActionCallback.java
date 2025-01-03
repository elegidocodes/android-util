package com.elegidocodes.android.util.recyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A general callback for handling swipe actions in a {@link RecyclerView}.
 *
 * <p>This class provides a customizable background and drawable icon for swipe actions (e.g., delete or archive).
 * It supports light and dark mode colors for better theme adaptability.</p>
 */
public abstract class SwipeActionCallback extends ItemTouchHelper.Callback {

    private final Context context;
    private final Paint clearPaint;
    private final ColorDrawable backgroundDrawable;
    private final int backgroundColor;
    private final Drawable actionDrawable;
    private final int intrinsicWidth;
    private final int intrinsicHeight;
    private final int swipeDirection;

    /**
     * Constructor for creating a swipe action callback.
     *
     * @param context         The application context.
     * @param backgroundColor The background color for the swipe action.
     * @param drawableResId   The resource ID for the action drawable.
     * @param drawableColor   The color for the action drawable.
     * @param swipeDirection  The direction of the swipe action (e.g., {@link ItemTouchHelper#LEFT} or {@link ItemTouchHelper#RIGHT}).
     */
    public SwipeActionCallback(Context context, int backgroundColor, int drawableResId, int drawableColor, int swipeDirection) {
        this.context = context;
        this.backgroundDrawable = new ColorDrawable();
        this.backgroundColor = ContextCompat.getColor(context, backgroundColor);
        this.clearPaint = new Paint();
        this.clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.swipeDirection = swipeDirection;
        this.actionDrawable = ContextCompat.getDrawable(context, drawableResId);

        if (actionDrawable != null) {
            this.actionDrawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, drawableColor), PorterDuff.Mode.SRC_ATOP));
            this.intrinsicWidth = actionDrawable.getIntrinsicWidth();
            this.intrinsicHeight = actionDrawable.getIntrinsicHeight();
        } else {
            this.intrinsicWidth = 0;
            this.intrinsicHeight = 0;
        }
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, swipeDirection);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        boolean isCancelled = dX == 0 && !isCurrentlyActive;
        if (isCancelled) {
            clearCanvas(canvas, itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            return;
        }

        // Draw the background
        backgroundDrawable.setColor(backgroundColor);
        backgroundDrawable.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        backgroundDrawable.draw(canvas);

        // Draw the action icon
        if (actionDrawable != null) {
            int actionIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int actionIconMargin = (itemHeight - intrinsicHeight) / 2;
            int actionIconLeft = itemView.getRight() - actionIconMargin - intrinsicWidth;
            int actionIconRight = itemView.getRight() - actionIconMargin;
            int actionIconBottom = actionIconTop + intrinsicHeight;

            actionDrawable.setBounds(actionIconLeft, actionIconTop, actionIconRight, actionIconBottom);
            actionDrawable.draw(canvas);
        }

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void clearCanvas(Canvas canvas, Float left, Float top, Float right, Float bottom) {
        canvas.drawRect(left, top, right, bottom, clearPaint);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.7f;
    }
}


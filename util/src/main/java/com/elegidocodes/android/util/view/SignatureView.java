package com.elegidocodes.android.util.view;

import static com.elegidocodes.android.util.date.DateUtil.DateFormats.DATE_COMPACT_WITH_UNDERSCORE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.elegidocodes.android.util.file.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A custom view that captures a user's signature via touch events.
 *
 * <p>This class extends the Android {@link View} to allow drawing a signature on the screen
 * using finger or stylus input. The user's strokes are recorded and displayed as lines on the screen.
 * You can call {@link #clear()} to reset the signature area.</p>
 *
 * <p>Example usage in XML layout:
 * <pre>{@code
 * <LinearLayout
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     android:orientation="vertical">
 *
 *     <com.elegidocodes.view.SignatureView
 *         android:id="@+id/signature_view"
 *         android:layout_width="match_parent"
 *         android:layout_height="match_parent" />
 * </LinearLayout>
 * }</pre>
 * </p>
 *
 * <p>Example usage in an Activity or Fragment:
 * <pre>{@code
 * SignatureView signatureView = findViewById(R.id.signature_view);
 *
 * // Clear the existing signature
 * signatureView.clear();
 * }</pre>
 * </p>
 */
public class SignatureView extends View {

    private static final String TAG = "SIGNATURE_VIEW";

    /**
     * The paint object that defines the brush style, color, stroke width, etc.
     */
    private Paint paint;

    /**
     * The path object that stores the current drawing path.
     */
    private Path path;

    /**
     * Flag to track if user has signed
     */
    private boolean hasSigned = false;

    /**
     * Constructs a new {@code SignatureView} with the specified context and attribute set.
     *
     * @param context the context from which the view is running
     * @param attrs   the set of attributes associated with the view
     */
    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Initializes the paint and path objects with default settings
     * for a smooth, black stroke.
     */
    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(8);

        path = new Path();
    }

    /**
     * Called when the view is drawn on the screen.
     * <p>This method draws the current path on the provided canvas.</p>
     *
     * @param canvas the {@link Canvas} on which the background will be drawn
     */
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    /**
     * Handles touch events to record the user's signature strokes.
     *
     * <p>When the user touches down, the path moves to that point.
     * As the user moves their finger (ACTION_MOVE), the path is extended
     * with a line to the new touch coordinates. Lifting the finger
     * (ACTION_UP) completes the current stroke.</p>
     *
     * @param event The {@link MotionEvent} describing the touch event
     * @return true to indicate the event was handled
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Start a new path segment at the touch-down coordinates.
                path.moveTo(x, y);
                hasSigned = true;
                break;
            case MotionEvent.ACTION_MOVE:
                // Extend the path to the new coordinates.
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                // Nothing special to do here, but this is where you'd finalize stroke data if needed.
                break;
        }

        // Redraw the view with the updated path.
        invalidate();
        return true;
    }

    /**
     * Resets the signature path, clearing any existing strokes.
     *
     * <p>Calling this method erases the current signature from the view.</p>
     *
     * <p>Example:
     * <pre>{@code
     * SignatureView signatureView = findViewById(R.id.signature_view);
     * signatureView.clear();
     * }</pre>
     * </p>
     */
    public void clear() {
        path.reset();
        hasSigned = false;
        invalidate();
    }

    /**
     * Returns true if the user has signed (touched and drawn on the view).
     */
    public boolean isSigned() {
        return hasSigned;
    }

    /**
     * Saves the current signature drawing as a PNG image in the specified folder under the app's external
     * Pictures directory, using a generated timestamp and provided prefix/suffix in the filename.
     * <p>
     * After creating and saving the file, it returns the absolute path to the saved image.</p>
     *
     * <p>Example usage:
     * <pre>{@code
     * SignatureView signatureView = findViewById(R.id.signature_view);
     * String prefix = "User";
     * String suffix = "Signature";
     * String childFolder = "MySignatures";
     * int compressQuality = 100;  // Full quality
     *
     * try {
     *     String imagePath = signatureView.saveSignatureImage(
     *         this, prefix, suffix, childFolder, compressQuality
     *     );
     *     Log.d(TAG, "Saved signature image at: " + imagePath);
     * } catch (IOException e) {
     *     e.printStackTrace();
     * }
     * }</pre>
     * </p>
     *
     * @param context         The application context, used to access external storage directories.
     * @param prefix          The prefix to be used in the generated filename.
     * @param suffix          The suffix to be used in the generated filename.
     * @param childFolder     The name of the child folder in the Pictures directory where the file should be saved.
     * @param compressQuality The compression quality for the PNG image (0-100).
     * @return The absolute path of the saved image file.
     * @throws IOException If an error occurs during file creation.
     */
    public String saveSignatureImage(Context context, String prefix, String suffix, String childFolder, int compressQuality) throws IOException {

        // Create a bitmap from the current drawing of the view.
        Bitmap signatureBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(signatureBitmap);
        draw(canvas);

        // Generate a timestamped filename.
        String timeStamp = new SimpleDateFormat(DATE_COMPACT_WITH_UNDERSCORE.getPattern(), Locale.getDefault())
                .format(new Date());
        String imageFileName = prefix + "_" + timeStamp + "_" + suffix;

        // Generate or retrieve the storage directory.
        File storageDir = FileUtil.generateFolder(context, Environment.DIRECTORY_PICTURES, childFolder);

        // Create a temporary file with a .png extension.
        File image = File.createTempFile(imageFileName, ".png", storageDir);

        // Save the bitmap into the file as a PNG.
        try (FileOutputStream fos = new FileOutputStream(image)) {
            signatureBitmap.compress(Bitmap.CompressFormat.PNG, compressQuality, fos);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw e;
        }

        // Return the absolute path to the saved file.
        return image.getAbsolutePath();
    }

}


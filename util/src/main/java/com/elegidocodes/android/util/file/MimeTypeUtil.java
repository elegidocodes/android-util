package com.elegidocodes.android.util.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.ByteArrayOutputStream;

public class MimeTypeUtil {

    /**
     * Retrieves the MIME type of a given URI.
     *
     * <p>This method attempts to determine the MIME type of a URI using the following steps:
     * <ul>
     *     <li>First, it queries the system's content resolver for the MIME type.</li>
     *     <li>If the MIME type cannot be determined directly, it falls back to using the file extension.</li>
     * </ul>
     *
     * <p>Example usage:
     * <pre>{@code
     * Uri fileUri = Uri.parse("content://path/to/file.jpg");
     * String mimeType = getMimeType(context, fileUri);
     * System.out.println("MIME Type: " + mimeType); // Output: image/jpeg
     * }</pre>
     *
     * @param context The context to access the content resolver.
     * @param uri     The URI of the file for which to retrieve the MIME type.
     * @return The MIME type of the file as a string, or {@code null} if it could not be determined.
     */
    public static String getMimeType(Context context, Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);
        if (mimeType == null && uri.getPath() != null) {
            // Try to determine MIME type based on the file extension
            String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(uri.getPath());
            if (extension != null) {
                mimeType = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
            }
        }
        return mimeType;
    }

    /**
     * Converts a Bitmap into a byte array using the specified compression format.
     *
     * <p>This method compresses the given bitmap into a byte array with 100% quality using the provided format
     * (e.g., JPEG, PNG). The resulting byte array can be used for tasks such as saving the bitmap to storage
     * or uploading it to a server.
     *
     * <p>Example usage:
     * <pre>{@code
     * Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);
     * byte[] byteArray = getByteArrayFromBitmap(bitmap, Bitmap.CompressFormat.JPEG);
     * }</pre>
     *
     * @param bitmap The bitmap to be converted into a byte array.
     * @param format The compression format (e.g., {@link Bitmap.CompressFormat#JPEG}).
     * @return A byte array containing the compressed bitmap data.
     */
    public static byte[] getByteArrayFromBitmap(Bitmap bitmap, Bitmap.CompressFormat format) {
        if (bitmap == null) {
            throw new IllegalArgumentException("Bitmap must not be null");
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(format, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

}
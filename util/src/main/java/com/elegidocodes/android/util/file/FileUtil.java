package com.elegidocodes.android.util.file;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    /**
     * Opens a file using the appropriate application on the device.
     *
     * <p>This method generates a URI for the specified file using the FileProvider and launches an intent
     * to open the file with the associated application. It can handle various file types by specifying
     * the MIME type (e.g., "application/pdf", "image/*").</p>
     *
     * <p>Required setup in the AndroidManifest.xml:
     * <pre>{@code
     * <provider
     *     android:name="androidx.core.content.FileProvider"
     *     android:authorities="com.example.fileprovider"
     *     android:exported="false"
     *     android:grantUriPermissions="true">
     *     <meta-data
     *         android:name="android.support.FILE_PROVIDER_PATHS"
     *         android:resource="@xml/file_paths" />
     * </provider>
     * }</pre>
     * </p>
     *
     * <p>Example usage:
     * <pre>{@code
     * String filePath = "/path/to/file.pdf";
     * String authority = "com.example.fileprovider";
     * String mimeType = "application/pdf";
     * String title = "Open PDF with";
     * FileUtil.open(context, filePath, authority, mimeType, title);
     * }</pre>
     *
     * @param context   The application context.
     * @param filePath  The absolute path to the file to open.
     * @param authority The authority string defined in the FileProvider configuration.
     * @param mimeType  The MIME type of the file (e.g., "application/pdf", "image/*").
     * @param title     The title for the chooser dialog.
     * @throws IllegalArgumentException If the file does not exist or the path is invalid.
     */
    public static void open(Context context, String filePath, String authority, String mimeType, String title) {
        File file = new File(filePath);

        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist at the specified path: " + filePath);
        }

        Uri uri = FileProvider.getUriForFile(context, authority, file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Use a chooser to give the user flexibility in selecting an app
        Intent chooser = Intent.createChooser(intent, title);
        context.startActivity(chooser);
    }

    /**
     * Shares a file using the appropriate application on the device.
     *
     * <p>This method generates a URI for the specified file using the FileProvider and launches an intent
     * to share the file with other applications. It supports various file types by specifying the MIME type
     * (e.g., "application/pdf", "image/*").</p>
     *
     * <p>Required setup in the AndroidManifest.xml:
     * <pre>{@code
     * <provider
     *     android:name="androidx.core.content.FileProvider"
     *     android:authorities="com.example.fileprovider"
     *     android:exported="false"
     *     android:grantUriPermissions="true">
     *     <meta-data
     *         android:name="android.support.FILE_PROVIDER_PATHS"
     *         android:resource="@xml/file_paths" />
     * </provider>
     * }</pre>
     * </p>
     *
     * <p>Example usage:
     * <pre>{@code
     * String filePath = "/path/to/file.pdf";
     * String authority = "com.example.fileprovider";
     * String mimeType = "application/pdf";
     * String title = "Share PDF using";
     * FileUtil.share(context, filePath, authority, mimeType, title);
     * }</pre>
     *
     * @param context   The application context.
     * @param filePath  The absolute path to the file to share.
     * @param authority The authority string defined in the FileProvider configuration.
     * @param mimeType  The MIME type of the file (e.g., "application/pdf", "image/*").
     * @param title     The title for the chooser dialog.
     * @throws IllegalArgumentException If the file does not exist or the path is invalid.
     */
    public static void share(Context context, String filePath, String authority, String mimeType, String title) {
        File file = new File(filePath);

        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist at the specified path: " + filePath);
        }

        Uri uri = FileProvider.getUriForFile(context, authority, file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType(mimeType);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Use a chooser to give the user flexibility in selecting an app
        Intent chooser = Intent.createChooser(shareIntent, title);
        context.startActivity(chooser);
    }

    /**
     * Generates a bitmap preview of the first page of a PDF file.
     *
     * <p>This method uses the Android {@link PdfRenderer} to render the first page of a PDF file into
     * a {@link Bitmap}. The resulting bitmap can be used as a thumbnail or preview image.</p>
     *
     * <p>Example usage:
     * <pre>{@code
     * String pdfPath = "/path/to/document.pdf";
     * Bitmap preview = FileUtil.getFirstPagePreview(pdfPath);
     * if (preview != null) {
     *     imageView.setImageBitmap(preview);
     * }
     * }</pre>
     *
     * @param pdfPath The absolute file path to the PDF document.
     * @return A {@link Bitmap} representing the first page of the PDF, or {@code null} if an error occurs.
     * @throws IllegalArgumentException If the provided file path is null or empty.
     */
    public static Bitmap getFirstPagePreview(String pdfPath) {
        if (pdfPath == null || pdfPath.isEmpty()) {
            throw new IllegalArgumentException("PDF path must not be null or empty");
        }

        File pdfFile = new File(pdfPath);

        if (!pdfFile.exists()) {
            throw new IllegalArgumentException("File does not exist at the specified path: " + pdfPath);
        }

        try (ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
             PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor)) {

            PdfRenderer.Page firstPage = pdfRenderer.openPage(0);

            // Create a bitmap with the page dimensions
            Bitmap bitmap = Bitmap.createBitmap(firstPage.getWidth(), firstPage.getHeight(), Bitmap.Config.ARGB_8888);
            firstPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            // Close the page
            firstPage.close();

            return bitmap;

        } catch (IOException e) {
            Log.e("FileUtil", "Error rendering PDF first page: " + e.getMessage(), e);
            return null;
        }

    }

}

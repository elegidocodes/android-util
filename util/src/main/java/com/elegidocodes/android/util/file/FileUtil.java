package com.elegidocodes.android.util.file;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

        try (ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY); PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor)) {

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

    /**
     * Formats a file size given in bytes into a human-readable string with appropriate units.
     *
     * <p>This method converts the file size into a more readable format using units like bytes,
     * kilobytes, megabytes, gigabytes, and larger units. It supports both abbreviated (e.g., "MB") and
     * full names (e.g., "megabytes") for the units.</p>
     *
     * <p>Example usage:
     * <pre>{@code
     * long bytes = 123456789;
     * String formattedSizeAbbr = formatFileSize(bytes, true);
     * String formattedSizeFull = formatFileSize(bytes, false);
     * System.out.println(formattedSizeAbbr); // Output: "117.74 MB"
     * System.out.println(formattedSizeFull); // Output: "117.74 megabytes"
     * }</pre>
     *
     * @param bytes       The size of the file in bytes.
     * @param abbreviated Whether to use abbreviated units (e.g., "MB") or full names (e.g., "megabytes").
     * @return A formatted string representing the file size in human-readable units (e.g., "117.74 MB").
     */
    public static String formatFileSize(long bytes, boolean abbreviated) {
        if (bytes < 0) {
            throw new IllegalArgumentException("File size cannot be negative");
        }

        double fileSize = bytes;

        // Units in abbreviated and full forms
        String[] unitsAbbr = {"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        String[] unitsFull = {"bytes", "kilobytes", "megabytes", "gigabytes", "terabytes", "petabytes", "exabytes", "zettabytes", "yottabytes"};

        String[] selectedUnits = abbreviated ? unitsAbbr : unitsFull;

        int index = 0;
        while (fileSize >= 1024 && index < selectedUnits.length - 1) {
            fileSize /= 1024;
            index++;
        }

        return String.format(Locale.getDefault(), "%.2f %s", fileSize, selectedUnits[index]);
    }

    /**
     * Compresses a folder into a ZIP file.
     *
     * <p>This method takes a parent folder and a target folder name, compressing all files in the
     * target folder into a ZIP file. The resulting ZIP file is saved in a specified output folder.</p>
     *
     * <p>Example usage:
     * <pre>{@code
     * File parentFolder = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
     * String targetFolderName = "PDF";
     * String outputFolderName = "ZIP";
     * String zipFilePath = FileUtil.zipFolder(context, parentFolder, targetFolderName, outputFolderName);
     * System.out.println("ZIP File Path: " + zipFilePath);
     * }</pre>
     *
     * @param context          The application context.
     * @param parentFolder     The parent folder containing the folder to be zipped.
     * @param targetFolderName The name of the folder to compress.
     * @param outputFolderName The name of the folder where the ZIP file will be saved.
     * @return The absolute path to the created ZIP file, or {@code null} if an error occurred.
     */
    public static String zipFolder(Context context, File parentFolder, String targetFolderName, String outputFolderName) {
        File targetFolder = new File(parentFolder, targetFolderName);

        if (targetFolder.exists() && targetFolder.isDirectory()) {
            File outputFolder = new File(parentFolder, outputFolderName);

            if (!outputFolder.exists()) {
                boolean wasCreated = outputFolder.mkdir();
                if (wasCreated) {
                    Log.i("FILE_UTILS", "Output folder created: " + outputFolder.getAbsolutePath());
                } else {
                    Log.e("FILE_UTILS", "Failed to create output folder: " + outputFolder.getAbsolutePath());
                    return null;
                }
            }

            String zipFileName = targetFolderName + "_compressed.zip";
            File zipFile = new File(outputFolder, zipFileName);

            try (FileOutputStream fos = new FileOutputStream(zipFile);
                 ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos))) {

                File[] files = targetFolder.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        if (file.isFile()) { // Ensure only files are processed
                            try (FileInputStream fis = new FileInputStream(file);
                                 BufferedInputStream bis = new BufferedInputStream(fis)) {

                                ZipEntry zipEntry = new ZipEntry(file.getName());
                                zos.putNextEntry(zipEntry);

                                byte[] buffer = new byte[1024];
                                int count;
                                while ((count = bis.read(buffer)) != -1) {
                                    zos.write(buffer, 0, count);
                                }

                                zos.closeEntry();
                            } catch (IOException e) {
                                Log.e("FILE_UTILS", "Error while compressing file: " + file.getName(), e);
                            }
                        }
                    }
                } else {
                    Log.e("FILE_UTILS", "No files found in target folder: " + targetFolder.getAbsolutePath());
                    return null;
                }

            } catch (IOException e) {
                Log.e("FILE_UTILS", "Error while creating ZIP file", e);
                return null;
            }

            return zipFile.getAbsolutePath();

        } else {
            Log.e("FILE_UTILS", "Target folder does not exist or is not a directory: " + targetFolder.getAbsolutePath());
            return null;
        }
    }

}

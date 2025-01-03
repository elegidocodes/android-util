package com.elegidocodes.android.util.file;

import static com.elegidocodes.android.util.date.DateUtil.DateFormats.DATE_COMPACT_WITH_UNDERSCORE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    /**
     * Converts a {@link Uri} to a temporary {@link File}.
     *
     * <p>This method reads the content from the provided {@code Uri} and writes it to a temporary file
     * in the app's cache directory. The file is set to be automatically deleted when the app exits.</p>
     *
     * <p>Example usage:
     * <pre>{@code
     * Uri uri = ...; // A valid Uri
     * File tempFile = FileUtil.uriToFile(context, uri, "temp_", ".tmp");
     * if (tempFile != null) {
     *     System.out.println("Temporary file created at: " + tempFile.getAbsolutePath());
     * }
     * }</pre>
     *
     * @param context The application context.
     * @param uri     The {@link Uri} of the file to convert.
     * @param prefix  The prefix for the temporary file name.
     * @param suffix  The suffix for the temporary file name, typically a file extension (e.g., ".tmp").
     * @return A {@link File} object pointing to the temporary file, or {@code null} if an error occurred.
     */
    public static File uriToFile(Context context, Uri uri, String prefix, String suffix) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream == null) {
                Log.e("FileUtil", "Failed to open InputStream for URI: " + uri);
                return null;
            }

            // Create a temporary file with the specified prefix and suffix
            File tempFile = File.createTempFile(prefix, suffix, context.getCacheDir());
            tempFile.deleteOnExit(); // Mark the file for deletion when the app exits

            // Conditionally use appropriate output stream based on API level
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try (OutputStream outputStream = Files.newOutputStream(tempFile.toPath())) {
                    copyStream(inputStream, outputStream);
                }
            } else {
                try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                    copyStream(inputStream, outputStream);
                }
            }

            return tempFile;
        } catch (Exception e) {
            Log.e("FileUtil", "Error converting URI to File: " + uri, e);
            return null; // Return null if an error occurred
        }
    }

    /**
     * Copies data from an InputStream to an OutputStream.
     *
     * @param inputStream  The source InputStream.
     * @param outputStream The destination OutputStream.
     * @throws IOException If an I/O error occurs during the copy operation.
     */
    private static void copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }

    /**
     * Creates a temporary file with a specified prefix, suffix, directory, and date pattern.
     *
     * <p>This method generates a file in the specified directory. If the directory does not exist,
     * it will attempt to create it. The prefix and suffix can be customized, and a date pattern can be
     * used to include timestamps in the file name.</p>
     *
     * <p>Example usage:
     * <pre>{@code
     * String directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
     * File file = FileUtil.createFile("file", ".txt", directory, "yyyy-MM-dd_HH-mm-ss");
     * System.out.println("File created at: " + file.getAbsolutePath());
     * }</pre>
     *
     * @param prefix      The prefix for the file name. If null or empty, the default date pattern will be used.
     * @param suffix      The suffix for the file name (e.g., ".txt"). If null or empty, no suffix is added.
     * @param directory   The directory where the file will be created.
     * @param datePattern The date pattern to include in the file name. If null or empty, the default pattern "yyyyMMdd_HHmmss" is used.
     * @return A {@link File} object pointing to the created file.
     * @throws IOException If an error occurs during file creation.
     */
    public static File createFile(String prefix, String suffix, String directory, String datePattern) throws IOException {
        // Use default date pattern if none is provided
        if (datePattern == null || datePattern.isEmpty()) {
            datePattern = DATE_COMPACT_WITH_UNDERSCORE.getPattern();
        }

        // Generate the timestamp for the file name
        String timestamp = new SimpleDateFormat(datePattern, Locale.getDefault()).format(new Date());

        // Use the timestamp as prefix if no prefix is provided
        if (prefix == null || prefix.isEmpty()) {
            prefix = timestamp;
        } else {
            prefix = prefix + "_" + timestamp;
        }

        // Use empty suffix if none is provided
        if (suffix == null || suffix.isEmpty()) {
            suffix = "";
        }

        // Ensure the storage directory exists
        File fileDirectory = new File(directory);
        if (!fileDirectory.exists()) {
            boolean mkdirsResult = fileDirectory.mkdirs();
            if (mkdirsResult) {
                Log.d("FileUtil", "Directory created: " + fileDirectory.getAbsolutePath());
            } else {
                Log.e("FileUtil", "Failed to create directory: " + fileDirectory.getAbsolutePath());
            }
        }

        // Create the temporary file in the specified directory

        return File.createTempFile(prefix, suffix, fileDirectory);
    }

    /**
     * Converts an image {@link Uri} to a {@link File} in a specified directory.
     *
     * <p>This method decodes an image from the provided {@link Uri}, compresses it using the specified
     * {@link Bitmap.CompressFormat}, and saves it to a file in the specified directory. The file's extension
     * is determined by the MIME type, ensuring proper file association. The compression quality is customizable,
     * allowing the user to control the output file size and image quality.</p>
     *
     * <p>Example usage:
     * <pre>{@code
     * Uri imageUri = ...; // A valid image Uri
     * String directory = context.getCacheDir().getAbsolutePath();
     * File imageFile = FileUtil.uriToFileForImage(context, imageUri, directory, "image/jpeg", 80);
     * if (imageFile != null) {
     *     System.out.println("Image file created at: " + imageFile.getAbsolutePath());
     * }
     * }</pre>
     *
     * @param context         The application context for accessing content resolver.
     * @param uri             The {@link Uri} of the image to convert.
     * @param directory       The directory where the image file will be saved.
     * @param mimeType        The MIME type of the image (e.g., "image/jpeg" or "image/png").
     * @param compressQuality The compression quality for the image (0–100, where 100 is the highest quality).
     * @return A {@link File} object pointing to the saved image file, or {@code null} if an error occurred.
     */
    public static File uriToFileForImage(Context context, Uri uri, String directory, String mimeType, int compressQuality) {
        Bitmap.CompressFormat compressFormat;
        String fileExtension;

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream == null) {
                Log.e("FileUtil", "Failed to open InputStream for URI: " + uri);
                return null;
            }

            // Decode the image from the InputStream
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null) {
                Log.e("FileUtil", "Failed to decode image from URI: " + uri);
                return null;
            }

            // Determine the compress format and file extension based on MIME type
            if ("image/png".equalsIgnoreCase(mimeType)) {
                compressFormat = Bitmap.CompressFormat.PNG;
                fileExtension = ".png";
            } else if ("image/jpeg".equalsIgnoreCase(mimeType) || "image/jpg".equalsIgnoreCase(mimeType)) {
                compressFormat = Bitmap.CompressFormat.JPEG;
                fileExtension = ".jpg";
            } else {
                Log.e("FileUtil", "Unsupported MIME type: " + mimeType);
                return null;
            }

            // Create the output file with the appropriate extension
            File outputFile = createFile("image", fileExtension, directory, DATE_COMPACT_WITH_UNDERSCORE.getPattern());
            if (outputFile == null) {
                Log.e("FileUtil", "Failed to create output file");
                return null;
            }

            // Write the compressed bitmap to the file
            try (OutputStream outputStream = new FileOutputStream(outputFile)) {
                if (!bitmap.compress(compressFormat, compressQuality, outputStream)) {
                    Log.e("FileUtil", "Failed to compress and save the image");
                    return null;
                }
            }

            return outputFile;

        } catch (Exception e) {
            Log.e("FileUtil", "Error converting URI to file: " + uri, e);
            return null;
        }
    }

    /**
     * Compresses an image in place, overwriting the original file with the compressed version.
     *
     * <p>This method loads an image from the given file path, compresses it using the specified
     * {@link Bitmap.CompressFormat}, and writes the compressed image back to the same file. The compression
     * quality is customizable, allowing control over the balance between image quality and file size.</p>
     *
     * <p>Example usage:
     * <pre>{@code
     * String filePath = "/path/to/image.jpg";
     * boolean result = FileUtil.compressImageInPlace(filePath, "image/jpeg", 80);
     * if (result) {
     *     System.out.println("Image compressed successfully.");
     * } else {
     *     System.out.println("Failed to compress image.");
     * }
     * }</pre>
     *
     * @param filePath        The absolute path of the image file to compress.
     * @param mimeType        The MIME type of the image (e.g., "image/jpeg" or "image/png").
     * @param compressQuality The compression quality (0–100, where 100 is the highest quality).
     * @return {@code true} if the compression and overwrite were successful; {@code false} otherwise.
     */
    public static boolean compressAndOverwriteImage(String filePath, String mimeType, int compressQuality) {
        if (filePath == null || filePath.isEmpty()) {
            Log.e("FileUtil", "Invalid file path provided.");
            return false;
        }

        if (compressQuality < 0 || compressQuality > 100) {
            Log.e("FileUtil", "Invalid compression quality: " + compressQuality);
            return false;
        }

        Bitmap.CompressFormat compressFormat;
        try {
            // Load the bitmap from the file path
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            if (bitmap == null) {
                Log.e("FileUtil", "Failed to load image from file path: " + filePath);
                return false;
            }

            // Determine the compression format based on the MIME type
            if ("image/png".equalsIgnoreCase(mimeType)) {
                compressFormat = Bitmap.CompressFormat.PNG;
            } else if ("image/jpeg".equalsIgnoreCase(mimeType) || "image/jpg".equalsIgnoreCase(mimeType)) {
                compressFormat = Bitmap.CompressFormat.JPEG;
            } else {
                Log.e("FileUtil", "Unsupported MIME type: " + mimeType);
                return false;
            }

            // Overwrite the original file with compressed content
            try (OutputStream outputStream = new FileOutputStream(filePath)) {
                if (!bitmap.compress(compressFormat, compressQuality, outputStream)) {
                    Log.e("FileUtil", "Failed to compress and overwrite the image.");
                    return false;
                }
            }

            return true; // Compression and overwrite were successful

        } catch (Exception e) {
            Log.e("FileUtil", "Error compressing image in place: " + filePath, e);
            return false;
        }
    }

}

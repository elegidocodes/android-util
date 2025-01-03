package com.elegidocodes.android.util.image;

import android.graphics.BitmapFactory;

public class ImageUtil {

    /**
     * Calculates the aspect ratio of an image file as a string in the format "width:height".
     *
     * <p>This method reads the dimensions of an image file without fully loading it into memory,
     * making it efficient for large images. The greatest common divisor (GCD) is used to simplify
     * the aspect ratio to its lowest terms.
     *
     * <p>Example usage:
     * <pre>{@code
     * String imagePath = "/path/to/image.jpg";
     * String aspectRatio = getAspectRatioAsString(imagePath);
     * System.out.println("Aspect Ratio: " + aspectRatio); // Output: "16:9" or "4:3"
     * }</pre>
     *
     * @param imagePath The file path of the image.
     * @return A string representing the aspect ratio in the format "width:height", or {@code null} if the dimensions could not be determined.
     * @throws IllegalArgumentException If the provided imagePath is null or empty.
     */
    public static String getAspectRatioAsString(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            throw new IllegalArgumentException("Image path must not be null or empty");
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;

        if (imageWidth > 0 && imageHeight > 0) {
            int gcd = gcd(imageWidth, imageHeight);
            return (imageWidth / gcd) + ":" + (imageHeight / gcd);
        } else {
            return null; // Dimensions could not be determined
        }
    }

    /**
     * Calculates the greatest common divisor (GCD) of two integers using the Euclidean algorithm.
     *
     * @param a The first integer.
     * @param b The second integer.
     * @return The greatest common divisor of {@code a} and {@code b}.
     */
    private static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

}

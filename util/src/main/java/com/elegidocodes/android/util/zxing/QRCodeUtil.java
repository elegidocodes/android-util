package com.elegidocodes.android.util.zxing;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;

public class QRCodeUtil {

    /**
     * Generates a QR Code from the provided URL as a Bitmap.
     *
     * <p>This method uses the {@link MultiFormatWriter} to encode the given URL into a QR code
     * and creates a Bitmap representation of it.</p>
     *
     * @param url The URL to encode in the QR code.
     * @return A {@link Bitmap} containing the QR code.
     * @throws WriterException If the QR code encoding fails.
     */
    public static Bitmap generateQRCodeBitmap(String url) throws WriterException {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }

        return bitmap;
    }

    /**
     * Generates a QR Code from the provided URL as a byte array in PNG format.
     *
     * <p>This method uses the {@link MultiFormatWriter} to encode the given URL into a QR code,
     * converts it to a Bitmap, and then compresses it into a PNG byte array.</p>
     *
     * @param url The URL to encode in the QR code.
     * @return A byte array containing the QR code image in PNG format.
     * @throws WriterException If the QR code encoding fails.
     */
    public static byte[] generateQRCodeBytes(String url) throws WriterException {
        Bitmap bitmap = generateQRCodeBitmap(url);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

}

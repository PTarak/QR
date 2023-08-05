package com.qr.qr;

import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.BarcodeFormat;
import com.qr.qr.dto.QrData;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/QRValidation")
public class QRController {

    @GetMapping(value = "/dummy")
    public String dummyEndPoint() {
        return "This is dummy end point";
    }

    @PostMapping(value = "/code",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] qrCodeGeneration(@RequestBody QrData qrData) {
        System.out.println(qrData.toString());
        String data = qrData.getData();
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hintsMap = new HashMap<>();
        hintsMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix;
        try {
            bitMatrix = qrCodeWriter.encode(data,
                    BarcodeFormat.QR_CODE,
                    200,
                    200,
                    hintsMap);
        } catch (Exception e) {
            throw new RuntimeException("Error in generating the qr code {}", e);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedImage qrImage = toBufferedImage(bitMatrix);
        try {
            ImageIO.write(qrImage, "jpeg", outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error in writing image to stream {}", e);
        }
        return outputStream.toByteArray();
    }

    @GetMapping(value = "/code/{data}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] creatingQrCodeImage(@PathVariable String data) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hintsMap = new HashMap<>();
        hintsMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix;
        try {
            bitMatrix = qrCodeWriter.encode(data,
                    BarcodeFormat.QR_CODE,
                    100,
                    100,
                    hintsMap);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code image.", e);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedImage qrImage = toBufferedImage(bitMatrix);
        try {
            ImageIO.write(qrImage, "jpeg", outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write QR code image to output stream.", e);
        }
        return outputStream.toByteArray();
    }

    private BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.BLACK);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (matrix.get(x, y)) {
                    graphics.fillRect(x, y, 1, 1);
                }
            }
        }
        return image;
    }
}

package com.qr.qr;

import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.BarcodeFormat;
import com.qr.qr.dto.QrData;
import com.qr.qr.service.QuickResponseCodeGeneration;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    QuickResponseCodeGeneration quickResponseCodeGeneration;

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
        return quickResponseCodeGeneration.generatingQuickResponseCode(data);
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
        BufferedImage qrImage = quickResponseCodeGeneration.toBufferedImage(bitMatrix);
        try {
            ImageIO.write(qrImage, "jpeg", outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write QR code image to output stream.", e);
        }
        return outputStream.toByteArray();
    }
}

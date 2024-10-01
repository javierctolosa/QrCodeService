package qrcodeapi;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.springframework.stereotype.Service;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

@Service
public class QrCodeService {
    public BufferedImage createQrCode(String contents, String correction, int size) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        ErrorCorrectionLevel errorCorrectionLevel = getErrorCorrectionLevel(correction);
        Map<EncodeHintType, ErrorCorrectionLevel> hints = Map.of(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE, size, size, hints);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            System.out.println("Could not encode contents into QR Code");
        }

        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, size, size);
        g.dispose();
        return image;
    }

    private ErrorCorrectionLevel getErrorCorrectionLevel(String correction) {
        for (ErrorCorrectionLevel errorCorrectionLevel : ErrorCorrectionLevel.values()) {
            if (errorCorrectionLevel.name().equalsIgnoreCase(correction)) { return errorCorrectionLevel; }
        }
        return ErrorCorrectionLevel.L;
    }
}

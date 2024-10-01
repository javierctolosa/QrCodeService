package qrcodeapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class QrCodeController {
    private final QrCodeService qrCodeService;
    private static final Set<String> CORRECTION_LEVELS = new HashSet<>(List.of("l","m","q","h"));
    private static final Set<String> SUPPORTED_FORMATS = new HashSet<>(List.of("png","jpeg","gif"));
    private static final Map<String, MediaType> MEDIA_TYPE_MAP = Map.of(
            "png", MediaType.IMAGE_PNG,
            "jpeg", MediaType.IMAGE_JPEG,
            "gif", MediaType.IMAGE_GIF
    );

    @Autowired
    public QrCodeController(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/api/health")
    public ResponseEntity<?> healthCheck() {
        return new ResponseEntity<>("200 OK", HttpStatus.OK);
    }

    @GetMapping("/api/qrcode")
    public ResponseEntity<?> qrCode(
            @RequestParam(name = "contents", defaultValue = "") String contents,
            @RequestParam(name = "correction", defaultValue = "L") String correction,
            @RequestParam(name = "size", defaultValue = "250") int size,
            @RequestParam(name = "type", defaultValue = "png") String format) {

        correction = correction.toLowerCase();
        format = format.toLowerCase();

        if (contents.isEmpty() || contents.trim().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new QrErrorHandler("Contents cannot be null or blank"));
        }
        if (size < 150 || size > 350) {
            return ResponseEntity.badRequest()
                    .body(new QrErrorHandler("Image size must be between 150 and 350 pixels"));
        }
        if (!CORRECTION_LEVELS.contains(correction)) {
            return ResponseEntity.badRequest()
                    .body(new QrErrorHandler("Permitted error correction levels are L, M, Q, H"));
        }
        if (!SUPPORTED_FORMATS.contains(format)) {
            return ResponseEntity.badRequest()
                    .body(new QrErrorHandler("Only png, jpeg and gif image types are supported"));

        }

        BufferedImage bufferedImage = qrCodeService.createQrCode(contents, correction, size);
        return ResponseEntity
                .ok()
                .contentType(MEDIA_TYPE_MAP.get(format))
                .body(bufferedImage);
    }
}

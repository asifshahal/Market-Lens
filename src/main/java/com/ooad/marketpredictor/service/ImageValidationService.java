package com.ooad.marketpredictor.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;

@Service
public class ImageValidationService {
    private static final Set<String> ALLOWED_TYPES = Set.of("image/png", "image/jpeg");
    public String validate(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Choose a chart screenshot first.");
        if (file.getSize() > 5L * 1024 * 1024) throw new IllegalArgumentException("Screenshot must be 5 MB or smaller.");
        String type = file.getContentType();
        if (type == null || !ALLOWED_TYPES.contains(type.toLowerCase())) throw new IllegalArgumentException("Use a PNG or JPEG chart screenshot.");
        try {
            if (ImageIO.read(new ByteArrayInputStream(file.getBytes())) == null) throw new IllegalArgumentException("The uploaded file is not a readable image.");
        } catch (IOException e) { throw new IllegalArgumentException("The screenshot could not be read."); }
        return type.toLowerCase();
    }
}

package org.redquark.apps.filesapp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Objects;

import static org.redquark.apps.filesapp.constants.AppConstants.HYPHEN;

public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    public static File convertMultipartFileToFile(MultipartFile multipartFile) {
        LOGGER.debug("{}: converting multipart file to file", TAG);
        try {
            File convertedFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            OutputStream fileOutputStream = new FileOutputStream(convertedFile);
            fileOutputStream.write(multipartFile.getBytes());
            fileOutputStream.close();
            return convertedFile;
        } catch (IOException e) {
            LOGGER.error("{}: cannot convert multipart file to file due to: {}", TAG, e.getMessage());
        }
        return null;
    }

    public static String generateFileName(MultipartFile multipartFile) {
        return LocalDate.now()
                + HYPHEN
                + Objects.requireNonNull(multipartFile.getOriginalFilename()).replace("\\s+", HYPHEN);
    }
}

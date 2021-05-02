package Messenger.services;

import lombok.extern.java.Log;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;

@Service
@Log
public class FileStorageService {
    private final Path fileStorageLocation;

    public FileStorageService() {
        String strTmp = System.getProperty("java.io.tmpdir");
        Path tempRootPath = Paths.get(strTmp).toAbsolutePath().normalize();
        Path tempDirectory = null;
        try {
            tempDirectory = Files.createTempDirectory(tempRootPath, "messenger-uploaded-files");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.fileStorageLocation = tempDirectory;
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            log.log(Level.WARNING, "Could not create the directory where the uploaded files will be stored.", ex);
        }
        log.info("Uploaded file storage: " + fileStorageLocation);
    }

    public String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                log.log(Level.WARNING, "Sorry! Filename contains invalid path sequence " + fileName);
                return null;
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            log.log(Level.WARNING, "Could not store file " + fileName + ". Please try again!", ex);
            return null;
        }
    }

    public Resource loadFileAsResource(String fileName) throws FileNotFoundException {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + fileName);
        }
    }
}

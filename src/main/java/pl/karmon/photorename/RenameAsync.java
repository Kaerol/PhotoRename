package pl.karmon.photorename;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class RenameAsync {

    public static final String NEW_FILE_NAME_PATTERN = "yyyy-MM-dd_HH-mm-ss-SSS";

    private final List<FXWindow> observers = new ArrayList();
    private final List<File> imageFilesFromFolder;
    private final String destinationFolder;

    public RenameAsync(FXWindow fxWindow, List<File> imageFilesFromFolder, String destinationFolder) {
        this.observers.add(fxWindow);
        this.destinationFolder = destinationFolder;
        this.imageFilesFromFolder = imageFilesFromFolder;
    }

    public void renamePhotos() {
        try {
            int photosCount = imageFilesFromFolder.size();
            String result = "";
            for (int i = 0; i < photosCount; i++) {
                File file = imageFilesFromFolder.get(i);
                result = renamePhoto(file);

                double perc = (double) (i + 1) / photosCount;
                updateObservers(perc, result);
            }
            updateObservers(100, "Process finished");
        } catch (Exception e) {
            updateObserversOnError(e.getMessage());
        }
    }

    private void updateObserversOnError(String errorMessage) {
        for (FXWindow observer : observers) {
            observer.onError(errorMessage);
        }
    }

    private void updateObservers(double perc, String result) {
        for (FXWindow observer : observers) {
            observer.onUpdate(perc, result);
        }
    }

    private String renamePhoto(File photoFile) throws Exception {
        Metadata metadata = ImageMetadataReader.readMetadata(photoFile);
        ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        if (directory == null) {
            System.out.println("No EXIF date found.");
            return "No `directory` date found.";
        }
        Date date = directory.getDateOriginal();
        if (date == null) {
            System.out.println("No EXIF date found.");
            return "No `EXIF` date found.";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(NEW_FILE_NAME_PATTERN);
        String formattedDate = sdf.format(date);
        String unique = Integer.toHexString(new Random().nextInt(0xFFFFF));
        String extension = photoFile.getName().substring(photoFile.getName().lastIndexOf('.'));
        String newName = formattedDate + "-" + unique + extension;
        File newFile = new File(photoFile.getParent(), newName);

        Path targetPath = new File(destinationFolder, newFile.getName()).toPath();
        Files.copy(photoFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return "";
    }
}

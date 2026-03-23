package pl.karmon.photorename;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class HelloController implements FXWindow {

    @FXML
    public TextField sourceFolder;
    @FXML
    public TextField destinationFolder;
    @FXML
    public ImageView windowIcon;
    @FXML
    public Label errorShow;
    @FXML
    public ProgressBar progressBar;

    @FXML
    public void initialize() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("rename.png");
            if (inputStream != null) {
                Image image = new Image(inputStream);
                windowIcon.setImage(image);
            }
        } catch (Exception ignored) {
        }
    }

    @FXML
    protected void onRenamePhotos() {
        String sourceFolderText = sourceFolder.getText();
        String destinationFolderText = destinationFolder.getText();

        if (sourceFolderText.isEmpty() || destinationFolderText.isEmpty()) {
            errorShow.setText("Proszę podać folder źródłowy i wynikowy.");
            return;
        }
        List<File> imageFilesFromFolder = getImageFilesFromFolder(sourceFolderText);

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            RenameAsync renameAsync = new RenameAsync(this, imageFilesFromFolder, destinationFolder.getText());
            renameAsync.renamePhotos();
            return null;
        });
        future.thenAccept(System.out::println);
    }

    @FXML
    protected void onExit() {
        System.exit(0);
    }

    @FXML
    protected void onSelectSourceFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Wybierz folder");
        File selectedDirectory = directoryChooser.showDialog(sourceFolder.getScene().getWindow());

        if (selectedDirectory != null) {
            sourceFolder.setText(selectedDirectory.getAbsolutePath());
        } else {
            sourceFolder.setText("");
        }
    }

    @FXML
    protected void onSelectDestinationFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Wybierz folder");
        File selectedDirectory = directoryChooser.showDialog(sourceFolder.getScene().getWindow());

        if (selectedDirectory != null) {
            destinationFolder.setText(selectedDirectory.getAbsolutePath());
        } else {
            destinationFolder.setText("");
        }
    }

    private List<File> getImageFilesFromFolder(String folderPath) {
        File folder = new File(folderPath);
        List<File> imageFiles = new ArrayList<>();

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                        lower.endsWith(".png") || lower.endsWith(".gif") ||
                        lower.endsWith(".bmp") || lower.endsWith(".webp");
            });

            if (files != null) {
                Collections.addAll(imageFiles, files);
            }
        }

        return imageFiles;
    }

    @Override
    public void onUpdate(double perc, String result) {
        progressBar.setProgress(perc);
        errorShow.setText(result);
    }

    @Override
    public void onError(String errorMessage) {
        errorShow.setText(errorMessage);
    }
}

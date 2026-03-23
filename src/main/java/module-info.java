module pl.karmon.photorename {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires metadata.extractor;

    opens pl.karmon.photorename to javafx.fxml;
    exports pl.karmon.photorename;
}
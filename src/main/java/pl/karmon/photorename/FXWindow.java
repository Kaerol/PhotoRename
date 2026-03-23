package pl.karmon.photorename;

public interface FXWindow {
    void onUpdate(double perc, String result);

    void onError(String errorMessage);
}

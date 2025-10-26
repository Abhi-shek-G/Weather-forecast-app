package com.weatherapp.ui;

import com.weatherapp.service.WeatherService;
import com.weatherapp.service.model.WeatherResponse;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

public class WeatherUI {
    private static final WeatherService service = new WeatherService();

    public static Parent create() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        // Top: input
        TextField cityField = new TextField();
        cityField.setPromptText("Enter city, e.g. London or Mumbai");
        Button fetchBtn = new Button("Get Weather");

        HBox top = new HBox(8, cityField, fetchBtn);
        top.setAlignment(Pos.CENTER_LEFT);

        // Center: display
        Label title = new Label("Weather");
        title.getStyleClass().add("title");

        Label location = new Label("-");
        location.getStyleClass().add("large");

        Label description = new Label("");
        Label temp = new Label("");
        Label details = new Label("");

        ImageView iconView = new ImageView();
        iconView.setFitWidth(100);
        iconView.setFitHeight(100);

        VBox center = new VBox(6, title, location, iconView, description, temp, details);
        center.setAlignment(Pos.TOP_CENTER);
        center.setPadding(new Insets(12));

        root.setTop(top);
        root.setCenter(center);

        fetchBtn.setOnAction(ev -> fetch(cityField.getText(), location, description, temp, details, iconView));
        cityField.setOnAction(ev -> fetch(cityField.getText(), location, description, temp, details, iconView));

        return root;
    }

    private static void fetch(String city, Label location, Label description, Label temp, Label details, ImageView iconView) {
        if (city == null || city.trim().isEmpty()) {
            showAlert("Validation", "Please enter a city name.");
            return;
        }
        // run in background thread
        location.setText("Loading...");
        description.setText("");
        temp.setText("");
        details.setText("");
        iconView.setImage(null);

        new Thread(() -> {
            try {
                WeatherResponse wr = service.fetchByCity(city.trim());
                javafx.application.Platform.runLater(() -> {
                    location.setText(wr.name + ", " + (wr.sys != null ? wr.sys.country : ""));
                    if (wr.weather != null && !wr.weather.isEmpty()) {
                        description.setText(wr.weather.get(0).main + " - " + wr.weather.get(0).description);
                        String icon = wr.weather.get(0).icon; // e.g. 04d
                        Image img = loadIcon(icon);
                        if (img != null) iconView.setImage(img);
                    }
                    temp.setText(String.format("%.1f °C", wr.main.temp));
                    details.setText(String.format("Humidity: %d%% | Pressure: %dhPa", wr.main.humidity, wr.main.pressure));
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> {
                    location.setText("Error");
                    description.setText("");
                    temp.setText("");
                    details.setText("");
                    iconView.setImage(null);
                    showAlert("API Error", ex.getMessage());
                });
            }
        }).start();
    }

    private static Image loadIcon(String iconCode) {
        try {
            String url = String.format("https://openweathermap.org/img/wn/%s@2x.png", iconCode);
            return new Image(url, true);
        } catch (Exception e) {
            return null;
        }
    }

    private static void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}

package com.weatherapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.weatherapp.ui.WeatherUI;

public class Main extends Application {
    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        stage.setTitle("Weather Forecast App");
        Scene scene = new Scene(WeatherUI.create(), 600, 400);
        scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

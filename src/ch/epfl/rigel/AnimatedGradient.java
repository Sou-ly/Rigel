package ch.epfl.rigel;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;


public class AnimatedGradient extends Application {
    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        Label label = new Label("Animated gradient");
        root.getChildren().add(label);
        label.getStyleClass().add("animated-gradient");
        Scene scene = new Scene(root,400,400);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/animated-gradient.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        ObjectProperty<Color> baseColor = new SimpleObjectProperty<>();

        KeyValue keyValue1 = new KeyValue(baseColor, Color.RED);
        KeyValue keyValue2 = new KeyValue(baseColor, Color.YELLOW);
        KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);
        KeyFrame keyFrame2 = new KeyFrame(Duration.millis(500), keyValue2);
        Timeline timeline = new Timeline(keyFrame1, keyFrame2);

        baseColor.addListener((obs, oldColor, newColor) -> {
            label.setStyle(String.format("-gradient-base: #%02x%02x%02x; ",
                    (int)(newColor.getRed()*255),
                    (int)(newColor.getGreen()*255),
                    (int)(newColor.getBlue()*255)));
        });

        timeline.setAutoReverse(true);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.EquatorialToHorizontalConversion;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;


public class Main extends Application {

    private static final String RETURN = "\uf053";
    private static final String PLAY   = "\uf04b";
    private static final String RESET  = "\uf0e2";
    private static final String PAUSE  = "\uf04c";
    private static final String SEARCH = "\uf002";
    private static final Color  MY_IRON_WHITE = Color.web("#e2d8e8");
    private ObservedSky observedSky;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StarCatalogue catalogue;
        String HYG_CATALOGUE_NAME = "/hygdata_v3.csv";
        String PUL_CAT_NAME = "/pulscat.csv";
        String AST_CATALOGUE_NAME = "/asterisms.txt";

        try (InputStream hygStream = getClass().getResourceAsStream(HYG_CATALOGUE_NAME)) {
            InputStream asterismStream = getClass().getResourceAsStream(AST_CATALOGUE_NAME);
            InputStream pulsarStream = getClass().getResourceAsStream(PUL_CAT_NAME);
            catalogue = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .loadFrom(asterismStream, AsterismLoader.INSTANCE)
                    .loadFrom(pulsarStream, PulsarDatabaseLoader.INSTANCE).build();
        }

        BorderPane pulsarPane = new BorderPane();
        pulsarPane.setPrefSize(800, 500);
        BorderPane skyPane = new BorderPane();
        skyPane.setPrefSize(1024, 600);
        AnchorPane informationPane = new AnchorPane();
        informationPane.setPrefSize(800, 500);

        DateTimeBean dateTimeBean = new DateTimeBean();
        dateTimeBean.setZonedDateTime(ZonedDateTime.of(LocalDate.now(), LocalTime.now(), ZoneId.systemDefault()));

        TimeAnimator timeAnimator = new TimeAnimator(dateTimeBean);
        ObserverLocationBean observerLocationBean = new ObserverLocationBean();
        observerLocationBean.setCoordinates(GeographicCoordinates.ofDeg(6.57, 46.52));

        ViewingParametersBean viewingParametersBean = new ViewingParametersBean();
        viewingParametersBean.setCenter(HorizontalCoordinates.ofDeg(180.000000000001, 15));
        viewingParametersBean.setFieldOfViewDeg(100);

        SkyCanvasManager canvasManager = new SkyCanvasManager(catalogue, dateTimeBean, observerLocationBean, viewingParametersBean);
        Canvas sky = canvasManager.canvas();
        observedSky = canvasManager.getSky();

        PulsarCanvasManager canvasForPulsarManager = new PulsarCanvasManager(catalogue, dateTimeBean, observerLocationBean, viewingParametersBean);
        Canvas pulsar = canvasForPulsarManager.pulsarCanvas();


        Scene skyScene = new Scene(skyPane);
        skyScene.getStylesheets().add(getClass().getResource("/sky.css").toString());

        Scene Pulsar = new Scene(pulsarPane);
        Pulsar.getStylesheets().add(getClass().getResource("/pulsar.css").toString());

        Scene informationScene = new Scene(informationPane);
        informationScene.getStylesheets().add(getClass().getResource("/information.css").toString());

        Scene menu = new Scene(MainMenu(primaryStage, Pulsar, skyScene, informationScene, sky, pulsar));
        menu.getStylesheets().add(getClass().getResource("/menu.css").toString());
        menu.getStylesheets().add(getClass().getResource("/animated-gradient.css").toExternalForm());

        drawSkyScene(primaryStage, skyPane, sky, observerLocationBean, dateTimeBean, timeAnimator, viewingParametersBean, canvasManager, menu);
        drawPulsarScene(primaryStage, pulsarPane, pulsar, observerLocationBean, canvasForPulsarManager, menu);
        information(primaryStage, informationScene, menu);

        primaryStage.setScene(menu);
        primaryStage.setResizable(true);
        primaryStage.setTitle("Rigel");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(500);
        primaryStage.show();

    }

    private void controlBar(Stage stage, BorderPane root, ObserverLocationBean obs, DateTimeBean dtb, ViewingParametersBean vpb, TimeAnimator timeAnimator, Scene menu) throws IOException {
        HBox controlBar = new HBox();
        controlBar.setId("controlBar");

        //Position
        HBox pos = new HBox(position(obs));

        //Date
        HBox time = new HBox();
        time.setId("timeBox");

        DatePicker datePicker = new DatePicker();
        datePicker.valueProperty().bindBidirectional(dtb.dateProperty());

        Label dateLabel = new Label("Date :");
        dateLabel.setTextFill(Color.WHITE);

        //Hour
        TextField hour = new TextField();
        hour.setId("hour");

        DateTimeFormatter hmsFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTimeStringConverter stringConverter2 = new LocalTimeStringConverter(hmsFormatter, hmsFormatter);

        TextFormatter<LocalTime> timeFormatter = new TextFormatter<>(stringConverter2);
        hour.setTextFormatter(timeFormatter);

        Label hourLabel = new Label("Heure : ");
        timeFormatter.valueProperty().bindBidirectional(dtb.timeProperty());

        hourLabel.setTextFill(Color.WHITE);

        //Zone
        List<String> zoneList = new ArrayList<>(ZoneId.getAvailableZoneIds());
        FXCollections.sort(FXCollections.observableArrayList(zoneList));
        List<ZoneId> zoneIds = zoneList.stream().map(ZoneId::of).collect(Collectors.toList());

        ComboBox<ZoneId> zone = new ComboBox<>(FXCollections.observableArrayList((zoneIds)));
        zone.valueProperty().bindBidirectional(dtb.zoneProperty());
        time.getChildren().addAll(dateLabel, datePicker, hourLabel, hour, zone);

        //Animation time
        HBox animator = new HBox();
        animator.setId("animator");

        ChoiceBox<NamedTimeAccelerator> acc = new ChoiceBox<>(FXCollections.observableArrayList(NamedTimeAccelerator.values()));
        acc.getSelectionModel().select(2);
        timeAnimator.acceleratorProperty().bind(Bindings.select(acc.valueProperty(), "Accelerator"));

        //Button
        Button reset = new Button();
        Button lecture = new Button();
        Button returnBut = new Button();

        Font fontAwesome = getFontAwesome();
        reset.setFont(fontAwesome);
        reset.setText(RESET);
        reset.setOnAction(e -> {
            dtb.setZonedDateTime(ZonedDateTime.of(LocalDate.now(), LocalTime.now(), ZoneId.systemDefault()));
        });

        lecture.setFont(fontAwesome);
        lecture.setText(PLAY);
        lecture.setOnAction(e -> {
            boolean isRunning = timeAnimator.getRunning().get();
            if (!isRunning) {
                timeAnimator.start();
                isRunning = timeAnimator.getRunning().get();
                lecture.setText(PAUSE);
                disableControlBar(zone, datePicker, reset, hour, acc, isRunning);
            } else {
                timeAnimator.stop();
                isRunning = timeAnimator.getRunning().get();
                lecture.setText(PLAY);
                disableControlBar(zone, datePicker, reset, hour, acc, isRunning);
            }
        });

        returnBut.setFont(fontAwesome);
        returnBut.setText(RETURN);
        returnBut.setOnAction(e -> {
            stage.setScene(menu);
            stage.setTitle("Rigel");
            stage.setResizable(false);
        });

        animator.getChildren().addAll(acc, reset, lecture);

        TextField search = new TextField();
        search.setPromptText("Rechercher");
        search.setId("search");
        search.setOnAction(e -> {
            boolean isInList = false;
            CelestialObject found = null;
            String name = search.getText();

            for (CelestialObject c : observedSky.allCO()) {
                if (c.name().equalsIgnoreCase(name)) {
                    isInList = true;
                    found = c;
                }
            }

            if (isInList) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.getDialogPane().setId("confirmationPane");
                alert.setTitle("Résultat  ");
                alert.setHeaderText("L'objet célèste « " + found.name() + " » a été trouvé, il se trouve actuellement aux coordonnées horizontales : " +
                        new EquatorialToHorizontalConversion(dtb.getZonedDateTime(), obs.getCoordinates()).apply(found.equatorialPos()).toString() + "\n" + "\n" +
                        "En cliquant sur voyager, le centre de la projection sera modifié et votre objet célèste apparaîtra au centre de l'image");

                alert.getButtonTypes().clear();
                ButtonType goTo = new ButtonType("Voyager");
                ButtonType OK = new ButtonType("OK");
                alert.getButtonTypes().addAll(OK, goTo);

                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(getClass().getResource("/dialog.css").toString());

                Optional<ButtonType> option = alert.showAndWait();

                if (option.get().equals(goTo)) {
                    vpb.setCenter(new EquatorialToHorizontalConversion(dtb.getZonedDateTime(), obs.getCoordinates()).apply(found.equatorialPos()));
                } else if (option.get().equals(OK)) {
                    alert.close();
                }

            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Résultat ");
                alert.setHeaderText("Non, pas cette fois-ci.");
                alert.setContentText("Désolé, votre objet n'appartient à notre catalogue. Essayer plus tard.");
                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(getClass().getResource("/dialog.css").toString());
                alert.showAndWait();
            }
        });

        Button searchT = new Button();
        searchT.setId("st");
        searchT.setFont(fontAwesome);
        searchT.setText(SEARCH);
        controlBar.getChildren().addAll(returnBut, pos, time, animator, search, searchT);
        root.setTop(controlBar);
    }

    private void sky(BorderPane root, Canvas sky) {
        Pane drawSky = new Pane(sky);
        sky.widthProperty().bind(root.widthProperty());
        sky.heightProperty().bind(root.heightProperty());
        root.setCenter(drawSky);
    }

    private void informationBar(BorderPane root, ViewingParametersBean view, SkyCanvasManager sky) {
        BorderPane inf = new BorderPane();
        inf.setId("inf");

        Text left = new Text();
        left.textProperty().bind(Bindings.format("Champ de vue : %.1f°", view.fieldProperty()));

        left.setFill(Color.WHITE);
        inf.setLeft(left);

        Text right = new Text();
        right.textProperty().bind(Bindings.format("Azimut :  %.1f°, hauteur : %.1f°", sky.mouseAzDegProperty(), sky.mouseAltDegProperty()));
        right.setFill(Color.WHITE);
        inf.setRight(right);

        Text cne = new Text();
        sky.objectUnderMouseProperty().addListener(
                (p, o, n) -> {
                    n.ifPresent(celestialObject -> cne.setText(!(celestialObject.magnitude() <= 10) ? "" : celestialObject.info()));
                    cne.setFill(Color.WHITE);
                    inf.setCenter(cne);
                    root.setBottom(inf);
                });
    }

    private void drawSkyScene(Stage stage, BorderPane skyRoot, Canvas sky, ObserverLocationBean obs, DateTimeBean dtb, TimeAnimator tm, ViewingParametersBean vpb, SkyCanvasManager canvasManager, Scene menu) throws IOException {
        sky(skyRoot, sky);
        controlBar(stage, skyRoot, obs, dtb, vpb, tm, menu);
        informationBar(skyRoot, vpb, canvasManager);
    }

    private Font getFontAwesome() throws IOException {
        Font fontAwesome;
        try (InputStream fontStream = getClass().getResourceAsStream("/Font Awesome 5 Free-Solid-900.otf")) {
            fontAwesome = Font.loadFont(fontStream, 11);
        }
        return fontAwesome;
    }

    private void disableControlBar(Node zone, Node datePicker, Node reset, Node hour, Node acc, boolean isRunning) {
        zone.disableProperty().set(isRunning);
        datePicker.disableProperty().set(isRunning);
        reset.disableProperty().set(isRunning);
        hour.disableProperty().set(isRunning);
        acc.disableProperty().set(isRunning);
    }

    private HBox position(ObserverLocationBean obs) {
        HBox pos = new HBox();

        TextFormatter<Number> lonTextFormatter = positionTextFormatter(true);
        TextFormatter<Number> latTextFormatter = positionTextFormatter(false);

        lonTextFormatter.valueProperty().bindBidirectional(obs.lonDegProperty());
        latTextFormatter.valueProperty().bindBidirectional(obs.latDegProperty());

        TextField lat = new TextField();
        lat.setTextFormatter(latTextFormatter);

        TextField lon = new TextField();
        lon.setTextFormatter(lonTextFormatter);

        Label lonLabel = new Label("Longitude (°) : ");
        lonLabel.setTextFill(Color.WHITE);

        Label latLabel = new Label("Latitude (°) : ");
        latLabel.setTextFill(Color.WHITE);

        pos.getChildren().addAll(lonLabel, lon, latLabel, lat);
        pos.getStylesheets().add(getClass().getResource("/position.css").toString());
        return pos;
    }

    private static TextFormatter<Number> positionTextFormatter(boolean isLon) {
        NumberStringConverter stringConverter = new NumberStringConverter("#0.00");
        UnaryOperator<TextFormatter.Change> filter = (change -> {
            try {
                String newText = change.getControlNewText();
                double l = stringConverter.fromString(newText).doubleValue();
                return (isLon ? GeographicCoordinates.isValidLonDeg(l) : GeographicCoordinates.isValidLatDeg(l))
                        ? change
                        : null;
            } catch (Exception e) {
                return null;
            }
        });
        return new TextFormatter<>(stringConverter, 0, filter);
    }


    // ------------------------- BONUS ------------------------- \\

    private AnchorPane MainMenu(Stage stage, Scene pulsarScene, Scene skyScene, Scene information, Canvas skyCanvas, Canvas pulsarCanvas) {
        AnchorPane menu = new AnchorPane();
        menu.setId("menu");
        menu.setPrefSize(800, 500);

        Text rigel = new Text("Rigel.");
        rigel.setId("title");
        rigel.setFill(Color.WHITE);
        rigel.getStyleClass().add("animated-gradient");



        VBox center = new VBox(10);
        center.setAlignment(Pos.CENTER);

        Button sky = new Button("Observer le ciel");
        sky.setPrefWidth(250);
        sky.setPrefHeight(40);
        sky.setOnAction(e -> {
            stage.setScene(skyScene);
            stage.setTitle("Ciel");
            stage.setResizable(true);
            skyCanvas.requestFocus();

        });

        Button pulsar = new Button("Observer des pulsars");
        pulsar.setPrefWidth(250);
        pulsar.setPrefHeight(40);
        pulsar.setOnAction(e -> {
            stage.setScene(pulsarScene);
            stage.setTitle("Pulsar");
            pulsarCanvas.requestFocus();
        });

        Label label = new Label();

        Button info = new Button("En savoir plus");
        info.setPrefWidth(250);
        info.setPrefHeight(40);
        info.setOnAction(e -> {
            stage.setScene(information);
            stage.setTitle("Information");
        });

        center.getChildren().addAll(sky, pulsar, info);

        AnchorPane.setTopAnchor(rigel, 90.0);
        AnchorPane.setLeftAnchor(rigel, 327.0);
        AnchorPane.setRightAnchor(rigel, 10.0);

        AnchorPane.setTopAnchor(label, 90.0);
        AnchorPane.setLeftAnchor(label, 327.0);
        AnchorPane.setRightAnchor(label, 10.0);

        AnchorPane.setTopAnchor(center, 150.0);
        AnchorPane.setLeftAnchor(center, 30.0);
        AnchorPane.setRightAnchor(center, 30.0);
        AnchorPane.setBottomAnchor(center, 130.0);

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

        menu.getChildren().addAll(center,rigel, label);
        return menu;
    }

    private void pulsar(BorderPane pulsarRoot, Canvas pulsar) {
        Pane drawPul = new Pane(pulsar);
        pulsar.widthProperty().bind(pulsarRoot.widthProperty());
        pulsar.heightProperty().bind(pulsarRoot.heightProperty());
        pulsarRoot.setCenter(drawPul);
    }

    private void controlBarForPulsarScene(Stage stage, BorderPane pulsarPane, ObserverLocationBean obs, Scene menu, PulsarCanvasManager pulsarCanvasManager) throws IOException {
        HBox controlBar = new HBox();
        controlBar.setId("controlBar");

        Font fontAwesome = getFontAwesome();
        Button returnBut = new Button();
        returnBut.setFont(fontAwesome);
        returnBut.setText(RETURN);
        returnBut.setOnAction(e -> {
            stage.setScene(menu);
            stage.setTitle("Rigel");
        });


        Label right = new Label();
        right.setTextFill(Color.WHITE);
        controlBar.setAlignment(Pos.CENTER);
        pulsarCanvasManager.distProperty().addListener(
                (p, o, n) -> right.setText(n.doubleValue() == 0 ? "Distance depuis la Terre (Kpc) : N/A" : "Distance depuis la Terre (Kpc) : " + String.format(Locale.ROOT, "%1.2e", n.doubleValue())));

        HBox dist = new HBox();
        dist.getChildren().add(right);

        //Position
        HBox pos = new HBox(position(obs));
        controlBar.getChildren().addAll(returnBut, pos, dist);
        pulsarPane.setTop(controlBar);
    }

    private void infoBarForPulsarScene(BorderPane pulsarPane, PulsarCanvasManager pulsarCanvasManager) {
        BorderPane info = new BorderPane();
        info.setId("info");

        Text center = new Text();
        center.setId("center");
        center.setFill(MY_IRON_WHITE);
        pulsarCanvasManager.objectUnderMouseProperty().addListener(
                //Takes only the objects with a magnitude >= 15 to be sure that is a pulsar
                (p, o, n) -> n.ifPresent(celestialObject -> center.setText(celestialObject.magnitude() >= 15 ? celestialObject.info() : " ")));

        Text left = new Text();
        left.setFill(MY_IRON_WHITE);
        pulsarCanvasManager.rotationPeriodProperty().addListener(
                (p, o, n) -> left.setText(n.doubleValue() == 0 ? "Période de rotation (s) : N/A" : "Période de rotation (s) : " + String.format(Locale.ROOT, "%.3f", n.doubleValue())));

        Text right = new Text();
        right.setFill(MY_IRON_WHITE);
        pulsarCanvasManager.ageProperty().addListener(
                (p, o, n) -> right.setText(n.doubleValue() == 0 ? "Age (en millions d'année) : N/A" : "Age (en millions d'année)  : " + String.format(Locale.ROOT, "%.2f", n.doubleValue())));

        info.setRight(right);
        info.setLeft(left);
        info.setCenter(center);
        pulsarPane.setBottom(info);
    }

    private void drawPulsarScene(Stage stage, BorderPane pulsar, Canvas p, ObserverLocationBean obs,PulsarCanvasManager canvasManager, Scene menu) throws IOException {
        pulsar(pulsar, p);
        controlBarForPulsarScene(stage, pulsar, obs, menu, canvasManager);
        infoBarForPulsarScene(pulsar, canvasManager);
    }

    private void information(Stage stage, Scene informationPane, Scene menu) throws IOException {
        AnchorPane info = new AnchorPane();
        info.setId("info");
        HBox top = new HBox();

        Font fontAwesome = getFontAwesome();
        Button returnBut = new Button();
        returnBut.setFont(fontAwesome);
        returnBut.setText(RETURN);
        returnBut.setOnAction(e -> {
            stage.setScene(menu);
            stage.setTitle("Menu");
            stage.setResizable(false);
        });

        top.getChildren().add(returnBut);

        AnchorPane.setTopAnchor(top, 10.0);
        AnchorPane.setLeftAnchor(top, 10.0);

        info.getChildren().add(top);
        informationPane.setRoot(info);
    }

}

package dk.easv;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ImageViewerWindowController implements Initializable {
    @FXML
    private ImageView imageView;
    @FXML
    private Label labelFilename, labelColor, labelSlideshow;
    @FXML
    private TextField fieldName;
    @FXML
    private Slider sliderDelay;
    @FXML
    private TableView<Slideshow> tableSlideshow;
    @FXML
    private TableColumn<Slideshow, String> columnName;
    @FXML
    private TableColumn<Slideshow, Integer> columnImages;

    private ExecutorService executorService;
    private Scheduler scheduler;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableSlideshow.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnImages.setCellValueFactory(new PropertyValueFactory<>("totalImages"));
    }

    public List<Image> chooseImages() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image files");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images",
                "*.png", "*.jpg", "*.gif", "*.tif", "*.bmp"));
        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());

        if (files != null) {
            List<Image> images = new ArrayList<>();
            files.forEach((File f) ->
            {
                images.add(new Image(f.toURI().toString()));
            });
            return images;
        }
        return new ArrayList<>();
    }

    public void handleStartAction(ActionEvent actionEvent) throws Exception {
        if (scheduler == null) {
            if (!tableSlideshow.getSelectionModel().getSelectedItems().isEmpty()) {
                scheduler = new Scheduler(new ArrayList<>(tableSlideshow.getSelectionModel().getSelectedItems()), (int) sliderDelay.getValue());

                scheduler.valueProperty().addListener((observableValue, image, t1) -> {
                    displayContent(t1);
                });

                executorService = Executors.newSingleThreadExecutor();
                executorService.submit(scheduler);
            }
        }
    }

    public void displayContent(Image image) {
        File file = new File(image.getUrl());
        int[] colors = ColorCounter.colors(image);

        imageView.setImage(image);
        labelFilename.setText("Image: "+file.getName());
        labelColor.setText("R: " + colors[0] + " G: " + colors[1] + " B: " + colors[2]);
        labelSlideshow.setText("Current slideshow: " + scheduler.getCurrentSlideshowName());
    }

    public void handleStopAction(ActionEvent actionEvent) {
        if (!executorService.isTerminated() || !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        if (scheduler != null) {
            imageView.setImage(null);
            labelFilename.setText("");
            labelColor.setText("");
            labelSlideshow.setText("");
            scheduler.cancel();
            scheduler = null;
        }
    }

    public void handleAddSlideshow(ActionEvent actionEvent) {
        String chosenName = fieldName.getText();

        if (!chosenName.isBlank()) {
            List<Image> images = chooseImages();
            if (!images.isEmpty() && tableSlideshow.getItems().stream().map(Slideshow::getName).noneMatch(s -> s.equals(chosenName))) {
                Slideshow slideshow = new Slideshow(chosenName, images);
                tableSlideshow.getItems().add(slideshow);
                fieldName.clear();
            }
        }
    }

    public void handleRemoveSlideshow(ActionEvent actionEvent) {
        tableSlideshow.getItems().removeAll(tableSlideshow.getSelectionModel().getSelectedItems());
    }
}
package dk.easv;

import javafx.scene.image.Image;

import java.util.List;

public class Slideshow {
    private final String name;

    private final List<Image> images;
    private final int totalImages;

    public Slideshow(String name, List<Image> images) {
        this.name = name;
        this.images = images;
        totalImages = images.size();
    }

    public List<Image> getImages() {
        return images;
    }

    public int getTotalImages() {
        return totalImages;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}

package dk.easv;

import javafx.concurrent.Task;
import javafx.scene.image.Image;

import java.util.List;
import java.util.concurrent.*;

public class Scheduler extends Task<Image> {
    private final BlockingQueue<Slideshow> queue = new ArrayBlockingQueue<>(100);
    private final int delay;

    private int currentImageIndex;
    private Slideshow currentSlideshow;

    public Scheduler(List<Slideshow> slideshows, int delay) {
        this.queue.addAll(slideshows);
        this.delay = delay;
    }


    @Override
    synchronized protected Image call() throws InterruptedException {
        while (true) {
            if (currentSlideshow == null) {
                currentSlideshow = queue.take();
                queue.put(currentSlideshow);
            }

            if (!hasNext()) {
                currentSlideshow = queue.take();
                queue.put(currentSlideshow);
                currentImageIndex = 0;
            }
            updateValue(nextImage());

            TimeUnit.SECONDS.sleep(delay);
        }
    }

    private Image nextImage() {
        return currentSlideshow.getImages().get(currentImageIndex++);
    }

    private boolean hasNext() {
        return currentImageIndex < currentSlideshow.getTotalImages();
    }

    public String getCurrentSlideshowName() {
        return currentSlideshow.getName();
    }
}

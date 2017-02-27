package gui.applet.map;

import de.fhpotsdam.unfolding.marker.SimplePointMarker;

/**
 * Вспомогательные маркеры для работы с программой
 *
 * Created by anatoliy on 27.02.17.
 */
public class ToolMarkers {

    public static ToolMarkers instence = new ToolMarkers();

    public static ToolMarkers getInstence() {
        return instence;
    }

    public ToolMarkers() {
    }

    private SimplePointMarker mouseClicked = new SimplePointMarker();

    public void setMouseClicked(SimplePointMarker mouseClicked) {
        this.mouseClicked = mouseClicked;
    }

    public SimplePointMarker getMouseClicked() {
        return mouseClicked;
    }
}

package tetracoveragearea.gui.applet;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.events.MapEvent;

/**
 * Событие, генерируемое при добавлении / удалении маркеров на карту
 *
 * Created by anatoliy on 22.02.17.
 */
public class MarkerChangeEvent extends MapEvent {


    public MarkerChangeEvent(Object source, String subType, String mapId) {
        super(source, subType, mapId);
    }

    @Override
    public void executeManipulationFor(UnfoldingMap unfoldingMap) {
    }
}

package common.entities.visualPart;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePolygonMarker;

import java.util.List;

/**
 * Полигон-маркер, хранит набор маркеров-точек
 *
 * Created by anatoliy on 17.02.17.
 */
public class PolygonMarkerRssi extends SimplePolygonMarker {

    private float rssi;

    private List<Location> innerLocations;

    public List<Location> getInnerLocations() {
        return innerLocations;
    }

    public PolygonMarkerRssi() {
        super();
    }

    public PolygonMarkerRssi(List<Location> list, float rssi) {

        super(list);
        this.rssi = rssi;
    }

    public float getRssi() {
        return rssi;
    }

    public void setRssi(float rssi) {
        this.rssi = rssi;
    }

    public PolygonMarkerRssi(List<Location> outerLocations, List<Location> innerLocations) {

        super(outerLocations);
        this.innerLocations = innerLocations;
    }

    // TODO: скооперировать данный класс с TriangleMarkerRssi, сделать его наследником этого класса

    /* Просто оставлю здесь:
     * TODO:
      * +1. Переписать апплет в соответствии с новыми классами
      * 4. Тестирование добавления и удаления точек с карты
      * 5. Заполнение панелей GUI, переписать MainFrame
      * 6. Написать класс таблицы и добавить вкладки в MainFrame
      * 7. Добиться отображения объектов в таблице по запросам
      * 8. Тестирование замены тайлового менеджера
      * +/- 9. Замутить красивый градиент в зависимости от rssi и придумать как им управлять с GUI
      * 10. Нагрузочное тестирование */

    /**
     *
     * Промежуточные итоги:
     *
     * 1. Полигоны Вороного не нужны, следовательно и polygonMarkerRssi
     * 2. Отдельные методы для отрисовки точек на карте не нужны (те, которые сейчас в CoverageArea), можно складывать в MapDisplay наследников маркера
     * 3. Придумать как сохранять однажды сгенеренную картинку маркеров в рисунок и сохранять его в высоком разрешении, перерисовывать по взаимодествию пользователя с GUI
     * либо придумать как сократить количество объектов, частоту отрисовок (отрисовка всех маркеров производится при каждой отрисовке карты 60 FPS
     * 4. Можно получать замкнутый полигон по набору Location, но скорей всего это только откушать еще ресурсов
     */
}

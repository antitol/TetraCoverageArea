package common.entities.visualPart;

import de.fhpotsdam.unfolding.marker.SimplePolygonMarker;

import java.util.List;

/**
 * Created by anatoliy on 17.02.17.
 */
public class PolygonMarkerRssi extends SimplePolygonMarker {

    private float rssi;

    List<PointMarkerRssi> markerRssiList;

    // TODO: скооперировать данный класс с TriangleMarkerRssi, сделать его наследником этого класса

    /* Просто оставлю здесь:
     * TODO:
      * 1. Переписать апплет в соответствии с новыми классами
      * 2. Добавить методы, которые будут складывать точки в CoverageMap
      * 3. Написать запросы для точек и вороного, генерации полигонов из точек
      * 4. Тестирование добавления и удаления точек с карты
      * 5. Заполнение панелей GUI, переписать MainFrame
      * 6. Написать класс таблицы и добавить вкладки в MainFrame
      * 7. Добиться отображения объектов в таблице по запросам
      * 8. Тестирование замены тайлового менеджера
      * 9. Замутить красивый градиент в зависимости от rssi и придумать как им управлять с GUI
      * 10. Нагрузочное тестирование */
}

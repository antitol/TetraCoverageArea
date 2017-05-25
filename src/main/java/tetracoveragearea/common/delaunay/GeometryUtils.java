package tetracoveragearea.common.delaunay;

/**
 * Приложения для работы с пространственными данными
 * Created by anatoliy on 15.03.17.
 */
public class GeometryUtils {

    /**
     * Находит расстояние между точками по их координатам в метрической системе
     * Формула гаверсинусов
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371.0088; // miles (or 6371.0088 kilometers)
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return dist;
    }

    /**
     * Находит расстояние между точками в метрах
     * @param point1
     * @param point2
     * @return
     */
    public static double getDistance(Point point1, Point point2) {
        return getDistance(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    }
}

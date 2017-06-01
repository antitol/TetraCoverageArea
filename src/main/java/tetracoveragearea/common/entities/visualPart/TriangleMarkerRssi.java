package tetracoveragearea.common.entities.visualPart;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.MapPosition;
import processing.core.PConstants;
import processing.core.PGraphics;
import tetracoveragearea.common.delaunay.Point;
import tetracoveragearea.common.delaunay.Triangle;
import tetracoveragearea.gui.panels.settingsPanels.gradient.GradientTableModel;

import java.awt.*;
import java.util.List;

/**
 * Created by anatoliy on 17.02.17.
 */
public class TriangleMarkerRssi extends PolygonMarkerRssi {

    private Triangle triangle;
    private Location centerPosition;

    public TriangleMarkerRssi(List<Location> locations, List<Double> rssiValues) {
        super(locations, rssiValues);
    }

    public TriangleMarkerRssi(Triangle triangle) {
        super(triangle.getRingPoints());
        this.triangle = triangle;
        Point centerPoint = triangle.getCentroid();
        centerPosition = new Location(centerPoint.getX(), centerPoint.getY());
    }

    /**
     * Среднее Rssi трех точек
     * @return
     */
    public double getAvgRssi() {
        return getRssiValues().stream().distinct().mapToDouble(Double::doubleValue).average().getAsDouble();
    }

    public Triangle getTriangle() {
        return triangle;
    }

    @Override
    public void draw(PGraphics pg, List<MapPosition> mapPositions) {

        if (mapPositions.isEmpty() || isHidden())
            return;

        openGLDraw(pg, mapPositions);

    }

    public void awtDraw(PGraphics pg, List<MapPosition> mapPositions) {

        int[] xArray = mapPositions.stream().mapToInt(mapPosition -> (int) mapPosition.x).limit(3).toArray();
        int[] yArray = mapPositions.stream().mapToInt(mapPosition -> (int) mapPosition.y).limit(3).toArray();

        Polygon polygon = new Polygon(xArray, yArray, 3);

        Rectangle r = polygon.getBounds();

        for (int i = 0; i <= r.width; i++) {
            for (int j = 0; j <= r.height; j++) {

                int ix = r.x + i;
                int iy = r.y + j;
                if (polygon.contains(ix, iy)) {

                    double[] lengths = new double[3];
                    double sum = 0;
                    for (int k = 0; k < 3; k++) {
                        lengths[k] = Math.sqrt(Math.pow(ix - xArray[k], 2) + Math.pow(iy - yArray[k], 2));
                        sum += lengths[k];
                    }

                    int rssiInPoint = (int) (rssiValues.get(0) * lengths[0]/sum +
                            rssiValues.get(1) * lengths[1]/sum +
                            rssiValues.get(2) * lengths[2]/sum);

                    pg.set(ix, iy,
                            GradientTableModel.getInstance().getMultilayerGradient().getColor(rssiInPoint).getRGB());
                }
            }
        }

        pg.pushStyle();
        pg.colorMode(PConstants.RGB, 1);


    }

    public void openGLDraw(PGraphics pg, List<MapPosition> mapPositions) {

        double lastRssi = 0;

        pg.pushStyle();
        pg.colorMode(PConstants.RGB, 1);

        // Без рамки
        pg.noStroke();
        /*pg.stroke(0x000000);
        pg.strokeWeight(1);*/
//        pg.stroke(0,0,0, 127);
        pg.beginShape();

        for (int i = 0; i < mapPositions.size(); i++) {

            // Определяем вес точки по уровню сигнала

            /*if (i > 0) {
                Double halfRssi = (getRssiValues().get(i) + lastRssi) / 2f;
                mapPositions.get(i - 1).add(mapPositions.get(i));
                mapPositions.get(i - 1).div(2);

                pg.fill(GradientTableModel.getInstance().getMultilayerGradient().getColor(halfRssi).getRGB(), 0.5f);
                pg.vertex(mapPositions.get(i - 1).x, mapPositions.get(i - 1).y);

            }*/

            // Заполняем цветом
            pg.fill(GradientTableModel.getInstance().getMultilayerGradient().getColor(getRssiValues().get(i)).getRGB(), 0.5f);

            pg.vertex(mapPositions.get(i).x, mapPositions.get(i).y);



            lastRssi = getRssiValues().get(i);
        }

        pg.endShape(PConstants.CLOSE);
        pg.popStyle();
    }

    public int areaTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        return (int)(0.5*Math.abs((x1-x3)*(y2-y1)-(x1-x2)*(y3-y1)));
    }
}

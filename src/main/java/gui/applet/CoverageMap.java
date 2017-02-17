package gui.applet;

import common.entities.visualPart.PointMarkerRssi;
import common.entities.visualPart.PolygonMarkerRssi;
import common.entities.visualPart.TriangleMarkerRssi;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import processing.core.PApplet;

import java.util.List;

/**
 * Created by anatoliy on 17.02.17.
 */
public class CoverageMap extends UnfoldingMap {

    private List<PointMarkerRssi> points;
    private List<TriangleMarkerRssi> delauneyTriangles;
    private List<PolygonMarkerRssi> voronoiPolygons;


    public CoverageMap(PApplet pApplet, AbstractMapProvider abstractMapProvider) {
        super(pApplet, abstractMapProvider);
    }

    public void setMapProvider(AbstractMapProvider provider) {
        this.mapDisplay.setMapProvider(provider);
    }

    public void addPoints() {
        try {
            points.forEach(p -> this.addMarkers(p));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addDelauneyTriangles() {
        try {
            delauneyTriangles.forEach(p -> this.addMarkers(p));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addVoronoiPolygons() {
        try {
        voronoiPolygons.forEach(p -> this.addMarkers(p));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeMarkers(List<? extends Marker> list) {
        list.forEach(getDefaultMarkerManager()::removeMarker);
    }

    public void removePoints() {
        removeMarkers(points);
    }

    public void removeDelauneyTriangles() {
        removeMarkers(delauneyTriangles);
    }

    public void removeVoronoiPolygons() {
        removeMarkers(voronoiPolygons);
    }

    public List<PointMarkerRssi> getPoints() {
        return points;
    }

    public List<TriangleMarkerRssi> getDelauneyTriangles() {
        return delauneyTriangles;
    }

    public List<PolygonMarkerRssi> getVoronoiPolygons() {
        return voronoiPolygons;
    }

    public void setPoints(List<PointMarkerRssi> points) {
        this.points = points;
    }

    public void setDelauneyTriangles(List<TriangleMarkerRssi> delauneyTriangles) {
        this.delauneyTriangles = delauneyTriangles;
    }

    public void setVoronoiPolygons(List<PolygonMarkerRssi> voronoiPolygons) {
        this.voronoiPolygons = voronoiPolygons;
    }
}


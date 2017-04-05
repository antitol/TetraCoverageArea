package tetracoveragearea.gui.panels.filterPanels;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * Библиотечный класс фильтр-данных
 * Created by anatoliy on 03.04.17.
 */
public class Filter {

    private static Optional<LocalDateTime> startTime = Optional.empty();
    private static Optional<LocalDateTime> endTime = Optional.empty();
    private static OptionalDouble minLat = OptionalDouble.empty();
    private static OptionalDouble minLong = OptionalDouble.empty();
    private static OptionalDouble minRssi = OptionalDouble.empty();
    private static OptionalDouble maxLat = OptionalDouble.empty();
    private static OptionalDouble maxLong = OptionalDouble.empty();
    private static OptionalDouble maxRssi = OptionalDouble.empty();

    public static Optional<LocalDateTime> getStartTime() {
        return startTime;
    }

    public static Optional<LocalDateTime> getEndTime() {
        return endTime;
    }

    public static OptionalDouble getMinLat() {
        return minLat;
    }

    public static OptionalDouble getMinLong() {
        return minLong;
    }

    public static OptionalDouble getMinRssi() {
        return minRssi;
    }

    public static OptionalDouble getMaxLat() {
        return maxLat;
    }

    public static OptionalDouble getMaxLong() {
        return maxLong;
    }

    public static OptionalDouble getMaxRssi() {
        return maxRssi;
    }

    public static void setStartTime(Optional<LocalDateTime> startTime) {
        Filter.startTime = startTime;
    }

    public static void setEndTime(Optional<LocalDateTime> endTime) {
        Filter.endTime = endTime;
    }

    public static void setMinLat(OptionalDouble minLat) {
        Filter.minLat = minLat;
    }

    public static void setMinLong(OptionalDouble minLong) {
        Filter.minLong = minLong;
    }

    public static void setMinRssi(OptionalDouble minRssi) {
        Filter.minRssi = minRssi;
    }

    public static void setMaxLat(OptionalDouble maxLat) {
        Filter.maxLat = maxLat;
    }

    public static void setMaxLong(OptionalDouble maxLong) {
        Filter.maxLong = maxLong;
    }

    public static void setMaxRssi(OptionalDouble maxRssi) {
        Filter.maxRssi = maxRssi;
    }
}

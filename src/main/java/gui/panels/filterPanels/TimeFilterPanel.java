package gui.panels.filterPanels;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.jidesoft.swing.RangeSlider;
import gui.applet.MapApplet;
import net.miginfocom.swing.MigLayout;
import serialDao.SerialTestDao;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

/**
 * Created by anatoliy on 02.03.17.
 */
public class TimeFilterPanel extends JPanel {

    private final JLabel timedateFilterLabel = new JLabel("Фильтр данных по дате");

    private JLabel beginTimedateLabel;
    private JLabel endTimedateLabel;
    private JLabel countPointsLabel = new JLabel("Количество точек: 0");

    private DatePicker beginDatePicker = new DatePicker();
    private DatePicker endDatePicker = new DatePicker();

    private TimePicker beginTimePicker;
    private TimePicker endTimePicker;

    private LocalDateTime earliestPointDateTime;
    private LocalDateTime latestPointDateTime;

    private RangeSlider timeSlider = new RangeSlider();

    private JButton confirmButton;
    private JButton resetButton;

    private boolean userSetted = false;

    public TimeFilterPanel() {

        setLayout(new MigLayout());

        setName("Время");

        beginTimedateLabel = new JLabel("Начальная дата: ");
        endTimedateLabel = new JLabel("Конечная дата: ");


        beginTimePicker = new TimePicker(getTimePickerSettings());
        endTimePicker = new TimePicker(getTimePickerSettings());

        // Добавляем слушателей по изменению времени
        Arrays.asList(beginDatePicker, endDatePicker)
                .forEach(datePicker -> {
                    datePicker.setSettings(getDatePickerSettings());
                    datePicker.addDateChangeListener(event -> timedateChanged());
                });

        Arrays.asList(beginTimePicker, endTimePicker)
                .forEach(timePicker -> {

                    timePicker.addTimeChangeListener(event -> timedateChanged());
                });

        timeSlider.setMinimum(0);
        // Диапазон слайдера - разница в секундах между моментами появления первой и последней точки в таблице

        timeSlider.addChangeListener(e -> {
            try {
                setBeginDateTime(earliestPointDateTime.plusSeconds(timeSlider.getLowValue()));
                setEndDateTime(earliestPointDateTime.plusSeconds(timeSlider.getHighValue()));
            } catch (NullPointerException ex) {
                System.err.println("time request error");
            }

        });

        countPointsLabel = new JLabel("");

        confirmButton = new JButton("Применить");
        confirmButton.addActionListener(e -> {

            MapApplet.getMap().setPoints(
                    SerialTestDao.getInstance().getPointsByDateTime(
                            getBeginLocalDateTime(), getEndLocalDateTime()
                    )
            );

            MapApplet.getMap().setDelaunayTriangles(
                    SerialTestDao.getInstance().getDelaunayByDateTime(
                            getBeginLocalDateTime(), getEndLocalDateTime()
                    )
            );

        });

        resetButton = new JButton("Сбросить");
        resetButton.addActionListener(e -> {
            setDefaultDateTimes();

            MapApplet.getMap().setPoints(
                    SerialTestDao.getInstance().getPoints()
            );

            MapApplet.getMap().setDelaunayTriangles(
                    SerialTestDao.getInstance().getDelaunayTriangles()
            );
        });

        List<JComponent> components = Arrays.asList(
                timedateFilterLabel, beginTimedateLabel,
                beginDatePicker, beginTimePicker,
                endTimedateLabel, endDatePicker, endTimePicker, timeSlider,
                countPointsLabel
        );

        components.forEach(c -> add(c, "span 2, w 100%, wrap"));

        add(confirmButton, "center");
        add(resetButton, "center, wrap");
    }

    /**
     * Метод вызывается при изменении времени на компонентах, устанавливается метка с числом точек в выбранном отрезке времени
     */
    public void timedateChanged() {

        countPointsLabel.setText("Количество точек: " +
                SerialTestDao.getInstance().getCountPoints(
                        getBeginLocalDateTime(), getEndLocalDateTime()
                )
        );
    }

    /**
     * Возвращает объект дата-время начала отрезка времени (установленное на компонентах в данных момент)
     * @return
     */
    public LocalDateTime getBeginLocalDateTime() {


        while (true) {

            try {

                return LocalDateTime.of(
                        beginDatePicker.getDate(), beginTimePicker.getTime()
                );

            } catch (NullPointerException ex) {
                setDefaultDateTimes();
            }
        }
    }

    /**
     * Возвращает объект дата-время конца отрезка времени
     * @return
     */
    public LocalDateTime getEndLocalDateTime() {

        while (true) {
            try {

                return LocalDateTime.of(
                        endDatePicker.getDate(), endTimePicker.getTime()
                );

            } catch (NullPointerException ex) {
                setDefaultDateTimes();
            }
        }
    }

    public void setDefaultDateTimes() {

        earliestPointDateTime = SerialTestDao.getInstance().getDateTimeOfEarliestPoint();
        latestPointDateTime = SerialTestDao.getInstance().getDateTimeOfLatestPoint().plusSeconds(1);

        setDateTimes(earliestPointDateTime, latestPointDateTime);

        timeSlider.setMaximum(
                (int) (latestPointDateTime.atZone(ZoneId.systemDefault()).toEpochSecond() -
                        earliestPointDateTime.atZone(ZoneId.systemDefault()).toEpochSecond())
        );

        timeSlider.setLowValue(timeSlider.getMinimum());
        timeSlider.setHighValue(timeSlider.getMaximum());
    }

    public void setDateTimes(LocalDateTime beginDateTime, LocalDateTime endDateTime) {

        setBeginDateTime(beginDateTime);
        setEndDateTime(endDateTime);
    }

    public void setBeginDateTime(LocalDateTime beginDateTime) {

        beginDatePicker.setDate(beginDateTime.toLocalDate());
        beginTimePicker.setTime(beginDateTime.toLocalTime());
    }

    public void setEndDateTime(LocalDateTime endDateTime) {

        endDatePicker.setDate(endDateTime.toLocalDate());
        // Плюс секунда нужна для корректного поиска числа всех точек, т.к. поиск в базе ведется невключительно
        endTimePicker.setTime(endDateTime.toLocalTime());
    }

    private TimePickerSettings getTimePickerSettings() {

        TimePickerSettings tps = new TimePickerSettings();
        tps.setDisplaySpinnerButtons(true);
        tps.setFormatForDisplayTime("HH:mm:ss");
        tps.setFormatForMenuTimes("HH:mm:ss");
        return tps;
    }

    private DatePickerSettings getDatePickerSettings() {

        DatePickerSettings dps = new DatePickerSettings();
        return dps;
    }
}

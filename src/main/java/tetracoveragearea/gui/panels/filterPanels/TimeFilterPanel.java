package tetracoveragearea.gui.panels.filterPanels;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.jidesoft.swing.RangeSlider;
import tetracoveragearea.common.entities.centralPart.GeometryStore;
import tetracoveragearea.gui.panels.primitives.SubPanel;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by anatoliy on 02.03.17.
 */
public class TimeFilterPanel extends SubPanel {

    private final JLabel timedateFilterLabel = new JLabel("Фильтр данных по дате");

    private JLabel beginTimedateLabel;
    private JLabel endTimedateLabel;

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

        confirmButton = new JButton("Применить");
        confirmButton.addActionListener(e -> {

            Filter.setStartTime(
                    Optional.of(getBeginLocalDateTime())
            );

            Filter.setEndTime(
                    Optional.of(getEndLocalDateTime())
            );

            GeometryStore.getInstance().filter();

            userSetted = true;

        });

        resetButton = new JButton("Сбросить");
        resetButton.addActionListener(e -> {

            Filter.setStartTime(Optional.empty());
            Filter.setEndTime(Optional.empty());
            GeometryStore.getInstance().filter();
            setDefaultDateTimes();

            userSetted = false;
        });

        List<JComponent> components = Arrays.asList(
                timedateFilterLabel, beginTimedateLabel,
                beginDatePicker, beginTimePicker,
                endTimedateLabel, endDatePicker, endTimePicker, timeSlider
        );

        components.forEach(c -> add(c, "span 2, w 100%, wrap"));

        add(confirmButton, "center");
        add(resetButton, "center, wrap");

        setDefaultDateTimes();
    }

    /**
     * Метод вызывается при изменении времени на компонентах, устанавливается метка с числом точек в выбранном отрезке времени
     */
    public void timedateChanged() {}

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

    /**
     * Устанавливает в компоненты дату / время, согласно времени добавления данных в таблицу
     */
    public void setDefaultDateTimes() {

        earliestPointDateTime = GeometryStore.getInstance().getDateTimeOfEarliestPoint();
        latestPointDateTime = GeometryStore.getInstance().getDateTimeOfLatestPoint().plusSeconds(1);

        setDateTimes(earliestPointDateTime, latestPointDateTime);

        timeSlider.setMaximum(
                (int) (latestPointDateTime.atZone(ZoneId.systemDefault()).toEpochSecond() -
                        earliestPointDateTime.atZone(ZoneId.systemDefault()).toEpochSecond())
        );

        timeSlider.setLowValue(timeSlider.getMinimum());
        timeSlider.setHighValue(timeSlider.getMaximum());
    }

    /**
     * Устанавливает в компоненты дату / время
     * @param beginDateTime
     * @param endDateTime
     */
    public void setDateTimes(LocalDateTime beginDateTime, LocalDateTime endDateTime) {

        setBeginDateTime(beginDateTime);
        setEndDateTime(endDateTime);
    }

    /**
     * Устанавливает в компоненты начальные дату / время
     * @param beginDateTime
     */
    public void setBeginDateTime(LocalDateTime beginDateTime) {

        beginDatePicker.setDate(beginDateTime.toLocalDate());
        beginTimePicker.setTime(beginDateTime.toLocalTime());
    }

    /**
     * Устанавливает в компоненты конечные дату / время
     * @param endDateTime
     */
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

    public boolean isUserSetted() {
        return userSetted;
    }

    @Override
    public void onInvoke() {
        if (!userSetted) {
            setDefaultDateTimes();
        }
    }
}

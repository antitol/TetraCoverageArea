package tetracoveragearea.gui.panels.settingsPanels.gradient;

import org.apache.log4j.Logger;
import tetracoveragearea.gui.tools.MultilayerGradient;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель таблицы выбора профиля градиента
 * Created by anatoliy on 29.03.17.
 */
public class ChooseGradientTableModel extends AbstractTableModel {

    public static final Logger log = Logger.getLogger(ChooseGradientTableModel.class);

    private List<MultilayerGradient> gradientList =  new ArrayList<MultilayerGradient>();
    private List<GradientLabel> gradientLabels = new ArrayList<GradientLabel>();
    private List<String> namesList = new ArrayList<String>();

    public ChooseGradientTableModel() {

        try {
            FileInputStream fileInputStream = new FileInputStream(
                    getClass().getClassLoader().getResource("assets/gradientProfiles").getFile()
            );

            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            setGradientList((List<MultilayerGradient>) objectInputStream.readObject());

            fileInputStream.close();
            objectInputStream.close();

        } catch (FileNotFoundException ex) {
            log.info("Не получилось загрузить файл профилей");
        } catch (IOException ex) {
            log.info("Ошибка ввода-вывода при попытке загрузить файл профилей");
        } catch (ClassNotFoundException ex) {
            log.info("Не найден класс десериализуемого объекта");
        }

    }

    public static ChooseGradientTableModel instance = new ChooseGradientTableModel();

    public static ChooseGradientTableModel getInstance() {
        return instance;
    }

    @Override
    public int getRowCount() {
        return gradientList.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        switch (columnIndex) {
            case 0:
                return gradientLabels.get(rowIndex);
            case 1:
                return gradientList.get(rowIndex).getMinPart() + " - " + gradientList.get(rowIndex).getMaxPart();
            case 2:
                return gradientList.get(rowIndex).getName();

            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Расцветка";
            case 1:
                return "Диапазон";
            case 2:
                return "Имя";

            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return ImageIcon.class;
        } else {
            return String.class;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 2) {
            gradientList.get(rowIndex).setName(aValue.toString());
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 2;
    }

    public List<MultilayerGradient> getGradientList() {
        return gradientList;
    }

    public void setGradientList(List<MultilayerGradient> gradientList) {

        for (MultilayerGradient multilayerGradient : gradientList) {
            addMultiGradient(multilayerGradient);
        }
    }

    /**
     * Добавление профиля градиента в таблицу
     * Уникальный ключ профиля - его имя
     * Профиль с именем, существующим в списке заменит существуюший профиль
     * @param multilayerGradient
     */
    public void addMultiGradient(MultilayerGradient multilayerGradient) {


        for (int i = 0; i < gradientList.size(); i++) {


            if (multilayerGradient.getName().equals(gradientList.get(i).getName())) {

                gradientList.set(i, multilayerGradient);
                gradientLabels.set(i, new GradientLabel(multilayerGradient));
                return;
            }
        }
        gradientList.add(multilayerGradient);
        gradientLabels.add(new GradientLabel(multilayerGradient));
        fireTableDataChanged();
    }

    /**
     * Удаление профиля мультиградиента
     * @param index
     */
    public void removeMultiGradient(int index) {

        gradientList.remove(index);
        gradientLabels.remove(index);
    }
}

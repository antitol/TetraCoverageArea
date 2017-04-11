package tetracoveragearea.gui.panels.databasePanels;

import tetracoveragearea.common.entities.centralPart.GeometryStore;
import tetracoveragearea.gui.components.GuiComponents;
import tetracoveragearea.gui.panels.primitives.PrimaryPanel;
import tetracoveragearea.serialDao.SerialTestDao;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by anatoliy on 15.03.17.
 */
public class DatabasePanel extends PrimaryPanel {

    private final String DB_ERROR = "Ошибка соединения с базой данных";

    private JLabel dbHostLabel = new JLabel("Хост");
    private JLabel dbPortLabel = new JLabel("Порт");
    private JLabel dbNameLabel = new JLabel("База");
    private JLabel dbLoginLabel = new JLabel("Имя");
    private JLabel dbPasswordLabel = new JLabel("Пароль");

    private JTextField dbHostField = new JTextField("localhost");
    private JTextField dbPortField = new JTextField("5432");
    private JTextField dbNameField = new JTextField("postgis");
    private JTextField dbLoginField = new JTextField("postgis");
    private JTextField dbPasswordField= new JTextField("postgis");

    private JToggleButton connectionButton = new JToggleButton("Подключение");
    private JButton loadData = new JButton("Загрузить данные");
    private JToggleButton syncronizeWithDB = new JToggleButton("Синхронизировать БД");

    public DatabasePanel() {

        connectionButton.setUI(GuiComponents.getToggleButtonGreenUI());

        connectionButton.addActionListener(e -> {

            if (connectionButton.isSelected()) {

                if (tryDatabaseConnect()) {

                    connectionButton.setText("Отключение");
                    loadData.setEnabled(true);
                    syncronizeWithDB.setEnabled(true);
                } else {
                    onDatabaseError();
                }

            } else {
                SerialTestDao.getInstance().closeConnection();
                connectionButton.setText("Подключение");
                loadData.setEnabled(false);

                if (syncronizeWithDB.isSelected()) {
                    syncronizeWithDB.doClick();
                }
                syncronizeWithDB.setEnabled(false);
            }
        });

        loadData.setEnabled(false);
        loadData.addActionListener(e -> {

            GeometryStore.getInstance().setPoints(SerialTestDao.getInstance().getPoints());
        });

        syncronizeWithDB.setUI(GuiComponents.getToggleButtonGreenUI());
        syncronizeWithDB.setEnabled(false);
        syncronizeWithDB.addActionListener(e -> {
            if (syncronizeWithDB.isSelected()) {
                GeometryStore.getInstance().addGeometryListener(SerialTestDao.getInstance());
            } else {
                GeometryStore.getInstance().removeGeometryListener(SerialTestDao.getInstance());
            }
        });

        List<JComponent> components = Arrays.asList(
                dbHostLabel, dbHostField, dbPortLabel, dbPortField,
                dbNameLabel, dbNameField, dbLoginLabel, dbLoginField,
                dbPasswordLabel, dbPasswordField, connectionButton, loadData, syncronizeWithDB
        );


        components.forEach(component -> add(component, "wrap, w 100%"));


    }

    public boolean tryDatabaseConnect() {

        try {
            SerialTestDao.getInstance().createConnection(
                    dbHostField.getText(),
                    dbPortField.getText(),
                    dbNameField.getText(),
                    dbLoginField.getText(),
                    dbPasswordField.getText()
            );

            return true;

        } catch (Exception ex) {

            return false;
        }
    }

    public void onDatabaseError() {

        JOptionPane.showMessageDialog(this, DB_ERROR, "Ошибка", JOptionPane.ERROR_MESSAGE);

        if (syncronizeWithDB.isSelected()) {
            syncronizeWithDB.doClick();
        }

        if (connectionButton.isSelected()) {
            connectionButton.doClick();
        }
    }

    @Override
    public void onInvoke() {

    }

    @Override
    public void onRevoke() {

    }
}

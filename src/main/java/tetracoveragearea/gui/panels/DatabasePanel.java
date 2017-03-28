package tetracoveragearea.gui.panels;

import net.miginfocom.swing.MigLayout;
import tetracoveragearea.common.entities.centralPart.GeometryStore;
import tetracoveragearea.gui.components.GuiComponents;
import tetracoveragearea.serialDao.SerialTestDao;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by anatoliy on 15.03.17.
 */
public class DatabasePanel extends JPanel {

    private final String DB_ERROR = "Не удалось подключиться к базе данных";

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

    public DatabasePanel() {

        setLayout(new MigLayout("debug"));

        setPreferredSize(new Dimension(265, 400));

        connectionButton.setUI(GuiComponents.getToggleButtonGreenUI());

        connectionButton.addActionListener(e -> {

            if (connectionButton.isSelected()) {

                if (tryDatabaseConnect()) {

                    connectionButton.setText("Отключение");
                    loadData.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(this, DB_ERROR, "Ошибка", JOptionPane.ERROR_MESSAGE);
                    connectionButton.setSelected(false);
                }

            } else {
                SerialTestDao.getInstance().closeConnection();
                connectionButton.setText("Подключение");
                loadData.setEnabled(false);
            }
        });

        loadData.setEnabled(false);
        loadData.addActionListener(e -> {

            GeometryStore.getInstance().setPoints(SerialTestDao.getInstance().getPoints());
        });

        List<JComponent> components = Arrays.asList(
                dbHostLabel, dbHostField, dbPortLabel, dbPortField,
                dbNameLabel, dbNameField, dbLoginLabel, dbLoginField,
                dbPasswordLabel, dbPasswordField, connectionButton, loadData
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
}

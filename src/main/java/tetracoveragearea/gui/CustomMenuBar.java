package tetracoveragearea.gui;

import javax.swing.*;

/**
 * Created by anatoliy on 14.03.17.
 */
public class CustomMenuBar extends JMenuBar {

    private JMenu fileMenu, viewMenu, devicesMenu, filterMenu;



    public CustomMenuBar() {

        super();

        fileMenu = new JMenu("Файл");

        JMenu loadItem = new JMenu("Загрузить");
        JMenu saveItem = new JMenu("Сохранить");

        JMenuItem loadDB = new JMenuItem("База данных");
        JMenuItem loadAnother = new JMenuItem("Другой источник");
        JMenuItem saveDB = new JMenuItem("База данных");
        JMenuItem saveAnother = new JMenuItem("Другой источник");

        JMenuItem settings = new JMenuItem("Настройки");

        loadDB.addActionListener(e -> {}/* Здесь проверяется доступность бд, если не доступна то выдаст диалоговое окно */ );
        loadAnother.addActionListener(e -> {} /* Окно выбора файла */);
        saveDB.addActionListener(e -> {} /* Выгрузка точек в базу */);
        saveAnother.addActionListener(e -> {} /* Окно выбора файла */);
        settings.addActionListener(e -> {} /* Вызвать диалоговое окно настроек */);

        loadItem.add(loadDB);
        loadItem.add(loadAnother);

        saveItem.add(saveDB);
        saveItem.add(saveAnother);

        fileMenu.add(loadItem);
        fileMenu.add(saveItem);
        fileMenu.add(settings);

        viewMenu = new JMenu("Вид");

        /* Здесь добавить отображение панелей (отображать / нет) */

        devicesMenu = new JMenu("Устройства");

        JMenuItem connectDevice = new JMenuItem("Подключить");

        devicesMenu.add(connectDevice);

        filterMenu = new JMenu("Фильтры");

        JMenuItem filters = new JMenuItem("Настроить");

        filterMenu.add(filters);

        add(fileMenu);
        add(viewMenu);
        add(devicesMenu);
        add(filterMenu);

        revalidate();
    }
}

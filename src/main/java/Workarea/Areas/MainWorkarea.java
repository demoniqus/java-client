package Workarea.Areas;

import Interfaces.IClearContainer;
import Interfaces.IEventDispatcher;
import Interfaces.IEventModel;
import Interfaces.IMainWorkarea;
import app.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.net.URL;


import org.jabricks.widgets.renderers.FloatRenderer;
import org.jabricks.widgets.treetable.*;



public class MainWorkarea extends AbstractArea implements IMainWorkarea {

    private JPanel mainPanel;


    private JPanel menuPanel;
    private JPanel mainWorkPanel;
    private JMenuBar mainMenu;
    private JMenu userMenu;
    private JPanel dataPanel;
    private JPanel filtersPanel;
    private JPanel headersPanel;
    private JPanel footerPanel;
//    private JPanel menuPanel;
    private Container container;
    @Override
    public Boolean open(Container container) {
        IClearContainer.clearContainer(container);
        this.container = container;
        container.add(mainPanel, BorderLayout.CENTER);
        /*
        Конфигурируем рабочее пространство в соответствии с выбранным сервером данных -
        создаем меню, рабочие панели и пр.
         */
        Main.DataServer().configureWorkarea(this);

//        JLabel l = new JLabel("243254355465756");
//        mainWorkPanel.add(l, BorderLayout.CENTER);
//        createTreeTable(mainWorkPanel);

//        mainPanel.setBackground(Color.decode("0xbbffbb"));

        return true;
    }

    @Override
    public void close() {
        this.container.remove(mainPanel);
    }

    private JPanel getMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        return panel;
    }

    private JMenuBar createMainMenu() {
        JMenuBar menuBar = new JMenuBar();

//        JMenu registriesMenu = new JMenu("Реестры");
//
//        JMenu settingsMenu = new JMenu("Настройки");
//
//        menuBar.add(registriesMenu);
//        menuBar.add(settingsMenu);



        return menuBar;
    }

    private JMenuBar createUserMenu(){
        JMenuBar menuBar = new JMenuBar();
        JMenu userMenu = new JMenu();

        URL iconURL = getClass().getClassLoader().getResource("main/resources/images/UserIcon_35.png");
        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(iconURL);
            Image image = icon.getImage();
            Image image2 = image.getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(image2);

            userMenu.setIcon(icon);
        }
        else {
            userMenu.setText("Пользователь");
        }


        menuBar.add(userMenu);

        JMenuItem logout = new JMenuItem(
                "Выйти",
                new ImageIcon("images/LogoutIcon_35.png")
        );
        userMenu.add(logout);

        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((IEventDispatcher) Main.ServiceContainer().GetService(IEventDispatcher.class)).fire(IEventModel.USER_LOGOUT_EVENT, logout, null);
            }
        });

        return menuBar;
    }

    private JPanel createMainWorkPanel()
    {
        JPanel mainGridPanel = new JPanel();
        mainGridPanel.setLayout(new BorderLayout());
        mainGridPanel.setBackground(Color.decode("0xffeeee"));




        return mainGridPanel;
    }

    @Override
    protected void init() {

        super.init();
        mainPanel = getMainPanel();

        menuPanel = createMenuPanel();
        mainPanel.add(menuPanel, BorderLayout.NORTH);


        mainMenu = createMainMenu();
        menuPanel.add(mainMenu, BorderLayout.WEST);

        JMenuBar userMenu = createUserMenu();
        menuPanel.add(userMenu, BorderLayout.EAST);

//        menuPanel.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));

        mainWorkPanel = createMainWorkPanel();
        mainPanel.add(mainWorkPanel, BorderLayout.CENTER);

    }

    @Override
    public JMenuBar getMenuContainer() {
        return mainMenu;
    }

    @Override
    public JPanel getWorkPanel() {
        return mainWorkPanel;
    }
}
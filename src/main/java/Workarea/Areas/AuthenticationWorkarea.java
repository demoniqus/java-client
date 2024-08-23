package Workarea.Areas;

import Interfaces.*;
import app.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;


public class AuthenticationWorkarea extends AbstractArea {

    private JPanel mainPanel;
    private JPanel componentsPanel;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JComboBox<String> dataServerField;
    private Container container;
    private JLabel stateLabel;
    private IEventListener lockAuthForm = new IEventListener() {
        @Override
        public <T> T handleEvent(IEvent event) {
            //TODO Реализовать визуальную блокировку формы авторизации
            return null;
        }
    };

    List<IDataServer> dataServers;

    @Override
    public Boolean open(Container container) {
        IClearContainer.clearContainer(container);

        this.container = container;


        container.add(mainPanel, BorderLayout.CENTER);

        IEventDispatcher dispatcher = (IEventDispatcher) Main.ServiceContainer().GetService(IEventDispatcher.class);
        dispatcher.subscribe(IEventModel.LOCK_AUTHENTICATION_FORM, lockAuthForm );

        return true;
    }

    @Override
    public void close() {
        this.container.remove(mainPanel);
        IEventDispatcher dispatcher = (IEventDispatcher) Main.ServiceContainer().GetService(IEventDispatcher.class);
        dispatcher.unsubscribe(IEventModel.LOCK_AUTHENTICATION_FORM, lockAuthForm );

    }


    private JPanel getMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
//        panel.setLayout(new GridBagLayout());

        return panel;
    }

    private JPanel getComponentsPanel() {
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new GridBagLayout());

        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new GridBagLayout());
        Dimension d = new Dimension();
        d.setSize(400, 200);
        innerPanel.setPreferredSize(d);
        innerPanel.setMaximumSize(d);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        //constraints.weightx = 1.0f;

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        outerPanel.add(innerPanel, constraints);
        mainPanel.add(outerPanel, BorderLayout.CENTER);


        return innerPanel;
    }


    @Override
    protected void init() {
        super.init();

        mainPanel = getMainPanel();
        componentsPanel = getComponentsPanel();


        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;

        constraints.insets = new Insets(5, 5, 5, 5);



        createDataServersField(constraints);

        createLoginField(constraints);
        createPasswordField(constraints);

        stateLabel = new JLabel();
        stateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        stateLabel.setVisible(false);
        mainPanel.add(stateLabel, BorderLayout.SOUTH);

        JButton loginButton = new JButton("Авторизоваться");

        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.CENTER;
        componentsPanel.add(loginButton, constraints);

        loginButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        //TODO Расписать реакцию на ошибки - неправильный ввод, отказ выбранного сервера в аутентификации и пр.
                        // также надо ставить disable|enable для компонентов, пока выполняется операция авторизации
                        if (dataServerField.getSelectedIndex() < 0)
                        {
                            stateLabel.setText("Выберите сервер данных");
                            //TODO В Main можно сохранить исходный стиль шрифта и его использовать при необходимости как базовый для всех форм
                            Font f = UIManager.getFont("Label.font");
                            Font labelFont =  f.deriveFont(Font.BOLD);

                            stateLabel.setFont(labelFont);
                            stateLabel.setVisible(true);
                            stateLabel.setForeground(Color.red);
                            mainPanel.revalidate();
                            mainPanel.repaint();
                            return;
                        }

                        if (loginField.getText().isEmpty()) {
                            stateLabel.setText("Укажите логин");
                            //TODO В Main можно сохранить исходный стиль шрифта и его использовать при необходимости как базовый для всех форм
                            Font f = UIManager.getFont("Label.font");
                            Font labelFont =  f.deriveFont(Font.BOLD);

                            stateLabel.setFont(labelFont);
                            stateLabel.setVisible(true);
                            stateLabel.setForeground(Color.red);
                            mainPanel.revalidate();
                            mainPanel.repaint();
                            return;
                        }
                        if (passwordField.getPassword().length == 0) {
                            stateLabel.setText("Укажите пароль");
                            //TODO В Main можно сохранить исходный стиль шрифта и его использовать при необходимости как базовый для всех форм
                            Font f = UIManager.getFont("Label.font");
                            Font labelFont =  f.deriveFont(Font.BOLD);

                            stateLabel.setFont(labelFont);
                            stateLabel.setVisible(true);
                            stateLabel.setForeground(Color.red);
                            mainPanel.revalidate();
                            mainPanel.repaint();
                            return;
                        }

                        IDataServer dataServer = dataServers.get(dataServerField.getSelectedIndex());
                        if (
                                dataServer.getAuthenticationManager().authenticate(
                                        loginField.getText(),
                                        new String(passwordField.getPassword()),
                                        Main.User(),
                                        dataServer
                                )
                        ) {
                            IEventDispatcher dispatcher = (IEventDispatcher) Main.ServiceContainer().GetService(IEventDispatcher.class);
                            HashMap<String, Object> params = new HashMap<>();
                            params.put(IDataServer.class.toString(), dataServer);
                            dispatcher.fire(IEventModel.USER_LOGIN_EVENT, this, params);
                        }
                    }
                }
        );

        IEventDispatcher dispatcher = (IEventDispatcher) Main.ServiceContainer().GetService(IEventDispatcher.class);
        dispatcher.subscribe(IEventModel.APP_LOADING_COMPLETED_EVENT, new IEventListener() {
            @Override
            public <T> T handleEvent(IEvent event) {
                DefaultComboBoxModel<String> dcbm = new DefaultComboBoxModel<String>();
                dataServers = ((IDataServerManager)Main.ServiceContainer().GetService(IDataServerManager.class)).getServers();
                for (IDataServer dataServer : dataServers) {
                    dcbm.addElement(dataServer.Title());
                }

                if (!dataServers.isEmpty()) {
                    dcbm.setSelectedItem(dataServers.getFirst().Title());
                }

                dataServerField.setModel(dcbm);
                return null;
            }
        });

    }

    private void createDataServersField(GridBagConstraints constraints)
    {
        JLabel label = new JLabel("Сервер данных");

        dataServerField = new JComboBox<String>();

        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;

        constraints.insets = new Insets(5, 5, 5, 5);
        componentsPanel.add(label, constraints);

        constraints.gridx = 1;
        componentsPanel.add(dataServerField, constraints);

        Dimension d = new Dimension();
        //TODO Для окна сделать минимально допустимые размеры, чтобы помещалась форма авторизации
        d.setSize( 200, 25);
        dataServerField.setPreferredSize(d);

        d.setSize( 100, 25);
        dataServerField.setMinimumSize(d);

        d.setSize( 250, 25);
        dataServerField.setMaximumSize(d);


    }

    private void createLoginField(GridBagConstraints constraints)
    {
        constraints.gridy = 1;

        JLabel label = new JLabel("Логин");
        loginField = new JTextField();
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.EAST;
        componentsPanel.add(label, constraints);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 1;
        componentsPanel.add(loginField, constraints);

        loginField.setText("test");
    }

    private void createPasswordField(GridBagConstraints constraints)
    {
        constraints.gridy =2 ;

        JLabel label = new JLabel("Пароль");
        passwordField = new JPasswordField();
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.EAST;
        componentsPanel.add(label, constraints);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 1;
        componentsPanel.add(passwordField, constraints);

        passwordField.setText("test");
    }
}

package Workarea.Areas;

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
        clearContainer(container);
        this.container = container;
        container.add(mainPanel, BorderLayout.CENTER);
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

    private void createTreeTable(JPanel mainGridPanel){



        //org.jabricks.widgets.treetable.ObjectModel.setSignificantFields("name", "leaf");
        String[] titles = {"Id", "Name", "Number"};
        String[] names = {"id", "name", "number"};
        Class<?>[] types = {TreeTableModel.class, String.class, String.class/*, Integer.class*/};
        int        cols_width[]  = {350, 110, 120};
        JTreeTable treeTable = new JTreeTable(new ObjectModel(names, titles, types));

        treeTable.setColumnsWidth(cols_width);
        treeTable.setRowHeight(22);

        treeTable.setDefaultRenderer(Float.class, new FloatRenderer());
        treeTable.setAutoResizeColumn (JTable.AUTO_RESIZE_OFF);



        //mainGridPanel.add(treeTable, BorderLayout.CENTER);
//        JLabel l = new JLabel("swfdefdgsfdsfs");;
        JScrollPane jsp = new JScrollPane(treeTable);
        jsp.setBackground(Color.decode("0xff5555"));

        mainGridPanel.add(jsp, BorderLayout.CENTER);
        treeTable.drawTableHeaderRaised();
        treeTable.updateUI();

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
class ObjectModel extends AbstractTreeTableModel
{
    private String[] column_titles = null;
    private String[] column_names = null;
    private Class<?>[] column_types = null;
    public ObjectModel(){
        super((Object) null);
    }
    public ObjectModel(
        String[] col_names,
        String[] col_titles,
        Class<?>[] col_types
    )
    {
        super((Object) null);

        this.column_titles = col_titles;
        this.column_names = col_names;
        this.column_types = col_types;

    }

    public void setColumnTitles(String[] col_titles) {

        this.column_titles = col_titles;
    }

    public String getColumnTitle(int column) {

        return this.column_titles[column];
    }

    public void setRootNode(ObjectNode root) {
        this.root = root;
    }

    public int getColumnCount() {

        return this.column_titles == null ? 0 : this.column_names.length;
    }

    public String getColumnName(int column) {

        return this.column_titles == null ? null : this.column_titles[column];
    }

    public Class<?> getColumnClass(int column) {

        return this.column_types[column];
    }

    public boolean isLeaf(Object node) {
        return ((ObjectNode)node).isLeaf();
    }

    private Object getValue(String fldName, Object object) {
        Object value = null;

        try {
            Field field = object.getClass().getDeclaredField(fldName);
            field.setAccessible(true);
            value = field.get(object);
        } catch (IllegalAccessException | NoSuchFieldException var5) {
        }

        return value;
    }

    public Object getValueAt(Object node, int column) {
        if (this.column_names == null) {
            return null;
        } else {
            Object obj = this.getValue(this.column_names[column], ((ObjectNode)node).getRecord());
            return obj;
        }
    }

    public Object getChild(Object node, int i) {
        return this.getChildren(node)[i];
    }

    private Object[] getChildren(Object node) {
        return ((ObjectNode)node).getChildren();
    }

    public int getChildCount(Object node) {
        Object[] children = this.getChildren(node);
        return children == null ? 0 : children.length;
    }

    private static void convertRecord2Node(ObjectNode node) {
        ObjectRecord record = (ObjectRecord)node.getRecord();
        if (record.getChildren() != null) {
            node.setChildren(new ObjectNode[record.getChildren().size()]);
            if (record.getChildren().size() > 0) {
                for(int i = 0; i < record.getChildren().size(); ++i) {
                    ObjectRecord child = (ObjectRecord)record.getChildren().get(i);
                    ObjectNode obj_node = new ObjectNode(child);
                    obj_node.setChildren(new ObjectNode[0]);
                    obj_node.setParent(node);
                    node.getChildren()[i] = obj_node;
                    if (child.getChildren().size() > 0) {
                        obj_node.setChildren(new ObjectNode[child.getChildren().size()]);

                        for(int j = 0; j < child.getChildren().size(); ++j) {
                            ObjectRecord descendant = (ObjectRecord)child.getChildren().get(j);
                            ObjectNode onode = new ObjectNode(descendant);
                            onode.setChildren(new ObjectNode[0]);
                            onode.setParent(obj_node);
                            obj_node.getChildren()[j] = onode;
                            convertRecord2Node(onode);
                        }
                    }
                }
            }
        }

    }

    public static void setSignificantFields(String fieldName, String fieldLeaf) {
        ObjectNode.setSignificantFields(fieldName, fieldLeaf);
    }

    public static ObjectNode convertRecord2Node(ObjectRecord record) {
        ObjectNode main = new ObjectNode(record);
        main.setChildren(new ObjectNode[record.getChildren().size()]);

        for(int i = 0; i < record.getChildren().size(); ++i) {
            ObjectNode obj_node = new ObjectNode(record.getChildren().get(i));
            obj_node.setChildren(new ObjectNode[0]);
            obj_node.setParent(main);
            main.getChildren()[i] = obj_node;
            convertRecord2Node(obj_node);
        }

        return main;
    }
}
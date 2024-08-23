package Workarea.Areas;

import Interfaces.IClearContainer;

import javax.swing.*;
import java.awt.*;
import java.util.Dictionary;


public class TestMainWorkarea extends AbstractArea {

    private JPanel mainPanel;

    private JPanel menuPanel;
    private JPanel mainGridPanel;
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

        mainPanel = getMainPanel();

        container.add(mainPanel, BorderLayout.CENTER);

        JPanel menuPanel = getMenuPanel();
        mainPanel.add(menuPanel, BorderLayout.NORTH);


        JMenuBar mainMenu = getMainMenu();
        menuPanel.add(mainMenu, BorderLayout.WEST);

        JMenuBar userMenu = getUserMenu();
        menuPanel.add(userMenu, BorderLayout.EAST);

        menuPanel.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));

        JPanel mainGridPanel = getMainGridPanel();
        mainPanel.add(mainGridPanel, BorderLayout.CENTER);




        mainPanel.setBackground(Color.decode("0xbbffbb"));

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

    private JPanel getMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        return panel;
    }

    private JMenuBar getMainMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu registry = new JMenu("Реестры");

        JMenu settings = new JMenu("Настройки");

        menuBar.add(registry);
        menuBar.add(settings);



        return menuBar;
    }

    private JMenuBar getUserMenu(){
        JMenuBar menuBar = new JMenuBar();
        JMenu userMenu = new JMenu("user");

        menuBar.add(userMenu);

        return menuBar;
    }

    private JPanel getMainGridPanel()
    {
        JPanel mainGridPanel = new JPanel();
        mainGridPanel.setLayout(new GridBagLayout());
        mainGridPanel.setBackground(Color.decode("0xffeeee"));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        //constraints.weightx = 1.0f;

        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
//        constraints.gridheight = 1;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        JPanel headersPanel = getGridHeadersPanel();
        mainGridPanel.add(headersPanel, constraints);

        constraints.gridy = 1;
        JPanel filterPanel = getGridFiltersPanel();
        mainGridPanel.add(filterPanel, constraints);

//        constraints.weighty = 1.0f;
        constraints.gridy = 2;
        JPanel dataPanel = getGridDataPanel();
        mainGridPanel.add(dataPanel, constraints);
//        constraints.weighty = 0.0f;

        constraints.gridy = 3;
        JPanel footerPanel = getGridFooterPanel();
        mainGridPanel.add(footerPanel, constraints);




        return mainGridPanel;
    }

    private JPanel getGridHeadersPanel()
    {
        JPanel headersPanel = new JPanel();
        headersPanel.setLayout(new GridBagLayout());
        headersPanel.setBackground(Color.decode("0xbbffbb"));

        headersPanel.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;


        addCell1(headersPanel, constraints);
        addCell2(headersPanel, constraints);
        addCell3(headersPanel, constraints);
        addCell4(headersPanel, constraints);
        addCell5(headersPanel, constraints);
        addCell6(headersPanel, constraints);
        addCell7(headersPanel, constraints);
        addCell8(headersPanel, constraints);
        addCell9(headersPanel, constraints);
        addCell10(headersPanel, constraints);
        addCell11(headersPanel, constraints);



        return headersPanel;

    }

    private void addCell1(JPanel headersPanel, GridBagConstraints constraints)
    {
        JPanel pCell = new JPanel();
        pCell.setLayout(new BorderLayout());
        JLabel cellCaption = new JLabel("", JLabel.CENTER);
        cellCaption.setText("A1:A2");
        pCell.add(cellCaption, BorderLayout.CENTER);
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 2;
        headersPanel.add(pCell, constraints);
        pCell.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));
        cellCaption.setAlignmentX(Component.CENTER_ALIGNMENT);
        cellCaption.setAlignmentY(Component.CENTER_ALIGNMENT);
        cellCaption.setBackground(Color.decode("0x99ffff"));
        cellCaption.setBorder(BorderFactory.createLineBorder(Color.BLUE));

    }

    private void addCell2(JPanel headersPanel, GridBagConstraints constraints)
    {
        JPanel pCell = new JPanel();
        pCell.setLayout(new BorderLayout());
        JLabel cellCaption = new JLabel();
        cellCaption.setText("B1:C2");
        pCell.add(cellCaption, BorderLayout.CENTER);
        constraints.gridy = 0;
        constraints.gridx = 1;
        constraints.gridwidth = 2;
        constraints.gridheight = 2;
        headersPanel.add(pCell, constraints);
        pCell.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));
    }

    private void addCell3(JPanel headersPanel, GridBagConstraints constraints)
    {
        JPanel pCell = new JPanel();
        pCell.setLayout(new BorderLayout());
        JLabel cellCaption = new JLabel();
        cellCaption.setText("D1:F1");
        pCell.add(cellCaption, BorderLayout.CENTER);
        constraints.gridy = 0;
        constraints.gridx = 3;
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        headersPanel.add(pCell, constraints);
        pCell.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));
    }

    private void addCell4(JPanel headersPanel, GridBagConstraints constraints)
    {
        JPanel pCell = new JPanel();
        pCell.setLayout(new BorderLayout());
        JLabel cellCaption = new JLabel();
        cellCaption.setText("D2:E2");
        pCell.add(cellCaption, BorderLayout.CENTER);
        constraints.gridy = 1;
        constraints.gridx = 3;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        headersPanel.add(pCell, constraints);
        pCell.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));
    }

    private void addCell5(JPanel headersPanel, GridBagConstraints constraints)
    {
        JPanel pCell = new JPanel();
        pCell.setLayout(new BorderLayout());
        JLabel cellCaption = new JLabel();
        cellCaption.setText("F1");
        pCell.add(cellCaption, BorderLayout.CENTER);
        constraints.gridy = 1;
        constraints.gridx = 5;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        headersPanel.add(pCell, constraints);
        pCell.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));
    }

    private void addCell6(JPanel headersPanel, GridBagConstraints constraints)
    {
        JPanel pCell = new JPanel();
        pCell.setLayout(new BorderLayout());
        JLabel cellCaption = new JLabel();
        cellCaption.setText("A3");
        pCell.add(cellCaption, BorderLayout.CENTER);
        constraints.gridy = 2;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        headersPanel.add(pCell, constraints);
        pCell.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));
    }


    private void addCell7(JPanel headersPanel, GridBagConstraints constraints)
    {
        JPanel pCell = new JPanel();
        pCell.setLayout(new BorderLayout());
        JLabel cellCaption = new JLabel();
        cellCaption.setText("B3");
        pCell.add(cellCaption, BorderLayout.CENTER);
        constraints.gridy = 2;
        constraints.gridx = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        headersPanel.add(pCell, constraints);
        pCell.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));
    }


    private void addCell8(JPanel headersPanel, GridBagConstraints constraints)
    {
        JPanel pCell = new JPanel();
        pCell.setLayout(new BorderLayout());
        JLabel cellCaption = new JLabel();
        cellCaption.setText("C3");
        pCell.add(cellCaption, BorderLayout.CENTER);
        constraints.gridy = 2;
        constraints.gridx = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        headersPanel.add(pCell, constraints);
        pCell.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));
    }


    private void addCell9(JPanel headersPanel, GridBagConstraints constraints)
    {
        JPanel pCell = new JPanel();
        pCell.setLayout(new BorderLayout());
        JLabel cellCaption = new JLabel();
        cellCaption.setText("D3");
        pCell.add(cellCaption, BorderLayout.CENTER);
        constraints.gridy = 2;
        constraints.gridx = 3;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        headersPanel.add(pCell, constraints);
        pCell.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));
    }


    private void addCell10(JPanel headersPanel, GridBagConstraints constraints)
    {
        JPanel pCell = new JPanel();
        pCell.setLayout(new BorderLayout());
        JLabel cellCaption = new JLabel();
        cellCaption.setText("E3");
        pCell.add(cellCaption, BorderLayout.CENTER);
        constraints.gridy = 2;
        constraints.gridx = 4;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        headersPanel.add(pCell, constraints);
        pCell.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));
    }


    private void addCell11(JPanel headersPanel, GridBagConstraints constraints)
    {
        JPanel pCell = new JPanel();
        pCell.setLayout(new BorderLayout());
        JLabel cellCaption = new JLabel();
        cellCaption.setText("F3");
        pCell.add(cellCaption, BorderLayout.CENTER);
        constraints.gridy = 2;
        constraints.gridx = 5;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        headersPanel.add(pCell, constraints);
        pCell.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));

    }

    private JPanel addDataRow(Dictionary<String, String> itemData, JPanel dataPanel)
    {
        /**
         * Нужно создать словарь ширин ячеек и устанавливать для каждой ячейки-панели
         * pCell.setPreferredSize();
         */
        JPanel pRow = new JPanel();
        JPanel pCell = null;
        JLabel cellCaption = null;

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridy = 0;

        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;


        pCell = new JPanel();
        pCell.setLayout(new BorderLayout());
        cellCaption = new JLabel();
        cellCaption.setText(itemData.get("A"));
        pCell.add(cellCaption, BorderLayout.CENTER);
        pRow.add(pCell, constraints);
        constraints.gridx = 0;
        pCell.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));



        pCell = new JPanel();
        pCell.setLayout(new BorderLayout());
        cellCaption = new JLabel();
        cellCaption.setText(itemData.get("B"));
        pCell.add(cellCaption, BorderLayout.CENTER);
        pRow.add(pCell, constraints);
        constraints.gridx = 1;
        pCell.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));

        pCell = new JPanel();
        pCell.setLayout(new BorderLayout());
        cellCaption = new JLabel();
        cellCaption.setText(itemData.get("C"));
        pCell.add(cellCaption, BorderLayout.CENTER);
        pRow.add(pCell, constraints);
        constraints.gridx = 2;
        pCell.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));

        pCell = new JPanel();
        pCell.setLayout(new BorderLayout());
        cellCaption = new JLabel();
        cellCaption.setText(itemData.get("D"));
        pCell.add(cellCaption, BorderLayout.CENTER);
        pRow.add(pCell, constraints);
        constraints.gridx = 3;
        pCell.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));

        pCell = new JPanel();
        pCell.setLayout(new BorderLayout());
        cellCaption = new JLabel();
        cellCaption.setText(itemData.get("E"));
        pCell.add(cellCaption, BorderLayout.CENTER);
        pRow.add(pCell, constraints);
        constraints.gridx = 4;
        pCell.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));

        pCell = new JPanel();
        pCell.setLayout(new BorderLayout());
        cellCaption = new JLabel();
        cellCaption.setText(itemData.get("F"));
        pCell.add(cellCaption, BorderLayout.CENTER);
        pRow.add(pCell, constraints);
        constraints.gridx = 5;
        pCell.setBorder(BorderFactory.createLineBorder(Color.MAGENTA));


        return pRow;

    }




    private JPanel getGridFooterPanel()
    {
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        footerPanel.setBackground(Color.decode("0xbbbbff"));

        return footerPanel;

    }

    private JPanel getGridFiltersPanel()
    {
        JPanel filtersPanel = new JPanel();
        filtersPanel.setLayout(new GridBagLayout());
        filtersPanel.setBackground(Color.decode("0xffffbb"));

        return filtersPanel;
    }

    private JPanel getGridDataPanel()
    {
        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new GridBagLayout());

        dataPanel.setBackground(Color.decode("0xbbffff"));

        return dataPanel;
    }

    @Override
    protected void init() {
        super.init();
    }
}

package Data.DataServer.Servers;

import Data.DataSource;
import Interfaces.IAuthenticationManager;
import Interfaces.IDataServer;
import Interfaces.IDataSource;
import Interfaces.IMainWorkarea;
import app.Main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


abstract public class AbstractDataServer implements IDataServer {
    protected IAuthenticationManager authenticationManager;
    final private String name;
    final private String title;
    final private String host;
    final private String scheme;
//    private IMainWorkarea mainWorkarea = null;
    protected List<IDataSource> dataSources = new ArrayList<>();

//    private HashMap<String, IRegistry> registries = new HashMap<>();

    public AbstractDataServer(Map<String, Object> config) {
        //TODO validate on null
        this.name = config.get("name").toString();
        this.title = config.get("title").toString();
        String[] path = config.get("url").toString().toLowerCase().split("://");
        this.host = path[path.length - 1];

        this.scheme = path.length == 2 ?
            path[0]:
            "http";
    }


    @Override
    public String Name() {
        return name;
    }

    public String Title()
    {
        return title;
    }

    public String Host() { return host; }

    public String Scheme() { return scheme; }

    public IAuthenticationManager getAuthenticationManager()
    {
        return authenticationManager;
    }


    protected void createMenuItems(JMenu menu, List<IDataSource> dataSources, IMainWorkarea mainWorkarea)
    {
        JMenuItem menuItem;
        for (IDataSource dataSource : dataSources) {
            menuItem = new JMenuItem();

            if (dataSource.getIcon() != null) {
                menuItem.setIcon(dataSource.getIcon());
            }

            if (dataSource.getTitle() != null) {
                menuItem.setText(dataSource.getTitle());
            }

            menu.add(menuItem);

            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JPanel p = mainWorkarea.getWorkPanel();
//                    JLabel l = new JLabel("243254355465756");
//                    p.setBackground(Color.decode("0xff99ff"));
//                    p.add(l, BorderLayout.CENTER);
                    dataSource.load(p);
                    p.revalidate();
                    p.repaint();

                }
            });
        }

    }
    public void configureWorkarea(IMainWorkarea mainWorkarea) {
        createMainMenu(mainWorkarea);
    }

    abstract protected void createMainMenu(IMainWorkarea mainWorkarea);

//    @Override
//    public void setWorkarea(IMainWorkarea mainWorkarea) {
//        this.mainWorkarea = mainWorkarea;
//    }

    @Override
    public void init() {
        List<Map<String, Object>> dataSourcesConfig = Main.ServiceContainer().getParameter("dataSources");

        for (Map<String, Object> dataSourceConfig : dataSourcesConfig) {
            String serverName =
                dataSourceConfig.containsKey("serverName") ?
                    (String) dataSourceConfig.get("serverName") :
                    null;

            String serverClass =
                dataSourceConfig.containsKey("serverClass") ?
                    (String) dataSourceConfig.get("serverClass") :
                    null;
            if (
                (serverName == null ? true : serverName.trim().equals(this.name)) &&
                (serverClass == null ? true : serverClass.trim().equals(getClass().getName()))
            )
            {
                dataSources.add(new DataSource(dataSourceConfig));
            }
        }

    }

//    private void initRegistries(){
//        registries.clear();
//
//        IRegistry registry = new IRegistry() {
//            private String title = "Бюджет";
//            private String action = "budgetRegistry";
//            private String name = "budgetRegistry";
//            @Override
//            public String Title() {
//                return title;
//            }
//
//            @Override
//            public String Action() {
//                return action;
//            }
//            @Override
//            public String Name() {
//                return name;
//            }
//            @Override
//            public ImageIcon Icon() {
//                return null;
//            }
//        };
//
//        registries.put(registry.Name(), registry);
//
//        registry = new IRegistry() {
//            private String title = "Реестр смет";
//            private String action = "estimateRegistry";
//            private String name = "estimateRegistry";
//            @Override
//            public String Title() {
//                return title;
//            }
//
//            @Override
//            public String Action() {
//                return action;
//            }
//            @Override
//            public String Name() {
//                return name;
//            }
//            @Override
//            public ImageIcon Icon() {
//                return null;
//            }
//        };
//
//        registries.put(registry.Name(), registry);
//
//        registry = new IRegistry() {
//            private String title = "Выполнение заказчика";
//            private String action = "bdrCustomerRegistry";
//            private String name = "bdrCustomerRegistry";
//            @Override
//            public String Title() {
//                return title;
//            }
//
//            @Override
//            public String Action() {
//                return action;
//            }
//            @Override
//            public String Name() {
//                return name;
//            }
//            @Override
//            public ImageIcon Icon() {
//                return null;
//            }
//        };
//
//        registries.put(registry.Name(), registry);
//    }


}

//interface IRegistry
//{
//    public String Name();
//    public String Title();
//    public String Action();
//    public ImageIcon Icon();
//}

package Data.DataServer.Servers;

import AuthenticationManager.ContAuthenticationManager;
import Interfaces.IAuthenticationManager;
import Interfaces.IContDataServer;
import Interfaces.IMainWorkarea;
import app.Main;

import javax.swing.*;
import java.util.Map;


final public class ContDataServer extends AbstractDataServer implements IContDataServer {
//    private HashMap<String, IRegistry> registries = new HashMap<>();

    public ContDataServer(Map<String, Object> config) {
        super(config);
        authenticationManager = (IAuthenticationManager) Main.ServiceContainer().GetService(ContAuthenticationManager.class);
//        initRegistries();
    }

    protected void createMainMenu(IMainWorkarea mainWorkarea) {
        JMenuBar mainMenuBar = mainWorkarea.getMenuContainer();
        mainMenuBar.removeAll();

        JMenu menuRegistries = new JMenu("Реестры");
        mainMenuBar.add(menuRegistries);

        createMenuItems(menuRegistries, dataSources, mainWorkarea);

    }




}


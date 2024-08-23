package Data.DataServer;

import Interfaces.IDataServer;
import Interfaces.IDataServerManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DataServerManager implements IDataServerManager {
    private Boolean locked = false;
    private HashMap<String, IDataServer> dataServers = new HashMap<>();
    private IDataServer activeDataServer = null;

    public DataServerManager(List<Map<String, Object>> serversConfig) throws RuntimeException {
        Map<String, IDataServer> dict = new HashMap<>();
        for (Map<String, Object> serverConfig : serversConfig) {
            String serverName = (String) serverConfig.get("name");
            String className = (String) serverConfig.get("class");
            String key = serverName + "|" + className;
            if (dict.containsKey(key)) {
                throw new RuntimeException("Duplicate data server: " + key);
            }
            try {

                Class<?> classDefinition = Class.forName(className);
                Constructor<?> constructor = classDefinition.getDeclaredConstructor(new Class[] {Map.class});
                IDataServer dataServer = (IDataServer) constructor.newInstance(new Object[] {serverConfig});
                dataServers.put(serverName, dataServer);
                dict.put(key, dataServer);
            } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                     InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void lock()
    {
        locked = true;
    }

    public IDataServerManager addServer(String alias, IDataServer dataServer)
    {
        if (locked) {
            throwException();
        }
        dataServers.put(alias, dataServer);

        return this;
    }

    public IDataServer getServer(String alias)
    {
        return dataServers.get(alias);
    }
    public IDataServerManager setActiveServer(IDataServer dataServer)
    {
        activeDataServer = dataServer;

        return this;
    }

    public IDataServer getActiveServer(String alias)
    {
        return activeDataServer;
    }

    public List<IDataServer> getServers()
    {
        return dataServers.values().stream().toList();
    }





}

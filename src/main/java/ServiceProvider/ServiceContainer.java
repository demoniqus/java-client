package ServiceProvider;

import Interfaces.IContainer;
import Interfaces.ILocked;

import java.util.*;

public class ServiceContainer implements IContainer, ILocked {
    private static IContainer instance;
    private Map<String, Object> servicesByAlias = new HashMap<>();
    private Map<Class<?>, Object> servicesByClass = new HashMap<>();
    private HashMap<String, Object> parameters = new HashMap<>();
//    private Map<String, T> parameters = new HashMap<String, T>();
    private Boolean locked = false;

    @Override
    public Object GetService(String alias) {
        return servicesByAlias.get(alias);
    }
    @Override
    public Object GetService(Class<?> className) {
        return servicesByClass.get(className);
    }

    @Override
    public <T> T getParameter(String paramName) {
        if (this.parameters.containsKey(paramName)) {
            return (T) this.parameters.get(paramName);
        }
        return null;
    }
    @Override
    public <T> T getParameter(List<String> path) {
        if (path.isEmpty()) {
            //TODO may be exception?
            return null;
        }

        Map<String, Object> p = parameters;
        int l = path.size() - 1;
        for (int i = 0; i < path.size(); i++) {
            String key = path.get(i);
            if (
                    p.containsKey(key) &&
                    p instanceof Map<String, Object>
            ) {
                p = (Map<String, Object>)p.get(key);
            }
            else {
                //TODO need Exception
                return null;
            }
        }
        if (p.containsKey(path.get(l))) {
            return (T)p.get(path.get(l));
        }
        return null;
    }

    @Override
    public <T> T getParameter(String paramName, Character splitter) {
        T result = getParameter(paramName);
        if (result == null) {
            List<String> path = Arrays.stream(paramName.split(splitter.toString())).toList();
            result = getParameter(path);
        }
        return result;
    }

    public <T> void  setParameter(String paramName, T value) {
        throwIfLocked();
        this.parameters.put(paramName, value);
    }

    public IContainer AddService(String alias, Object service) {
        throwIfLocked();
        servicesByAlias.put(alias, service);
        return this;
    }
    public IContainer AddService(Class<?> className, Object service) {
        throwIfLocked();
        servicesByClass.put(className, service);
        return this;
    }

    public void lock()
    {
        locked = true;
    }

    private void throwIfLocked() {
        if (locked) {
            throwException();
        }
    }

    public static IContainer instantiate()
    {
        if (ServiceContainer.instance == null) {
            instance = new ServiceContainer();
        }

        return instance;
    }


}

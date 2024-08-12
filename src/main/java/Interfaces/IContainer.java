package Interfaces;

import java.util.List;

public interface IContainer {
    public Object GetService(String alias);
    public Object GetService(Class<?> className);

    public <T> T getParameter(String paramName);
    public <T> T getParameter(String paramName, Character splitter);
    public <T> T getParameter(List<String> path);
}

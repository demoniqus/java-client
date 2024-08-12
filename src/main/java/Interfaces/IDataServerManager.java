package Interfaces;

import java.util.List;

public interface IDataServerManager {
    public IDataServerManager addServer(String alias, IDataServer dataServer);
    public IDataServer getServer(String alias);
    public void lock();
    public IDataServer getActiveServer(String alias);
    public IDataServerManager setActiveServer(IDataServer dataServer);
    public List<IDataServer> getServers();
}

package Interfaces;

import java.util.List;

public interface IDataServerManager extends ILocked{
    IDataServerManager addServer(String alias, IDataServer dataServer);
    IDataServer getServer(String alias);
    IDataServer getActiveServer(String alias);
    IDataServerManager setActiveServer(IDataServer dataServer);
    List<IDataServer> getServers();
}

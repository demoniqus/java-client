package Interfaces;

public interface IAuthenticationManager {
    boolean authenticate(String username, String password, IUser user, IDataServer dataServer);
    void authenticateRequest();

}

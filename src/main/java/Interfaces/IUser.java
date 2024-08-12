package Interfaces;

public interface IUser {
    boolean isAuthenticated();
    IAuthentication getAuthentication();
    void setAuthentication(IAuthentication authentication);
    void logout();
    int getId();
    String getName();
}

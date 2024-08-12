package Interfaces;

import jdk.jfr.Description;
import jdk.jshell.spi.ExecutionControl;

import java.util.Map;

public interface IAuthentication {
    @Description("Авторизация пользователя")
    boolean authenticate(String username, String password);

    @Description("Восстановление завершенного сеанса авторизации пользователя")
    boolean restore() throws ExecutionControl.NotImplementedException;

    boolean isAuthenticated();

    <T> IAuthentication setAuthData(String key, T value);
    <T> T getAuthData(String key);
    Map<String, Object> getAuthData();
}

package AuthenticationManager.Authentication;

import Interfaces.IAuthentication;
import Interfaces.IAuthenticationManager;
import Interfaces.IUser;
import jdk.jshell.spi.ExecutionControl;

import java.util.HashMap;
import java.util.Map;

public class ContAuthentication implements IAuthentication {
    private String username;
    private String password;
    private IAuthenticationManager authenticationManager;
    private boolean _isAuthenticated = false;
    private IUser user;
    private Map<String, Object> authData = new HashMap<>();

    public ContAuthentication(
            IAuthenticationManager authenticationManager,
            IUser user,
            String username,
            String password
            ) {
        this.authenticationManager = authenticationManager;
        this.user = user;
        this.username = username;;
        this.password = password;
    }

    public boolean authenticate(String username, String password) {
        if (authenticationManager.authenticate(username, password, this.user, null)) {
            _isAuthenticated = true;
            this.username = username;
            this.password = password;
            return true;
        }
        this._isAuthenticated = false;
        this.username = null;
        this.password = null;
        return false;
    }

    public boolean isAuthenticated() {
        return _isAuthenticated;
    }

    public boolean restore() throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Method not implemented");
    }

    public <T> IAuthentication setAuthData(String key, T value) {
        authData.put(key, value);
        return this;
    }

    public <T> T getAuthData(String key) {
        return (T) authData.get(key);
    }

    public Map<String, Object> getAuthData() { return authData; }
}

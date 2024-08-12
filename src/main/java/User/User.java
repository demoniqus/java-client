package User;

import Interfaces.*;
import app.Main;

public class User implements IUser {
    private IAuthentication authentication;
    private int id;
    private String name;

    public User()
    {
        init();
    }

    public IAuthentication getAuthentication() {
        return authentication;
    }

    public boolean isAuthenticated() {
        return authentication.isAuthenticated();
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void logout(){
        init();
        ((IEventDispatcher)Main.ServiceContainer().GetService(IEventDispatcher.class)).<User>fire(IEventModel.USER_LOGOUT_EVENT, this, null);
    }

    private void init(){
        authentication = null;
        id = -1;
        name = null;
    }

    public void setAuthentication(IAuthentication authentication) {
        this.authentication = authentication;
    }


}

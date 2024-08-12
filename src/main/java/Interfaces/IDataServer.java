package Interfaces;

public interface IDataServer {
    String Name();
    String Title();
    String Host();
    String Scheme();
    IAuthenticationManager getAuthenticationManager();
//    void createMainMenu(IMainWorkarea mainWorkarea);
//    void setWorkarea(IMainWorkarea mainWorkarea);
    void configureWorkarea(IMainWorkarea mainWorkarea);
    void init();
    //void getDataModel();
    //void getData();

}

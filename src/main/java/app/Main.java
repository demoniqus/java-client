package app;
import AdaptersManager.AdaptersManager;
import AuthenticationManager.ContAuthenticationManager;
import Data.Adapter.JabricsTreeGridDataAdapter;
import Data.Model.Managers.TestDataModelsManager;
import Data.DataServer.DataServerManager;
import Data.Visualizer.TreeGridDataVisualizer;
import Data.Model.Adapters.JabricsTreeGridModelAdapter;
import EventDispatcher.EventDispatcher;
import Interfaces.*;
import ServiceProvider.ServiceContainer;
import ConfigParser.*;
import User.User;
import Workarea.WorkareaManager;
import Workarea.Areas.*;
import jdk.jfr.Description;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Map;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
/*
Правила именования методов
lock, adapt - со строчной называется метод-действие, основным назначением которого является выполнение какого-либо действия.
    Например, lock - заблокировать объект от изменений, adapt - выполнить адаптацию данных, после чего вернуть результат
Field, Fields - с прописной называется метод, основным назначением которого является доступ к непереназначаемому объекту
    или набору объектов (нет публичного setter'а)
getTitle, setTitle - Getter + Setter - основное назначение метода - доступ и переназначение объекта
 */
public class Main {
    /**
     * В приложении работает конкретный пользователь.
     * Работу пользователь осуществляет в рамках одного сервера.
     * При этом доступ из приложения потенциально возможен к нескольким серверам (prod, test, debug, kis, pmc и пр.)
     * Поэтому в app доступны ServiceContainer, User, MainFrame (основное рабочее окно), DataServer
     */
    private static IContainer serviceContainer = new ServiceContainer();
    private static JFrame mainFrame = new JFrame();
    private static IUser user;


    private static IDataServer dataServer;
    public static void main(String[] args) {
        registerParameters();
        createMainFrame();
        injectDependencies();
        registerEvents();
        createUser();

        System.setProperty("javax.net.debug", "all");

        IEventDispatcher dispatcher = (IEventDispatcher) serviceContainer.GetService(IEventDispatcher.class);
        dispatcher.fire(IEventModel.APP_LOADING_COMPLETED_EVENT, null, null);
    }

    public static IContainer ServiceContainer() {
        return serviceContainer;
    }

    public static JFrame MainFrame(){
        return mainFrame;
    }

    public static IUser User()
    {
        return user;
    }

    public static IDataServer DataServer() {
        return dataServer;
    }


    private static void injectDependencies()
    {
        registerServices();
        registerServiceComponents();

        ((ILocked)serviceContainer).lock();
    }

    private static void registerParameters()
    {
        URL configURL = serviceContainer.getClass().getClassLoader().getResource("main/resources/configs/config.yml");
        if (configURL == null) {
            configURL = serviceContainer.getClass().getClassLoader().getResource("main/resources/configs/config.yaml");
        }

        if (configURL == null) {
            return;
        }

        IConfigParserFactory configParserFactory = new ConfigParserFactory();
        configParserFactory.Parser(IConfigParserModel.YAML).parse(configURL, serviceContainer);
    }

    @Description("Регистрация сервисов типа менеджеров и т.п.")
    private static void registerServices()
    {
        List<Map<String, Object>> dataServersConfig = serviceContainer.getParameter("dataServers");
        //Менеджер рабочих пространств
        ((ServiceContainer)serviceContainer).AddService(IWorkareaManager.class, new WorkareaManager());
        //Диспетчер событий
        ((ServiceContainer)serviceContainer).AddService(IEventDispatcher.class, new EventDispatcher());
        //Менеджер данных. TODO Заменить на реальный менеджер данных
        ((ServiceContainer)serviceContainer).AddService(IDataModelManager.class, new TestDataModelsManager());
        //Менеджер аутентификации на серверах Cont
        ((ServiceContainer)serviceContainer).AddService(ContAuthenticationManager.class, new ContAuthenticationManager());
        //Менеджер серверов данных
        ((ServiceContainer)serviceContainer).AddService(IDataServerManager.class, new DataServerManager(dataServersConfig));
        //Визуализатор древесных данных
        ((ServiceContainer)serviceContainer).AddService(ITreeGridDataVisualizer.class, new TreeGridDataVisualizer());
        //Менеджер адаптеров
        ((ServiceContainer)serviceContainer).AddService(IAdaptersManager.class, new AdaptersManager());



    }


    @Description("Регистрация компонентов сервисов, например Workareas для WorkareaManager  или DataServers для DataServerManager")
    private static void registerServiceComponents()
    {
        registerWorkareas();
        registerDataServers();
        registerAdapters();

    }
    @Description("Регистрация рабочих пространств")
    private static void registerWorkareas()
    {
        ((ServiceContainer)serviceContainer).AddService(MainWorkarea.class, new MainWorkarea());
        ((ServiceContainer)serviceContainer).AddService(AuthenticationWorkarea.class, new AuthenticationWorkarea());

    }
    @Description("Регистрация источников данных")
    private static void registerDataServers(){
        //TODO Переделать сюда загрузку из конфига ???
        IDataServerManager dataServerManager = (IDataServerManager) serviceContainer.GetService(IDataServerManager.class);
//        IDataServer server = new DebugContDataServer();
//        dataServerManager.addServer(server.getName(), server);
//
//        server = new TestContDataServer();
//        dataServerManager.addServer(server.getName(), server);
//
//        server = new ContProdDataServer();
//        dataServerManager.addServer(server.getName(), server);
//
        dataServerManager.lock();
    }
    @Description("Регистрация источников данных")
    private static void registerAdapters(){
        //TODO Переделать сюда загрузку из конфига ???
        AdaptersManager adaptersManager = (AdaptersManager) serviceContainer.GetService(IAdaptersManager.class);
        //Jabrics-компоненты отображения данных
        adaptersManager.addAdapter(
                IDataModelAdapter.class,
                TreeGridDataVisualizer.class,
                new JabricsTreeGridModelAdapter()
        );
        adaptersManager.addAdapter(
                IDataAdapter.class,
                TreeGridDataVisualizer.class,
                new JabricsTreeGridDataAdapter()
        );

//        IDataServer server = new DebugContDataServer();
//        dataServerManager.addServer(server.getName(), server);
//
//        server = new TestContDataServer();
//        dataServerManager.addServer(server.getName(), server);
//
//        server = new ContProdDataServer();
//        dataServerManager.addServer(server.getName(), server);
//
        adaptersManager.lock();
    }



    @Description("Регистрация базовых событий")
    private static void registerEvents(){
        IEventDispatcher dispatcher = (IEventDispatcher) serviceContainer.GetService(IEventDispatcher.class);

        dispatcher.subscribe(IEventModel.USER_LOGIN_EVENT, new IEventListener() {
            @Override
            public <T> T handleEvent(IEvent event) {
                /**
                 * Успешно аутентифицировались на выбранном сервере данных. Теперь запомним и инициируем этот сервер
                 */
                dataServer = event.getParam(IDataServer.class.toString());
                mainFrame.setTitle(dataServer.Title());
                dataServer.init();
                return null;
            }
        });

        dispatcher.subscribe(IEventModel.USER_LOGIN_EVENT, new IEventListener() {
            @Override
            public <T> T handleEvent(IEvent event) {
                /**
                 * После успешной аутентификации открываем основное рабочее пространство
                 */
                IWorkareaManager workareaManager = (IWorkareaManager) Main.ServiceContainer().GetService(IWorkareaManager.class);
                IWorkarea workarea = (IWorkarea) Main.ServiceContainer().GetService(MainWorkarea.class);
//                /**
//                 * Сообщаем в dataServer информацию об основной рабочей области
//                 */
//                dataServer.setWorkarea((IMainWorkarea) workarea);
                workareaManager.openArea(workarea, Main.MainFrame());
                return null;
            }
        });

        dispatcher.subscribe(IEventModel.USER_LOGOUT_EVENT, new IEventListener() {
            @Override
            public <T> T handleEvent(IEvent event) {
                dataServer = null;
                mainFrame.setTitle("Cont.Client");
                return null;
            }
        });
        dispatcher.subscribe(IEventModel.USER_LOGOUT_EVENT, new IEventListener() {
            @Override
            public <T> T handleEvent(IEvent event) {
                IWorkareaManager workareaManager = (IWorkareaManager) Main.ServiceContainer().GetService(IWorkareaManager.class);
                IWorkarea workarea = (IWorkarea) Main.ServiceContainer().GetService(AuthenticationWorkarea.class);
                workareaManager.openArea(workarea, Main.MainFrame());
                return null;
            }
        });

        dispatcher.subscribe(IEventModel.APP_LOADING_COMPLETED_EVENT, new IEventListener() {
            @Override
            public <T> T handleEvent(IEvent event) {
                IWorkareaManager workareaManager = (IWorkareaManager) Main.ServiceContainer().GetService(IWorkareaManager.class);
                IWorkarea workarea = (IWorkarea) Main.ServiceContainer().GetService(AuthenticationWorkarea.class);
//                IWorkarea workarea = (IWorkarea) Main.ServiceContainer().GetService(MainWorkarea.class);
                workareaManager.openArea(workarea, Main.MainFrame());
                return null;
            }
        });
    }

    private static JFrame createMainFrame(){
        mainFrame = new JFrame("Cont.Client");

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        /*узнаем размер экрана,
          возвращает пару значений
          длина-ширина*/

        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        mainFrame.setVisible(true);
        //метод позволяет визуализировать окно (если флаг true)

        int frame_width = (int)Math.floor(size.width * .75),
                frame_height = (int)Math.floor(size.height * .75);
        //задаем исходные размеры нашего окна

        mainFrame.setBounds((size.width - frame_width) /2,
                (size.height - frame_height)/2,
                frame_width, frame_height);
        /*задает положение окна на экране
           первые два параметра отвечают за
           расположение окна по горизонтали и
           вертикали, а вторые за размеры самого  окна*/

        mainFrame.setResizable(true);
        mainFrame.setTitle("Cont.Client");



        return mainFrame;
    }

    private static void createUser(){
        user = new User();
    }


}
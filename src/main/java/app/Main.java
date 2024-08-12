package app;
import AuthenticationManager.ContAuthenticationManager;
import Data.DataServer.DataServerManager;
import EventDispatcher.EventDispatcher;
import Interfaces.*;
import ServiceProvider.ServiceContainer;
import ConfigParser.*;
import User.User;
import Workarea.WorkareaManager;
import Workarea.Areas.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import jdk.jfr.Description;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
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

        ((ServiceContainer)serviceContainer).Lock();
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
        ((ServiceContainer)serviceContainer).AddService(IWorkareaManager.class, new WorkareaManager());
        ((ServiceContainer)serviceContainer).AddService(IEventDispatcher.class, new EventDispatcher());
        ((ServiceContainer)serviceContainer).AddService(ContAuthenticationManager.class, new ContAuthenticationManager());
        ((ServiceContainer)serviceContainer).AddService(IDataServerManager.class, new DataServerManager(dataServersConfig));

    }


    @Description("Регистрация компонентов сервисов, например Workareas для WorkareaManager  или DataServers для DataServerManager")
    private static void registerServiceComponents()
    {
        registerWorkareas();
        registerDataServers();

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
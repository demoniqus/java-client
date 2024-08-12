package app;
import AuthenticationManager.ContAuthenticationManager;
import Data.DataServer.DataServerManager;
import EventDispatcher.EventDispatcher;
import Interfaces.*;
import ServiceProvider.ServiceContainer;
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

        ObjectMapper $om = new ObjectMapper();
        String baseDir = System.getProperty("base.dir");

        URL configURL = serviceContainer.getClass().getClassLoader().getResource("main/resources/configs/config.yml");
        if (configURL == null) {
            configURL = serviceContainer.getClass().getClassLoader().getResource("main/resources/configs/config.yaml");
        }

        if (configURL == null) {
            return;
        }
        File configFile = new File(configURL.getFile());
        YAMLFactory yamlFactory = new YAMLFactory();
        try {

            YAMLParser yamlParser = yamlFactory.createParser(configFile);
            Map<String, Object> config = parseYamlFile(yamlParser);
            if (config.containsKey("parameters") && config.get("parameters") instanceof Map) {
                Map<String, Object> parameters = (Map<String, Object>) config.get("parameters");

                for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                    ((ServiceContainer)serviceContainer).setParameter(entry.getKey(), entry.getValue());
                }
            }

        } catch (IOException e) {
            //TODO throw own exception
            return;
        }

        //((ServiceContainer)serviceContainer).setParameter("baseDir", baseDir);

    }

    private static  Map<String, Object> parseYamlFile(YAMLParser yamlParser) throws JsonParseException, JsonMappingException, IOException
    {
        return parseYamlFileObjectStructure(yamlParser);
//        JsonToken jsonToken = yamlParser.nextToken();
//
//        while (jsonToken != null) {
//            switch (jsonToken) {
//                case START_OBJECT: System.out.println("Object Started");
//                    HashMap<String, Object> object = new HashMap<>();
//                    while (jsonToken != null && jsonToken != JsonToken.END_OBJECT) {
//
//                    }
//                    break;
//                case END_OBJECT: System.out.println("Object Ended");
//                    break;
//                case START_ARRAY: System.out.println("Array Started");
//                    break;
//                case END_ARRAY: System.out.println("Array Ended");
//                    break;
//                case FIELD_NAME: System.out.println("Key field: " + yamlParser.getText());
//                    break;
//                case VALUE_FALSE:
//                case VALUE_NULL:
//                case VALUE_NUMBER_FLOAT:
//                case VALUE_NUMBER_INT:
//                case VALUE_STRING:
//                case VALUE_TRUE:
//                default:System.out.println("Key value: " + yamlParser.getText());
//                    break;
//            }
//        }
    }

    private static  List<Map<String, Object>>  parseYamlFileArrayStructure(YAMLParser yamlParser) throws JsonParseException, JsonMappingException, IOException
    {
        List<Map<String, Object>> list = new ArrayList<>();
        JsonToken jsonToken = yamlParser.nextToken();

        while (
            jsonToken != null &&
            jsonToken != JsonToken.END_ARRAY
        ) {
            switch (jsonToken) {
                case START_OBJECT: System.out.println("Object Started");
                    list.add(parseYamlFileObjectStructure(yamlParser));
                    break;
                case END_OBJECT: System.out.println("Object Ended");
                    break;
                case START_ARRAY: System.out.println("Array Started");
                    break;
                case END_ARRAY: System.out.println("Array Ended");
                    break;
                case FIELD_NAME: System.out.println("Key field: " + yamlParser.getText());
                    break;
                case VALUE_FALSE:
                case VALUE_NULL:
                case VALUE_NUMBER_FLOAT:
                case VALUE_NUMBER_INT:
                case VALUE_STRING:
                case VALUE_TRUE:
                default:System.out.println("Key value: " + yamlParser.getText());
                    break;
            }
            jsonToken = yamlParser.nextToken();
        }

        if (jsonToken != JsonToken.END_ARRAY) {
            throw new RuntimeException("Не найден завершающий токен ]");
        }

        return list;
    }

    private static  Map<String, Object> parseYamlFileObjectStructure(YAMLParser yamlParser) throws JsonParseException, JsonMappingException, IOException
    {
        return parseYamlFileObjectStructure(yamlParser, null);
    }

    private static  Map<String, Object> parseYamlFileObjectStructure(
        YAMLParser yamlParser,
        JsonToken beginWithToken
    ) throws JsonParseException, JsonMappingException, IOException
    {
        String fieldName;
        HashMap<String, Object> object = new HashMap<>();
        JsonToken jsonToken = beginWithToken != null ?
                beginWithToken :
                yamlParser.nextToken();
        JsonToken prevToken = null;
        String prevTokenId;
        Stack<String> fields = new Stack<>();

        while (
            jsonToken != null &&
            jsonToken != JsonToken.END_OBJECT
        ) {
            switch (jsonToken) {
                case START_OBJECT:

                    Map<String, Object> map = parseYamlFileObjectStructure(yamlParser);
                    if (fields.size() == 0) {
                        return map;
//                        throw new RuntimeException("Неверная структура yaml-файла");
                    }
                    fieldName = fields.pop();
                    object.put(fieldName, map);
                    break;
                case START_ARRAY:
                    fieldName = fields.pop();
                    if (fieldName == null) {
                        throw new RuntimeException("Неверная структура yaml-файла");
                    }
                    object.put(fieldName, parseYamlFileArrayStructure(yamlParser));
                    break;
                case END_OBJECT:
                case END_ARRAY:
                    //TODO Сделать определение имени файла и номера строки
                    String message = "Неожиданный токен " + jsonToken + " в строке ";// + yamlParser.
                    throw new RuntimeException(message);
                case FIELD_NAME:
                    //TODO Возможно, что field, следующий за field - это тоже объект типа:
                    //   field:
                    //      field2:
                    //         field3: some_value

                    fields.push(yamlParser.getText());
                    if (prevToken == JsonToken.FIELD_NAME) {
                        object.put(fields.pop(), parseYamlFileObjectStructure(yamlParser, jsonToken));
                    }

                    break;
                case VALUE_FALSE:
                case VALUE_NULL:
                case VALUE_NUMBER_FLOAT:
                case VALUE_NUMBER_INT:
                case VALUE_STRING:
                case VALUE_TRUE:
                default:

                    //TODO Обрабатывать пустые строки, если таковые имеются
                    String value = yamlParser.getText();
                    if (
                            value == null ||
                                    value.trim() == "" ||
                                    value.trim() == "~" ||
                                    value.trim() == "null"
                    ) {
                        object.put(fields.pop(), null);
                    }
                    else {
                        switch (value.trim().toLowerCase()) {
                            case "true":
                                object.put(fields.pop(), true);
                                break;
                            case "false":
                                object.put(fields.pop(), false);
                                break;
                            default:
                                object.put(fields.pop(), yamlParser.getText());
                                break;
                        }
                    }

                    break;
            }
            prevToken = jsonToken;
            jsonToken = yamlParser.nextToken();
        }

        if (jsonToken != JsonToken.END_OBJECT)
        {
            throw new RuntimeException("Не найден завершающий токен }");
        }

        while (fields.size() > 0) {
            object.put(fields.pop(), null);
        }
        return object;
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
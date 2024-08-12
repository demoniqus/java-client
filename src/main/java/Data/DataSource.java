package Data;

import AuthenticationManager.Authentication.ContAuthentication;
import Interfaces.*;
import app.Main;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Jdk14Logger;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.DefaultManagedHttpClientConnection;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jabricks.widgets.renderers.FloatRenderer;
import org.jabricks.widgets.treetable.JTreeTable;
import org.jabricks.widgets.treetable.TreeTableModel;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import com.google.gson.*;


import static org.apache.http.client.protocol.HttpClientContext.COOKIE_STORE;

final public class DataSource implements IDataSource {
    final private String title;
    final private String modelUrl;
    final private String dataUrl;
    final private String optionsUrl;




    public DataSource(Map<String, Object> sourceConfig) throws RuntimeException {
        title = sourceConfig.get("title").toString();
        modelUrl = sourceConfig.get("modelUrl").toString();
        dataUrl = sourceConfig.get("dataUrl").toString();
        optionsUrl = sourceConfig.get("optionsUrl").toString();

    }

    @Override
    public String getTitle() {
        return title;
    }

    public void load(JPanel workPanel)
    {
        org.jabricks.widgets.treetable.ObjectModel.setSignificantFields("name", "leaf");


        List<IDataModelItem> dataModel = getModel();
        int[] width = new int[dataModel.size()];
        int counter = 0;
        for (IDataModelItem dataModelItem : dataModel) {
            width[counter++] = dataModelItem.getWidth();
        }

        TreeTableModel model = new DataModel(getModel());
        JTreeTable treeTable = new JTreeTable(model);

        treeTable.setColumnsWidth(width);

        treeTable.setRowHeight(22);

        treeTable.setDefaultRenderer(Float.class, new FloatRenderer());
        treeTable.setAutoResizeColumn (JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane jsp = new JScrollPane(treeTable);
        jsp.setBackground(Color.decode("0xff00ff"));
        workPanel.add(jsp, BorderLayout.CENTER);
        treeTable.drawTableHeaderRaised();
        treeTable.updateUI();
//        loadData();

    }

    private void loadData_new()
    {

        try {

            org.apache.http.client.HttpClient client = HttpClientBuilder
                    .create()
//                    .setRedirectStrategy(strategy)
                    .build()
                    ;
//            org.apache.http.client.HttpClient client = HttpClients.createDefault();
            HttpContext localContext = new BasicHttpContext();
            URI uri = new URI("https", "debug", null, null);
            HttpPost httpPost = new HttpPost(uri);
            HttpGet httpGet = new HttpGet(uri);

            org.apache.http.HttpResponse response = client.execute(httpGet);


            IUser user = Main.User();
//            if (response.getStatusLine().getStatusCode() == 302) {
//                /**
//                 * Аутентификация с указанными данными прошла успешно - запоминаем эти данные
//                 */
//                IAuthentication authentication = new ContAuthentication(this, user, username, password);
//                user.setAuthentication(authentication);
//                //Arrays.stream(response.getHeaders("set-cookie")).toList()
////                List<org.apache.http.Header> headers = Arrays.stream(response.getAllHeaders()).toList();
////                Predicate<? super T> predicate = ;
//                List<Header> headers = Arrays
//                        .stream(response.getHeaders("set-cookie"))
//                        .filter(header -> !header.getValue().toLowerCase().contains("redirect"))
//                        .toList();
////                List<org.apache.http.Header> headers = Arrays.stream(response.getHeaders("set-cookie")).toList();
////                getCookies(headers);
//                authentication.setAuthData("set-cookie", headers);
//                res = true;
//            }
//            HttpEntity entity = response.getEntity();
//
//            if (entity != null) {
//                try (InputStream instream = entity.getContent()) {
//                    // do something useful
//                }
//            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    private void loadData()
    {

        try {

            Gson gson = new Gson();
            String lp = System.getProperty("user.dir");
            Path p = Path.of(lp + "/src/main/resources/json/budget/habarovsk/headers.json");
            String s = Files.readString(p);

            JsonObject o = gson.fromJson(s, JsonObject.class);
            s += "";
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadData_http()
    {
        IDataServer dataServer = Main.DataServer();
//        String path = "/reestr/data/income-budget-bundle_reestr_income-budget_income-budget-reestr";
        String path = "/reestr/show/estimate_customer";

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("income-budget-bundle_reestr_income-budget_income-budget-reestr[ProjectContractType][project]", "46"));
        params.add(new BasicNameValuePair("income-budget-bundle_reestr_income-budget_income-budget-reestr[ProjectContractType][ContractType][srcContract]", "3145"));
        params.add(new BasicNameValuePair("income-budget-bundle_reestr_income-budget_income-budget-reestr[reestr_type]", "income-budget-bundle_reestr_income-budget_income-budget-reestr"));
        params.add(new BasicNameValuePair("context", "income-budget-bundle_reestr_income-budget_income-budget-reestr"));
        params.add(new BasicNameValuePair("reestrMode", "main"));

        try {
            CookieStore cookieStore = new BasicCookieStore();
            BasicClientCookie cookie;

            List<Header> cookies = Main.User().getAuthentication().getAuthData("set-cookie");
            for (Header header : cookies) {
                String[] parts = header.getValue().split(";");
                parts = parts[0].split("=");
                cookie = new BasicClientCookie(parts[0], parts[1]);
//                cookie.setPath("/");
                cookie.setPath(path);
                cookie.setDomain(".ite-ng.ru");
                cookie.setSecure(true);
                cookie.setExpiryDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7));
                cookie.setAttribute(ClientCookie.DOMAIN_ATTR, cookie.getDomain());
                cookieStore.addCookie(cookie);
            }
            LaxRedirectStrategy strategy = new LaxRedirectStrategy();

            org.apache.http.client.HttpClient client = HttpClientBuilder
                    .create()
//                    .setRedirectStrategy(strategy)
                    .build()
                    ;
//            org.apache.http.client.HttpClient client = HttpClients.createDefault();
            HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute(COOKIE_STORE, cookieStore);
            URI uri = new URI(dataServer.Scheme(), dataServer.Host(), path, null);
            HttpPost httpPost = new HttpPost(uri);
//            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            httpPost.setHeader("Content-Type", "application/json");
//            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            //Logger.getGlobal().setLevel(Level.ALL);
            Jdk14Logger log = (Jdk14Logger)LogFactory.getLog(DefaultManagedHttpClientConnection.class);
            log.getLogger().setLevel(Level.ALL);
            //log.isDebugEnabled()

            org.apache.http.HttpResponse response = client.execute(httpPost, localContext);
            if (strategy.isRedirected(httpPost, response, localContext))
            {
                HttpUriRequest request = strategy.getRedirect(httpPost, response, localContext);
                response = client.execute(request, localContext);
            }

            IUser user = Main.User();
//            if (response.getStatusLine().getStatusCode() == 302) {
//                /**
//                 * Аутентификация с указанными данными прошла успешно - запоминаем эти данные
//                 */
//                IAuthentication authentication = new ContAuthentication(this, user, username, password);
//                user.setAuthentication(authentication);
//                //Arrays.stream(response.getHeaders("set-cookie")).toList()
////                List<org.apache.http.Header> headers = Arrays.stream(response.getAllHeaders()).toList();
////                Predicate<? super T> predicate = ;
//                List<Header> headers = Arrays
//                        .stream(response.getHeaders("set-cookie"))
//                        .filter(header -> !header.getValue().toLowerCase().contains("redirect"))
//                        .toList();
////                List<org.apache.http.Header> headers = Arrays.stream(response.getHeaders("set-cookie")).toList();
////                getCookies(headers);
//                authentication.setAuthData("set-cookie", headers);
//                res = true;
//            }
//            HttpEntity entity = response.getEntity();
//
//            if (entity != null) {
//                try (InputStream instream = entity.getContent()) {
//                    // do something useful
//                }
//            }
        } catch (IOException | URISyntaxException | ProtocolException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadData_orig()
    {
        IDataServer dataServer = Main.DataServer();
//        String path = "/reestr/data/income-budget-bundle_reestr_income-budget_income-budget-reestr";
        String path = "/reestr/show/estimate_customer";

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("income-budget-bundle_reestr_income-budget_income-budget-reestr[ProjectContractType][project]", "46"));
        params.add(new BasicNameValuePair("income-budget-bundle_reestr_income-budget_income-budget-reestr[ProjectContractType][ContractType][srcContract]", "3145"));
        params.add(new BasicNameValuePair("income-budget-bundle_reestr_income-budget_income-budget-reestr[reestr_type]", "income-budget-bundle_reestr_income-budget_income-budget-reestr"));
        params.add(new BasicNameValuePair("context", "income-budget-bundle_reestr_income-budget_income-budget-reestr"));
        params.add(new BasicNameValuePair("reestrMode", "main"));

        try {
            CookieStore cookieStore = new BasicCookieStore();
            BasicClientCookie cookie;

            List<Header> cookies = Main.User().getAuthentication().getAuthData("set-cookie");
            for (Header header : cookies) {
                String[] parts = header.getValue().split(";");
                parts = parts[0].split("=");
                cookie = new BasicClientCookie(parts[0], parts[1]);
//                cookie.setPath("/");
                cookie.setPath(path);
                cookie.setDomain(".ite-ng.ru");
                cookie.setSecure(true);
                cookie.setExpiryDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7));
                cookie.setAttribute(ClientCookie.DOMAIN_ATTR, cookie.getDomain());
                cookieStore.addCookie(cookie);
            }
            LaxRedirectStrategy strategy = new LaxRedirectStrategy();

            org.apache.http.client.HttpClient client = HttpClientBuilder
                    .create()
//                    .setRedirectStrategy(strategy)
                    .build()
                    ;
//            org.apache.http.client.HttpClient client = HttpClients.createDefault();
            HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute(COOKIE_STORE, cookieStore);
            URI uri = new URI(dataServer.Scheme(), dataServer.Host(), path, null);
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            httpPost.setHeader("Content-Type", "application/json");
//            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            //Logger.getGlobal().setLevel(Level.ALL);
            Jdk14Logger log = (Jdk14Logger)LogFactory.getLog(DefaultManagedHttpClientConnection.class);
            log.getLogger().setLevel(Level.ALL);
            //log.isDebugEnabled()

            org.apache.http.HttpResponse response = client.execute(httpPost, localContext);
            if (strategy.isRedirected(httpPost, response, localContext))
            {
                HttpUriRequest request = strategy.getRedirect(httpPost, response, localContext);
                response = client.execute(request, localContext);
            }

            IUser user = Main.User();
//            if (response.getStatusLine().getStatusCode() == 302) {
//                /**
//                 * Аутентификация с указанными данными прошла успешно - запоминаем эти данные
//                 */
//                IAuthentication authentication = new ContAuthentication(this, user, username, password);
//                user.setAuthentication(authentication);
//                //Arrays.stream(response.getHeaders("set-cookie")).toList()
////                List<org.apache.http.Header> headers = Arrays.stream(response.getAllHeaders()).toList();
////                Predicate<? super T> predicate = ;
//                List<Header> headers = Arrays
//                        .stream(response.getHeaders("set-cookie"))
//                        .filter(header -> !header.getValue().toLowerCase().contains("redirect"))
//                        .toList();
////                List<org.apache.http.Header> headers = Arrays.stream(response.getHeaders("set-cookie")).toList();
////                getCookies(headers);
//                authentication.setAuthData("set-cookie", headers);
//                res = true;
//            }
//            HttpEntity entity = response.getEntity();
//
//            if (entity != null) {
//                try (InputStream instream = entity.getContent()) {
//                    // do something useful
//                }
//            }
        } catch (IOException | URISyntaxException | ProtocolException e) {
            throw new RuntimeException(e);
        }
    }

    public Icon getIcon()
    {
        return null;
    }

    private List<IDataModelItem> getModel()
    {
        List<IDataModelItem> model = new ArrayList<>();
        JsonObject o;

        try {

            Gson gson = new Gson();
            String lp = System.getProperty("user.dir");
            Path p = Path.of(lp + "/src/main/resources/json/budget/habarovsk/headers.json");
            String s = Files.readString(p);

            o = gson.fromJson(s, JsonObject.class);
            JsonObject fields = o.get("fields").getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> set = fields.entrySet();
            for (Map.Entry<String, JsonElement> entry : set) {
                Class<?> c = String.class;
                JsonObject params = entry.getValue().getAsJsonObject();
                if (entry.getKey() == "tree") {
                    c = TreeTableModel.class;
                }
                else {

                    //switch (params.get("type").getAsString()) {}
                }
                JsonPrimitive name = params.get("name").getAsJsonPrimitive();
                int width = 25;
                if (params.has("width")) {
                    width = params.get("width").getAsInt();
                    width = width < 1 ? 25 : width;
                }
                IDataModelItem dataModelItem = new DataModelItem(c,entry.getKey(),  name.getAsString());
                dataModelItem.setWidth(width);
                model.add(dataModelItem);
            }

        }  catch (IOException e) {
            throw new RuntimeException(e);
        }

//        IDataModelItem dataModelItem = new DataModelItem(TreeTableModel.class, "id", "#ID");
//        dataModelItem.setWidth(75);
//        model.add(dataModelItem);
//
//        dataModelItem = new DataModelItem(String.class, "name", "Наименование");
//        dataModelItem.setWidth(175);
//        model.add(dataModelItem);
//
//        dataModelItem = new DataModelItem(String.class, "number", "Номер");
//        dataModelItem.setWidth(175);
//        model.add(dataModelItem);
//
//        switch (modelUrl) {
//            case "budget":
//                break;
//        }
        return model;
    }

}

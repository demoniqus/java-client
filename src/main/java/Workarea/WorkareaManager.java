package Workarea;


import Interfaces.IUser;
import Interfaces.IWorkarea;
import Interfaces.IWorkareaManager;
import app.Main;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.apache.http.client.protocol.HttpClientContext.COOKIE_STORE;

public class WorkareaManager implements IWorkareaManager {
    //TODO Сделать привязку открытого рабочего пространства к контейнеру и закрывать только в том случае, если в указанном контейнере открывается новое рабочее пространство
    private IWorkarea currentWorkarea = null;
    public void openArea(IWorkarea workarea, Container container) {
//        test();
        if (workarea != null) {
            if (currentWorkarea != null) {
                currentWorkarea.close();
            }
            if (workarea.open(container)) {
                currentWorkarea = workarea;
                container.revalidate();
                container.repaint();

            }
        }
    }

    public void test()
    {
        try {
            org.apache.http.client.HttpClient client = HttpClientBuilder.create().build();
            URI uri = new URI("https", "qsurv.ite-ng.ru", "/login", null);
            HttpPost httpPost = new HttpPost(uri);

            org.apache.http.HttpResponse response = client.execute(httpPost);


            IUser user = Main.User();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public void test1()
    {
        try {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https", "qsurv.ite-ng.ru", "/login", null))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            IUser user = Main.User();
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

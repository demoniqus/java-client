package AuthenticationManager;

import AuthenticationManager.Authentication.ContAuthentication;
import Interfaces.*;
import app.Main;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;

import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.function.Predicate;

import static org.apache.http.client.protocol.HttpClientContext.COOKIE_STORE;


public class ContAuthenticationManager implements IAuthenticationManager {

    @Override
    public boolean authenticate(String username, String password, IUser user, IDataServer dataServer) {

        return auth3(username, password, dataServer);

//        final Collection<NameValuePair> params = new ArrayList<>();
//        params.add(new BasicNameValuePair("_username", username));
//        params.add(new BasicNameValuePair("_password", password));
//        try {
//            final Content getResult = Request.Post("https://debugcont.ite-ng.ru/login_check")
//                    .setHeader("verify_peer", "false")
//                    .setHeader("verify_host", "false")
//                    .bodyForm(params, Charset.defaultCharset())
//                    .execute().returnContent();
//        } catch (IOException ex) {
//            String res = "FALSE";
//        }


    }

    private Boolean auth(String username, String password, IUser user, IDataServer dataServer)
    {
        HashMap<String, Boolean> testUsers = new HashMap<>(5);
        testUsers.put("yutsinn", Boolean.TRUE);
        testUsers.put("nikolns", Boolean.TRUE);
        testUsers.put("antipda", Boolean.TRUE);
        testUsers.put("test", Boolean.TRUE);
        testUsers.put("true", Boolean.TRUE);

        boolean res = testUsers.containsKey(username.toLowerCase());
        if (res) {
            /**
             * Аутентификация с указанными данными прошла успешно - запоминаем эти данные
             */
            IAuthentication authentication = new ContAuthentication(this, user, username, password);
            user.setAuthentication(authentication);
        }

        return res;
    }

    private void auth1()
    {
        HttpResponse<String> response = null;

        try {

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{MOCK_TRUST_MANAGER}, new SecureRandom());
            HttpClient client = HttpClient.newBuilder().sslContext(sslContext).build();
            HttpRequest request = HttpRequest
                    .newBuilder()

                    .uri(new URI("https://debugcont.ite-ng.ru/login_check"))
                    .build();
            HttpPost httpPost = new HttpPost("https://debugcont.ite-ng.ru/login");
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {} catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String auth2(String username, String password)
    {
        String url = "https://debugcont.ite-ng.ru/login_check";
        String urlParameters = "_username=" + username + "&_password=" + password;
        String resp = null;

        HttpClient client = null;
        HttpResponse<String> response;
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(urlParameters))
                .uri(URI.create(url))

                .setHeader("User-Agent", USER_AGENT) // request header
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try {
            //https://stackoverflow.com/questions/66388231/json-object-to-body-publisher
            //keytool.exe -importcert -trustcacerts -file "D:\projects\java\Cont.Client\src\main\resources\certificates\_.ite-ng.ru.der" -alias cont_cacert -keystore "C:\Program Files\Java\jdk-22\lib\security\cacerts"
            client = getHttpClient();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            resp = response.body();

        }
        catch (IOException | InterruptedException e) {
            //PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
            throw new RuntimeException(e);
        }
        //response.headers().allValues("set-cookie")
        resp = resp.trim();
        return resp;
    }

    private Boolean auth3(String username, String password, IDataServer dataServer) {
        //Args.notNull(route, "HTTP route");
        if (!(dataServer instanceof IContDataServer)) {
            throw new RuntimeException("Системная ошибка. Обратитесь к разработчику.");
        }
        boolean res = false;

        //curl -d "_username=test&_password=test" -H "Content-type: application/json" -X POST https://debugcont.ite-ng.ru/login_check
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("_username", username));
        params.add(new BasicNameValuePair("_password", password));
        try {
//            org.apache.http.client.HttpClient client = HttpClients.createDefault();
            LaxRedirectStrategy strategy = new LaxRedirectStrategy();
            org.apache.http.client.HttpClient client = HttpClientBuilder
                    .create()
//                    .setRedirectStrategy(strategy)
                    .build()
                    ;

            URI uri = new URI(dataServer.Scheme(), dataServer.Host(), "/login_check", null);
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            org.apache.http.HttpResponse response = client.execute(httpPost);

            if (response.getStatusLine().getStatusCode() == 302) {
                IUser user = Main.User();
                /**
                 * Аутентификация с указанными данными прошла успешно - запоминаем эти данные
                 */
                IAuthentication authentication = new ContAuthentication(this, user, username, password);
                user.setAuthentication(authentication);
                //Arrays.stream(response.getHeaders("set-cookie")).toList()
//                List<org.apache.http.Header> headers = Arrays.stream(response.getAllHeaders()).toList();
//                Predicate<? super T> predicate = ;
                List<Header> headers = Arrays
                        .stream(response.getHeaders("set-cookie"))
                        .filter(header -> !header.getValue().toLowerCase().contains("redirect"))
                        .toList();
//                List<org.apache.http.Header> headers = Arrays.stream(response.getHeaders("set-cookie")).toList();
//                getCookies(headers);
                /*
                BEARER=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOjE3MjMxMDU4MDgsImV4cCI6MTcyMzEwNjQwOCwicGF5bG9hZCI6InRlc3QifQ.SLslZ4m2Pu_StAapx7bYMi56AndoAc7WsrqI16lI7fBoB9B_tPp7pOomdq0RnG9PxsPfh8d2eDCijShkp72WNK0kHGXxZnxwJohYwHlPIJM_wUn8UhmTOxLm3MbxNtGGBe6esesjMQ9ujPhKFDT4-2poxX4ywDBPM3B4kkaSTiS_b6zW7NUi4js432VEDIDWNuZpL9z4K3OEyojHElrZdO33511gZhZUu82n4DV_3EGoyrytTSm1OScSck_nZtaVb5NwIAp6Z2vNLA1L6vSw3-dYVFJHz04oCtpp4Ihsu0cs8mvPaRd1yhJsgIhx5lW46V0ShXqgSiU7pYQ1JbWzLcig9HbpxsK6v625ag0XJDXGxxbJ7s0mL046PU5OasvXJU1SVVfsn0WvY6a21aXRo7TUMLkGY_riBzXKCWpBA15cQNZXZMJRs_xwCst42xI0EUiQoeEMmc1fRJohqzCACjoIezAozHiVwTM_4CS-gEGdJHdAllcDSg-O13RjwqQgqtSHCOWmFYt-bgHRmIrVXQUzcxT_Pt_Ayb-a4AXU9M5VcJMjogKtTpzknL-b6eOU6tG9XKdReI7tut1GfmP4Lrs_rL81Xt1PHgcsdzdDA3xRJyIILsg4mzdl0g46VY64E9fjBt-Dmg63XHolCvXrORKamPqAgtLVZ8sqfKK_HL4;
                expires=Thu, 08-Aug-2024 08:40:08 GMT;
                Max-Age=599;
                path=/;
                domain=.ite-ng.ru;
                samesite=lax
                 */
                //List<BasicClientCookie> cookies = headers.stream().map( ContAuthenticationManager::convertHeaderToCookie).toList();


                authentication.setAuthData("set-cookie", headers);
                res = true;
//====================================

//                CookieStore cookieStore = new BasicCookieStore();
//                BasicClientCookie cookie;
//                String path = "/reestr/show/estimate_customer";
//                List<Header> cookies = Main.User().getAuthentication().getAuthData("set-cookie");
//                for (Header header : cookies) {
//                    String[] parts = header.getValue().split(";");
//                    parts = parts[0].split("=");
//                    cookie = new BasicClientCookie(parts[0], parts[1]);
////                cookie.setPath("/");
//                    cookie.setPath(path);
//                    cookie.setDomain(".ite-ng.ru");
//                    cookie.setSecure(true);
//                    cookie.setExpiryDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7));
//                    cookie.setAttribute(ClientCookie.DOMAIN_ATTR, cookie.getDomain());
//                    cookieStore.addCookie(cookie);
//                }
//
//                HttpContext localContext = new BasicHttpContext();
//                localContext.setAttribute(COOKIE_STORE, cookieStore);
//                uri = new URI(dataServer.Scheme(), dataServer.Host(), path, null);
//                httpPost.setURI(uri);
//                httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//                httpPost.setHeader("Content-Type", "application/json");
//                response = client.execute(httpPost, localContext);
//                HttpEntity entity = response.getEntity();










//====================================
            }
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

        return res;
    }
    private Boolean auth4(String username, String password, IDataServer dataServer) {

        if (!(dataServer instanceof IContDataServer)) {
            throw new RuntimeException("Системная ошибка. Обратитесь к разработчику.");
        }
        boolean res = false;


        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("_username", username));
        params.add(new BasicNameValuePair("_password", password));
        try {
//            org.apache.http.client.HttpClient client = HttpClients.createDefault();
            LaxRedirectStrategy strategy = new LaxRedirectStrategy();
            org.apache.http.client.HttpClient client = HttpClientBuilder
                    .create()
                    .setRedirectStrategy(strategy)
                    .build()
                    ;
            HttpContext localContext = new BasicHttpContext();
            URI uri = new URI(dataServer.Scheme(), dataServer.Host(), "/login_check", null);
            //на api без _csrf_token невозможно авторизоваться
//            URI uri = new URI(
//                    dataServer.Scheme(),
//                    null,
//                    "debugcont.ite-ng.ru",
//                    8443,
//                    "/evrinoma/security/login_check",
//                    null,
//                    null
//            );

            //this(scheme, null, host, -1, path, null, fragment);
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            httpPost.setHeader("Content-Type", "application/json");
            org.apache.http.HttpResponse response = client.execute(httpPost, localContext);
            if (strategy.isRedirected(httpPost, response, localContext))
            {
                HttpUriRequest request = strategy.getRedirect(httpPost, response, localContext);
                response = client.execute(request, localContext);
            }
            if (response.getStatusLine().getStatusCode() == 302) {
                IUser user = Main.User();
                /**
                 * Аутентификация с указанными данными прошла успешно - запоминаем эти данные
                 */
                IAuthentication authentication = new ContAuthentication(this, user, username, password);
                user.setAuthentication(authentication);
                //Arrays.stream(response.getHeaders("set-cookie")).toList()
//                List<org.apache.http.Header> headers = Arrays.stream(response.getAllHeaders()).toList();
//                Predicate<? super T> predicate = ;
                List<Header> headers = Arrays
                        .stream(response.getHeaders("set-cookie"))
                        .filter(header -> !header.getValue().toLowerCase().contains("redirect"))
                        .toList();
//                List<org.apache.http.Header> headers = Arrays.stream(response.getHeaders("set-cookie")).toList();
//                getCookies(headers);
                /*
                BEARER=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOjE3MjMxMDU4MDgsImV4cCI6MTcyMzEwNjQwOCwicGF5bG9hZCI6InRlc3QifQ.SLslZ4m2Pu_StAapx7bYMi56AndoAc7WsrqI16lI7fBoB9B_tPp7pOomdq0RnG9PxsPfh8d2eDCijShkp72WNK0kHGXxZnxwJohYwHlPIJM_wUn8UhmTOxLm3MbxNtGGBe6esesjMQ9ujPhKFDT4-2poxX4ywDBPM3B4kkaSTiS_b6zW7NUi4js432VEDIDWNuZpL9z4K3OEyojHElrZdO33511gZhZUu82n4DV_3EGoyrytTSm1OScSck_nZtaVb5NwIAp6Z2vNLA1L6vSw3-dYVFJHz04oCtpp4Ihsu0cs8mvPaRd1yhJsgIhx5lW46V0ShXqgSiU7pYQ1JbWzLcig9HbpxsK6v625ag0XJDXGxxbJ7s0mL046PU5OasvXJU1SVVfsn0WvY6a21aXRo7TUMLkGY_riBzXKCWpBA15cQNZXZMJRs_xwCst42xI0EUiQoeEMmc1fRJohqzCACjoIezAozHiVwTM_4CS-gEGdJHdAllcDSg-O13RjwqQgqtSHCOWmFYt-bgHRmIrVXQUzcxT_Pt_Ayb-a4AXU9M5VcJMjogKtTpzknL-b6eOU6tG9XKdReI7tut1GfmP4Lrs_rL81Xt1PHgcsdzdDA3xRJyIILsg4mzdl0g46VY64E9fjBt-Dmg63XHolCvXrORKamPqAgtLVZ8sqfKK_HL4;
                expires=Thu, 08-Aug-2024 08:40:08 GMT;
                Max-Age=599;
                path=/;
                domain=.ite-ng.ru;
                samesite=lax
                 */
                List<BasicClientCookie> cookies = headers.stream().map( ContAuthenticationManager::convertHeaderToCookie).toList();


                authentication.setAuthData("set-cookie", headers);
                res = true;
            }
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

        return res;
    }

    private Boolean auth5(String username, String password, IDataServer dataServer) {

        if (!(dataServer instanceof IContDataServer)) {
            throw new RuntimeException("Системная ошибка. Обратитесь к разработчику.");
        }
        boolean res = false;
        HttpResponse<String> response;


        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("_username", username));
        params.add(new BasicNameValuePair("_password", password));
        try {
            URI uri = new URI(dataServer.Scheme(), dataServer.Host(), "/login_check", null);
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString("_username=test&_password=test"))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());




        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    public static BasicClientCookie convertHeaderToCookie(Header header) {
        BasicClientCookie cookie = new BasicClientCookie(header.getName(), header.getValue());
        return cookie;
    }

//    private void getCookies(List<org.apache.http.Header> cookies)
//    {
//        IUser user = Main.User();
//        Main.User().getAuthentication().setAuthData("set-cookie", cookies);
//
//    }

    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36";

    private HttpClient getHttpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NEVER)
//                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    @Override
    public void authenticateRequest() {
        //аутентификация запроса данных. На входе принимать объект запроса
    }

    private static final TrustManager MOCK_TRUST_MANAGER = new X509ExtendedTrustManager() {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {

        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {

        }
    };
}

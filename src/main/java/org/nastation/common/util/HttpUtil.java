package org.nastation.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

/**
 * @author John | NaChain
 * @since 12/28/2021 1:13
 */
@Slf4j
public class HttpUtil {

    public static int TimeOutSeconds = 30;

    private static SSLSocketFactory factory;

    static{
        factory = socketFactory();
    }

    static public SSLSocketFactory socketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory result = sslContext.getSocketFactory();
            return result;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }
    }

    public static Connection get(String url) throws Exception {
        return Jsoup.connect(url).sslSocketFactory(factory)
                .maxBodySize(0)
                .ignoreHttpErrors(true).followRedirects(true)
                .ignoreContentType(true)
                .method(Connection.Method.GET).timeout(TimeOutSeconds*1000);
    }

    public static Connection getNftResource(String url) throws Exception {
        return Jsoup.connect(url).sslSocketFactory(factory)
                .maxBodySize(0)
                .ignoreHttpErrors(true).followRedirects(true)
                .ignoreContentType(true)
                .method(Connection.Method.GET).timeout(5*1000);
    }

    public static Connection post(String url) throws Exception {
        return Jsoup.connect(url).sslSocketFactory(factory)
                .maxBodySize(0)
                .ignoreHttpErrors(true).followRedirects(true)
                .ignoreContentType(true)
                .method(Connection.Method.POST).timeout(TimeOutSeconds*1000);
    }

    public static Connection.Response retryConn(Connection conn, String url, int times, int seconds, Map<Object,Object> params) throws Exception {
        Connection.Response response = null;
        Exception lastException = null;
        int retries = times;
        while (--retries >= 0) {
            try {
                response = conn.execute();

                if (response != null && response.statusCode() == 500) {

                    String body = response.body();
                    if (StringUtils.isNotBlank(body)) {
                        return response;
                    }
                }

                if (response == null || response.statusCode() != 200) {
                    throw new IOException("The response of load " + url + " is null");
                }

                return response;
            } catch (Exception e) {
                log.error("RetryConn[{}/{}] -> Error while loading {} , params = {}",retries,times,url,params == null ?"":params.toString(), e);
                lastException = e;
            }

            try {
                Thread.sleep(seconds * 1000);
            } catch (Exception e) {
            }
        }
        throw new Exception("Failed to load " + url + " after " + times + " attempts", lastException);
    }

    public static void main(String[] args) throws Exception {
        /*

        for (int i = 0; i < 10; i++) {
            Connection connection = HttpUtil.get("https://service.nachain.org/api/v1/getPrice");
            Connection.Response execute = connection.execute();
            String body = execute.body();
            System.out.println("service = " + body);
        }

        for (int i = 0; i < 10; i++) {
            Connection connection = HttpUtil.get("https://datacenter.nachain.org/dc/getBlock?instanceId=1&height=1");
            Connection.Response execute = connection.execute();
            String body = execute.body();
            System.out.println("datacenter = " + body);
        }
        */

        List<Object> list = Lists.newArrayList();
        list.add("1");
        list.add("");
        list.add(null);

        String join = StringUtils.join(list, ",");
        System.out.printf(join);

    }


}

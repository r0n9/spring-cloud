package vip.fanrong.common;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.fanrong.common.proxy.FakeDnsResolver;
import vip.fanrong.common.proxy.MyConnectionSocketFactory;
import vip.fanrong.common.proxy.MySSLConnectionSocketFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rong on 2017/11/29.
 */
public class MyHttpClient {
    private final static Logger LOG = LoggerFactory.getLogger(MyHttpClient.class);

    private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0";

    public static int testProxy(String proxyHost, int proxyPort, String proxyType) {
        HttpGet request = new HttpGet("http://baidu.com/");
        MyHttpResponse myHttpResponse = getHttpResponse(request, null, null, proxyHost, proxyPort, proxyType);
        if (null == myHttpResponse) {
            return HttpStatus.SC_INTERNAL_SERVER_ERROR;
        }

        if (null == myHttpResponse.getStatusLine()) {
            return HttpStatus.SC_INTERNAL_SERVER_ERROR;
        }
        return myHttpResponse.getStatusLine().getStatusCode();
    }

    private static CloseableHttpClient getHttpClient() {
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new MyConnectionSocketFactory())
                .register("https", new MySSLConnectionSocketFactory(SSLContexts.createSystemDefault())).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg, new FakeDnsResolver());
        CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();
        return httpclient;
    }

    private static void entityToString(HttpEntity entity, StringBuilder htmlBuilder) throws IOException {
        if (entity != null) {
            InputStream stream = entity.getContent();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String readLine;
            while ((readLine = in.readLine()) != null) {
                htmlBuilder.append(readLine).append("\n");
            }
            EntityUtils.consume(entity);
        }
    }

    private static String getHtmlFromResponse(HttpResponse httpResponse) throws IOException {
        if (httpResponse == null || httpResponse.getEntity() == null) {
            return null;
        }
        StringBuilder htmlBuilder = new StringBuilder();
        entityToString(httpResponse.getEntity(), htmlBuilder);
        return StringEscapeUtils.unescapeJava(htmlBuilder.toString());
    }


    public static MyHttpResponse getHttpResponse(HttpRequestBase request,
                                                 Map<String, String> map,
                                                 String cookie) {
        setRequest(request, map, cookie);

        // 执行post请求操作，并拿到结果
        StringBuilder htmlBuilder = new StringBuilder();
        LOG.info("Executing request " + request + " via no proxy ");
        try (CloseableHttpResponse httpResponse = HttpClients.createDefault().execute(request)) {
            LOG.info("----------------------------------------");
            LOG.info("Response Status: " + String.valueOf(httpResponse.getStatusLine()));
            MyHttpResponse myHttpResponse = new MyHttpResponse();
            myHttpResponse.setHeaders(httpResponse.getAllHeaders());
            myHttpResponse.setHtml(getHtmlFromResponse(httpResponse));
            myHttpResponse.setStatusLine(httpResponse.getStatusLine());
            return myHttpResponse;
        } catch (ParseException | IOException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static void setRequest(HttpRequestBase request,
                                   Map<String, String> map,
                                   String cookie) {
        request.setHeader("Content-type", "application/x-www-form-urlencoded");
        request.setHeader("User-Agent", USER_AGENT); // 设置请求头消息User-Agent
        request.setHeader("Content-type", "application/x-www-form-urlencoded");
        request.addHeader("Cookie", cookie);

        if (request instanceof HttpPost) {
            List<NameValuePair> params = new ArrayList<>();
            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }

            try {
                ((HttpPost) request).setEntity(new UrlEncodedFormEntity(params, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                LOG.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }


    public static MyHttpResponse getHttpResponse(HttpRequestBase request,
                                                 Map<String, String> map,
                                                 String cookie,
                                                 String proxyHost, int proxyPort, String proxyType) {
        setRequest(request, map, cookie);

        StringBuilder htmlBuilder = new StringBuilder();

        if ("SOCKS".equalsIgnoreCase(proxyType)) {
            CloseableHttpClient httpclient = getHttpClient();
            try {
                InetSocketAddress socksaddr = new InetSocketAddress(proxyHost, proxyPort);
                HttpClientContext context = HttpClientContext.create();
                context.setAttribute("socks.address", socksaddr);


                LOG.info("Executing request " + request + " via SOCKS proxy " + socksaddr);
                try (CloseableHttpResponse httpResponse = httpclient.execute(request, context)) {
                    LOG.info("----------------------------------------");
                    LOG.info("Response Status: " + String.valueOf(httpResponse.getStatusLine()));
                    MyHttpResponse myHttpResponse = new MyHttpResponse();
                    myHttpResponse.setHeaders(httpResponse.getAllHeaders());
                    myHttpResponse.setHtml(getHtmlFromResponse(httpResponse));
                    myHttpResponse.setStatusLine(httpResponse.getStatusLine());
                    return myHttpResponse;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } finally {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        } else if ("HTTP".equalsIgnoreCase(proxyType)) {
            HttpClientBuilder hcBuilder = HttpClients.custom();
            HttpHost proxy = new HttpHost(proxyHost, proxyPort, "http");
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            hcBuilder.setRoutePlanner(routePlanner);
            CloseableHttpClient httpClient = hcBuilder.build();
            LOG.info("Executing request " + request + " via HTTP proxy " + proxy);

            try {
                HttpResponse httpResponse = httpClient.execute(request);
                LOG.info("----------------------------------------");
                LOG.info("Response Status: " + String.valueOf(httpResponse.getStatusLine()));
                MyHttpResponse myHttpResponse = new MyHttpResponse();
                myHttpResponse.setHeaders(httpResponse.getAllHeaders());
                myHttpResponse.setHtml(getHtmlFromResponse(httpResponse));
                myHttpResponse.setStatusLine(httpResponse.getStatusLine());
                return myHttpResponse;
            } catch (IOException e) {
                LOG.error(e.getMessage());
                e.printStackTrace();
                return null;
            } finally {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            LOG.error("Unsupported proxy type: " + proxyType);
            return null;
        }
    }

}

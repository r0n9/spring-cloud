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
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rong on 2017/11/29.
 */
public class MyHttpClient {
    private final static Logger LOG = LoggerFactory.getLogger(MyHttpClient.class);

    private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36";

    public static int testProxy(String proxyHost, int proxyPort, String proxyType) {
        HttpGet request = new HttpGet("http://www.baidu.com/");
        MyHttpResponse myHttpResponse = getHttpResponse(request, null, null, proxyHost, proxyPort, proxyType);
        if (null == myHttpResponse) {
            return HttpStatus.SC_INTERNAL_SERVER_ERROR;
        }

        if (null == myHttpResponse.getStatusLine()) {
            return HttpStatus.SC_INTERNAL_SERVER_ERROR;
        }
        return myHttpResponse.getStatusLine().getStatusCode();
    }

    private static CloseableHttpClient getHttpClientForSocksProxy() {
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
            BufferedReader in = new BufferedReader(new InputStreamReader(stream, Charset.forName("utf-8")));
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
                                                 List<Cookie> cookies) {
        setRequest(request, map);

        // 执行post请求操作，并拿到结果
        LOG.info("Executing request " + request + " via no proxy ");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
            LOG.info("----------------------------------------");
            LOG.info("Response Status: " + String.valueOf(httpResponse.getStatusLine()));
            MyHttpResponse myHttpResponse = new MyHttpResponse();
            myHttpResponse.setHeaders(httpResponse.getAllHeaders());
            myHttpResponse.setHtml(getHtmlFromResponse(httpResponse));
            myHttpResponse.setStatusLine(httpResponse.getStatusLine());
            return myHttpResponse;
        } catch (ParseException | IOException e) {
            LOG.error(e.getMessage());
            return null;
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void setRequest(HttpRequestBase request, Map<String, String> map) {
        request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        request.setHeader("Accept-Encoding", "gzip, deflate");
        request.setHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,ja;q=0.7");
        request.setHeader("User-Agent", USER_AGENT); // 设置请求头消息User-Agent

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
            }
        }
    }

    public static CloseableHttpClient getHttpClientWithProxy(HttpClientContext context, String proxyHost, int proxyPort, String proxyType) {
        BasicCookieStore basicCookieStore = new BasicCookieStore();
        if ("SOCKS".equalsIgnoreCase(proxyType)) {
            Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new MyConnectionSocketFactory())
                    .register("https", new MySSLConnectionSocketFactory(SSLContexts.createSystemDefault())).build();
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(reg, new FakeDnsResolver());

            CloseableHttpClient httpclient = HttpClients.custom()
                    .setConnectionManager(cm)
                    .setDefaultCookieStore(basicCookieStore)
                    .build();

            InetSocketAddress socksaddr = new InetSocketAddress(proxyHost, proxyPort);
            context.setAttribute("socks.address", socksaddr);

            return httpclient;

        } else if ("HTTP".equalsIgnoreCase(proxyType)) {
            HttpClientBuilder hcBuilder = HttpClients.custom();
            HttpHost proxy = new HttpHost(proxyHost, proxyPort, "http");
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            hcBuilder.setRoutePlanner(routePlanner);
            CloseableHttpClient httpClient = hcBuilder
                    .setDefaultCookieStore(basicCookieStore)
                    .build();

            return httpClient;
        } else {
            LOG.error("Unsupported proxy type: " + proxyType);
            return null;
        }
    }


    public static MyHttpResponse getHttpResponse(HttpRequestBase request,
                                                 Map<String, String> map,
                                                 List<Cookie> cookies,
                                                 String proxyHost, int proxyPort, String proxyType) {
        setRequest(request, map);

        StringBuilder htmlBuilder = new StringBuilder();
        HttpClientContext context = HttpClientContext.create();
        CloseableHttpClient httpclient = getHttpClientWithProxy(context, proxyHost, proxyPort, proxyType);
        try {
            LOG.info("Executing request " + request + " via " + proxyType + " proxy " + proxyHost + ":" + proxyPort);
            try (CloseableHttpResponse httpResponse = httpclient.execute(request, context)) {
                LOG.info("----------------------------------------");
                LOG.info("Response Status: " + String.valueOf(httpResponse.getStatusLine()));
                MyHttpResponse myHttpResponse = new MyHttpResponse();
                myHttpResponse.setHeaders(httpResponse.getAllHeaders());
                myHttpResponse.setHtml(getHtmlFromResponse(httpResponse));
                myHttpResponse.setStatusLine(httpResponse.getStatusLine());
                return myHttpResponse;
            } catch (IOException e) {
                LOG.error(e.getMessage());
                return null;
            }
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
    }


    public static boolean downloadImage(String imageUrl, String dirPath, String fileName) {
        boolean isSuccess;
        CloseableHttpResponse response;
        InputStream in;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            response = httpClient.execute(new HttpGet(imageUrl));
            in = response.getEntity().getContent();
            savePicToDisk(in, dirPath, fileName);
            isSuccess = true;
        } catch (IOException e) {
            LOG.error(e.getMessage());
            isSuccess = false;
        }

        return isSuccess;
    }


    /**
     * 将图片写到 硬盘指定目录下
     *
     * @param in
     * @param dirPath
     * @param fileName
     */
    public static void savePicToDisk(InputStream in, String dirPath, String fileName) {

        try {
            File dir = new File(dirPath);
            if (dir == null || !dir.exists()) {
                dir.mkdirs();
            }

            //文件真实路径
            String realPath = dirPath.concat(fileName);
            File file = new File(realPath);
            if (file == null || !file.exists()) {
                file.createNewFile();
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buf = new byte[1024];
                int len = 0;
                while ((len = in.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                fos.flush();
                fos.close();
            }

        } catch (IOException e) {
            LOG.error(e.getMessage());
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
    }

}

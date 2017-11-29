package vip.fanrong.common;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rong on 2017/11/29.
 */
public class MyHttpClient {
    final static Logger LOG = LoggerFactory.getLogger(MyHttpClient.class);

    public static String httpGet(String url) {
        HttpGet httpGet = new HttpGet(url);

        String body = "";
        try (CloseableHttpResponse httpResponse = HttpClients.createDefault().execute(httpGet)) {
            // 获取结果实体
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                // 按指定编码转换结果实体为String类型
                body = EntityUtils.toString(entity, "utf-8");
            }
            EntityUtils.consume(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return StringEscapeUtils.unescapeJava(body);
    }

    public static String httpPost(String url, Map<String, String> map, String cookie) {
        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
            // 设置header信息
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            // 添加cookie到头文件
            httpPost.addHeader("Cookie", cookie);
        } catch (UnsupportedEncodingException e) {
            LOG.error("Failed to set HttpPost.");
            return "";
        }

        // 执行post请求操作，并拿到结果
        String body = "";
        try (CloseableHttpResponse httpResponse = HttpClients.createDefault().execute(httpPost)) {
            // 获取结果实体
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                // 按指定编码转换结果实体为String类型
                body = EntityUtils.toString(entity, "utf-8");
            }
            // 消耗掉entity
            EntityUtils.consume(entity);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return StringEscapeUtils.unescapeJava(body);
    }


}

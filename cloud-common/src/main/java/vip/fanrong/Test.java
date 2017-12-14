package vip.fanrong;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.fanrong.common.MyHttpClient;
import vip.fanrong.common.MyHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Rong on 2017/11/29.
 */
public class Test {
    private final static Logger LOG = LoggerFactory.getLogger(Test.class);


    static class Toggle {
        String name;
        String aria;

        Toggle(String name, String aria) {
            this.name = name;
            this.aria = aria;
        }

        @Override
        public String toString() {
            return "Toggle{" +
                    "name='" + name + '\'' +
                    ", aria='" + aria + '\'' +
                    '}';
        }
    }

    static class Season {
        String name;
        String aria;

        public Season(String name, String aria) {
            this.name = name;
            this.aria = aria;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAria() {
            return aria;
        }

        public void setAria(String aria) {
            this.aria = aria;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Season season = (Season) o;

            if (name != null ? !name.equals(season.name) : season.name != null) return false;
            return aria != null ? aria.equals(season.aria) : season.aria == null;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (aria != null ? aria.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Season{" +
                    "name='" + name + '\'' +
                    ", aria='" + aria + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {

        MyHttpResponse response = MyHttpClient.getHttpResponse(new HttpGet("http://xiazai003.com/ZywCQ3"), null, null);

        Document doc = Jsoup.parse(response.getHtml());
        Element metaElement = doc.selectFirst("ul.tab-header.tab-side");

        Map<Season, List<Toggle>> seasonToToggles = new HashMap<>();

        for (Element li : metaElement.select("li:has(ul)")) {
            List<Toggle> toggles = new ArrayList<>();
            seasonToToggles.put(new Season(li.selectFirst("a").text(), li.selectFirst("a").attr("aria-controls")), toggles);
            int i = 0;
            for (Element toggle : li.select("li")) {
                if (0 == i++) {
                    continue;
                }
                Element a = toggle.selectFirst("a");
                toggles.add(new Toggle(a.text(), a.attr("aria-controls")));
            }
        }

        System.out.println(seasonToToggles);


        for (Season season : seasonToToggles.keySet()) {
            if ("周边资源".equalsIgnoreCase(season.getName())) {
                System.out.println("Ignore 周边资源");
                continue;
            }
            List<Toggle> toggles = seasonToToggles.get(season);
            System.out.println("dealing with " + season.getName());

            Element element = doc.getElementById(season.aria);
            for (Toggle toggle : toggles) {
                if(toggle.aria.endsWith("APP")){
                    continue;
                }
                System.out.println("dealing with " + season.getName() + " - " + toggle.name);
                Element elementOfToggle = element.selectFirst("div#" + toggle.aria);

                Elements eps = elementOfToggle.select("li:has(span.episode)");
                for(Element ep : eps){
                    System.out.println(ep.selectFirst("span.episode").text());
                    System.out.println(ep);

                    // TODO

                    break;
                }

                break;
            }
            break;
        }


    }

    public static void main2(String[] args) throws IOException, URISyntaxException {
        // "http://www.zimuzu.tv/resource/index_json/rid/11057/channel/tv"

        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        HttpGet getCookie = new HttpGet("http://www.zimuzu.tv/user/login");
        CloseableHttpResponse response1 = httpclient.execute(getCookie);
        System.out.println(StringEscapeUtils.unescapeJava(EntityUtils.toString(response1.getEntity())));
        response1.close();

        System.out.println(cookieStore.getCookies());
        System.out.println("~~~~~~~~~~~");

        String loginUrl = "http://www.zimuzu.tv/User/Login/ajaxLogin";

        HttpUriRequest postLogin = RequestBuilder.post().setUri(new URI(loginUrl))
                .addParameter("account", "8d787d3d-23dd")
                .addParameter("password", "e54711b01d9b")
                .addParameter("remember", "1")
                .addParameter("url_back", "http://www.zimuzu.tv/user/login")
                .build();
        CloseableHttpResponse response2 = httpclient.execute(postLogin);
        System.out.println(StringEscapeUtils.unescapeJava(EntityUtils.toString(response2.getEntity())));
        response2.close();

        System.out.println(cookieStore.getCookies());
        System.out.println("~~~~~~~~~~~");

        String getUserInfoUrl = "http://www.zimuzu.tv/user/login/getCurUserTopInfo";
        HttpUriRequest postExamMark = RequestBuilder.post().setUri(new URI(getUserInfoUrl))
                .build();
        CloseableHttpResponse response = httpclient.execute(postExamMark);
        HttpEntity entity = response.getEntity();
        String json = StringEscapeUtils.unescapeJava(EntityUtils.toString(entity));
        EntityUtils.consume(entity);
        System.out.println(json);

        System.out.println(cookieStore.getCookies());
        System.out.println("~~~~~~~~~~~");
    }

    public static void main1(String[] args) {
        HttpGet request = new HttpGet("http://www.baidu.com");
        MyHttpResponse response = MyHttpClient.getHttpResponse(request, null, null, "61.163.139.161", 80, "HTTP");

        System.out.println(response.getHtml());
    }
}

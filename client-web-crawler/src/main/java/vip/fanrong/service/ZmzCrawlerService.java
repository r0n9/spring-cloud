package vip.fanrong.service;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vip.fanrong.common.DateTimeUtil;
import vip.fanrong.common.MyHttpClient;
import vip.fanrong.common.MyHttpResponse;
import vip.fanrong.mapper.MovieResourceMapper;
import vip.fanrong.mapper.TvResourceMapper;
import vip.fanrong.mapper.ZmzAccountMapper;
import vip.fanrong.mapper.ZmzResourceTopMapper;
import vip.fanrong.model.*;

import java.io.IOException;
import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rong on 2017/9/11.
 */
@Service
public class ZmzCrawlerService {

    private static final Log LOGGER = LogFactory.getLog(ZmzCrawlerService.class);
    private static final DateTimeFormatter FORMATTER_SIMPLE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.of("GMT+08:00"));

    private static final Pattern URL_PATTERN_XIAZAI003 = Pattern.compile("((http://xiazai003\\.com/\\w+))");

    private static final Pattern URL_PATTERN_RESOURCE_ID = Pattern.compile("http://m.zimuzu.tv/resource/(\\d+)");

    @Autowired
    private ZmzResourceTopMapper zmzResourceTopMapper;

    @Autowired
    private ZmzAccountMapper zmzAccountMapper;

    @Autowired
    private MovieResourceMapper movieResourceMapper;

    @Autowired
    private TvResourceMapper tvResourceMapper;

    @Autowired
    private ProxyCrawlerService proxyCrawlerService;


    public MyHttpResponse getMyHttpResponse(ProxyConfig proxy,
                                            HttpRequestBase request,
                                            Map<String, String> map,
                                            List<Cookie> cookie) {
        MyHttpResponse response;
        if (null == proxy) {
            response = MyHttpClient.getHttpResponse(request, map, cookie);
        } else {
            response = MyHttpClient.getHttpResponse(request, map, cookie,
                    proxy.getHost(), proxy.getPort(), proxy.getType());
        }
        return response;
    }

    public List<ZmzResourceTop> getZmzResourceTops(ProxyConfig proxy) {
        HttpGet request = new HttpGet("http://m.zimuzu.tv/resource/top");
        MyHttpResponse response = getMyHttpResponse(proxy, request, null, null);
        if (null == response) {
            return null;
        }
        String html = response.getHtml();
        Date getTime = DateTimeUtil.getTimeNowGMT8();
        List<ZmzResourceTop> list = parseZmzResourceTops(html, getTime);
        String getTimeStr = ZonedDateTime.ofInstant(getTime.toInstant(), ZoneId.of("GMT+08:00")).format(FORMATTER_SIMPLE);
        LOGGER.info("成功获取最新资源数量为：" + list.size() + " 获取时间：" + getTimeStr);
        return list;
    }

    public int loadZmzResourceTops(ProxyConfig proxy) {
        List<ZmzResourceTop> list = getZmzResourceTops(proxy);
        int loaded = zmzResourceTopMapper.batchInsert(list);
        return loaded;
    }


    private List<ZmzResourceTop> parseZmzResourceTops(String html, Date getTime) {

        List<ZmzResourceTop> list = new ArrayList<>();
        Document doc = Jsoup.parse(html, "http://m.zimuzu.tv/");

        Element rankingBox1 = doc.getElementById("ranking-box-1"); // 美剧
        Element rankingBox2 = doc.getElementById("ranking-box-2"); // 电影
        Element rankingBox3 = doc.getElementById("ranking-box-3"); // 日剧

        parseZmzResourceTops(list, rankingBox1, getTime);
        parseZmzResourceTops(list, rankingBox2, getTime);
        parseZmzResourceTops(list, rankingBox3, getTime);

        return list;
    }


    private void parseZmzResourceTops(List<ZmzResourceTop> list, Element rankingBox, Date getTime) {
        Elements lis = rankingBox.getElementsByTag("li");
        for (Element li : lis) {
            Element count = li.selectFirst("span.count");
            Element type = li.selectFirst("span.type");
            Element img = li.selectFirst("img[data-src]");
            Element desc = li.selectFirst("p.desc");
            Element descEn = li.getElementsByClass("desc desc-en").get(0);

            ZmzResourceTop data = new ZmzResourceTop();
            data.setGetTime(getTime);
            data.setCount(Integer.valueOf(count.text()));
            data.setType(type.text());
            data.setSrc(desc.getElementsByTag("a").first().attr("abs:href"));
            data.setImgDataSrc(img.attr("data-src"));
            data.setName(desc.getElementsByTag("a").first().text());
            data.setNameEn(descEn.getElementsByTag("a").first().text());
            data.setProcessed(0);

            list.add(data);
        }
    }

    public int loadLatestTopMovieResources(ProxyConfig proxy) {
        List<ZmzResourceTop> list = zmzResourceTopMapper.selectLatest();
        int count = 0;
        for (ZmzResourceTop resourceTop : list) {
            if (!"电影".equalsIgnoreCase(resourceTop.getType())) continue;

            String name = resourceTop.getName();
            String nameEn = resourceTop.getNameEn();
            String imgUrl = resourceTop.getImgDataSrc();
            String src = resourceTop.getSrc();
            Matcher matcher = URL_PATTERN_RESOURCE_ID.matcher(src);
            if (!matcher.find()) continue;
            String resourceId = matcher.group(1);

            List<MovieResource.MovieResourceFile> files = movieResourceMapper.selectBySourceAndResourceId("zmz", resourceId);
            if (files == null || files.isEmpty()) {
                MovieResource movieResource = getMovieResourceByZmzResourceId(proxy, resourceId);
                if (movieResource == null || movieResource.getResources() == null || movieResource.getResources().isEmpty()) {
                    LOGGER.warn("Resource not found: resourceId=" + resourceId + " name=" + name);
                    continue;
                }
                movieResource.setImgUrl(imgUrl);
                movieResource.setName(name);
                movieResource.setNameChn(nameEn);
                LOGGER.info("New resource found: resourceId=" + resourceId + " name=" + name);
                movieResourceMapper.insert(movieResource);
                count++;
            }
        }
        return count;
    }


    class Toggle {
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

    class Season {
        String name;
        String aria;

        Season(String name, String aria) {
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

    private int getHashIndex(String source, String resourceId, String season, String episode, String toggle) {
        StringBuilder sb = new StringBuilder();
        sb.append(source).append(resourceId).append(season).append(episode).append(toggle);
        return sb.toString().hashCode();
    }

    public int loadLatestTopTVResources(ProxyConfig proxy) {
        List<ZmzResourceTop> list = zmzResourceTopMapper.selectLatest();
        int total = 0;
        for (ZmzResourceTop top : list) {
            if ("日剧".equalsIgnoreCase(top.getType()) || "美剧".equalsIgnoreCase(top.getType())
                    || "英剧".equalsIgnoreCase(top.getType()) || "德剧".equalsIgnoreCase(top.getType())
                    || "纪录片".equalsIgnoreCase(top.getType())) {
                total += loadTVResource(proxy, top);
            }
        }
        return total;
    }

    public int loadTVResource(ProxyConfig proxy, ZmzResourceTop zmzResourceTop) {

        Matcher matcher = URL_PATTERN_RESOURCE_ID.matcher(zmzResourceTop.getSrc());
        if (!matcher.find()) return 0;
        String resourceId = matcher.group(1);

        List<TvResource> resourceIndexes = tvResourceMapper.selectResourceIndex("zmz", resourceId);
        List<Integer> hashList = new ArrayList<>();
        if (null != resourceIndexes) {
            for (TvResource resourceIndex : resourceIndexes) {
                hashList.add(getHashIndex(resourceIndex.getSource(), resourceIndex.getResourceId(),
                        resourceIndex.getSeason(), resourceIndex.getEpisode(), resourceIndex.getToggle()));
            }
        }

        // e.g. "http://www.zimuzu.tv/resource/index_json/rid/11057/channel/tv"
        String sourceUrl = "http://www.zimuzu.tv/resource/index_json/rid/" + resourceId + "/channel/tv";

        MyHttpResponse response = MyHttpClient.getHttpResponse(new HttpGet(sourceUrl), null, null);


        if (null == response) {
            return 0;
        }

        matcher = URL_PATTERN_XIAZAI003.matcher(response.getHtml());

        if (!matcher.find()) {
            return 0;
        }

        sourceUrl = matcher.group(1);
        LOGGER.info("Resource page url: " + sourceUrl);
        if (null == proxy) {
            response = MyHttpClient.getHttpResponse(new HttpGet(sourceUrl), null, null);
        } else {
            response = MyHttpClient.getHttpResponse(new HttpGet(sourceUrl), null, null,
                    proxy.getHost(), proxy.getPort(), proxy.getType());
        }


        if (null == response) {
            return 0;
        }

        // 解析
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

//        System.out.println(seasonToToggles);

        int total = 0;
        for (Season season : seasonToToggles.keySet()) { // by season
            if ("周边资源".equalsIgnoreCase(season.getName())) {
                continue;
            }
            List<Toggle> toggles = seasonToToggles.get(season);
            LOGGER.info("Start to deal with " + season.getName());

            Element element = doc.getElementById(season.aria);
            for (Toggle toggle : toggles) { // by toggles
                List<TvResource> tvResourcesList = new ArrayList<>();

                if (toggle.aria.endsWith("APP")) {
                    continue;
                }
                LOGGER.info("Start to deal with " + zmzResourceTop.getName() + " - " + season.getName() + " - " + toggle.name);
                Element elementOfToggle = element.selectFirst("div#" + toggle.aria);

                Elements eps = elementOfToggle.select("li:has(span.episode)");
                for (Element ep : eps) {
                    String episodeName = ep.selectFirst("span.episode").text();
                    // 判断是否已经入库
                    if (hashList.contains(getHashIndex("zmz", resourceId, season.name, episodeName, toggle.name))) {
                        continue;
                    }

                    LOGGER.info(episodeName + " - new");
                    String fileName = ep.selectFirst("span.filename").text();
                    String fileSize = ep.selectFirst("span.filesize").text();

                    Elements elements = ep.select("a.btn");
                    if (null == elements) {
                        LOGGER.error("element not found: a.btn");
                        continue;
                    }

                    for (Element el : elements) {
                        String downloadLink = el.attr("href");
                        if (StringUtils.isBlank(downloadLink)) {
                            LOGGER.error("element not found: href");
                            continue;
                        }
                        if (null == el.selectFirst("p.desc")) {
                            LOGGER.error("element not found: p.desc");
                            continue;
                        }

                        TvResource tvResource = new TvResource();
                        tvResource.setResourceId(resourceId);
                        tvResource.setSource("zmz");
                        tvResource.setName(zmzResourceTop.getName());
                        tvResource.setAltName(zmzResourceTop.getNameEn());
                        tvResource.setEpisode(episodeName);
                        tvResource.setSeason(season.name);
                        tvResource.setToggle(toggle.name);
                        tvResource.setFileName(fileName);
                        tvResource.setFileSize(fileSize);
                        String nameChn = el.selectFirst("p.desc").text();
                        tvResource.setDownloadType(nameChn);
                        tvResource.setDownloadLink(downloadLink);
                        tvResource.setInsertTime(DateTimeUtil.getTimeNowGMT8());

                        tvResourcesList.add(tvResource);
//                        tvResourceMapper.insert(tvResource);
                    }
                }

                int loaded = tvResourceMapper.batchInsert(tvResourcesList);
                total += loaded;
            }
        }
        return total;
    }

    public MovieResource getMovieResourceByZmzResourceId(ProxyConfig proxy, String zmzResourceId) {
        String sourceUrl = "http://www.zimuzu.tv/resource/index_json/rid/" + zmzResourceId + "/channel/movie";
        HttpGet request = new HttpGet(sourceUrl);

        String html;
        MyHttpResponse response = getMyHttpResponse(proxy, request, null, null);

        if (null == response) {
            return null;
        } else {
            html = response.getHtml();
        }
        Matcher matcher = URL_PATTERN_XIAZAI003.matcher(html);

        if (matcher.find()) {
            sourceUrl = matcher.group(1);
            LOGGER.info("Resource page url: " + sourceUrl);
            request = new HttpGet(sourceUrl);
            if (null == proxy) {
                response = MyHttpClient.getHttpResponse(request, null, null);
            } else {
                response = MyHttpClient.getHttpResponse(request, null, null,
                        proxy.getHost(), proxy.getPort(), proxy.getType());
            }


            if (null == response) {
                return null;
            }
            return getMovieResource(response.getHtml(), zmzResourceId, "zmz");
        }

        return null;


    }


    private MovieResource getMovieResource(String html, String resouceId, String source) {
        if (StringUtils.isBlank(html)) {
            return null;
        }

        List<String> ids = new ArrayList<>();
//        ids.add("tab-g1-APP");
        ids.add("tab-g1-720P");
        ids.add("tab-g1-HR-HDTV");
        ids.add("tab-g1-MP4");
        ids.add("tab-g1-RMVB");
        ids.add("tab-g1-WEB-DL");
        ids.add("tab-g3-OST");

        Document doc = Jsoup.parse(html);
        List<ResourceFile> resourceFiles = new ArrayList<>();
        for (String id : ids) {
            ResourceFile resourceFile = new ResourceFile();
            Element element = doc.getElementById(id);
            if (element == null) {
                continue;
            }
            Element titleElement = element.selectFirst("div.title");
            String fileName = titleElement.selectFirst("span.filename").text();
            String fileSize = titleElement.selectFirst("span.filesize").text();

            resourceFile.setFileName(fileName);
            resourceFile.setFileSize(fileSize);
            Map<DownloadType, String> resources = new HashMap<>();
            resourceFile.setResources(resources);
            Elements elements = element.select("a.btn");
            if (null == elements) {
                continue;
            }

            for (Element el : elements) {
                String downloadLink = el.attr("href");
                if (StringUtils.isBlank(downloadLink)) {
                    continue;
                }
                if (null == el.selectFirst("p.desc")) {
                    continue;
                }
                String nameChn = el.selectFirst("p.desc").text();
                DownloadType type = DownloadType.valueOfNameChn(nameChn);
                resources.put(type, downloadLink);
            }
            resourceFiles.add(resourceFile);
        }

        MovieResource movieResource = new MovieResource();
        movieResource.setResourceId(resouceId);
        movieResource.setSource(source);
        movieResource.setResources(resourceFiles);
        movieResource.setInsertTime(DateTimeUtil.getTimeNowGMT8());
        return movieResource;
    }

    public ZmzAccount registerZmzAccountRandom(ProxyConfig proxy) {
        String uuid = UUID.randomUUID().toString();
        ZmzAccount zmzAccount = new ZmzAccount();
        Random random = new Random();
        int randomInt = (random.nextInt(3) + 1) + 10;
        zmzAccount.setEmail(String.valueOf(System.currentTimeMillis() / randomInt / 10) + "@qq.com");
        zmzAccount.setNickname(uuid.substring(0, 13));
        zmzAccount.setPassword(uuid.substring(24));
        zmzAccount.setSex(String.valueOf(random.nextInt(2) + 1));
        return registerZmzAccount(proxy, zmzAccount);
    }

    public ZmzAccount registerZmzAccount(ProxyConfig proxy, ZmzAccount zmzAccount) {

        // zmz 注册
        String url = "http://www.zimuzu.tv/user/reg"; // 初始页面
        HttpGet request = new HttpGet(url);
        String html;
        MyHttpResponse response = getMyHttpResponse(proxy, request, null, null);
        if (null == response) {
            return null;
        } else {
            html = response.getHtml();
        }
        Document doc = Jsoup.parse(html);
        Elements inputs = doc.getElementsByTag("input");
        Element hashInput = null;
        for (Element element : inputs) {
            if ("__hash__".equalsIgnoreCase(element.attr("name"))) {
                hashInput = element;
                break;
            }
        }
        String hashCode = hashInput.attr("value");

        String regUrl = "http://www.zimuzu.tv/User/Reg/saveReg";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", zmzAccount.getEmail());
        requestBody.put("nickname", zmzAccount.getNickname());
        requestBody.put("password", zmzAccount.getPassword());
        requestBody.put("repassword", zmzAccount.getPassword());
        requestBody.put("sex", zmzAccount.getSex());
        requestBody.put("__hash__", hashCode);

        HttpPost post = new HttpPost(regUrl);

        String result;
        response = getMyHttpResponse(proxy, request, requestBody, null);

        if (null == response) {
            return null;
        } else {
            result = response.getHtml();
        }

        result = Jsoup.parse(result).getElementById("tipsMsg").getElementsByTag("a").first().text();
        LOGGER.info(result);
        boolean isSuccess = StringUtils.contains(result, "注册成功");

        if (isSuccess) {
            zmzAccount.setIsValide(1);
            zmzAccount.setRegisterDate(DateTimeUtil.getTimeNowGMT8());
            zmzAccountMapper.insert(zmzAccount);
        } else {
            return null;
        }

        return zmzAccount;
    }

    public MyHttpResponse zmzAccountLogin(ProxyConfig proxy, ZmzAccount account) {
        HttpClientContext context = HttpClientContext.create();

        CloseableHttpClient httpclient;
        if (proxy == null) {
            httpclient = HttpClients.createDefault();
        } else {
            httpclient = MyHttpClient.getHttpClientWithProxy(context, proxy.getHost(), proxy.getPort(), proxy.getType());
        }

        try {

            HttpGet getCookie = new HttpGet("http://www.zimuzu.tv/user/login");
            CloseableHttpResponse response1 = httpclient.execute(getCookie);
            response1.close();

            String loginUrl = "http://www.zimuzu.tv/User/Login/ajaxLogin";

            HttpUriRequest postLogin = RequestBuilder.post().setUri(new URI(loginUrl))
                    .addParameter("account", account.getNickname())
                    .addParameter("password", account.getPassword())
                    .addParameter("remember", "1")
                    .addParameter("url_back", "http://www.zimuzu.tv/user/login")
                    .build();
            CloseableHttpResponse response2 = httpclient.execute(postLogin);
            LOGGER.info(StringEscapeUtils.unescapeJava(EntityUtils.toString(response2.getEntity())));
            response2.close();

            String getUserInfoUrl = "http://www.zimuzu.tv/user/login/getCurUserTopInfo";
            HttpUriRequest postExamMark = RequestBuilder.post().setUri(new URI(getUserInfoUrl))
                    .build();
            CloseableHttpResponse response = httpclient.execute(postExamMark);
            StatusLine statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            String json = StringEscapeUtils.unescapeJava(EntityUtils.toString(entity));
            EntityUtils.consume(entity);
            LOGGER.info(json);

            MyHttpResponse myHttpResponse = new MyHttpResponse();
            myHttpResponse.setHtml(json);
            myHttpResponse.setStatusLine(statusLine);
            return myHttpResponse;
        } catch (Exception e) {
            e.printStackTrace(); // TODO 这里需要解决，可以递归登陆今日未登陆成功账号
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String zmzAccountLoginAll() {
        LOGGER.info("Zmz accounts login is start...");

        List<ZmzAccount> accounts = zmzAccountMapper.getZmzAccountAll();
        LOGGER.info("Total size = " + accounts.size());


        ZmzAccount firstAccount = accounts.get(0);
        ProxyConfig proxy = proxyCrawlerService.getRandomValidatedProxy();
        while (!zmzAccountLoginTry(firstAccount, proxy)) {
            proxy = proxyCrawlerService.getRandomValidatedProxy();
        }

        LOGGER.info("Proxy found: " + proxy);
        ExecutorService service = Executors.newFixedThreadPool(10);
        List<FutureTask<Boolean>> tasks = new ArrayList<>();

        for (int i = 1; i < accounts.size(); i++) {
            FutureTask<Boolean> task = new FutureTask<>(new ZmzAccountLoginCaller(proxy, accounts.get(i)));
            service.submit(task);
            tasks.add(task);
        }

        int total = tasks.size() + 1;
        int success = 1;
        for (FutureTask<Boolean> task : tasks) {
            try {
                boolean result = task.get(120, TimeUnit.MINUTES);
                if (!result) {
                    LOGGER.error("Login task not success.");
                } else {
                    success++;
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                LOGGER.error("Exception for login task: " + task);
                LOGGER.error(e);
            }
        }

        service.shutdown();

        LOGGER.info("Zmz accounts login is end...");
        return String.valueOf(success) + "/" + String.valueOf(total);

    }

    class ZmzAccountLoginCaller implements Callable<Boolean> {
        private ProxyConfig proxy;
        private ZmzAccount account;

        ZmzAccountLoginCaller(ProxyConfig proxy, ZmzAccount account) {
            this.proxy = proxy;
            this.account = account;
        }

        @Override
        public Boolean call() throws Exception {
            MyHttpResponse response = zmzAccountLogin(proxy, account);
            if (response != null && response.getHtml() != null) {
                if (StringUtils.contains(response.getHtml(), "common_group_name")) {
                    LOGGER.info("Login succeeded for account: " + account);
                    account.setLastLoginDate(DateTimeUtil.getTimeNowGMT8());
                    zmzAccountMapper.update(account);
                    return true;
                }
            }
            return false;
        }
    }

    private boolean zmzAccountLoginTry(ZmzAccount account, ProxyConfig proxy) {
        ExecutorService service = null;
        try {
            service = Executors.newSingleThreadExecutor();
            FutureTask<Boolean> task = new FutureTask<>(new ZmzAccountLoginCaller(proxy, account));
            service.submit(task);

            try {
                return task.get(120, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                return false;
            } catch (ExecutionException e) {
                return false;
            } catch (TimeoutException e) {
                LOGGER.warn("Timeout for proxy: " + proxy);
                return false;
            }
        } finally {
            if (null != service) {
                service.shutdown();
            }
        }
    }

}

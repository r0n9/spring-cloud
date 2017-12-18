package vip.fanrong.mapper;

import vip.fanrong.model.KdsTopic;
import vip.fanrong.model.ProxyConfig;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by Rong on 2017/12/18.
 */
public class KdsTopicProvider {

    public String batchInsert(Map map) {
        List<KdsTopic> topics = (List<KdsTopic>) map.get("kdsTopicList");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO kds_topics ");
        sb.append("(title, link, img_link, reply, user, post_time, insert_time) ");
        sb.append("VALUES ");
        MessageFormat mf = new MessageFormat("(" +
                "#'{'kdsTopicList[{0}].title}, " +
                "#'{'kdsTopicList[{0}].link}, " +
                "#'{'kdsTopicList[{0}].imgLink}, " +
                "#'{'kdsTopicList[{0}].replyTo}, " +
                "#'{'kdsTopicList[{0}].userto}, " +
                "#'{'kdsTopicList[{0}].postTime}, " +
                "#'{'kdsTopicList[{0}].insertTime}" +
                ")");
        for (int i = 0; i < topics.size(); i++) {
            sb.append(mf.format(new Object[]{i}));
            if (i < topics.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}

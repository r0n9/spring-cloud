package vip.fanrong.mapper;

import vip.fanrong.model.ProxyConfig;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by Rong on 2017/12/4.
 */
public class ProxyConfigProvider {
    public String batchInsert(Map map) {
        List<ProxyConfig> users = (List<ProxyConfig>) map.get("proxyConfigList");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO proxy_config ");
        sb.append("(host, port, location, type, status, statusUpdateTime, insertTime) ");
        sb.append("VALUES ");
        MessageFormat mf = new MessageFormat("(" +
                "#'{'proxyConfigList[{0}].host}, " +
                "#'{'proxyConfigList[{0}].port}, " +
                "#'{'proxyConfigList[{0}].location}, " +
                "#'{'proxyConfigList[{0}].type}, " +
                "#'{'proxyConfigList[{0}].status}, " +
                "#'{'proxyConfigList[{0}].statusUpdateTime}, " +
                "#'{'proxyConfigList[{0}].insertTime}" +
                ")");
        for (int i = 0; i < users.size(); i++) {
            sb.append(mf.format(new Object[]{i}));
            if (i < users.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}

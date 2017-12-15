package vip.fanrong.mapper;

import vip.fanrong.model.ProxyConfig;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by Rong on 2017/12/4.
 */
public class TvResourceProvider {
    public String batchInsert(Map map) {
        List<ProxyConfig> users = (List<ProxyConfig>) map.get("tvResourceList");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO tv_resource ");
        sb.append("(source, resource_id, name, alt_name, season, episode, toggle, " +
                "file_name, file_size, download_type, download_link, insert_time) ");
        sb.append("VALUES ");
        MessageFormat mf = new MessageFormat("(" +
                "#'{'tvResourceList[{0}].source}, " +
                "#'{'tvResourceList[{0}].resourceId}, " +
                "#'{'tvResourceList[{0}].name}, " +
                "#'{'tvResourceList[{0}].altName}, " +
                "#'{'tvResourceList[{0}].season}, " +
                "#'{'tvResourceList[{0}].episode}, " +
                "#'{'tvResourceList[{0}].toggle}, " +
                "#'{'tvResourceList[{0}].fileName}, " +
                "#'{'tvResourceList[{0}].fileSize}, " +
                "#'{'tvResourceList[{0}].downloadType}, " +
                "#'{'tvResourceList[{0}].downloadLink}, " +
                "#'{'tvResourceList[{0}].insertTime} " +
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

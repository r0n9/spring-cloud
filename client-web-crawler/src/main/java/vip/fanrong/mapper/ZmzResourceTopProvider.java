package vip.fanrong.mapper;

import vip.fanrong.model.ZmzResourceTop;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by Rong on 2017/11/29.
 */
public class ZmzResourceTopProvider {
    public String batchInsert(Map map) {
        List<ZmzResourceTop> users = (List<ZmzResourceTop>) map.get("zmzResourceTopList");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO zmz_resource_top ");
        sb.append("(get_time, count, type, src, img_data_src, name, name_en, processed, process_time) ");
        sb.append("VALUES ");
        MessageFormat mf = new MessageFormat("(" +
                "#'{'zmzResourceTopList[{0}].getTime}, " +
                "#'{'zmzResourceTopList[{0}].count}, " +
                "#'{'zmzResourceTopList[{0}].type}, " +
                "#'{'zmzResourceTopList[{0}].src}, " +
                "#'{'zmzResourceTopList[{0}].imgDataSrc}, " +
                "#'{'zmzResourceTopList[{0}].name}, " +
                "#'{'zmzResourceTopList[{0}].nameEn}, " +
                "#'{'zmzResourceTopList[{0}].processed}, " +
                "#'{'zmzResourceTopList[{0}].processTime}" +
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

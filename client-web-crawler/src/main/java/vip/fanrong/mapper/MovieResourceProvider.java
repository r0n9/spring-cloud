package vip.fanrong.mapper;

import org.apache.commons.lang.StringUtils;
import vip.fanrong.model.DownloadType;
import vip.fanrong.model.MovieResource;
import vip.fanrong.model.ResourceFile;

import java.util.List;
import java.util.Map;

/**
 * Created by Rong on 2017/12/11.
 */
public class MovieResourceProvider {

    public String insert(Map map) {
        MovieResource movieResource = (MovieResource) map.get("movieResource");

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO movie_resource ");
        sb.append("(source, resource_id, name, alt_name, file_name, file_size, download_type, download_link, insert_time) ");
        sb.append("VALUES ");

        String source = replaceSingleQuote(movieResource.getSource());
        String resourceId = replaceSingleQuote(movieResource.getResourceId());
        String name = replaceSingleQuote(movieResource.getName());
        String altName = replaceSingleQuote(movieResource.getNameChn());
        String insertTime = "now()";

        String preValue = "('" + source + "', '" + resourceId + "', '" + name + "', '" + altName;

        List<ResourceFile> resources = movieResource.getResources();
        for (ResourceFile rf : resources) {
            String fileName = replaceSingleQuote(rf.getFileName());
            String fileSize = replaceSingleQuote(rf.getFileSize());
            Map<DownloadType, String> downloadTypeStringMap = rf.getResources();
            for (DownloadType type : downloadTypeStringMap.keySet()) {
                String link = replaceSingleQuote(downloadTypeStringMap.get(type));
                sb.append(preValue);
                sb.append("', '");
                sb.append(fileName);
                sb.append("', '");
                sb.append(fileSize);
                sb.append("', '");
                sb.append(type.name());
                sb.append("', '");
                sb.append(link);
                sb.append("', ");
                sb.append(insertTime);
                sb.append("),");
            }
        }

        return StringUtils.removeEnd(sb.toString(), ",");
    }

    private String replaceSingleQuote(String value) {
        return StringUtils.replace(value, "'", "''");
    }
}

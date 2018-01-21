package vip.fanrong.service;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import vip.fanrong.LuceneUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Rong on 2018/1/18.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class LuceneUtilsTests {

    @Test
    public void testIndex() throws IOException, ParseException {
        String id1 = "1";
        Document doc1 = new Document();
        doc1.add(new TextField("id", id1 + "", Field.Store.YES));
        doc1.add(new TextField("title", "中华人民共和国国歌" + "", Field.Store.YES));
        doc1.add(new TextField("content", "起来，不愿做奴隶的人们，把我们的血肉做成我们新的长城！中华民族到了最危险的时候，每个人民迫着发出最后的吼声！", Field.Store.YES));
        LuceneUtils.createIndex("test", id1,  LuceneUtils.fromLuceneDoc(doc1));

        String id2 = "2";
        Document doc2 = new Document();
        doc2.add(new TextField("id", id1 + "", Field.Store.YES));
        doc2.add(new TextField("title", "九儿" + "", Field.Store.YES));
        doc2.add(new TextField("content", "身边的那片田野啊. 手边的枣花香. 高粱熟来红满天. 九儿我送你去远方.", Field.Store.YES));

        LuceneUtils.createIndex("test", id2, LuceneUtils.fromLuceneDoc(doc2));

        List<String> queryFields = new ArrayList<>();
        queryFields.add("title");
        queryFields.add("content");
        List<vip.fanrong.model.Document> docs = LuceneUtils.search("test", "我", queryFields, 1, 10);

        for(vip.fanrong.model.Document doc : docs){
            System.out.println(doc.toString());
        }


    }
}

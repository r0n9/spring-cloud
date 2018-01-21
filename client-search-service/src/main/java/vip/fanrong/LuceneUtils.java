package vip.fanrong;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vip.fanrong.common.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LuceneUtils {

    private static Map<String, IndexReader> readerMap = new ConcurrentHashMap<>();
    private static Map<String, IndexWriter> writerMap = new ConcurrentHashMap<>();

    private static String luceneDir = "/home/rong/cloud/search/lucene";

    @Value("${lucene.path}")
    private void setLuceneDir(String luceneDir) {
        LuceneUtils.luceneDir = luceneDir;
        File file = new File(luceneDir);
        file.mkdirs();
    }

    public static void createIndex(String key, String id, vip.fanrong.model.Document doc) throws IOException {
        Document docLucene = toLuceneDoc(id, doc);
        IndexWriter writer = getWriter(key);
        writer.addDocument(docLucene);
        writer.commit();
    }

    public static Document toLuceneDoc(String id, vip.fanrong.model.Document doc) {
        Document docLucene = new Document();
        docLucene.add(new TextField("id", id, Field.Store.YES));
        for (vip.fanrong.model.Document.TextField textField : doc.getTextFields()) {
            if ("id".equalsIgnoreCase(textField.getName())) {
                continue;
            }
            docLucene.add(new TextField(textField.getName(), textField.getValue(), textField.getStore() ? Field.Store.YES : Field.Store.NO));
        }
        return docLucene;
    }

    public static vip.fanrong.model.Document fromLuceneDoc(Document docLucene) {
        vip.fanrong.model.Document doc = new vip.fanrong.model.Document();
        List<vip.fanrong.model.Document.TextField> fields = new ArrayList<>();
        doc.setTextFields(fields);
        docLucene.forEach(f -> fields.add(new vip.fanrong.model.Document.TextField(f.name(), f.stringValue(), true)));
        return doc;
    }

    public static void updateIndex(String key, String id, vip.fanrong.model.Document doc) throws IOException {
        IndexWriter writer = getWriter(key);
        Document docLucene = toLuceneDoc(id, doc);
        writer.updateDocument(new Term("id", id), docLucene); // 更新索引
        writer.commit();
    }

    public static void deleteIndex(String key, String id) throws IOException {
        IndexWriter writer = getWriter(key);
        writer.deleteDocuments(new Term("id", id)); // 删除索引
        writer.commit();
    }

    private static IndexWriter getWriter(String key) throws IOException {
        if (writerMap.get(key) == null) {
            synchronized (IndexWriter.class) {
                if (writerMap.get(key) == null) {
                    Directory dir = FSDirectory.open(Paths.get(luceneDir + "/" + key));
                    SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
                    IndexWriterConfig config = new IndexWriterConfig(analyzer);
                    IndexWriter writer = new IndexWriter(dir, config);
                    writerMap.put(key, writer);
                }
            }
        }
        return writerMap.get(key);
    }

    private static IndexReader getReader(String key) throws IOException {
        if (readerMap.get(key) == null) {
            synchronized (IndexReader.class) {
                if (readerMap.get(key) == null) {
                    Directory dir = FSDirectory.open(Paths.get(luceneDir + "/" + key));
                    IndexReader reader = DirectoryReader.open(dir);
                    readerMap.put(key, reader);
                }
            }
        }
        return readerMap.get(key);
    }

    public static List<vip.fanrong.model.Document> search(String key, String keyword, List<String> queryFileds, int pageStart, int pageSize) throws IOException, ParseException {

        if (key == null || keyword == null || queryFileds == null) {
            return null;
        }

        IndexReader reader = getReader(key);
        IndexSearcher searcher = new IndexSearcher(reader);
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        ScoreDoc lastBottom;
        TopDocs hits;
        List<vip.fanrong.model.Document> docs;
        try (SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer()) {
            for (String queryField : queryFileds) {
                QueryParser parser = new QueryParser(queryField, analyzer);//对文章标题进行搜索
                Query query = parser.parse(keyword);
                booleanQuery.add(query, Occur.SHOULD);
            }
        }
        lastBottom = getLastScoreDoc(pageStart, pageSize, booleanQuery.build(), searcher);
        hits = searcher.searchAfter(lastBottom, booleanQuery.build(), pageSize);
        docs = new ArrayList<>();
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            docs.add(fromLuceneDoc(doc));
        }
        return docs;
    }

    private static ScoreDoc getLastScoreDoc(int pageStart, int pageSize, Query query, IndexSearcher searcher) throws IOException {
        if (pageStart == 1) return null;//如果是第一页就返回空
        int num = pageSize * (pageStart - 1);//获取上一页的最后数量
        TopDocs tds = searcher.search(query, num);
        return tds.scoreDocs[num - 1];
    }

//    public static void main(String[] args) {
//        List<String> fields = new ArrayList<>();
//        fields.add("title");
//        fields.add("content");
//        System.out.println(JsonUtil.objectToJson(fields));
//    }
}

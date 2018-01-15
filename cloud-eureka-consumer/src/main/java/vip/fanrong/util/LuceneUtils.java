package vip.fanrong.util;

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
import vip.fanrong.model.Blog;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class LuceneUtils {

    private static Directory dir;
    private static IndexReader reader = null;
    private static IndexWriter writer = null;


    private static String luceneDir = "/home/rong/cloud/web/lucene";

    public static void createIndex(Blog blog) throws IOException {
        IndexWriter writer = getWriter();
        Document doc = new Document();
        doc.add(new TextField("id", blog.getId() + "", Field.Store.YES));
        doc.add(new TextField("title", blog.getTitle(), Field.Store.YES)); // 对标题做索引
        doc.add(new TextField("content", blog.getContent(), Field.Store.YES));// 对文章内容做索引
        writer.addDocument(doc);
        writer.commit();
    }

    public static void updateIndex(Blog blog) throws IOException {
        IndexWriter writer = getWriter();
        Document doc = new Document();
        doc.add(new TextField("id", blog.getId() + "", Field.Store.YES));
        doc.add(new TextField("title", blog.getTitle(), Field.Store.YES));
        doc.add(new TextField("content", blog.getContent(), Field.Store.YES));
        writer.updateDocument(new Term("id", String.valueOf(blog.getId())), doc); // 更新索引
        writer.commit();
    }

    // 用来更新或者删除索引的
    public static IndexWriter getWriter() throws IOException {
        if (writer == null) {
            synchronized (IndexWriter.class) {
                if (writer == null) {
                    dir = FSDirectory.open(Paths.get(luceneDir));
                    SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
                    IndexWriterConfig config = new IndexWriterConfig(analyzer);
                    writer = new IndexWriter(dir, config);
                }
            }
        }
        return writer;
    }

    public static IndexReader getReader() throws IOException {
        if (reader == null) {
            synchronized (IndexReader.class) {
                if (reader == null) {
                    dir = FSDirectory.open(Paths.get(luceneDir));
                    reader = DirectoryReader.open(dir);
                }
            }
        }
        return reader;
    }

    public static List<Blog> search(String keyword, int pageStart, int pageSize) throws IOException, ParseException {
        IndexReader reader = getReader();
        IndexSearcher searcher = new IndexSearcher(reader);
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
        QueryParser parser1 = new QueryParser("title", analyzer);//对文章标题进行搜索
        Query query1 = parser1.parse(keyword);
        QueryParser parser2 = new QueryParser("content", analyzer);//对文章内容进行搜索
        Query query2 = parser2.parse(keyword);
        booleanQuery.add(query1, Occur.SHOULD);
        booleanQuery.add(query2, Occur.SHOULD);
        ScoreDoc lastBottom = getLastScoreDoc(pageStart, pageSize, booleanQuery.build(), searcher);
        TopDocs hits = searcher.searchAfter(lastBottom, booleanQuery.build(), pageSize);
        List<Blog> blogs = new LinkedList<>();
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            Blog blog = new Blog();
            blog.setId(Long.parseLong(doc.get(("id"))));
            blog.setTitle(doc.get("title"));
            blog.setContent(doc.get("content"));
            blogs.add(blog);
        }
        return blogs;
    }

    private static ScoreDoc getLastScoreDoc(int pageStart, int pageSize, Query query, IndexSearcher searcher) throws IOException {
        if (pageStart == 1) return null;//如果是第一页就返回空
        int num = pageSize * (pageStart - 1);//获取上一页的最后数量
        TopDocs tds = searcher.search(query, num);
        return tds.scoreDocs[num - 1];
    }

    //待实现
    public static void deleteIndex(Long id) {
        // TODO 自动生成的方法存根

    }
}

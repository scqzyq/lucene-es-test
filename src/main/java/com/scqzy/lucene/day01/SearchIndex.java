package com.scqzy.lucene.day01;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

/**
 * @Description:
 * @Author 盛春强
 * @Date 2021/7/4 15:55
 */
public class SearchIndex {
    IndexReader indexReader;
    IndexSearcher indexSearcher;

    @Before
    public void init() throws IOException {
        indexReader =
                DirectoryReader.open(FSDirectory.open(new File("C:\\Users\\0\\Documents\\hehima\\lucene-project\\libr\\index").toPath()));
        indexSearcher = new IndexSearcher(indexReader);
    }

    @Test
    public void testRangeQuery() throws Exception {
        Query query = LongPoint.newRangeQuery("size", 0, 100);

        printResult(query);

    }

    private void printResult(Query query) throws IOException {
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println(topDocs.totalHits);
        for (ScoreDoc doc : topDocs.scoreDocs) {
            int docId = doc.doc;
            Document document = indexSearcher.doc(docId);
            System.out.println("==================");
            System.out.println(document.get("name"));
            System.out.println("---------------");
            System.out.println(document.get("path"));
            System.out.println("---------------");
            System.out.println(document.get("content"));
            System.out.println("---------------");
            System.out.println(document.get("size"));
            System.out.println("===================");
        }
        indexReader.close();
    }

    @Test
    public void testQueryParser() throws ParseException, IOException {
        QueryParser queryParser = new QueryParser("name", new IKAnalyzer());
        Query query = queryParser.parse("lucene是一个Java开发的全文检索工具包");
        printResult(query);
    }
}

package com.scqzy.lucene.day01;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @Description:
 * @Author 盛春强
 * @Date 2021/7/4 11:01
 */
public class LuceneFirst {
    @Test
    public void createIndex() throws Exception {
        Directory directory = FSDirectory.open(new File("C:\\Users\\0\\Documents\\hehima\\lucene-project\\libr\\index").toPath());
        IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(new IKAnalyzer()));
        File dir = new File("C:\\Users\\0\\Documents\\hehima\\lucene-project\\libr\\searchsource");
        File[] files = dir.listFiles();
        for (File f : files) {
            String fileName = f.getName();
            String filePath = f.getPath();
            String fileContent = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
            long size = FileUtils.sizeOf(f);
            Field fieldName = new TextField("name", fileName, Field.Store.YES);
            Field fieldPath = new StoredField("path", filePath);
            Field fieldContent = new TextField("content", fileContent, Field.Store.YES);
            Field fieldSizeValue = new LongPoint("size", size);
            Field fieldSizeStore = new StoredField("size", size);
            Document document = new Document();
            document.add(fieldName);
            document.add(fieldPath);
            document.add(fieldContent);
            document.add(fieldSizeValue);
            document.add(fieldSizeStore);
            indexWriter.addDocument(document);
        }
        indexWriter.close();
    }

    @Test
    public void searchIndex() throws Exception {
        Directory directory = FSDirectory.open(new File("C:\\Users\\0\\Documents\\hehima\\lucene-project\\libr\\index").toPath());
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        TermQuery query = new TermQuery(new Term("content", "字符串"));
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println(topDocs.totalHits);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc doc : scoreDocs) {
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
    public void testTokenStream() throws Exception {
//        Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
        TokenStream tokenStream = analyzer.tokenStream("", "公安局全文检索是将整本书java、整篇文章中的任意内容信息查找出来的检索，java" +
                "。它可以根据需要获得全文中有关章、节、段、句、词等信息，计算机程序通过扫描文章中的每一个词，对每一个词建立一个索引，指明该词在文章中出现的次数和位置，当用户查询时根据建立的索引查找，类似于通过字典的检索字表查字的过程。 " +
                "经过几年的发展，全文检索从最初的字符串匹配程序已经演进到能对超大文本java、java，语音、java，图像、活动影像等非结构化数据进行综合管理的大型软件。本教程只讨论文本检索。");
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            System.out.println(charTermAttribute.toString());
        }
        tokenStream.close();
    }


}

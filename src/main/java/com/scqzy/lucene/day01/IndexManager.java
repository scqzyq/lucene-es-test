package com.scqzy.lucene.day01;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

/**
 * @Description:
 * @Author 盛春强
 * @Date 2021/7/4 15:29
 */
public class IndexManager {
    IndexWriter indexWriter;

    @Before
    public void init() throws IOException {
        indexWriter = new IndexWriter(FSDirectory.open(new File("C:\\Users\\0\\Documents\\hehima\\lucene-project\\libr\\index"
        ).toPath()), new IndexWriterConfig(new IKAnalyzer()));
    }

    @Test
    public void addDocument() throws IOException {
        Document document = new Document();
        document.add(new TextField("name", "新添加的文件", Field.Store.YES));
        document.add(new TextField("content", "新添加的文件内容", Field.Store.NO));
        document.add(new StoredField("path", "C:\\Users\\0\\Documents\\hehima\\lucene-project\\libr\\searchsource"));
        indexWriter.addDocument(document);
        indexWriter.close();
    }


    @Test
    public void deleteAllDocument() throws IOException {
        indexWriter.deleteAll();
        indexWriter.close();
    }

    @Test
    public void deleteByQuery() throws IOException {
        long l = indexWriter.deleteDocuments(new Term("name", "apache"));
        System.out.println(l);
        indexWriter.close();
    }

    @Test
    public void updateDocument() throws Exception {
        Document document = new Document();
        document.add(new TextField("name", "更新文档", Field.Store.YES));
        document.add(new TextField("name1", "更新文档2", Field.Store.YES));
        document.add(new TextField("name2", "更新文档3", Field.Store.YES));

        indexWriter.updateDocument(new Term("name", "spring"), document);
        indexWriter.close();
    }
}

package com.scqzy.elasticsearch.day02;

import com.apifan.common.random.source.OtherSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Description:
 * @Author 盛春强
 * @Date 2021/7/5 16:23
 */
public class ElasticSearchClientTest {
    Settings settings;
    TransportClient client;

    @Before
    public void init() throws UnknownHostException {
        settings = Settings.builder().put("cluster.name", "my-elasticsearch").build();
        client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9301));
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9302));
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9303));
    }

    @Test
    public void createIndex() {
        client.admin().indices().prepareCreate("index_01")
                .get();
        client.close();

    }

    @Test
    public void setMappings() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("article")
                .startObject("properties")
                .startObject("id")
                .field("type", "long")
                .field("store", true)
                .endObject()
                .startObject("title")
                .field("type", "text")
                .field("store", true)
                .field("analyzer", "ik_smart")
                .endObject()
                .startObject("content")
                .field("type", "text")
                .field("store", true)
                .field("analyzer", "ik_smart")
                .endObject()
                .endObject()
                .endObject()
                .endObject();
        client.admin().indices()
                .preparePutMapping("index_01")
                .setType("article")
                .setSource(builder)
                .get();
        client.close();
    }

    @Test
    public void addDocument() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("id", 2L)
                .field("title", "如何在我的应用启动界面实现「开屏广告」")
                .field("content", "开屏广告是一种在应用启动时且在应用主界面显示之前需要被展示的广告。一般是5s展示时间，广告展示时间结束后自动进入应用，用户可以点击跳过按钮直接进入主界面。")
                .endObject();
        client.prepareIndex().setIndex("index_01")
                .setType("article")
                .setId("2")
                .setSource(builder)
                .get();
        client.close();
    }

    @Test
    public void addDocument1() throws IOException {
        Article article = new Article();
        article.setId(3L);
        article.setTitle("用电脑的方式思考");
        article.setContent("生活就是做事，就是一件一件的事情叠加，吃饭是事情，睡觉是事情，工作是事情，这样一件件事情累积起来构成生活。");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonDocument = objectMapper.writeValueAsString(article);
        System.out.println(jsonDocument);
        client.prepareIndex("index_01", "article", "3")
                .setSource(jsonDocument, XContentType.JSON)
                .get();
        client.close();
    }

    @Test
    public void addDocument2() throws IOException {
        for (long i = 101L; i < 10000L; i++) {
            Article article = new Article();
            article.setId(i);
            article.setTitle(OtherSource.getInstance().randomChineseSentence());
            article.setContent(OtherSource.getInstance().randomChineseSentence());
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonDocument = objectMapper.writeValueAsString(article);
            System.out.println(jsonDocument);
            client.prepareIndex("index_01", "article", i+"")
                    .setSource(jsonDocument, XContentType.JSON)
                    .get();
        }
        client.close();
    }
}

package com.scqzy.elasticsearch.day02;

import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Description:
 * @Author 盛春强
 * @Date 2021/7/6 9:59
 */
public class SearchIndex {
    Settings settings;
    TransportClient client;

    @Before
    public void init() throws UnknownHostException {
        settings = Settings.builder().put("cluster.name", "my-elasticsearch").build();
        client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9301))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9302))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9303));
    }

    private <T extends AbstractQueryBuilder> void searchAndPrint(T queryBuilder) {
        SearchResponse searchResponse = client
                .prepareSearch("index_01")
                .setTypes("article")
                .setQuery(queryBuilder)
                .setFrom(0)
                .setSize(25)
                .get();
        SearchHits searchHits = searchResponse.getHits();
        System.out.println("count:" + searchHits.getTotalHits());
        for (SearchHit searchHit : searchHits) {
            System.out.println(searchHit.getSourceAsString());
        }
        client.close();
    }

    private <T extends AbstractQueryBuilder> void searchAndPrintHighlight(T queryBuilder, String highlightField) {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(highlightField);
        highlightBuilder.preTags("<em>");
        highlightBuilder.postTags("</em>");
        SearchResponse searchResponse = client
                .prepareSearch("index_01")
                .setTypes("article")
                .setQuery(queryBuilder)
                .setFrom(0)
                .setSize(10)
                .highlighter(highlightBuilder)
                .get();
        SearchHits searchHits = searchResponse.getHits();
        System.out.println("count:" + searchHits.getTotalHits());
        for (SearchHit searchHit : searchHits) {
            System.out.println(searchHit.getHighlightFields());
        }
        client.close();
    }

    @Test
    public void testSearchById() {
        IdsQueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds("1", "2");
        searchAndPrint(queryBuilder);

    }



    @Test
    public void testSearchByTerm() {
        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("title", "教你");
        searchAndPrint(queryBuilder);
    }

    @Test
    public void testSearchByQueryString() {
        QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery("有些广告成年犹豫不决竹筒呢？只要这个横批煞风景合法权益。").defaultField("title");
        searchAndPrintHighlight(queryBuilder,"title");
    }
}

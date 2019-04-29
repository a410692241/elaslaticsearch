package com.wei.elaslaticsearch;

import com.google.gson.Gson;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class EsClientTest {
    @Test
    public void name() throws IOException {
        RestHighLevelClient highLevelClient = ESClientFactory.getHighLevelClient();
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("full_name", "上仙肉铺"));//对指定字段设置ik分词器
        searchSourceBuilder.size(3000);
        searchSourceBuilder.from(0);
        searchSourceBuilder.sort("category_id", SortOrder.DESC);
        //指定高亮字段
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightTitle = new HighlightBuilder.Field("full_name");
        //修改高亮前缀（默认http标签）
        highlightBuilder.preTags("<p style='red'>");
        //修改高亮后缀（默认http标签）
        highlightBuilder.postTags("</p>");
        highlightBuilder.field(highlightTitle);
        searchSourceBuilder.highlighter(highlightBuilder);
        searchRequest.source(searchSourceBuilder);
        searchRequest.indices("goods_sku_index");
//        searchRequest.types("goods");
        SearchResponse response = highLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {

            //获取商铺名字(关键字高亮)
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField descHightField = highlightFields.get("full_name");
            Text[] fragments = descHightField.getFragments();
            StringBuilder sb = new StringBuilder();
            for (Text fragment : fragments) {
                sb.append(fragment);
            }
            System.out.println(sb.toString());

        }


    }
}

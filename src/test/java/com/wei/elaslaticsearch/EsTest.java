package com.wei.elaslaticsearch;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EsTest {
    private String type = "index";
    private String index = "fullText";
    public static final String HOST = "localhost";
    public static final int PORT = 9300;
    private static Client client = null;

    private Logger logger = LoggerFactory.getLogger(EsTest.class);

    @Before
    public void openConnect() throws UnknownHostException {

        client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddresses(
                new InetSocketTransportAddress(InetAddress.getByName(HOST), PORT));
    }



    /**
     * 单个传入索引
     */
    @Test
    public void addIndex() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "别克");
        IndexResponse response = client.prepareIndex(index, type).setSource(map).get();

        logger.info("map索引名称:" + response.getIndex() + "\n map类型:" + response.getType()
                + "\n map文档ID:" + response.getId() + "\n当前实例map状态:" + response.status());
    }

    @Test
    public void batchAddIndex() {
        // 创建索引
        BulkRequestBuilder builder = client.prepareBulk();

        for (int i = 0; i < 5; i++) {
            Person p = new Person();
            p.setId(UUID.randomUUID().toString());
            p.setAge(20);
            p.setIsStudent(false);
            p.setSex("男");
            p.setName("小别克听老别克讲别克的故事");

            String source = new Gson().toJson(p);

            IndexRequest request = client.prepareIndex().setIndex(index)
                    .setType(type).setId(p.getId()).setSource(source)
                    .request();

            builder.add(request);
        }

        BulkResponse bResponse = builder.execute().actionGet();
        if (bResponse.hasFailures()) {
            Assert.fail("创建索引出错！");
        }
    }


    /**
     * 从索引库获取数据
     *
     * @return void
     * @Title: getData1
     * @author sunt
     * @date 2017年11月23日
     */
    @Test
    public void getData() {
        GetResponse getResponse = client.prepareGet(index, type, "AWoLf_0Mjmo9g6X8Q0yl").get();
        logger.info("索引库的数据:" + getResponse.getSourceAsString());
    }

    /**
     * version是每一次新增默认是1,之后每次修改都会+1
     */
    @Test
    public void version() {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<>();
        map.put("username", "hu");
        map.put("password", "a123456");
        IndexResponse indexResponse = client.prepareIndex(index, type, "AWoLf_0Mjmo9g6X8Q0yl").setSource(gson.toJson(map)).get();
        System.out.println("version是" + indexResponse.getVersion());

    }



    /**
     * 解析索引之中的键值对
     */
    @Test
    public void getJsonData() {
        GetResponse response = client.prepareGet(index, type, "1").execute().actionGet();
        String sourceAsString = response.getSourceAsString();
        Gson gson = new Gson();
        TypeToken<Map> mapTypeToken = new TypeToken<Map>() {
        };

        Map map = gson.fromJson(sourceAsString, mapTypeToken.getType());
        System.out.println("username:" + map.get("username"));
        System.out.println("password:" + map.get("password"));
    }



    /**
     * 索引内容多条件匹配(或的关系)
     */
    @Test
    public void multiMatchQuery() {
        // 检索
        QueryBuilder qb = QueryBuilders.multiMatchQuery("别克", "id", "name");
        SearchResponse searchResponse = client.prepareSearch(index)
                .setTypes(type).setQuery(qb).setFrom(0).setSize(12)
                .execute().actionGet();
        searchFunction(searchResponse);


    }

    /**
     * 模糊查找短语的索引
     */
    @Test
    public void matchQuery() {
        // 检索
        QueryBuilder qb = QueryBuilders.matchQuery("name","小别克老别克");
        SearchResponse searchResponse = client.prepareSearch(index)
                .setTypes(type).setQuery(qb).setFrom(0).setSize(12)
                .execute().actionGet();
        searchFunction(searchResponse);


    }

    /**
     * 索引内容单条件匹配(对比matchQuery,这家伙支持跨词,中间少了字也能查出)
     */
    @Test
    public void matchPhraseQuery() {
        // 检索
        QueryBuilder qb1 = QueryBuilders.matchPhraseQuery("name", "小别克老别克");
        SearchResponse searchResponse = client.prepareSearch(index)
                .setTypes(type).setQuery(qb1).setFrom(0).setSize(12)
                .execute().actionGet();

        searchFunction(searchResponse);
    }




    /**
     * 查找所有的索引
     */
    @Test
    public void matchAllQuery() {
        // 检索
        QueryBuilder qb = QueryBuilders.matchAllQuery();
        SearchResponse searchResponse = client.prepareSearch(index)
                .setTypes(type).setQuery(qb).setFrom(0).setSize(12)
                .execute().actionGet();
        searchFunction(searchResponse);


    }
    /**
     * es通过类型删除
     */
    @Test
    public void delete() {
        DeleteResponse deleteResponse = client.prepareDelete(index, type, "AWoLf_0Mjmo9g6X8Q0yl").execute().actionGet();
        System.out.println("删除的索引是" + deleteResponse.getIndex());
    }

    /**
     * es通过索引id索引删除
     */
    @Test
    public void deleteById() {
        DeleteResponse deleteResponse = client.prepareDelete(index, type, "AWoLf_0Mjmo9g6X8Q0yl").execute().actionGet();
        System.out.println("删除的索引是" + deleteResponse.getIndex());
    }




/*-----------------------------------------------------以下演示java操作中对ik中文分词器的使用-------------------------------------------------------------------------*/


    /**
     * 新增一个索引,要求mapping指定为ik_work_max,这样通过ik分词的查询才能有效
     */
    @Test
    public void createContent() throws Exception {
        createMapping(index, type);

        HashMap<String, Object> map = new HashMap<>();
        map.put("content", "美国留给伊拉克的是个烂摊子吗");
        client.prepareIndex(index, type, "1").setSource(map).execute().actionGet();

        map.put("content", "公安部：各地校车将享最高路权");
        client.prepareIndex(index, type, "2").setSource(map).execute().actionGet();

        map.put("content", "中韩渔警冲突调查：韩警平均每天扣1艘中国渔船");
        client.prepareIndex(index, type, "3").setSource(map).execute().actionGet();

        map.put("content", "中国驻洛杉矶领事馆遭亚裔男子枪击 嫌犯已自首");
        client.prepareIndex(index, type, "4").setSource(map).execute().actionGet();

    }

    /**
     * 演示ik分词器的分词操作
     */
    @Test
    public void ikAnalyzer() {
        AnalyzeRequest analyzeRequest = new AnalyzeRequest().analyzer("ik_smart").text("中国人民从此站起来了");
        List<AnalyzeResponse.AnalyzeToken> tokens = client.admin().indices().analyze(analyzeRequest).actionGet().getTokens();
        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            System.out.println(token.getTerm());
        }
    }

    /**
     * 通过ik分词器操作查询条件,并且将查询结果高亮显示
     */
    @Test
    public void analyze() {
        MatchQueryBuilder match = new MatchQueryBuilder("content", "中国美国公安部").analyzer("ik_max_word");
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("*");
        highlightBuilder.preTags("<p style='color:red'>");
        highlightBuilder.postTags("</p>");
        SearchResponse searchResponse = client.prepareSearch(index).setTypes(type).setQuery(match).highlighter(highlightBuilder).setExplain(true).execute().actionGet();
        searchHightLightFunction(searchResponse);


    }


    /**
     * 创建mapping(feid("indexAnalyzer","ik_max_word")该字段分词IK索引 ；feid("searchAnalyzer","ik_max_word")该字段分词ik查询；具体分词插件请看IK分词插件说明)
     * @param indices 索引名称；
     * @param mappingType 类型
     * @throws Exception
     */
    public void createMapping(String indices,String mappingType)throws Exception{
        client.admin().indices().prepareCreate(indices).execute().actionGet();
        new XContentFactory();
        XContentBuilder builder=XContentFactory.jsonBuilder()
                .startObject()
                .startObject(mappingType)
                .startObject("properties")
                .startObject("title").field("type", "string").field("store", "yes").field("analyzer","ik_max_word").field("index","analyzed").endObject()
                .startObject("content").field("type", "string").field("store", "yes").field("analyzer","ik_max_word").field("index","analyzed").endObject()
                .endObject()
                .endObject()
                .endObject();
        PutMappingRequest mapping = Requests.putMappingRequest(indices).type(mappingType).source(builder);

        client.admin().indices().putMapping(mapping).actionGet();
        client.close();
    }

    private void searchFunction(SearchResponse searchResponse) {
        SearchHits hits = searchResponse.getHits();
        if (hits == null || hits.totalHits == 0) {
            logger.error("找不到结果啊!");
        }
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }

    private void searchHightLightFunction(SearchResponse searchResponse) {
        SearchHits hits = searchResponse.getHits();
        if (hits == null || hits.totalHits == 0) {
            logger.error("找不到结果啊!");
        }
        for (SearchHit hit : hits) {
            System.out.println(hit.getHighlightFields());
        }
    }



    @After
    public void closeConnect() {
        if (client != null) {
            logger.info("执行关闭连接操作...");
            client.close();
        }
    }

}

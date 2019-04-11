package com.wei.elaslaticsearch;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class EsTest {

    public static final String HOST = "localhost";
    public static final int PORT = 9300;
    private Client client = null;

    private Logger logger = LoggerFactory.getLogger(EsTest.class);

    @Before
    public void openConnect() throws UnknownHostException {

        client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddresses(
                                                 new InetSocketTransportAddress(InetAddress.getByName(HOST),PORT));
    }

    @After
    public void closeConnect() {
        if (client != null) {
            logger.info("执行关闭连接操作...");
            client.close();
        }
    }
    /**
     * 创建索引-传入Map对象
     * @Title: addIndex3
     * @author sunt
     * @date 2017年11月23日
     * @return void
     */
    @Test
    public void addIndex() {
        Map<String, Object> map = new HashMap<String,Object>();
        map.put("userName", "张三");
        map.put("sendDate", new Date());
        map.put("msg", "你好李四");

        IndexResponse response = client.prepareIndex("momo", "tweet").setSource(map).get();

        logger.info("map索引名称:" + response.getIndex() + "\n map类型:" + response.getType()
                + "\n map文档ID:" + response.getId() + "\n当前实例map状态:" + response.status());
    }

    /**
     * 从索引库获取数据
     * @Title: getData1
     * @author sunt
     * @date 2017年11月23日
     * @return void
     */
    @Test
    public void getData() {
        GetResponse getResponse = client.prepareGet("momo", "tweet", "1").get();
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
        IndexResponse indexResponse = client.prepareIndex("momo", "tweet").setId("1").setSource(gson.toJson(map)).get();
        System.out.println("version是" + indexResponse.getVersion());

    }

    /**
     * es索引删除
     */
    @Test
    public void delete() {
        DeleteResponse deleteResponse = client.prepareDelete("momo", "tweet", "1").execute().actionGet();
        System.out.println("删除的索引是" + deleteResponse.getIndex());
    }

    /**
     * 解析索引之中的键值对
     */
    @Test
    public void getJsonData() {
        GetResponse response = client.prepareGet("momo", "tweet", "1").execute().actionGet();
        String sourceAsString = response.getSourceAsString();
        Gson gson = new Gson();
        TypeToken<Map> mapTypeToken = new TypeToken<Map>(){};

        Map map = gson.fromJson(sourceAsString, mapTypeToken.getType());
        System.out.println("username:" + map.get("username"));
        System.out.println("password:" + map.get("password"));
    }

    /**
     * 分词
     */
    @Test
    public void analyze() {

    }
}

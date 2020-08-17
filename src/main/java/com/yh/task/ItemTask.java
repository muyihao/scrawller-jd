package com.yh.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.yh.pojo.Item;
import com.yh.service.ItemService;
import com.yh.utils.HttpClientUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;

//使用定时任务，可以定时抓取最新的数据
@Component
public class ItemTask {
    @Autowired
    private ItemService itemService;
    @Autowired
    private HttpClientUtils httpClientUtils;

    public static final ObjectMapper MAPPER = new ObjectMapper();


    //设置定时任务执行完成后，再间隔100秒执行一次
    @Scheduled(fixedDelay = 1000 * 100)
    public void itemTask() throws Exception {

        //声明需要解析的资源地址
        String url = "https://search.jd.com/Search?keyword=%E6%89%8B%E6%9C%BA&s=51&click=0&page=";
        //遍历执行，获取所有的数据
        for (int i = 1; i < 4; i = i + 2) {
            //发起请求进行访问，获取页面数据,先访问第一页
            String html = this.httpClientUtils.getHtml(url + i);

            //解析页面数据，保存数据到数据库中
            this.parseHtml(html);

        }
        System.out.println("执行完成");
    }

    private void parseHtml(String html) throws IOException {
        //使用jsoup解析页面
        Document document = Jsoup.parse(html);
        //获取商品数据
        Elements spus = document.select("div#J_goodsList > ul > li");
        //遍历商品spu数据
        for (Element spuEle : spus) {
            //获取商品spu

            Long spuId = spuEle.attr("data-spu")==""?0L:Long.parseLong(spuEle.attr("data-spu"));

//获取商品sku数据
            Elements skus = spuEle.select("li.ps-item img");
            for (Element skuEle : skus) {
                long skuId = Long.parseLong(skuEle.attr("data-sku"));

                //判断商品是否被抓取过，可以根据sku判断
                Item param = new Item();
                param.setSku(skuId);
                List<Item> list = this.itemService.findAll(param);
                //判断是否查询到结果
                if (list.size() > 0) {
                    //如果有结果，表示商品已下载，进行下一次遍历
                    continue;
                }
                //保存商品数据，声明商品对象
                Item item = new Item();
                //商品spu
                item.setSpu(spuId);
                //商品sku
                item.setSku(skuId);
                //商品url地址
                item.setUrl("https://item.jd.com/" + skuId + ".html");
                //创建时间
                item.setCreated(new Date());
                //修改时间
                item.setUpdated(item.getCreated());
                //获取商品标题
                String itemHtml = this.httpClientUtils.getHtml(item.getUrl());
                String title = Jsoup.parse(itemHtml).select("div.sku-name").text();
                item.setTitle(title);
                //获取商品价格
                String priceUrl = "https://p.3.cn/prices/mgets?skuIds=J_"+skuId;
                String priceJson = this.httpClientUtils.getHtml(priceUrl);
                //解析json数据获取商品价格
                double price = MAPPER.readTree(priceJson).get(0).get("p").asDouble();

                item.setPrice(price);
                //获取图片地址
                //有时候程序获取的属性和浏览器上看见的不一样
                //获取图片地址
                String picUrl = "https:" + skuEle.attr("data-lazy-img").replace("/n9/","/n1/");
                System.out.println(picUrl);

                //下载图片
                String picName = this.httpClientUtils.getImage(picUrl);
                item.setPic(picName);
                //保存每个sku商品数据
                this.itemService.save(item);
            }

        }

    }
}

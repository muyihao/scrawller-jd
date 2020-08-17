package com.yh.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

//我们需要经常使用HttpClient，所以需要进行封装，方便使用
@Component
public class HttpClientUtils {
    //连接池
    private PoolingHttpClientConnectionManager cm;

    public HttpClientUtils() {
        this.cm = new PoolingHttpClientConnectionManager();
        //    设置最大连接数
        cm.setMaxTotal(200);

        //    设置每个主机的并发数
        cm.setDefaultMaxPerRoute(20);

    }
    //获取内容
    public String getHtml(String url) {
        //获取httpclient
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
        //设置请求路径
        HttpGet httpGet = new HttpGet(url);
        // 设置请求参数RequestConfig
        httpGet.setConfig(this.getConfig());
        //设置请求头，不然会被判定为爬虫，从而就会被拦截
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36");
        //发起请求
        CloseableHttpResponse response=null;
        try {
            response = httpClient.execute(httpGet);
            String content=null;
            if (response.getStatusLine().getStatusCode()==200) {
                //如果响应成功则返回响应体里面的html
                // 如果response.getEntity获取的结果是空，在执行EntityUtils.toString会报错
                // 需要对Entity进行非空的判断
                String html=null;
                if (response.getEntity() != null) {
                    html = EntityUtils.toString(response.getEntity(), "UTF-8");
                }
                return html;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (response != null) {
                    // 关闭连接
                    response.close();
                }
                // 不能关闭，现在使用的是连接管理器
                // httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    //获取图片
    public String getImage(String url) {
        // 获取HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();

        // 声明httpGet请求对象
        HttpGet httpGet = new HttpGet(url);
        // 设置请求参数RequestConfig
        httpGet.setConfig(this.getConfig());

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                // 获取文件类型
                String extName = url.substring(url.lastIndexOf("."));
                // 使用uuid生成图片名
                String imageName = UUID.randomUUID().toString() + extName;
// 声明输出的文件
                OutputStream outstream = new FileOutputStream(new File("E:\\tempImage\\" + imageName));
                // 使用响应体输出文件
                response.getEntity().writeTo(outstream);

                // 返回生成的图片名
                return imageName;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (response != null) {
                    // 关闭连接
                    response.close();
                }
                // 不能关闭，现在使用的是连接管理器
                // httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

        //获取请求参数对象
    private RequestConfig getConfig() {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(1000)// 设置创建连接的超时时间
                .setConnectionRequestTimeout(500) // 设置获取连接的超时时间
                .setSocketTimeout(10000) // 设置连接的超时时间
                .build();

        return config;
    }


}

package org.lxq.shortlink.project.service.impl;

import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.lxq.shortlink.project.service.UrlTitleService;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * url标题接口实现层
 */
@Service
public class UrlTitleServiceImpl implements UrlTitleService {
    /**
     * 根据Url获取标题
     * @param url 目标网站地址
     * @return 网站标题
     */
    @Override
    @SneakyThrows
    public String getTitleByUrl(String url)  {
        URL tagetUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) tagetUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Document document = Jsoup.connect(url).get();
            return document.title();
        }

        return "Erro while fetching title";


    }
}

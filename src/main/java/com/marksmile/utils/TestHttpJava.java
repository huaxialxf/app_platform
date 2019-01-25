package com.marksmile.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestHttpJava {
    public static void test(String domain) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL("https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxcheckurl?requrl=http%3A%2F%2F" + domain
                + "&skey=%40crypt_d782a6e8_2a873ba0402d7c8758cb4f61e73686b1&deviceid=e490944074626703&pass_ticket=undefined&opcode=2&scene=1&username=@170ec19731d1604c3762708a7d687969")
                        .openConnection();
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setConnectTimeout(60 * 1000);
        conn.setReadTimeout(60 * 1000);
        conn.setRequestProperty("Host", "wx.qq.com");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Cache-Control", "max-age=0");
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6726.400 QQBrowser/10.2.2265.400");

        conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
        conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
        conn.setRequestProperty("Cookie",
                "cuid=729575941; RK=OCfPz2naGH; pac_uid=1_350363013; pgv_pvi=3070366720; tvfe_boss_uuid=5d955bfd5f7ebcc2; o_cookie=350363013; pgv_pvid=1694995130; ptcz=c5a8aa258d5a67b878d75862a24e562ebd7994b93b9c19c3e74ada6c03e8a2b7; luin=o0350363013; pt2gguin=o0350363013; lskey=000100009df6c406d8f6ae295b80ddab924f2252b5b3f10bac77751f5e61c7a40f340a4caaec9e8bae1743de; _qpsvr_localtk=1543497558336; mm_lang=zh_CN; MM_WX_NOTIFY_STATE=1; MM_WX_SOUND_STATE=1; wxuin=14128035; webwxuvid=f4108873f916b55cb09f7cb8943a172c8d5c4fed338f685d19a0e87ff497d894625337dc0fc1c74c2ebdb1290bfe221d; last_wxuin=14128035; refreshTimes=3; wxsid=MoawOK59sPY6BiRI; webwx_data_ticket=gSc9xEnFqFEUsYhW+WlLTu2h; webwx_auth_ticket=CIsBEIWn980BGoAB8MWjuvT1ou09TqypS2rOwDPUwleZi7foDNWRP9juRwgsh0hChvcI7PGDbKFy2YVmZD0MJ81ntOi9MQEfXl7xcwXEr4mywjkHhfayFkoX0k+5+LMA+VhJxb6gHaV6SAcQMO83d953KMBMcjCgD1EV6YzzG2Z7zTpkFH7YUV8ElZA=; login_frequency=2; wxloadtime=1543566510_expired; wxpluginkey=1543565743");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringWriter writer = new StringWriter();

        char[] chars = new char[256];
        int count = 0;
        while ((count = reader.read(chars)) > 0) {
            writer.write(chars, 0, count);
        }
        System.out.println(writer.toString());

        conn.disconnect();

    }

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 10; i++) {
            System.out.println("===================" + i);
            test("054700.cn");
            test("365.cn");
        }
    }
}

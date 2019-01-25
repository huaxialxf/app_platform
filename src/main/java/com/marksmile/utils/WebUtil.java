package com.marksmile.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebUtil {
    private static Logger logger = LoggerFactory.getLogger(WebUtil.class);
    private static final String METHOD_GET = "GET";

    /**
     * 执行HTTP GET请求。
     * 
     * @param url
     *            请求地址
     * @param params
     *            请求参数
     * @param charset
     *            字符集，如UTF-8, GBK, GB2312
     * @return 响应字符串
     * @throws IOException
     */
    public static String doGet(String url) throws IOException {

        HttpURLConnection conn = null;
        String rsp = null;

        try {
            String ctype = "application/x-www-form-urlencoded;charset=UTF-8";

            conn = getConnection(new URL(url), METHOD_GET, ctype);
            rsp = getResponseAsString(conn);

        } catch (IOException e) {
            throw e;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return rsp;
    }

    private static HttpURLConnection getConnection(URL url, String method, String ctype) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setConnectTimeout(60 * 1000);
        conn.setReadTimeout(60 * 1000);
        conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html,application/json");
        conn.setRequestProperty("User-Agent", "365-api-sdk-java");
        conn.setRequestProperty("Content-Type", ctype);
        return conn;
    }

    private static String getResponseAsString(HttpURLConnection conn) throws IOException {
        String charset = "UTF-8";
        InputStream es = conn.getErrorStream();
        if (es == null) {
            return getStreamAsString(conn.getInputStream(), charset);
        } else {
            String msg = getStreamAsString(es, charset);
            if (msg == null || "".equals(msg)) {
                throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
            } else {
                return msg;
            }
        }
    }

    private static String getStreamAsString(InputStream stream, String charset) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
            StringWriter writer = new StringWriter();

            char[] chars = new char[256];
            int count = 0;
            while ((count = reader.read(chars)) > 0) {
                writer.write(chars, 0, count);
            }

            return writer.toString();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String ret = WebUtil.doGet("https://qztask.oss-cn-beijing.aliyuncs.com/task.txt");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        String fileName = String.format("tasks/%s.txt", dateStr);
        File file = new File(fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(ret.getBytes());
        fos.close();
        logger.info("生成文件:"+fileName);
    }
}

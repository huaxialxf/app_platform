package com.marksmile.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientKit {
	private static Logger logger = LoggerFactory.getLogger(HttpClientKit.class);
	/**
	 * 内部对象 httpClient
	 */
	private CloseableHttpClient httpClient = null;
	private Map<String, String> cookies = new HashMap<String, String>();

	public HttpClientKit() {
		try {
			initHttpCient(null, null);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public HttpClientKit(String proxyHostInfo, String localIp) {
		try {
			initHttpCient(proxyHostInfo, localIp);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void initHttpCient(String proxyHostInfo, String localIp) throws Exception {
		HttpClientBuilder builder = HttpClients.custom();
		if (proxyHostInfo != null) {
			String[] arr = proxyHostInfo.split(";");
			String ip = arr[0];
			int port = Integer.parseInt(arr[1]);
			HttpHost proxyHost = new HttpHost(ip, port);
			builder.setProxy(proxyHost);
		}
		if (localIp != null) {
			InetAddress address;
			address = InetAddress.getByName(localIp);
			RequestConfig config = RequestConfig.custom().setLocalAddress(address).build();
			builder.setDefaultRequestConfig(config);
		}
		httpClient = builder.build();

	}

	public String exeGetMethodForString(Map<String, String> headers, String url) throws Exception {
		CloseableHttpResponse response = exeGetMethod(headers, url);
		handleRespone(response);
		String htmlResponse = EntityUtils.toString(response.getEntity());
		return htmlResponse;
	}

	public String exeGetMethodForString(String url) throws Exception {
		CloseableHttpResponse response = null;
		for (int i = 0; i < 3; i++) {
			try {
				response = exeGetMethod(url);
				if (response.getStatusLine().getStatusCode() != 200) {
					throw new Exception("getStatusCode = " + response.getStatusLine().getStatusCode());
				}
				handleRespone(response);
				String htmlResponse = EntityUtils.toString(response.getEntity(), "GBK");
				return htmlResponse;
			} catch (Exception e) {
				if (i == 2) {
					throw e;
				}
			}

		}
		throw new Exception("未知错误");

	}

	public String exeGetMethodForString(String url, String charset) throws Exception {
		CloseableHttpResponse response = null;
		for (int i = 0; i < 3; i++) {
			try {
				response = exeGetMethod(url);
				if (response.getStatusLine().getStatusCode() != 200) {
					throw new Exception("getStatusCode = " + response.getStatusLine().getStatusCode());
				}
				handleRespone(response);
				String htmlResponse = EntityUtils.toString(response.getEntity(), charset == null ? "UTF-8" : charset);
				return htmlResponse;
			} catch (Exception e) {
				if (i == 2) {
					throw e;
				}
			}

		}
		throw new Exception("未知错误");

	}

	private void handleRespone(CloseableHttpResponse response) {
		Header[] headers = response.getAllHeaders();
		for (Header header : headers) {

			// Set-Cookie>>>>__cfduid=de8b3f6dfdacdcb66c1e6fb84bd9326a91478168074;
			// expires=Fri, 03-Nov-17 10:14:34 GMT; path=/; domain=.adndrc.org;
			// HttpOnly
			if ("Set-Cookie".equals(header.getName())) {
				String value = header.getValue();
				String key = value.substring(0, value.indexOf("="));
				String cValue = value.substring(value.indexOf("=") + 1, value.indexOf(";"));
				cookies.put(key, cValue);
			}
		}
	}

	public String getCookies() {
		StringBuffer buffer = new StringBuffer();
		for (String key : cookies.keySet()) {
			buffer.append(key + "=" + cookies.get(key) + "; ");
		}
		return buffer.toString();
	}

	public CloseableHttpResponse exeGetMethod(String url) throws Exception {
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response = httpClient.execute(httpGet);
		handleRespone(response);
		return response;
	}

	public CloseableHttpResponse exeGetMethod(Map<String, String> headers, String url) throws Exception {
		HttpGet httpGet = new HttpGet(url);

		if (headers != null) {
			for (String key : headers.keySet()) {
				String value = headers.get(key);
				httpGet.setHeader(key, value);
			}
		}
		CloseableHttpResponse response = httpClient.execute(httpGet);
		handleRespone(response);
		return response;
	}

	public String exePostMethodForString(String url, Map<String, String> headers, Map<String, String> params)
			throws Exception {
		return exePostMethodForString(url, headers, params, "UTF-8");
	}


	public String exePostMethodForString(String url, Map<String, String> headers, Map<String, String> params,String charset)
			throws Exception {
		for (int i = 0; i < 3; i++) {
			try {
				CloseableHttpResponse response = exePostMethod(url, headers, params,charset);
				handleRespone(response);
				String htmlResponse = EntityUtils.toString(response.getEntity());
				return htmlResponse;
			} catch (Exception e) {
				if (i == 2) {
					throw e;
				}
			}

		}
		throw new Exception("exePostMethodForString unkown");
	}
	public String exePostMethodForString(String url, Map<String, String> headers, String body) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		if (headers != null) {
			for (String key : headers.keySet()) {
				String value = headers.get(key);
				httpPost.setHeader(key, value);
			}
		}
		httpPost.setEntity(new InputStreamEntity(new ByteArrayInputStream(body.getBytes())));

		CloseableHttpResponse response = httpClient.execute(httpPost);
		handleRespone(response);

		String htmlResponse = EntityUtils.toString(response.getEntity());
		return htmlResponse;
	}

	public CloseableHttpResponse exePostMethod(String url, Map<String, String> headers, Map<String, String> params ,String charset)
			throws Exception {
		HttpPost httpPost = new HttpPost(url);
		if (headers != null) {
			for (String key : headers.keySet()) {
				String value = headers.get(key);
				httpPost.setHeader(key, value);
			}
		}
		if (params != null) {
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for (String key : params.keySet()) {
				String value = params.get(key);
				list.add(new BasicNameValuePair(key, value));
			}
			HttpEntity postBodyEnt = new UrlEncodedFormEntity(list, charset);
			httpPost.setEntity(postBodyEnt);
		}

		CloseableHttpResponse response = httpClient.execute(httpPost);
		handleRespone(response);

		return response;
	}
	public CloseableHttpResponse exePostMethod(String url, Map<String, String> headers, Map<String, String> params)
			throws Exception {
		return exePostMethod(url, headers, params, "UTF-8");
	}

	public byte[] downHttpFile(String url, Map<String, String> headers) throws Exception {
		CloseableHttpResponse rs;
		try {
			HttpGet httpGet = new HttpGet(url);
			// httpGet.setConfig(StaticHttpClient.bindingTimeOut());
			if (headers != null) {
				for (String key : headers.keySet()) {
					String value = headers.get(key);
					httpGet.setHeader(key, value);
				}
			}
			rs = httpClient.execute(httpGet);

			handleRespone(rs);
			HttpEntity rsENti = rs.getEntity();
			if (rsENti != null) {
				InputStream is = rsENti.getContent();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				int n = -1;
				byte[] bytes = new byte[1024];
				while ((n = is.read(bytes)) > -1) {
					bos.write(bytes, 0, n);
				}
				return bos.toByteArray();
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}
		return null;
	}

	public void downHttpFile(String url, Map<String, String> headers, File localFile) throws Exception {
		HttpResponse rs;
		FileOutputStream fos = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			// httpGet.setConfig(StaticHttpClient.bindingTimeOut());
			if (headers != null) {
				for (String key : headers.keySet()) {
					String value = headers.get(key);
					httpGet.setHeader(key, value);
				}
			}
			rs = httpClient.execute(httpGet);
			HttpEntity rsENti = rs.getEntity();
			if (rsENti != null) {
				InputStream is = rsENti.getContent();
				fos = new FileOutputStream(localFile);
				int n = -1;
				byte[] bytes = new byte[1024];
				while ((n = is.read(bytes)) > -1) {
					fos.write(bytes, 0, n);
				}
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void close() {
		try {
			httpClient.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void main(String[] args) throws Exception {
		Map<String, String> headers = new LinkedHashMap<String, String>();
		// headers.put("Host",
		// "www.miitbeian.gov.cn");
		headers.put("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6726.400 QQBrowser/10.2.2265.400");
		// headers.put("Cookie", "__jsluid=909a2a7042c4515ae5fe57d9f1616cec;
		// __jsl_clearance=1544575100.384|0|sLOwfA5Q39Pfhu%2F8YBGi50sVaKU%3D;
		// JSESSIONID=aq6f3XYxriDmXnihfEc9wZFHLaueeAQ0qxQvdmE_Kfz0BUuK30xQ!2144121308");

		HttpClientKit clientUtil = new HttpClientKit("34.229.98.250;443", null);

		String ret = clientUtil.exeGetMethodForString(headers, "https://www.lxf.com/");
		System.out.println(ret);
	}

}

package com.taobao.update;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author Liupeng
 * @version CreateTime:2010-12-20
 */
public class VersionService {
	private String[] charsets = new String[] { "UTF-8", "UTF-8" };
	private static String[] urls = new String[] {"http://yjheeq.javaeye.com/blog/847011", "http://blog.sina.com.cn/s/blog_61313c620100nyg0.html"};
	
	public int version() {
		int version = -1;
		for (int i = 0; i < urls.length; i++) {
			version = getVersion(urls[i], i);
			if (version != -1) {
				break;
			}
		}
		return version;
	}

	private int getVersion(String urlPath, int no) {
		int version = -1;
		try {
			URL url = new URL(urlPath);
			HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.6) Gecko/20100625 Firefox/3.6.6 ( .NET CLR 3.5.30729)");
			InputStream l_urlStream = httpUrlConnection.getInputStream();
			String sCurrentLine = "";
			String msg = "";
			BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream, charsets[no]));
			while ((sCurrentLine = l_reader.readLine()) != null) {
				msg += sCurrentLine + "\r\n";
			}
			l_reader.close();
			Document doc = Jsoup.parse(msg);
			String content = "";
			if (no == 0) {
			    content = doc.getElementsByClass("blog_content").html();
			} else if (no == 1) {
			    content = doc.getElementById("sina_keyword_ad_area2").html();
			}
			if (!content.equals("")) {
				content = content.substring(content.indexOf(":") + 1);
				version = Integer.parseInt(content.replace(".", ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return version;
	}

	public static void main(String[] args) {
		new VersionService().version();
	}
}

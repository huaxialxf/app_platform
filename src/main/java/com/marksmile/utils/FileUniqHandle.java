package com.marksmile.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marksmile.utils.FileUtil;
import com.marksmile.utils.FileUtil.LineHandler;

/**
 * 文件的重复项处理
 * 
 * @Title:
 * @Description:
 * @Author : LuXinFeng
 * @Since : 2016年3月21日
 * @Version : 1.0.0
 */
public abstract class FileUniqHandle {

	private static Logger logger = LoggerFactory.getLogger(FileUniqHandle.class);

	private void doHanlde(String srcFileName, BufferedWriter writer, int num) throws IOException {
		final AtomicInteger lasRow = new AtomicInteger(0);
		final AtomicInteger preRow = new AtomicInteger(0);

		for (int i = 0; i < num; i++) {
			final Set<String> set = new HashSet<String>();
			int index = i;
			LineHandler handler = (line) -> {
				String key = getKey(line);
				int hashCode = key.hashCode();
				hashCode = Math.abs(hashCode);
				if (hashCode % num == index) {
					if (key != null && !set.contains(key)) {
						set.add(key);
						try {
							writer.write(line);
							writer.write("\n");
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
						lasRow.incrementAndGet();
					}
					preRow.incrementAndGet();
				}
				return null;
			};

			FileUtil.doHandle(srcFileName, null, handler);
		}

		logger.info(String.format("去重 [%d  -->  %d] ", preRow.get(), lasRow.get()));

	}

	public void doHanlde(String srcFileName, String tarFile, int num) {

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(tarFile)), 1024 * 1024 * 8);
			doHanlde(srcFileName, writer, num);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public abstract String getKey(String line);

	public static void main(String[] args) throws IOException {

		new FileUniqHandle() {
			@Override
			public String getKey(String line) {
				return line;
			}
		}.doHanlde("G:/ms_data/yumi_whois/data/test/sql_export", "G:/ms_data/yumi_whois/data/test/q.txt", 10);
	}
}

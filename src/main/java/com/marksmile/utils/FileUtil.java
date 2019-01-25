package com.marksmile.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.Assert;

import com.marksmile.utils.FileUtil.LineHandler;

/**
 * 
 * @Title:
 * @Description: 常用文件操作类
 * @Author : LuXinFeng
 * @Since : 2015年10月14日
 * @Version : 1.0.0
 */
public class FileUtil {

	/**
	 * 
	 * @param file
	 * @Description : 删除文件，如果是目录则删除目录下的所有文件
	 */
	public static void delFile(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File file2 : files) {
				delFile(file2);
			}
			file.delete();
		} else {
			file.delete();
		}
	}

	public static List<String> getFileContext(File file) throws IOException {
		List<String> list = new ArrayList<String>();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				list.add(line);
			}
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
				}
			}
		}

		return list;
	}

	public static String getFileInfo(File file) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] bytes = new byte[1024];
			int n = -1;
			while ((n = fis.read(bytes)) > -1) {
				bos.write(bytes, 0, n);
			}
			return new String(bos.toByteArray());
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static List<String> getFileContextByLineNum(File file, int lineNum) throws IOException {
		List<String> list = new ArrayList<String>();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				list.add(line);
				if (list.size() == lineNum) {
					return list;
				}
			}
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
				}
			}
		}

		return list;
	}

	private static void doHandleToOutStream(String srcFileName, BufferedWriter writer, LineHandler lineHandler,
			AtomicInteger num) throws IOException {
		File srcFile = new File(srcFileName);
		if (srcFile.isDirectory()) {
			File[] files = srcFile.listFiles();
			for (File file : files) {
				doHandleToOutStream(file.getAbsolutePath(), writer, lineHandler, num);
			}
		} else {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile)));
				String line = null;

				while ((line = reader.readLine()) != null) {
					if (line == null || "".equals(line)) {
						continue;
					}
					String newLine = lineHandler.doHandler(line);
					if (newLine != null) {
						writer.write(newLine);
						writer.write("\n");
					}
					if ((num.incrementAndGet()) % 100000 == 0) {
						System.out.println("n:" + num.get() + " " + new Date());
					}
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
			}

		}
	}

	public static void doHandle(String srcFileName, String tarFileName, LineHandler lineHandler) throws IOException {
		AtomicInteger num = new AtomicInteger();
		BufferedWriter writer = null;
		try {
			if (tarFileName != null) {
				File tarFile = new File(tarFileName);
				if (tarFile.getParentFile() != null && !tarFile.getParentFile().exists()) {
					tarFile.getParentFile().mkdirs();
				}
				writer = new BufferedWriter(new FileWriter(tarFile), 1024 * 1024 * 8);
			}
			doHandleToOutStream(srcFileName, writer, lineHandler, num);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		System.out.println("total:" + num.get());
	}

	public static void doHandle(Iterator<String> dataSource, String tarFileName, LineHandler lineHandler)
			throws IOException {
		AtomicInteger num = new AtomicInteger();
		BufferedWriter writer = null;
		try {
			if (tarFileName != null) {
				File tarFile = new File(tarFileName);
				if (tarFile.getParentFile() != null && !tarFile.getParentFile().exists()) {
					tarFile.getParentFile().mkdirs();
				}
				writer = new BufferedWriter(new FileWriter(tarFile), 1024 * 1024 * 8);
			}
			String line = null;
			while ((line = dataSource.next()) != null) {
				line = lineHandler.doHandler(line);
				if (line != null) {
					if (writer != null) {
						writer.write(line);
					}
					num.incrementAndGet();
				}
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		System.out.println("total:" + num.get());
	}

	public static void split(Iterator<String> dataSource, String tarFileDir, String preName, int size,
			LineHandler lineHandler) throws IOException {
		Assert.isTrue(size > 0);
		File pFile = new File(tarFileDir);
		pFile.mkdirs();
		int index = 0;

		BufferedWriter writer = null;
		String line = null;
		while (dataSource.hasNext()) {
			line = dataSource.next();
			line = lineHandler.doHandler(line);
			if (line != null) {
				if (index % size == 0) {
					if (writer != null) {
						writer.close();
					}
					String _fileName = String.format(preName + "_%04d.txt", index / size);
					// System.out.println("f:" + _fileName);
					writer = new BufferedWriter(new FileWriter(new File(tarFileDir, _fileName)), 1024 * 1024 * 8);
				}
				writer.write(line + "\n");
				index++;
			}
		}

		if (writer != null) {
			writer.close();
		}

		System.out.println("total:" + index);
	}

	@FunctionalInterface
	public interface LineHandler {
		public String doHandler(String str);
	}

	public static void split(String srcFile, String tarDir, String preName, int size, LineHandler handler)
			throws IOException {
		Assert.isTrue(size > 0);
		AtomicInteger preNum = new AtomicInteger(0);
		AtomicInteger postNum = new AtomicInteger(0);

		File pFile = new File(tarDir);
		pFile.mkdirs();

		FileOutputStream[] arr = new FileOutputStream[1];
		// arr[0] = fos;
		LineHandler linehandler = (line) -> {
			try {
				int num = preNum.incrementAndGet();
				if (handler != null) {
					line = handler.doHandler(line);
				}

				if (line != null) {

					if (postNum.get() % size == 0) {
						if (arr[0] != null)
							arr[0].close();
						String _fileName = String.format(preName + "_%04d.txt", postNum.get() / size);
						System.out.println("f:" + _fileName);
						arr[0] = new FileOutputStream(new File(pFile, _fileName));
					}

					postNum.incrementAndGet();
					arr[0].write((line + "\n").getBytes());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		};
		doHandle(srcFile, null, linehandler);
		System.out.println("total-split-pre:" + preNum.get());
		System.out.println("total-split-post:" + postNum.get());
		if (arr[0] != null) {
			arr[0].close();
		}
	}

	public static void main2(String[] args) throws IOException {
		AtomicInteger n = new AtomicInteger(0);
		LineHandler lineHandler = (line) -> {
			n.incrementAndGet();
			return line;
		};
		FileUtil.doHandle("C:/Users/lu/Desktop/data/task_done", "C:/Users/lu/Desktop/data/whois_cn.txt", lineHandler);
		// System.out.println("n====" + n.get());
		//
		// LineHandlerForSplit h = (line) -> {
		// if (line.length() > 1) {
		// return false;
		// }
		// return true;
		// };
		// FileUtil.split("test.txt", "data/", "abc", 9, h, null);
		// new FileHandler() {
		//
		// @Override
		// public String doHandlerLine(String line) {
		// String[] arr = line.split("\\+\\-\\+");
		// if (arr[3].endsWith("@qq.com") || arr[3].endsWith("@foxmail.com")) {
		// return line;
		// }
		// return null;
		// }
		// }.doHandle("C:/Users/admin/Desktop/sendmail_58_1513322981794_old.txt",
		// "C:/Users/admin/Desktop/sendmail_58_1513322981794.txt");

	}

	public static void main22(String[] args) throws IOException {
		LineHandler lineHandler = (line) -> {
			String arr[] = line.split(",");
			int num = Integer.parseInt(arr[1]);
			if (num > 500) {
				return line;
			}

			return null;
		};
		FileUtil.doHandle("G:/dns_server.txt", "G:/dns_server_2.txt", lineHandler);
	}

	static Set<String> set = new HashSet<String>();
	static {
		set.add("dnbiz");
		set.add("juming");
		set.add("hichina");
		set.add("dnspod");
		set.add("dnsdun");
		set.add("xundns");
		set.add("alidns");
		set.add("myhostadmin");
		set.add("51dns");
	}
	private static String preDomainName = null;

	public static void main444(String[] args) throws IOException {

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		AtomicInteger n = new AtomicInteger(0);
		LineHandler lineHandler = (line) -> {
			n.incrementAndGet();
			String str = line.toLowerCase();
			if (str.indexOf(" ns ") < 0) {
				return null;
			}

			String arr[] = str.split(" ");
			if (arr.length == 3 && arr[1].equals("ns") && !arr[0].equals("")) {
				String domainName = arr[0];
				if (domainName.equals(preDomainName)) {
					return null;
				} else {
					preDomainName = domainName;
					return domainName+".com";
				}
			}

			return null;
		};
		FileUtil.doHandle("G:/com.zone20190109", "G:/com.zone20190109_xhb.txt", lineHandler);
	}
	
	


	public static void main(String[] args) throws IOException {
		// dns.com.cn

		AtomicInteger n = new AtomicInteger(0);
		LineHandler lineHandler = (line) -> {
			return line.substring(1,line.length()-4);
		};
		FileUtil.doHandle("G:/com.zone20190109_xhb.txt", "G:/com.zone20190109_xhb_2.txt", lineHandler);

	}
	
	

	public static void main33(String[] args) throws IOException {
		// dns.com.cn

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		AtomicInteger n = new AtomicInteger(0);
		LineHandler lineHandler = (line) -> {
			n.incrementAndGet();
			String str = line.toLowerCase();
			if (str.indexOf(" ns ") < 0) {
				return null;
			}

			String arr[] = str.split(" ");
			if (arr.length == 3 && arr[1].equals("ns") && !arr[0].equals("")) {
				String nsserver = arr[2];
				if (nsserver.endsWith(".")) {
					nsserver = nsserver.substring(0, nsserver.length() - 1);
				}
				if (isChina(nsserver, set)) {
					return line;
				}
				// System.out.println(nsserver);
				//
				// System.out.println(str);
			}

			return null;
		};
		FileUtil.doHandle("G:/com.zone20190109", "G:/com.zone20190109_2.txt", lineHandler);
		FileOutputStream fos = new FileOutputStream(new File("G:/dns_server.txt"));
		for (String string : map.keySet()) {
			fos.write((string + "," + map.get(string) + "\n").getBytes());
		}
		fos.close();
		System.out.println(map.size());

	}
	

	public static void mainwww(String[] args) throws IOException {
		// dns.com.cn

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		AtomicInteger n = new AtomicInteger(0);
		LineHandler lineHandler = (line) -> {
			n.incrementAndGet();
			String str = line.toLowerCase();
			if (str.indexOf(" ns ") < 0) {
				return null;
			}

			String arr[] = str.split(" ");
			if (arr.length == 3 && arr[1].equals("ns") && !arr[0].equals("")) {
				String nsserver = arr[2];
				if (nsserver.endsWith(".")) {
					nsserver = nsserver.substring(0, nsserver.length() - 1);
				}
				if (isChina(nsserver, set)) {
					return line;
				}
				// System.out.println(nsserver);
				//
				// System.out.println(str);
			}

			return null;
		};
		FileUtil.doHandle("G:/com.zone20190109", "G:/com.zone20190109_2.txt", lineHandler);
		FileOutputStream fos = new FileOutputStream(new File("G:/dns_server.txt"));
		for (String string : map.keySet()) {
			fos.write((string + "," + map.get(string) + "\n").getBytes());
		}
		fos.close();
		System.out.println(map.size());

	}

	public static boolean isChina(String dns, Set<String> set) {
		String arr[] = dns.split("\\.");
		for (String key : arr) {
			if (set.contains(key)) {
				return true;
			}
		}
		if (dns.indexOf(".dns.com") > -1) {
			return true;
		}
		return false;
	}

	public static void main111(String[] args) {
		System.out.println(isChina("dns9.hichina", set));
	}
}

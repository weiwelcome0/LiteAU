package com.couragechallenge.liteau.base;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommTool {
	/** URI类型：file */
	public static final String URI_TYPE_FILE = "file";

	/**
	 * 提供类似数据库中的nvl函数功能。如果参数值为null则返回一个给定的默认值
	 * 
	 * @param value
	 * @param nullValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T nvl(Object value, T nullValue) {
		return null == value ? nullValue : (T) value;
	}

	public static String empty(String value, String nullValue) {
		return null != value && value.trim().length()>0?value:nullValue;
	}

	public static int nvl(Object value, int nullValue) {
		try {
			return null == value ? nullValue : Integer.parseInt(value.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nullValue;
	}

	public static char nvl(Object value, char nullValue) {
		try {
			return null == value ? nullValue : value.toString().charAt(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nullValue;
	}

	public static boolean nvl(Object value, boolean nullValue) {
		try {
			return null == value ? nullValue : Boolean.parseBoolean(value.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nullValue;
	}

	public static long nvl(Object value, long nullValue) {
		try {
			return null == value ? nullValue : Long.parseLong(value.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nullValue;
	}

	public static float nvl(Object value, float nullValue) {
		try {
			return null == value ? nullValue : Float.parseFloat(value.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nullValue;
	}

	public static double nvl(Object value, double nullValue) {
		try {
			return null == value ? nullValue : Double.parseDouble(value.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nullValue;
	}

	public static int sum(int[] array) {
		int sum = 0;
		if (null != array) {
			for (int var : array) {
				sum += var;
			}
		}
		return sum;
	}

	public static double sum(double[] array) {
		double sum = 0d;
		if (null != array) {
			for (double var : array) {
				sum += var;
			}
		}
		return sum;
	}

	public static <T> T[] removeRepeated(T[] objs) {
		int size = null == objs ? 0 : objs.length;
		if (0 >= size) {
			return objs;
		}

		HashSet<T> set = new HashSet<T>(size);
		for (int i = 0; i < size; i++) {
			if (!set.contains(objs[i])) {
				set.add(objs[i]);
			}
		}

		@SuppressWarnings("unchecked")
		T[] r = (T[]) java.lang.reflect.Array.newInstance(objs.getClass().getComponentType(), set.size());
		return set.toArray(r);
	}

	public static <T> String array2String(T[] array) {
		return array2String(array, ",");
	}

	public static <T> String array2String(T[] array, String strJoinner) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0, n = null == array ? 0 : array.length; i < n; i++) {
			sb.append("" + array[i]);
			if (i < n - 1) {
				sb.append(strJoinner);
			}
		}
		return 0 == sb.length() ? "" : sb.substring(0, sb.length() - strJoinner.length());
	}

	public static <T> String arrayFormatInsql(T[] array) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0, n = null == array ? 0 : array.length; i < n; i++) {
			sb.append("'").append("" + array[i]).append("'");
			if (i < n - 1) {
				sb.append(",");
			}
		}
		return 0 == sb.length() ? "''" : sb.toString();
	}

	/**
	 * 字符串str左边填充count字符ch
	 * 
	 * @param str
	 * @param ch
	 * @param count
	 * @return
	 */
	public static String padLeft(String str, char ch, int count) {
		if (count <= 0) {
			return str;
		}

		int len = str.length() + count;
		char[] s = new char[len];
		for (int i = 0; i < count; i++) {
			s[i] = ch;
		}
		str.getChars(0, str.length(), s, count);

		return new String(s);
	}

	/**
	 * 字符串str右边填充count字符ch
	 * 
	 * @param str
	 * @param ch
	 * @param count
	 * @return
	 */
	public static String padRight(String str, char ch, int count) {
		if (count <= 0) {
			return str;
		}
		int len = str.length() + count;
		char[] s = new char[len];
		for (int i = str.length() - 1; i < len; i++) {
			s[i] = ch;
		}
		str.getChars(0, str.length(), s, 0);

		return new String(s);
	}

	public static final String bytesToHexString(byte[] bArray) {
		int size = null == bArray ? 0 : bArray.length;
		StringBuilder sb = new StringBuilder(size * 2);
		for (int i = 0; i < size; i++) {
			int hex = 0xFF & bArray[i];
			if (hex <= 0xF) {
				sb.append(0);
			}
			sb.append(Integer.toHexString(hex));
		}
		return sb.toString();
	}

	public static final String initialUpperCase(String str) {
		String s = null;
		if (null != str && 0 < str.length()) {
			char[] value = str.toCharArray();
			value[0] = Character.toUpperCase(value[0]);
			s = String.valueOf(value);
		}
		return s;
	}

	public static final String initialLowerCase(String str) {
		String s = null;
		if (null != str && 0 < str.length()) {
			char[] value = str.toCharArray();
			value[0] = Character.toLowerCase(value[0]);
			s = String.valueOf(value);
		}
		return s;
	}

	public static final String trim(String str, char c) {
		if (null != str && 0 < str.length()) {
			char[] value = str.toCharArray();
			int len = value.length, left = 0, right = len - 1;
			for (; left < len && value[left] == c; left++)
				;
			for (; left < right && value[right] == c; right--)
				;
			return str.substring(left, right + 1);
		}
		return str;
	}

	@SuppressWarnings("unchecked")
	public static final <T> T convert(String name, Object value) {
		if (null != name && name.length() > 0) {
			switch (name.charAt(0)) {
			case 's':
			case 'S':
			case 't':
			case 'T':
				return (T) ("" + value);
			case 'b':
			case 'B':
				return (T) Boolean.valueOf("" + value);
			case 'i':
			case 'I':
				try {
					return (T) Integer.valueOf("" + value);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return (T) Integer.valueOf(0);
			case 'n':
			case 'N':
				try {
					return (T) new BigDecimal("" + value);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return (T) BigDecimal.valueOf(0);
			default:
				return null;
			}
		}

		return null;

	}

	/**
	 * 两个集合的交集
	 * 
	 * @param ls
	 * @param ls2
	 * @return
	 */
	public <T> List<T> intersect(List<T> ls, List<T> ls2) {
		@SuppressWarnings("unchecked")
		List<T> list = new ArrayList<T>((Collection<? extends T>) Arrays.asList(new Object[ls.size()]));
		Collections.copy(list, ls);
		list.retainAll(ls2);
		return list;
	}

	/**
	 * 两个集合的并集
	 * 
	 * @param ls
	 * @param ls2
	 * @return
	 */
	public <T> List<T> union(List<T> ls, List<T> ls2) {
		@SuppressWarnings("unchecked")
		List<T> list = new ArrayList<T>((Collection<? extends T>) Arrays.asList(new Object[ls.size()]));
		Collections.copy(list, ls);
		list.addAll(ls2);
		return list;
	}

	/**
	 * 两个集合的差集
	 * 
	 * @param ls
	 * @param ls2
	 * @return
	 */
	public <T> List<T> diff(List<T> ls, List<T> ls2) {
		@SuppressWarnings("unchecked")
		List<T> list = new ArrayList<T>((Collection<? extends T>) Arrays.asList(new Object[ls.size()]));
		Collections.copy(list, ls);
		list.removeAll(ls2);
		return list;
	}

	public static String encodeBase64(String s) {
		try {
			return new String(Base64.encodeToChar(s.getBytes("UTF-8"), false));
		} catch (UnsupportedEncodingException e) {
			System.err.println("Base64 encoding error: " + e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	public static String decodeBase64(String s) {
		try {
			return new String(Base64.decodeFast(s), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.err.println("Base64 decoding error: " + e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	private static final String[] str1 = { "元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟" };
	private static final String[] str2 = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
	private static final String[] str3 = { "角", "分" };

	public static String convertRMB2(String str) {
		int index = str.indexOf(".");
		char[] temp1 = str.substring(0, index).toCharArray();
		char[] temp2 = str.substring(index + 1).toCharArray();

		int value = -1;
		StringBuilder sb = new StringBuilder();

		int len = temp1.length - 1;
		for (int i = 0; i < temp1.length; i++) {
			value = temp1[i] - '0';
			sb.append(str2[value]).append(str1[len - i]);
		}

		for (int i = 0; i < temp2.length; i++) {
			value = temp2[i] - '0';
			sb.append(str2[value]).append(str3[i]);
		}

		return sb.toString();
	}

	public static void main(String[] args) {
		String n = "20040050078.23";

		String str = convertRMB2(n + "");

		System.out.println(str);
	}

	public static String mkdirs(String path) {
		new File(path).mkdirs();
		return path;
	}

	public static String touch(String file) {
		File f = new File(file);
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return file;
	}

	public static void copyFile(File source, File dest, boolean autoRename, boolean isCut) throws Exception {
		boolean exists = dest.exists();
		if (autoRename && exists) {
			// dest = getUniqueFile(dest);
			dest = new File(dest.getPath() + DateUtil.getNow());
			exists = false;
		}
		if (!exists)
			dest.createNewFile();
		FileChannel in = null;
		FileChannel out = null;
		try {
			in = new FileInputStream(source).getChannel();
			out = new FileOutputStream(dest).getChannel();
			in.transferTo(0, in.size(), out);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Throwable t) {
				}
			}
			if (out != null)
				out.close();
		}
		dest.setLastModified(source.lastModified());
		if (isCut)
			source.delete();
	}

	/**
	 * 将sql中＂?var＂转化为标准的sql
	 * 
	 * @param sql
	 * @return
	 */
	public static String convertSql(String sql) {
		if (null == sql || sql.length() <= 0) {
			return sql;
		}

		boolean isVar = false;
		char[] sqlCh = sql.toCharArray();
		for (int i = 0; i < sqlCh.length;) {

			// 定位到?
			if (sqlCh[i++] != '?') {
				continue;
			}

			// 将?后的变量名替换为空格
			int end = i;
			for (; end < sqlCh.length && Character.isJavaIdentifierStart(sqlCh[end]); end++) {
				isVar = true;
				sqlCh[end] = ' ';
			}

			i = end + 1;
		}

		if (!isVar) {
			sqlCh = null;
			return sql;
		}

		return new String(sqlCh);
	}

	/**
	 * 获取sql中的＂?var＂形式的变量名.匿名变量则用别名$0,$1...
	 * 
	 * @param sql
	 * @return
	 */
	public static List<String> getSqlParams(String sql) {
		List<String> list = new LinkedList<String>();
		if (null == sql || sql.length() <= 0) {
			return list;
		}

		int count = 0; // 记录变量个数
		char[] sqlCh = sql.toCharArray();
		for (int i = 0; i < sqlCh.length;) {

			// 定位到?
			if (sqlCh[i++] != '?') {
				continue;
			}

			// 将?后的变量名替换为空格
			int begin = i, end = i;
			for (; end < sqlCh.length && Character.isJavaIdentifierStart(sqlCh[end]); end++)
				;

			// 每个变量都有一个别名$0,$1....等
			if (begin == end) {
				list.add("$" + count);
			} else {
				list.add(new String(sqlCh, begin, end - begin));
			}

			count++;
			i = end + 1;
		}

		return list;
	}

	// 将驼峰风格替换为下划线风格
	public static String camelhumpToUnderline(String str) {
		Matcher matcher = Pattern.compile("[A-Z]").matcher(str);
		StringBuilder builder = new StringBuilder(str);
		for (int i = 0; matcher.find(); i++) {
			builder.replace(matcher.start() + i, matcher.end() + i, "_" + matcher.group().toLowerCase());
		}
		if (builder.charAt(0) == '_') {
			builder.deleteCharAt(0);
		}
		return builder.toString();
	}

	// 将下划线风格替换为驼峰风格
	public static String underlineToCamelhump(String str) {
		Matcher matcher = Pattern.compile("_[a-z]").matcher(str);
		StringBuilder builder = new StringBuilder(str);
		for (int i = 0; matcher.find(); i++) {
			builder.replace(matcher.start() - i, matcher.end() - i, matcher.group().substring(1).toUpperCase());
		}
		if (Character.isUpperCase(builder.charAt(0))) {
			builder.replace(0, 1, String.valueOf(Character.toLowerCase(builder.charAt(0))));
		}
		return builder.toString();
	}

	public static String MD5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
			return bytesToHexString(hash);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将一个InputStream中的数据写入到SD卡中
	 * 
	 * @param path
	 *            需要写入数据的文件的路径
	 * @param fileName
	 *            需要写入数据的文件的名称
	 * @param input
	 *            输入管道
	 * @return 返回代表写入数据的文件对象
	 */
	public static File writeFromInput(File file, InputStream input) {
		OutputStream output = null;

		try {
			File parent = file.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			// 通过文件对象，创建一个文件输出管道对象FileOutputStream
			output = new FileOutputStream(file);
			// 定义一个字节数组作为缓存，以4K为单位，每4K写一次到文件中
			byte[] buffer = new byte[4 * 1024];
			// 循环读取InputStream流中的数据，并通过FileOutputStream对象写入到文件中
			while (input.read(buffer) != -1) {
				output.write(buffer);
			}
			// 写出缓存里的数据并清空缓存
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null) {
					output.close();
					output = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return file;
	}

	public static String readFile(String filePath) {
		File file = new File(filePath);

		// FileInputStream 用于读取诸如图像数据之类的原始字节流。要读取字符流，请考虑使用 FileReader。

		FileInputStream inStream;
		try {
			inStream = new FileInputStream(file);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024];

			int length = -1;

			while ((length = inStream.read(buffer)) != -1) {

				bos.write(buffer, 0, length);
			}

			bos.close();

			inStream.close();

			return bos.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";

	}

	public static boolean save2File(byte[] data, String filePath) {
		File file = new File(filePath);
		FileOutputStream fos = null;
		try {
			// 文件或目录不存在时,创建目录和文件.
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}

			// 写入数据
			fos = new FileOutputStream(file);
			fos.write(data);
			fos.close();

			return true;
		} catch (IOException ex) {
			Logger.e("[save2File]保存文件:)" + filePath + " 失败." + ex.getMessage());

			return false;
		}
	}

	

}
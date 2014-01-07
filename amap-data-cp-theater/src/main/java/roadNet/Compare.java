/**
 * 2013-10-22
 */
package roadNet;

import zengkunceju.ProjectionUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Compare {
	@SuppressWarnings("rawtypes")
	private static Map philiRoads = new HashMap();
	@SuppressWarnings("rawtypes")
	private static Map myRoads = new HashMap();

	@SuppressWarnings("rawtypes")
	private static Map philiHasOnly = new HashMap();
	@SuppressWarnings("rawtypes")
	private static Map myHasOnly = new HashMap();
	@SuppressWarnings("rawtypes")
	private static Map common = new HashMap();

	private static ProjectionUtil pUtil = new ProjectionUtil();
	private static int maxDistance = 5;

	// init philiRoads
	@SuppressWarnings("unchecked")
	private static void initphiliRoads() throws IOException {
		String path = "E://交叉口.txt";
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(path), "gbk"));
		String data;
		while ((data = reader.readLine()) != null) {
			String[] fields = data.split("	");
			String name = fields[0];
			name = full2HalfChange(name);
			name = getName(name);
			String x = fields[1];
			String y = fields[2];
			philiRoads.put(name, x + " " + y);
		}
	}

	// 名称规范化
	private static String getName(String name) {
		String result = "";
		name = name.replace("与", "_").replace("交叉口", "");
		String[] fields = name.split("_");
		if (fields[0].compareToIgnoreCase(fields[1]) < 0) {
			result = fields[0] + "_" + fields[1];
		} else {
			result = fields[1] + "_" + fields[0];
		}

		result = result.replace("_", "与");
		result += "交叉口";
		return result;
	}

	// init myRoads
	@SuppressWarnings("unchecked")
	private static void initmyRoads() throws IOException {
		String path = "E://allCoords.csv";
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(path), "gbk"));
		String data = reader.readLine();
		while ((data = reader.readLine()) != null) {
			String[] fields = data.split(",");
			String name = fields[0];
			name = full2HalfChange(name);
			name = name.replace("_", "与");
			name += "交叉口";
			name = getName(name);
			String coords = fields[1];
			myRoads.put(name, coords);
		}
	}

	public static void main(String[] args) throws IOException {
		initphiliRoads();
		initmyRoads();

		// phili中有 而我计算结果中没有的
		getDifferentPhili(philiRoads, myRoads);

		// 我计算结果中有的而phili中没有的
		getDifferentMy(myRoads, philiRoads);

		// 写结果
		System.out.println(philiHasOnly.size() + "," + myHasOnly.size() + ","
				+ common.size());
		writerResult("E://philiHasOnly.csv", philiHasOnly);
		writerResult("E://myHasOnly.csv", myHasOnly);
		writerResult("E://common.csv", common);
	}

	// 计算A中有而B中没有的
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void getDifferentPhili(Map a, Map b) {
		for (Object key : a.keySet()) {
			// b中有对应的名字
			if (b.containsKey(key)) {
				if (!assertDistance(a.get(key).toString(), b.get(key)
						.toString())) {
					philiHasOnly.put(key, a.get(key));
				}
			} else {
				philiHasOnly.put(key, a.get(key));
			}
		}
	}

	// 计算A中有而B中没有的
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void getDifferentMy(Map a, Map b) {
		for (Object key : a.keySet()) {
			// b中有对应的名字
			if (b.containsKey(key)) {
				if (!assertDistance(b.get(key).toString(), a.get(key)
						.toString())) {
					myHasOnly.put(key, a.get(key));
				} else {
					common.put(key, b.get(key));
				}
			} else {
				myHasOnly.put(key, a.get(key));
			}
		}
	}

	private static boolean assertDistance(String a, String b) {
		String[] axy = a.split(" ");
		double ax = Double.parseDouble(axy[0]);
		double ay = Double.parseDouble(axy[1]);

		if (b.contains(";")) {
			String[] fields = b.split(";");
			for (String xys : fields) {
				String[] bxy = xys.split(" ");
				double bx = Double.parseDouble(bxy[0]);
				double by = Double.parseDouble(bxy[1]);

				int distance = pUtil.ComputeFormCD(ax, ay, bx, by);
				if (distance < maxDistance) {
					return true;
				}
			}
		} else {
			String[] bxy = b.split(" ");
			double bx = Double.parseDouble(bxy[0]);
			double by = Double.parseDouble(bxy[1]);

			int distance = pUtil.ComputeFormCD(ax, ay, bx, by);
			if (distance < maxDistance) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	private static void writerResult(String writerpath, Map result)
			throws IOException {
		File file = new File(writerpath);
		file.mkdirs();
		file.delete();
		File f0 = new File(writerpath);
		try {
			f0.createNewFile();
			System.out.println(f0.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputStreamWriter writer = new OutputStreamWriter(
				new FileOutputStream(f0), "gbk");
		// 输出结果
		writer.write("名称,交叉点坐标");
		writer.write("\n");
		for (Object key : result.keySet()) {
			writer.write(key + "," + result.get(key));
			writer.write("\n");
		}
		writer.close();
	}

	public static final String full2HalfChange(String QJstr) {
		StringBuffer outStrBuf = new StringBuffer("");
		String Tstr = "";
		byte[] b = null;
		for (int i = 0; i < QJstr.length(); i++) {
			Tstr = QJstr.substring(i, i + 1);
			// 全角空格转换成半角空格
			if (Tstr.equals("　")) {
				outStrBuf.append(" ");
				continue;
			}
			try {
				b = Tstr.getBytes("unicode");
				// 得到 unicode 字节数据
				if (b[2] == -1) {
					// 表示全角
					b[3] = (byte) (b[3] + 32);
					b[2] = 0;
					outStrBuf.append(new String(b, "unicode"));
				} else {
					outStrBuf.append(Tstr);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		} // end for.
		return outStrBuf.toString();

	}
}

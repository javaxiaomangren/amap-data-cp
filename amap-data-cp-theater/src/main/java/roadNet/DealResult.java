/**
 * 2013-10-24
 */
package roadNet;

import java.io.*;

/**
 * 处理全国道路交叉点结果：名称规范化；过滤交叉点个数大于30个的；高速路出口提取出来（不作为交叉口）
 */
public class DealResult {
	//大于指定个数的交叉点被认为不正常的，单独输出
	private static int maxNum = 30;
	public static void main(String[] args) throws IOException {
		//写结果
		String path = "E:/rightResult.csv";
		File f0 = new File(path);
		try {
			f0.createNewFile();
			System.out.println(f0.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputStreamWriter writerRight = new OutputStreamWriter(
				new FileOutputStream(f0), "gbk");
		
		String path1 = "E:/muchResult.csv";
		File f1 = new File(path1);
		try {
			f1.createNewFile();
			System.out.println(f1.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		OutputStreamWriter writerMuch = new OutputStreamWriter(
				new FileOutputStream(f1), "gbk");
		
		// 读取结果数据
		path = "E://allCoords.csv";
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(path), "gbk"));
		String data = reader.readLine();
		while ((data = reader.readLine()) != null) {
			String[] fields = data.split(",");
			String name = fields[0];
			name = full2HalfChange(name);
			//判断名称是否是。。与。。出口，如果是，则忽略不计
			if(assertName(name)){
				continue;
			}
			name = name.replace("_", "与");
			name += "交叉口";
			name = getName(name);
			String coords = fields[1];
			
			//判断交叉点个数是否大于指定个数
//			String[] coordFields = coords.split(";");
////			if(coordFields.length > maxNum){
//				writerMuch.write(name + "," + coords);
//				writerMuch.write("\n");
////			} else {
				writerRight.write(name + "," + coords);
				writerRight.write("\n");
//			}
		}
		
		writerMuch.close();
		writerRight.close();
	}
	
	//判断是否是高速与高速出口或入口交叉口
	private static boolean assertName(String name){
		String[] fields = name.split("_");
		String name1 = fields[0].replace("入口", "").replace("出口", "");
		String name2 = fields[1].replace("入口", "").replace("出口", "");
		if(name1.equalsIgnoreCase(name2)){
			return true;
		}
		return false;
	}

	// 全角转为半角
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
}

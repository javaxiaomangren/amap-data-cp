/**
 * 2013-10-24
 */
package roadNet;

import java.io.*;

/**
 * 给定路名 找其所在的城市文件
 */
public class FindRoadCsv {
	private static String findName = "花集路";

	public static void main(String[] args) throws IOException {
		String path = "E:/13Q2版全国道路面数据";
		File f = new File(path);
		File[] files = f.listFiles();

		for (int k = files.length - 1; k >= 0; k--) {
			File file = files[k];
			String filePath = file.getPath();

			File f1 = new File(filePath);
			File[] csvs = f1.listFiles();
			for (File csv : csvs) {
				String roadPath = filePath + "/" + csv.getName();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(roadPath),
								"gbk"));
				String data;
				reader.readLine();
				boolean flag = true;
				while ((data = reader.readLine()) != null && flag) {
					String[] fields = data.split(",");
					// 名称为空的不考虑
					if (fields[2] == null || fields[2].equals("")) {
						continue;
					}
					String name = fields[2];
					if (findName.equalsIgnoreCase(name)) {
						System.out.println(csv.getName());
						flag = false;
					}
				}
			}
		}
	}
}

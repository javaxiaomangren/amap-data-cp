package com.amap.data.base;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.amap.base.db.DbUtil;
import com.amap.base.db.dao.GeneralizeDao;

public class InsertSqlGen {

	public static void main(String[] args) {
		System.out.print(getInsertSql("tuangou","busi_tuangou"));
	}

	@SuppressWarnings({ "static-access" })
	public static String getInsertSql(String database,String table_name) {
		String insert_column = "";
		String insert_value = "";
		String insert = "";

		try {
			DbUtil du = new DbUtil();
			GeneralizeDao dao = new GeneralizeDao();
			if (database == null) {
				dao = du.getGeneralizeDao();
			} else {
				dao = du.getGeneralizeDao(database);
			}
			List<String> columnList = dao.queryColumn(table_name);

			for (int i = 0; i < columnList.size(); i++) {
				String columnName = StringUtils.lowerCase(columnList.get(i));

				insert_column += "`" + columnName + "`";
				insert_value += ":" + columnName;

				if (i == columnList.size() - 1)
					break;

				insert_column += ",";
				insert_value += ",";
			}
			insert = "insert into " + table_name + " (" + insert_column
					+ ")values(" + insert_value + ")";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return insert;
	}
}

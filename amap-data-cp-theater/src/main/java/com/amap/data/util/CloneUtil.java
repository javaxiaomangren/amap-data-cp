/**
 * @author caoxuena
 *2012-12-13
 */
package com.amap.data.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author caoxuena 2012-12-13
 */
public class CloneUtil {
	public static final Object deepClone(Object obj) {
		ByteArrayOutputStream bo = null;
		ObjectOutputStream oo = null;
		ByteArrayInputStream bi = null;
		ObjectInputStream oi = null;
		Object resultObject = null;
		try {
			// 将对象写到流里
			bo = new ByteArrayOutputStream();
			oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);

			// 从流里读出来
			bi = new ByteArrayInputStream(bo.toByteArray());
			oi = new ObjectInputStream(bi);

			resultObject = oi.readObject();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
		} finally {
			try {
				if (oi != null) {
					oi.close();
					oi = null;
				}
				if (bi != null) {
					bi.close();
					bi = null;
				}
				if (oo != null) {
					oo.close();
					oo = null;
				}
				if (bo != null) {
					bo.close();
					bo = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultObject;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		Map a = new HashMap();
		List l = new ArrayList();
		a.put("l", l);

		Map b =a;
		
		l.add("11111");
		
		System.out.println(((List)b.get("l")).size());

	}
}

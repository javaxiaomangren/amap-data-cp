package com.amap.cms.dao;

import com.mongodb.*;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

public class GeneralDao {
	private static final Logger log = LoggerFactory.getLogger(GeneralDao.class);
	private MongoTemplate mongoTemplate;
	@SuppressWarnings("unused")
	private Integer nun = 0;
	private Integer dbnum = 0;

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	/**
	 * 获取集合
	 * 
	 * @param collectionName
	 *            集合名称
	 * @return 集合
	 */
	public DBCollection getCollection(String collectionName) {
		DB db = mongoTemplate.getDb();
		if (db == null) {
			log.error("get Collection is null!");
			return null;
		}
		return db.getCollection(collectionName);
	}

	/**
	 * 插入
	 * 
	 * @param collection
	 * @param o
	 *            插入
	 */
	public boolean insert(String collection, DBObject o) {
		WriteResult ret = null;

		try {
			ret = getCollection(collection).insert(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ret != null && ret.getError() == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 批量插入
	 * 
	 * @param collection
	 * @param list
	 *            插入的列表
	 */
	public boolean insertBatch(String collection, List<DBObject> list) {

		if (list == null || list.isEmpty()) {
			return false;
		}

		WriteResult ret = null;
		try {
			dbnum = dbnum + list.size();
//			log.info("listsize:" + list.size());
//			log.info("num:" + nun++);
//			log.info("dbnum" + dbnum);
			ret = getCollection(collection).insert(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ret.getError() == null) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 删除
	 * 
	 * @param collection
	 * @param q
	 *            查询条件
	 */
	public boolean delete(String collection, DBObject q) {

		WriteResult ret = null;
		try {
			ret = getCollection(collection).remove(q);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ret.getN() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 批量删除
	 * 
	 * @param collection
	 * @param list
	 *            删除条件列表
	 */
	public void deleteBatch(String collection, List<DBObject> list) {

		if (list == null || list.isEmpty()) {
			return;
		}

		try {
			for (int i = 0; i < list.size(); i++) {
				getCollection(collection).remove(list.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 计算集合总条数
	 * 
	 * @param collection
	 */
	public int getCount(String collection) {
		int count = 0;
		try {
			count = getCollection(collection).find().count();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 计算满足条件条数
	 * 
	 * @param collection
	 * @param q
	 *            查询条件
	 */

	public long getCount(String collection, DBObject q) {
		long count = 0;
		try {
			count = getCollection(collection).getCount(q);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 更新
	 * 
	 * @param collection
	 * @param q
	 *            查询条件
	 * @param setFields
	 *            更新对象
	 */
	public boolean update(String collection, DBObject q, DBObject setFields) {

		WriteResult ret = null;
		try {
			ret = getCollection(collection).updateMulti(q,
					new BasicDBObject("$set", setFields));
//			 System.out.println("update error:"+ret.getLastError());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ret.getN() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 更新
	 * 
	 * @param collection
	 * @param q
	 *            查询条件
	 * @param setFields
	 *            更新对象
	 */
	public boolean saveofupdate(String collection, DBObject q,
			DBObject setFields) {

		WriteResult ret = null;
		try {
			ret = getCollection(collection).update(q,
					new BasicDBObject("$set", setFields), true, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ret.getN() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 查找集合所有对象
	 * 
	 * @param collection
	 */
	public List<DBObject> findAll(String collection) {
		List<DBObject> list = new ArrayList<DBObject>();
		try {
			list = getCollection(collection).find().toArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 按顺序查找集合所有对象
	 * 
	 * @param collection
	 *            数据集
	 * @param orderBy
	 *            排序
	 */
	public List<DBObject> findAll(String collection, DBObject orderBy) {
		List<DBObject> list = new ArrayList<DBObject>();
		try {
			list = getCollection(collection).find().sort(orderBy).toArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 取最大uuid
	 * 
	 * @param collection
	 * @param orderBy
	 * @return
	 */
	public DBObject findMax(String collection, DBObject orderBy) {
		return getCollection(collection).find().sort(orderBy).limit(1)
				.toArray().get(0);
	}

	/**
	 * 按顺序查找集合所有对象
	 * 
	 * @param collection
	 *            数据集
	 * @param orderBy
	 *            排序
	 */
	public String findAllStr(String collection, DBObject orderBy) {
		String resultstr = null;
		try {

			resultstr = JSON.serialize(getCollection(collection).find()
					.sort(orderBy).toArray());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultstr;
	}

	/**
	 * 查找（返回一个对象）
	 * 
	 * @param collection
	 * @param q
	 *            查询条件
	 */
	public DBObject findOne(String collection, DBObject q) {
		DBObject dbobject = null;
		try {
			dbobject = getCollection(collection).findOne(q);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbobject;
	}

	/**
	 * 查找（返回一个对象）
	 * 
	 * @param collection
	 * @param q
	 *            查询条件
	 * @param fileds
	 *            返回字段
	 */
	public DBObject findOne(String collection, DBObject q, DBObject fileds) {
		DBObject dbobject = null;
		try {
			dbobject = getCollection(collection).findOne(q, fileds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbobject;
	}

	/**
	 * 查找返回特定字段（返回一个List<DBObject>）
	 * 
	 * @param collection
	 * @param q
	 *            查询条件
	 * @param fileds
	 *            返回字段
	 */
	public List<DBObject> findLess(String collection, DBObject q,
			DBObject fileds) {

		DBCursor c = null;
		try {
			c = getCollection(collection).find(q, fileds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (c != null)
			return c.toArray();
		else
			return null;
	}

	/**
	 * 查找返回特定字段（返回一个List<DBObject>）
	 * 
	 * @param collection
	 * @param q
	 *            查询条件
	 * @param fileds
	 *            返回字段
	 * @param orderBy
	 *            排序
	 */
	public List<DBObject> findLess(String collection, DBObject q,
			DBObject fileds, DBObject orderBy) {

		DBCursor c = null;
		try {
			c = getCollection(collection).find(q, fileds).sort(orderBy);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (c != null)
			return c.toArray();
		else
			return null;
	}

	/**
	 * 分页查找集合对象，返回特定字段
	 * 
	 * @param collection
	 * @param q
	 *            查询条件
	 * @param fileds
	 *            返回字段
	 * @pageNo 第n页
	 * @perPageCount 每页记录数
	 */
	public List<DBObject> findLess(String collection, DBObject q,
			DBObject fileds, int pageNo, int perPageCount) {
		List<DBObject> list = new ArrayList<DBObject>();
		try {
			list = getCollection(collection).find(q, fileds)
					.skip((pageNo - 1) * perPageCount).limit(perPageCount)
					.toArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 按顺序分页查找集合对象，返回特定字段
	 * 
	 * @param collection
	 *            集合
	 * @param q
	 *            查询条件
	 * @param fileds
	 *            返回字段
	 * @param orderBy
	 *            排序
	 * @param pageNo
	 *            第n页
	 * @param perPageCount
	 *            每页记录数
	 */
	public List<DBObject> findLess(String collection, DBObject q,
			DBObject fileds, DBObject orderBy, int pageNo, int perPageCount) {
		List<DBObject> list = new ArrayList<DBObject>();
		try {
			list = getCollection(collection).find(q, fileds).sort(orderBy)
					.skip((pageNo - 1) * perPageCount).limit(perPageCount)
					.toArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 按顺序分页查找集合对象
	 * 
	 * @param collection
	 *            集合
	 * @param q
	 *            查询条件
	 * @param orderBy
	 *            排序
	 * @param pageNo
	 *            第n页
	 * @param perPageCount
	 *            每页记录数
	 */
	public List<DBObject> findLessd(String collection, DBObject q,
			DBObject orderBy, int pageNo, int perPageCount) {
		List<DBObject> list = new ArrayList<DBObject>();
		try {
			list = getCollection(collection).find(q).sort(orderBy)
					.skip((pageNo - 1) * perPageCount).limit(perPageCount)
					.toArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 查找（返回一个List<DBObject>）
	 * 
	 * @param collection
	 * @param q
	 *            查询条件
	 */
	public List<DBObject> find(String collection, DBObject q) {

		DBCursor c = null;
		try {
			c = getCollection(collection).find(q);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (c != null)
			return c.toArray();
		else
			return null;
	}

	/**
	 * 按顺序查找（返回一个List<DBObject>）
	 * 
	 * @param collection
	 * @param q
	 *            查询条件
	 * @param orderBy
	 *            排序
	 */
	public List<DBObject> find(String collection, DBObject q, DBObject orderBy) {

		DBCursor c = null;
		try {
			c = getCollection(collection).find(q).sort(orderBy);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (c != null)
			return c.toArray();
		else
			return null;
	}

	/**
	 * 分页查找集合对象
	 * 
	 * @param collection
	 * @param q
	 *            查询条件
	 * @pageNo 第n页
	 * @perPageCount 每页记录数
	 */
	public List<DBObject> find(String collection, DBObject q, int pageNo,
			int perPageCount) {
		DBCursor c = null;
		try {
			c = getCollection(collection).find(q)
					.skip((pageNo - 1) * perPageCount).limit(perPageCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c.toArray();
	}

	/**
	 * 按顺序分页查找集合对象
	 * 
	 * @param collection
	 *            集合
	 * @param q
	 *            查询条件
	 * @param orderBy
	 *            排序
	 * @param pageNo
	 *            第n页
	 * @param perPageCount
	 *            每页记录数
	 */
	public List<DBObject> find(String collection, DBObject q, DBObject orderBy,
			int pageNo, int perPageCount) {
		DBCursor c = null;
		try {
			c = getCollection(collection).find(q).sort(orderBy)
					.skip((pageNo - 1) * perPageCount).limit(perPageCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c.toArray();
	}

	/**
	 * distinct操作
	 * 
	 * @param collection
	 *            集合
	 * @param field
	 *            distinct字段名称
	 */
	@SuppressWarnings("rawtypes")
	public Object[] distinct(String collection, String field) {
		List list = new ArrayList();
		try {
			list = getCollection(collection).distinct(field);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list.toArray();
	}

	/**
	 * distinct操作
	 * 
	 * @param collection
	 *            集合
	 * @param field
	 *            distinct字段名称
	 * @param q
	 *            查询条件
	 */
	@SuppressWarnings("rawtypes")
	public Object[] distinct(String collection, String field, DBObject q) {
		List list = new ArrayList();
		try {
			list = getCollection(collection).distinct(field, q);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list.toArray();
	}

	/**
	 * 删除集合
	 * 
	 * @param collection
	 */
	public boolean dropCollection(String collection) {
		try {
			getCollection(collection).drop();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
/**
 * 获取uuid
 * @param collection
 * @return
 */
	public int getMaxID(String collection) {

		DBObject query = new BasicDBObject();

		query.put("name", collection);

		DBObject update = new BasicDBObject();

		update.put("$inc", new BasicDBObject().append("id", 1));

		DBObject result =getCollection("ids").findAndModify(query, null, null, false,
				update, true, false);

		int id = 0;

		if (result == null) {

			insert("ids", new BasicDBObject().append("name", collection)
					.append("id", 1));

			id = 1;

		} else {

			id = Integer.parseInt(result.get("id").toString());

		}

		return id;

	}

}

package com.amap.data.util.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class GeneralDao {
	private static final Logger log = LoggerFactory.getLogger(GeneralDao.class);
	private MongoTemplate mongoTemplate;

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	/**
	 * ��ȡ����
	 * 
	 * @param collectionName
	 *            �������
	 * @return ����
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
	 * ����
	 * 
	 * @param collection
	 * @param o
	 *            ����
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
	 * ��������
	 * 
	 * @param collection
	 * @param list
	 *            ������б�
	 */
	public boolean insertBatch(String collection, List<DBObject> list) {

		if (list == null || list.isEmpty()) {
			return false;
		}

		WriteResult ret = null;
		try {
			ret = getCollection(collection).insert(list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (ret.getError() == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ɾ��
	 * 
	 * @param collection
	 * @param q
	 *            ��ѯ����
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
	 * ����ɾ��
	 * 
	 * @param collection
	 * @param list
	 *            ɾ�������б�
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
	 * ���㼯��������
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
	 * ����������������
	 * 
	 * @param collection
	 * @param q
	 *            ��ѯ����
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
	 * ����
	 * 
	 * @param collection
	 * @param q
	 *            ��ѯ����
	 * @param setFields
	 *            ���¶���
	 */
	public boolean update(String collection, DBObject q, DBObject setFields) {

		WriteResult ret = null;
		try {
			// System.out.println("dao update query="+q);
			// System.out.println("dao update setFields="+setFields);
			ret = getCollection(collection).updateMulti(q,
					new BasicDBObject("$set", setFields));
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
	 * ���Ҽ������ж���
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
	 * ��˳����Ҽ������ж���
	 * 
	 * @param collection
	 *            ��ݼ�
	 * @param orderBy
	 *            ����
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
	 * ���ң�����һ������
	 * 
	 * @param collection
	 * @param q
	 *            ��ѯ����
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
	 * ���ң�����һ������
	 * 
	 * @param collection
	 * @param q
	 *            ��ѯ����
	 * @param fileds
	 *            �����ֶ�
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
	 * ���ҷ����ض��ֶΣ�����һ��List<DBObject>��
	 * 
	 * @param collection
	 * @param q
	 *            ��ѯ����
	 * @param fileds
	 *            �����ֶ�
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
	 * ���ҷ����ض��ֶΣ�����һ��List<DBObject>��
	 * 
	 * @param collection
	 * @param q
	 *            ��ѯ����
	 * @param fileds
	 *            �����ֶ�
	 * @param orderBy
	 *            ����
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
	 * ��ҳ���Ҽ��϶��󣬷����ض��ֶ�
	 * 
	 * @param collection
	 * @param q
	 *            ��ѯ����
	 * @param fileds
	 *            �����ֶ�
	 * @pageNo ��nҳ
	 * @perPageCount ÿҳ��¼��
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
	 * ��˳���ҳ���Ҽ��϶��󣬷����ض��ֶ�
	 * 
	 * @param collection
	 *            ����
	 * @param q
	 *            ��ѯ����
	 * @param fileds
	 *            �����ֶ�
	 * @param orderBy
	 *            ����
	 * @param pageNo
	 *            ��nҳ
	 * @param perPageCount
	 *            ÿҳ��¼��
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
	 * ���ң�����һ��List<DBObject>��
	 * 
	 * @param collection
	 * @param q
	 *            ��ѯ����
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
	 * ��˳����ң�����һ��List<DBObject>��
	 * 
	 * @param collection
	 * @param q
	 *            ��ѯ����
	 * @param orderBy
	 *            ����
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
	 * ��ҳ���Ҽ��϶���
	 * 
	 * @param collection
	 * @param q
	 *            ��ѯ����
	 * @pageNo ��nҳ
	 * @perPageCount ÿҳ��¼��
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
	 * ��˳���ҳ���Ҽ��϶���
	 * 
	 * @param collection
	 *            ����
	 * @param q
	 *            ��ѯ����
	 * @param orderBy
	 *            ����
	 * @param pageNo
	 *            ��nҳ
	 * @param perPageCount
	 *            ÿҳ��¼��
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
	 * distinct����
	 * 
	 * @param collection
	 *            ����
	 * @param field
	 *            distinct�ֶ����
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
	 * distinct����
	 * 
	 * @param collection
	 *            ����
	 * @param field
	 *            distinct�ֶ����
	 * @param q
	 *            ��ѯ����
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
	 * ��ȡ�����������ID
	 * 
	 * @param collection
	 *            �������
	 * @return ���ID
	 */
	public int getMaxID(String collection) {
		DBCollection idscol = getCollection("ids");
		DBObject query = new BasicDBObject();
		query.put("name", collection);
		DBObject update = new BasicDBObject();
		update.put("$inc", new BasicDBObject().append("id", 1));
		DBObject result = idscol.findAndModify(query, null, null, false,
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

	/**
	 * ɾ���
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

	public boolean upsert(String collection, DBObject q, DBObject o,
			boolean multi) {
		try {
			getCollection(collection).update(q, o, true, multi);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean createIndex(String collection, DBObject keys, DBObject options) {
		try {
			getCollection(collection).createIndex(keys, options);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean createIndex(String collection, DBObject keys) {
		try {
			getCollection(collection).createIndex(keys);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


}

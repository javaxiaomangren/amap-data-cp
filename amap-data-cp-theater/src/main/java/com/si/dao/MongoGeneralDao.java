package com.si.dao;

import com.mongodb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

public class MongoGeneralDao {
	private static final Logger log = LoggerFactory.getLogger(MongoGeneralDao.class);
	private MongoTemplate mongoTemplate;
	
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	

	public DBCollection getCollection(String collectionName) {
		DB db = mongoTemplate.getDb();
		if(db==null) {
			log.error("get Collection is null!");
			return null;
		}
		return db.getCollection(collectionName);
	}

	public boolean insert(String collection, DBObject o) {
		WriteResult ret = null;
		
		try {
			ret = getCollection(collection).insert(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(ret!=null && ret.getError()==null) {
			return true;
		}else {
			return false;
		}	
	}


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
		if(ret.getError()==null) {
			return true;
		}else {
			return false;
		}
	}


	public boolean delete(String collection, DBObject q) {

		WriteResult ret = null;
		try {
			ret = getCollection(collection).remove(q);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(ret.getN()>0) {
			return true;
		}else {
			return false;
		}
	}


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

	public int getCount(String collection) {
		int count = 0;
		try {
			count = getCollection(collection).find().count();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	public long getCount(String collection, DBObject q) {
		long count = 0;
		try {
			count = getCollection(collection).getCount(q);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}


	public boolean update(String collection, DBObject q, DBObject setFields) {

		WriteResult ret = null;
		try {
			ret = getCollection(collection).updateMulti(q,
					new BasicDBObject("$set", setFields));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(ret.getN()>0) {
			return true;
		}else {
			return false;
		}
	}


	public List<DBObject> findAll(String collection) {
		List<DBObject> list = new ArrayList<DBObject>();
		try {
			list = getCollection(collection).find().toArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}


	public List<DBObject> findAll(String collection, DBObject orderBy) {
		List<DBObject> list = new ArrayList<DBObject>();
		try {
			list = getCollection(collection).find().sort(orderBy)
					.toArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	
	public DBObject findOne(String collection, DBObject q) {
		DBObject dbobject = null;
		try {
			dbobject = getCollection(collection).findOne(q);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbobject;
	}

	public DBObject findOne(String collection, DBObject q, DBObject fileds) {
		DBObject dbobject = null;
		try {
			dbobject = getCollection(collection).findOne(q, fileds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbobject;
	}

	
	public List<DBObject> findLess(String collection, DBObject q, DBObject fileds) {

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

	
	public List<DBObject> findLess(String collection, DBObject q, DBObject fileds, DBObject orderBy) {

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

	
	public List<DBObject> findLess(String collection, DBObject q, DBObject fileds, int pageNo,
			int perPageCount) {
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

	
	public List<DBObject> findLess(String collection, DBObject q, DBObject fileds, DBObject orderBy,
			int pageNo, int perPageCount) {
		List<DBObject> list = new ArrayList<DBObject>();
		try {
			list = getCollection(collection).find(q, fileds)
					.sort(orderBy).skip((pageNo - 1) * perPageCount)
					.limit(perPageCount).toArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	
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

	
	public List<DBObject> find(String collection, DBObject q, DBObject orderBy,
			int pageNo, int perPageCount) {
		DBCursor c = null;
		try {
			c = getCollection(collection).find(q)
					.sort(orderBy).skip((pageNo - 1) * perPageCount)
					.limit(perPageCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c.toArray();
	}

	
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
	
	
	public int getMaxID(String collection) {
		DBCollection idscol =  getCollection("ids");
		DBObject query = new BasicDBObject();
		query.put("name",collection);
		DBObject update = new BasicDBObject();
		update.put("$inc",new BasicDBObject().append("id", 1));
		DBObject result = idscol.findAndModify(query, null, null, false, update, true, false);
		int id = 0;
		if(result==null) {
			insert("ids", new BasicDBObject().append("name", collection).append("id", 1));
			id = 1;
		}else {
			id = Integer.parseInt(result.get("id").toString());
		}
		return id;
	}
	
	
	public boolean dropCollection(String collection) {
		try {
			getCollection(collection).drop();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List getDistinct(String CollectionName,String distinctKey) {
		DB db = mongoTemplate.getDb();
		if(db==null) {
			log.error("get Collection is null!");
			return null;
		}
		
		DBObject dbo = new BasicDBObject();
		dbo.put("distinct", CollectionName);
		dbo.put("key", distinctKey);
		
		CommandResult cr = db.command(dbo);
		return (List) cr.get("values");
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
}

package com.dr.spider.utils.helper;

import com.dr.spider.constant.GlobalConst;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MongodbHelper {


  public static MongoClient getClient() {
    return new MongoClient(new MongoClientURI(GlobalConst.MONGOURI));
  }

  public static MongoDatabase getDatabase() {
    return getDatabase(GlobalConst.DATABASE_NAME);
  }

  public static MongoCollection<Document> getCollection(String collName) {
    return getDatabase().getCollection(collName);
  }

  public static MongoDatabase getDatabase(String databasesName) {
    return getClient().getDatabase(databasesName);
  }


  /**
   * 查找一个
   */
  public static Document findOne(BasicDBObject query, String collName) {
    MongoCollection<Document> coll = getCollection(collName);

    // FindIterable<Document> findIterable = coll.find(query).limit(1);
    FindIterable<Document> findIterable = coll.find(query);
    MongoCursor<Document> mongoCursor = findIterable.iterator();
    Document result=null;
    if(mongoCursor.hasNext()){
      result=mongoCursor.next();
    }
    return result;
  }

  /**
   * 查找集合内所有Document
   * @param collName
   * @return
   */
  public List<Document> findAll(String collName){
    MongoCollection<Document> coll =  getCollection(collName);
    List<Document> result = new ArrayList<>();
    FindIterable<Document> findIterable = coll.find();
    MongoCursor<Document> mongoCursor = findIterable.iterator();
    while(mongoCursor.hasNext()){
      result.add(mongoCursor.next());
    }
    return result;
  }


  /**
   * 指定条件查找
   * @param query
   * @param collName
   * @return
   */
  public List<Document> findAll(BasicDBObject query, String collName){
    MongoCollection<Document> coll =  getCollection(collName);
    List<Document> result = new ArrayList<>();
    FindIterable<Document> findIterable = coll.find(query);
    MongoCursor<Document> mongoCursor = findIterable.iterator();
    while(mongoCursor.hasNext()){
      result.add(mongoCursor.next());
    }
    return result;
  }


  /**
   * 指定条件查找指定字段
   * @param query
   * @param collName
   * @return
   */
  public List<Document> findAll(BasicDBObject query, BasicDBObject key, String collName){
    MongoCollection<Document> coll = getCollection(collName);
    List<Document> result = new ArrayList<>();
    FindIterable<Document> findIterable = coll.find(query).projection(key);
    MongoCursor<Document> mongoCursor = findIterable.iterator();
    while(mongoCursor.hasNext()){
      result.add(mongoCursor.next());
    }
    return result;
  }


  /**
   * 插入单个文档
   * @param doc Bson文档
   * @param collName 集合名称
   */
  public static void insert(Document doc, String collName){
    MongoCollection<Document> coll = getCollection(collName);
    coll.insertOne(doc);
  }


  /**
   * 批量插入文档
   * @param list List类型文档
   * @param collName 集合名称
   */
  public void insert(List<Document> list, String collName){
    MongoCollection<Document> coll = getCollection(collName);
    coll.insertMany(list);
  }

  /**
   * 删除集合中的所有数据
   * @param collName
   */
  public void deleteAll(String collName){
    MongoCollection<Document> coll = getCollection(collName);
    BasicDBObject delDbo=new BasicDBObject();
    delDbo.append("_id", -1);
    coll.deleteMany(Filters.not(delDbo));
  }


  /**
   * 删除指定的所有数据
   * @param b
   * @param collName
   */
  public void deleteAll(Bson b, String collName){
    MongoCollection<Document> coll = getCollection(collName);
    coll.deleteMany(b);
  }


  /**
   * 删除指定的一条数据
   * @param b
   * @param collName
   */
  public void deleteOne(Bson b, String collName){
    MongoCollection<Document> coll = getCollection(collName);
    coll.deleteOne(b);
  }






  /**
   * 按查询条件批量修改
   * @param b
   * @param doc
   * @param collName
   */
  public void updateAll(Bson b, Document doc, String collName){
    MongoCollection<Document> coll = getCollection(collName);
    coll.updateMany(b, doc);
  }






}

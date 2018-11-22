package com.Util;

import com.Globals;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

public class MongoDBJDBC {

    /**
     * 建立数据库服务
     *
     * bug:
     * 数据库连接要持久化
     * 不知道如何持久化，模仿vertx???
     */
    public static MongoDatabase connectToMongoDB(){
        try {
            MongoClient mongoClient = new MongoClient(Globals.LocalDBHost, Globals.LocalDBPort);// 连接到 mongodb 服务
            MongoDatabase mongoDatabase = mongoClient.getDatabase(Globals.LocalDBName);// 连接到数据库
            System.out.println("Connect to database:" + Globals.LocalDBName + " successfully");
            return mongoDatabase;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 连接集合collectionName
     */
    public static MongoCollection<Document> connectToCollection(String collectionName) {
        MongoDatabase mongoDatabase = connectToMongoDB();
        MongoCollection<Document> collection = null;
        try {
            collection = mongoDatabase.getCollection(collectionName);
        } catch (Exception e) {
            System.out.println("connect to collection:" + collectionName + " fail !");
            return null;
        }
        System.out.println("connect to collection:" + collectionName + " successfully !");
        return collection;
    }

    /**
     * 向collectionName插入document
     *
     * example:
     * Document document = new Document("x", 1).append("y", 0);
     * insertDocument(Globals.point, document);
     */
    public static boolean insertDocument(String collectionName, Document document) {
        if(document.size() == 0 || document == null) return true;
        boolean insertFlag = false;
        try {
            MongoCollection<Document> collection = connectToCollection(collectionName);
            collection.insertOne(document);
            insertFlag = true;
            System.out.println("document insert into " +  collectionName + " successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return insertFlag;
        }
        return insertFlag;
    }

    /**
     * 插入List<Document> documentList
     */
    public static boolean insertDocumentList(String collectionName, List<Document> documentList) {
        if(documentList == null || documentList.size() == 0) return true;
        try {
            MongoCollection<Document> collection = connectToCollection(collectionName);
            collection.insertMany(documentList);
            System.out.println("documents insert into " +  collectionName + " successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 查询
     *
     * example:
     * Document query = new Document("x",1);
     * FindIterable<Document> queryRes = queryDocument(Globals.point, query);
     *      for (Document qr : queryRes) {
     *      System.out.println(qr);
     * }
     */
    public static FindIterable<Document> queryDocument(String collectionName, Document query) {
        MongoCollection<Document> collection = connectToCollection(collectionName);
        FindIterable<Document> findIterable = null;
        if(query == null)
            findIterable = collection.find();
        else
            findIterable = collection.find(query);
        return findIterable;
    }

}

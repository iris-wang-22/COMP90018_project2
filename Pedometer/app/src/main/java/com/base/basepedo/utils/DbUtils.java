package com.base.basepedo.utils;

import android.content.Context;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.util.List;


public class DbUtils {

    public static String DB_NAME;
    public static LiteOrm liteOrm;

    public static void createDb(Context _activity, String DB_NAME) {
        DB_NAME = DB_NAME + ".db";
        if (liteOrm == null) {
            liteOrm = LiteOrm.newCascadeInstance(_activity, DB_NAME);
            liteOrm.setDebugged(true);
        }
    }

    public static LiteOrm getLiteOrm() {
        return liteOrm;
    }


    public static <T> void insert(T t) {
        liteOrm.save(t);
    }


    public static <T> void insertAll(List<T> list) {
        liteOrm.save(list);
    }


    public static <T> List<T> getQueryAll(Class<T> cla) {
        return liteOrm.query(cla);
    }


    public static <T> List<T> getQueryByWhere(Class<T> cla, String field, String[] value) {
        return liteOrm.<T>query(new QueryBuilder(cla).where(field + "=?", value));
    }


    public static <T> List<T> getQueryByWhereLength(Class<T> cla, String field, String[] value, int start, int length) {
        return liteOrm.<T>query(new QueryBuilder(cla).where(field + "=?", value).limit(start, length));
    }


    public static <T> void deleteAll(Class<T> cla) {
        liteOrm.deleteAll(cla);
    }

    public static <T> void update(T t) {
        liteOrm.update(t, ConflictAlgorithm.Replace);
    }
    
    public static <T> void updateALL(List<T> list) {
        liteOrm.update(list);
    }

    public static void closeDb(){
        liteOrm.close();
    }

}

package controllers;

import model.PostListInfo;
import model.Record;
import play.api.db.DB$;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.DBUtil;
import util.TimeUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Map;

/**
 * Created by jiangecho on 15/8/1.
 */
public class Post extends Controller {

    public static Result collectPost(){
        String uid = session("id");
        if (uid == null || uid.length() == 0){
            return ok();
        }

        Map<String, String[]> params = request().body().asFormUrlEncoded();
        String title = params.get("title")[0];
        String url = params.get("url")[0];

        String dateArray[] = params.get("date");
        String date;
        if (dateArray != null){
            date = dateArray[0];
        }else {
            date = new Date().toString();
        }

        //String sql = "INSERT INTO t_train_record(owner_id, start, end) VALUES('%s', %s, %s)";
        String sql = "INSERT INTO t_post_collect(owner_id, title, date, url) VALUES(%s, '%s', '%s', '%s')";
        title = title.replaceAll("'", "''");
        sql = String.format(sql, uid, title, date, url);
        long id = DBUtil.insert(sql);

        return ok(id + "");
    }

    public static Result getCollectedPosts(long lastId){
        String uid = session("id");
        PostListInfo postListInfo = new PostListInfo();

        if (uid == null || uid.length() == 0){
            return ok();
        }

        String sql = "SELECT * FROM t_post_collect WHERE owner_id = %s AND id < %d ORDER BY id DESC LIMIT 20";
        sql = String.format(sql, uid, lastId);
        try {
            Connection connection = DB.getConnection();
            Statement statement = connection.createStatement();
            //ResultSet result = DBUtil.queryLastRecord(statement, "t_post_collect", "owner_id = " + uid + " and id > " + lastId, "id", 10);
            ResultSet result = statement.executeQuery(sql);
            postListInfo = new PostListInfo(result);

            if (statement != null){
                statement.close();
            }

            if (connection != null){
                connection.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return ok(Json.toJson(postListInfo));

    }
}

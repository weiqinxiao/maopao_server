package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.tencent.xinge.XingeApp;
import model.BaseComment;
import model.CommentListResult;
import model.CommentResult;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.DBUtil;
import util.TextContentUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jiangecho on 15/5/10.
 */
public class Comment extends Controller{

    public static Result comments(long id, int pageSize){
        String sql = "SELECT * FROM t_comment WHERE tweet_id = %s ORDER BY ID LIMIT %s";
        sql = String.format(sql, id, pageSize);

        Connection connection = DB.getConnection();
        ResultSet resultSet = null;
        Statement statement = null;
        CommentListResult commentListResult = new CommentListResult();
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            List<BaseComment> comments = new ArrayList<BaseComment>();

            while (resultSet.next()){
                comments.add(new BaseComment(resultSet));
            }

            commentListResult.setCode(0);
            commentListResult.setComments(comments);
        } catch (SQLException e) {
            commentListResult.setCode(-1);
            e.printStackTrace();
        }finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        //JsonNode jsonNode = Json.toJson(commentListResult);
        return ok(Json.toJson(commentListResult));
    }

    public static Result publishComment(long id){

        CommentResult commentResult;
        Map<String, String[]> param = request().body().asFormUrlEncoded();
        String content = param.get("content")[0];

        content = TextContentUtil.processComment(content);
        String uid = session("id");

        String insertComment = "INSERT INTO t_comment ( owner_id, create_at, tweet_id, content) VALUES (%s,  now(), %s, '%s')";
        insertComment = String.format(insertComment, uid, id, content);

        String queryComment = "SELECT * FROM t_comment where id = ";
        String queryOwnId = "SELECT owner_id FROM t_tweet where id = " + id;

        long rowId;

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;


        try{
            connection = DB.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(insertComment);
            rowId = DBUtil.queryLastId(statement);

            DBUtil.increaseOneById(statement, "t_tweet", "comment_count", id);

            resultSet = statement.executeQuery(queryComment + rowId);
            resultSet.next();
            BaseComment comment = new BaseComment(resultSet);
            commentResult = new CommentResult(0, comment);

            resultSet = statement.executeQuery(queryOwnId);
            if (resultSet.next()){
                String ownerId = resultSet.getString("owner_id");
                // for android
                // xg push
                ownerId = "plank_" + ownerId;
                XingeApp.pushAccountAndroid(2100112110, "9a4277af000f76b89d0f9d0c41f86e5c", "平板支撑", "有同学评论了你的冒泡~", ownerId);
            }


        }catch (SQLException e){
            e.printStackTrace();
            commentResult = new CommentResult(-1, null);
        }finally {
            try {
                if (resultSet != null){
                    resultSet.close();
                }
                if (statement != null){
                    statement.close();
                }
                if (connection != null){
                    connection.close();
                }

            }catch (SQLException e){

            }

        }

        return ok(Json.toJson(commentResult));
    }
}

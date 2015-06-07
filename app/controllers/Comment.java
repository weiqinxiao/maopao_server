package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.tencent.xinge.Message;
import com.tencent.xinge.XingeApp;
import model.BaseComment;
import model.CommentListResult;
import model.CommentResult;
import model.UserObject;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.DBUtil;
import util.TextContentUtil;
import util.XGUtil;

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

        String sql = "SELECT t_user.id, t_user.name, t_user.head_url, t_comment.id, t_comment.content, t_comment.create_at, t_user.created_at " +
                "FROM t_user INNER JOIN t_comment ON t_user.id = t_comment.owner_id WHERE tweet_id = %s ORDER BY t_comment.id ASC LIMIT %s";
        sql = String.format(sql, id, pageSize);

        Connection connection = DB.getConnection();
        ResultSet commentResultSet = null;
        Statement commentStatement = null;
        CommentListResult commentListResult = new CommentListResult();
        try {
            commentStatement = connection.createStatement();
            List<BaseComment> comments = new ArrayList<BaseComment>();

            BaseComment comment;
            commentResultSet = commentStatement.executeQuery(sql);
            while (commentResultSet.next()){
                //BaseComment(String id, String owner_id, String tweet_id, String content, String created_at)
                comment = new BaseComment(commentResultSet.getString(4),
                        commentResultSet.getString(1),
                        id + "",
                        commentResultSet.getString(5),
                        commentResultSet.getTimestamp(6).getTime()
                );
                //UserObject(long id, String name, String headImgUrl, long created_at){
                comment.owner = new UserObject(commentResultSet.getLong(1),
                        commentResultSet.getString(2),
                        commentResultSet.getString(3),
                        commentResultSet.getLong(7)
                );
                comments.add(comment);

            }

            commentListResult.setCode(0);
            commentListResult.setComments(comments);
        } catch (SQLException e) {
            commentListResult.setCode(-1);
            e.printStackTrace();
        }finally {
            try {
                if (commentResultSet != null){
                    commentResultSet.close();
                }

                if (commentStatement != null){
                    commentStatement.close();
                }

                if (connection != null){
                    connection.close();
                }
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
                XGUtil.pushCommentNotificationToSingelAccountAndroid(ownerId, id);
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

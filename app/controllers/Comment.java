package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import model.BaseComment;
import model.CommentListResult;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.DBUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
}

package controllers;

import model.*;
import play.libs.Json;
import play.mvc.*;
import play.db.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by jiangecho on 15/5/2.
 */
public class App extends Controller{
    private static final String QUERY_LAST_ID = "SELECT LAST_INSERT_ID()";
    private static final String LAST_ROW_ID_COLUMN_NAME = "LAST_INSERT_ID()";

    public static Result index(){
       return ok("HELLO world");
    }

    public static Result test(){
        Connection connection = DB.getConnection();
        String restult = "";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from t_user_log");
            resultSet.next();
            restult += resultSet.getString(3);

            // TODO check null pointer
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ok("test " + restult + " ok");
    }

    public static Result test(String value){
       return ok("test " + value);
    }


    public static Result publicTweets(long last_id, String sort){
        String result = "";
        List<Maopao> maopaos = null;
        Maopao maopao;
        MaopaoList maopaoList = new MaopaoList();

        String sortType = " order by id ";
        if ("hot".equals(sort)){
            sortType = " order by comment_count ";
        }

        String queryTweet = "SELECT * FROM t_tweet where id > " + last_id + sortType + " limit 30";
        String queryComment = "SELECT * FROM t_comment WHERE id = ";
        Connection connection = null;
        Statement maopaoStatement = null;
        Statement commentStatement = null;
        ResultSet maopaoResultSet = null;
        ResultSet commentResultSet = null;
        long tweetId;
        try{
            connection = DB.getConnection();
            maopaoStatement = connection.createStatement();
            commentStatement = connection.createStatement();
            maopaoResultSet = maopaoStatement.executeQuery(queryTweet);

            maopaos = new ArrayList<Maopao>();
            while (maopaoResultSet.next()){
                maopao = new Maopao(maopaoResultSet);
                maopao.owner = new UserObject();
                if (maopao.likes > 0){
                    maopao.like_users = new ArrayList<UserObject>();
                    for (int i = 0; i < maopao.likes; i ++){
                        // TODO the following line is just for testing
                        maopao.like_users.add(maopao.owner);
                    }
                }
                if (maopao.comments > 0){
                    tweetId = Long.parseLong(maopao.id);
                    maopao.comment_list = new ArrayList<BaseComment>();
                    BaseComment comment;
                    commentResultSet = commentStatement.executeQuery(queryComment + tweetId);
                    while (commentResultSet.next()){
                        comment = new BaseComment(commentResultSet);
                        comment.owner = new UserObject();
                        maopao.comment_list.add(comment);

                    }

                }
                maopaos.add(maopao);
            }

            maopaoList.setCode(0);
            maopaoList.setData(maopaos);
        }catch (SQLException e){
            e.printStackTrace();
            maopaoList.setCode(-1);
            maopaoList.setData(null);
        }finally {
            try {
                if (maopaoResultSet != null){
                    maopaoResultSet.close();
                }
                if (commentResultSet != null){
                    commentResultSet.close();
                }

                if (maopaoStatement != null){
                    maopaoStatement.close();
                }
                if (commentStatement != null){
                    commentStatement.close();
                }

                if (connection != null){
                    connection.close();
                }

            }catch (SQLException e){

            }

        }

        return ok(Json.toJson(maopaoList));

    }

    /**
     * the content-type should be application/x-www-form-urlencoded
     * @return
     */
    public static Result publishTweet(){

        TweetResult tweetResult;
        Http.Request request = request();
        Map<String, String[]> param = request.body().asFormUrlEncoded();
        String content = param.get("content")[0];
        String device = param.get("device")[0];

        // TODO use Session to store uid(owner_id)
        String owner_id = "123456";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String insertTweet = "INSERT INTO t_tweet (owner_id, create_at, sysversion, content, device, clansid) VALUES( '%s', now(), 'mac', '%s', '%s', 1)";
        String queryTweet = "SELECT * FROM t_tweet where id = ";
        String queryLastRowId = "SELECT LAST_INSERT_ID()";
        insertTweet = String.format(insertTweet, owner_id, content, device);
        long rowId;
        try {
            connection = DB.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(insertTweet);

            resultSet = statement.executeQuery(queryLastRowId);
            resultSet.next();
            rowId = resultSet.getLong("LAST_INSERT_ID()");

            resultSet = statement.executeQuery(queryTweet + rowId);
            resultSet.next(); // attention
            Maopao maopao = new Maopao(resultSet);
            tweetResult = new TweetResult(0, maopao);

        }catch (SQLException e){
            e.printStackTrace();
            tweetResult = new TweetResult(-1, null);

        }finally {
            try {
                if (resultSet != null){
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return ok(Json.toJson(tweetResult));
    }

    public static Result publishComment(long id){

        CommentResult commentResult;
        Map<String, String[]> param = request().body().asFormUrlEncoded();
        String content = param.get("content")[0];
        // TODO more fields;

        // TODO use cookies to store uid
        String uid = "12345";

        String insertComment = "INSERT INTO t_comment ( owner_id, create_at, tweet_id, content) VALUES (%s, now(), %s, '%s')";
        insertComment = String.format(insertComment, uid, id, content);

        String queryComment = "SELECT * FROM t_comment where id = ";

        long rowId;

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;


        try{
            connection = DB.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(insertComment);
            resultSet = statement.executeQuery(QUERY_LAST_ID);
            resultSet.next();
            rowId = resultSet.getLong(LAST_ROW_ID_COLUMN_NAME);

            resultSet = statement.executeQuery(queryComment + rowId);
            resultSet.next();
            BaseComment comment = new BaseComment(resultSet);
            commentResult = new CommentResult(0, comment);

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

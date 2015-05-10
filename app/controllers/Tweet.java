package controllers;

import model.*;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import util.DBUtil;

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
public class Tweet extends Controller{
    public static Result publicTweets(long last_id, String sort){
        String result = "";
        List<Maopao> maopaos = null;
        Maopao maopao;
        MaopaoList maopaoList = new MaopaoList();

        String orderBy = "id";
        if ("hot".equals(sort)){
            orderBy = "comment_count";
        }

        Connection connection = null;
        Statement maopaoStatement = null;
        Statement commentStatement = null;
        ResultSet maopaoResultSet = null;
        ResultSet commentResultSet = null;
        long tweetId;
        long maxTweetId;
        String tableComment = "t_comment";
        String tableMaopao = "t_tweet";
        try{
            connection = DB.getConnection();
            maopaoStatement = connection.createStatement();
            commentStatement = connection.createStatement();

            maxTweetId = DBUtil.queryMaxId(maopaoStatement, tableMaopao);

            if (last_id > maxTweetId){
                maopaoResultSet = DBUtil.queryLastRecord(maopaoStatement, tableMaopao, orderBy, 30);
            }else {
                maopaoResultSet = DBUtil.queryLessLastRecord(maopaoStatement, tableMaopao, orderBy, "" + last_id, 30);
            }


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
                    commentResultSet = DBUtil.queryBy(commentStatement, tableComment, "tweet_id", "" + tweetId);
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
        String content = null;
        // TODO i am not sure need to decode or not?
//        try {
//            content = URLDecoder.decode(param.get("content")[0], "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        content = param.get("content")[0];
        String device = param.get("device")[0];
        String owner_id = session("id");

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

    public static Result star(long tweetId, String type){
        String sql;
        if ("like".equals(type)){
            sql = "INSERT INTO t_like_tweet ( owner_id, tweet_id, create_at) values ( %s, %s, now())";
        }else {
            sql = "DELETE FROM t_like_tweet WHERE owner_id = %s AND tweet_id = %s";
        }
        sql = String.format(sql, session("id"), tweetId);

        Connection connection = DB.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;

        TweetResult tweetResult = new TweetResult();
        try {
            statement = connection.createStatement();
            statement.execute(sql);

            if ("like".equals(type)){
                DBUtil.increaseOneById(statement, "t_tweet", "like_count", tweetId);
            }else {
                DBUtil.decreaseOneById(statement, "t_tweet", "like_count", tweetId);
            }

            resultSet = DBUtil.queryBy(statement, "t_tweet", "id", tweetId+"");

            resultSet.next();
            Maopao maopao = new Maopao(resultSet);
            tweetResult.setCode(0);
            tweetResult.setMaopao(maopao);

        } catch (SQLException e) {
            e.printStackTrace();
            tweetResult.setCode(-1);
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
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return  ok(Json.toJson(tweetResult));
    }

}

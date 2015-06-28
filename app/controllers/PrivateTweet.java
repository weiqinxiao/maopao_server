package controllers;

import model.*;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
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

// ATTENTION: in order to ensure the id between t_tweet and t_private_tweet is different,
    // now the tmp solution is: the id of t_private_tweet start at 1000000000
    // ALTER TABLE `t_private_tweet` AUTO_INCREMENT=1000000000;
public class PrivateTweet extends Controller{
    public static final long PRIVATE_TWEET_START_ID = 1000000000;
    public static final String PRIVATE_TWEET_TABLE_NAME = "t_private_tweet";
    public static Result privateTweets(long last_id, String sort, int limit){
        String result = "";
        List<Maopao> maopaos = null;
        Maopao maopao;
        MaopaoList maopaoList = new MaopaoList();

        String uid = session("id");

        if (uid == null || uid.length() == 0){
            return ok(Json.toJson(maopaoList));
        }

        String orderBy = "id";
        if ("hot".equals(sort)){
            orderBy = "comment_count";
        }

        if (limit <= 0){
            limit = 30;
        }

        Connection connection = null;
        Statement maopaoStatement = null;
        Statement commentStatement = null;
        Statement likeUserStatement = null;
        Statement ownerStatement = null;
        ResultSet maopaoResultSet = null;
        ResultSet commentResultSet = null;
        ResultSet likeUsersResultSet = null;
        ResultSet ownerResultSet = null;
        long tweetId;
        long maxTweetId;
        String where = " owner_id = " + uid + " ";
        String tableComment = "t_comment";
        String tableMaopao = "t_private_tweet";
        try{
            connection = DB.getConnection();
            maopaoStatement = connection.createStatement();
            commentStatement = connection.createStatement();
            likeUserStatement = connection.createStatement();
            ownerStatement = connection.createStatement();

            maxTweetId = DBUtil.queryMaxId(maopaoStatement, tableMaopao);

            if (last_id > maxTweetId){
                maopaoResultSet = DBUtil.queryLastRecord(maopaoStatement, tableMaopao, where, orderBy, limit);
            }else {
                maopaoResultSet = DBUtil.queryLessLastRecord(maopaoStatement, tableMaopao, where, orderBy, "" + last_id, limit);
            }


            maopaos = new ArrayList<Maopao>();
            while (maopaoResultSet.next()){
                maopao = new Maopao(maopaoResultSet);
                tweetId = Long.parseLong(maopao.id);

                String userSql = "SELECT * FROM t_user WHERE id = " + maopao.owner_id;
                ownerResultSet = ownerStatement.executeQuery(userSql);
                ownerResultSet.next();
                maopao.owner = new UserObject(ownerResultSet);

                if (maopao.likes > 0){
                    maopao.like_users = new ArrayList<UserObject>();

                    String sql = "SELECT * FROM t_user WHERE id IN (SELECT owner_id FROM t_like_tweet WHERE tweet_id = '%s')";
                    sql = String.format(sql,  tweetId);
                    likeUsersResultSet = likeUserStatement.executeQuery(sql);

                    UserObject userObject;
                    while (likeUsersResultSet.next()){
                        userObject = new UserObject(likeUsersResultSet);
                        maopao.like_users.add(userObject);
                        if (userObject.id.equals(session("id"))){
                            maopao.liked = true;
                        }
                    }

                }
                if (maopao.comments > 0){
                    maopao.comment_list = new ArrayList<BaseComment>();
                    String sql = "SELECT t_user.id, t_user.name, t_user.head_url, t_comment.id, t_comment.content, t_comment.create_at, t_user.created_at " +
                            "FROM t_user INNER JOIN t_comment ON t_user.id = t_comment.owner_id WHERE tweet_id = %s";
                    sql = String.format(sql, tweetId);
                    BaseComment comment;
                    commentResultSet = commentStatement.executeQuery(sql);
                    while (commentResultSet.next()){
                        //BaseComment(String id, String owner_id, String tweet_id, String content, String created_at)
                        comment = new BaseComment(commentResultSet.getString(4),
                                                    commentResultSet.getString(1),
                                                    tweetId + "",
                                                    commentResultSet.getString(5),
                                                    commentResultSet.getTimestamp(6).getTime()
                        );
                        //UserObject(long id, String name, String headImgUrl, long created_at){
                        comment.owner = new UserObject(commentResultSet.getLong(1),
                                                        commentResultSet.getString(2),
                                                        commentResultSet.getString(3),
                                                        commentResultSet.getLong(7)
                                );
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

                if (likeUsersResultSet != null){
                    likeUsersResultSet.close();
                }
                if (likeUserStatement != null){
                    likeUserStatement.close();
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
    public static Result publishPrivateTweet(){

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
        String owner_id = "123";
        //String owner_id = session("id");

        return publishPrivateTweet(owner_id, content, device);
    }

    private static Result publishPrivateTweet(String owner_id, String content, String device){
        TweetResult tweetResult;

        content = TextContentUtil.processTweetContent(content);

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String insertTweet = "INSERT INTO t_private_tweet (owner_id, create_at, sysversion, content, device) VALUES( '%s', now(), 'mac', '%s', '%s')";
        String queryTweet = "SELECT * FROM t_private_tweet where id = ";
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

}

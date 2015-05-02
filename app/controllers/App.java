package controllers;

import model.BaseComment;
import model.Maopao;
import model.MaopaoList;
import model.UserObject;
import play.libs.Json;
import play.mvc.*;
import play.db.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by jiangecho on 15/5/2.
 */
public class App extends Controller{
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
}

package controllers;

import play.db.DB;
import play.mvc.Controller;
import play.mvc.Result;
import util.TimeUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by jiangecho on 15/5/28.
 */
public class Train extends Controller{

    public static Result getTodayFinishedTrainCount(){
        String sql = "SELECT count(1) from t_train WHERE start > %d";
        long todayStartMillis = TimeUtil.getTodayStartMillis();
        sql = String.format(sql, todayStartMillis);


        long count = 0;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DB.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()){
                count = resultSet.getLong(1);
                break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

        return ok(Long.toString(count + 100)); // haha, at least 100

    }
}

package controllers;

import model.*;
import play.libs.Json;
import play.mvc.*;
import play.db.*;
import util.DBUtil;
import util.QiniuUtil;
import util.TimeUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

    public static Result index(){
       return ok("HELLO world:  " + TimeUtil.getCurrentWeekStartMillis() + " " + TimeUtil.getTodayStartMillis());
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



    public static Result qiniuToken(){
        return ok(QiniuUtil.getUploadToken());
    }
}

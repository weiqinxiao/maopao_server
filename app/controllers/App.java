package controllers;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import constant.Constant;
import play.db.DB;
import play.mvc.Controller;
import play.mvc.Result;
import util.QiniuUtil;
import util.TimeUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Created by jiangecho on 15/5/2.
 */
public class App extends Controller{

    public static Result index(){
        Config config = ConfigFactory.parseFile(new File("conf/application.conf")).resolve();
        String version = config.getString("app.version");
        String name = config.getString("app.name");
       return ok("HELLO world: " + name + " " + version  + " "
               + " isDebugMode: " + (Constant.isDebugMode ? " true ":" false ")
               + TimeUtil.getCurrentWeekStartMillis() + " " + TimeUtil.getTodayStartMillis());
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

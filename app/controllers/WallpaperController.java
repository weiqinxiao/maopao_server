package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import model.PhotoItem;
import play.db.DB;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangecho on 15/11/5.
 */
public class WallpaperController extends Controller {
    public static Result getWallpagers(int type) {
        /**
         * code
         * data[photoItem...]
         */

        // attention now do not check type
        String sql = "SELECT * FROM t_wallpaper ORDER BY id DESC LIMIT 10";
        Connection connection;
        Statement statement = null;
        ResultSet resultSet = null;

        int id;
        String url, name, author, link, description;
        PhotoItem photoItem;

        ObjectNode objectNode = Json.newObject();
        connection = DB.getConnection();
        List<PhotoItem> photoItems;
        try {

            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            photoItems = new ArrayList<>();
            while (resultSet.next()) {
                photoItem = new PhotoItem();
                photoItem.group.name = resultSet.getString("name");
                photoItem.group.author = resultSet.getString("author");
                photoItem.group.link = resultSet.getString("link");
                photoItem.group.description = resultSet.getString("description");
                photoItem.group.id = resultSet.getInt("id");
                photoItem.url = resultSet.getString("url");

                photoItems.add(photoItem);
            }

            objectNode.put("code", 0);
            objectNode.put("data", Json.toJson(photoItems));
        } catch (SQLException e) {
            objectNode.put("code", -1);
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return ok(objectNode);
    }
}

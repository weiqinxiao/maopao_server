package model;

import util.TextContentUtil;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by cc191954 on 14-8-21.
 */
public class Maopao implements Serializable {


    public String activity_id = "";
    public ArrayList<BaseComment> comment_list = new ArrayList<BaseComment>();
    public int comments;
    public String content = "";
    public String created_at; // attention: the original type is long
    public String id = "";
    public ArrayList<UserObject> like_users = new ArrayList<UserObject>();
    public boolean liked;
    public int likes;
    // TODO modify the following line, now it is testing
    public UserObject owner = new UserObject();
    public String owner_id = "";
    public String path = "";
    public String device = "";

    public Maopao() {

    }

    public Maopao(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                id = resultSet.getString("id");
                owner_id = resultSet.getString("owner_id");
                String tmp = "<p>" + resultSet.getString("content") + "</p>";
                content = TextContentUtil.processMarkDownImageLink(tmp);
                created_at = resultSet.getString("create_at");
                device = resultSet.getString("device");
                comments = resultSet.getInt("comment_count");
                likes = resultSet.getInt("like_count");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}

package model;

import util.TextContentUtil;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by cc191954 on 14-8-21.
 */
public class Maopao implements Serializable {


    public String activity_id = "";
    public ArrayList<BaseComment> comment_list = new ArrayList<BaseComment>();
    public int comments;
    public String content = "";
    public long created_at; // attention: the original type is long
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
                if (resultSet.isBeforeFirst()){
                    resultSet.next();
                }
                id = resultSet.getString("id");
                owner_id = resultSet.getString("owner_id");
//                String tmp = "<p>" + resultSet.getString("content") + "</p>"; // TODO handle the content before insert into db
//                content = TextContentUtil.processMarkDownImageLink(tmp);
                content = resultSet.getString("content");
                created_at = resultSet.getTimestamp("create_at").getTime();
                device = resultSet.getString("device");
                comments = resultSet.getInt("comment_count");
                likes = resultSet.getInt("like_count");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}

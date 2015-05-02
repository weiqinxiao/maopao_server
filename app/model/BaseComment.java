package model;


import util.Util;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseComment implements Serializable{
    public String content = "";
    public String created_at; // 1408614375604, the original type is long
    public String id = ""; // 9291,
    public String owner_id = ""; // 8205,
    public String tweet_id = ""; // 4676

    public UserObject owner;


    public BaseComment() {
    }

    public BaseComment(ResultSet resultSet){
        if (resultSet != null){
            try {
                id = resultSet.getString("id");
                owner_id = resultSet.getString("owner_id");
                tweet_id = resultSet.getString("tweet_id");
                String tmp = "<p>" + resultSet.getString("content") + "</p>";
                content = Util.processMarkDownImageLink(tmp);
                created_at = resultSet.getString("create_at");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }

}
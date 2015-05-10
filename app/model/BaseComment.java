package model;


import util.TextContentUtil;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseComment implements Serializable{
    public String content = "";
    public long created_at; // 1408614375604
    public String id = ""; // 9291,
    public String owner_id = ""; // 8205,
    public String tweet_id = ""; // 4676

    public UserObject owner;


    public BaseComment() {
    }

    public BaseComment(String id, String owner_id, String tweet_id, String content, long created_at){
        this.id = id;
        this.owner_id = owner_id;
        this.tweet_id = tweet_id;
        this.content = "<p>" + content + "</p>";
        this.created_at = created_at;
    }

    public BaseComment(ResultSet resultSet){
        if (resultSet != null){
            try {
                id = resultSet.getString("id");
                owner_id = resultSet.getString("owner_id");
                tweet_id = resultSet.getString("tweet_id");
                String tmp = "<p>" + resultSet.getString("content") + "</p>";
                content = TextContentUtil.processMarkDownImageLink(tmp);
                created_at = resultSet.getDate("create_at").getTime();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }

}
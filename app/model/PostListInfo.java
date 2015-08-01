package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangecho on 15/8/1.
 */
public class PostListInfo {
    List<PostInfo> posts;

    public PostListInfo() {
        posts = new ArrayList<>();
    }

    public PostListInfo(ResultSet resultSet){
        this();
        PostInfo postInfo;
        try {
            while (resultSet != null && resultSet.next()){
                postInfo = new PostInfo();
                postInfo.setId(resultSet.getLong("id"));
                postInfo.setTitle(resultSet.getString("title"));
                postInfo.setDate(resultSet.getString("date"));
                postInfo.setUrl(resultSet.getString("url"));

                posts.add(postInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PostInfo> getPosts() {
        return posts;
    }

    public void setPosts(List<PostInfo> posts) {
        this.posts = posts;
    }

    public void addPost(PostInfo post) {
        if (post != null) {
            posts.add(post);
        }
    }
}

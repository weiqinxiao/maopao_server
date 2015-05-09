package model;

import java.util.List;

/**
 * Created by jiangecho on 15/5/10.
 */
public class CommentListResult {
    // TODO: when the fields is private, Json.toJson would not work
    public int code;
    public CommentList data;

    public CommentListResult(){
        code = 0;
        data = new CommentList();
    }

    public void setCode(int code){
        this.code = code;
    }

    public void setComments(List<BaseComment> comments){
        this.data.list = comments;
    }

    public static class CommentList {
        public List<BaseComment> list;

    }

}

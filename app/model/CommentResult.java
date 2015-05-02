package model;

import java.io.Serializable;

/**
 * Created by jiangecho on 15/5/2.
 */
public class CommentResult implements Serializable {
    public int code;
    public BaseComment data;

    public CommentResult(int code, BaseComment comment) {
        this.code = code;
        this.data = comment;
    }
}

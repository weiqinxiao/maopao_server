package model;

import java.io.Serializable;

/**
 * Created by jiangecho on 15/5/2.
 */
public class TweetResult implements Serializable{
    public int code;
    public Maopao data;

    public TweetResult(int code, Maopao maopao){
        this.code = code;
        this.data = maopao;
    }
}

package model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jiangecho on 15/5/2.
 */
public class MaopaoList implements Serializable{
    public int code;
    public List<Maopao> data;

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(List<Maopao> data) {
        this.data = data;
    }
    public List<Maopao> getMaopaoList(){
        return data;
    }
}

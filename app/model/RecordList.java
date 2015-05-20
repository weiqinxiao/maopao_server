package model;

import java.util.List;

/**
 * Created by jiangecho on 15/5/20.
 */
public class RecordList {
    public int code;
    List<Record> data;

    public RecordList(){
        this.code = -1;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Record> getRecordList() {
        return data;
    }

    public void setRecordList(List<Record> recordList) {
        this.data = recordList;
    }
}

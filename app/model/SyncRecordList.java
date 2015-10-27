package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangecho on 15/10/25.
 */
public class SyncRecordList {
    private int code;
    private String table;
    private List<SyncRecord> records;

    public SyncRecordList(String table){
        this.table = table;
    }

    public SyncRecordList() {
    }

    public int getCode() {
        return code;
    }

    public String getTable() {
        return table;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<SyncRecord> getRecords() {
        return records;
    }

    public void addRecord(SyncRecord record){
        if (records == null){
            records = new ArrayList<>();
        }
        records.add(record);
    }
}

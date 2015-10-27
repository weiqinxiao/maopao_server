package model;

/**
 * Created by jiangecho on 15/10/25.
 */
public class SyncRecord {
    private int id;
    private String date;
    private long duration;

    public SyncRecord(int id, String date, long duration) {
        this.id = id;
        this.date = date;
        this.duration = duration;
    }

    public SyncRecord() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }
}

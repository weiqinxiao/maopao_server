package model;

/**
 * Created by jiangecho on 15/10/25.
 */
public class SyncRecord {
    private String date;
    private long duration;

    public SyncRecord(String date, long duration) {
        this.date = date;
        this.duration = duration;
    }

    public SyncRecord() {
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

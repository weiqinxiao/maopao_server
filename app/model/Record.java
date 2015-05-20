package model;

/**
 * Created by jiangecho on 15/5/20.
 */
public class Record {
    public int code;
    public long startMillis;
    public long endMillis;
    public UserObject owner;

    public Record(){
        this.code = -1;
    }

    public Record(UserObject owner, long startMillis, long endMillis){
        this.code = 0;
        this.owner = owner;
        this.startMillis = startMillis;
        this.endMillis = endMillis;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getEndMillis() {
        return endMillis;
    }

    public void setEndMillis(long endMillis) {
        this.endMillis = endMillis;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public void setStartMillis(long startMillis) {
        this.startMillis = startMillis;
    }

    public void setOwner(UserObject owner){
        this.owner = owner;
    }

    public UserObject getOwner(){
        return this.owner;
    }
}

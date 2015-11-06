package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangecho on 15/11/1.
 */
public class FollowList extends Base{
    public List<UserObject> list;

    public FollowList() {
        this.code = -1;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setList(List<UserObject> list) {
        this.list = list;
    }

    public void addFollow(UserObject userObject) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(userObject);
    }
}

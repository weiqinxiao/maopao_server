package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangecho on 15/11/1.
 */
public class FollowList extends Base{
    public List<UserObject> follows;

    public FollowList() {
        this.code = -1;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setFollows(List<UserObject> follows) {
        this.follows = follows;
    }

    public void addFollow(UserObject userObject) {
        if (follows == null) {
            follows = new ArrayList<>();
        }
        follows.add(userObject);
    }
}

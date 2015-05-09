package model;


import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by cc191954 on 14-8-7.
 */
public class UserObject implements Serializable{
    public String avatar = "";
    public String slogan = "";
    public String tags = "";
    public String tags_str = "";
    public String company = "";
    public String global_key = "";
    public String id = "";
    public String introduction = "";
    public String job_str = "";
    public String lavatar = "";
    public String location = "";
    public String name = "";
    public String path = "";
    public String phone = "";
    public String birthday;
    public long created_at;
    public int fans_count;
    public boolean follow;  // 别人是否关注我
    public boolean followed;
    public int follows_count;
    public int job;
    public int sex;
    public int status;
    public long last_activity_at;
    public long last_logined_at;
    public long updated_at;
    public int tweets_count;
    public String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserObject that = (UserObject) o;

        if (created_at != that.created_at) return false;
        if (fans_count != that.fans_count) return false;
        if (follow != that.follow) return false;
        if (followed != that.followed) return false;
        if (follows_count != that.follows_count) return false;
        if (job != that.job) return false;
        if (last_activity_at != that.last_activity_at) return false;
        if (last_logined_at != that.last_logined_at) return false;
        if (sex != that.sex) return false;
        if (status != that.status) return false;
        if (tweets_count != that.tweets_count) return false;
        if (updated_at != that.updated_at) return false;
        if (avatar != null ? !avatar.equals(that.avatar) : that.avatar != null) return false;
        if (birthday != null ? !birthday.equals(that.birthday) : that.birthday != null)
            return false;
        if (company != null ? !company.equals(that.company) : that.company != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (global_key != null ? !global_key.equals(that.global_key) : that.global_key != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (introduction != null ? !introduction.equals(that.introduction) : that.introduction != null)
            return false;
        if (job_str != null ? !job_str.equals(that.job_str) : that.job_str != null) return false;
        if (lavatar != null ? !lavatar.equals(that.lavatar) : that.lavatar != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        if (slogan != null ? !slogan.equals(that.slogan) : that.slogan != null) return false;
        if (tags != null ? !tags.equals(that.tags) : that.tags != null) return false;
        if (tags_str != null ? !tags_str.equals(that.tags_str) : that.tags_str != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = avatar != null ? avatar.hashCode() : 0;
        result = 31 * result + (slogan != null ? slogan.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + (tags_str != null ? tags_str.hashCode() : 0);
        result = 31 * result + (company != null ? company.hashCode() : 0);
        result = 31 * result + (global_key != null ? global_key.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (introduction != null ? introduction.hashCode() : 0);
        result = 31 * result + (job_str != null ? job_str.hashCode() : 0);
        result = 31 * result + (lavatar != null ? lavatar.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (int) (created_at ^ (created_at >>> 32));
        result = 31 * result + fans_count;
        result = 31 * result + (follow ? 1 : 0);
        result = 31 * result + (followed ? 1 : 0);
        result = 31 * result + follows_count;
        result = 31 * result + job;
        result = 31 * result + sex;
        result = 31 * result + status;
        result = 31 * result + (int) (last_activity_at ^ (last_activity_at >>> 32));
        result = 31 * result + (int) (last_logined_at ^ (last_logined_at >>> 32));
        result = 31 * result + (int) (updated_at ^ (updated_at >>> 32));
        result = 31 * result + tweets_count;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }


    public UserObject() {
        avatar = "http://tp1.sinaimg.cn/1228986464/50/5697730439/0";
        lavatar = avatar;
        created_at = Long.parseLong("1405671313000");
        last_logined_at = created_at;
        last_activity_at = created_at;
        global_key = "7828";
        name ="testUser";
        updated_at = created_at;
        id = "7828";
    }

    public UserObject(long id, String name, String headImgUrl){
        this.avatar = headImgUrl;
        this.id = id + "";
        this.name = name;
        this.created_at = System.currentTimeMillis();
    }

    public UserObject(ResultSet resultSet){
        if (resultSet != null){
            try {
                // TODO
                resultSet.getString("jo");

            }catch (SQLException e){

            }
        }

    }

}

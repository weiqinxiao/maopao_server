package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import constant.Constant;
import model.SyncRecord;
import model.SyncRecordList;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.DBUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangecho on 15/10/26.
 */
public class Sync extends Controller {
    public static Result upload() {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = request().body().asJson();
        SyncRecordList syncRecordList = null;
        try {
            syncRecordList = objectMapper.treeToValue(jsonNode, SyncRecordList.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        SyncRecordList result = new SyncRecordList();

        if (syncRecordList == null || syncRecordList.getRecords() == null || syncRecordList.getRecords().size() == 0) {
            result.setCode(0);
            return ok(Json.toJson(result));
        }


        String uid = session("id");
        if (uid == null || uid.length() == 0) {
            result.setCode(Constant.UN_LOGIN);
            return ok(Json.toJson(result));
        }

        // t_train and t_challenge 用户存储用户每天的数据，不是每次的数据
        final String sql = "INSERT INTO t_train(owner_id, start, end) VALUES('%s', %s, %s)";
        String tmp;
        List<String> sqls = new ArrayList<>();
        for (SyncRecord record : syncRecordList.getRecords()) {
            tmp = String.format(sql, uid, record.getDate(), record.getDuration());
            sqls.add(tmp);
        }
        int count = DBUtil.bulkInsert(sqls);
        if (count > 0) {
            result.setCode(0);
        } else {
            result.setCode(-1);
        }
        return ok(Json.toJson(result));
    }
}

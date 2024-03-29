/**
 * Created by jiangecho on 15/11/3.
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import scala.concurrent.duration.Duration;
import util.DBUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Global extends GlobalSettings {

    @Override
    public void onStart(Application application) {

        Akka.system().scheduler().scheduleOnce(
                Duration.create(0, TimeUnit.MILLISECONDS),
                () -> {
                    Logger.info("ON START ---    " + System.currentTimeMillis());
                },
                Akka.system().dispatcher()
        );

        Akka.system().scheduler().schedule(
                Duration.create(nextExecutionInSeconds(10, 0), TimeUnit.SECONDS),
                Duration.create(24, TimeUnit.HOURS),
                () -> {
                    Logger.info("EVERY DAY AT 10:00 ---    " + System.currentTimeMillis());
                    syncPosts();
                    Logger.info("sync post end");
                },
                Akka.system().dispatcher()
        );
    }

    public static int nextExecutionInSeconds(int hour, int minute) {
        return Seconds.secondsBetween(
                new DateTime(),
                nextExecution(hour, minute)
        ).getSeconds();
    }

    public static DateTime nextExecution(int hour, int minute) {
        DateTime next = new DateTime()
                .withHourOfDay(hour)
                .withMinuteOfHour(minute)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);

        return (next.isBeforeNow())
                ? next.plusHours(24)
                : next;
    }

    public static void syncPosts() {

        int t = 60 * 1000;
        String url = "http://diaoba.wang/?json=get_recent_posts&count=20";
        F.Promise<JsonNode> jsonPromise = WS.url(url).setTimeout(t).get().map(
                new F.Function<WSResponse, JsonNode>() {
                    public JsonNode apply(WSResponse response) {
                        JsonNode json = response.asJson();
                        insertWXPostInsertDb(json);

                        return json;
                    }
                }
        );
    }

    private static void insertWXPostInsertDb(JsonNode jsonNode) {
        if (jsonNode == null) {
            return;
        }
        List<JsonNode> jsonNodes = jsonNode.findValues("posts");
        if (jsonNodes == null) {
            return;
        }

        ArrayNode arrayNode = (ArrayNode) jsonNodes.get(0);
        if (arrayNode == null || arrayNode.size() == 0) {
            return;
        }

        String title;
        String content;
        String url;
        int post_id;
        int category_id;

        final String sql = "INSERT INTO t_wx_post(post_id, title, content, url, image_url, category_id) " +
                "VALUES(%d, '%s', '%s', '%s', '%s', %d) ON DUPLICATE KEY UPDATE id= id";
        String tmpSql;
        List<String> sqls = new ArrayList<>();

        String imageUrl = "";
        final String regex = "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;

        ArrayNode categoryNode;
        for (JsonNode node : arrayNode) {
            imageUrl = "";
            post_id = node.path("id").asInt();
            content = node.path("content").asText();
            title = node.path("title").asText();
            url = node.path("url").asText();
            categoryNode = (ArrayNode) node.path("categories");
            if (categoryNode != null && categoryNode.size() > 0) {
                category_id = categoryNode.get(0).path("id").asInt();
            } else {
                category_id = 1; // default, uncategorized
            }

            matcher = pattern.matcher(content);
//            boolean found = matcher.find();
//            int count = matcher.groupCount();
            if (matcher.find()) {
                imageUrl = matcher.group(1);
            }
            tmpSql = String.format(sql, post_id, title, content, url, imageUrl, category_id);
            sqls.add(tmpSql);
        }

        if (sqls.size() > 0) {
            DBUtil.bulkInsert(sqls);
        }
    }
}

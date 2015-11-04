/**
 * Created by jiangecho on 15/11/3.
 */

import com.fasterxml.jackson.databind.JsonNode;
import scala.concurrent.duration.Duration;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import play.libs.ws.*;
import play.libs.F.Function;
import play.libs.F.Promise;

import java.util.concurrent.TimeUnit;

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
                    Logger.info("EVERY DAY AT 8:00 ---    " + System.currentTimeMillis());
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

    public static void syncPosts(){

        long t = 1000;
        String url = "http://diaoba.wang/?json=get_recent_posts";
        Promise<JsonNode> jsonPromise = WS.url(url).get().map(
                new Function<WSResponse, JsonNode>() {
                    public JsonNode apply(WSResponse response) {
                        JsonNode json = response.asJson();
                        return json;
                    }
                }
        );

        JsonNode jsonNode = jsonPromise.get(t);
        jsonNode.get(0);

    }
}

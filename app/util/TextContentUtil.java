package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jiangecho on 15/5/2.
 */
public class TextContentUtil {
    public static String processMarkDownImageLink(String string){
        String markdownImagePattern = "!\\[[^\\]]*\\]\\(([^\\)]*)\\)";
        String imageAhref = "<a href=\"$1\" target=\"_blank\" class=\"bubble-markdown-image-link\" " +
                "rel=\"nofollow\"><img src=\"$1\" alt=\"图片\" class=\" bubble-markdown-image\"></a>";
        Pattern pattern  = Pattern.compile(markdownImagePattern);
        Matcher matcher =  pattern.matcher(string);
        String result = matcher.replaceAll(imageAhref);

        return result;
    }

    public static String processEmoji(String string){
        //String emojiPattern = "%3A([^\\\\])%3A";
        String emojiPattern = ":([a-zA-Z0-9_][a-zA-Z0-9_]*):";
        String emojiHref = "<img class=\"emotion emoji\" src=\"https://coding.net/static/emojis/$1.png\" title=\"$1\">";

        string = string.replaceAll(emojiPattern, emojiHref);
        return string;
    }

    public static String processAt(String string){
        //String atPattern = "%40(.*)\\+";
        String atPattern = "@(.*)\\+";
        // TODO maybe the link should be something like: https://coding.net/u....
        // <a class=\"at-someone\" href=\"https://coding.net/u/dandelion\" rel=\"nofollow\">@dandelion</a>

        String atHref = "<a class=\"at-someone\" href=\"/u/@$1\" rel=\"nofollow\">@$1</a> ";
        string = string.replaceAll(atPattern, atHref);
        return string;
    }

    public static String processTweetContent(String tweetContent){
        if (tweetContent == null){
            return null;
        }
        tweetContent = processMarkDownImageLink(tweetContent);
        tweetContent = processAt(tweetContent);
        tweetContent = processEmoji(tweetContent);

        return tweetContent;
    }

    // TODO I think we should process the content before insert into the db
    public static String processComment(String comment){
        comment = processEmoji(comment);
        comment = processAt(comment);
        return comment;
    }
}

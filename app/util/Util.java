package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jiangecho on 15/5/2.
 */
public class Util {
    public static String processMarkDownImageLink(String string){
        String markdownImagePattern = "!\\[[^\\]]*\\]\\(([^\\)]*)\\)";
        String imageAhref = "<a href=\"$1\" target=\"_blank\" class=\"bubble-markdown-image-link\" " +
                "rel=\"nofollow\"><img src=\"$1\" alt=\"图片\" class=\" bubble-markdown-image\"></a>";
        Pattern pattern  = Pattern.compile(markdownImagePattern);
        Matcher matcher =  pattern.matcher(string);
        String result = matcher.replaceAll(imageAhref);

        return result;
    }
}

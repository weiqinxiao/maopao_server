package util;

import scala.collection.JavaConverters;

import java.util.List;


/**
 * Created by jiangecho on 15/11/4.
 */
public class Util {

    public static <T> scala.collection.immutable.List<T> scalaList(List<T> javaList) {
        return JavaConverters.asScalaBufferConverter(javaList).asScala().toList();
    }
}

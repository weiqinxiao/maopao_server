package model;

/**
 * Created by jiangecho on 15/11/5.
 */
public class PhotoItem {
    public Group group = new Group();
    public String url = "";

    public PhotoItem() {
    }

    public class Group {
        public String name = "";
        public String author = "";
        public String link = "";
        public String description = "";
        public int id;

        public Group() {
        }
    }

}


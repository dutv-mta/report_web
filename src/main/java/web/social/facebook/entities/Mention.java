package web.social.facebook.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.social.facebook.utils.ElasticUtils;

import java.util.List;
import java.util.Objects;

public class Mention {
    private static final Logger logger = LoggerFactory.getLogger(Mention.class);

    private String id;
    private int topicId;
    private String url;
    private String title;
    private String content;
    private String userPub;
    private String pubTime;
    private boolean show;

    public Mention() {
    }

    public Mention(String id, int topicId, String url, String title, String content, String userPub, String pubTime, boolean show) {
        this.id = id;
        this.topicId = topicId;
        this.url = url;
        this.title = title;
        this.content = content;
        this.userPub = userPub;
        this.pubTime = pubTime;
        this.show = show;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUserPub(String userPub) {
        this.userPub = userPub;
    }

    public void setPubTime(String pubTime) {
        this.pubTime = pubTime;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getId() {
        return id;
    }

    public int getTopicId() {
        return topicId;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getUserPub() {
        return userPub;
    }

    public String getPubTime() {
        return pubTime;
    }

    public boolean isShow() {
        return show;
    }

    public static JSONObject toJson(List<Mention> mention) throws JSONException {
        Objects.requireNonNull(mention);

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        mention.forEach(item->{
            try {
                JSONObject jsonSub = new JSONObject();

                jsonSub.put("url", item.getUrl());
                jsonSub.put("title", item.getTitle());
                jsonSub.put("content", item.getContent());
                jsonSub.put("date", item.getPubTime());
                jsonArray.put(jsonSub);
            } catch (JSONException e) {
                logger.error("Exception", e);
            }
        });
        jsonObject.put("info", jsonArray);
        return jsonObject;
    }
}





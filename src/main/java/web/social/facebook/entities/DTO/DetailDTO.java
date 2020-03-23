package web.social.facebook.entities.DTO;

import web.social.facebook.entities.Mention;

import java.util.ArrayList;
import java.util.List;

public class DetailDTO {
    private String id;
    private String url;
    private String title;
    private String content;
    private String date;

    public DetailDTO(String id, String url, String title, String content, String date) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public DetailDTO(String content, String date, String title, String id) {
        this.content = content;
        this.date = date;
        this.title = title;
        this.id = id;
    }

    public static List<DetailDTO> getAllPost(List<Mention> mentionList) {
        List<DetailDTO> detailDTOList = new ArrayList<>();
        mentionList.forEach(item->{
            String date = item.getPubTime();
            String content = item.getContent();
            String title = item.getTitle();
            String id = item.getId();
            DetailDTO detailDTO = new DetailDTO(content, date, title, id);
            detailDTOList.add(detailDTO);
        });
        return detailDTOList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "DetailDTO{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}

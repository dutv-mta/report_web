package web.social.facebook.entities;

public class Post {
    private String url;
    private int likeTotal;
    private int commentTotal;
    private int shareTotal;
    private String title;
    private String content;
    private String date;
    private double score;

    public Post(String url, int likeTotal, int commentTotal, int shareTotal, String title,String content, String date, float score) {
        this.url = url;
        this.likeTotal = likeTotal;
        this.commentTotal = commentTotal;
        this.shareTotal = shareTotal;
        this.title = title;
        this.content = content;
        this.date = date;
        this.score = score;
    }

    public Post() {

    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLikeTotal(int likeTotal) {
        this.likeTotal = likeTotal;
    }

    public void setCommentTotal(int commentTotal) {
        this.commentTotal = commentTotal;
    }

    public void setShareTotal(int shareTotal) {
        this.shareTotal = shareTotal;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public int getLikeTotal() {
        return likeTotal;
    }

    public int getCommentTotal() {
        return commentTotal;
    }

    public int getShareTotal() {
        return shareTotal;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getScore() {
        return score;
    }
}

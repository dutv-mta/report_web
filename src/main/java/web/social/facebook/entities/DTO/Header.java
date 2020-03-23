package web.social.facebook.entities.DTO;

public class Header {
    private int deCap;
    private int postCount;
    private int commentCount;

    public Header(int deCap, int postCount, int commentCount) {
        this.deCap = deCap;
        this.postCount = postCount;
        this.commentCount = commentCount;
    }

    public void setDeCap(int deCap) {
        this.deCap = deCap;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getDeCap() {
        return deCap;
    }

    public int getPostCount() {
        return postCount;
    }

    public int getCommentCount() {
        return commentCount;
    }
}

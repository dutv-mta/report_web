package web.social.facebook.entities.DTO;

public class Reaction {
    private int like;
    private int comment;
    private int share;

    public Reaction() {

    }

    public Reaction(int like, int comment, int share) {
        this.like = like;
        this.comment = comment;
        this.share = share;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public void setShare(int share) {
        this.share = share;
    }

    public int getLike() {
        return like;
    }

    public int getComment() {
        return comment;
    }

    public int getShare() {
        return share;
    }
}

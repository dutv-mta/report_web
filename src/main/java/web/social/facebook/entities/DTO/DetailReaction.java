package web.social.facebook.entities.DTO;

public class DetailReaction {
    private String date;
    private Reaction reaction;


    public DetailReaction(String date, Reaction reaction) {
        this.date = date;
        this.reaction = reaction;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setReaction(Reaction reaction) {
        this.reaction = reaction;
    }

    public String getDate() {
        return date;
    }

    public Reaction getReaction() {
        return reaction;
    }
}

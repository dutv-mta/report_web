package web.social.facebook.entities.DTO;

public class ChartInfoDTO {
    private String date;
    private int number;

    public ChartInfoDTO(String date, int number) {
        this.date = date;
        this.number = number;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public int getNumber() {
        return number;
    }


}

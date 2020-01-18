package martian.mystery.data;

public class ResponseFromServer { // ответ от сервера заворачивается в этот класс-обертку

    private String winner; // имя победителя
    private String prize; // приз
    private String season; // сезон
    private String email; // контакт для связи
    private int result; // результат запроса
    private int existwinner; // наличие победителя
    private int updateapp; // обновление приложения
    private int updateforce; // принудительное обновление приложения
    private int place; // место в игре

    public int getUpdate() {
        return updateapp;
    }

    public void setUpdate(int update) {
        this.updateapp = update;
    }

    public int getForceUpdate() {
        return updateforce;
    }

    public void setForceUpdate(int forceUpdate) {
        this.updateforce = forceUpdate;
    }

    public int getExistWinner() {
        return existwinner;
    }

    public void setExistWinner(int existWinner) {
        this.existwinner = existWinner;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }
}
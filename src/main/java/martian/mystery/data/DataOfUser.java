package martian.mystery.data;

/*Класс-обертка данных о пользователе.
  Этот класс преобразовывается в json с помощью Retrofit и отправляется на сервер
  Может содержать неактуальные данные! Актуальные данные содержаться в классе Progress*/

public class DataOfUser {

    private String nameOfUser = "none"; // по умлочанию none
    private int level = 1;
    private int timeOfLevel; // время прохождения последнего уровня

    public String getNameOfUser() {
        return nameOfUser;
    }

    public void setNameOfUser(String nameOfUser) {
        this.nameOfUser = nameOfUser;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getTimeOfLevel() {
        return timeOfLevel;
    }

    public void setTimeOfLevel(int timeOfLevel) {
        this.timeOfLevel = timeOfLevel;
    }
}
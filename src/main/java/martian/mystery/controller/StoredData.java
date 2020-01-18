package martian.mystery.controller;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

public class StoredData { // класс, реализующий доступ к сохраненным на устройстве данным

    public static final String APP_PREFERENCES = "app_data2";
    public static final String DATA_LEVEL = "game_level3";
    public static final String DATA_WINS = "wins3";
    public static final String DATA_PRIZE = "prize3";
    public static final String DATA_SEASON = "season3";
    public static final String DATA_COUNT_ATTEMPTS = "count_attempts3";
    public static final String DATA_COUNT_LAUNCH_APP = "count_launch_app2"; // счетчик запусков лучше не менять
    public static final String DATA_WINNER_IS_SENDED = "winner_is_sended3";
    public static final String DATA_LAST_ANSWER = "last_answer3";
    public static final String DATA_WINNER_IS_CHECKED = "winner_is_checked3";
    public static final String DATA_IS_WINNER = "is_winner3";
    public static final String DATA_PLACE = "place_gamer3";
    public static final String DATA_LASTDATE = "last_date3";

    public StoredData() { }

    // сохранение данных на устройстве
    public static void saveData(String typeData, int data) {
        SharedPreferences sharedPreferences = GetContextClass.getContext().getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(typeData,data);
        editor.apply();
    }
    public static void saveData(String typeData, String data) {
        SharedPreferences sharedPreferences = GetContextClass.getContext().getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(typeData,data);
        editor.apply();
    }
    public static void saveData(String typeData, boolean data) {
        SharedPreferences sharedPreferences = GetContextClass.getContext().getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(typeData,data);
        editor.commit();
    }

    // получение данных с устройства
    public static int getDataInt(String typeData, int defValue) {
        SharedPreferences sharedPreferences = GetContextClass.getContext().getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(typeData,defValue);
    }
    public static String getDataString(String typeData, String defValue) {
        SharedPreferences sharedPreferences = GetContextClass.getContext().getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE);
        return sharedPreferences.getString(typeData,defValue);
    }
    public static boolean getDataBool(String typeData) {
        SharedPreferences sharedPreferences = GetContextClass.getContext().getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(typeData,false);
    }

    // стирание данных для нового сезона
    /*public static boolean deleteDataSeason() {
        // стираем уровень
        SharedPreferences sharedPreferences = GetContextClass.getContext().getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(DATA_LEVEL,1);

        // стираем попытки
        editor.putInt(DATA_COUNT_ATTEMPTS,3);

        // стираем победителя
        editor.putString(DATA_WINS,GetContextClass.getContext().getResources().getString(R.string.no_winner_text));
        editor.putBoolean(DATA_IS_WINNER,false);
        editor.putBoolean(DATA_WINNER_IS_CHECKED,false);
        editor.putBoolean(DATA_WINNER_IS_SENDED,false);

        return editor.commit();
    }*/
    private static void clearSharedPreferences(Context ctx){
        File dir = new File(ctx.getFilesDir().getParent() + "/shared_prefs/");
        String[] children = dir.list();
        for (int i = 0; i < children.length; i++) {
            // clear each of the prefrances
            ctx.getSharedPreferences(children[i].replace(".xml", ""), Context.MODE_PRIVATE).edit().clear().commit();
        }
        // Make sure it has enough time to save all the commited changes
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        for (int i = 0; i < children.length; i++) {
            // delete the files
            new File(dir, children[i]).delete();
        }
    }
}
package martian.mystery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.florent37.viewtooltip.ViewTooltip;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yanzhikai.textpath.AsyncTextPathView;


public class MainActivity extends AppCompatActivity {

    private Button btnNext;
    private TextView tvWinner;
    private AsyncTextPathView tvPrize;
    private TextView tvSeason;
    private TextView tvProgressPick1;
    private TextView tvProgressPick2;
    private TextView tvProgressPick3;
    private TextView tvProgressPick4;
    private ImageView imgLvlTop1;
    private ImageView imgLvlTop2;
    private ImageView imgLvlTop3;
    private ImageView imgLvlTop4;
    private ImageView imgLvl2;
    private ImageView imgLvl3;
    private ImageView imgLvl4;
    private ImageView imgFirst;
    private ImageView imgLast;
    private ImageView btnHelp;
    private ImageView btnReview;
    private ConstraintLayout clProgressPick1;
    private ConstraintLayout clProgressPick2;
    private ConstraintLayout clProgressPick3;
    private ConstraintLayout clProgressPick4;
    ConstraintLayout.LayoutParams imgTop1Params;
    ConstraintLayout.LayoutParams imgTop2Params;
    ConstraintLayout.LayoutParams imgTop3Params;
    ConstraintLayout.LayoutParams imgTop4Params;


    private CheckWinsThread checkWinsThread;
    private ProgressViewController progressViewController;
    private Handler handler;
    private ObjectAnimator animBtnHelp;
    AppUpdateManager appUpdateManager;
    AssistentDialog assistentDialogRules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        StoredData.incrementCountLaunches();
        appUpdateManager = AppUpdateManagerFactory.create(GetContextClass.getContext());

        btnNext = findViewById(R.id.btnNext);
        btnHelp = findViewById(R.id.btnHelp);
        btnReview = findViewById(R.id.btnReview);
        imgLast = findViewById(R.id.imgLastCircleLvl);
        imgFirst = findViewById(R.id.imgFirstCircleLvl);

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
            }
        };

        // все что между первым и последним уровнем
        imgLvl2 = findViewById(R.id.imgLvl2);
        imgLvl3 = findViewById(R.id.imgLvl3);
        imgLvl4 = findViewById(R.id.imgLvl4);
        imgLvlTop1 = findViewById(R.id.imgLvlTop1);
        imgLvlTop2 = findViewById(R.id.imgLvlTop2);
        imgLvlTop3 = findViewById(R.id.imgLvlTop3);
        imgLvlTop4 = findViewById(R.id.imgLvlTop4);
        tvProgressPick1 = findViewById(R.id.tvProgressPick1);
        tvProgressPick2 = findViewById(R.id.tvProgressPick2);
        tvProgressPick3 = findViewById(R.id.tvProgressPick3);
        tvProgressPick4 = findViewById(R.id.tvProgressPick4);
        clProgressPick1 = findViewById(R.id.clProgressPick1);
        clProgressPick2 = findViewById(R.id.clProgressPick2);
        clProgressPick3 = findViewById(R.id.clProgressPick3);
        clProgressPick4 = findViewById(R.id.clProgressPick4);

        imgTop1Params = (ConstraintLayout.LayoutParams) imgLvlTop1.getLayoutParams();
        imgTop2Params = (ConstraintLayout.LayoutParams) imgLvlTop2.getLayoutParams();
        imgTop3Params = (ConstraintLayout.LayoutParams) imgLvlTop3.getLayoutParams();
        imgTop4Params = (ConstraintLayout.LayoutParams) imgLvlTop4.getLayoutParams();
        progressViewController = new ProgressViewController();

        tvWinner = findViewById(R.id.firstPerson);
        tvWinner.setText(
                String.valueOf(
                StoredData.getDataString(StoredData.DATA_WINS,
                        getResources().getString(R.string.no_winner_text))));
        tvWinnerAnimation();
        tvPrize = findViewById(R.id.tvPrize);
        tvPrize.setText(
                String.valueOf(
                StoredData.getDataString(StoredData.DATA_PRIZE,
                        getResources().getString(R.string.prize))));
        tvPrize.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvSeason = findViewById(R.id.tvSeason);
        tvSeason.setText(
                String.valueOf(
                StoredData.getDataString(StoredData.DATA_SEASON,
                        getResources().getString(R.string.season))));

        btnNext.setOnClickListener(onClickListener);
        btnHelp.setOnClickListener(onClickListener);
        btnReview.setOnClickListener(onClickListener);
        if(StoredData.getCountLaunch() == 1) {
            ViewTooltip
                    .on(this, btnHelp)
                    .autoHide(true, 5000)
                    .corner(30)
                    .position(ViewTooltip.Position.BOTTOM)
                    .withShadow(false)
                    .text(getResources().getString(R.string.read_rules))
                    .show();
            animBtnHelp = ObjectAnimator.ofFloat(btnHelp, "rotationY", 0.0f, 360f);
            animBtnHelp.setDuration(2400);
            animBtnHelp.setRepeatCount(ObjectAnimator.INFINITE);
            animBtnHelp.setInterpolator(new AccelerateDecelerateInterpolator());
            animBtnHelp.start();
        }
        assistentDialogRules = new AssistentDialog(AssistentDialog.DIALOG_RULES);
    }

    @Override
    protected void onResume() {
        super.onResume();

        progressViewController.increaseProgressAnimation(0);
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        new OnSuccessListener<AppUpdateInfo>() {
                            @Override
                            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ResponseFromServer response = null;
                                                try {
                                                    response = RequestController.getInstance()
                                                            .getJsonApi()
                                                            .checkUpdate(BuildConfig.VERSION_CODE)
                                                            .execute().body();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                if(response.getUpdate() == 1) {
                                                    AssistentDialog updateDialog = new AssistentDialog(AssistentDialog.DIALOG_UPDATE_APP);
                                                    updateDialog.show(getSupportFragmentManager(),"UPDATE");
                                                }
                                            }
                                        }).start();
                                } else if(appUpdateInfo.updateAvailability()
                                        == UpdateAvailability.UPDATE_NOT_AVAILABLE) {
                                }
                            }
                        });

        if(Progress.getInstance().getLevel() == 1) btnNext.setText(this.getResources().getString(R.string.start_game));
        else if(Progress.getInstance().getLevel() < 22) btnNext.setText(this.getResources().getString(R.string.continue_game));


        checkWinsThread = new CheckWinsThread();
        checkWinsThread.start();
        CheckForceUpdateTask checkForceUpdateTask = new CheckForceUpdateTask();
        checkForceUpdateTask.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkWinsThread.toStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkWinsThread.toStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        progressViewController.increaseProgressAnimation(0);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void tvWinnerAnimation() {
        Animation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(1200); //You can manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(3);
        tvWinner.startAnimation(anim);
    }
    public static float dpToPx(float dp){
        return dp * ((float) GetContextClass.getContext().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    private Spanned textToSpannedWithUnderline(String text) {
        return android.text.Html.fromHtml("<u>" + text + "</u>");
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnNext: {
                    Intent questionIntent = new Intent(MainActivity.this,QuestionActivity.class);
                    startActivityForResult(questionIntent, 1);
                    break;
                }
                case R.id.btnHelp: {
                    assistentDialogRules.show(getSupportFragmentManager(),"HELP");
                    // анимация кнопки
                    AnimationController animationController = new AnimationController();
                    animationController.clickRules();
                    break;
                }
                case R.id.btnReview: {
                    AssistentDialog reviewDialog = new AssistentDialog(AssistentDialog.DIALOG_REVIEW);
                    reviewDialog.show(getSupportFragmentManager(),"DIALOG_REVIEW");
                }
            }
        }
    };

    // внутренние контроллеры и модели -----------------------------------------------------------------------------
    private class AnimationController {

        public void clickRules() {
            ObjectAnimator animBtnHelpClickX = ObjectAnimator.ofFloat(btnHelp, "scaleX", 1.0f,0.8f,1.0f);
            ObjectAnimator animBtnHelpClickY = ObjectAnimator.ofFloat(btnHelp, "scaleY", 1.0f,0.8f,1.0f);
            animBtnHelpClickX.setRepeatCount(0);
            animBtnHelpClickY.setRepeatCount(0);
            animBtnHelpClickX.setDuration(200);
            animBtnHelpClickY.setDuration(200);
            animBtnHelpClickX.start();
            animBtnHelpClickY.start();
            if(animBtnHelp != null) {
                animBtnHelp.cancel();
                animBtnHelp = ObjectAnimator.ofFloat(btnHelp, "rotationY", 0.0f);
                animBtnHelp.setRepeatCount(0);
                animBtnHelp.setDuration(500);
                animBtnHelp.start();
            }

        }
    }
    private class ProgressViewController { // класс для управления состоянием (вида) прогресса

        private float widthBetweenLvl;
        private float widthOneBlockLvl;
        private float ONE_LVL_WIDTH_LEFTRIGHT;
        private float ONE_LVL_WIDTH_CENTER;
        public ProgressViewController() {
            int widthScreen = getWidthSreeen();
            widthBetweenLvl = widthScreen - widthScreen*0.2f - widthScreen*0.2f;
            widthOneBlockLvl = (widthBetweenLvl - (imgLvl2.getLayoutParams().width*3))/4 + 1;
            ONE_LVL_WIDTH_LEFTRIGHT = widthOneBlockLvl/5;
            ONE_LVL_WIDTH_CENTER = widthOneBlockLvl/4;
        }
        public int getWidthSreeen() {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            return size.x;
        }

        public void increaseProgressAnimation(int differenceLvl) {
            int currentLevel = Progress.getInstance().getLevel() - 1;
            if(currentLevel == 0) {
                imgFirst.setAlpha(0.5f);
                imgLvlTop1.setVisibility(View.INVISIBLE);
                clProgressPick1.setVisibility(View.INVISIBLE);
            }
            if(currentLevel == 1) {
                imgFirst.setAlpha(1.0f);
                imgLvlTop1.setVisibility(View.INVISIBLE);
                clProgressPick1.setVisibility(View.INVISIBLE);
            } else if(currentLevel > 1 && currentLevel < 7) {
                imgFirst.setAlpha(1.0f);
                imgLvlTop1.setVisibility(View.VISIBLE);
                clProgressPick1.setVisibility(View.VISIBLE);
                tvProgressPick1.setText(String.valueOf(currentLevel));

                if((currentLevel - 2) == 4) imgTop1Params.width = (int)ONE_LVL_WIDTH_LEFTRIGHT*(currentLevel-2)+(int)ONE_LVL_WIDTH_LEFTRIGHT/2;
                else imgTop1Params.width = (int)ONE_LVL_WIDTH_LEFTRIGHT*(currentLevel-1);

                imgLvlTop1.setLayoutParams(imgTop1Params);
            } else if(currentLevel == 7) {
                imgFirst.setAlpha(1.0f);
                imgLvl2.setAlpha(1.0f);
                imgLvlTop1.setVisibility(View.VISIBLE);
                imgTop1Params.width = (int)ONE_LVL_WIDTH_LEFTRIGHT*5;
                clProgressPick1.setVisibility(View.INVISIBLE);

                imgLvlTop1.setLayoutParams(imgTop1Params);
            } else if(currentLevel > 7 && currentLevel < 11) {
                imgFirst.setAlpha(1.0f);
                imgLvlTop1.setVisibility(View.VISIBLE);
                imgLvlTop2.setVisibility(View.VISIBLE);
                clProgressPick1.setVisibility(View.INVISIBLE);
                imgLvl2.setAlpha(1.0f);
                imgTop1Params.width = (int)ONE_LVL_WIDTH_LEFTRIGHT*5;

                clProgressPick2.setVisibility(View.VISIBLE);
                tvProgressPick2.setText(String.valueOf(currentLevel));

                if((currentLevel - 8) == 3) imgTop2Params.width = (int)ONE_LVL_WIDTH_CENTER*(currentLevel-8)+(int)ONE_LVL_WIDTH_CENTER/2;
                else imgTop2Params.width = (int)ONE_LVL_WIDTH_CENTER*(currentLevel-7);

                imgLvlTop1.setLayoutParams(imgTop1Params);
                imgLvlTop2.setLayoutParams(imgTop2Params);
            } else if(currentLevel == 11) {
                imgFirst.setAlpha(1.0f);
                imgLvlTop1.setVisibility(View.VISIBLE);
                imgLvlTop2.setVisibility(View.VISIBLE);
                imgLvl2.setAlpha(1.0f);
                imgLvl3.setAlpha(1.0f);
                imgTop1Params.width = (int)ONE_LVL_WIDTH_LEFTRIGHT*5;
                imgTop2Params.width = (int)ONE_LVL_WIDTH_CENTER*4;
                clProgressPick1.setVisibility(View.INVISIBLE);
                clProgressPick2.setVisibility(View.INVISIBLE);

                imgLvlTop1.setLayoutParams(imgTop1Params);
                imgLvlTop2.setLayoutParams(imgTop2Params);
            } else if(currentLevel > 11 && currentLevel < 15) {
                imgFirst.setAlpha(1.0f);
                imgLvlTop1.setVisibility(View.VISIBLE);
                imgLvlTop2.setVisibility(View.VISIBLE);
                imgLvlTop3.setVisibility(View.VISIBLE);
                imgLvl2.setAlpha(1.0f);
                imgLvl3.setAlpha(1.0f);
                imgTop1Params.width = (int)ONE_LVL_WIDTH_LEFTRIGHT*5;
                imgTop2Params.width = (int)ONE_LVL_WIDTH_CENTER*4;
                clProgressPick1.setVisibility(View.INVISIBLE);
                clProgressPick2.setVisibility(View.INVISIBLE);

                clProgressPick3.setVisibility(View.VISIBLE);
                tvProgressPick3.setText(String.valueOf(currentLevel));

                if((currentLevel - 12) == 3) imgTop3Params.width = (int)ONE_LVL_WIDTH_CENTER*(currentLevel-12)+(int)ONE_LVL_WIDTH_CENTER/2;
                else imgTop3Params.width = (int)ONE_LVL_WIDTH_CENTER*(currentLevel-11);

                imgLvlTop1.setLayoutParams(imgTop1Params);
                imgLvlTop2.setLayoutParams(imgTop2Params);
                imgLvlTop3.setLayoutParams(imgTop3Params);
            } else if(currentLevel == 15) {
                imgFirst.setAlpha(1.0f);
                imgLvlTop1.setVisibility(View.VISIBLE);
                imgLvlTop2.setVisibility(View.VISIBLE);
                imgLvlTop3.setVisibility(View.VISIBLE);
                imgLvl2.setAlpha(1.0f);
                imgLvl3.setAlpha(1.0f);
                imgLvl4.setAlpha(1.0f);
                imgTop1Params.width = (int)ONE_LVL_WIDTH_LEFTRIGHT*5;
                imgTop2Params.width = (int)ONE_LVL_WIDTH_CENTER*4;
                imgTop3Params.width = (int)ONE_LVL_WIDTH_CENTER*4;
                clProgressPick1.setVisibility(View.INVISIBLE);
                clProgressPick2.setVisibility(View.INVISIBLE);
                clProgressPick3.setVisibility(View.INVISIBLE);

                imgLvlTop1.setLayoutParams(imgTop1Params);
                imgLvlTop2.setLayoutParams(imgTop2Params);
                imgLvlTop3.setLayoutParams(imgTop3Params);
            } else if(currentLevel > 15 && currentLevel < 21) {
                imgFirst.setAlpha(1.0f);
                imgLvlTop1.setVisibility(View.VISIBLE);
                imgLvlTop2.setVisibility(View.VISIBLE);
                imgLvlTop3.setVisibility(View.VISIBLE);
                imgLvlTop4.setVisibility(View.VISIBLE);
                imgLvl2.setAlpha(1.0f);
                imgLvl3.setAlpha(1.0f);
                imgLvl4.setAlpha(1.0f);
                imgTop1Params.width = (int)ONE_LVL_WIDTH_LEFTRIGHT*5;
                imgTop2Params.width = (int)ONE_LVL_WIDTH_CENTER*4;
                imgTop3Params.width = (int)ONE_LVL_WIDTH_CENTER*4;
                clProgressPick1.setVisibility(View.INVISIBLE);
                clProgressPick2.setVisibility(View.INVISIBLE);
                clProgressPick3.setVisibility(View.INVISIBLE);

                clProgressPick4.setVisibility(View.VISIBLE);
                tvProgressPick4.setText(String.valueOf(currentLevel));
                if((currentLevel - 16) == 4) imgTop4Params.width = (int)ONE_LVL_WIDTH_LEFTRIGHT*(currentLevel-16)+(int)ONE_LVL_WIDTH_LEFTRIGHT/2;
                else imgTop4Params.width = (int)ONE_LVL_WIDTH_LEFTRIGHT*(currentLevel-15);

                imgLvlTop1.setLayoutParams(imgTop1Params);
                imgLvlTop2.setLayoutParams(imgTop2Params);
                imgLvlTop3.setLayoutParams(imgTop3Params);
                imgLvlTop4.setLayoutParams(imgTop4Params);
            } else if(currentLevel >= 21) {
                imgFirst.setAlpha(1.0f);
                imgLvlTop1.setVisibility(View.VISIBLE);
                imgLvlTop2.setVisibility(View.VISIBLE);
                imgLvlTop3.setVisibility(View.VISIBLE);
                imgLvlTop4.setVisibility(View.VISIBLE);
                imgLvl2.setAlpha(1.0f);
                imgLvl3.setAlpha(1.0f);
                imgLvl4.setAlpha(1.0f);
                imgTop1Params.width = (int)ONE_LVL_WIDTH_LEFTRIGHT*5;
                imgTop2Params.width = (int)ONE_LVL_WIDTH_CENTER*4;
                imgTop3Params.width = (int)ONE_LVL_WIDTH_CENTER*4;
                imgTop4Params.width = (int)ONE_LVL_WIDTH_CENTER*4;
                clProgressPick1.setVisibility(View.INVISIBLE);
                clProgressPick2.setVisibility(View.INVISIBLE);
                clProgressPick3.setVisibility(View.INVISIBLE);
                clProgressPick4.setVisibility(View.INVISIBLE);
                imgLast.setAlpha(1.0f);

                imgLvlTop1.setLayoutParams(imgTop1Params);
                imgLvlTop2.setLayoutParams(imgTop2Params);
                imgLvlTop3.setLayoutParams(imgTop3Params);
                imgLvlTop4.setLayoutParams(imgTop4Params);
            }
        }
    }
    private class CheckForceUpdateTask extends AsyncTask<Void,Void,Boolean> { // проверяет принудительные обновления

        int typeUpdate;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                ResponseFromServer responseFromServer = RequestController.getInstance()
                        .getJsonApi()
                        .checkUpdate(BuildConfig.VERSION_CODE)
                        .execute().body();
                if(responseFromServer.getForceUpdate() > 0) {
                    typeUpdate = responseFromServer.getForceUpdate();
                    return true;
                }
            } catch (IOException e) {
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if(success) {
                switch (typeUpdate) {
                    case 1: { // Обновления ради нового сезона
                        AssistentDialog updateDialog = new AssistentDialog(AssistentDialog.DIALOG_UPDATE_APP);
                        updateDialog.show(getSupportFragmentManager(),"UPDATE");
                        break;
                    }
                    case 2: { // Техническое обновление
                        AssistentDialog updateDialog = new AssistentDialog(AssistentDialog.DIALOG_UPDATE_APP_TECH);
                        updateDialog.show(getSupportFragmentManager(),"UPDATE");
                        break;
                    }
                }
            }
        }
    }
    private class CheckWinsThread extends Thread { // поток, проверяющий основыне данные на главной активити
        private boolean isStop = false;
        @Override
        public void run() {
            while (true) {
                if(!isStop) {
                    RequestController.getInstance() // загрузка данных с сервера
                            .getJsonApi()
                            .getMainData("true3")
                            .enqueue(new Callback<ResponseFromServer>() {
                                @Override
                                public void onResponse(Call<ResponseFromServer> call, Response<ResponseFromServer> response) {
                                    ResponseFromServer responseFromServer = response.body();
                                    if(!responseFromServer.getWinner().equals(StoredData.getDataString(StoredData.DATA_WINS,GetContextClass.getContext().getResources().getString(R.string.no_winner_text)))) {
                                        if(responseFromServer.getExistWinner() == 1) {
                                            if(responseFromServer.getWinner().equals("none")) {
                                                StoredData.saveData(StoredData.DATA_WINS,getResources().getString(R.string.winner_didnt_name));
                                                tvWinner.setText(getResources().getString(R.string.winner_didnt_name));
                                            } else {
                                                StoredData.saveData(StoredData.DATA_WINS,responseFromServer.getWinner());
                                                tvWinner.setText(responseFromServer.getWinner());
                                            }
                                        } else {
                                            tvWinner.setText(getResources().getString(R.string.no_winner_text));
                                            StoredData.saveData(StoredData.DATA_WINS,getResources().getString(R.string.no_winner_text));
                                        }
                                    }
                                    if(!responseFromServer.getPrize().equals(StoredData.getDataString(StoredData.DATA_PRIZE,GetContextClass.getContext().getResources().getString(R.string.prize)))) {
                                        StoredData.saveData(StoredData.DATA_PRIZE,responseFromServer.getPrize());
                                        tvPrize = findViewById(R.id.tvPrize);
                                        tvPrize.setText(responseFromServer.getPrize());
                                        tvPrize.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        tvPrize.startAnimation(0,1);
                                    }
                                    if(!responseFromServer.getSeason().equals(StoredData.getDataString(StoredData.DATA_SEASON,GetContextClass.getContext().getResources().getString(R.string.season)))) {
                                        StoredData.saveData(StoredData.DATA_SEASON,responseFromServer.getSeason());
                                        tvSeason.setText(responseFromServer.getSeason());
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseFromServer> call, Throwable t) {
                                }
                            });

                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else return;
            }
        }
        public void toStop() { // остановить поток
            isStop = true;
        }
    }
}
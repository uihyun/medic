package com.uihyun.medic.page;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.uihyun.medic.MainActivity;
import com.uihyun.medic.R;
import com.uihyun.medic.vo.Medicine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Uihyun on 2016. 9. 23..
 */
public class SplashActivity extends Activity {

    public static final int SEARCHED_LIST_SIZE = 10;

    public static final List<Medicine> favoriteMedicineList = new ArrayList<>();
    public static final List<String> searchedNameList = new ArrayList<>();
    public static final List<String> searchedIndgList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView imageView = (ImageView) findViewById(R.id.splash);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.splash));

        Handler hd = new Handler();
        hd.postDelayed(new splashHandler(), 2000); // 2초 후에 Handler 실행

        // favorite
        SharedPreferences prefs = getSharedPreferences("favorite", MODE_PRIVATE);
        Gson gson = new Gson();

        if (favoriteMedicineList.size() > 0)
            favoriteMedicineList.clear();
        Medicine medicine;
        for (int i = 0; i < FavoriteActivity.FAVORITE_SIZE; i++) {
            String json = prefs.getString("favorite." + i, null);
            if (json != null) {
                medicine = gson.fromJson(json, Medicine.class);
                favoriteMedicineList.add(medicine);
            }
        }

        // recently searched text - name
        prefs = getSharedPreferences("searchedNameList", MODE_PRIVATE);

        if (searchedNameList.size() > 0)
            searchedNameList.clear();
        for (int i = SEARCHED_LIST_SIZE - 1; i > -1; i--) {
            String text = prefs.getString("name." + i, null);
            if (text != null) {
                searchedNameList.add(text);
            }
        }

        // recently searched text - indg
        prefs = getSharedPreferences("searchedIndgList", MODE_PRIVATE);

        if (searchedIndgList.size() > 0)
            searchedIndgList.clear();
        for (int i = SEARCHED_LIST_SIZE - 1; i > -1; i--) {
            String text = prefs.getString("indg." + i, null);
            if (text != null) {
                searchedIndgList.add(text);
            }
        }
    }

    private class splashHandler implements Runnable {
        public void run() {
            startActivity(new Intent(getApplication(), MainActivity.class)); // 로딩이 끝난후 이동할 Activity
            SplashActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거
        }
    }
}

package com.uihyun.medic.page;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.uihyun.medic.Medicine;
import com.uihyun.medic.R;
import com.uihyun.medic.TabActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Uihyun on 2016. 9. 23..
 */
public class SplashActivity extends Activity {

    public static final List<Medicine> favoriteMedicineList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView imageView = (ImageView) findViewById(R.id.splash);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.splash));

        Handler hd = new Handler();
        hd.postDelayed(new splashHandler(), 2000); // 2초 후에 Handler 실행

        final SharedPreferences prefs = getSharedPreferences("favorite", MODE_PRIVATE);
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
    }

    private class splashHandler implements Runnable {
        public void run() {
            startActivity(new Intent(getApplication(), TabActivity.class)); // 로딩이 끝난후 이동할 Activity
            SplashActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거
        }
    }
}

package com.uihyun.medic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView imageView = (ImageView) findViewById(R.id.splash);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.splash));

        Handler hd = new Handler();
        hd.postDelayed(new splashHandler(), 2000); // 3초 후에 Handler 실행
    }

    private class splashHandler implements Runnable{
        public void run() {
            startActivity(new Intent(getApplication(), TabActivity.class)); // 로딩이 끝난후 이동할 Activity
            SplashActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거
        }
    }
}

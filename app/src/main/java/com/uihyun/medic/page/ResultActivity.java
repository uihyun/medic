package com.uihyun.medic.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.uihyun.medic.CustomProgressDialog;
import com.uihyun.medic.Medicine;
import com.uihyun.medic.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends Activity {

    private AsyncPostData asyncPostData;
    private Medicine medicine;

    private Button favoriteButton;
    private TextView nameTextView;
    private TextView descTextView;
    private ImageView resultImageView;

    private ImageView guideImage0;
    private ImageView guideImage1;
    private ImageView guideImage2;
    private ImageView guideImage3;
    private ImageView guideImage4;
    private ImageView guideImage5;
    private ImageView guideImage6;
    private ImageView guideImage7;

    private TextView guideWhat;
    private TextView guideWhatContent;
    private TextView guideHow;
    private TextView guideHowContent;
    private TextView guideUsage;
    private TextView guideUsageContent;
    private TextView guideStore;
    private TextView guideStoreContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        medicine = (Medicine) intent.getSerializableExtra("medicine");

        favoriteButton = (Button) findViewById(R.id.add_favorite);
        nameTextView = (TextView) findViewById(R.id.result_name);
        descTextView = (TextView) findViewById(R.id.result_desc);
        resultImageView = (ImageView) findViewById(R.id.result_image);

        guideImage0 = (ImageView) findViewById(R.id.guide_0);
        guideImage1 = (ImageView) findViewById(R.id.guide_1);
        guideImage2 = (ImageView) findViewById(R.id.guide_2);
        guideImage3 = (ImageView) findViewById(R.id.guide_3);
        guideImage4 = (ImageView) findViewById(R.id.guide_4);
        guideImage5 = (ImageView) findViewById(R.id.guide_5);
        guideImage6 = (ImageView) findViewById(R.id.guide_6);
        guideImage7 = (ImageView) findViewById(R.id.guide_7);

        guideWhat = (TextView) findViewById(R.id.guide_what);
        guideWhatContent = (TextView) findViewById(R.id.guide_what_content);
        guideHow = (TextView) findViewById(R.id.guide_how);
        guideHowContent = (TextView) findViewById(R.id.guide_how_content);
        guideUsage = (TextView) findViewById(R.id.guide_usage);
        guideUsageContent = (TextView) findViewById(R.id.guide_usage_content);
        guideStore = (TextView) findViewById(R.id.guide_store);
        guideStoreContent = (TextView) findViewById(R.id.guide_store_content);

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                // set the state of button
                button.setSelected(!button.isSelected());
            }
        });

        asyncPostData = new AsyncPostData(this, medicine);
        asyncPostData.execute();
    }

    public class AsyncPostData extends AsyncTask<Void, Void, Void> {
        private String strUrl;
        private String resultWhatContent;
        private String resultHowContent;
        private Context context;
        private Medicine medicine;
        private Bitmap bitmapMainImage;
        private List<Bitmap> bitmaps;
        private CustomProgressDialog progressDialog;

        public AsyncPostData(Context context, Medicine medicine) {
            this.context = context;
            this.medicine = medicine;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = CustomProgressDialog.show(context, "", false, null);
            strUrl = "http://www.health.kr/drug_info/basedrug/" + medicine.getDetailLink();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            InputStream is = null;
            bitmaps = new ArrayList<Bitmap>();
            try {
                if (medicine.getImageUrl() != null) {
                    URL imageUrl = new URL(medicine.getImageUrl());
                    bitmapMainImage = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                } else {
//                resultImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), getResources().getDrawable(R.drawable.no_image)));
                }

                URL url = new URL(strUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST"); // post방식 통신
                conn.setDoOutput(true);       // 쓰기모드 지정
                conn.setDoInput(true);        // 읽기모드 지정
                conn.setUseCaches(false);     // 캐싱데이터를 받을지 안받을지
                conn.setDefaultUseCaches(false); // 캐싱데이터 디폴트 값 설정

                is = conn.getInputStream();        //input스트림 개방

                BufferedReader br = new BufferedReader(new InputStreamReader(is, "EUC-KR"));  //문자열 셋 세팅
                String line;

                while ((line = br.readLine()) != null) {
                    if (line.contains("medi_guide")) {
                        line = line.substring(line.indexOf("medi_"));
                        medicine.setGuideLink(line.substring(0, line.indexOf("COORDS") - 2));
                        strUrl = "http://www.health.kr/drug_info/basedrug/" + medicine.getGuideLink();
                    } else if (line.contains("저장방법")) {
                        line = br.readLine();
                        line = line.substring(line.indexOf('>') + 1, line.lastIndexOf('<'));
                        medicine.setStore(line);
//                    } else if (line.contains("tabcon_effect") && !line.contains("changeTab")) {
//                        br.readLine();
//                        br.readLine();
//                        br.readLine();
//                        br.readLine();
//                        line = br.readLine();
//                        line = line.substring(line.indexOf("break-all") + 12, line.lastIndexOf("</td>"));
//                        if (line.indexOf("&nbsp;") > -1)
//                            line = line.replaceAll("&nbsp;", " ");
//                        if (line.indexOf("<br>") > -1)
//                            line = line.replaceAll("<br>", "\n");
//                        medicine.setEffect(line);
                    } else if (line.contains("tabcon_dosage") && !line.contains("changeTab")) {
                        br.readLine();
                        br.readLine();
                        br.readLine();
                        br.readLine();
                        line = br.readLine();
                        line = line.substring(line.indexOf("break-all") + 12);
                        if (line.indexOf("</td>") > -1)
                            line = line.substring(0, line.indexOf("</td>"));
                        if (line.lastIndexOf("<TABLE") > -1)
                            line = line.substring(0, line.lastIndexOf("<TABLE") - 4);
                        if (line.indexOf("&nbsp;") > -1)
                            line = line.replaceAll("&nbsp;", " ");
                        if (line.indexOf("<br><br>") > -1)
                            line = line.replaceAll("<br><br>", "\n");
                        if (line.indexOf("<br>") > -1)
                            line = line.replaceAll("<br>", "\n");
                        medicine.setUsage(line);
                        break;
                    }
                }

                url = new URL(strUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST"); // post방식 통신
                conn.setDoOutput(true);       // 쓰기모드 지정
                conn.setDoInput(true);        // 읽기모드 지정
                conn.setUseCaches(false);     // 캐싱데이터를 받을지 안받을지
                conn.setDefaultUseCaches(false); // 캐싱데이터 디폴트 값 설정

                is = conn.getInputStream();        //input스트림 개방

                br = new BufferedReader(new InputStreamReader(is, "EUC-KR"));  //문자열 셋 세팅

                while ((line = br.readLine()) != null) {
                    if (line.contains("li style")) {
                        if (line.contains("images")) {
                            line = line.substring(line.indexOf("images") - 1, line.indexOf("alt") - 2);
                            URL imageUrl = new URL("http://www.health.kr" + line);
                            bitmaps.add(BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream()));
                        }
                    } else if (line.contains("1. 이 약은 무슨")) {
                        br.readLine();
                        br.readLine();
                        br.readLine();
                        br.readLine();
                        line = br.readLine();
                        line = line.substring(line.indexOf("<td>") + 4);
                        if (line.indexOf("</td>") > -1)
                            line = line.substring(0, line.indexOf("</td>"));
                        if (line.indexOf("\t") > -1)
                            line = line.replaceAll("\t", "");
                        if (line.indexOf("<br>") > -1)
                            line = line.replaceAll("<br>", "\n");
                        resultWhatContent = line;
                    } else if (line.contains("2. 이 약은 어떻게")) {
                        br.readLine();
                        br.readLine();
                        br.readLine();
                        br.readLine();
                        br.readLine();
                        line = br.readLine();
                        line = line.substring(line.indexOf("<td>") + 4);
                        if (line.indexOf("</td>") > -1)
                            line = line.substring(0, line.indexOf("</td>"));
                        if (line.indexOf("\t") > -1)
                            line = line.replaceAll("\t", "");
                        if (line.indexOf("<br>") > -1)
                            line = line.replaceAll("<br>", "\n");
                        resultHowContent = line;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();

            nameTextView.setText(medicine.getName());
            descTextView.setText(medicine.getIngredient());
            resultImageView.setImageBitmap(bitmapMainImage);

            for (int i = 0; i < bitmaps.size(); i++) {
                switch (i) {
                    case 0:
                        guideImage0.setImageBitmap(bitmaps.get(0));
                        break;
                    case 1:
                        guideImage1.setImageBitmap(bitmaps.get(1));
                        break;
                    case 2:
                        guideImage2.setImageBitmap(bitmaps.get(2));
                        break;
                    case 3:
                        guideImage3.setImageBitmap(bitmaps.get(3));
                        break;
                    case 4:
                        guideImage4.setImageBitmap(bitmaps.get(4));
                        break;
                    case 5:
                        guideImage5.setImageBitmap(bitmaps.get(5));
                        break;
                    case 6:
                        guideImage6.setImageBitmap(bitmaps.get(6));
                        break;
                    case 7:
                        guideImage7.setImageBitmap(bitmaps.get(7));
                        break;
                    default:
                        break;
                }
            }

            guideWhat.setText(R.string.guide_what);
            guideWhatContent.setText(resultWhatContent);
            guideHow.setText(R.string.guide_how);
            guideHowContent.setText(resultHowContent);
            guideUsage.setText(R.string.guide_usage);
            guideUsageContent.setText(medicine.getUsage());
            guideStore.setText(R.string.guide_store);
            guideStoreContent.setText(medicine.getStore());
        }
    }
}

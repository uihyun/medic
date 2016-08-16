package com.uihyun.medic.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.uihyun.medic.CustomProgressDialog;
import com.uihyun.medic.Medicine;
import com.uihyun.medic.R;
import com.uihyun.medic.list.ListViewAdapter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class IndgActivity extends Activity {

    protected List<Medicine> medicines;
    private String enteredText;
    private ListViewAdapter adapter;
    private EditText searchText;
    private ListView listView;
    private AsyncPostData asyncPostData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indg);

        // Adapter 생성
        adapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listView = (ListView) findViewById(R.id.list_view_indg);
        listView.setAdapter(adapter);

        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // 결과 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                intent.putExtra("medicine", medicines.get(position));
                startActivity(intent);
            }
        });

        searchText = (EditText) findViewById(R.id.search_text);
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            if (adapter.getCount() != 0)
                                adapter.removeListViewItems();

                            enteredText = searchText.getText().toString();
                            hideKeyboard();
                            if (enteredText != null && enteredText.length() != 0) {
                                asyncPostData = new AsyncPostData(v.getContext());
                                asyncPostData.execute();
                            }
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public class AsyncPostData extends AsyncTask<Void, Void, Void> {
        private String strUrl;
        private String result;
        private Context context;
        private CustomProgressDialog progressDialog;

        public AsyncPostData(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = CustomProgressDialog.show(context, "", false, null);
            strUrl = "http://www.health.kr/drug_info/basedrug/list.asp";
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DataOutputStream os = null;
            InputStream is = null;
            medicines = new ArrayList<>();
            try {
                URL url = new URL(strUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST"); // post방식 통신
                conn.setDoOutput(true);       // 쓰기모드 지정
                conn.setDoInput(true);        // 읽기모드 지정
                conn.setUseCaches(false);     // 캐싱데이터를 받을지 안받을지
                conn.setDefaultUseCaches(false); // 캐싱데이터 디폴트 값 설정

                StringBuffer sbPost = new StringBuffer();
                sbPost.append("drug_name").append("=").append(URLEncoder.encode("", "EUC-KR")).append("&");
                sbPost.append("sunb_name").append("=").append(URLEncoder.encode(enteredText, "EUC-KR")).append("&");
                sbPost.append("firm_name").append("=").append(URLEncoder.encode("", "EUC-KR"));

                os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(sbPost.toString());
                os.flush();

                is = conn.getInputStream();        //input스트림 개방

                BufferedReader br = new BufferedReader(new InputStreamReader(is, "EUC-KR"));  //문자열 셋 세팅
                String line;
                String hrefLine = null;


                while ((line = br.readLine()) != null) {
                    if (line.contains("<font color='red'>" + enteredText)) {
                        Medicine medicine = new Medicine();
                        // 링크
                        hrefLine = line.substring(line.indexOf("show_"), line.indexOf('>', line.indexOf('>') + 1) - 1);
                        medicine.setDetailLink(hrefLine);

                        // 제품명
                        line = line.substring(line.indexOf('>', line.indexOf('>') + 1) + 1, line.indexOf("</A>"));
                        line = line.replaceAll("<font color='red'>", "").replaceAll("</font>", "");
                        medicine.setName(line);

                        // 성분/함량
                        br.readLine();
                        line = br.readLine();
                        line = line.substring(line.indexOf('>') + 2);
                        if (line.contains("\t"))
                            line = line.replaceAll("\t", "");
                        medicine.setIngredient(line);

                        // 제조수입사
                        br.readLine();
                        br.readLine();
                        line = br.readLine();
//                        line = line.substring(line.indexOf('>') + 1, line.lastIndexOf('<'));
//                        medicine.setCompany(line);

                        // 분류
                        br.readLine();
                        line = br.readLine();
//                        line = line.substring(line.indexOf('>') + 1, line.lastIndexOf('<'));
//                        medicine.setClassification(line);

                        // 투여경로
                        line = br.readLine();
//                        line = line.substring(line.indexOf('>') + 1, line.lastIndexOf('<'));
//                        medicine.setRoute(line);

                        // 제형
                        line = br.readLine();
//                        line = line.substring(line.indexOf('>') + 1, line.lastIndexOf('<'));
//                        medicine.setType(line);

                        // 구분
                        line = br.readLine();
//                        line = line.substring(line.indexOf('>') + 1, line.lastIndexOf('<'));
//                        medicine.setCategory(line);

                        // 보험
                        line = br.readLine();
//                        line = line.substring(line.indexOf('>') + 1, line.lastIndexOf('<'));
//                        medicine.setInsurance(line);

                        // 이미지
                        Bitmap image = null;
                        br.readLine();
                        br.readLine();
                        br.readLine();
                        line = br.readLine();
                        if (line.contains("sbcode")) {
                            line = line.substring(line.indexOf("sbcode") + 7, line.indexOf("class") - 2);
                            medicine.setId(line);
                            URL imageUrl = new URL(medicine.getSmallImageUrl());
                            image = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                        } else {
//                            image = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image);
                        }

                        medicines.add(medicine);
                        adapter.addItem(image, medicine.getName(), medicine.getIngredient() + "\n");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (os != null)
                        os.close();
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

            if (medicines.size() > 0)
                result = medicines.size() + "개의 결과가 검색되었습니다.";
            else
                result = "검색된 결과가 없습니다.";

            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

            listView.setSelectionAfterHeaderView();
        }
    }
}

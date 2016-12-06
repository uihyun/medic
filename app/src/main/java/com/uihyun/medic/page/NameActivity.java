package com.uihyun.medic.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.uihyun.medic.R;
import com.uihyun.medic.dialog.CustomProgressDialog;
import com.uihyun.medic.list.ListViewAdapter;
import com.uihyun.medic.vo.Medicine;

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

/**
 * Created by Uihyun on 2016. 9. 23..
 */
public class NameActivity extends Activity {

    private List<Medicine> medicines;
    private ListViewAdapter adapter;
    private AutoCompleteTextView searchText;
    private ListView listView;
    private AsyncPostData asyncPostData;

    private String enteredText;
    private int pageNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        // Adapter 생성
        adapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listView = (ListView) findViewById(R.id.list_view_name);
        listView.setAdapter(adapter);

        medicines = new ArrayList<>();

        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                if (medicines.size() > position) {
                    // 결과 페이지로 이동
                    Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                    intent.putExtra("medicine", medicines.get(position));
                    startActivity(intent);
                } else {
                    adapter.removeListViewItem(position);
                    hideKeyboard();
                    enteredText = searchText.getText().toString();
                    pageNum = pageNum + 1;

                    asyncPostData = new AsyncPostData(v.getContext());
                    asyncPostData.execute();
                }
            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.autotext_item,
                R.id.autoCompleteItem, SplashActivity.searchedNameList.toArray(new String[0]));
        searchText = (AutoCompleteTextView) findViewById(R.id.search_text);
        searchText.setThreshold(0);
        searchText.setAdapter(arrayAdapter);
        searchText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (SplashActivity.searchedNameList.size() > 0) {
                        searchText.showDropDown();
                    }
                }
                return false;
            }
        });
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (medicines.size() > 0)
                        medicines.clear();
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            if (adapter.getCount() != 0)
                                adapter.removeListViewItems();

                            enteredText = searchText.getText().toString();
                            pageNum = 1;
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

    public void addSearchedText(String enteredText) {
        SharedPreferences prefs = getSharedPreferences("searchedNameList", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // 중복이 있을 경우, 제일 앞 순서로 이동
        for (int i = 0; i < SplashActivity.searchedNameList.size(); i++) {
            if (SplashActivity.searchedNameList.get(i).equals(enteredText)) {
                SplashActivity.searchedNameList.remove(i);
                SplashActivity.searchedNameList.add(0, enteredText);
                return;
            }
        }

        // 중복은 없고 사이즈가 다 찼을 경우, 제일 앞 순서 삭제 후 추가
        if (SplashActivity.searchedNameList.size() == SplashActivity.SEARCHED_LIST_SIZE) {
            SplashActivity.searchedNameList.remove(0);
            SplashActivity.searchedNameList.add(enteredText);
            editor.remove("name.0");
            for (int i = 1; i < SplashActivity.SEARCHED_LIST_SIZE; i++) {
                int j = i - 1;
                editor.putString("name." + j, prefs.getString("name." + i, null));
                if (i == SplashActivity.SEARCHED_LIST_SIZE - 1)
                    editor.putString("name." + i, enteredText);
            }
        } else {
            // 리스트에 추가
            for (int i = 0; i < SplashActivity.SEARCHED_LIST_SIZE; i++) {
                if (prefs.getString("name." + i, null) == null) {
                    SplashActivity.searchedNameList.add(enteredText);
                    editor.putString("name." + i, enteredText);
                    break;
                }
            }
        }


        editor.apply();
        editor.commit();
    }

    public class AsyncPostData extends AsyncTask<Void, Void, Void> {
        private String strUrl;
        private String result;
        private Context context;
        private CustomProgressDialog progressDialog;
        private boolean hasNextPage;

        public AsyncPostData(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = CustomProgressDialog.show(context, "", false, null);
            strUrl = "http://www.health.kr/drug_info/basedrug/drug_list.asp";
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DataOutputStream os = null;
            InputStream is = null;
            try {
                URL url = new URL(strUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST"); // post방식 통신
                conn.setDoOutput(true);       // 쓰기모드 지정
                conn.setDoInput(true);        // 읽기모드 지정
                conn.setUseCaches(false);     // 캐싱데이터를 받을지 안받을지
                conn.setDefaultUseCaches(false); // 캐싱데이터 디폴트 값 설정

                StringBuffer sbPost = new StringBuffer();
                sbPost.append("drug_name").append("=").append(URLEncoder.encode(enteredText, "EUC-KR")).append("&");
                sbPost.append("sunb_name").append("=").append(URLEncoder.encode("", "EUC-KR")).append("&");
                sbPost.append("firm_name").append("=").append(URLEncoder.encode("", "EUC-KR")).append("&");
                sbPost.append("_c_tab").append("=").append(URLEncoder.encode("all_pro", "EUC-KR")).append("&");
                sbPost.append("x").append("=").append(URLEncoder.encode("0", "EUC-KR")).append("&");
                sbPost.append("y").append("=").append(URLEncoder.encode("0", "EUC-KR")).append("&");
                sbPost.append("_page").append("=").append(URLEncoder.encode(Integer.toString(pageNum), "EUC-KR"));

                os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(sbPost.toString());
                os.flush();

                is = conn.getInputStream();        //input스트림 개방

                BufferedReader br = new BufferedReader(new InputStreamReader(is, "EUC-KR"));  //문자열 셋 세팅
                String line;
                String hrefLine;

                while ((line = br.readLine()) != null) {
                    if (line.contains("show_detail")) {
                        Medicine medicine = new Medicine();
                        // 링크
                        hrefLine = line.substring(line.indexOf("show_"), line.indexOf('>', line.indexOf('>') + 1) - 1);
                        medicine.setDetailLink(hrefLine);
                        if (hrefLine.contains("btn_pop_img"))
                            continue;

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
                        }
                        // XXX no image에 대한 image를 굳이 넣을 필요는 없겠지
//                        else {
//                            image = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image);
//                        }

                        medicines.add(medicine);
                        adapter.addItem(image, medicine.getName(), medicine.getIngredient());
                    }

                    if (line.contains("icon_prev_img")) {
                        while ((line = br.readLine()) != null) {
                            if (line.contains("javascript:go_page")) {
                                hasNextPage = true;
                                break;
                            }
                        }
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

            if (hasNextPage) {
                adapter.addItem(null, "더 보기", null);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();

            if (medicines.size() > 0) {
                if (hasNextPage) {
                    if (pageNum == 1)
                        result = medicines.size() + "개 이상의 결과가 검색되었습니다.";
                } else
                    result = medicines.size() + "개의 결과가 검색되었습니다.";

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        addSearchedText(enteredText);
                    }
                }).start();
            } else
                result = "검색된 결과가 없습니다.";

            if (!hasNextPage || pageNum == 1)
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

            listView.setSelectionAfterHeaderView();

            if (pageNum != 1)
                listView.setSelection(listView.getCount());
        }
    }
}

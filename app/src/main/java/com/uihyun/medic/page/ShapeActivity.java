package com.uihyun.medic.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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

/**
 * Created by Uihyun on 2016. 9. 23..
 */
public class ShapeActivity extends Activity {

    private List<Medicine> medicines;
    private Spinner typeSpinner;
    private Spinner colorSpinner;
    private Spinner shapeSpinner;
    private Spinner lineSpinner;
    private EditText searchText;
    private TextView summaryText;
    private ListView listView;
    private ListViewAdapter adapter;
    private AsyncPostData asyncPostData;

    private String enteredType;
    private String enteredColor;
    private String enteredShape;
    private String enteredLine;
    private String enteredText;
    private int pageNum = 1;

    private String[] types = {"정제류", "경질캡슐", "연질캡슐", "기타"};
    private String[] colors = {"하양", "노랑", "주황", "분홍", "빨강", "갈색", "연두", "초록", "청록", "파랑", "남색", "자주", "회색", "검정", "투명"};
    private String[] shapes = {"원형", "반원형", "삼각형", "사각형", "마름모형", "팔각형", "장방형", "타원형", "육각형", "오각형", "8자형", "강낭콩형", "과일모양", "과일모양", "구형", "나비넥타이형", "다이아몬드형", "도넛형", "동물모양", "땅콩형", "레몬형", "물방울형", "방패형", "볼록한 사각형", "사과형", "심장형", "십각형", "애벌래형", "얼굴모양", "오목한 삼각형", "클로버형", "탄환형"};
    private String[] lines = {"없음", "-", "+", "기타"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape);

        typeSpinner = (Spinner) findViewById(R.id.spinner_type);
        colorSpinner = (Spinner) findViewById(R.id.spinner_color);
        shapeSpinner = (Spinner) findViewById(R.id.spinner_shape);
        lineSpinner = (Spinner) findViewById(R.id.spinner_line);

        searchText = (EditText) findViewById(R.id.search_text);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, types);
        // Specify the layout to use when the list of choices appears
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setPrompt(getString(R.string.prompt_type));

        ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, colors);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(colorAdapter);
        colorSpinner.setPrompt(getString(R.string.prompt_color));

        ArrayAdapter<String> shapeAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, shapes);
        shapeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shapeSpinner.setAdapter(shapeAdapter);
        shapeSpinner.setPrompt(getString(R.string.prompt_shape));

        ArrayAdapter<String> lineAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, lines);
        lineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lineSpinner.setAdapter(lineAdapter);
        lineSpinner.setPrompt(getString(R.string.prompt_line));

        // Adapter 생성
        adapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listView = (ListView) findViewById(R.id.list_view_shape);
        listView.setAdapter(adapter);

        // summary of saerching shape
        LinearLayout layout = (LinearLayout) findViewById(R.id.summary_layout);
        layout.setVisibility(LinearLayout.GONE);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    LinearLayout layout = (LinearLayout) findViewById(R.id.search_layout);
                    layout.setVisibility(LinearLayout.VISIBLE);

                    layout = (LinearLayout) findViewById(R.id.summary_layout);
                    layout.setVisibility(LinearLayout.GONE);
                    return true;
                }
                return false;
            }
        });

        summaryText = (TextView) findViewById(R.id.summary);

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
                    enteredType = typeSpinner.getSelectedItem().toString();
                    if (enteredType.equals("정제류"))
                        enteredType = "on, 나정, 필름코팅정, 설하정, 붕해(현탁)정, 당의정, 다층정, 저작정, 트로키정, 좌제";
                    enteredColor = colorSpinner.getSelectedItem().toString();
                    enteredShape = shapeSpinner.getSelectedItem().toString();
                    enteredLine = lineSpinner.getSelectedItem().toString();
                    if (enteredLine.equals("없음"))
                        enteredLine = "";
                    enteredText = searchText.getText().toString();
                    pageNum = pageNum + 1;

                    asyncPostData = new AsyncPostData(v.getContext());
                    asyncPostData.execute();
                }
            }
        });

        Button searchButton = (Button) findViewById(R.id.button_search);
        searchButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (adapter.getCount() != 0)
                        adapter.removeListViewItems();

                    hideKeyboard();
                    enteredType = typeSpinner.getSelectedItem().toString();
                    if (enteredType.equals("정제류"))
                        enteredType = "on, 나정, 필름코팅정, 설하정, 붕해(현탁)정, 당의정, 다층정, 저작정, 트로키정, 좌제";
                    enteredColor = colorSpinner.getSelectedItem().toString();
                    enteredShape = shapeSpinner.getSelectedItem().toString();
                    enteredLine = lineSpinner.getSelectedItem().toString();
                    if (enteredLine.equals("없음"))
                        enteredLine = "";
                    enteredText = searchText.getText().toString();
                    pageNum = 1;

                    asyncPostData = new AsyncPostData(v.getContext());
                    asyncPostData.execute();

                    if (summaryText.getText().length() != 0)
                        summaryText.setText("");

                    LinearLayout layout = (LinearLayout) findViewById(R.id.search_layout);
                    layout.setVisibility(LinearLayout.GONE);

                    layout = (LinearLayout) findViewById(R.id.summary_layout);
                    layout.setVisibility(LinearLayout.VISIBLE);
                    return true;
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
        private boolean hasNextPage;

        public AsyncPostData(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = CustomProgressDialog.show(context, "", false, null);
            strUrl = "http://www.health.kr/drug_info/sb/list.asp";
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
                sbPost.append("print").append("=").append(URLEncoder.encode(enteredText, "EUC-KR")).append("&");
                sbPost.append("match").append("=").append(URLEncoder.encode("include", "EUC-KR")).append("&");
                sbPost.append("drug_form").append("=").append(URLEncoder.encode(enteredType, "EUC-KR")).append("&");
                sbPost.append("drug_color").append("=").append(URLEncoder.encode(enteredColor, "EUC-KR")).append("&");
                sbPost.append("drug_shape").append("=").append(URLEncoder.encode(enteredShape, "EUC-KR")).append("&");
                sbPost.append("drug_line").append("=").append(URLEncoder.encode(enteredLine, "EUC-KR")).append("&");
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
                    if (line.contains("chk")) {
                        Medicine medicine = new Medicine();
                        // 제품명
                        line = line.substring(0, line.indexOf("식별정보") - 1);
                        line = line.substring(line.lastIndexOf('>') + 1, line.length());
                        medicine.setName(line);

                        // 이미지
                        Bitmap image = null;
                        br.readLine();
                        br.readLine();
                        line = br.readLine();
                        if (line.contains("sbcode")) {
                            line = line.substring(line.indexOf("sbcode") + 7, line.indexOf("class") - 2);
                            medicine.setId(line);
                            URL imageUrl = new URL(medicine.getSmallImageUrl());
                            image = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                        }

                        // 링크
                        br.readLine();
                        br.readLine();
                        br.readLine();
                        br.readLine();
                        br.readLine();
                        br.readLine();
                        br.readLine();
                        line = br.readLine();
                        hrefLine = line.substring(line.indexOf("show_"), line.indexOf("target") - 2);
                        medicine.setDetailLink(hrefLine);

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

            if (hasNextPage)
                adapter.addItem(null, "더 보기", null);

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
            } else
                result = "검색된 결과가 없습니다.";

            if (!hasNextPage || pageNum == 1)
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

            if (searchText.getText().length() != 0)
                summaryText.setText(typeSpinner.getSelectedItem().toString() + ", " +
                        colorSpinner.getSelectedItem().toString() + ", " +
                        shapeSpinner.getSelectedItem().toString() + ", " +
                        lineSpinner.getSelectedItem().toString() + ", " +
                        searchText.getText() + "\n클릭하면 검색창이 다시 나옵니다.");
            else
                summaryText.setText(typeSpinner.getSelectedItem().toString() + ", " +
                        colorSpinner.getSelectedItem().toString() + ", " +
                        shapeSpinner.getSelectedItem().toString() + ", " +
                        lineSpinner.getSelectedItem().toString() + "\n클릭하면 검색창이 다시 나옵니다.");

            listView.setSelectionAfterHeaderView();

            if (pageNum != 1)
                listView.setSelection(listView.getCount());
        }
    }
}

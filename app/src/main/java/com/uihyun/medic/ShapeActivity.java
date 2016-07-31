package com.uihyun.medic;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class ShapeActivity extends Activity {

    private Spinner typeSpinner;
    private Spinner colorSpinner;
    private Spinner shapeSpinner;
    private Spinner lineSpinner;
    private EditText editText;

    private String[] types = {"정제류", "경질캡슐", "연질캡슐", "기타"};
    private String[] colors = {"하양", "노랑", "주황", "분홍", "빨강", "갈색", "연두", "초록", "청록", "파랑", "남색", "자주", "회색", "검정", "투명"};
    private String[] shapes = {"원형", "반원형", "삼각형", "사각형", "마름모형", "팔각형", "장방형", "타원형", "육각형", "오각형", "8자형", "강낭콩형", "과일모양", "과일모양", "구형", "나비넥타이형", "다이아몬드형", "도넛형", "동물모양", "땅콩형", "레몬형", "물방울형", "방패형", "볼록한 사각형", "사과형", "심장형", "십각형", "애벌래형", "얼굴모양", "오목한 삼각형", "클로버형", "탄환형"};
    private String[] lines = {"없음", "-형", "+형", "기타"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape);

        typeSpinner = (Spinner) findViewById(R.id.spinner_type);
        colorSpinner = (Spinner) findViewById(R.id.spinner_color);
        shapeSpinner = (Spinner) findViewById(R.id.spinner_shape);
        lineSpinner = (Spinner) findViewById(R.id.spinner_line);

        editText = (EditText) findViewById(R.id.search_text);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, types);
        // Specify the layout to use when the list of choices appears
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        typeSpinner.setAdapter(typeAdapter);

        ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, colors);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(colorAdapter);

        ArrayAdapter<String> shapeAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, shapes);
        shapeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shapeSpinner.setAdapter(shapeAdapter);

        ArrayAdapter<String> lineAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, lines);
        lineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lineSpinner.setAdapter(lineAdapter);
    }
}

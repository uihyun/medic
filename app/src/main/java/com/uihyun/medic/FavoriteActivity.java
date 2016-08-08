package com.uihyun.medic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends Activity {

    protected List<Medicine> medicines;
    private ListView listView;
    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        // Adapter 생성
        adapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listView = (ListView) findViewById(R.id.favorite_list);
        listView.setAdapter(adapter);

        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // 결과 페이지로 이동
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
//                intent.putExtra("medicine", (Serializable) medicines.get(position));
                startActivity(intent);
            }
        });

        // XXX 저장되어 있는 favorite medicines 불러오기. splash에서 아예 처음에 static으로 불러와둘까.
        medicines = new ArrayList<>();

        for (int i = 0; i < medicines.size(); i++) {
            // XXX image를 bitmap으로 저장해볼까. serialized 처리 떄문에 그런데 알아보자
            adapter.addItem(null, medicines.get(i).getName(), medicines.get(i).getIngredient() + "\n");
        }
    }
}

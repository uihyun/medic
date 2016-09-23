package com.uihyun.medic.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.uihyun.medic.CustomProgressDialog;
import com.uihyun.medic.Medicine;
import com.uihyun.medic.R;
import com.uihyun.medic.list.ListViewAdapter;

import java.net.URL;
import java.util.List;

/**
 * Created by Uihyun on 2016. 9. 23..
 */
public class FavoriteActivity extends Activity {

    public static final int FAVORITE_SIZE = 20;

    private List<Medicine> medicines;
    private ListView listView;
    private ListViewAdapter adapter;
    private Button refreshButton;
    private AsyncPostData asyncPostData;

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
                intent.putExtra("medicine", medicines.get(position));
                startActivity(intent);
            }
        });

        refreshButton = (Button) findViewById(R.id.refresh_favorite);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                onStart();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        asyncPostData = new AsyncPostData(this);
        asyncPostData.execute();
    }

    public class AsyncPostData extends AsyncTask<Void, Void, Void> {

        private Context context;
        private CustomProgressDialog progressDialog;

        public AsyncPostData(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (adapter.getCount() > 0)
                adapter.removeListViewItems();
            progressDialog = CustomProgressDialog.show(context, "", false, null);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            URL imageUrl;
            try {
                medicines = SplashActivity.favoriteMedicineList;
                for (int i = 0; i < medicines.size(); i++) {
                    Bitmap image = null;
                    // XXX image를 bitmap으로 저장해볼까. serialized 처리 떄문에 그런데 알아보자
                    if (medicines.get(i).getId() != null) {
                        imageUrl = new URL(medicines.get(i).getSmallImageUrl());
                        image = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                    }
                    adapter.addItem(image, medicines.get(i).getName(), medicines.get(i).getIngredient());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            listView.setSelectionAfterHeaderView();
        }
    }
}

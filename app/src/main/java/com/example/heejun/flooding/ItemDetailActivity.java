package com.example.heejun.flooding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Created by HEEJUN on 2017-01-17.
 */

public class ItemDetailActivity extends AppCompatActivity {
    private SensorListItem item;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemdetail);

        //Intent intent = getIntent();
        //item = (SensorListItem) intent.getSerializableExtra("item");
        //Toast.makeText(this, " " + a, Toast.LENGTH_SHORT).show();



        // action bar 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

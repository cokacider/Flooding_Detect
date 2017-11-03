package com.example.heejun.flooding;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private long lastTimeBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         //service 시작
        if(!isServiceRunningCheck()) {
            Intent intent = new Intent(this, SensorService.class);
            startService(intent);
        }

    }

    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.heejun.flooding.SensorService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sensorList:
                startActivity(new Intent(this, SensorListActivity.class));

                break;
            case R.id.btn_option:
                startActivity(new Intent(this, OptionActivity.class));

                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed < 1500) {
            finish();
            return;
        }
        Toast.makeText(this, "'뒤로'버튼 한 번 더 눌려야 종료됩니다", Toast.LENGTH_SHORT).show();
        lastTimeBackPressed = System.currentTimeMillis();
    }
}


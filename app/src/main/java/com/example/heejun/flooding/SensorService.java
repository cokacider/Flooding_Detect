package com.example.heejun.flooding;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SensorService extends Service {
    private NotificationManager Notifi_M;
    private Notification Notifi ;
    private SensorServiceThread thread;
    private SharedPreferences sharedPref;

    private String alarmJSON;
    private JSONArray floodedSensors = null;

    private static String TAG_RESULTS;
    private static String TAG_NAME;
    private static String TAG_LOCATION;
    private static String TAG_SERIAL;
    private static String TAG_STATUS;

    public SensorService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        TAG_RESULTS = getString(R.string.TAG_RESULTS);
        TAG_NAME = getString(R.string.TAG_NAME);
        TAG_LOCATION = getString(R.string.TAG_LOCATION);
        TAG_SERIAL = getString(R.string.TAG_SERIAL);
        TAG_STATUS = getString(R.string.TAG_STATUS);

        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        ServiceHandler handler = new ServiceHandler();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String delay = sharedPref.getString("listAutoUpdateTime", "10000");
        SensorServiceThread.setThreadDelay(Long.parseLong(delay));
        thread = new SensorServiceThread(handler);
        thread.start();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        thread.stopForever();
        thread = null;
    }
    class ServiceHandler extends Handler {
        private final String serverAddress = getString(R.string.serverAddress);
        private final String alarmPhp = getString(R.string.alarmPhpFile);

        @Override
        public void handleMessage(android.os.Message msg) {
            if (sharedPref.getBoolean("useAlarm", true)) {

                alarmDataFromDB(serverAddress + alarmPhp);

                String warningNotifiText = "센서";
                String dangerNotifiText = "센서";
                boolean noDanger = true;
                boolean noWarning = true;

                if(alarmJSON != null) {
                    String temp = alarmJSON.substring(alarmJSON.indexOf("{"), alarmJSON.lastIndexOf("}") + 1);

                    try {
                        JSONObject jsonObj = null;
                        jsonObj = new JSONObject(alarmJSON.substring(alarmJSON.indexOf("{"), alarmJSON.lastIndexOf("}") + 1));

                        floodedSensors = jsonObj.getJSONArray(TAG_RESULTS);

                        for (int i = 0; i < floodedSensors.length(); i++) {
                            JSONObject c = floodedSensors.getJSONObject(i);
                            String name = c.getString(TAG_NAME);
                            String condition = c.getString(TAG_STATUS);

                            if ("2".equals(condition)) {
                                warningNotifiText += " " + name;
                                noWarning = false;
                            } else if ("3".equals(condition)) {
                                dangerNotifiText += " " + name;
                                noDanger = false;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    warningNotifiText += " 가 경고 상태 입니다 ";
                    dangerNotifiText += " 가 위험 상태 입니다 ";

                    String contentText = "";
                    if (!(noDanger && noWarning)) {
                        if (!noDanger) {
                            contentText += dangerNotifiText;
                        }
                        if (!noWarning) {
                            if (!noDanger)
                                contentText += "\n";
                            contentText += warningNotifiText;
                        }
                        Intent intent = new Intent(SensorService.this, MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(SensorService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        Notifi = new Notification.Builder(getApplicationContext())
                                .setContentTitle("침수 감지 알리미")
                                .setContentText(contentText)
                                .setSmallIcon(R.drawable.umbrella)  //TODO 로고 넣어
                                .setTicker("침수 감지 !!!")
                                .setContentIntent(pendingIntent)
                                .build();

                        if (sharedPref.getBoolean("useSound", true)) {
                            //소리추가

                            Notifi.sound = Uri.parse(sharedPref.getString("alarm_ringtone", "DEFAULT_SOUND"));

                            //알림 소리를 한번만 내도록
                            Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;
                        }

                        if (sharedPref.getBoolean("useVibrate", true)) {
                            //진동
                            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vibe.vibrate(1500);
                        }

                        //확인하면 자동으로 알림이 제거 되도록
                        Notifi.flags = Notification.FLAG_AUTO_CANCEL;


                        Notifi_M.notify(777, Notifi);
                    }
                }

            }
        }


        private void alarmDataFromDB(String url) {
            class GetDataJSON extends AsyncTask<String, Void, String> {

                @Override
                protected String doInBackground(String... params) {
                    String exception = "ERROR ";

                    String uri = params[0];
                    BufferedReader bufferedReader = null;
                    try {
                        URL url = new URL(uri);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();

                        con.setConnectTimeout(10000);
                        StringBuilder sb = new StringBuilder();

                        bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                        String json;
                        while((json = bufferedReader.readLine()) != null) {
                            sb.append(json + "\n");
                        }
                        return sb.toString().trim();

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        exception += "Exception : " + e;
                    } catch (IOException e) {
                        e.printStackTrace();
                        exception += "Exception : " + e;
                    }
                    return exception;
                }

                @Override
                protected void onPostExecute(String s) {

                    alarmJSON = s;

                }
            }

            GetDataJSON g = new GetDataJSON();
            g.execute(url);
        }
    };
}

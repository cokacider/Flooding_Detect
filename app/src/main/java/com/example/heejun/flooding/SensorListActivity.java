package com.example.heejun.flooding;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by HEEJUN on 2017-01-10.
 */

public class SensorListActivity extends AppCompatActivity {
    private ListViewAdapter adapter;
    private ListView listView;

    private String serverAddress;
    private String refreshPhpFile;

    private String refreshJSON;
    private JSONArray sensors = null;

    private static String TAG_RESULTS;
    private static String TAG_NAME;
    private static String TAG_LOCATION;
    private static String TAG_SERIAL;
    private static String TAG_STATUS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensorlist);

        TAG_RESULTS = getString(R.string.TAG_RESULTS);
        TAG_NAME = getString(R.string.TAG_NAME);
        TAG_LOCATION = getString(R.string.TAG_LOCATION);
        TAG_SERIAL = getString(R.string.TAG_SERIAL);
        TAG_STATUS = getString(R.string.TAG_STATUS);

        serverAddress = getString(R.string.serverAddress);
        refreshPhpFile = getString(R.string.refreshPhpFile);

        //adapter
        adapter = new ListViewAdapter();

        listView = (ListView) findViewById(R.id.sensorListView);
        listView.setAdapter(adapter);
        //리스트 새로고침
        refresh(serverAddress + refreshPhpFile);

        //handler
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 항목 눌렸을 때
                //TODO delete
                SensorListItem item = (SensorListItem) parent.getItemAtPosition(position);

            }

        });

        //context menu
        registerForContextMenu(listView);

        // action bar 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showList() {

        try {
            JSONObject jsonObj = null;
            jsonObj = new JSONObject(refreshJSON.substring(refreshJSON.indexOf("{"), refreshJSON.lastIndexOf("}") + 1));

            sensors = jsonObj.getJSONArray(TAG_RESULTS);

            adapter.clearItem();

            for (int i = 0; i < sensors.length(); i++) {
                JSONObject c = sensors.getJSONObject(i);
                String name = c.getString(TAG_NAME);
                String location = c.getString(TAG_LOCATION);
                String serialNumber = c.getString(TAG_SERIAL);
                String condition = c.getString(TAG_STATUS);


                adapter.addItem(name, location, serialNumber, condition);

            }

            listView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void refresh(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String exception = "ERROR";

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

                refreshJSON = s;
                showList();

            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, 1, 100, "제거");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        SensorListItem sensorItem = (SensorListItem) adapter.getItem(index);

        switch (item.getItemId()) {
            case 1: //제거
                deleteSensor(sensorItem);
                adapter.deleteItem(index);
                refresh(serverAddress + refreshPhpFile);
                return true;
        }

        return super.onContextItemSelected(item);
    }

    private void deleteSensor(SensorListItem sensorItem) {
        String serialNumber = sensorItem.getSerialNumber();
        sendDeletionToDB(serialNumber);

    }

    private void sendDeletionToDB(final String serialNumber) {

        class sendData extends AsyncTask<String, Void, String> {
            private String serverAddress = getString(R.string.serverAddress);
            private String deletePhpFile = getString(R.string.deletePhpFile);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {
                String exception = "";
                try {
                    String serialNumber = (String)params[0];
                    String data = URLEncoder.encode("serialNumber", "UTF-8") + "=" + URLEncoder.encode(serialNumber, "UTF-8");

                    URL url = new URL(serverAddress + deletePhpFile);
                    URLConnection conn = url.openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    exception += new String("Exception: " + e.getMessage() + "\n");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    exception += new String("Exception: " + e.getMessage() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                    exception += new String("Exception: " + e.getMessage() + "\n");
                }
                return exception;
            }
        }
        sendData task = new sendData();
        task.execute(serialNumber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sensorlist_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.listAction_register:  // 등록
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);

            case R.id.listAction_update:    // 새로고침
                refresh(serverAddress + refreshPhpFile);

                return true;

            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

package com.example.heejun.flooding;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by HEEJUN on 2017-01-18.
 */

public class RegisterActivity extends AppCompatActivity {
    EditText editTextName;
    EditText editTextLocation;
    EditText editTextSerialNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = (EditText) findViewById(R.id.sensorName);
        editTextLocation = (EditText) findViewById(R.id.sensorLocation);
        editTextSerialNumber = (EditText) findViewById(R.id.sensorNumber);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                String name = editTextName.getText().toString();
                String location = editTextLocation.getText().toString();
                String serialNumber = editTextSerialNumber.getText().toString();

                registerToDB(name, location, serialNumber);

                finish();
                break;
            case R.id.btn_reg_cancel:
                finish();
                break;
        }
    }

    private void registerToDB(String name, String location, String serialNumber) {

        class InsertData extends AsyncTask<String, Void, String> {
            private String serverAddress = getString(R.string.serverAddress);
            private String registerPhpFile = getString(R.string.registerPhpFile);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                String exception = "";
                try {
                    String name = (String)params[0];
                    String location = (String)params[1];
                    String serialNumber = (String)params[2];


                    String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")
                            + "&" + URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8")
                            + "&" + URLEncoder.encode("serialNumber", "UTF-8") + "=" + URLEncoder.encode(serialNumber, "UTF-8");


                    URL url = new URL(serverAddress + registerPhpFile);
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

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

        }
        InsertData task = new InsertData();
        task.execute(name, location, serialNumber);
    }
}

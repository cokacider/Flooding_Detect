package com.example.heejun.flooding;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by HEEJUN on 2017-01-10.
 */

public class OptionActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new OptionFragment()).commit();
    }
}

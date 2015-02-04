package com.example.leon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.leon.cpuusage.R;
import com.example.leon.util.LogUtil;

import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    public static final int UPDATE_TEXT = 0;

    public static View sFragmentRoot = null;
    public static Handler sHandler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what) {
                case UPDATE_TEXT:
                    Map<String, Map<String, String>> topOfOneMin = LongRunningService.sTopOfOneMin;
                    String first = topOfOneMin.get("1").get("cpu") + "% || " + topOfOneMin.get("1").get("name");
                    String second = topOfOneMin.get("2").get("cpu") + "% || " + topOfOneMin.get("2").get("name");
                    String third = topOfOneMin.get("3").get("cpu") + "% || " + topOfOneMin.get("3").get("name");

                    ((TextView) sFragmentRoot.findViewById(R.id.first_process_one_min)).setText(first);
                    ((TextView) sFragmentRoot.findViewById(R.id.second_process_one_min)).setText(second);
                    ((TextView) sFragmentRoot.findViewById(R.id.third_process_one_min)).setText(third);

                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PlaceholderFragment fragment = new PlaceholderFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }


    }

    @Override
    protected void onResume(){
        super.onResume();

        LogUtil.d("in onResume");
        Intent intent = new Intent(this, LongRunningService.class);
        startService(intent);
        LogUtil.d("quit onResume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            sFragmentRoot = inflater.inflate(R.layout.fragment_main, container, false);
            return sFragmentRoot;
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {

        }
    }
}

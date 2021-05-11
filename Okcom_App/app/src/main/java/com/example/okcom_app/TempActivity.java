package com.example.okcom_app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.EventLogTags;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.okcom_app.ApiService.retrofit;

public class TempActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private final String BASE_URL = "http://192.168.1.166/";
    private ApiService mMyAPI;
    private List<Sensor> mList = null;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initMyAPI(BASE_URL);

        final LineChart lineChart = (LineChart) findViewById(R.id.line_chart);
        final ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<Result> sensors = new ArrayList<>();

        final ArrayList<String> labels = new ArrayList<>();

        LineDataSet dataset = new LineDataSet(entries, "# of Calls");

        LineData data = new LineData(labels, dataset);

        lineChart.setData(data);
        lineChart.animateY(5000);

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                mMyAPI = retrofit.create(ApiService.class);
                ApiService apiService = retrofit.create(ApiService.class);
                Call<Result> call = apiService.getSensorData("temp");

                try {
                    mList = call.execute().body().getData();
                    return mList.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Call<Result> getCall = mMyAPI.getSensorData("temp");

                String id = "";
                Integer value = null;
                String created_at = "";
                String updated_at = "";

                int i = 0;
                for (Sensor sensor : mList) {

                    id = sensor.getId();
                    value = sensor.getValue();
                    //type = sensor.getType();
                    created_at = sensor.getCreatedAt();
                    updated_at = sensor.getUpdatedAt();

                    entries.add(new Entry(value, i));
                    labels.add(created_at);

                    i++;
                }
                LineDataSet dataset = new LineDataSet(entries, "온도");
                dataset.setDrawCircles(false);
                dataset.setColor(R.color.purple);
                LineData data = new LineData(labels, dataset);
                lineChart.setData(data);

            }
        }.execute();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void initMyAPI(String baseUrl){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .callTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        mMyAPI = retrofit.create(ApiService.class);

    }


}


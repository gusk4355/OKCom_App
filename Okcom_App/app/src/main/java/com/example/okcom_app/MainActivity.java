package com.example.okcom_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView current;
    private Button refresh_btn;
    private TextView temp_txt;
    private Button temp_btn;
    private TextView humi_txt;
    private Button humi_btn;
    private TextView udust_txt;
    private Button udust_btn;
    private TextView dust_txt;
    private Button dust_btn;
    private Button control;
    private Button view_btn;
    private TextView test;

//         현재시간
    long mNow = System.currentTimeMillis();
    Date mDate = new Date(mNow);
    // 2020-08-12 12:00:00 포멧
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String formatDate = mFormat.format(mDate);

    // BASE_URL 설정
    private final String BASE_URL = "http://192.168.1.166/";
    private ApiService mMyAPI;
    private final String TAG = getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        current = (TextView) findViewById(R.id.currentTime);
        current.setText(formatDate);

        temp_txt = (TextView) findViewById(R.id.temp_txt);
        humi_txt = (TextView) findViewById(R.id.humi_txt);
        dust_txt = (TextView) findViewById(R.id.dust_txt);
        udust_txt = (TextView) findViewById(R.id.udust_txt);
//        test = (TextView) findViewById(R.id.test);

        refresh_btn = (Button) findViewById(R.id.refresh_btn);
        refresh_btn.setOnClickListener(this);

        String url = "http://192.168.1.166/api/app";

        initMyAPI(BASE_URL);



    }

    // Retrofit 시작 설정
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


    // 새로고침 이벤트 설정
    @Override
    public void onClick(final View v) {
        if (v == refresh_btn) {
            Log.d(TAG, "Get");
            current.setText(getTime());

            Call<Result> getCall = mMyAPI.getData();
            getCall.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    if (response.isSuccessful()) {

                        Result rs = response.body();
                        List<Sensor> mList = rs.getData();
                        String id = "";
                        String value = "";
                        String type = "";
                        String created_at = null;
                        String updated_at = null;

                        // Sensor.java 의 DTO 를 mList 에 for 문을 통해 집어넣는다.
                        for (Sensor sensor : mList) {
                            id = sensor.getId();
                            value = String.valueOf(sensor.getValue());
                            type = sensor.getType();
                            created_at = sensor.getCreatedAt();
                            updated_at = sensor.getUpdatedAt();

                            // type 을 기준으로 가져온 데이터 value 와 맞춰놓은 기호를 함께 Textview 에 설정한다.
                            switch (type) {
                                case "temp":
                                    temp_txt.setText(value + "°C");
                                    break;
                                case "humi":
                                    humi_txt.setText(value + "% rh");
                                    break;
                                case "dust":
                                    dust_txt.setText(value + "μm");
                                    break;
                                case "udust":
                                    udust_txt.setText(value + "㎍");
                                    break;
                                default:
                                    break;
                            }
                        }
                    } else {
                        Log.d(TAG, "Status Code : " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    Log.d(TAG, "Fail : " + t.getMessage());
                }

            });
        }

    }


                //    버튼 이벤트
    public void EventControlBtn(View view) {
        Intent intent = new Intent(getApplicationContext(), ControlActivity.class);
        startActivity(intent);
    }

    public void EventTempBtn(View view) {
        Intent intent = new Intent(getApplicationContext(), TempActivity.class);
        startActivity(intent);
    }

    public void EventHumiBtn(View view) {
        Intent intent = new Intent(getApplicationContext(), HumiActivity.class);
        startActivity(intent);
    }

    public void EventUdustBtn(View view) {
        Intent intent = new Intent(getApplicationContext(), UdustActivity.class);
        startActivity(intent);
    }

    public void EventDustBtn(View view) {
        Intent intent = new Intent(getApplicationContext(), DustActivity.class);
        startActivity(intent);
    }



    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }
}
package com.example.okcom_app;

        import java.util.List;

        import retrofit2.Call;
        import retrofit2.Retrofit;
        import retrofit2.converter.gson.GsonConverterFactory;
        import retrofit2.http.GET;
        import retrofit2.http.Path;

public interface ApiService {

    @GET("/api/app")
    Call<Result> getData();

    @GET("/api/app/sensor/{type}")
    Call<Result> getSensorData(@Path("type") String type);

//    @GET("/api/app")
//    Call<List<Sensor>> getHumiData(@Query("type") String humi);
//
//    @GET("/api/app")
//    Call<List<Sensor>> getDustData(@Query("/data/{dust}") String dust);
//
//    @GET("/api/app")
//    Call<List<Sensor>> getUdustData(@Query("/data/{udust}") String udust);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.1.166/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}



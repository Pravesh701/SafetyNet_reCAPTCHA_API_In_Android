package com.appinventiv.safetynetrecaptchaapi;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecaptchaRepository {

    public LiveData<RecaptchaVerifyResponsePOJO> doRecaptchaValidation(@NonNull String baseUrl, @NonNull String response, @NonNull String key) {
        final MutableLiveData<RecaptchaVerifyResponsePOJO> data = new MutableLiveData<>();
        Map<String, String> params = new HashMap<>();
        params.put("response", response);
        params.put("secret", key);
        getRecaptchaValidationService(baseUrl).verifyResponse(params).enqueue(new Callback<RecaptchaVerifyResponsePOJO>() {
            @Override
            public void onResponse(Call<RecaptchaVerifyResponsePOJO> call, Response<RecaptchaVerifyResponsePOJO> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<RecaptchaVerifyResponsePOJO> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    private RecaptchaVerificationAPI getRecaptchaValidationService(@NonNull String baseUrl) {
        return getRetrofit(baseUrl).create(RecaptchaVerificationAPI.class);
    }

    private Retrofit getRetrofit(@NonNull String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder().addInterceptor(getLoggingInterceptor()).build();
    }

    private HttpLoggingInterceptor getLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }
}

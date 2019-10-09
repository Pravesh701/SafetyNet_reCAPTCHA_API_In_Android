package com.appinventiv.safetynetrecaptchaapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    private final String BASE_URL = "https://www.google.com";
    private final String SECRET_KEY = "6LdFn7wUAAAAAJ5T5JpKlLvFBtT30V280exsW4mc";
    private final String SITE_KEY = "6LdFn7wUAAAAAL4OKTfnkltAK3Ake543i3WZh4m5";
    private final String TAG = "MainActivity";
    private CheckBox cbValidate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cbValidate = findViewById(R.id.cb_validate);

        cbValidate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SafetyNet.getClient(MainActivity.this).verifyWithRecaptcha(SITE_KEY)
                            .addOnSuccessListener(
                                    new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                                        @Override
                                        public void onSuccess(SafetyNetApi.RecaptchaTokenResponse recaptchaTokenResponse) {
                                            String userResponseToken = recaptchaTokenResponse.getTokenResult();
                                            if (!userResponseToken.isEmpty()) {
                                                verifyCaptchaThroughServer(userResponseToken);
                                            }
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            if (e instanceof ApiException) {
                                                ApiException apiException = (ApiException) e;
                                                int statusCode = apiException.getStatusCode();
                                                Log.d(TAG, "Error: " + CommonStatusCodes
                                                        .getStatusCodeString(statusCode));
                                                cbValidate.setChecked(false);
                                            } else {
                                                Log.d(TAG, "Error: " + e.getMessage());
                                                cbValidate.setChecked(false);
                                            }
                                        }
                                    });
                }
            }
        });

    }

    private void verifyCaptchaThroughServer(String userResponseToken){

        RecaptchaResponseViewModel mViewModel = ViewModelProviders.of(MainActivity.this).get(RecaptchaResponseViewModel.class);

        mViewModel.getmRecaptchaObservable(BASE_URL, userResponseToken, SECRET_KEY).observe(MainActivity.this, new Observer<RecaptchaVerifyResponsePOJO>() {
            @Override
            public void onChanged(@Nullable RecaptchaVerifyResponsePOJO recaptchaVerifyResponsePOJO) {
                if (recaptchaVerifyResponsePOJO != null && recaptchaVerifyResponsePOJO.isSuccess()) {
                    showAlertWithButton("Pravesh is a human", "Welcome", "Press for next!");
                } else {
                    showAlertWithButton("Obie ain't a human", "You are a ROBOT", "Doggone it!");
                }
            }
        });
    }

    private void showAlertWithButton(@NonNull String title, @NonNull String message, @NonNull String buttonMessage) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton(buttonMessage, null).create().show();
    }
}

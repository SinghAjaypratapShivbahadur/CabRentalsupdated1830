package com.example.cabrentals;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class splashscreen extends AppCompatActivity {

  private final static int LOGIN_REQUEST_CODE=283;

    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;

    private void delaysplash(){
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(splashscreen.this, splashscreen.class);
            firebaseAuth.addAuthStateListener(listener);
            startActivity(intent);
            finish();
        }, 5000);
    }

    @Override
    protected void onStart() {
        super.onStart();
       delaysplash();
    }

    @Override
    protected void onStop() {
        if (firebaseAuth!=null&&listener!=null)
        {
            firebaseAuth.removeAuthStateListener(listener);
        }
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        delaysplash();

        init();
    }
    private void init() {
        providers= Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

         firebaseAuth = FirebaseAuth.getInstance();
        listener= myFirebaseAuth -> {
          FirebaseUser user= myFirebaseAuth.getCurrentUser();
          if(user!=null) {
              Toast.makeText(this, "Welcome!"+user.getUid(), Toast.LENGTH_SHORT).show();

          }
            else
                showLoginLayout();
        };
    }

    private void showLoginLayout() {
        AuthMethodPickerLayout authMethodPickerLayout = new AuthMethodPickerLayout
                .Builder(R.layout.sign_in)
                .setPhoneButtonId(R.id.signphone)
                .setPhoneButtonId(R.id.signgoogle)
                .build();

        startActivityForResult(AuthUI.getInstance()
        .createSignInIntentBuilder()
                .setAuthMethodPickerLayout(authMethodPickerLayout)
                .setIsSmartLockEnabled(false)
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .build(),LOGIN_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==LOGIN_REQUEST_CODE)
        {
            IdpResponse response=IdpResponse.fromResultIntent(data);
            if (resultCode==RESULT_OK)
            {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            }
            else
            {

                assert response != null;
                Toast.makeText(this, "[ERROR]:"+ Objects.requireNonNull(response.getError()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
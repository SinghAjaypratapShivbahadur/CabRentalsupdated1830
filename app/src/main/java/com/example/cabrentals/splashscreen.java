package com.example.cabrentals;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cabrentals.Model.DriverInfoModel;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class splashscreen extends AppCompatActivity {
    TextInputEditText edt_first_name,edt_last_name,edt_mobnumb,edt_emailid;

    Button btn_register;

  private final static int LOGIN_REQUEST_CODE=283;

    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;

    @BindView(R.id.progress_bar)
    ProgressBar progress_bar;

    FirebaseDatabase database;
    DatabaseReference driverInfoRef;

    private void delaysplash(){
        progress_bar.setVisibility(View.VISIBLE);


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
        setContentView(R.layout.sign_in);
        //delaysplash();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(splashscreen.this, splashscreen.class);
            firebaseAuth.addAuthStateListener(listener);
            startActivity(intent);
            finish();
        }, 5000);
        setContentView(R.layout.activity_splashscreen);

        init();
    }
    private void init() {
        ButterKnife.bind(this);

        database= FirebaseDatabase.getInstance();
        driverInfoRef= database.getReference(Common.DRIVER_INFO_REFERENCE);


        providers= Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

         firebaseAuth = FirebaseAuth.getInstance();
        listener= myFirebaseAuth -> {
          FirebaseUser user= myFirebaseAuth.getCurrentUser();
          if(user!=null) {
              //Toast.makeText(this, "Welcome!"+user.getUid(), Toast.LENGTH_SHORT).show();
              checkUserFromFirebase();

          }
            else
                showLoginLayout();
        };
    }

    private void checkUserFromFirebase() {
        driverInfoRef.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    //Toast.makeText(splashscreen.this, "User Already Registered!", Toast.LENGTH_SHORT).show();
                    DriverInfoModel driverInfoModel= dataSnapshot.getValue(DriverInfoModel.class);
                    goToHomeActivity(driverInfoModel);
                }
                else
                {
                    showRegisterLayout();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(splashscreen.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void goToHomeActivity(DriverInfoModel driverInfoModel) {
        Common.currentUser= driverInfoModel;  //INITIAL or INIT Value
        startActivity(new Intent(splashscreen.this,DriverHomeActivity.class));
        finish();
    }

    private void showRegisterLayout() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this, R.style.DialogTheme);
        View itemView= LayoutInflater.from(this).inflate(R.layout.layout_register, null);

         edt_first_name= itemView.findViewById(R.id.edt_first_name);
         edt_last_name= itemView.findViewById(R.id.edt_last_name);
         edt_mobnumb= itemView.findViewById(R.id.edt_mobnumb);
         edt_emailid= itemView.findViewById(R.id.edt_emailid);

         btn_register= itemView.findViewById(R.id.btn_register);

         //Set Data
        if (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber()!=null
                && TextUtils.isEmpty(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))
            edt_mobnumb.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
       if (FirebaseAuth.getInstance().getCurrentUser().getEmail()!=null
              && TextUtils.isEmpty(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
        edt_emailid.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

         //Set View
        builder.setView(itemView);
        AlertDialog dialog= builder.create();
        dialog.show();

        btn_register.setOnClickListener(view -> {
            if (TextUtils.isEmpty(Objects.requireNonNull(edt_first_name.getText()).toString()))
            {
                Toast.makeText(this, "Please Enter Your First Name", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(Objects.requireNonNull(edt_last_name.getText()).toString()))
            {
                Toast.makeText(this, "Please Enter Your Last Name", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(Objects.requireNonNull(edt_mobnumb.getText()).toString()))
            {
                Toast.makeText(this, "Please Enter Your Mobile Number", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(Objects.requireNonNull(edt_emailid.getText()).toString()))
            {
                Toast.makeText(this, "Please Enter Your Email Id", Toast.LENGTH_SHORT).show();
            }
            else
            {
                DriverInfoModel model= new DriverInfoModel();
                model.setFirstname(edt_first_name.getText().toString());
                model.setLastname(edt_last_name.getText().toString());
                model.setMobile_Number(edt_mobnumb.getText().toString());
                model.setEmail_Id(edt_emailid.getText().toString());
                model.setRating(0.0);

                driverInfoRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(model)
                        .addOnFailureListener(e ->
                        {
                            dialog.dismiss();
                            Toast.makeText(splashscreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        )
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            goToHomeActivity(model);
                        });
            }

        });
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
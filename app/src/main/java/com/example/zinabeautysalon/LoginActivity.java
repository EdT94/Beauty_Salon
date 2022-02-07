package com.example.zinabeautysalon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.view.View;

import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.zinabeautysalon.Fragments.LogoFragment;
import com.example.zinabeautysalon.Models.Appointment;
import com.example.zinabeautysalon.Models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.fragment.app.Fragment;


public class LoginActivity extends AppCompatActivity {
    private MaterialButton login_BTN_google;
    private MaterialButton login_BTN_register;
    private MaterialButton login_BTN_login;
    private MaterialTextView login_TXT_validation;
    private TextInputEditText login_EDT_emailInput;
    private TextInputEditText login_EDT_passwordInput;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference appointmentsRef;
    private DatabaseReference managerRef;
    private DatabaseReference usersRef;
    private Intent intent = new Intent();
    private Handler handler = new Handler();
    private FrameLayout login_FRM_logo;
    private Fragment logoFragment = new LogoFragment();
    private static final String TAG = "LoginActivity";
    private int RC_SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        hideSystemUI();
        firebaseAuth = FirebaseAuth.getInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.login_FRM_logo, logoFragment).commit();
        buildClient();
        setClickListenersOnButtons();
    }


    private void setClickListenersOnButtons() {
        login_BTN_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInByGoogle();
            }
        });

        login_BTN_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        login_BTN_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login_EDT_emailInput.getText().toString().equals("") || login_EDT_passwordInput.getText().toString().equals("")) {
                    login_TXT_validation.setText("מלא את כל פרטי ההזדהות");
                    login_TXT_validation.setVisibility(View.VISIBLE);
                    login_TXT_validation.startAnimation(shakeError());
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            login_TXT_validation.setVisibility(View.INVISIBLE);
                        }
                    }, 800);
                } else {
                    signIn();
                }

            }

        });
    }

    private void signIn() {
        maintainManagerRef();
        maintainUserRef();
        maintainAppoinmentsRef();
    }

    private void maintainAppoinmentsRef() {
        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Appointment appointment = child.getValue(Appointment.class);
                    if (appointment.getAccountEmail().equals(login_EDT_emailInput.getText().toString())) {
                        login_TXT_validation.setText("התחבר באמצעות גוגל");
                        login_TXT_validation.setVisibility(View.VISIBLE);
                        login_TXT_validation.startAnimation(shakeError());
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                login_TXT_validation.setVisibility(View.INVISIBLE);
                            }
                        }, 800);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void maintainUserRef() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean userFound = false;
                for (DataSnapshot child : snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (user.getEmail().equals(login_EDT_emailInput.getText().toString())) {
                        userFound = true;
                        if (user.getPassword().equals(login_EDT_passwordInput.getText().toString())) {
                            intent.setClass(LoginActivity.this, MainActivity.class);
                            intent.putExtra("ManagerAccount", "notManager");
                            intent.putExtra("userEmail", user.getEmail());
                            intent.putExtra("userPassword", user.getPassword());
                            intent.putExtra("userName", user.getName());
                            startActivity(intent);
                            finish();
                        } else {
                            login_TXT_validation.setText("בדוק את פרטי ההתחברות");
                            login_TXT_validation.setVisibility(View.VISIBLE);
                            login_TXT_validation.startAnimation(shakeError());
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    login_TXT_validation.setVisibility(View.INVISIBLE);
                                }
                            }, 800);
                        }
                    }

                }
                if (!userFound) {
                    login_TXT_validation.setText("משתמש לא קיים");
                    login_TXT_validation.setVisibility(View.VISIBLE);
                    login_TXT_validation.startAnimation(shakeError());
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            login_TXT_validation.setVisibility(View.INVISIBLE);
                        }
                    }, 800);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void maintainManagerRef() {
        managerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String managerEmail = snapshot.child("ManagerEmail").getValue(String.class);
                String managerPassword = snapshot.child("ManagerPassword").getValue(String.class);
                if (managerEmail.equals(login_EDT_emailInput.getText().toString()) && managerPassword.equals(login_EDT_passwordInput.getText().toString())) {
                    //manager connected
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    intent.putExtra("ManagerAccount", "manager");
                    intent.putExtra("GoogleSignInAccount", (GoogleSignInAccount) null);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void buildClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        logOutUser();
    }

    private void logOutUser() {
        if (googleSignInClient != null) {
            buildClient();
            googleSignInClient.signOut();
        }
    }

    private void findViews() {
        login_BTN_google = findViewById(R.id.login_BTN_google);
        login_BTN_register = findViewById(R.id.login_BTN_register);
        login_BTN_login = findViewById(R.id.login_BTN_login);
        login_EDT_emailInput = findViewById(R.id.login_EDT_emailInput);
        login_EDT_passwordInput = findViewById(R.id.login_EDT_passwordInput);
        login_TXT_validation = findViewById(R.id.login_TXT_validation);
        login_TXT_validation.setVisibility(View.INVISIBLE);
        login_FRM_logo = findViewById(R.id.login_FRM_logo);
        firebaseDatabase = FirebaseDatabase.getInstance();
        appointmentsRef = firebaseDatabase.getReference("Appointments");
        managerRef = firebaseDatabase.getReference("Manager");
        usersRef = firebaseDatabase.getReference("Users");
    }

    private void signInByGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount acc = task.getResult(ApiException.class);
            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            Toast.makeText(LoginActivity.this, "ההתחברות נכשלה", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    intent.putExtra("ManagerAccount", "notManager");
                    intent.putExtra("GoogleSignInAccount", account);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "ההתחברות נכשלה", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void hideSystemUI() {
        getWindow().setStatusBarColor(getResources().getColor(R.color.myPurple));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.myPink));

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY       // Set the content to appear under the system bars so that the
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    public TranslateAnimation shakeError() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(7));
        return shake;
    }


}
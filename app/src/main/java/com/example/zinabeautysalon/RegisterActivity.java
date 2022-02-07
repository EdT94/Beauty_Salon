package com.example.zinabeautysalon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.zinabeautysalon.Fragments.LogoFragment;
import com.example.zinabeautysalon.Models.Appointment;
import com.example.zinabeautysalon.Models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    private FrameLayout register_FRM_logo;
    private EditText register_EDT_nameInput;
    private EditText register_EDT_passwordInput;
    private EditText register_EDT_emailInput;
    private MaterialButton register_BTN_register;
    private MaterialTextView register_TXT_validation;
    private Handler handler = new Handler();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersRef;
    private DatabaseReference appointmentsRef;
    private boolean userExistsInAppointments = false;
    private boolean userExistsInUsers = false;
    private boolean searchingInAppointmentsCompleted = false;
    private boolean searchingInUsersCompleted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        hideSystemUI();
        findViews();
        getSupportFragmentManager().beginTransaction().add(R.id.register_FRM_logo, new LogoFragment()).commit();
        register_BTN_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!allInputsExists())
                    showAlert("מלא את כל פרטי המשתמש");
                else {
                    findUserInFirebase();
                }
            }
        });
    }

    private void findUserInFirebase() {
        usersRef = firebaseDatabase.getReference("Users");
        appointmentsRef = firebaseDatabase.getReference("Appointments");
        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        Appointment appointment = child.getValue(Appointment.class);
                        String userEmail = register_EDT_emailInput.getText().toString();
                        String fireBaseEmail = appointment.getAccountEmail().toString();
                        if (userEmail.equals(fireBaseEmail)) {
                            userExistsInAppointments = true;
                            break;
                        }

                    } catch (Exception e) {
                    }
                }
                searchingInAppointmentsCompleted = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        User user = child.getValue(User.class);
                        String userEmail = register_EDT_emailInput.getText().toString();
                        String fireBaseEmail = user.getEmail().toString();
                        if (userEmail.equals(fireBaseEmail)) {
                            userExistsInUsers = true;
                            break;
                        }
                    } catch (Exception e) {
                    }
                }

                searchingInUsersCompleted = true;
                if (searchingInAppointmentsCompleted && searchingInUsersCompleted) {
                    if (userExistsInAppointments || userExistsInUsers) {
                        showAlert("משתמש קיים במערכת");
                        userExistsInAppointments = false;
                        userExistsInUsers = false;
                        searchingInAppointmentsCompleted = false;
                        searchingInUsersCompleted = false;
                        return;
                    } else
                        registerAccountToFirebase();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void registerAccountToFirebase() {
        User user = new User(register_EDT_emailInput.getText().toString(), register_EDT_passwordInput.getText().toString(), register_EDT_nameInput.getText().toString());
        usersRef.push().setValue(user);
        Toast.makeText(this, "משתמש נוסף בהצלחה", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private boolean allInputsExists() {
        if (register_EDT_nameInput.getText().toString().equals("") ||
                register_EDT_emailInput.getText().toString().equals("") ||
                register_EDT_passwordInput.getText().toString().equals(""))
            return false;
        return true;
    }

    private void findViews() {
        register_FRM_logo = findViewById(R.id.register_FRM_logo);
        register_BTN_register = findViewById(R.id.register_BTN_register);
        register_EDT_nameInput = findViewById(R.id.register_EDT_nameInput);
        register_EDT_passwordInput = findViewById(R.id.register_EDT_passwordInput);
        register_EDT_emailInput = findViewById(R.id.register_EDT_emailInput);
        register_TXT_validation = findViewById(R.id.register_TXT_validation);
        firebaseDatabase = FirebaseDatabase.getInstance();
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

    private void showAlert(String message) {
        register_TXT_validation.setText(message);
        register_TXT_validation.setVisibility(View.VISIBLE);
        register_TXT_validation.startAnimation(shakeError());
        handler.postDelayed(new Runnable() {
            public void run() {
                register_TXT_validation.setVisibility(View.INVISIBLE);
            }
        }, 800);
    }
}
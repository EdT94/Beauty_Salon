package com.example.zinabeautysalon;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.zinabeautysalon.Fragments.BookAppointmentFragment;
import com.example.zinabeautysalon.Fragments.ContactFragment;
import com.example.zinabeautysalon.Fragments.MainFragment;
import com.example.zinabeautysalon.Fragments.ManagerFragment;
import com.example.zinabeautysalon.Models.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;

import com.google.android.material.textview.MaterialTextView;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MainFragment.OnFragmentInteractionListener, ManagerFragment.OnFragmentInteractionListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private static MaterialTextView greeting;
    private static boolean managerConnected = false;
    private static GoogleSignInAccount googleSignInAccount;
    private static User user = new User();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideSystemUI();
        findViews();
        getGoogleUser();
        maintainNavigationView();
        manageFragmentTransactions();
    }

    private void maintainNavigationView() {
        if (managerConnected) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_manager_menu);
        }
    }

    private void manageFragmentTransactions() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        manageDrawerLayout();
        if (managerConnected)
            fragmentTransaction.add(R.id.container_fragment, new ManagerFragment());
        else
            fragmentTransaction.add(R.id.container_fragment, new MainFragment());

        navigationView.setNavigationItemSelectedListener(this);

        fragmentTransaction.commit();
    }

    private void manageDrawerLayout() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        increaceToolbarSize();
    }

    private void increaceToolbarSize() {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            if (toolbar.getChildAt(i) instanceof ImageButton) {
                toolbar.getChildAt(i).setScaleX(1.5f);
                toolbar.getChildAt(i).setScaleY(1.5f);
            }
        }
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
    }

    private void findViews() {
        drawerLayout = findViewById(R.id.main_DRW_drawer);
        toolbar = findViewById(R.id.drawerHeaderToolbar);
        setSupportActionBar(toolbar);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        navigationView = findViewById(R.id.main_NV_navigationView);
        View mainFragmentView = inflater.inflate(R.layout.main_fragment, null);
        greeting = (MaterialTextView) mainFragmentView.findViewById(R.id.mainFragment_TXT_greeting);
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

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if (item.getItemId() == R.id.nav_home) {
            fragmentTransaction.replace(R.id.container_fragment, new MainFragment());
            fragmentTransaction.commit();
        } else if (item.getItemId() == R.id.nav_bookAppointment) {
            fragmentTransaction.replace(R.id.container_fragment, new BookAppointmentFragment());
            fragmentTransaction.commit();
        } else if (item.getItemId() == R.id.nav_contact) {
            fragmentTransaction.replace(R.id.container_fragment, new ContactFragment());
            fragmentTransaction.commit();
        } else if (item.getItemId() == R.id.nav_logout) {
            managerConnected = false;
            googleSignInAccount = null;
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            finish();
            startActivity(intent);
        }

        return true;
    }


    public static String greetingToUser() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        Date date = cal.getTime();
        int hour = date.getHours();
        if (hour >= 5 && hour < 12) {
            if (googleSignInAccount != null)
                greeting.setText(googleSignInAccount.getGivenName() + " בוקר טוב ");
            else if (managerConnected)
                greeting.setText("בוקר טוב מנהל");
            else
                greeting.setText(user.getName() + " בוקר טוב ");

        } else if (hour >= 12 && hour < 17) {
            if (googleSignInAccount != null)
                greeting.setText(googleSignInAccount.getGivenName() + " צהריים טובים ");
            else if (managerConnected)
                greeting.setText("צהריים טובים מנהל");
            else
                greeting.setText(user.getName() + " צהריים טובים ");

        } else {
            if (googleSignInAccount != null)
                greeting.setText(googleSignInAccount.getGivenName() + " ערב טוב ");
            else if (managerConnected)
                greeting.setText("ערב טוב מנהל");
            else
                greeting.setText(user.getName() + " ערב טוב ");

        }

        return greeting.getText().toString();

    }

    public static void setGreeting(String str) {
        greeting.setText(str);
    }


    public GoogleSignInAccount getGoogleUser() {

        if (getIntent().getStringExtra("ManagerAccount").equals("manager")) {
            managerConnected = true;
            return null;
        } else {
            if (getIntent().getParcelableExtra("GoogleSignInAccount") != null) {
                googleSignInAccount = getIntent().getParcelableExtra("GoogleSignInAccount");
                return googleSignInAccount;
            } else {
                user.setEmail(getIntent().getStringExtra("userEmail"));
                user.setName(getIntent().getStringExtra("userName"));
                user.setPassword(getIntent().getStringExtra("userPassword"));
                return null;
            }


        }
    }

    public User getUser() {
        return user;
    }


    @Override
    public void messageFromParentFragment(Uri uri) {
    }


    public static boolean getIsManagerConnected() {
        return managerConnected;
    }


}
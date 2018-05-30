package com.nymeria.admin.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.nymeria.admin.R;
import com.nymeria.admin.Utiliti.NavHandlerListener;
import com.nymeria.admin.fragment.ContactFragment;
import com.nymeria.admin.fragment.FaqFragment;
import com.nymeria.admin.fragment.HomeFragment;
import com.nymeria.admin.fragment.ProfileFragment;
import com.nymeria.admin.helper.SQLiteHandler;
import com.nymeria.admin.helper.SessionManager;

import static com.nymeria.admin.Utiliti.ColorUtil.getNavIconColorState;
import static com.nymeria.admin.Utiliti.ColorUtil.getNavTextColorState;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        NavHandlerListener{

    private NavigationView navigationView = null;
    private NavigationMenuView navigationMenuView = null;
    private DrawerLayout drawer = null;
    private View headerView;
    private RelativeLayout navHeaderImgContainer;
    public NavHandlerListener navHandlerListener = null;
    private boolean isDoubleBackToExit = false;

    private SQLiteHandler db;
    private SessionManager session;

    String name ;
    String comp ;
    String email ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

         name = db.getUserDetails ().get ( "name" );
         comp = db.getUserDetails ().get ( "comp" );
        email = db.getUserDetails ().get ( "email" );


        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
        View header = navigationView.getHeaderView(0);
        TextView name_us = (TextView) header.findViewById(R.id.nameTxt);
        TextView com = (TextView) header.findViewById(R.id.compTxt);
        TextView email_ = (TextView) header.findViewById(R.id.emailTxt);

        name_us.setText ( name );
        com.setText ( comp );
        email_.setText ( email );

        navigationView.setNavigationItemSelectedListener(this);
        if (navigationMenuView != null) {
            navigationMenuView.setVerticalScrollBarEnabled(false);
        }
        navigationView.setItemTextColor(getNavTextColorState());
        navigationView.setItemIconTintList(getNavIconColorState());

        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_home));
        navigationView.setCheckedItem(R.id.nav_home);



    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (navigationView.getMenu().findItem(itemId).isChecked()) {
            return true;
        }

        if (itemId == R.id.nav_home) {
            showHomeScreen ();

        }else

        if (itemId == R.id.nav_profile) {

            showProfile ();


        } else if (itemId == R.id.nav_log_out) {
            logoutUser();


        } else if (itemId == R.id.nav_faq) {

            showFaqScreen ();



        } else if (itemId == R.id.nav_contact_us) {
            showContactScreen();


        } else {

        }

        drawer.closeDrawer(GravityCompat.START);
        setTitle(navigationView.getMenu().findItem(itemId).getTitle());
        return true;
    }



    private void showActivityToolbar() {
        if (!getSupportActionBar().isShowing()) {
            getSupportActionBar().show();
        }
    }

    private void hideActivityToolbar() {
        if (getSupportActionBar().isShowing()) {
            getSupportActionBar().hide();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (isDoubleBackToExit) {
                super.onBackPressed();
                finish();
            }
            if (!isDoubleBackToExit) {
                Toast.makeText(this, getString(R.string.re_tap_text), Toast.LENGTH_SHORT).show();
            }
            this.isDoubleBackToExit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isDoubleBackToExit = false;
                }
            }, 2000);
        }
    }



    private void showHomeScreen() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, HomeFragment.newInstance(" "))
                .commit();
    }


    private void showContactScreen() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ContactFragment.newInstance (" "))
                .commit();
    }

    private void showFaqScreen() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, FaqFragment.newInstance (" "))
                .commit();
    }


    private void showProfile() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment.newInstance (" "))
                .commit();
    }


    @Override
    protected void onResume() {
        navHandlerListener = this;
        super.onResume();
    }

    @Override
    public void onNavOpenRequested() {
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    private void logoutUser() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer ( GravityCompat.START );

            session.setLogin ( false );
            db.deleteUsers ();
            finish ();
        }
    }


}

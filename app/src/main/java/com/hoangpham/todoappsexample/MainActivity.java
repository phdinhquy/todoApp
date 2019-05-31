package com.hoangpham.todoappsexample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.hoangpham.todoappsexample.Fragment.DialogFragmentAdd;
import com.hoangpham.todoappsexample.Fragment.FavoriteFragment;
import com.hoangpham.todoappsexample.Fragment.TodoFragment;
import com.hoangpham.todoappsexample.NavigateController.FrefManager;
import com.hoangpham.todoappsexample.models.TodoDTO;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,DialogFragmentAdd.DataChangeListener {
    private static final String TAG = "MainActivity";
    private Context mContext = MainActivity.this;
    // widget
    FloatingActionButton fab;
    TextView mUser, mEmail;

    // var
    FrefManager mFrefManager;
    private String username = "", email = "";
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        setSupportActionBar(toolbar);

        fragmentList = new ArrayList<>();
        TodoFragment todoFragment = new TodoFragment();
        FavoriteFragment favoriteFragment = new FavoriteFragment();
        fragmentList.add(todoFragment);
        fragmentList.add(favoriteFragment);

        if (savedInstanceState == null){
            controlFragment(fragmentList.get(0));
        }

        handleNavigationDrawer(toolbar);
    }


    private void handleNavigationDrawer(Toolbar toolbar) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                FragmentManager fm = getSupportFragmentManager();
                DialogFragmentAdd dialogFragmentAdd = new DialogFragmentAdd();
                dialogFragmentAdd.show(fm, "addFragment");

            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mFrefManager = FrefManager.getInstance(mContext);

        username = mFrefManager.getUsername();
        email = mFrefManager.getEmail();

        Log.d(TAG, "onCreate: " + username + email);
        View headerView = navigationView.getHeaderView(0);
        mUser = headerView.findViewById(R.id.Username1);
        mEmail = headerView.findViewById(R.id.email1);
        if (username.length() != 0) {
            mUser.setText(username);
            mEmail.setText(email);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_todo:
                controlFragment(fragmentList.get(0));
                break;
            case R.id.nav_favorite:
                controlFragment(fragmentList.get(1));
                break;
            case R.id.nav_logout:
                mFrefManager.setLogin(false);
                mFrefManager.setUserName("");
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                finish();
                break;

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void controlFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    @Override
    public void updateRecyclerView(TodoDTO todoDTO) {
        Log.d(TAG, "updateRecyclerView: update recyclerview");
        ((TodoFragment)fragmentList.get(0)).update(todoDTO);
    }
}

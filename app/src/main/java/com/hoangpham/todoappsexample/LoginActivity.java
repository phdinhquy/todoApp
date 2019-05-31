package com.hoangpham.todoappsexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hoangpham.todoappsexample.NavigateController.FrefManager;
import com.hoangpham.todoappsexample.models.Users;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    // widget
    EditText mUser, mPass;
    Button mBtnLogin, mBtnNavigate;

    //Fire base
    // Firebase
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference myRef;

    FrefManager frefManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        mUser = findViewById(R.id.editText_username);
        mPass = findViewById(R.id.editText_password);
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnNavigate = findViewById(R.id.btn_navigate_register);
        isLoginIn();
        setupFirebase();
        init();


    }

    /**
     * if is login just navigate to Home screen
     */
    private void isLoginIn() {
        frefManager = FrefManager.getInstance(this);
        if (frefManager.isLoginIn()) {
            navigateHomeScreen();
        }
    }

    private void navigateHomeScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean checkInput(String username, String password) {
        return username.length() == 0 || password.length() == 0;
    }

    private boolean isUserInDB(String currentUsername, String currentPassword, String dbUserName, String dbPassWord) {
        if (currentUsername.equals(dbUserName) && currentPassword.equals(dbPassWord)) {
            return true;
        }
        return false;
    }

    private void init() {
        mBtnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisActivity.class);
                startActivity(intent);
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String username = mUser.getText().toString();
                final String password = mPass.getText().toString();
                Log.d(TAG, "onClick: login in " + username);
                if (!checkInput(username, password)) {
                    Query query = myRef.child(getString(R.string.db_name_user))
                            .orderByChild(getString(R.string.field_username)).equalTo(username);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: " + dataSnapshot);
                            if (dataSnapshot.getValue() == null) {

                                Snackbar.make(view, "Wrong username or password", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

                            }
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Users users = ds.getValue(Users.class);
                                Log.d(TAG, "onDataChange: start checking user name ");

                                if (isUserInDB(username, password, users.getUsername(), users.getPassword())) {
                                    Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                                    frefManager.setLogin(true);
                                    frefManager.setUserName(username);
                                    frefManager.setEmail(users.getEmail());
                                    navigateHomeScreen();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else {
                    Snackbar.make(view, "Fill all information above", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

    }

    private void setupFirebase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
    }
}
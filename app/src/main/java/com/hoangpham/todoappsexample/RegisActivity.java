package com.hoangpham.todoappsexample;

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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hoangpham.todoappsexample.models.Users;

public class RegisActivity extends AppCompatActivity {
    private static final String TAG = "RegisActivity";
    // widget
    EditText mUser, mPass, mEmail;
    Button mBtnRegis;

    // Firebase
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference myRef;

    // var
    Users usersTemp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_regis);
        setupFirebase();
        // init Widget
        mUser = findViewById(R.id.register_editText_Username);
        mPass = findViewById(R.id.register_editText_password);
        mEmail = findViewById(R.id.register_editText_email);
        mBtnRegis = findViewById(R.id.btn_register);
        myRef.child(getString(R.string.db_name_user)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: data" + dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        init();
    }

    private boolean checkInput(String username, String password, String email) {
        return username.length() == 0 || password.length() == 0 || email.length() == 0;
    }


    private void init() {
        mBtnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUser.getText().toString();
                String password = mPass.getText().toString();
                String email = mEmail.getText().toString();

                usersTemp = new Users(username, password, email);

                if (!checkInput(username, password, email)) {

                    isUniqueUsername(username);

                } else {
                    Snackbar.make(view, "Fill all the input above to register your account", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    private void isUniqueUsername(final String username) {
        Log.d(TAG, "isUniqueUsername: Start check is unique user");
        Query query = myRef.child(getString(R.string.db_name_user))
                .orderByChild(getString(R.string.field_username)).equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot);
                boolean isExist = false;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Users users = ds.getValue(Users.class);
                    Log.d(TAG, "onDataChange: start checking user name ");
                    if (username.equals(users.getUsername())) {
                        isExist = true;
                    }
                }
                if (isExist) {
                    Toast.makeText(RegisActivity.this, "Your username is exist", Toast.LENGTH_SHORT).show();
                } else {
                    //push new user to db user
                    myRef.child(getString(R.string.db_name_user))
                            .push().setValue(usersTemp).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegisActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: databaseError" + databaseError);
            }
        });
    }

    private void setupFirebase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
    }
}

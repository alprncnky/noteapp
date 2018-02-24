package com.starkalp.fireapp2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private EditText mLoginField;
    private EditText mLoginPassword;
    private Button mLoginBtn;
    private Button mRegister;

    private ProgressDialog mProgress;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    //private DatabaseReference mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth=FirebaseAuth.getInstance();
        mDatabaseUsers= FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        mProgress=new ProgressDialog(this);

        mLoginField=(EditText)findViewById(R.id.loginEmailField);
        mLoginPassword=(EditText)findViewById(R.id.loginPasswordField);
        mLoginBtn=(Button)findViewById(R.id.loginBtn);
        mRegister=(Button)findViewById(R.id.yenikayit);


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kayitIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(kayitIntent);
            }
        });

    }

    private void checkLogin() {

        final String email=mLoginField.getText().toString().trim();
        final String password=mLoginPassword.getText().toString().trim();
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
        {
            mProgress.setMessage("Giriş Kontrol Ediliyor...");
            mProgress.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful())
                    {
                        mProgress.dismiss();
                        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(mainIntent);
                        //checkUserExist();
                    }
                    else{
                        mProgress.dismiss();
                        Toast.makeText(LoginActivity.this,"Error Giriş",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

    private void checkUserExist() {

        final String user_id=mAuth.getCurrentUser().getUid();
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_id))
                {
                    Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);               //kullanici geri gidemesin diye clear top
                    startActivity(mainIntent);
                }
                else
                {
                                                                                                                    // Intent setupIntent = new Intent(LoginActivity.this,SetupActivity.class);
                    Toast.makeText(LoginActivity.this,"Error Giriş (userexist)",Toast.LENGTH_LONG).show();          // setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);               //kullanici geri gidemesin diye clear top
                    // startActivity(setupIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}

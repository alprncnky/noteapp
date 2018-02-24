package com.starkalp.fireapp2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PostActivity extends AppCompatActivity {

    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmit;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mUserA;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mUserA=FirebaseAuth.getInstance();

        mStorage= FirebaseStorage.getInstance().getReference();

        mUser=FirebaseAuth.getInstance().getCurrentUser();
        String cur_us=mUser.getUid();

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(cur_us);
        mPostTitle=(EditText)findViewById(R.id.titleField);
        mPostDesc=(EditText)findViewById(R.id.descField);
        mSubmit=(Button)findViewById(R.id.submitBtn);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });

    }

    private void startPosting() {
        String title_val=mPostTitle.getText().toString().trim();
        String desc_val=mPostDesc.getText().toString().trim();
        if(!TextUtils.isEmpty(title_val)&& !TextUtils.isEmpty(desc_val))
        {
            DatabaseReference newPost=mDatabase.push();
            newPost.child("title").setValue(title_val);
            newPost.child("desc").setValue(desc_val);
            Toast.makeText(PostActivity.this,"Kaydedildi.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this,MainActivity.class));
        }
        else
        {
            Toast.makeText(PostActivity.this,"Bos metin kaydedilemez.", Toast.LENGTH_SHORT).show();
        }
    }

}

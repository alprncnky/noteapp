package com.starkalp.fireapp2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mNoteList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseUsers;
    private String s_result;
    private FirebaseUser mUse;

    private DatabaseReference myDbUser;

   // private AdView mAdView;           //reklam icin

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


             //   mAdView = (AdView) findViewById(R.id.adView);                     //reklam icin
             //   AdRequest adRequest = new AdRequest.Builder().build();            //reklam icin
              //  mAdView.loadAd(adRequest);                                        //reklam icin

        mAuthListener= new FirebaseAuth.AuthStateListener() {                       // login yapilmiss mi diye kontrol etmek icin
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {        //firebaseAuth bize result u getiriyor
                if(firebaseAuth.getCurrentUser()==null)
                {
                    Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);               //kullanici geri gidemesin diye clear top
                    startActivity(loginIntent);
                }
            }
        };

        mAuth=FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance().getReference().child("Users");


        mUse=FirebaseAuth.getInstance().getCurrentUser();
        if(mUse!=null) {
            s_result = mUse.getUid();
            mDatabase=mDatabase.child(s_result);
        }

        //mDatabase=FirebaseDatabase.getInstance().getReference().child("Notes");
        mDatabaseUsers=FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        mDatabase.keepSynced(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mNoteList=(RecyclerView)findViewById(R.id.note_list);                   //recycler icin recycler manager kullanmak lazim
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mNoteList.setHasFixedSize(true);
        mNoteList.setLayoutManager(mLayoutManager);              // asagi dogru inicek dikine oyuzden Linear Layout



    }



    @Override
    protected void onStart() {
        super.onStart();
        //checkUserExist();

        mAuth.addAuthStateListener(mAuthListener);



        FirebaseRecyclerAdapter<Note, NoteViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Note, NoteViewHolder>(

                Note.class,
                R.layout.note_row,
                NoteViewHolder.class,
                mDatabase
        ) {

            @Override
            protected void populateViewHolder(NoteViewHolder viewHolder, Note model, int position) {

                final String post_key=getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());

                viewHolder.mDelbtn.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Vibrator vari = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                        vari.vibrate(40);
                        mDatabase.child(post_key).removeValue();
                        return false;
                    }
                });

                /*              // uzun basılı tuttugun post silinir
                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mDatabase.child(post_key).removeValue();    //sil sonra
                        return false;
                    }
                });
                */
            }
        };

        mNoteList.setAdapter(firebaseRecyclerAdapter);
    }


    private void checkUserExist() {

        if (mAuth.getCurrentUser() != null) {
            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user_id)) {
                        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);               //kullanici geri gidemesin diye clear top
                        startActivity(setupIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }




    public static class NoteViewHolder extends RecyclerView.ViewHolder{

        View mView;

        ImageButton mDelbtn;

        public NoteViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            mDelbtn=(ImageButton)mView.findViewById(R.id.del_btn);
        }

        public void setTitle(String title)
        {
            TextView post_title=(TextView)mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setDesc(String desc)
        {
            TextView post_desc=(TextView)mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {                   //menu secekleri burada bak
        if(item.getItemId()==R.id.action_add)
        {
            startActivity(new Intent(MainActivity.this,PostActivity.class));

        }

        if(item.getItemId()==R.id.action_logout)
        {
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {

        mAuth.signOut();                // bunu kullaniyosan mAuthListeneri kurdgumuzdan dolayi kullanbildik

    }
}


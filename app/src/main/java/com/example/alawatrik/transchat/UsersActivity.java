package com.example.alawatrik.transchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView UsersList;
    private DatabaseReference signUpDatabase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = (Toolbar)findViewById(R.id.users_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add a friend to chat");
        mToolbar.setTitleTextColor(0xFFFFFFFF);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //database reference
        signUpDatabase = FirebaseDatabase.getInstance().getReference().child("SignUp");


        UsersList = (RecyclerView)findViewById(R.id.users_list);
        UsersList.setHasFixedSize(true);
        UsersList.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<SignUp, SignUpViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SignUp, SignUpViewHolder>(
                SignUp.class,
                R.layout.users_single_layout,
                SignUpViewHolder.class,
                signUpDatabase

        ) {

            @Override
            protected void populateViewHolder(SignUpViewHolder viewHolder, SignUp model, int position) {

                final String list_user_id = getRef(position).getKey();

                signUpDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                if (mAuth.getCurrentUser() != null) {

                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());

            }

           viewHolder.userView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {

                   Intent messageIntent = new Intent(UsersActivity.this, ConversationActivity.class);
                   messageIntent.putExtra("user_id", list_user_id);
                   startActivity(messageIntent);


               }
           });

            }
        };

        UsersList.setAdapter(firebaseRecyclerAdapter);


    }

    //creating viewHolder for the Recycler Adapter

    public static class SignUpViewHolder extends RecyclerView.ViewHolder{

        View userView;
        public SignUpViewHolder(View itemView) {
            super(itemView);

            userView = itemView;
        }


        //the setName method in 'populateViewHolder' class to get name from single_user_layout
       public void setName(String name){
           TextView userNameView = (TextView) userView.findViewById(R.id.user_single_name);
           userNameView.setText(name);

       }
        //the setStatus method in 'populateViewHolder' class to get status from single_user_layout
        public void setStatus(String status){
            TextView userStatusView = (TextView) userView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);

        }

    }


}

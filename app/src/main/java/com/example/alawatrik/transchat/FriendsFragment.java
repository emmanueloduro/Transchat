package com.example.alawatrik.transchat;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView UsersList;
    private DatabaseReference signUpDatabase;
    private View mMainView;
    private FirebaseAuth mAuth;


    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        signUpDatabase = FirebaseDatabase.getInstance().getReference().child("SignUp");



        UsersList = (RecyclerView) mMainView.findViewById(R.id.users_list);
        UsersList.setHasFixedSize(true);
        UsersList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;


    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<SignUp, UsersActivity.SignUpViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SignUp, UsersActivity.SignUpViewHolder>(
                SignUp.class,
                R.layout.users_single_layout,
                UsersActivity.SignUpViewHolder.class,
                signUpDatabase

        ) {




            @Override
            protected void populateViewHolder(final UsersActivity.SignUpViewHolder viewHolder, SignUp model, int position) {

                final String list_user_id = getRef(position).getKey();

                signUpDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());

                signUpDatabase = FirebaseDatabase.getInstance().getReference().child("SignUp").child(mAuth.getCurrentUser().getUid());



                viewHolder.userView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent messageIntent = new Intent(getContext(), ConversationActivity.class);
                        messageIntent.putExtra("user_id", list_user_id);
                        startActivity(messageIntent);


                    }
                });

            }
        };

        UsersList.setAdapter(firebaseRecyclerAdapter);


    }

    //creating viewHolder for the Recycler Adapter

    public static class SignUpViewHolder extends RecyclerView.ViewHolder {

        View userView;

        public SignUpViewHolder(View itemView) {
            super(itemView);

            userView = itemView;
        }


        //the setName method in 'populateViewHolder' class to get name from single_user_layout
        public void setName(String name) {
            TextView userNameView = (TextView) userView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        //the setStatus method in 'populateViewHolder' class to get status from single_user_layout
        public void setStatus(String status) {
            TextView userStatusView = (TextView) userView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);

        }




    }




    @Override
    public void registerForContextMenu(View view) {
    }




}
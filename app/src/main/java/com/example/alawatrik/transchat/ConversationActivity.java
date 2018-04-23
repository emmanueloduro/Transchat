package com.example.alawatrik.transchat;

import android.content.Context;
import java.util.List;
import java.util.Locale;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.graphics.Color.YELLOW;

public class ConversationActivity extends AppCompatActivity {
    private String mChatUser;
    private Toolbar mChatToolbar;
    private DatabaseReference mRootRef;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private String mCurrent_userId;
    private FirebaseUser current_user;



    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;
    private TextView mTitleView;
    private CircleImageView mProfile_Image;
    private RecyclerView mMessageList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;
    private ArrayList<String> messageList;
    private ArrayList<String> timeList;
    private RecyclerView messages_list_recycler;
    private RecyclerView.Adapter adapter;
    private TextView username;
    private DatabaseReference mLanguageRef;
    public static String language_preference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        //Actionbar References
        mChatToolbar = (Toolbar) findViewById(R.id.message_app_bar);
        setSupportActionBar(mChatToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        mChatToolbar.setTitleTextColor(0xFFFFFFFF);
        mChatToolbar.setSubtitleTextColor(YELLOW);

        //send button and editText control references
        mChatSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);
        mChatMessageView = (EditText) findViewById(R.id.chat_message_view);
        mMessageList = (RecyclerView) findViewById(R.id.messages_list);
        mLinearLayout = new LinearLayoutManager(this);



        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(mLinearLayout);
        mAdapter = new MessageAdapter(messagesList);
        mMessageList.setAdapter(mAdapter);



        //action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        //getting Extra "user_id" string from FriendsFragment
        mChatUser = getIntent().getStringExtra("user_id");
        getSupportActionBar().setTitle(mChatUser);


        //Get current user Id
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_userId = mAuth.getCurrentUser().getUid();

        mLanguageRef = FirebaseDatabase.getInstance().getReference().child("SignUp");

        //Inflating layout with chat_custom_bar layout resource file
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(action_bar_view);



        //load messages from the database(message table/tree)
      //  loadMessages();


        //----custom action bar items--- retrieving stufz//
        mRootRef.child("SignUp").child(mChatUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String chat_user_name = dataSnapshot.child("name").getValue().toString();
               // String chat_user_status = dataSnapshot.child("status").getValue().toString();

                //retrieving the language of the other user, so that his/her language could be translated
              //  language_preference = dataSnapshot.child("language").getValue().toString();
                getSupportActionBar().setTitle(chat_user_name);

                String online = dataSnapshot.child("online").getValue().toString();

                if(online.equals("true")){

                    getSupportActionBar().setSubtitle("online");
                }
                else{
                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long Lastseen = Long.parseLong(online);

                    String LastseenTime = GetTimeAgo.getTimeAgo(Lastseen, getApplication());

                    getSupportActionBar().setSubtitle(LastseenTime);
                }
                //getSupportActionBar().setSubtitle(chat_user_status);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        //storing chats in database
        mRootRef.child("Chats").child(mCurrent_userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(mChatUser)) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrent_userId + "/" + mChatUser, chatAddMap);
                    chatUserMap.put("Chat/" + mChatUser + "/" + mCurrent_userId, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {

                                Log.d("CHAT_LOG", databaseError.getMessage().toString());

                            }

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //OnClickListerner for the send button(what happens when the send button is clicked) Call the sendMessage method

        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();
            }
        });

    }









    //method to load the messages
    private void loadMessages(String msgId) {

        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.keepSynced(true);



        mRef.child("messages").child(mCurrent_userId).child(mChatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);
                String msg = message.getMessage();

                mMessageList.scrollToPosition(messageList.size()-1);
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); //end of child even listerner

    }//end of loadMessage method


    //a method to set friend name to app bar



    //The sendMessage method, method to send messages to other user as it gets stored in the database
    private void sendMessage() {

        String message = mChatMessageView.getText().toString();

        if (!TextUtils.isEmpty(message)) {

            String current_user_ref = "messages/" + mCurrent_userId + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + mCurrent_userId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrent_userId).child(mChatUser).push();
            mRootRef.keepSynced(true);
            

            String push_id = user_message_push.getKey();





            Date currentTime = Calendar.getInstance().getTime();
            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("time", currentTime.toString());
            messageMap.put("from", mCurrent_userId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mChatMessageView.setText("");

            mRootRef.child("Chat").child(mCurrent_userId).child(mChatUser).child("seen").setValue(true);
            mRootRef.keepSynced(true);
            mRootRef.child("Chat").child(mCurrent_userId).child(mChatUser).child("timestamp").setValue(ServerValue.TIMESTAMP);
            mRootRef.keepSynced(true);

            mRootRef.child("Chat").child(mChatUser).child(mCurrent_userId).child("seen").setValue(false);
            mRootRef.keepSynced(true);
            mRootRef.child("Chat").child(mChatUser).child(mCurrent_userId).child("timestamp").setValue(ServerValue.TIMESTAMP);
            mRootRef.keepSynced(true);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null) {

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }

                }
            });

            ///loadMessages(push_id);
            loadSentMessage(push_id);

        }//end of main if statement




    }//end of sendMessage method

    private void loadSentMessage(String push_id) {
        mRootRef.keepSynced(true);
        messageList = new ArrayList<String>();
        timeList = new ArrayList<String>();

        messages_list_recycler = findViewById(R.id.messages_list);
        messages_list_recycler.setHasFixedSize(true);
        messages_list_recycler.setLayoutManager(new LinearLayoutManager(this));
        setMessageAdapter(push_id);


    }



    private void setMessageAdapter(final String msg_id) {

        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.keepSynced(true);

        mRef.child("messages").child(mCurrent_userId).child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Messages message = dataSnapshot.getValue(Messages.class);
                    mRef.keepSynced(true);
                    String msg = snapshot.child("message").getValue(String.class);
                    String time = snapshot.child("time").getValue(String.class);
                    Collections.addAll(messageList, msg);
                    Collections.addAll(timeList, time);
                }}
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        adapter = new messageAdapter(getApplicationContext(), messageList, timeList);
        messages_list_recycler.setAdapter(adapter);

    }


    public class messageAdapter extends RecyclerView.Adapter<messageAdapter.messageViewHolder> {
        Context context;
        ArrayList<String> messageList;
        ArrayList<String> timeList;

        public class messageViewHolder extends RecyclerView.ViewHolder {

            TextView message_text, message_time;

            public messageViewHolder(View itemView) {
                super(itemView);
                message_text =  itemView.findViewById(R.id.message_text);
                message_time = itemView.findViewById(R.id.message_time);

            }
        }

        public messageAdapter(Context context, ArrayList<String> messageList, ArrayList<String> timeList) {
            this.context = context;
            this.messageList = messageList;
            this.timeList = timeList;
        }


        @Override
        public messageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.message_single_layout, parent, false);
            return new messageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(messageViewHolder holder, int position) {
            holder.message_text.setText(messageList.get(position));
            holder.message_time.setText(timeList.get(position));
        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }

    }//end of docOrRelAdapter

}

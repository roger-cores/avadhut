package com.frostox.calculoII.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.frostox.calculoII.R;
import com.frostox.calculoII.nodes.Usermcq;
import com.frostox.calculoII.nodes.Usertopics;
import com.frostox.calculoII.view_holders.MyViewHolder;
import com.frostox.calculoII.view_holders.ResultDataObjectHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;


public class Result extends AppCompatActivity {
    private FirebaseRecyclerAdapter recyclerAdapter, recyclerAdapter2;
    private DatabaseReference usertopicref, usermcqref;
    private String userkey;
    private String[] key;
    private RecyclerView recyclerView, recyclerView2;
    private boolean topicmode = true;

    File extraClassFolder;

    int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        extraClassFolder = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()), "extraClass");

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView2 = (RecyclerView) findViewById(R.id.rv2);

        Intent intent = this.getIntent();
        userkey = intent.getStringExtra("userkey");

        usertopicref = FirebaseDatabase.getInstance().getReference().child("users").child(userkey).child("topics");
        getKey(usertopicref);
        usermcqref = FirebaseDatabase.getInstance().getReference().child("users").child(userkey).child("mcqs");
        Query query = usertopicref.orderByChild("name");


        recyclerAdapter = new FirebaseRecyclerAdapter<Usertopics, ResultDataObjectHolder>(Usertopics.class, R.layout.resulttopicitem, ResultDataObjectHolder.class, query) {
            @Override
            public void populateViewHolder(ResultDataObjectHolder dataObjectHolder, final Usertopics usertopics, final int position) {
                dataObjectHolder.label.setText(usertopics.getName());
                dataObjectHolder.difficulty.setText("Difficulty: "+usertopics.getDifficulty());
                dataObjectHolder.date.setText("Date: "+usertopics.getTimestamp());
                dataObjectHolder.ll.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        recyclerView2.setVisibility(View.VISIBLE);
                        counter = 1;
                        topicmode = false;
                        Query query1 = usermcqref.orderByChild("topic").equalTo(key[position]);
                        recyclerAdapter2 = new FirebaseRecyclerAdapter<Usermcq, MyViewHolder>(Usermcq.class, R.layout.navresultitem, MyViewHolder.class, query1) {
                            @Override
                            public void populateViewHolder(MyViewHolder myViewHolder, final Usermcq usermcq, final int position2) {
                                myViewHolder.getQuestionnumber().setText("Q." + counter);
                                if(usermcq.getType().equals("text")) {
                                    myViewHolder.getQuestion().setText(usermcq.getQuestion());
                                    myViewHolder.getAnswer().setText(usermcq.getAnswer());
                                    myViewHolder.getYourAnswer().setText(usermcq.getYourAnswer());
                                    myViewHolder.getExplanation().setText(usermcq.getExplanation());
//                                    Picasso.with(getBaseContext()).load("http://www.frostox.com/extraclass/uploads/");
//
//                                    Picasso.with(getBaseContext()).load("http://www.frostox.com/extraclass/uploads/");
                                }
                                else if(usermcq.getType().equals("image"))
                                {

                                    String answer = usermcq.getState().substring(usermcq.getState().length()-1);
                                    File quest = new File(extraClassFolder, usermcq.getMcqid() + "quest");
                                    File A = new File(extraClassFolder,  usermcq.getMcqid() + answer);
                                    File yourAnswerFile = new File(extraClassFolder,  usermcq.getMcqid() + usermcq.getYourOption());

                                    myViewHolder.getQuestion().setText("");
                                    myViewHolder.getAnswer().setText("");
                                    myViewHolder.getYourAnswer().setText("");
                                    Picasso.with(getBaseContext()).load(quest).into(myViewHolder.getImgquestion());

                                    Picasso.with(getBaseContext()).load(A).into(myViewHolder.getImganswer());
                                    Picasso.with(getBaseContext()).load(yourAnswerFile).into(myViewHolder.getYourAnswerImage());


                                }

                                File explanation = new File(extraClassFolder,  usermcq.getMcqid() + "explanation");
                                if(explanation.exists()){
                                    Picasso.with((getBaseContext())).load(explanation).into(myViewHolder.getImgexplanation());
                                } else myViewHolder.getExplanation().setText(usermcq.getExplanation());


                                if (usermcq.getState().contains("Correct"))
                                    myViewHolder.getCt().setImageResource(R.drawable.mark);
                                else if (usermcq.getState().contains("Wrong"))
                                    myViewHolder.getCt().setImageResource(R.drawable.cross);
                                else if(usermcq.getState().contains("Skip"))
                                    myViewHolder.getCt().setImageResource(R.drawable.skip);
                                counter++;
                            }
                        };

                        recyclerView2.setAdapter(recyclerAdapter2);
                        recyclerView.setVisibility(View.GONE);
                    }
                });


            }
        };

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);

    }






    @Override
    public void onBackPressed() {
      //  super.onBackPressed();
        if (!topicmode) {
            Log.d("checking",""+topicmode);
            recyclerView2.setVisibility(View.GONE);
            recyclerView.setAdapter(recyclerAdapter);
            recyclerView.setVisibility(View.VISIBLE);
            topicmode = true;
        } else {
            finish();
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recyclerAdapter.cleanup();
        if(!topicmode) {
           recyclerAdapter2.cleanup();
        }
    }


    public void getKey(Query query) {
        query.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count1 = 0;
                int length = (int) dataSnapshot.getChildrenCount();
                key = new String[length];

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    key[count1] = postSnapshot.getKey();
                  //  Usertopics usertopics = postSnapshot.getValue(Usertopics.class);
                    count1++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }


        });
    }
}
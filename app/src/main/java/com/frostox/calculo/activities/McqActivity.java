package com.frostox.calculo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.frostox.calculo.nodes.MCQs;
import com.frostox.calculo.nodes.Usermcq;
import com.frostox.calculo.adapters.ResultData;
import com.frostox.calculo.adapters.Resultadapter;
import com.google.common.collect.Lists;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import calculo.frostox.com.calculo.R;

public class McqActivity extends AppCompatActivity {


    TextView optionA, optionB, optionC, optionD, qn, questionnumber, click, score;
    ImageView imga, imgb, imgc, imgd, imgquestion;
    String id, namebar, difficulty, noq, userkey, usertopickey;
    CardView cardview;
    Button Skip;

    String[] ans, ansA, ansB, ansC, ansD, explanation, explanationType, name, question, topic, type, key;
    String[] rvqno, rvexpurl, rvurl, rvansurl, rvqn, rvans, rvexp, rvnid;
    int[] ct;
    ScrollView scrollres;
    RelativeLayout choosea, chooseb, choosec, choosed, prntrl;

    int count, scorecount;
    boolean[] checkanswer;
    int skipped, correct, page;
    boolean noqmode = false;
    RecyclerView rv;
    Resultadapter ra;
    Firebase ref, mcqref;

    File extraClassFolder;



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcq);

        extraClassFolder = new File(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()), "extraClass");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Skip = (Button) findViewById(R.id.skip);
        click = (TextView) findViewById(R.id.answer);
        questionnumber = (TextView) findViewById(R.id.questionnumber);
        imgquestion = (ImageView) findViewById(R.id.imgquestion);
        click.setVisibility(View.INVISIBLE);
        questionnumber.setVisibility(View.INVISIBLE);
        prntrl = (RelativeLayout) findViewById(R.id.prntrl);
        prntrl.setVisibility(View.INVISIBLE);

        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        ref = new Firebase("https://extraclass.firebaseio.com/mcqs/");

        Intent intent = this.getIntent();
        id = intent.getStringExtra("id");
        difficulty = intent.getStringExtra("difficulty");
        noq = intent.getStringExtra("noq");
        userkey = intent.getStringExtra("userkey");
        usertopickey = intent.getStringExtra("usertopickey");
        mcqref = new Firebase("https://extraclass.firebaseio.com/users/" + userkey + "/mcqs");
        namebar = "Default";
        Query query = ref.orderByChild("topic").equalTo(id);
        count = 0;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalCount = Integer.parseInt(noq);

                ans = new String[totalCount];
                ansA = new String[totalCount];
                ansB = new String[totalCount];
                ansC = new String[totalCount];
                ansD = new String[totalCount];
                type = new String[totalCount];
                name = new String[totalCount];
                question = new String[totalCount];
                explanationType = new String[totalCount];
                explanation = new String[totalCount];
                key = new String[totalCount];
                Random random = new Random();
                List<DataSnapshot> data = Lists.newArrayList(dataSnapshot.getChildren().iterator());
                if(data.size()!=0)
                do{
                    Log.e("data.size", data.size() + "");
                    int index = random.nextInt(data.size());
                    Log.e("index", index + "");
                    DataSnapshot postSnapshot = data.get(index);

                    key[count] = postSnapshot.getKey();
                    MCQs mcqtext = postSnapshot.getValue(MCQs.class);
                    int diff = Integer.parseInt(difficulty);
                    if (mcqtext.getDifficulty() <= diff) {


                        if (mcqtext.getType().equals("image")) {
                            File quest = new File(extraClassFolder, "" + key[count] + "quest");
                            File A = new File(extraClassFolder, key[count] + "A");
                            File B = new File(extraClassFolder, key[count] + "B");
                            File C = new File(extraClassFolder, key[count] + "C");
                            File D = new File(extraClassFolder, key[count] + "D");

                            if((!quest.exists()) || (!quest.exists()) || (!quest.exists()) || (!quest.exists())){
                                data.remove(index);
                                continue;
                            }


                        }

                        if(mcqtext.getExplanationType().equals("image")) {

                            File explanation = new File(extraClassFolder, key[count] + "explanation");

                            if(!explanation.exists()){
                                data.remove(index);
                                continue;
                            }
                        }

                        ans[count] = mcqtext.getAns();
                        ansA[count] = mcqtext.getAnsA();
                        ansB[count] = mcqtext.getAnsB();
                        ansC[count] = mcqtext.getAnsC();
                        ansD[count] = mcqtext.getAnsD();
                        type[count] = mcqtext.getType();
                        name[count] = mcqtext.getName();
                        question[count] = mcqtext.getQuestion();
                        explanation[count] = mcqtext.getExplanation();
                        explanationType[count] = mcqtext.getExplanationType();
                        count++;

                        if (count >= totalCount) break;
                    }

                    data.remove(index);
                }
                while(count<totalCount && data.size() != 0);

                /*
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    key[count] = postSnapshot.getKey();
                    MCQs mcqtext = postSnapshot.getValue(MCQs.class);
                    int diff = Integer.parseInt(difficulty);
                    if (mcqtext.getDifficulty() <= diff) {


                        if (mcqtext.getType().equals("image")) {
                            File quest = new File(extraClassFolder, "" + key[count] + "quest");
                            File A = new File(extraClassFolder, key[count] + "A");
                            File B = new File(extraClassFolder, key[count] + "B");
                            File C = new File(extraClassFolder, key[count] + "C");
                            File D = new File(extraClassFolder, key[count] + "D");

                            if((!quest.exists()) || (!quest.exists()) || (!quest.exists()) || (!quest.exists())){
                                continue;
                            }


                        }

                        if(mcqtext.getExplanationType().equals("image")) {

                            File explanation = new File(extraClassFolder, key[count] + "explanation");

                            if(!explanation.exists()){
                                continue;
                            }
                        }

                        ans[count] = mcqtext.getAns();
                        ansA[count] = mcqtext.getAnsA();
                        ansB[count] = mcqtext.getAnsB();
                        ansC[count] = mcqtext.getAnsC();
                        ansD[count] = mcqtext.getAnsD();
                        type[count] = mcqtext.getType();
                        name[count] = mcqtext.getName();
                        question[count] = mcqtext.getQuestion();
                        explanation[count] = mcqtext.getExplanation();
                        explanationType[count] = mcqtext.getExplanationType();
                        count++;

                        if (count >= totalCount) break;
                    }
                }
                */
                if (count != 0) {
                    initViews();
                } else {
                    Skip.setVisibility(View.INVISIBLE);
                    click.setVisibility(View.VISIBLE);
                    click.setText("No MCQs over here");
                    Toast.makeText(McqActivity.this, "Sorry there are no MCQs", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initViews() {

        prntrl.setVisibility(View.VISIBLE);
        ct = new int[count];
        rvqn = new String[count];
        rvqno = new String[count];
        rvurl = new String[count];
        rvansurl = new String[count];
        rvexpurl = new String[count];
        rvans = new String[count];
        rvexp = new String[count];
        rvnid = new String[count];

        cardview = (CardView) findViewById(R.id.card_view);
        choosea = (RelativeLayout) findViewById(R.id.choosea);
        chooseb = (RelativeLayout) findViewById(R.id.chooseb);
        choosec = (RelativeLayout) findViewById(R.id.choosec);
        choosed = (RelativeLayout) findViewById(R.id.choosed);

        scrollres = (ScrollView) findViewById(R.id.scrollres);
        qn = (TextView) findViewById(R.id.question);
        optionA = (TextView) findViewById(R.id.ansa);
        optionB = (TextView) findViewById(R.id.ansb);
        optionC = (TextView) findViewById(R.id.ansc);
        optionD = (TextView) findViewById(R.id.ansd);
        score = (TextView) findViewById(R.id.score);
        scorecount = 0;
        click = (TextView) findViewById(R.id.answer);
        questionnumber = (TextView) findViewById(R.id.questionnumber);
        imga = (ImageView) findViewById(R.id.imga);
        imgb = (ImageView) findViewById(R.id.imgb);
        imgc = (ImageView) findViewById(R.id.imgc);
        imgd = (ImageView) findViewById(R.id.imgd);
        imgquestion = (ImageView) findViewById(R.id.imgquestion);
        click.setVisibility(View.VISIBLE);
        questionnumber.setVisibility(View.VISIBLE);
        qn.setVisibility(View.INVISIBLE);
        imgquestion.setVisibility(View.INVISIBLE);
        textinvisible();
        imginvisible();
        TouchListener(choosea, "A");
        TouchListener(chooseb, "B");
        TouchListener(choosec, "C");
        TouchListener(choosed, "D");
        TouchListener(Skip, "skip");
        page = 0;
        Log.d("Testreachedinit",type[page] + ".." + ansA[page]);
        checkanswer = new boolean[count];
        load(0);
    }


    public void onClickPrev(View v) {
        --page;
        if (page >= (-1)) {
            load(page);
        } else {
            Toast.makeText(McqActivity.this, "No MCQs Behind", Toast.LENGTH_LONG).show();
        }

    }

    public void onClickNext(View v) {
        ++page;

        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

       /* if (v == null && !skip)
            Snackbar.make(viewGroup, "Correct!", Snackbar.LENGTH_LONG).show();
        else if(v!=null && !skip)Snackbar.make(viewGroup, "Wrong!", Snackbar.LENGTH_LONG).show();
        else Snackbar.make(viewGroup, "Skipped!", Snackbar.LENGTH_LONG).show();*/

        if (page == (Integer.parseInt(noq))) {
            noqmode = true;
            Toast.makeText(McqActivity.this, "All Done!", Toast.LENGTH_LONG).show();
            getSupportActionBar().setTitle("Result");
            scrollres.setVisibility(View.VISIBLE);
            textinvisible();
            imginvisible();
            prntrl.setVisibility(View.INVISIBLE);
            score.setVisibility(View.VISIBLE);
            cardview.setVisibility(View.VISIBLE);
            count = Integer.parseInt(noq);
            score.setText("Total Questions: " + count + "\n" + "Attempted: " + (count - skipped) + "\n" + "Not Attempted: " + skipped + "\n" + "Right Answer: " + scorecount + "\n" + "Wrong Answer: " + (count - scorecount - skipped) + "\n" + "Total Score: " + scorecount);
            rv.setVisibility(View.VISIBLE);
            ra = new Resultadapter(this, getdata());
            rv.setAdapter(ra);
        }

        if (page != count && count != 0) {
            load(page);
        } else if (page >= count) {
            Toast.makeText(McqActivity.this, "All Done!", Toast.LENGTH_LONG).show();
            Skip.setVisibility(View.INVISIBLE);
            getSupportActionBar().setTitle("Result");
            textinvisible();
            imginvisible();
            prntrl.setVisibility(View.INVISIBLE);
            scrollres.setVisibility(View.VISIBLE);
            score.setVisibility(View.VISIBLE);
            cardview.setVisibility(View.VISIBLE);

            if (!noqmode)
                score.setText("Total Questions: " + count + "\n" + "Attempted: " + (count - skipped) + "\n" + "Not Attempted: " + skipped + "\n" + "Right Answer: " + scorecount + "\n" + "Wrong Answer: " + (count - scorecount - skipped) + "\n" + "Total Score: " + scorecount);

            rv.setVisibility(View.VISIBLE);
            ra = new Resultadapter(this, getdata());
            rv.setAdapter(ra);
        }

    }

    public void load(int i) {
        Log.e("type[i]", i + "");
        if (type[i].equals("text")) {
            textvisible();
            imginvisible();
            qn.setText(question[i]);
            optionA.setText("(a) " + ansA[i]);
            optionB.setText("(b) " + ansB[i]);
            optionC.setText("(c) " + ansC[i]);
            optionD.setText("(d) " + ansD[i]);
            questionnumber.setText("Q." + (i + 1));
            rvqno[i] = ("Q." + (i + 1));
            rvqn[i] = question[i];
            rvurl[i] = getString(R.string.base_url_extraclass)+"uploads/";

            if (ans[i].equals("A")) {
                rvans[i] = ansA[i];
            } else if (ans[i].equals("B")) {
                rvans[i] = ansB[i];
            } else if (ans[i].equals("C")) {
                rvans[i] = ansC[i];
            } else if (ans[i].equals("D")) {
                rvans[i] = ansD[i];
            }
            rvansurl[i] = getString(R.string.base_url_extraclass)+"uploads/";


        } else if (type[i].equals("image")) {
            textinvisible();
            imgvisible();
            Log.d("key", key[i]);
            File quest = new File(extraClassFolder, "" + key[i] + "quest");
            Log.d("key", quest.exists() + "");
            File A = new File(extraClassFolder, key[i] + "A");
            File B = new File(extraClassFolder, key[i] + "B");
            File C = new File(extraClassFolder, key[i] + "C");
            File D = new File(extraClassFolder, key[i] + "D");

            Picasso.with(getBaseContext()).load(quest).into(imgquestion);
            Picasso.with(getBaseContext()).load(A).into(imga);
            Picasso.with(getBaseContext()).load(B).into(imgb);
            Picasso.with(getBaseContext()).load(C).into(imgc);
            Picasso.with(getBaseContext()).load(D).into(imgd);





            questionnumber.setText("Q." + (i + 1));
            rvqno[i] = ("Q." + (i + 1));
            rvqn[i] = "";
            rvurl[i] = (getString(R.string.base_url_extraclass)+"uploads/" + key[i] + "quest");

            if (ans[i].equals("A")) {
                rvansurl[i] = (getString(R.string.base_url_extraclass)+"uploads/" + key[i] + "A");
                rvans[i] = "A";
            } else if (ans[i].equals("B")) {
                rvansurl[i] = (getString(R.string.base_url_extraclass)+"uploads/" + key[i] + "B");
                rvans[i] = "B";
            } else if (ans[i].equals("C")) {
                rvansurl[i] = (getString(R.string.base_url_extraclass)+"uploads/" + key[i] + "C");
                rvans[i] = "C";
            } else if (ans[i].equals("D")) {
                rvansurl[i] = (getString(R.string.base_url_extraclass)+"uploads/" + key[i] + "D");
                rvans[i] = "D";
            }
        }
        if (explanationType[i].equals("text")) {
            rvexp[i] = explanation[i];
            rvexpurl[i] = getString(R.string.base_url_extraclass)+"uploads/";
        } else if (explanationType[i].equals("image")) {
            rvexpurl[i] = (getString(R.string.base_url_extraclass)+"uploads/" + key[i] + "explanation");
            rvexp[i] = "";
        }

        rvnid[i] = key[i];


        getSupportActionBar().setTitle(name[i]);
    }

   /* public void vibrateDevice() {
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }*/

    public void TouchListener(View v, final String option) {

        final String check = String.valueOf(v.getId());
        v.setOnTouchListener(new View.OnTouchListener() {
            public final static int FINGER_RELEASED = 0;
            public final static int FINGER_TOUCHED = 1;
            public final static int FINGER_DRAGGING = 2;
            public final static int FINGER_UNDEFINED = 3;
            private int fingerState = FINGER_RELEASED;
            private String state;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        if (fingerState == FINGER_RELEASED) fingerState = FINGER_TOUCHED;
                        else fingerState = FINGER_UNDEFINED;

                        if (ans != null) {

                            Usermcq mcqs = null;


                            if (ans[page].equalsIgnoreCase(option)) {
                                ct[page] = R.drawable.mark;
                                state = "Correct";
                                checkanswer[page] = true;
                                scorecount++;
                                onClickNext(null);

                            } else if (option.equals("skip")) {
                                ct[page] = R.drawable.skip;
                                state = "Skip";
                                skipped++;
                                checkanswer[page] = false;
                                onClickNext(null);
                            } else {
                                ct[page] = R.drawable.cross;
                                state = "Wrong";
                                checkanswer[page] = false;
                                onClickNext((View) findViewById(R.id.dummy));
                            }
                            Log.d("Testreachedtouch",state + ".." + usertopickey + ".." +type[page-1] + ".." + ansA[page-1]);
                            if (ans[page-1].equals("A"))
                                mcqs = new Usermcq(usertopickey, key[page-1], state + " " + ans[page-1], ansA[page-1], question[page-1], type[page-1]);
                            else if (ans[page-1].equals("B"))
                                mcqs = new Usermcq(usertopickey, key[page-1], state + " " + ans[page-1], ansA[page-1], question[page-1], type[page-1]);
                            else if (ans[page-1].equals("C"))
                                mcqs = new Usermcq(usertopickey, key[page-1], state + " " + ans[page-1], ansA[page-1], question[page-1], type[page-1]);
                            else if (ans[page-1].equals("D"))
                                mcqs = new Usermcq(usertopickey, key[page-1], state + " " + ans[page-1], ansA[page-1], question[page-1], type[page-1]);
                            mcqref.push().setValue(mcqs);
                        }

                        break;

                    case MotionEvent.ACTION_UP:
                        if (fingerState != FINGER_DRAGGING) {
                            fingerState = FINGER_RELEASED;

                        } else if (fingerState == FINGER_DRAGGING) fingerState = FINGER_RELEASED;
                        else fingerState = FINGER_UNDEFINED;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (fingerState == FINGER_TOUCHED || fingerState == FINGER_DRAGGING)
                            fingerState = FINGER_DRAGGING;
                        else fingerState = FINGER_UNDEFINED;
                        break;

                    default:
                        fingerState = FINGER_UNDEFINED;

                }

                return false;
            }
        });

    }

    public List<ResultData> getdata() {
        List<ResultData> input = new ArrayList<>();
        int length;
        if (noqmode) length = (Integer.parseInt(noq));
        else length = count;
        for (int i = 0; i < length; i++) {
            ResultData current = new ResultData(rvnid[i], rvurl[i], rvansurl[i], rvexpurl[i], rvqno[i], rvqn[i], rvans[i], rvexp[i], ct[i]);


            input.add(current);
        }
        return input;
    }

    public void textvisible() {
        qn.setVisibility(View.VISIBLE);
        optionA.setVisibility(View.VISIBLE);
        optionB.setVisibility(View.VISIBLE);
        optionC.setVisibility(View.VISIBLE);
        optionD.setVisibility(View.VISIBLE);
    }

    public void textinvisible() {
        qn.setVisibility(View.INVISIBLE);
        optionA.setVisibility(View.INVISIBLE);
        optionB.setVisibility(View.INVISIBLE);
        optionC.setVisibility(View.INVISIBLE);
        optionD.setVisibility(View.INVISIBLE);
    }

    public void imgvisible() {
        imgquestion.setVisibility(View.VISIBLE);
        imga.setVisibility(View.VISIBLE);
        imgb.setVisibility(View.VISIBLE);
        imgc.setVisibility(View.VISIBLE);
        imgd.setVisibility(View.VISIBLE);
    }

    public void imginvisible() {
        imgquestion.setVisibility(View.INVISIBLE);
        imga.setVisibility(View.INVISIBLE);
        imgb.setVisibility(View.INVISIBLE);
        imgc.setVisibility(View.INVISIBLE);
        imgd.setVisibility(View.INVISIBLE);

    }
}


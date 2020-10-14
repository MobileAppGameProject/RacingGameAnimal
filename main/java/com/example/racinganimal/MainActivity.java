package com.example.racinganimal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView txtGrade, firstPosition, secondPosition, thirdPosition;
    CheckBox cb1, cb2, cb3;
    SeekBar sk1, sk2, sk3;
    ImageButton btnPlay, btnClose;
    RelativeLayout rnkBox;
    RacingAnimalModel model;
    Spinner scoreSpinner;
    ArrayList<Integer> scoreArray;
    ArrayList<Integer> previousState;
    Button boostButton, rankingBtn;
    final String PREVIOUS_STATE = "previous state";
    int score = 100;


    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        int cbId;
        int sk1Progress = sk1.getProgress();
        int sk2Progress = sk2.getProgress();
        int sk3Progress = sk3.getProgress();
        int currentScore = model.getScore();
        int rnkBoxVisibility = rnkBox.getVisibility();
        if(cb1.isChecked()){
            cbId = cb1.getId();
        } else if(cb2.isChecked()){
            cbId = cb2.getId();
        } else {
            cbId = cb3.getId();
        }
        ArrayList<Integer> listState = new ArrayList<>();
        listState.add(cbId);
        listState.add(sk1Progress);
        listState.add(sk2Progress);
        listState.add(sk3Progress);
        listState.add(currentScore);
        listState.add(scoreSpinner.getSelectedItemPosition());
        listState.add(rnkBoxVisibility);
        if(model.isListRankingEmpty()) {
            listState.add(model.getIdAtPosition(0));
            listState.add(model.getIdAtPosition(1));
            listState.add(model.getIdAtPosition(2));
        }
        outState.putIntegerArrayList(PREVIOUS_STATE,listState);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AnhXa();
        disenableSeekBar();
        rnkBox.setVisibility(View.INVISIBLE);

        if(savedInstanceState != null){
            previousState = savedInstanceState.getIntegerArrayList(PREVIOUS_STATE);
            if(previousState.get(0) == cb1.getId()){
                cb1.setChecked(true);
            } else if(previousState.get(0) == cb2.getId() ){
                cb2.setChecked(true);
            } else {
                cb3.setChecked(true);
            }
            sk1.setProgress(previousState.get(1));
            sk2.setProgress(previousState.get(2));
            sk3.setProgress(previousState.get(3));
            score = previousState.get(4);
            scoreSpinner.setSelection(previousState.get(5));
            if((previousState.get(1) >= sk1.getMax() && previousState.get(2) >= sk2.getMax() && previousState.get(3) >= sk3.getMax())
                || (previousState.get(1) == 0 && previousState.get(2) == 0 && previousState.get(3) == 0)){
                enableCheckbox();
            } else {
                disableCheckbox();
            }
            if(previousState.get(6) == View.VISIBLE){
                //First Place
                if(previousState.indexOf(sk1.getId()) == 7){
                    firstPosition.setText("Tiger");
                } else if (previousState.indexOf(sk2.getId()) == 7){
                    firstPosition.setText("Lion");
                } else{
                    firstPosition.setText("Dog");
                }

                //Second Place
                if(previousState.indexOf(sk1.getId()) == 8){
                    secondPosition.setText("Tiger");
                } else if (previousState.indexOf(sk2.getId()) == 8){
                    secondPosition.setText("Lion");
                } else{
                    secondPosition.setText("Dog");
                }

                //Third Place
                if(previousState.indexOf(sk1.getId()) == 9){
                    thirdPosition.setText("Tiger");
                } else if (previousState.indexOf(sk2.getId()) == 9){
                    thirdPosition.setText("Lion");
                } else{
                    thirdPosition.setText("Dog");
                }
                rnkBox.setVisibility(View.VISIBLE);
            }
        }


        txtGrade.setText("" + score);
        model = new RacingAnimalModel(5,60000,300,score);
        scoreArray = model.getScoreArray();

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_expandable_list_item_1,getResources().getStringArray(R.array.score));
//        ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_expandable_list_item_1,scoreArray);
        final ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,R.layout.spinner_item,scoreArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scoreSpinner.setAdapter(adapter);
        scoreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int bet = (Integer) adapterView.getItemAtPosition(i);
                model.setBet(bet);
                Toast.makeText(MainActivity.this, bet +"", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final CountDownTimer countDownTimer = new CountDownTimer(model.getPeriod(),model.getInterval()) {
            @Override
            public void onTick(long l) {
                int one = model.getStep();
                int two = model.getStep();
                int three = model.getStep();
                if(sk1.getProgress() >= sk1.getMax() && sk2.getProgress() >= sk2.getMax() && sk3.getProgress() >= sk3.getMax()){
                    this.cancel();
                    ranking();
                    rnkBox.setVisibility(View.VISIBLE);
                    if(cb1.isChecked()){
                        model.calculateScore(sk1.getId());
                    }
                    if(cb2.isChecked()){
                        model.calculateScore(sk2.getId());
                    }
                    if(cb3.isChecked()){
                        model.calculateScore(sk3.getId());
                    }
                    txtGrade.setText("" + model.getScore());
                    enableCheckbox();
                }
                sk1.setProgress(sk1.getProgress()+one);
                sk2.setProgress(sk2.getProgress()+two);
                sk3.setProgress(sk3.getProgress()+three);

                if(sk1.getProgress() >= sk1.getMax()){
                    model.addId(sk1.getId());
                }
                if(sk2.getProgress() >= sk2.getMax()){
                    model.addId(sk2.getId());
                }
                if(sk3.getProgress() >= sk3.getMax()){
                    model.addId(sk3.getId());
                }
            }
            @Override
            public void onFinish() {
            }
        };

        rankingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RankingPage.class);
                startActivity(intent);
            }
        });

        boostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb1.isChecked()){
                    sk1.setProgress(model.boostPay(sk1.getProgress()));
                } else if(cb2.isChecked()){
                    sk2.setProgress(model.boostPay(sk2.getProgress()));
                } else {
                    sk3.setProgress(model.boostPay(sk3.getProgress()));
                }
                txtGrade.setText(model.getScore()+"");
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb1.isChecked() || cb2.isChecked() || cb3.isChecked()){
                    btnPlay.setVisibility(View.INVISIBLE);
                    if(savedInstanceState != null ){
                        if(!(sk1.getProgress() < sk1.getMax() && sk1.getProgress()>0
                        || sk2.getProgress() < sk2.getMax() && sk2.getProgress()>0
                        || sk3.getProgress() < sk3.getMax() && sk3.getProgress()>0))
                        {
                            sk1.setProgress(0);
                            sk2.setProgress(0);
                            sk3.setProgress(0);
                        }
                    } else {
                        sk1.setProgress(0);
                        sk2.setProgress(0);
                        sk3.setProgress(0);
                    }






//                    if(savedInstanceState != null ){
//                        ArrayList<Integer> previousState = savedInstanceState.getIntegerArrayList(PREVIOUS_STATE);
//                        if(previousState.get(1) < sk1.getMax() || previousState.get(2) < sk2.getMax() || previousState.get(3) < sk3.getMax())
//                        {
//                            sk1.setProgress(previousState.get(1));
//                            sk2.setProgress(previousState.get(2));
//                            sk3.setProgress(previousState.get(3));
//                        } else {
//                            sk1.setProgress(0);
//                            sk2.setProgress(0);
//                            sk3.setProgress(0);
//                        }
//                    } else {
//                        sk1.setProgress(0);
//                        sk2.setProgress(0);
//                        sk3.setProgress(0);
//                    }
                    model.clearRanking();
                    countDownTimer.start();
                    disableCheckbox();
                } else {
                    Toast.makeText(MainActivity.this, "check the box", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPlay.setVisibility(View.VISIBLE);
                rnkBox.setVisibility(View.INVISIBLE);
            }
        });


        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    //bo check 2 va 3
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                }
            }
        });

        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    //bo check 1 va 3
                    cb1.setChecked(false);
                    cb3.setChecked(false);
                }
            }
        });


        cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    //bo check 1 va 2
                    cb1.setChecked(false);
                    cb2.setChecked(false);
                }
            }
        });
    }

    private void disableCheckbox(){
        cb1.setEnabled(false);
        cb2.setEnabled(false);
        cb3.setEnabled(false);
        scoreSpinner.setEnabled(false);
        rankingBtn.setEnabled(false);
    }

    private void enableCheckbox(){
        cb1.setEnabled(true);
        cb2.setEnabled(true);
        cb3.setEnabled(true);
        scoreSpinner.setEnabled(true);
        rankingBtn.setEnabled(true);

    }

    private void disenableSeekBar(){
        sk1.setEnabled(false);
        sk2.setEnabled(false);
        sk3.setEnabled(false);
    }
    private void AnhXa(){
        txtGrade = findViewById(R.id.score);
        cb1 = findViewById(R.id.checkBox1);
        cb2 = findViewById(R.id.checkBox2);
        cb3 = findViewById(R.id.checkBox3);
        sk1 = findViewById(R.id.seekBar1);
        sk2 = findViewById(R.id.seekBar2);
        sk3 = findViewById(R.id.seekBar3);
        btnPlay = findViewById(R.id.playButton);
        btnClose = findViewById(R.id.closeButton);
        rnkBox = findViewById(R.id.rankingBox);
        firstPosition = findViewById(R.id.firstPosition);
        secondPosition = findViewById(R.id.secondPosition);
        thirdPosition = findViewById(R.id.thirdPosition);
        scoreSpinner = findViewById(R.id.scoreSpinner);
        boostButton = findViewById(R.id.boostButton);
        rankingBtn = findViewById(R.id.rankingButton);

    }

    private void ranking(){
        //First Place
        if(model.getPosition(sk1.getId()) == 0){
            firstPosition.setText("Tiger");
        } else if (model.getPosition(sk2.getId()) == 0){
            firstPosition.setText("Lion");
        } else{
            firstPosition.setText("Dog");
        }

        //Second Place
        if(model.getPosition(sk1.getId()) == 1){
            secondPosition.setText("Tiger");
        } else if (model.getPosition(sk2.getId()) == 1){
            secondPosition.setText("Lion");
        } else{
            secondPosition.setText("Dog");
        }

        //Third Place
        if(model.getPosition(sk1.getId()) == 2){
            thirdPosition.setText("Tiger");
        } else if (model.getPosition(sk2.getId()) == 2){
            thirdPosition.setText("Lion");
        } else{
            thirdPosition.setText("Dog");
        }
        Log.d("Test",sk1.getId()+"");
        Log.d("Test",sk2.getId()+"");
        Log.d("Test",sk3.getId()+"");
//        Log.d("Test",model.listRanking.toString());
    }
}
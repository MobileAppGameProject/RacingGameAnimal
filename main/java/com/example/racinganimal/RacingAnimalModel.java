package com.example.racinganimal;

import android.os.CountDownTimer;

import java.util.ArrayList;
import java.util.Random;

public class RacingAnimalModel {

    private ArrayList<Integer> listRanking;
    private ArrayList<Integer> scoreArray;
    private Random random;
    private int maxStep ;
    private int period;
    private int interval;
    private int score;
    private int bet;


    public RacingAnimalModel(int maxStep,int period, int interval, int score){
        this.period = period;
        this.interval = interval;
        this.maxStep = maxStep;
        this.score = score;
        listRanking = new ArrayList<Integer>();
        scoreArray = new ArrayList<>();
        scoreArray.add(2);
        scoreArray.add(6);
        scoreArray.add(8);
        random = new Random();
    }

    public int getMaxStep(){
        return maxStep;
    }

    public int getStep(){
        return random.nextInt(maxStep);
    }


    public void addId(int id){
        if (!listRanking.contains(id)){
            listRanking.add(id);
        }
    }

    public int getIdAtPosition(int i){
        return listRanking.get(i);
    }

    public int getPosition(int id){
        return listRanking.indexOf(id);
    }

    public void clearRanking(){
        listRanking.clear();
    }

    public int getPeriod(){
        return period;
    }

    public int getInterval(){
        return interval;
    }

    public void calculateScore(int id){
        if(getPosition(id) == 0){
            score += bet;
        } else {
            score -= bet;
        }
    }

    public int getScore(){
        return score;
    }

    public ArrayList<Integer> getScoreArray(){
        return scoreArray;
    }

    public void setBet(int bet){
        this.bet = bet;
    }

    public int boostPay(int progress){
        score -= bet/2;
        return progress+getMaxStep()*3;
    }
    public boolean isListRankingEmpty(){
        if(listRanking == null){
            return true;
        } else {
            return false;
        }
    }
}

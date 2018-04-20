package project.aha.models;

import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class CARS_Question implements Serializable {
    private int q_id;
    private String question ;
    private LinkedHashMap<Integer , CARS_Answer> answers ;
    private int answer_id;

    public CARS_Question(int q_id, String question) {
        this.q_id = q_id;
        this.question = question;
        this.answers = new LinkedHashMap<>();
    }


    public void addAnswer(int a_id , String answer , double grade){
        answers.put(a_id , new CARS_Answer(a_id , answer , grade ));
    }


    public int getAnswer_id() {
        return answer_id;
    }

    public void setAnswer_id(int answer_id) {
        this.answer_id = answer_id;
    }

    public int getQ_id() {
        return q_id;
    }

    public void setQ_id(int q_id) {
        this.q_id = q_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }


    public LinkedHashMap<Integer, CARS_Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(LinkedHashMap<Integer, CARS_Answer> answers) {
        this.answers = answers;
    }
}

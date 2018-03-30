package project.aha.models;

import java.util.ArrayList;

public class CARS_Question {

    private int q_id;
    private String question ;
    private ArrayList<CARS_Answer> answers ;

    public CARS_Question(int q_id, String question) {
        this.q_id = q_id;
        this.question = question;
        this.answers = new ArrayList<>();
    }


    public void addAnswer(int a_id , String answer , double grade){
        answers.add(new CARS_Answer(a_id , answer , grade ));
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

    public ArrayList<CARS_Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<CARS_Answer> answers) {
        this.answers = answers;
    }
}

package project.aha.models;

/**
 * Created by Hema on 3/18/2018 AD.
 */

public class CARS_Answer {

    private int a_id ;
    private String answer ;
    private double grade ;

    public CARS_Answer(int a_id, String answer, double grade) {
        this.a_id = a_id;
        this.answer = answer;
        this.grade = grade;
    }


    public int getA_id() {
        return a_id;
    }

    public void setA_id(int a_id) {
        this.a_id = a_id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }
}

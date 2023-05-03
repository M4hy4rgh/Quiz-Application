/* Developed By:
    Mahyar Ghasemi Khah
*/


package com.example.demo;

public class Question {
    private String qText;
    private String[] AnswersText;
    private String answerIndex;

    public Question(String qText, String[] AnswersText, String answerIndex) {
        this.qText = qText;
        this.AnswersText = AnswersText;
        this.answerIndex = answerIndex;
    }

    public String getQuestion() {
        return qText;
    }

    public String[] getOptions() {
        return AnswersText;
    }

    public String  getCorrectAnswer() {
        return answerIndex;
    }


}

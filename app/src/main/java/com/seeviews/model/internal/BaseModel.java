package com.seeviews.model.internal;

import com.google.gson.annotations.SerializedName;
import com.seeviews.model.api.receive.Hotel;
import com.seeviews.model.api.receive.Incentive;
import com.seeviews.model.api.receive.OAuthResponse;
import com.seeviews.model.api.receive.Question;
import com.seeviews.model.api.receive.UserResponse;

import java.util.ArrayList;

/**
 * Created by Jan-Willem on 3-12-2016.
 */

public class BaseModel {
    @SerializedName("code")
    String code;
    @SerializedName("auth")
    Auth auth;
    @SerializedName("user")
    User user;
    @SerializedName("hotel")
    Hotel hotel;
    @SerializedName("incentive")
    Incentive incentive;
    @SerializedName("questions")
    ArrayList<Question> questions;
//    @SerializedName("answers")
//    HashMap<Integer, Answer> answers;

    public BaseModel(String code, OAuthResponse auth, UserResponse u, Incentive i, ArrayList<Question> q) {
        this.code = code;
        updateFromAuthResponse(auth);
        updateFromUserResponse(u);
        updateFromIncentive(i);
        updateFromQuestions(q);
    }

    public void updateFromAuthResponse(OAuthResponse auth) {
        this.auth = new Auth(auth);
    }


    public void updateFromUserResponse(UserResponse u) {
        this.user = new User(u);
        this.hotel = new Hotel(u);
    }

    public void updateFromIncentive(Incentive i) {
        this.incentive = i;
    }

    public void updateFromQuestions(ArrayList<Question> q) {
        this.questions = q;
    }

    public String getCode() {
        if (code == null)
            code = "";
        return code;
    }

    public Auth getAuth() {
        if (auth == null)
            auth = new Auth();
        return auth;
    }

    public User getUser() {
        if (user == null)
            user = new User();
        return user;
    }

    public Hotel getHotel() {
        if (hotel == null)
            hotel = new Hotel();
        return hotel;
    }

    public Incentive getIncentive() {
        if (incentive == null)
            incentive = new Incentive();
        return incentive;
    }

    public ArrayList<Question> getQuestions() {
        if (questions == null)
            questions = new ArrayList<>();
        return questions;
    }

    public Question getQuestion(int questionId) {
        for (Question q : getQuestions())
            if (q.getId() == questionId)
                return q;
        return null;
    }

//    public HashMap<Integer, Answer> getAnswers() {
//        if (answers == null)
//            answers = new HashMap<>();
//        return answers;
//    }
//
//    public Answer getAnswer(int questionId) {
//        Answer a = getAnswers().get(questionId);
//        if (a == null) {
//            a = new Answer(questionId);
//            getAnswers().put(questionId, a);
//        }
//        return a;
//    }

    public boolean isValid() {
        return getAuth().isValid() && getUser().isValid() && getHotel().isValid(); //TODO consider also validating questions
    }

    public boolean allQuestionsAreAnswered() {
        for (Question q : getQuestions()) {
            if (!q.isComplete())
                return false;
        }
        return true;
    }

    public void setQuestion(Question question) {
        int toRemove = -1;
        for (int i = 0; i < getQuestions().size(); i++) {
            if (getQuestions().get(i).getId() == question.getId()) {
                toRemove = i;
                break;
            }
        }

        if (toRemove != -1) {
            getQuestions().remove(toRemove);
            getQuestions().add(toRemove, question);
        }
    }

    public String getAuthHeader() {
        return getAuth() == null ? "" : getAuth().getTokenType() + " " + getAuth().getAccess_token();
    }
}

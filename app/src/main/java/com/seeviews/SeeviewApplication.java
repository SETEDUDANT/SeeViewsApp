package com.seeviews;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.seeviews.model.api.receive.Incentive;
import com.seeviews.model.api.receive.OAuthResponse;
import com.seeviews.model.api.receive.Question;
import com.seeviews.model.api.receive.UserResponse;
import com.seeviews.model.internal.Answer;
import com.seeviews.model.internal.BaseModel;
import com.seeviews.utils.DataFileUtils;
import com.seeviews.utils.NetworkUtils;

import java.util.ArrayList;

/**
 * Created by Jan-Willem on 3-12-2016.
 */

public class SeeviewApplication extends Application {

    private static final String TAG = "SeeviewApplication";

    private BaseModel data;
    private ArrayList<DataListener> listeners = new ArrayList<>();
    private boolean isLoading;

    public interface DataListener {
        void onDataLoaded(@NonNull BaseModel data);

        void onDataError(@NonNull Throwable t);
    }

    public void assumeDataLoaded(DataListener listener) {
        if (data != null && listener != null)
            listener.onDataLoaded(data);
    }

    public void registerDataListener(DataListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            if (isLoading) {
                //dataListener will be called later
            } else {
                if (data == null)
                    readDataForAllListeners();
                else
                    listener.onDataLoaded(data);
            }
        }
    }

    public void unregisterDataListener(DataListener listener) {
        if (listener != null)
            listeners.remove(listener);
    }

    public void notifyListenersSuccess() {
        try {
            for (DataListener l : listeners)
                if (l != null)
                    l.onDataLoaded(data);
        } catch (Exception e) {
            Log.e(TAG, "notifyListenersSuccess: " + e.getLocalizedMessage());
        }
    }

    public void notifyListenersError(Throwable t) {
        try {
            for (DataListener l : listeners)
                if (l != null)
                    l.onDataError(t);
        } catch (Exception e) {
            Log.e(TAG, "notifyListenersError: " + e.getLocalizedMessage());
        }
    }

    private void readDataForAllListeners() {
        if (data == null) {
            isLoading = true;
            DataFileUtils.getData(this, new DataListener() {

                @Override
                public void onDataLoaded(BaseModel d) {
                    isLoading = false;
                    data = d;
                    notifyListenersSuccess();
                }

                @Override
                public void onDataError(Throwable t) {
                    Log.e(TAG, "onDataError: " + (t == null ? "null" : t.getLocalizedMessage()));
                    isLoading = false;
                    notifyListenersError(t);
                }
            });
        } else {
            notifyListenersSuccess();
        }
    }

    public void writeData() {
        DataFileUtils.writeData(this, data);
    }

    public void burnData(Runnable runOnfinish) {
        data = null;
        listeners = new ArrayList<>();
        DataFileUtils.burnEverything(this, runOnfinish);
    }

    public void storeAuthUserAndQuestions(String code, OAuthResponse auth, UserResponse u, Incentive i, ArrayList<Question> q) {
        data = new BaseModel(code, auth, u, i, q);
        writeData();
    }

    interface RefreshListener {
        void onRefreshComplete();
    }

    public void refreshUser(final RefreshListener listener) {
        if (data != null)
            NetworkUtils.getUser(this, data.getAuth(), new NetworkUtils.UserListener() {
                @Override
                public void onUserSuccess(UserResponse u) {
                    data.updateFromUserResponse(u);
                    writeData();
                    if (listener != null)
                        listener.onRefreshComplete();
                }

                @Override
                public void onUserFail() {
                }
            });
    }

    public void refreshIncentive(final RefreshListener listener) {
        if (data != null)
            NetworkUtils.getIncentive(this, data.getAuth(), new NetworkUtils.IncentiveListener() {
                @Override
                public void onIncentiveSuccess(Incentive i) {
                    data.updateFromIncentive(i);
                    writeData();
                    if (listener != null)
                        listener.onRefreshComplete();
                }

                @Override
                public void onIncentiveFail(Throwable t) {
                }
            });
    }

    public void refreshQuestions(final RefreshListener listener) {
        if (data != null)
            NetworkUtils.getQuestions(this, data.getAuth(), new NetworkUtils.QuestionsListener() {
                @Override
                public void onQuestionsSuccess(ArrayList<Question> q) {
                    data.updateFromQuestions(q);
                    writeData();
                    if (listener != null)
                        listener.onRefreshComplete();
                }

                @Override
                public void onQuestionsFail(Throwable t) {
                }
            });
    }

    public void postSentiment(final Answer changedAnswer, final NetworkUtils.SentimentListener listener) {
        if (data != null)
            NetworkUtils.postSentiments(this, data.getAuth(), changedAnswer, new NetworkUtils.SentimentListener() {
                @Override
                public void onSentimentSuccess(Question q) {
                    data.setQuestion(q);
                    writeData();
                    if (listener != null)
                        listener.onSentimentSuccess(q);
                }

                @Override
                public void onSentimentFail(Throwable t) {
                    if (listener != null)
                        listener.onSentimentFail(t);
                }
            });
    }

    public void deleteReview(int reviewId, final NetworkUtils.DeleteAnswerCallback callback) {
        if (data != null)
            NetworkUtils.deleteAnswer(this, data.getAuthHeader(), reviewId, new NetworkUtils.DeleteAnswerCallback() {
                @Override
                public void onSuccess(ArrayList<Question> questions) {
                    data.updateFromQuestions(questions);
                    if (callback != null)
                        callback.onSuccess(questions);
                }

                @Override
                public void onFailure(Throwable t) {
                    if (callback != null)
                        callback.onFailure(t);
                }
            });
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        cleanup();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        cleanup();
    }

    private void cleanup() {
        writeData();
        data = null;
        listeners = new ArrayList<>();
    }
}

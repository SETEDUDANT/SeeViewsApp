package com.seeviews.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.seeviews.R;
import com.seeviews.model.api.receive.OAuthResponse;
import com.seeviews.model.api.receive.Question;
import com.seeviews.model.api.receive.UserResponse;
import com.seeviews.model.internal.Answer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan-Willem on 30-11-2016.
 */

public class PrefUtils {
    private static final String TAG = "PrefUtils";

    private static final String KEY_OAUTH_TOKENTYPE = "oauth_tokenType";
    private static final String KEY_OAUTH_EXPIRETIME_S = "oauth_expiretimeinseconds";
    private static final String KEY_OAUTH_ACCESS_TOKEN = "oauth_accesstoken";
    private static final String KEY_OAUTH_REFRESH_TOKEN = "oauth_refreshtoken";

    private static final String KEY_USER = "key_user";
    private static final String KEY_QUESTIONS = "key_questions";
    private static final String KEY_ANSWER_PREFIX = "question";
    private static final String KEY_HEADER_HEIGHT = "header_height";

    private static SharedPreferences p(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c);//new SecurePreferences(c);
    }

    private static SharedPreferences.Editor e(Context c) {
        return p(c).edit();
    }

    private static long currentTimeInSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    public static void logout(Context c, Runnable onFinish) {
//        setOAuth(c, null, null);
//        e(c).remove(KEY_USER)
//                .remove(KEY_QUESTIONS)
//                .apply();
        e(c).clear().apply(); //Burn everything
    }

//    static void setOAuth(final Context c, final OAuthResponse token, final Runnable onFinish) {
//        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                if (c != null) {
//                    if (token == null) {
//                        e(c).remove(KEY_OAUTH_TOKENTYPE)
//                                .remove(KEY_OAUTH_EXPIRETIME_S)
//                                .remove(KEY_OAUTH_ACCESS_TOKEN)
//                                .remove(KEY_OAUTH_REFRESH_TOKEN)
//                                .apply();
//                    } else {
//                        e(c).putString(KEY_OAUTH_TOKENTYPE, token.getTokenType())
//                                .putLong(KEY_OAUTH_EXPIRETIME_S, currentTimeInSeconds() + token.getExpires_in())
//                                .putString(KEY_OAUTH_ACCESS_TOKEN, token.getAccess_token())
//                                .putString(KEY_OAUTH_REFRESH_TOKEN, token.getRefresh_token())
//                                .apply();
//                    }
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                if (onFinish != null)
//                    onFinish.run();
//            }
//        });
//    }
//
//    public interface AuthListener {
//        void onAuth(String type, String token);
//    }
//
//    static class AuthStrings {
//        String type;
//        String token;
//    }
//
//    static AuthStrings getValidOAuthTokenOrNull(Context c) {
//        AuthStrings res = null;
//        if (c != null) {
//            SharedPreferences p = p(c);
//            long expiresTime = p.getLong(KEY_OAUTH_EXPIRETIME_S, 0);
//            if (expiresTime > currentTimeInSeconds()) {
//                String type = p.getString(KEY_OAUTH_TOKENTYPE, null);
//                String accessToken = p.getString(KEY_OAUTH_ACCESS_TOKEN, null);
//                String refreshToken = p.getString(KEY_OAUTH_REFRESH_TOKEN, null);
//
//                if (!StringUtils.hasEmpty(type, accessToken, refreshToken)) {
//                    res = new AuthStrings();
//                    res.type = type;
//                    res.token = accessToken;
//                }
//            }
//        }
//        return res;
//    }

//    public static void getValidOAuthTokenOrNull(final Context c, final AuthListener listener) {
//        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, AuthStrings>() {
//            @Override
//            protected AuthStrings doInBackground(Void... voids) {
//                return getValidOAuthTokenOrNull(c);
//            }
//
//            @Override
//            protected void onPostExecute(AuthStrings s) {
//                super.onPostExecute(s);
//                if (listener != null)
//                    listener.onAuth(s == null ? null : s.type, s == null ? null : s.token);
//            }
//        });
//    }
//
//    static void setUser(final Context c, final UserResponse u, final Runnable onFinish) {
//        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                if (c != null) {
//                    if (u == null)
//                        e(c).remove(KEY_USER).apply();
//                    else {
//                        Gson g = new GsonBuilder().create();
//                        String gson = g.toJson(u);
//                        e(c).putString(KEY_USER, gson).apply();
//                    }
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                if (onFinish != null)
//                    onFinish.run();
//            }
//        });
//    }
//
//    public interface UserListener {
//        void onUser(UserResponse u);
//    }
//
//    public static void getUser(final Context c, final UserListener listener) {
//        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, UserResponse>() {
//            @Override
//            protected UserResponse doInBackground(Void... voids) {
//                UserResponse res = null;
//                if (c != null) {
//                    String gson = p(c).getString(KEY_USER, "");
//                    try {
//                        res = new Gson().fromJson(gson, UserResponse.class);
//                    } catch (Exception e) {
//                        Log.e(TAG, "getUser Exception: " + e.getLocalizedMessage());
//                    }
//                }
//                return res;
//            }
//
//            @Override
//            protected void onPostExecute(UserResponse u) {
//                super.onPostExecute(u);
//                if (listener != null)
//                    listener.onUser(u);
//            }
//        });
//    }
//
//    static void setQuestions(final Context c, final List<Question> q, final Runnable onFinish) {
//        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                if (c != null) {
//                    if (q == null)
//                        e(c).remove(KEY_QUESTIONS).apply();
//                    else {
//                        Gson g = new GsonBuilder().create();
//                        String gson = g.toJson(q);
//                        e(c).putString(KEY_QUESTIONS, gson).apply();
//                    }
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                if (onFinish != null)
//                    onFinish.run();
//            }
//        });
//    }
//
//    public interface QuestionsListener {
//        void onQuestions(ArrayList<Question> questions);
//    }
//
//    public static void getQuestions(final Context c, final QuestionsListener listener) {
//        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, ArrayList<Question>>() {
//            @Override
//            protected ArrayList<Question> doInBackground(Void... voids) {
//                ArrayList<Question> res = null;
//                if (c != null) {
//                    String gson = p(c).getString(KEY_QUESTIONS, "");
//                    try {
//                        Type listType = new TypeToken<ArrayList<Question>>() {
//                        }.getType();
//                        res = new Gson().fromJson(gson, listType);
//                    } catch (Exception e) {
//                        Log.e(TAG, "getQuestions Exception: " + e.getLocalizedMessage());
//                    }
//                }
//                return res;
//            }
//
//            @Override
//            protected void onPostExecute(ArrayList<Question> q) {
//                super.onPostExecute(q);
//                if (listener != null)
//                    listener.onQuestions(q);
//            }
//        });
//    }
//
//    public interface AnswerListerner {
//        void onAnswer(Answer answer);
//    }
//
//    private static String getAnswerId(int hotelId, int questionId) {
//        return KEY_ANSWER_PREFIX + "_" + hotelId + "_" + questionId;
//    }
//
//    public static void getAnswer(final Context c, final int hotelId, final int questionId, final AnswerListerner listener) {
//        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Answer>() {
//            @Override
//            protected Answer doInBackground(Void... voids) {
//                Answer a = null;
//                if (c != null) {
//                    String gson = p(c).getString(getAnswerId(hotelId, questionId), "");
//                    try {
//                        a = new Gson().fromJson(gson, Answer.class);
//                    } catch (Exception e) {
//                        Log.e(TAG, "getAnswer " + hotelId + "_" + questionId + " Exception: " + e.getLocalizedMessage());
//                    }
//                }
//                return a;
//            }
//
//            @Override
//            protected void onPostExecute(Answer answer) {
//                super.onPostExecute(answer);
//                if (listener != null)
//                    listener.onAnswer(answer);
//            }
//        });
//    }
//
//    public static void setAswer(final Context c, final int hotelId, final int questionId, final Answer a, final Runnable onFinish) {
//        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                if (c != null) {
//                    String answerId = getAnswerId(hotelId, questionId);
//                    if (a == null)
//                        e(c).remove(answerId).apply();
//                    else {
//                        Gson g = new GsonBuilder().create();
//                        String gson = g.toJson(a);
//                        e(c).putString(answerId, gson).apply();
//                    }
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                if (onFinish != null)
//                    onFinish.run();
//            }
//        });
//    }

    public static void setHeaderHeight(Context c, int headerHeight) {
        e(c).putInt(KEY_HEADER_HEIGHT, headerHeight).apply();
    }

    public static int getHeaderHeight(Context c) {
        int height = p(c).getInt(KEY_HEADER_HEIGHT, 0);
        return height > 0
                ? height
                : c.getResources().getDimensionPixelSize(R.dimen.header_height);
    }
}

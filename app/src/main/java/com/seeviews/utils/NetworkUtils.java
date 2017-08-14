package com.seeviews.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.gson.Gson;
import com.seeviews.R;
import com.seeviews.model.api.receive.Incentive;
import com.seeviews.model.api.receive.OAuthResponse;
import com.seeviews.model.api.receive.Question;
import com.seeviews.model.api.receive.UserResponse;
import com.seeviews.model.api.send.SentimentBody;
import com.seeviews.model.api.send.SentimentPreset;
import com.seeviews.model.api.send.UserBody;
import com.seeviews.model.internal.Answer;
import com.seeviews.model.internal.Auth;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Jan-Willem on 29-11-2016.
 */

public class NetworkUtils {

    private static final String TAG = "NetworkUtils";

    interface OAuthApi {
        @POST("oauth/token")
        @Headers("Accept: application/json")
        Call<OAuthResponse> getOAuthToken(@Body UserBody body);
    }

    interface BaseApi {
        @GET("api/v1/user")
        @Headers("Accept: application/json")
        Call<UserResponse> getUser(@Header("Authorization") String auth);


        @GET("api/v1/questions")
        @Headers("Accept: application/json")
        Call<ArrayList<Question>> getQuestions(@Header("Authorization") String auth);

        @Multipart
        @POST("/api/v1/sentiments")
        Call<Question> postSentiments(@Header("Authorization") String auth,
                                      @Part MultipartBody.Part dataPart);

        @Multipart
        @POST("/api/v1/sentiments")
        Call<Question> postSentimentsImage(@Header("Authorization") String auth,
                                           @Part MultipartBody.Part imagePart,
                                           @Part MultipartBody.Part dataPart);

        @Multipart
        @POST("/api/v1/sentiments")
        Call<Question> postSentimentsVideo(@Header("Authorization") String auth,
                                           @Part MultipartBody.Part thumbnailPart,
                                           @Part MultipartBody.Part videoPart,
                                           @Part MultipartBody.Part dataPart);

        @DELETE("/api/v1/sentiments/{questionId}")
        Call<ArrayList<Question>> deleteSentiment(@Header("Authorization") String auth,
                                                  @Path("questionId") int questionId);

        @GET("api/v1/incentive")
        @Headers("Accept: application/json")
        Call<Incentive> getIncentive(@Header("Authorization") String auth);
    }

    private static OAuthApi oAuthApi = null;
    private static BaseApi baseApi = null;

    public enum ImageType {
        REVIEW_IMAGE("review-image"),
        REVIEW_VIDEO("review-video"),
        DASHBOARD_IMAGE("dashboard-image"),
        QUESTION_IMAGE("question-image"),
        INCENTIVE_IMAGE("incentive-image");

        private final String description;

        ImageType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public static GlideUrl getAuthenticatedGlideUrl(final Context c, String auth, ImageType type, String fileName) {
        String url = c.getString(R.string.urWl_image) + type.getDescription() + "/" + fileName;
        return new GlideUrl(url, new LazyHeaders.Builder()
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", auth)
                .build());
    }

    private static Retrofit getRetrofit(Context c) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …
        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        return new Retrofit.Builder()
                .baseUrl(c == null ? "" : c.getString(R.string.url_base))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }

    private static OAuthApi getOAuthApi(Context c) {
        if (oAuthApi == null)
            oAuthApi = getRetrofit(c).create(OAuthApi.class);
        return oAuthApi;
    }

    private static BaseApi getBaseApi(Context c) {
        if (baseApi == null)
            baseApi = getRetrofit(c).create(BaseApi.class);
        return baseApi;
    }

    public interface OAuthListener {
        void onValidOAuth(OAuthResponse auth);

        void onOAuthFail();
    }

    public static void getOAuthToken(@NonNull final Context c, String userKey, @NonNull final OAuthListener listener) {
        getOAuthApi(c).getOAuthToken(new UserBody(c.getString(R.string.api_client_id), c.getString(R.string.api_client_secret), userKey)).enqueue(new Callback<OAuthResponse>() {
            @Override
            public void onResponse(Call<OAuthResponse> call, Response<OAuthResponse> response) {
                if (response.isSuccessful()) {
                    OAuthResponse r = response.body();
                    if (r != null && r.isValid()) {
//                        PrefUtils.setOAuth(c, r, null);
                        listener.onValidOAuth(r);
                    } else {
                        onFailure(call, new Exception("getOAuthToken Invalid response"));
                    }
                } else {
                    onFailure(call, new Exception("getOAuthToken Unsuccessful response"));
                }
            }

            @Override
            public void onFailure(Call<OAuthResponse> call, Throwable t) {
                Log.e(TAG, "getOAuthToken onFailure: " + t.getLocalizedMessage());
                listener.onOAuthFail();
            }
        });
    }

    public interface UserListener {
        void onUserSuccess(UserResponse u);

        void onUserFail();
    }

    public static void getUser(@NonNull final Context c, @NonNull OAuthResponse auth, @NonNull final UserListener listener) {
        String authToken = auth.getTokenType() + " " + auth.getAccess_token();
        getBaseApi(c).getUser(authToken).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                final UserResponse u = response.body();
                if (u != null) {
                    listener.onUserSuccess(u);
                } else {
                    onFailure(call, new Exception("getUser Invalid response"));
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e(TAG, "getUser onFailure: " + t.getLocalizedMessage());
                listener.onUserFail();
            }
        });
    }

    public interface IncentiveListener {
        void onIncentiveSuccess(Incentive i);

        void onIncentiveFail(Throwable t);
    }

    public static void getIncentive(@NonNull final Context c, OAuthResponse auth, @NonNull final IncentiveListener listener) {
        String authToken = auth.getTokenType() + " " + auth.getAccess_token();
        getBaseApi(c).getIncentive(authToken).enqueue(new Callback<Incentive>() {
            @Override
            public void onResponse(Call<Incentive> call, Response<Incentive> response) {
                Log.d(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: " + response.body());
                    listener.onIncentiveSuccess(response.body());
                } else {
                    onFailure(call, new Exception("Unsuccessful response"));
                }
            }

            @Override
            public void onFailure(Call<Incentive> call, Throwable t) {
                Log.e(TAG, "getIncetive onFailure: " + t.getLocalizedMessage());
                listener.onIncentiveFail(t);
            }
        });
    }

    public interface QuestionsListener {
        void onQuestionsSuccess(ArrayList<Question> questions);

        void onQuestionsFail(Throwable t);
    }

    public static void getQuestions(@NonNull final Context c, OAuthResponse auth, @NonNull final QuestionsListener listener) {
        String authToken = auth.getTokenType() + " " + auth.getAccess_token();
        getBaseApi(c).getQuestions(authToken).enqueue(new Callback<ArrayList<Question>>() {
            @Override
            public void onResponse(Call<ArrayList<Question>> call, Response<ArrayList<Question>> response) {
                final ArrayList<Question> q = response.body();
                if (q != null) {
//                    PrefUtils.setQuestions(c, q, new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//                    });
                    listener.onQuestionsSuccess(q);
                } else {
                    onFailure(call, new Exception("getQuestions Invalid response"));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Question>> call, Throwable t) {
                Log.e(TAG, "getQuestions onFailure: " + t.getLocalizedMessage());
                listener.onQuestionsFail(t);
            }
        });
    }

    public interface SentimentListener {
        void onSentimentSuccess(Question q);

        void onSentimentFail(Throwable t);
    }

    public static void postSentiments(Context c, Auth auth, Answer changeAnswer, final SentimentListener listener) {
        String authToken = auth.getTokenType() + " " + auth.getAccess_token();

        int questionId = changeAnswer.getQuestionId();

        String comment = changeAnswer.getComment();
        String imagePath = changeAnswer.getImageLoc();
        String videoPath = changeAnswer.getVideoLoc();

        ArrayList<SentimentPreset> presets = new ArrayList<>();
        for (Map.Entry<Integer, Float> e : changeAnswer.getSliderValues().entrySet()) {
            try {
                presets.add(new SentimentPreset(e.getKey(), e.getValue()));
            } catch (Exception ex) {
                Log.e(TAG, "postSentiments: " + ex.getLocalizedMessage());
            }
        }

        String imageName = null;
        try {
            imageName = new File(imagePath).getName();
        } catch (Exception e) {
        }
        String videoName = null;
        try {
            videoName = new File(videoPath).getName();
        } catch (Exception e) {
        }

        SentimentBody body = new SentimentBody(questionId, comment, imageName, videoName, presets);
        String bodyString = "";
        try {
            bodyString = new Gson().toJson(body);
        } catch (Exception e) {
            Log.e(TAG, "postSentiments: bodyString exception: " + e.getLocalizedMessage());
        }

        Log.d(TAG, "postSentiments: " + bodyString);
        MultipartBody.Part dataPart = MultipartBody.Part.createFormData("data", bodyString);

        final Callback<Question> callback = new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                Log.d(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    if (listener != null)
                        listener.onSentimentSuccess(response.body());
                } else {
                    onFailure(call, new Exception("Unsuccessful response"));
                }
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                if (listener != null)
                    listener.onSentimentFail(t);
            }
        };

        //https://github.com/square/retrofit/issues/1063, maybe parse "multipart/form-data" ?
        if (!StringUtils.hasEmpty(imagePath)) {
            File imageFile = new File(imagePath);
            Log.d(TAG, "postSentiments: " + imageFile.getAbsolutePath() + " | " + imageFile.exists());
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                    "image",
                    imageFile.getName(),
                    RequestBody.create(MediaType.parse("image/*"), imageFile));
            getBaseApi(c).postSentimentsImage(authToken, imagePart, dataPart).enqueue(callback);
        } else if (!StringUtils.hasEmpty(videoPath)) {
            File videoFile = new File(videoPath);
            final File thumbnailFile = new File(DataFileUtils.getVideoThumbnailLocation(videoFile.getAbsolutePath()));
            Log.d(TAG, "postSentiments: " + videoFile.getAbsolutePath() + " | " + videoFile.exists()
                    + "\n" + thumbnailFile.getAbsolutePath() + " | " + thumbnailFile.exists());
            MultipartBody.Part thumbnailPart = MultipartBody.Part.createFormData(
                    "image",
                    thumbnailFile.getName(),
                    RequestBody.create(MediaType.parse("image/*"), thumbnailFile));
            MultipartBody.Part videoPart = MultipartBody.Part.createFormData("video",
                    videoFile.getName(),
                    RequestBody.create(MediaType.parse("video/*"), videoFile));
            getBaseApi(c).postSentimentsVideo(authToken, thumbnailPart, videoPart, dataPart).enqueue(new Callback<Question>() {
                @Override
                public void onResponse(Call<Question> call, Response<Question> response) {
                    DataFileUtils.eraseFile(thumbnailFile);
                    if (callback != null)
                        callback.onResponse(call, response);
                }

                @Override
                public void onFailure(Call<Question> call, Throwable t) {
                    DataFileUtils.eraseFile(thumbnailFile);
                    if (callback != null)
                        callback.onFailure(call, t);
                }
            });
        } else {
            Log.e(TAG, "postSentiments: No media");
            getBaseApi(c).postSentiments(authToken, dataPart).enqueue(callback);
        }
    }

    public interface DeleteAnswerCallback {
        void onSuccess(ArrayList<Question> questions);

        void onFailure(Throwable t);
    }

    public static void deleteAnswer(@NonNull final Context c, String authToken, int reviewId, final DeleteAnswerCallback callback) {
        getBaseApi(c).deleteSentiment(authToken, reviewId).enqueue(new Callback<ArrayList<Question>>() {
            @Override
            public void onResponse(Call<ArrayList<Question>> call, Response<ArrayList<Question>> response) {
                if (response.isSuccessful()) {
                    if (callback != null)
                        callback.onSuccess(response.body());
                } else {
                    onFailure(call, new Exception("Unsuccessful call"));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Question>> call, Throwable t) {
                Log.e(TAG, "onFailure: deleteAnswer" + t.getLocalizedMessage());
                if (callback != null)
                    callback.onFailure(t);
            }
        });
    }
}

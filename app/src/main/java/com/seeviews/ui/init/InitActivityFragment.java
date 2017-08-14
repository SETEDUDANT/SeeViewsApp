package com.seeviews.ui.init;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.seeviews.R;
import com.seeviews.SeeviewApplication;
import com.seeviews.SeeviewFragment;
import com.seeviews.model.api.receive.Incentive;
import com.seeviews.model.api.receive.OAuthResponse;
import com.seeviews.model.api.receive.Question;
import com.seeviews.model.api.receive.UserResponse;
import com.seeviews.model.internal.BaseModel;
import com.seeviews.ui.home.HomeActivity;
import com.seeviews.utils.KeyboardUtils;
import com.seeviews.utils.NetworkUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

public class InitActivityFragment extends SeeviewFragment {

    private static final String TAG = "InitActivityFragment";
    private static final String KEY_INPUT = "init_input_stored";
    private static final String KEY_STATE = "init_state";

    ViewGroup sceneRoot;

    @BindView(R.id.init_center_logo)
    View centerLogo;
    @BindView(R.id.init_center_content)
    View centerContent;
    @BindView(R.id.init_input_container)
    View inputGroup;
    @BindView(R.id.init_input1)
    TextView input1;
    @BindView(R.id.init_input2)
    TextView input2;
    @BindView(R.id.init_input3)
    TextView input3;
    @BindView(R.id.init_input4)
    TextView input4;
    @BindView(R.id.init_input5)
    TextView input5;
    @BindView(R.id.init_input6)
    TextView input6;

    @BindView(R.id.init_input_holder)
    EditText inputHolder;

    @BindView(R.id.init_error)
    View error;
    @BindView(R.id.init_loading)
    View loading;

    FragmentState currentState;

    enum FragmentState {
        INIT, IDLE, LOADING, ERROR
    }

    public InitActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sceneRoot = (ViewGroup) inflater.inflate(R.layout.fragment_init, container, false);
        ButterKnife.bind(this, sceneRoot);
        return sceneRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            setState(FragmentState.INIT);
            registerDataListener(new SeeviewApplication.DataListener() {
                @Override
                public void onDataLoaded(@NonNull BaseModel data) {
                    unregisterDataListener(this);
                    if (data.isValid()) {
                        continueFromValidData();
                    } else {
                        Log.w(TAG, "onViewCreated onDataLoaded: No Valid Authentication");
                        onDataError(new Exception("No Valid Authentication"));
                    }
                }

                @Override
                public void onDataError(@NonNull Throwable t) {
                    unregisterDataListener(this);
                    setState(FragmentState.IDLE);
                }
            });
        } else {
            inputHolder.setText(savedInstanceState.getString(KEY_INPUT));
            setState(FragmentState.values()[savedInstanceState.getInt(KEY_STATE)]);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_INPUT, inputHolder.getText().toString());
        outState.putInt(KEY_STATE, currentState.ordinal());
    }

    @OnTextChanged(R.id.init_input_holder)
    public void afterInputChanged(Editable s) {
        final String input = s.toString();
        int length = input.length();
        if (length > 6)
            inputHolder.setText(input.substring(0, 5));
        else {
            input1.setText(length >= 1 ? "" + input.charAt(0) : "");
            input2.setText(length >= 2 ? "" + input.charAt(1) : "");
            input3.setText(length >= 3 ? "" + input.charAt(2) : "");
            input4.setText(length >= 4 ? "" + input.charAt(3) : "");
            input5.setText(length >= 5 ? "" + input.charAt(4) : "");
            input6.setText(length == 6 ? "" + input.charAt(5) : "");
        }

        if (length == 6) {
            //TODO make sense of the lower/uppercase mess.
            final String usableInputBecauseIOSdoesntToUppercase = input.toLowerCase();
            onCodeEntered(usableInputBecauseIOSdoesntToUppercase);
        } else
            setState(FragmentState.IDLE);
    }

    private void onCodeEntered(final String code) {
        setState(FragmentState.LOADING);
        KeyboardUtils.hideSoftKeyboard(getContext());

        NetworkUtils.getOAuthToken(getContext(), code, new NetworkUtils.OAuthListener() {
            @Override
            public void onValidOAuth(OAuthResponse auth) {
                getUserDetails(code, auth);
            }

            @Override
            public void onOAuthFail() {
                setStateError();
            }
        });
    }

    private void getUserDetails(final String code, final OAuthResponse auth) {
        setState(FragmentState.LOADING);
        if (auth.isValid())
            NetworkUtils.getUser(getContext(), auth, new NetworkUtils.UserListener() {
                        @Override
                        public void onUserSuccess(UserResponse u) {
                            getUserIncentive(code, auth, u);
                        }

                        @Override
                        public void onUserFail() {
                            setStateError();
                        }
                    }
            );
        else
            setStateError();
    }

    private void getUserIncentive(final String code, final OAuthResponse auth, final UserResponse u){
        setState(FragmentState.LOADING);
        if (auth.isValid())
            NetworkUtils.getIncentive(getContext(), auth, new NetworkUtils.IncentiveListener() {
                @Override
                public void onIncentiveSuccess(Incentive i) {
                    getUserQuestions(code, auth, u, i);
                }

                @Override
                public void onIncentiveFail(Throwable t) {
                    setStateError();
                }
            });
        else
            setStateError();
    }

    private void getUserQuestions(final String code, final OAuthResponse auth, final UserResponse u, final Incentive i) {
        setState(FragmentState.LOADING);
        if (auth.isValid())
            NetworkUtils.getQuestions(getContext(), auth, new NetworkUtils.QuestionsListener() {
                @Override
                public void onQuestionsSuccess(ArrayList<Question> questions) {
                    continueWithAuthUserAndQuestions(code, auth, u, i, questions);
                }

                @Override
                public void onQuestionsFail(Throwable t) {
                    setStateError();
                }
            });
        else
            setStateError();
    }

    private void continueWithAuthUserAndQuestions(String code, OAuthResponse auth, UserResponse u, Incentive i, ArrayList<Question> q) {
        if (getApp() != null)
            getApp().storeAuthUserAndQuestions(code, auth, u, i, q);
        continueFromValidData();
    }

    private void continueFromValidData() {
        if (!isDetached() && getActivity() != null)
            HomeActivity.start(getActivity());
    }

    private void setStateError() {
        inputHolder.setText(inputHolder.getText().toString().substring(0, 5));
        setState(FragmentState.ERROR);
    }

    private void setState(FragmentState newState) {
        Log.d(TAG, "setState: " + newState);
        currentState = newState;

        TransitionManager.beginDelayedTransition(sceneRoot);
        switch (currentState) {
            case INIT:
                centerLogo.setVisibility(View.VISIBLE);
                centerContent.setVisibility(View.GONE);
                error.setVisibility(View.GONE);
                setActivityLogoVisible(false);
                break;
            case IDLE:
                centerLogo.setVisibility(View.GONE);
                centerContent.setVisibility(View.VISIBLE);
                inputGroup.setVisibility(View.VISIBLE);
                inputHolder.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                error.setVisibility(View.GONE);
                setActivityLogoVisible(true);
                break;
            case LOADING:
                centerLogo.setVisibility(View.GONE);
                centerContent.setVisibility(View.VISIBLE);
                inputGroup.setVisibility(View.INVISIBLE);
                inputHolder.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                error.setVisibility(View.GONE);
                setActivityLogoVisible(true);
                break;
            case ERROR:
                centerLogo.setVisibility(View.GONE);
                centerContent.setVisibility(View.VISIBLE);
                inputGroup.setVisibility(View.VISIBLE);
                inputHolder.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);
                setActivityLogoVisible(true);
                break;
            default:
                break;
        }
    }

    private void setActivityLogoVisible(boolean visible) {
        if (getActivity() != null && getActivity() instanceof InitActivity)
            ((InitActivity) getActivity()).setLogoVisible(visible);
        else
            Log.w(TAG, "setActivityLogoVisible from wrong activity:  " + visible + " ->" + getActivity());
    }
}

package com.seeviews.ui.personal_info;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.seeviews.R;
import com.seeviews.SeeviewActivity;
import com.seeviews.SeeviewApplication;
import com.seeviews.model.api.receive.OAuthResponse;
import com.seeviews.model.internal.Auth;
import com.seeviews.model.internal.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class activity_personalinfo extends Activity {

    Toolbar toolbar;
    TextView back;
    ImageView backArrow;
    EditText name;
    EditText email;
    EditText room_number;
    Spinner countryname;
    Button twenty;
    Button thirty;
    Button fourthy;
    Button fifty;
    Button sixty;
    Button seventy;
    ImageButton garbage;
    ScrollView sv;
    Button buttonDelete;

    private BaseModel data;
    Auth a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalinfo);
        defineDataListener();

        toolbar = (Toolbar) findViewById(R.id.appbar);
        back = (TextView) toolbar.findViewById(R.id.back);
        backArrow = (ImageView) toolbar.findViewById(R.id.backArrow);

        name = (EditText) findViewById(R.id.eTname);
        email = (EditText) findViewById(R.id.eTeMail);
        room_number = (EditText) findViewById(R.id.eTroomNumber);
        countryname = (Spinner) findViewById(R.id.spinner4);

        twenty = (Button) findViewById(R.id.bt20);
        thirty = (Button) findViewById(R.id.bt2130);
        fourthy = (Button) findViewById(R.id.bt3140);
        fifty = (Button) findViewById(R.id.bt4150);
        sixty = (Button) findViewById(R.id.bt5160);
        seventy = (Button) findViewById(R.id.bt6170);
        sv = (ScrollView) findViewById(R.id.scrollview);
        garbage = (ImageButton) findViewById(R.id.iBgarbage);
        buttonDelete = (Button) findViewById(R.id.button5);

        back.setOnClickListener(buttonHandler);
        backArrow.setOnClickListener(buttonHandler);
        garbage.setOnClickListener(garbageHandler);



    }

    View.OnClickListener garbageHandler = new View.OnClickListener()
    {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    View.OnClickListener buttonHandler = new View.OnClickListener()
    {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void sendDataToServer(View v)
    {
        new postJSON().execute();
    }

    String authHeader;

    public SeeviewApplication.DataListener defineDataListener() {
        return new SeeviewApplication.DataListener() {
            @Override
            public void onDataLoaded(@NonNull BaseModel data) {
                authHeader = data.getAuthHeader();

            }

            @Override
            public void onDataError(@NonNull Throwable t) {

            }
        };
    }


    public class postJSON extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... strings) {

            String _address = "https://requestb.in/1ncxuyz1?inspect";
            try{
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("name", "abc");
                postDataParams.put("email", "abc@gmail.com");
                postDataParams.put("room_number", "1");

                JSONObject age = new JSONObject();
                age.put("id", "10");
                age.put("start_age", "10");
                age.put("end_age", "10");
                postDataParams.put("age_category", age);

                JSONObject language = new JSONObject();
                language.put("iso_code", "nl");
                language.put("name", "dutch");
                postDataParams.put("language", language);


                URL url = new URL(_address); // Waar je mee wilt connecten
                HttpURLConnection con = (HttpURLConnection) url.openConnection(); // Connectie openen
                con.setDoInput(true);
                con.setDoOutput(true); // You need to set it to true if you want to send (output) a request body, for example with POST or PUT requests. Sending the request body itself is done via the connection's output stream:
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Content-Language", "en-US");
                con.setRequestProperty ("Authorization", "authorization: Bearer " + authHeader);
                con.connect();

                OutputStream os = con.getOutputStream();
                OutputStreamWriter ow = new OutputStreamWriter(os, "UTF-8");
                BufferedWriter writer = new BufferedWriter(ow);

                writer.write(postDataParams.toString());
                writer.flush();
                writer.close();
                os.close();
                return "sucessfull!";

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "failed!";
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
        }
    }



}

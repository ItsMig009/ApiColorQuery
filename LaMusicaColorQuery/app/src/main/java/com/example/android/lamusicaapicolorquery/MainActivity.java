package com.example.android.lamusicaapicolorquery;
/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.android.lamusicaapicolorquery.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mUrlDisplayTextView;
    private TextView mSearchResultsTextView;
    TextView mErrorMessageTextViewReference;
    ProgressBar mProgressBarReference;
    String mColorInFirstObject = null;
    LinearLayout LL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUrlDisplayTextView = (TextView) findViewById(R.id.tv_url_display);
        mSearchResultsTextView = (TextView) findViewById(R.id.tv_color_search_result_from_json);
        mErrorMessageTextViewReference = (TextView) findViewById(R.id.tv_error_message_display);
        mProgressBarReference = (ProgressBar) findViewById(R.id.pb_loading_indicator);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_change_color) {
            makeLaMusicaQuery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method retrieves the search text from the EditText, constructs the
     * URL (using {@link NetworkUtils}) for La Musica API color to change background color,
     * , and finally fires off an AsyncTask to perform the GET request using our {@link LaMusicaQueryTask}
     */
    private void makeLaMusicaQuery() {

        URL lamusicaApiUrl = NetworkUtils.buildUrl();
        mUrlDisplayTextView.setText(lamusicaApiUrl.toString());
        new LaMusicaQueryTask().execute(lamusicaApiUrl);
    }

    public class LaMusicaQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBarReference.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String lamusicaSearchResults = null;
            try {
                lamusicaSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return lamusicaSearchResults;
        }

        @Override
        protected void onPostExecute(String lamusicaSearchResults) {
            if (lamusicaSearchResults != null && !lamusicaSearchResults.equals("")) {

                showJsonDataView();

                try{
                    // Gets JSON from String lamusicaSearchResults
                    JSONObject lamusicaJsonObject = new JSONObject(lamusicaSearchResults);
                    // Gets data array
                    JSONArray lamusicaJsonArray = lamusicaJsonObject.getJSONArray("data");
                    // Gets first element in array
                    JSONObject firstObjectInArray = lamusicaJsonArray.getJSONObject(0);
                    // Gets color value
                    mColorInFirstObject = firstObjectInArray.getString("color");
                    // Displays parsed color
                    mSearchResultsTextView.setText(mColorInFirstObject);
                    LinearLayout mainActivityLayout = (LinearLayout) findViewById(R.id.my_linear_layout);
                    // Finally changes background color to mColorFirstObject
                    mainActivityLayout.setBackgroundColor(Color.parseColor(mColorInFirstObject));

                    mProgressBarReference.setVisibility(View.INVISIBLE);

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }else{
                showErrorMessage();
            }
        }

        private void showJsonDataView(){
            mSearchResultsTextView.setVisibility(View.VISIBLE);
            mErrorMessageTextViewReference.setVisibility(View.INVISIBLE);
        }
        private void showErrorMessage(){
            mSearchResultsTextView.setVisibility(View.INVISIBLE);
            mErrorMessageTextViewReference.setVisibility(View.VISIBLE);
        }
    }

    public void setActivityBackgroundColor(int color) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }
}


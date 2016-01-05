/*
 * Copyright 2014 Google Inc. All rights reserved.
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

package com.saarang.samples.apps.iosched.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.saarang.samples.apps.iosched.R;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExpertsDirectoryActivity extends BaseActivity {
    private static final String NAVDRAWER_ITEM_EXPERTS_DIRECTORY = "Sponsors";

    private static final String SCREEN_LABEL = "Sponsors";
    private static final String LOG_TAG = "SponsorsScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "Loading");

        if (isFinishing()) {
            return;
        }

        setContentView(com.saarang.samples.apps.iosched.R.layout.activity_experts_directory);
        Log.d("hai dude", "hai");
        new getSponsors().execute();
        if (!loadpref("firstjsonloadsucces")) processJSON(loadJSONFromAsset());


    }

    private void readFromJSONFirstTime() {
        Log.d(LOG_TAG, loadJSONFromAsset());

    }

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("sponsors.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    @Override
    protected int getSelfNavDrawerItem() {
        return BaseActivity.NAVDRAWER_ITEM_EXPERTS_DIRECTORY;
    }
    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        // If the user is attending remotely, redirect them to 'Explore'
//        if (!PrefUtils.isAttendeeAtVenue(this)) {
//            startActivity(new Intent(this, ExpertsDirectoryActivity.class));
//            finish();
//        }
    }


    class getSponsors extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        JSONArray MainObject;
        JSONParser jsonParser = new JSONParser();
        int status;


        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
           if (!loadpref("firstjsonloadsucces")) {
               Log.d(LOG_TAG, "Sponsors  empty");

               // String url = "http://10.0.2.2:80/JSON/wall.php";
            String url = "http://erp.saarang.org/api/mobile/display_spons/";
            // "api/mobile/walls/";
            List<NameValuePair> paramse = new ArrayList<NameValuePair>();

            JSONObject json = jsonParser.makeHttpRequest(url,false, "GET", paramse,
                    null);
            if(json!=null) {

                SharedPreferences sharedPrefjson = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPrefjson.edit();
                editor.putString("user_name", json.toString());
                editor.commit();
                Log.d("ghj", json.toString());
                savepref("firstjsonloadsucces",true);
            }
            else{
                savepref("inetrnettoast",true);


            }
           }
            else Log.d(LOG_TAG, "Sponsors empty");
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {

            if(loadpref("inetrnettoast")){ Toast.makeText(
                    getBaseContext(),
                    "No internet connection. Check your connection and "
                            + "try again later", Toast.LENGTH_SHORT).show();
            }
          if(loadpref("firstjsonloadsucces"))  {

              SharedPreferences sharedPrefjson = getPreferences(Context.MODE_PRIVATE);
              String uname = sharedPrefjson.getString("user_name", null);
              processJSON(uname);

            }
        }
    }

    private void processJSON(String uname) {
        JSONArray theArray =null;
        try {

            JSONObject jObj;
            jObj = new JSONObject(uname);


            theArray = jObj.getJSONArray("data");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (true) {
            final String id[] = new String[theArray.length()];
            final double priority[] = new double[theArray.length()];
            final String tittle[] = new String[theArray.length()];

            final String logolink[] = new String[theArray.length()];

            final String link[] = new String[theArray.length()];


            try {
                for (int i = 0; i < theArray.length(); i++) {
                    JSONObject jsonInside = theArray.getJSONObject(i);
                    id[i] = jsonInside.getString("id");
                    priority[i] = jsonInside.getDouble("priority");
                    tittle[i] = jsonInside.getString("title");
                    logolink[i] = jsonInside.getString("logo");
                    link[i] = jsonInside.getString("sponsor_link");


                }
                int input = theArray.length();


                for(int index = 0; index<input;index++)
                {
                    for(int index1 = 0; index1<input-1;index1++)
                    {
                        if(priority[index1]>priority[index1+1])
                        {

                            double temp_prio = priority[index1];
                            String temp_logolink = logolink[index1];
                            String temp_title =tittle[index1];
                            String temp_link =link[index1];
                            priority[index1] = priority[index1+1];
                            logolink[index1] = logolink[index1+1];
                            tittle[index1] = tittle[index1+1];
                            link[index1] = link[index1+1];


                            priority[index1+1] = temp_prio;
                            logolink[index1+1] = temp_logolink;
                            tittle[index1+1] = temp_title;
                            link[index1+1] = temp_link;




                        }
                    }
                }
                for (int i = 0; i < logolink.length / 2; i++) {
                    String temp = logolink[i];
                    logolink[i] = logolink[logolink.length - 1 - i];
                    logolink[logolink.length - 1 - i] = temp;
                }
                for (int i = 0; i < tittle.length / 2; i++) {
                    String temp = tittle[i];
                    tittle[i] = tittle[tittle.length - 1 - i];
                    tittle[tittle.length - 1 - i] = temp;
                }
                for (int i = 0; i < link.length / 2; i++) {
                    String temp = link[i];
                    link[i] = link[link.length - 1 - i];
                    link[link.length - 1 - i] = temp;
                }



                for (int i=0 ;i<input;i++) {
                    Log.d("", String.valueOf(priority[i]));

                }

                SponsorsList adapter = new SponsorsList(
                        ExpertsDirectoryActivity.this, id, tittle, logolink, link);
                ListView list = (ListView) findViewById(
                        R.id.listview);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long useless_id) {

                        String url = link[position];

                        Uri webpage = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }


                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void savepref(String event,boolean key ){

        SharedPreferences fav= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit =fav.edit();
        edit.putBoolean(event, key);
        edit.commit();


    }
    public  Boolean loadpref(String event){

        SharedPreferences fav= PreferenceManager.getDefaultSharedPreferences(this);
        Boolean cbvalue=fav.getBoolean(event, false);
        return cbvalue;
    }

}
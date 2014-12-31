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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import com.saarang.samples.apps.iosched.R;
import com.saarang.samples.apps.iosched.util.PrefUtils;

public class PeopleIveMetActivity extends BaseActivity {

    private static final String FRAGMENT_PEOPLE_IVE_MET = "people_ive_met";
    public static final int REQUEST_RESOLUTION_FOR_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isFinishing()) {
            return;
        }




        setContentView(com.saarang.samples.apps.iosched.R.layout.activity_people_ive_met);
        CardView bug = (CardView) findViewById(R.id.card_view);
        CardView rate = (CardView) findViewById(R.id.card_view1);
        bug.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String[] email = {"webadmin@saarang.org"};


                composeEmail(email, "Saarang Android app bug report") ;

            }
        });

        rate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String appPackageName = "org.saarang.app"; // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }

            }
        });

        if (null == savedInstanceState) {

        }

        overridePendingTransition(0, 0);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_PEOPLE_IVE_MET;
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        // If the user is attending remotely, redirect them to 'Explore'
        if (!PrefUtils.isAttendeeAtVenue(this)) {
            startActivity(new Intent(this, BrowseSessionsActivity.class));
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLUTION_FOR_RESULT) {
            PeopleIveMetFragment fragment = (PeopleIveMetFragment) getFragmentManager()
                    .findFragmentByTag(FRAGMENT_PEOPLE_IVE_MET);
            if (resultCode == RESULT_OK) {
               // fragment.retry();
            } else {
               // fragment.showApiError();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}

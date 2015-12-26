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

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.saarang.samples.apps.iosched.R;
import com.saarang.samples.apps.iosched.util.ContactObject;
import com.saarang.samples.apps.iosched.util.PrefUtils;

public class PeopleIveMetActivity extends BaseActivity {

    private static final String FRAGMENT_PEOPLE_IVE_MET = "people_ive_met";
    public static final int REQUEST_RESOLUTION_FOR_RESULT = 1;



    ContactObject con1 = new ContactObject(
            "Aditya U",
            "adi@saarang.org",
            "+919962971662",
            "ayoo",
            R.drawable.p_adi);
    ContactObject con2 = new ContactObject(
            "Krishna Koushik",
            "krishnakoushik@saarang.org",
            "+917418789160",
            "ayoo",
            R.drawable.p_krishnakoushik);
    ContactObject con3 = new ContactObject(
            "Anish Rathi",
            "anish.rathi@saarang.org",
            "+919884685016",
            "ayoo",
            R.drawable.p_anish);
    ContactObject con4 = new ContactObject(
            "Anirvan Bordoloi",
            "anirvan.bordoloi@saarang.org",
            "+919962609617",
            "ayoo",
            R.drawable.p_anirvan);
    ContactObject con5 = new ContactObject(
            "Ashish Jha",
            "ashish.jha@saarang.org",
            "+918695851252",
            "ayoo",
            R.drawable.p_aashish);
    ContactObject con6 = new ContactObject(
            "Rushabh Menon",
            "menon@saarang.org",
            "+919791057815 ",
            "ayoo",
            R.drawable.p_rushabh);
    ContactObject con7 = new ContactObject(
            "Srijith R",
            "srijith@saarang.org",
            "+919176467000",
            "ayoo",
            R.drawable.p_srijith);
    ContactObject con8 = new ContactObject(
            "Ashwin S Pothen",
            "ashwin@saarang.org",
            "+919884300360",
            "ayoo",
            R.drawable.p_pothen);
    ContactObject con9 = new ContactObject(
            "Harshith Guntha",
            "harshith.guntha@saarang.org",
            "+918056218712 ",
            "ayoo",
            R.drawable.p_harshith);
    ContactObject con10 = new ContactObject(
            "Sai Krishna Koushik",
            "koushik.esk@saarang.org",
            "+918124343670",
            "ayoo",
            R.drawable.p_saikrishnakoushik);
    ContactObject con11 = new ContactObject(
            "Sai Akhil Matha",
            "akhil@saarang.org",
            "+918056128354",
            "ayoo",
            R.drawable.p_akhil);
    ContactObject con12 = new ContactObject(
            "Rinkesh Virani",
            "rinkeshvirani@saarang.org",
            "+919884299313",
            "ayoo",
            R.drawable.p_rinkesh);
    ContactObject con13 = new ContactObject(
            "Sreeharsha Gunda",
            "sreeharsha@saarang.org",
            "+919962663576",
            "ayoo",
            R.drawable.p_sreeharsha);
    ContactObject con14 = new ContactObject(
            "Deepak Padamata",
            "deepak@saarang.org",
            "+919789107938",
            "ayoo",
            R.drawable.p_deepak);
    ContactObject con15 = new ContactObject(
            "Aqel Ahammed",
            "aqel@saarang.org",
            "+919633229144",
            "ayoo",
            R.drawable.p_aqel);
    ContactObject con16 = new ContactObject(
            "Manavala Thambi",
            "ktmanav@saarang.org",
            "+919962605305",
            "ayoo",
            R.drawable.p_thambi);
    ContactObject con17 = new ContactObject(
            "Rathees P",
            "ratiz@saarang.org",
            "+919789575877",
            "ayoo",
            R.drawable.p_rathees);
    ContactObject con18 = new ContactObject(
            "Rahul K",
            "rahul@saarang.org",
            "+919884299695",
            "ayoo",
            R.drawable.p_rahul);
    ContactObject con19 = new ContactObject(
            "Favas M P",
            "favas@saarang.org",
            "+917418304010",
            "ayoo",
            R.drawable.p_favas);
    ContactObject con20 = new ContactObject(
            "Srinivas Ramanand",
            "ramanand@saarang.org",
            "+918807488931",
            "ayoo",
            R.drawable.p_ramanand);
    ContactObject con21 = new ContactObject(
            "Hari M",
            "hari@saarang.org",
            "+917708948827",
            "ayoo",
            R.drawable.p_hari);
    ContactObject con22 = new ContactObject(
            "Amar",
            "amar@saarang.org",
            "+919952912020",
            "ayoo",
            R.drawable.p_amar);
    ContactObject con23 = new ContactObject(
            "Bharadwaj M",
            "bharadwaj@saarang.org",
            "+918056102507",
            "ayoo",
            R.drawable.p_bharadwaj);
    ContactObject con24 = new ContactObject(
            "Ruth Babu Marpu",
            "ruth.marpu@saarang.org",
            "+919043812280",
            "ayoo",
            R.drawable.p_ruthbabu);
    ContactObject con25 = new ContactObject(
            "Vidyadhar Mudium",
            "vidyadhar@saarang.org",
            "+919952044531",
            "ayoo",
            R.drawable.p_vidyadhar);


    public Boolean isExpanded = false;
    public View expLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isFinishing()) {
            return;
        }


        ContactObject[] contactCulsec1 = {con1};
        ContactObject[] contactCulsec2 = {con2};
        ContactObject[] contactSpons = {con3,con4,con5};
        ContactObject[] contactEvents = {con6, con7};
        ContactObject[] contactProshows = {con8,con9};
        ContactObject[] contactMarketing = {con10, con11};
        ContactObject[] contactPublicity = {con12};
        ContactObject[] contactHospitality = {con13};
        ContactObject[] contactMobops = {con14,con15};
        ContactObject[] contactDesign = {con16, con17, con18};
        ContactObject[] contactFinance = {con19, con20};
        ContactObject[] contactFacilities = {con21, con22};
        ContactObject[] contactSecurity = {con23, con24};
        ContactObject[] contactQMS = {con25};

        int[] llSpons={R.id.llSponsPerson1, R.id.llSponsPerson2, R.id.llSponsPerson3};
        int[] llEvents={R.id.llEvents1, R.id.llEvents2};
        int[] llPro={R.id.llPro1, R.id.llPro2};
        int[] llMarketing={R.id.llMarketing1, R.id.llMarketing2};
        int[] llPublicity={R.id.llPublicity1};
        int[] llHospitality={R.id.llHospitality1};
        int[] llMobops={R.id.llMobops1, R.id.llMobops2};
        int[] llDesign={R.id.llDesign1, R.id.llDesign2, R.id.llDesign3};
        int[] llFinance={R.id.llFinance1, R.id.llFinance2};
        int[] llFacilities={R.id.llFacilities1, R.id.llFacilities2};
        int[] llSecurity={R.id.llSecurity1, R.id.llSecurity2};
        int[] llQMS={R.id.llQMS1};
        int[] llCulsec1= {R.id.llCulsec1};
        int[] llCulsec2= {R.id.llCulsec2};

        setContentView(com.saarang.samples.apps.iosched.R.layout.activity_people_ive_met);
        CardView bug = (CardView) findViewById(R.id.card_view);
        CardView rate = (CardView) findViewById(R.id.card_view1);

        addPopupContactN(1, R.id.layout1, R.id.layout_exp_1, llCulsec1, contactCulsec1,200);
        addPopupContactN(1, R.id.layout2, R.id.layout_exp_2, llCulsec2, contactCulsec2, 200);
        addCallContact(R.id.layout3, R.id.layout_exp_31, R.id.linearLayoutGod, R.id.linearLayoutSha, "+914422579815", "+914422579816");
        addCallContact(R.id.layout7, R.id.layout_exp_71, R.id.linearLayoutHelp1, R.id.linearLayoutHelp2, "+914422579810", "+914422579811");
        addPopupContactN(2, R.id.layout4, R.id.layout_exp_41, llEvents, contactEvents, 200);
        addPopupContactN(2, R.id.layout5, R.id.layout_exp_51, llPro, contactProshows, 200);
        addPopupContactN(2, R.id.layout6, R.id.layout_exp_61,llMarketing, contactMarketing,200);
        addPopupContactN(3, R.id.cardLayoutSpons, R.id.llSpons, llSpons, contactSpons , 400);
        addPopupContactN(3, R.id.cardLayoutDesign, R.id.llDesign, llDesign, contactDesign , 400);
        addPopupContactN(2, R.id.cardLayoutMobops, R.id.llMobops,llMobops, contactMobops,200);
        addPopupContactN(2, R.id.cardLayoutFinance, R.id.llFinance,llFinance, contactFinance,200);
        addPopupContactN(2, R.id.cardLayoutFacilities, R.id.llFacilities,llFacilities, contactFacilities,200);
        addPopupContactN(2, R.id.cardLayoutSecurity, R.id.llSecurity,llSecurity, contactSecurity,200);
        addPopupContactN(1, R.id.cardLayoutPublicity, R.id.rlPublicity, llPublicity, contactPublicity, 200);
        addPopupContactN(1, R.id.cardLayoutHospitality, R.id.rlHospitality, llHospitality, contactHospitality, 200);
        addPopupContactN(1, R.id.cardLayoutQMS, R.id.rlQMS, llQMS, contactQMS, 200);


        bug.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String[] email = {"webadmin@saarang.org"};
                composeEmail(email, "Saarang android app bug report") ;

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

    public void addCallContact(int cardview, int listview, int lla, int llb,final String num1, final String num2){
        CardView mLayout1 = (CardView) findViewById(cardview);
        LinearLayout mLayoutToBeExpanded1 = (LinearLayout) findViewById(listview);

        addAction(mLayout1, mLayoutToBeExpanded1, 200);
        collapse(mLayoutToBeExpanded1);

        LinearLayout ll1 = (LinearLayout) findViewById(lla);
        LinearLayout ll2 = (LinearLayout) findViewById(llb);
        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhoneNumber(num1);
            }
        });
        ll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhoneNumber(num2);
            }
        });

    }



    public void addPopupContactN(int num, int cardview, int listview, int[] llList, final ContactObject[] c, int height){


        CardView mLayout1 = (CardView) findViewById(cardview);
        if(num==1){
            RelativeLayout mLayoutToBeExpanded1 = (RelativeLayout) findViewById(listview);
            addAction(mLayout1, mLayoutToBeExpanded1, height);
            collapse(mLayoutToBeExpanded1);
        }
        else {
            LinearLayout mLayoutToBeExpanded1 = (LinearLayout) findViewById(listview);
            addAction(mLayout1, mLayoutToBeExpanded1, height);
            collapse(mLayoutToBeExpanded1);
        }



        for(int i=0; i<num; i++){
            LinearLayout ll = (LinearLayout) findViewById(llList[i]);
            final int sase = i;
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(PeopleIveMetActivity.this, ContactDialogActivity.class);
                    in.putExtra("details", c[sase]);
                    startActivity(in);
                }
            });
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
    private void expand(View summary, int value) {
        //set Visible
        int finalHeight = summary.getHeight();

        ValueAnimator mAnimator ;

        summary.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        summary.measure(widthSpec, 300);

        mAnimator = slideAnimator(0, value, summary);

        mAnimator.start();
    }

    public void addAction(final View layout, final View summary, final int value) {

        layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (summary.getVisibility() == View.GONE) {


                    if(isExpanded){
                        collapse(expLayout);

                    }
                    else{
                        isExpanded = true;
                    }

                    expLayout = summary;
                    expand(summary, value);

                } else {

                    if(isExpanded){
                        isExpanded = false;

                    }

                    collapse(summary);
                }
            }
        });
    }


    private void collapse(final View summary) {
        int finalHeight = summary.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0, summary);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                summary.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimator.start();
    }


    private ValueAnimator slideAnimator(int start, int end, final View summary) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);


        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = summary.getLayoutParams();
                layoutParams.height = value;
                summary.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }
    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}

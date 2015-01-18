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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
        contacts(R.id.layout1,R.id.layout_exp_1,R.id.imageView1,"+919444005768");
        contacts(R.id.layout2,R.id.layout_exp_2,R.id.imageView2,"+919444005769");

        CardView mLayout1 = (CardView) findViewById(R.id.layout3);
        LinearLayout mLayoutToBeExpanded1 = (LinearLayout) findViewById(R.id.layout_exp_31);

        addAction1(mLayout1, mLayoutToBeExpanded1);
        collapse(mLayoutToBeExpanded1);
        ImageView imagecall31 =(ImageView) findViewById(R.id.imageView31);

        imagecall31.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhoneNumber("+914422579815");
            }
        });
        ImageView imagecall32 =(ImageView) findViewById(R.id.imageView31);

        imagecall32.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhoneNumber("+914422579816");
            }
        });

        contacts2(R.id.layout4,R.id.layout_exp_41,R.id.imageView41,R.id.imageView42,"+919677169792","+919444357587");
        contacts2(R.id.layout5,R.id.layout_exp_51,R.id.imageView51,R.id.imageView52,"+919962301415","+919952993777");
        contacts2(R.id.layout6,R.id.layout_exp_61,R.id.imageView61,R.id.imageView62,"+918754557441","+919962252801");
        contacts2(R.id.layout7,R.id.layout_exp_71,R.id.imageView71,R.id.imageView72,"+914422579810","+914422579811");






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

    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    public void addAction(final View layout, final View summary) {



        layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (summary.getVisibility() == View.GONE) {
                    expand(summary);
                } else {
                    collapse(summary);
                }
            }
        });
    }

    private void expand(View summary) {
        //set Visible
        int finalHeight = summary.getHeight();

        ValueAnimator mAnimator ;

        summary.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        summary.measure(widthSpec, 300);

        mAnimator = slideAnimator(0, 150, summary);

        mAnimator.start();
    }
    public void addAction1(final View layout, final View summary) {



        layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (summary.getVisibility() == View.GONE) {
                    expand1(summary);
                } else {
                    collapse(summary);
                }
            }
        });
    }

    private void expand1(View summary) {
        //set Visible
        int finalHeight = summary.getHeight();

        ValueAnimator mAnimator ;

        summary.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        summary.measure(widthSpec, 300);

        mAnimator = slideAnimator(0, 260, summary);

        mAnimator.start();
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
    public  void contacts(int cardview,int listview,int imageview, final String number){
        CardView mLayout1 = (CardView) findViewById(cardview);
        RelativeLayout mLayoutToBeExpanded1 = (RelativeLayout) findViewById(listview);
        ImageView imagecall =(ImageView) findViewById(imageview);
        addAction(mLayout1, mLayoutToBeExpanded1);
        collapse(mLayoutToBeExpanded1);
        imagecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhoneNumber(number);
            }
        });
    }
    public  void contacts2(int cardview,int listview,int imageview1,int imageview2, final String number1,final String number2){
        CardView mLayout1 = (CardView) findViewById(cardview);
        LinearLayout mLayoutToBeExpanded1 = (LinearLayout) findViewById(listview);

        addAction1(mLayout1, mLayoutToBeExpanded1);
        collapse(mLayoutToBeExpanded1);
        ImageView imagecall31 =(ImageView) findViewById(imageview1);

        imagecall31.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhoneNumber(number1);
            }
        });
        ImageView imagecall32 =(ImageView) findViewById(imageview2);

        imagecall32.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhoneNumber(number2);
            }
        });
    }

}

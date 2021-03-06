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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.saarang.samples.apps.iosched.BuildConfig;
import com.saarang.samples.apps.iosched.Config;
import com.saarang.samples.apps.iosched.R;
import com.saarang.samples.apps.iosched.gcm.ServerUtilities;
import com.saarang.samples.apps.iosched.io.JSONHandler;
import com.saarang.samples.apps.iosched.provider.ScheduleContract;
import com.saarang.samples.apps.iosched.sync.ConferenceDataHandler;
import com.saarang.samples.apps.iosched.sync.SyncHelper;
import com.saarang.samples.apps.iosched.ui.debug.DebugActionRunnerActivity;
import com.saarang.samples.apps.iosched.ui.widget.MultiSwipeRefreshLayout;
import com.saarang.samples.apps.iosched.ui.widget.ScrimInsetsScrollView;
import com.saarang.samples.apps.iosched.util.AccountUtils;
import com.saarang.samples.apps.iosched.util.AnalyticsManager;
import com.saarang.samples.apps.iosched.util.HelpUtils;
import com.saarang.samples.apps.iosched.util.ImageLoader;
import com.saarang.samples.apps.iosched.util.LUtils;
import com.saarang.samples.apps.iosched.util.LogUtils;
import com.saarang.samples.apps.iosched.util.LoginAndAuthHelper;
import com.saarang.samples.apps.iosched.util.PlayServicesUtils;
import com.saarang.samples.apps.iosched.util.PrefUtils;
import com.saarang.samples.apps.iosched.util.RecentTasksStyler;
import com.saarang.samples.apps.iosched.util.UIUtils;
import com.saarang.samples.apps.iosched.util.WiFiUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A base activity that handles common functionality in the app. This includes the
 * navigation drawer, login and authentication, Action Bar tweaks, amongst others.
 */
public abstract class BaseActivity extends ActionBarActivity implements
        LoginAndAuthHelper.Callbacks,
        SharedPreferences.OnSharedPreferenceChangeListener,
        MultiSwipeRefreshLayout.CanChildScrollUpCallback {
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    private static final String TAG = LogUtils.makeLogTag(BaseActivity.class);

    // the LoginAndAuthHelper handles signing in to Google Play Services and OAuth
    private LoginAndAuthHelper mLoginAndAuthHelper;

    // Navigation drawer:
    private DrawerLayout mDrawerLayout;

    // Helper methods for L APIs
    private LUtils mLUtils;

    private ObjectAnimator mStatusBarColorAnimator;
    private LinearLayout mAccountListContainer;
    private ViewGroup mDrawerItemsListContainer;
    private Handler mHandler;
    private ImageView mExpandAccountBoxIndicator;
    private boolean mAccountBoxExpanded = false;

    // When set, these components will be shown/hidden in sync with the action bar
    // to implement the "quick recall" effect (the Action Bar and the header views disappear
    // when you scroll down a list, and reappear quickly when you scroll up).
    private ArrayList<View> mHideableHeaderViews = new ArrayList<View>();

    // Durations for certain animations we use:
    private static final int HEADER_HIDE_ANIM_DURATION = 300;
    private static final int ACCOUNT_BOX_EXPAND_ANIM_DURATION = 200;

    // symbols for navdrawer items (indices must correspond to array below). This is
    // not a list of items that are necessarily *present* in the Nav Drawer; rather,
    // it's a list of all possible items.
    protected static final int NAVDRAWER_ITEM_MY_SCHEDULE = 0;
    protected static final int NAVDRAWER_ITEM_EXPLORE = 1;
    protected static final int NAVDRAWER_ITEM_MAP = 2;
    protected static final int NAVDRAWER_ITEM_SOCIAL = 3;
    protected static final int NAVDRAWER_ITEM_VIDEO_LIBRARY = 4;
    protected static final int NAVDRAWER_ITEM_SIGN_IN = 5;
    protected static final int NAVDRAWER_ITEM_SETTINGS = 8;

    protected static final int NAVDRAWER_ITEM_EXPERTS_DIRECTORY = 7;
    protected static final int NAVDRAWER_ITEM_PEOPLE_IVE_MET = 6;
    protected static final int NAVDRAWER_ITEM_INVALID = -1;
    protected static final int NAVDRAWER_ITEM_SEPARATOR = -2;
    protected static final int NAVDRAWER_ITEM_SEPARATOR_SPECIAL = -3;

    // titles for navdrawer items (indices must correspond to the above)
    private static final int[] NAVDRAWER_TITLE_RES_ID = new int[]{
            com.saarang.samples.apps.iosched.R.string.navdrawer_item_my_schedule,
            com.saarang.samples.apps.iosched.R.string.navdrawer_item_explore,
            com.saarang.samples.apps.iosched.R.string.navdrawer_item_map,
            com.saarang.samples.apps.iosched.R.string.navdrawer_item_social,
            com.saarang.samples.apps.iosched.R.string.navdrawer_item_video_library,
            com.saarang.samples.apps.iosched.R.string.navdrawer_item_sign_in,
            com.saarang.samples.apps.iosched.R.string.navdrawer_item_people_ive_met  ,
            com.saarang.samples.apps.iosched.R.string.navdrawer_item_experts_directory,
            com.saarang.samples.apps.iosched.R.string.navdrawer_item_settings
    };

    // icons for navdrawer items (indices must correspond to above array)
    private static final int[] NAVDRAWER_ICON_RES_ID = new int[] {
            com.saarang.samples.apps.iosched.R.drawable.ic_drawer_my_schedule,  // My Schedule
            com.saarang.samples.apps.iosched.R.drawable.ic_drawer_explore,  // Explore
            com.saarang.samples.apps.iosched.R.drawable.ic_drawer_map, // Map
            com.saarang.samples.apps.iosched.R.drawable.ic_drawer_social, // Social
            com.saarang.samples.apps.iosched.R.drawable.ic_drawer_video_library, // Video Library
            0, // Sign in
            com.saarang.samples.apps.iosched.R.drawable.ic_drawer_people_met,
            com.saarang.samples.apps.iosched.R.drawable.ic_drawer_experts,
            com.saarang.samples.apps.iosched.R.drawable.ic_drawer_settings,


    };

    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    // fade in and fade out durations for the main content when switching between
    // different Activities of the app through the Nav Drawer
    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;

    // list of navdrawer items that were actually added to the navdrawer, in order
    private ArrayList<Integer> mNavDrawerItems = new ArrayList<Integer>();

    // views that correspond to each navdrawer item, null if not yet created
    private View[] mNavDrawerItemViews = null;

    // SwipeRefreshLayout allows the user to swipe the screen down to trigger a manual refresh
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // Primary toolbar and drawer toggle
    private Toolbar mActionBarToolbar;

    // asynctask that performs GCM registration in the backgorund
    private AsyncTask<Void, Void, Void> mGCMRegisterTask;

    // handle to our sync observer (that notifies us about changes in our sync state)
    private Object mSyncObserverHandle;

    // data bootstrap thread. Data bootstrap is the process of initializing the database
    // with the data cache that ships with the app.
    Thread mDataBootstrapThread = null;

    // variables that control the Action Bar auto hide behavior (aka "quick recall")
    private boolean mActionBarAutoHideEnabled = false;
    private int mActionBarAutoHideSensivity = 0;
    private int mActionBarAutoHideMinY = 0;
    private int mActionBarAutoHideSignal = 0;
    private boolean mActionBarShown = true;

    // A Runnable that we should execute when the navigation drawer finishes its closing animation
    private Runnable mDeferredOnDrawerClosedRunnable;
    private boolean mManualSyncRequest;

    private int mThemedStatusBarColor;
    private int mNormalStatusBarColor;
    private int mProgressBarTopWhenActionBarShown;
    private static final TypeEvaluator ARGB_EVALUATOR = new ArgbEvaluator();
    private ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsManager.initializeAnalyticsTracker(getApplicationContext());
        RecentTasksStyler.styleRecentTasksEntry(this);

        PrefUtils.init(this);

        // Check if the EULA has been accepted; if not, show it.
        if (!PrefUtils.isTosAccepted(this)) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }

        mImageLoader = new ImageLoader(this);
        mHandler = new Handler();

        // Enable or disable each Activity depending on the form factor. This is necessary
        // because this app uses many implicit intents where we don't name the exact Activity
        // in the Intent, so there should only be one enabled Activity that handles each
        // Intent in the app.
        UIUtils.enableDisableActivitiesByFormFactor(this);

        if (savedInstanceState == null) {
            registerGCMClient();
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mLUtils = LUtils.getInstance(this);
        mThemedStatusBarColor = getResources().getColor(com.saarang.samples.apps.iosched.R.color.theme_primary_dark);
        mNormalStatusBarColor = mThemedStatusBarColor;
    }

    private void trySetupSwipeRefresh() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(com.saarang.samples.apps.iosched.R.id.swipe_refresh_layout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources(
                    com.saarang.samples.apps.iosched.R.color.refresh_progress_1,
                    com.saarang.samples.apps.iosched.R.color.refresh_progress_2,
                    com.saarang.samples.apps.iosched.R.color.refresh_progress_3);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    requestDataRefresh();
                }
            });

            if (mSwipeRefreshLayout instanceof MultiSwipeRefreshLayout) {
                MultiSwipeRefreshLayout mswrl = (MultiSwipeRefreshLayout) mSwipeRefreshLayout;
                mswrl.setCanChildScrollUpCallback(this);
            }
        }
    }

    protected void setProgressBarTopWhenActionBarShown(int progressBarTopWhenActionBarShown) {
        mProgressBarTopWhenActionBarShown = progressBarTopWhenActionBarShown;
        updateSwipeRefreshProgressBarTop();
    }

    private void updateSwipeRefreshProgressBarTop() {
        if (mSwipeRefreshLayout == null) {
            return;
        }

        int progressBarStartMargin = getResources().getDimensionPixelSize(
                com.saarang.samples.apps.iosched.R.dimen.swipe_refresh_progress_bar_start_margin);
        int progressBarEndMargin = getResources().getDimensionPixelSize(
                com.saarang.samples.apps.iosched.R.dimen.swipe_refresh_progress_bar_end_margin);
        int top = mActionBarShown ? mProgressBarTopWhenActionBarShown : 0;
        mSwipeRefreshLayout.setProgressViewOffset(false,
                top + progressBarStartMargin, top + progressBarEndMargin);
    }

    /**
     * Returns the navigation drawer item that corresponds to this Activity. Subclasses
     * of BaseActivity override this to indicate what nav drawer item corresponds to them
     * Return NAVDRAWER_ITEM_INVALID to mean that this Activity should not have a Nav Drawer.
     */
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }

    /**
     * Sets up the navigation drawer as appropriate. Note that the nav drawer will be
     * different depending on whether the attendee indicated that they are attending the
     * event on-site vs. attending remotely.
     */
    private void setupNavDrawer() {
        // What nav drawer item should be selected?
        int selfItem = getSelfNavDrawerItem();

        mDrawerLayout = (DrawerLayout) findViewById(com.saarang.samples.apps.iosched.R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }
        mDrawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(com.saarang.samples.apps.iosched.R.color.theme_primary_dark));
        ScrimInsetsScrollView navDrawer = (ScrimInsetsScrollView)
                mDrawerLayout.findViewById(com.saarang.samples.apps.iosched.R.id.navdrawer);
        if (selfItem == NAVDRAWER_ITEM_INVALID) {
            // do not show a nav drawer
            if (navDrawer != null) {
                ((ViewGroup) navDrawer.getParent()).removeView(navDrawer);
            }
            mDrawerLayout = null;
            return;
        }

        if (navDrawer != null) {
            final View chosenAccountContentView = findViewById(com.saarang.samples.apps.iosched.R.id.chosen_account_content_view);
            final View chosenAccountView = findViewById(com.saarang.samples.apps.iosched.R.id.chosen_account_view);
            final int navDrawerChosenAccountHeight = getResources().getDimensionPixelSize(
                    com.saarang.samples.apps.iosched.R.dimen.navdrawer_chosen_account_height);
            navDrawer.setOnInsetsCallback(new ScrimInsetsScrollView.OnInsetsCallback() {
                @Override
                public void onInsetsChanged(Rect insets) {
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)
                            chosenAccountContentView.getLayoutParams();
                    lp.topMargin = insets.top;
                    chosenAccountContentView.setLayoutParams(lp);

                    ViewGroup.LayoutParams lp2 = chosenAccountView.getLayoutParams();
                    lp2.height = navDrawerChosenAccountHeight + insets.top;
                    chosenAccountView.setLayoutParams(lp2);
                }
            });
        }

        if (mActionBarToolbar != null) {
            mActionBarToolbar.setNavigationIcon(com.saarang.samples.apps.iosched.R.drawable.ic_drawer);
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            });
        }

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                // run deferred action, if we have one
                if (mDeferredOnDrawerClosedRunnable != null) {
                    mDeferredOnDrawerClosedRunnable.run();
                    mDeferredOnDrawerClosedRunnable = null;
                }
                if (mAccountBoxExpanded) {
                    mAccountBoxExpanded = false;
                    setupAccountBoxToggle();
                }
                onNavDrawerStateChanged(false, false);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                onNavDrawerStateChanged(true, false);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                onNavDrawerStateChanged(isNavDrawerOpen(), newState != DrawerLayout.STATE_IDLE);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                onNavDrawerSlide(slideOffset);
            }
        });

        mDrawerLayout.setDrawerShadow(com.saarang.samples.apps.iosched.R.drawable.drawer_shadow, Gravity.START);

        // populate the nav drawer with the correct items
        populateNavDrawer();

        // When the user runs the app for the first time, we want to land them with the
        // navigation drawer open. But just the first time.
        if (!PrefUtils.isWelcomeDone(this)) {
            // first run of the app starts with the nav drawer open
            PrefUtils.markWelcomeDone(this);
            mDrawerLayout.openDrawer(Gravity.START);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }

    // Subclasses can override this for custom behavior
    protected void onNavDrawerStateChanged(boolean isOpen, boolean isAnimating) {
        if (mActionBarAutoHideEnabled && isOpen) {
            autoShowOrHideActionBar(true);
        }
    }

    protected void onNavDrawerSlide(float offset) {}

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.START);
    }

    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(Gravity.START);
        }
    }

    /** Populates the navigation drawer with the appropriate items. */
    private void populateNavDrawer() {
        boolean attendeeAtVenue = true;
        mNavDrawerItems.clear();

        // decide which items will appear in the nav drawer
        if (AccountUtils.hasActiveAccount(this)) {
            // Only logged-in users can save sessions, so if there is no active account,
            // there is no My Schedule
            mNavDrawerItems.add(NAVDRAWER_ITEM_MY_SCHEDULE);
        } else {
            // If no active account, show Sign In
            mNavDrawerItems.add(NAVDRAWER_ITEM_SIGN_IN);
        }

        // Explore is always shown
        mNavDrawerItems.add(NAVDRAWER_ITEM_EXPLORE);
       // mNavDrawerItems.add(NAVDRAWER_ITEM_PEOPLE_IVE_MET);

        // If the attendee is on-site, show Map on the nav drawer
        if (attendeeAtVenue) {
            mNavDrawerItems.add(NAVDRAWER_ITEM_MAP);
        }
        if (attendeeAtVenue) {
            mNavDrawerItems.add(NAVDRAWER_ITEM_PEOPLE_IVE_MET);
        }

 /*       // If attendee is on-site, show the People I've Met item
        if (attendeeAtVenue) {
            mNavDrawerItems.add(NAVDRAWER_ITEM_PEOPLE_IVE_MET);
        }

        // If the experts directory hasn't expired, show it


        // Other items that are always in the nav drawer irrespective of whether the
        // attendee is on-site or remote:
        mNavDrawerItems.add(NAVDRAWER_ITEM_SOCIAL);
        mNavDrawerItems.add(NAVDRAWER_ITEM_VIDEO_LIBRARY);*/

        //mNavDrawerItems.add(NAVDRAWER_ITEM_SETTINGS);

        mNavDrawerItems.add(NAVDRAWER_ITEM_EXPERTS_DIRECTORY);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SETTINGS);

        createNavDrawerItems();
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private void createNavDrawerItems() {
        mDrawerItemsListContainer = (ViewGroup) findViewById(com.saarang.samples.apps.iosched.R.id.navdrawer_items_list);
        if (mDrawerItemsListContainer == null) {
            return;
        }

        mNavDrawerItemViews = new View[mNavDrawerItems.size()];
        mDrawerItemsListContainer.removeAllViews();
        int i = 0;
        for (int itemId : mNavDrawerItems) {
            mNavDrawerItemViews[i] = makeNavDrawerItem(itemId, mDrawerItemsListContainer);
            mDrawerItemsListContainer.addView(mNavDrawerItemViews[i]);
            ++i;
        }
    }

    /**
     * Sets up the given navdrawer item's appearance to the selected state. Note: this could
     * also be accomplished (perhaps more cleanly) with state-based layouts.
     */
    private void setSelectedNavDrawerItem(int itemId) {
        if (mNavDrawerItemViews != null) {
            for (int i = 0; i < mNavDrawerItemViews.length; i++) {
                if (i < mNavDrawerItems.size()) {
                    int thisItemId = mNavDrawerItems.get(i);
                    formatNavDrawerItem(mNavDrawerItemViews[i], thisItemId, itemId == thisItemId);
                }
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PrefUtils.PREF_ATTENDEE_AT_VENUE)) {
            LogUtils.LOGD(TAG, "Attendee at venue preference changed, repopulating nav drawer and menu.");
            populateNavDrawer();
            invalidateOptionsMenu();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
      setupNavDrawer();
        setupAccountBox();

        trySetupSwipeRefresh();
        updateSwipeRefreshProgressBarTop();

        View mainContent = findViewById(com.saarang.samples.apps.iosched.R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        } else {
            LogUtils.LOGW(TAG, "No view with ID main_content to fade in.");
        }
    }

    /**
     * Sets up the account box. The account box is the area at the top of the nav drawer that
     * shows which account the user is logged in as, and lets them switch accounts. It also
     * shows the user's Google+ cover photo as background.
     */
    private void setupAccountBox() {
        mAccountListContainer = (LinearLayout) findViewById(com.saarang.samples.apps.iosched.R.id.account_list);

        if (mAccountListContainer == null) {
            //This activity does not have an account box
            return;
        }

        final View chosenAccountView = findViewById(com.saarang.samples.apps.iosched.R.id.chosen_account_view);
        Account chosenAccount = AccountUtils.getActiveAccount(this);
        if (chosenAccount == null) {
            // No account logged in; hide account box
            chosenAccountView.setVisibility(View.GONE);
            mAccountListContainer.setVisibility(View.GONE);
            return;
        } else {
            chosenAccountView.setVisibility(View.VISIBLE);
            mAccountListContainer.setVisibility(View.INVISIBLE);
        }

        AccountManager am = AccountManager.get(this);
        Account[] accountArray = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        List<Account> accounts = new ArrayList<Account>(Arrays.asList(accountArray));
        accounts.remove(chosenAccount);

        ImageView coverImageView = (ImageView) chosenAccountView.findViewById(com.saarang.samples.apps.iosched.R.id.profile_cover_image);
        ImageView profileImageView = (ImageView) chosenAccountView.findViewById(com.saarang.samples.apps.iosched.R.id.profile_image);
        TextView nameTextView = (TextView) chosenAccountView.findViewById(com.saarang.samples.apps.iosched.R.id.profile_name_text);
        TextView email = (TextView) chosenAccountView.findViewById(com.saarang.samples.apps.iosched.R.id.profile_email_text);
        mExpandAccountBoxIndicator = (ImageView) findViewById(com.saarang.samples.apps.iosched.R.id.expand_account_box_indicator);

        String name = AccountUtils.getPlusName(this);
        if (name == null) {
            nameTextView.setVisibility(View.GONE);
        } else {
            nameTextView.setVisibility(View.VISIBLE);
            nameTextView.setText(name);
        }

        String imageUrl = AccountUtils.getPlusImageUrl(this);
        if (imageUrl != null) {
            mImageLoader.loadImage(imageUrl, profileImageView);
        }

        String coverImageUrl = AccountUtils.getPlusCoverUrl(this);
        if (coverImageUrl != null) {
            mImageLoader.loadImage(coverImageUrl, coverImageView);
        } else {
            coverImageView.setImageResource(R.drawable.bg_cover_2016);
        }

        email.setText(chosenAccount.name);

        if (accounts.isEmpty()) {
            // There's only one account on the device, so no need for a switcher.
            mExpandAccountBoxIndicator.setVisibility(View.GONE);
            mAccountListContainer.setVisibility(View.GONE);
            chosenAccountView.setEnabled(false);
            return;
        }

        chosenAccountView.setEnabled(true);

        mExpandAccountBoxIndicator.setVisibility(View.VISIBLE);
        chosenAccountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAccountBoxExpanded = !mAccountBoxExpanded;
                setupAccountBoxToggle();
            }
        });
        setupAccountBoxToggle();

        populateAccountList(accounts);
    }

    private void populateAccountList(List<Account> accounts) {
        mAccountListContainer.removeAllViews();

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        for (Account account : accounts) {
            View itemView = layoutInflater.inflate(com.saarang.samples.apps.iosched.R.layout.list_item_account,
                    mAccountListContainer, false);
            ((TextView) itemView.findViewById(com.saarang.samples.apps.iosched.R.id.profile_email_text))
                    .setText(account.name);
            final String accountName = account.name;
            String imageUrl = AccountUtils.getPlusImageUrl(this, accountName);
            if (!TextUtils.isEmpty(imageUrl)) {
                mImageLoader.loadImage(imageUrl,
                        (ImageView) itemView.findViewById(com.saarang.samples.apps.iosched.R.id.profile_image));
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConnectivityManager cm = (ConnectivityManager)
                            getSystemService(CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    if (activeNetwork == null || !activeNetwork.isConnected()) {
                        // if there's no network, don't try to change the selected account
                        Toast.makeText(BaseActivity.this, com.saarang.samples.apps.iosched.R.string.no_connection_cant_login,
                                Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawer(Gravity.START);
                        return;
                    } else {
                        LogUtils.LOGD(TAG, "User requested switch to account: " + accountName);
                        AccountUtils.setActiveAccount(BaseActivity.this, accountName);
                        onAccountChangeRequested();
                        startLoginProcess();
                        mAccountBoxExpanded = false;
                        setupAccountBoxToggle();
                        mDrawerLayout.closeDrawer(Gravity.START);
                        setupAccountBox();
                    }
                }
            });
            mAccountListContainer.addView(itemView);
        }
    }

    protected void onAccountChangeRequested() {
        // override if you want to be notified when another account has been selected account has changed
    }

    private void setupAccountBoxToggle() {
        int selfItem = getSelfNavDrawerItem();
        if (mDrawerLayout == null || selfItem == NAVDRAWER_ITEM_INVALID) {
            // this Activity does not have a nav drawer
            return;
        }
        mExpandAccountBoxIndicator.setImageResource(mAccountBoxExpanded
                ? com.saarang.samples.apps.iosched.R.drawable.ic_drawer_accounts_collapse
                : com.saarang.samples.apps.iosched.R.drawable.ic_drawer_accounts_expand);
        int hideTranslateY = -mAccountListContainer.getHeight() / 4; // last 25% of animation
        if (mAccountBoxExpanded && mAccountListContainer.getTranslationY() == 0) {
            // initial setup
            mAccountListContainer.setAlpha(0);
            mAccountListContainer.setTranslationY(hideTranslateY);
        }

        AnimatorSet set = new AnimatorSet();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mDrawerItemsListContainer.setVisibility(mAccountBoxExpanded
                        ? View.INVISIBLE : View.VISIBLE);
                mAccountListContainer.setVisibility(mAccountBoxExpanded
                        ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                onAnimationEnd(animation);
            }
        });

        if (mAccountBoxExpanded) {
            mAccountListContainer.setVisibility(View.VISIBLE);
            AnimatorSet subSet = new AnimatorSet();
            subSet.playTogether(
                    ObjectAnimator.ofFloat(mAccountListContainer, View.ALPHA, 1)
                            .setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION),
                    ObjectAnimator.ofFloat(mAccountListContainer, View.TRANSLATION_Y, 0)
                            .setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION));
            set.playSequentially(
                    ObjectAnimator.ofFloat(mDrawerItemsListContainer, View.ALPHA, 0)
                            .setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION),
                    subSet);
            set.start();
        } else {
            mDrawerItemsListContainer.setVisibility(View.VISIBLE);
            AnimatorSet subSet = new AnimatorSet();
            subSet.playTogether(
                    ObjectAnimator.ofFloat(mAccountListContainer, View.ALPHA, 0)
                            .setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION),
                    ObjectAnimator.ofFloat(mAccountListContainer, View.TRANSLATION_Y,
                            hideTranslateY)
                            .setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION));
            set.playSequentially(
                    subSet,
                    ObjectAnimator.ofFloat(mDrawerItemsListContainer, View.ALPHA, 1)
                            .setDuration(ACCOUNT_BOX_EXPAND_ANIM_DURATION));
            set.start();
        }

        set.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case com.saarang.samples.apps.iosched.R.id.menu_about:
                HelpUtils.showAbout(this);
                return true;

            case com.saarang.samples.apps.iosched.R.id.menu_wifi:
                WiFiUtils.showWiFiDialog(this);
                return true;

            /*case com.saarang.samples.apps.iosched.R.id.menu_i_o_hunt:
                launchIoHunt();
                return true;*/

            case com.saarang.samples.apps.iosched.R.id.menu_debug:
                if (BuildConfig.DEBUG) {
                    startActivity(new Intent(this, DebugActionRunnerActivity.class));
                }
                return true;

            case com.saarang.samples.apps.iosched.R.id.menu_refresh:
                requestDataRefresh();
                break;

            case com.saarang.samples.apps.iosched.R.id.menu_io_extended:
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(Config.IO_EXTENDED_LINK)));
                break;

            case com.saarang.samples.apps.iosched.R.id.menu_map:
                startActivity(new Intent(this, UIUtils.getMapActivityClass(this)));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchIoHunt() {
        if (!TextUtils.isEmpty(Config.IO_HUNT_PACKAGE_NAME)) {
            LogUtils.LOGD(TAG, "Attempting to launch I/O hunt.");
            PackageManager pm = getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage(Config.IO_HUNT_PACKAGE_NAME);
            if (launchIntent != null) {
                // start I/O Hunt
                LogUtils.LOGD(TAG, "I/O hunt intent found, launching.");
                startActivity(launchIntent);
            } else {
                // send user to the Play Store to download it
                LogUtils.LOGD(TAG, "I/O hunt intent NOT found, going to Play Store.");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        Config.PLAY_STORE_URL_PREFIX + Config.IO_HUNT_PACKAGE_NAME));
                startActivity(intent);
            }
        }
    }

    protected void requestDataRefresh() {
        Account activeAccount = AccountUtils.getActiveAccount(this);
        ContentResolver contentResolver = getContentResolver();
        if (contentResolver.isSyncActive(activeAccount, ScheduleContract.CONTENT_AUTHORITY)) {
            LogUtils.LOGD(TAG, "Ignoring manual sync request because a sync is already in progress.");
            return;
        }
        mManualSyncRequest = true;
        LogUtils.LOGD(TAG, "Requesting manual data refresh.");
        SyncHelper.requestManualSync(activeAccount);
    }

    private void goToNavDrawerItem(int item) {
        Intent intent;
        switch (item) {
            case NAVDRAWER_ITEM_MY_SCHEDULE:
                intent = new Intent(this, MyScheduleActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAVDRAWER_ITEM_EXPLORE:
                intent = new Intent(this, BrowseSessionsActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAVDRAWER_ITEM_MAP:
                intent = new Intent(this, UIUtils.getMapActivityClass(this));
                startActivity(intent);
                finish();
                break;

            case NAVDRAWER_ITEM_SOCIAL:
                intent = new Intent(this, SocialActivity.class);
                startActivity(intent);
                finish();
                break;



            case NAVDRAWER_ITEM_EXPERTS_DIRECTORY:
                intent = new Intent(this, ExpertsDirectoryActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAVDRAWER_ITEM_PEOPLE_IVE_MET:
                Log.d(TAG, "Clicked People I've met");
                intent = new Intent(this, PeopleIveMetActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAVDRAWER_ITEM_SIGN_IN:
                signInOrCreateAnAccount();
                break;
            case NAVDRAWER_ITEM_SETTINGS:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            case NAVDRAWER_ITEM_VIDEO_LIBRARY:
                intent = new Intent(this, VideoLibraryActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void signInOrCreateAnAccount() {
        //Get list of accounts on device.
        AccountManager am = AccountManager.get(BaseActivity.this);
        Account[] accountArray = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        if (accountArray.length == 0) {
            //Send the user to the "Add Account" page.
            Intent intent = new Intent(Settings.ACTION_ADD_ACCOUNT);
            intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[] {"com.google"});
            startActivity(intent);
        } else {
            //Try to log the user in with the first account on the device.
            startLoginProcess();
            mDrawerLayout.closeDrawer(Gravity.START);
        }
    }

    private void onNavDrawerItemClicked(final int itemId) {
        if (itemId == getSelfNavDrawerItem()) {
            mDrawerLayout.closeDrawer(Gravity.START);
            return;
        }

        if (isSpecialItem(itemId)) {
            goToNavDrawerItem(itemId);
        } else {
            // launch the target Activity after a short delay, to allow the close animation to play
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToNavDrawerItem(itemId);
                }
            }, NAVDRAWER_LAUNCH_DELAY);

            // change the active item on the list so the user can see the item changed
            setSelectedNavDrawerItem(itemId);
            // fade out the main content
            View mainContent = findViewById(com.saarang.samples.apps.iosched.R.id.main_content);
            if (mainContent != null) {
                mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
            }
        }

        mDrawerLayout.closeDrawer(Gravity.START);
    }

    protected void configureStandardMenuItems(Menu menu) {
        // if attendee is remote, show map on the overflow instead of on the nav bar
        final boolean isRemote = !PrefUtils.isAttendeeAtVenue(this);
        final MenuItem mapItem = menu.findItem(com.saarang.samples.apps.iosched.R.id.menu_map);
        if (mapItem != null) {
            mapItem.setVisible(isRemote);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Verifies the proper version of Google Play Services exists on the device.
        PlayServicesUtils.checkGooglePlaySevices(this);

        // Watch for sync state changes
        mSyncStatusObserver.onStatusChanged(0);
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }
    }

    /**
     * Converts an intent into a {@link Bundle} suitable for use as fragment arguments.
     */
    public static Bundle intentToFragmentArguments(Intent intent) {
        Bundle arguments = new Bundle();
        if (intent == null) {
            return arguments;
        }

        final Uri data = intent.getData();
        if (data != null) {
            arguments.putParcelable("_uri", data);
        }

        final Bundle extras = intent.getExtras();
        if (extras != null) {
            arguments.putAll(intent.getExtras());
        }

        return arguments;
    }

    /**
     * Converts a fragment arguments bundle into an intent.
     */
    public static Intent fragmentArgumentsToIntent(Bundle arguments) {
        Intent intent = new Intent();
        if (arguments == null) {
            return intent;
        }

        final Uri data = arguments.getParcelable("_uri");
        if (data != null) {
            intent.setData(data);
        }

        intent.putExtras(arguments);
        intent.removeExtra("_uri");
        return intent;
    }

    @Override
    public void onStart() {
        LogUtils.LOGD(TAG, "onStart");
        super.onStart();
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // Perform one-time bootstrap setup, if needed
        if (!PrefUtils.isDataBootstrapDone(this) && mDataBootstrapThread == null) {

                performDataBootstrap();
            }
        startLoginProcess();
    }
    /*
    @Override
    public void onStart() {
        LogUtils.LOGD(TAG, "onStart");
        super.onStart();
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        // Perform one-time bootstrap setup, if needed
        if (!PrefUtils.isDataBootstrapDone(this) && mDataBootstrapThread == null) {
            if(isInternetPresent){
                requestDataRefresh();
            }
            else{
                AlertDialog alertDialog = new AlertDialog.Builder(BaseActivity.this).create();
                alertDialog.setMessage("The data you get may be outdated\nConnect to internet & refresh data.");
                alertDialog.setTitle("No Internet Connection");
                alertDialog.setIcon(R.drawable.ic_error);
                alertDialog.setButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.show();
            LogUtils.LOGD(TAG, "One-time data bootstrap not done yet. Doing now.");
            performDataBootstrap();
        }}
        startLoginProcess();
    }*/


    /**
     * Performs the one-time data bootstrap. This means taking our prepackaged conference data
     * from the R.raw.bootstrap_data resource, and parsing it to populate the database. This
     * data contains the sessions, speakers, etc.
     */
    private void performDataBootstrap() {
        final Context appContext = getApplicationContext();
        LogUtils.LOGD(TAG, "Starting data bootstrap background thread.");
        mDataBootstrapThread = new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtils.LOGD(TAG, "Starting data bootstrap process.");
                try {
                    // Load data from bootstrap raw resource
                    String bootstrapJson = JSONHandler.parseResource(appContext, com.saarang.samples.apps.iosched.R.raw.bootstrap_data);

                    // Apply the data we read to the database with the help of the ConferenceDataHandler
                    ConferenceDataHandler dataHandler = new ConferenceDataHandler(appContext);
                    dataHandler.applyConferenceData(new String[]{bootstrapJson},
                            Config.BOOTSTRAP_DATA_TIMESTAMP, false);
                    SyncHelper.performPostSyncChores(appContext);
                    LogUtils.LOGI(TAG, "End of bootstrap -- successful. Marking boostrap as done.");
                    PrefUtils.markSyncSucceededNow(appContext);
                    PrefUtils.markDataBootstrapDone(appContext);
                    getContentResolver().notifyChange(Uri.parse(ScheduleContract.CONTENT_AUTHORITY),
                            null, false);
                } catch (IOException ex) {
                    // This is serious -- if this happens, the app won't work :-(
                    // This is unlikely to happen in production, but IF it does, we apply
                    // this workaround as a fallback: we pretend we managed to do the bootstrap
                    // and hope that a remote sync will work.
                    LogUtils.LOGE(TAG, "*** ERROR DURING BOOTSTRAP! Problem in bootstrap data?");
                    LogUtils.LOGE(TAG, "Applying fallback -- marking boostrap as done; sync might fix problem.");
                    PrefUtils.markDataBootstrapDone(appContext);
                }

                mDataBootstrapThread = null;

                // Request a manual sync immediately after the bootstrapping process, in case we
                // have an active connection. Otherwise, the scheduled sync could take a while.
                SyncHelper.requestManualSync(AccountUtils.getActiveAccount(appContext));
            }
        });
        mDataBootstrapThread.start();
    }

    /**
     * Returns the default account on the device. We use the rule that the first account
     * should be the default. It's arbitrary, but the alternative would be showing an account
     * chooser popup which wouldn't be a smooth first experience with the app. Since the user
     * can easily switch the account with the nav drawer, we opted for this implementation.
     */
    private String getDefaultAccount() {
        // Choose first account on device.
        LogUtils.LOGD(TAG, "Choosing default account (first account on device)");
        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        if (accounts.length == 0) {
            // No Google accounts on device.
            LogUtils.LOGW(TAG, "No Google accounts on device; not setting default account.");
            return null;
        }

        LogUtils.LOGD(TAG, "Default account is: " + accounts[0].name);
        return accounts[0].name;
    }


    private void complainMustHaveGoogleAccount() {
        LogUtils.LOGD(TAG, "Complaining about missing Google account.");
        new AlertDialog.Builder(this)
                .setTitle(com.saarang.samples.apps.iosched.R.string.google_account_required_title)
                .setMessage(com.saarang.samples.apps.iosched.R.string.google_account_required_message)
                .setPositiveButton(com.saarang.samples.apps.iosched.R.string.add_account, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        promptAddAccount();
                    }
                })
                .setNegativeButton(com.saarang.samples.apps.iosched.R.string.not_now, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    private void promptAddAccount() {
        Intent intent = new Intent(Settings.ACTION_ADD_ACCOUNT);
        intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[]{"com.google"});
        startActivity(intent);
        finish();
    }

    private void startLoginProcess() {
        LogUtils.LOGD(TAG, "Starting login process.");
        if (!AccountUtils.hasActiveAccount(this)) {
            LogUtils.LOGD(TAG, "No active account, attempting to pick a default.");
            String defaultAccount = getDefaultAccount();
            if (defaultAccount == null) {
                LogUtils.LOGE(TAG, "Failed to pick default account (no accounts). Failing.");
                complainMustHaveGoogleAccount();
                return;
            }
            LogUtils.LOGD(TAG, "Default to: " + defaultAccount);
            AccountUtils.setActiveAccount(this, defaultAccount);
        }

        if (!AccountUtils.hasActiveAccount(this)) {
            LogUtils.LOGD(TAG, "Can't proceed with login -- no account chosen.");
            return;
        } else {
            LogUtils.LOGD(TAG, "Chosen account: " + AccountUtils.getActiveAccountName(this));
        }

        String accountName = AccountUtils.getActiveAccountName(this);
        LogUtils.LOGD(TAG, "Chosen account: " + AccountUtils.getActiveAccountName(this));

        if (mLoginAndAuthHelper != null && mLoginAndAuthHelper.getAccountName().equals(accountName)) {
            LogUtils.LOGD(TAG, "Helper already set up; simply starting it.");
            mLoginAndAuthHelper.start();
            return;
        }

        LogUtils.LOGD(TAG, "Starting login process with account " + accountName);

        if (mLoginAndAuthHelper != null) {
            LogUtils.LOGD(TAG, "Tearing down old Helper, was " + mLoginAndAuthHelper.getAccountName());
            if (mLoginAndAuthHelper.isStarted()) {
                LogUtils.LOGD(TAG, "Stopping old Helper");
                mLoginAndAuthHelper.stop();
            }
            mLoginAndAuthHelper = null;
        }

        LogUtils.LOGD(TAG, "Creating and starting new Helper with account: " + accountName);
        mLoginAndAuthHelper = new LoginAndAuthHelper(this, this, accountName);
        mLoginAndAuthHelper.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mLoginAndAuthHelper == null || !mLoginAndAuthHelper.onActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStop() {
        LogUtils.LOGD(TAG, "onStop");
        super.onStop();
        if (mLoginAndAuthHelper != null) {
            mLoginAndAuthHelper.stop();
        }
    }

    @Override
    public void onPlusInfoLoaded(String accountName) {
        setupAccountBox();
        populateNavDrawer();
    }

    /**
     * Called when authentication succeeds. This may either happen because the user just
     * authenticated for the first time (and went through the sign in flow), or because it's
     * a returning user.
     * @param accountName name of the account that just authenticated successfully.
     * @param newlyAuthenticated If true, this user just authenticated for the first time.
     * If false, it's a returning user.
     */
    @Override
    public void onAuthSuccess(String accountName, boolean newlyAuthenticated) {
        Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        LogUtils.LOGD(TAG, "onAuthSuccess, account " + accountName + ", newlyAuthenticated=" + newlyAuthenticated);

        refreshAccountDependantData();

        if (newlyAuthenticated) {
            LogUtils.LOGD(TAG, "Enabling auto sync on content provider for account " + accountName);
            SyncHelper.updateSyncInterval(this, account);
            SyncHelper.requestManualSync(account);
        }

        setupAccountBox();
        populateNavDrawer();
        registerGCMClient();
    }

    @Override
    public void onAuthFailure(String accountName) {
        LogUtils.LOGD(TAG, "Auth failed for account " + accountName);
        refreshAccountDependantData();
    }

    protected void refreshAccountDependantData() {
        // Force local data refresh for data that depends on the logged user:
        LogUtils.LOGD(TAG, "Refreshing MySchedule data");
        getContentResolver().notifyChange(ScheduleContract.MySchedule.CONTENT_URI, null, false);
    }

    protected void retryAuth() {
        mLoginAndAuthHelper.retryAuthByUserRequest();
    }

    /**
     * Initializes the Action Bar auto-hide (aka Quick Recall) effect.
     */
    private void initActionBarAutoHide() {
        mActionBarAutoHideEnabled = true;
        mActionBarAutoHideMinY = getResources().getDimensionPixelSize(
                com.saarang.samples.apps.iosched.R.dimen.action_bar_auto_hide_min_y);
        mActionBarAutoHideSensivity = getResources().getDimensionPixelSize(
                com.saarang.samples.apps.iosched.R.dimen.action_bar_auto_hide_sensivity);
    }

    /**
     * Indicates that the main content has scrolled (for the purposes of showing/hiding
     * the action bar for the "action bar auto hide" effect). currentY and deltaY may be exact
     * (if the underlying view supports it) or may be approximate indications:
     * deltaY may be INT_MAX to mean "scrolled forward indeterminately" and INT_MIN to mean
     * "scrolled backward indeterminately".  currentY may be 0 to mean "somewhere close to the
     * start of the list" and INT_MAX to mean "we don't know, but not at the start of the list"
     */
    private void onMainContentScrolled(int currentY, int deltaY) {
        if (deltaY > mActionBarAutoHideSensivity) {
            deltaY = mActionBarAutoHideSensivity;
        } else if (deltaY < -mActionBarAutoHideSensivity) {
            deltaY = -mActionBarAutoHideSensivity;
        }

        if (Math.signum(deltaY) * Math.signum(mActionBarAutoHideSignal) < 0) {
            // deltaY is a motion opposite to the accumulated signal, so reset signal
            mActionBarAutoHideSignal = deltaY;
        } else {
            // add to accumulated signal
            mActionBarAutoHideSignal += deltaY;
        }

        boolean shouldShow = currentY < mActionBarAutoHideMinY ||
                (mActionBarAutoHideSignal <= -mActionBarAutoHideSensivity);
        autoShowOrHideActionBar(shouldShow);
    }

    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(com.saarang.samples.apps.iosched.R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }

    protected void autoShowOrHideActionBar(boolean show) {
        if (show == mActionBarShown) {
            return;
        }

        mActionBarShown = show;
        onActionBarAutoShowOrHide(show);
    }

    protected void enableActionBarAutoHide(final ListView listView) {
        initActionBarAutoHide();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            final static int ITEMS_THRESHOLD = 3;
            int lastFvi = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                onMainContentScrolled(firstVisibleItem <= ITEMS_THRESHOLD ? 0 : Integer.MAX_VALUE,
                        lastFvi - firstVisibleItem > 0 ? Integer.MIN_VALUE :
                                lastFvi == firstVisibleItem ? 0 : Integer.MAX_VALUE
                );
                lastFvi = firstVisibleItem;
            }
        });
    }

    private View makeNavDrawerItem(final int itemId, ViewGroup container) {
        boolean selected = getSelfNavDrawerItem() == itemId;
        int layoutToInflate = 0;
        if (itemId == NAVDRAWER_ITEM_SEPARATOR) {
            layoutToInflate = com.saarang.samples.apps.iosched.R.layout.navdrawer_separator;
        } else if (itemId == NAVDRAWER_ITEM_SEPARATOR_SPECIAL) {
            layoutToInflate = com.saarang.samples.apps.iosched.R.layout.navdrawer_separator;
        } else {
            layoutToInflate = com.saarang.samples.apps.iosched.R.layout.navdrawer_item;
        }
        View view = getLayoutInflater().inflate(layoutToInflate, container, false);

        if (isSeparator(itemId)) {
            // we are done
            UIUtils.setAccessibilityIgnore(view);
            return view;
        }

        ImageView iconView = (ImageView) view.findViewById(com.saarang.samples.apps.iosched.R.id.icon);
        TextView titleView = (TextView) view.findViewById(com.saarang.samples.apps.iosched.R.id.title);
        int iconId = itemId >= 0 && itemId < NAVDRAWER_ICON_RES_ID.length ?
                NAVDRAWER_ICON_RES_ID[itemId] : 0;
        int titleId = itemId >= 0 && itemId < NAVDRAWER_TITLE_RES_ID.length ?
                NAVDRAWER_TITLE_RES_ID[itemId] : 0;

        // set icon and text
        iconView.setVisibility(iconId > 0 ? View.VISIBLE : View.GONE);
        if (iconId > 0) {
            iconView.setImageResource(iconId);
        }
        titleView.setText(getString(titleId));

        formatNavDrawerItem(view, itemId, selected);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavDrawerItemClicked(itemId);
            }
        });

        return view;
    }

    private boolean isSpecialItem(int itemId) {
        return itemId == NAVDRAWER_ITEM_SETTINGS;
    }

    private boolean isSeparator(int itemId) {
        return itemId == NAVDRAWER_ITEM_SEPARATOR || itemId == NAVDRAWER_ITEM_SEPARATOR_SPECIAL;
    }

    private void formatNavDrawerItem(View view, int itemId, boolean selected) {
        if (isSeparator(itemId)) {
            // not applicable
            return;
        }

        ImageView iconView = (ImageView) view.findViewById(com.saarang.samples.apps.iosched.R.id.icon);
        TextView titleView = (TextView) view.findViewById(com.saarang.samples.apps.iosched.R.id.title);

        // configure its appearance according to whether or not it's selected
        titleView.setTextColor(selected ?
                getResources().getColor(com.saarang.samples.apps.iosched.R.color.navdrawer_text_color_selected) :
                getResources().getColor(com.saarang.samples.apps.iosched.R.color.navdrawer_text_color));
        iconView.setColorFilter(selected ?
                getResources().getColor(com.saarang.samples.apps.iosched.R.color.navdrawer_icon_tint_selected) :
                getResources().getColor(com.saarang.samples.apps.iosched.R.color.navdrawer_icon_tint));
    }

    /** Registers device on the GCM server, if necessary. */
    private void registerGCMClient() {
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);

        final String regId = GCMRegistrar.getRegistrationId(this);

        if (TextUtils.isEmpty(regId)) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, Config.GCM_SENDER_ID);

        } else {
            // Get the correct GCM key for the user. GCM key is a somewhat non-standard
            // approach we use in this app. For more about this, check GCM.MD.
            final String gcmKey = AccountUtils.hasActiveAccount(this) ?
                    AccountUtils.getGcmKey(this, AccountUtils.getActiveAccountName(this)) : null;
            // Device is already registered on GCM, needs to check if it is
            // registered on our server as well.
            if (ServerUtilities.isRegisteredOnServer(this, gcmKey)) {
                // Skips registration.
                LogUtils.LOGI(TAG, "Already registered on the GCM server with right GCM key.");
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                mGCMRegisterTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        LogUtils.LOGI(TAG, "Registering on the GCM server with GCM key: "
                                + AccountUtils.sanitizeGcmKey(gcmKey));
                        boolean registered = ServerUtilities.register(BaseActivity.this,
                                regId, gcmKey);
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered) {
                            LogUtils.LOGI(TAG, "GCM registration failed.");
                            GCMRegistrar.unregister(BaseActivity.this);
                        } else {
                            LogUtils.LOGI(TAG, "GCM registration successful.");
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mGCMRegisterTask = null;
                    }
                };
                mGCMRegisterTask.execute(null, null, null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mGCMRegisterTask != null) {
            LogUtils.LOGD(TAG, "Cancelling GCM registration task.");
            mGCMRegisterTask.cancel(true);
        }

        try {
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            LogUtils.LOGW(TAG, "C2DM unregistration error", e);
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        @Override
        public void onStatusChanged(int which) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String accountName = AccountUtils.getActiveAccountName(BaseActivity.this);
                    if (TextUtils.isEmpty(accountName)) {
                        onRefreshingStateChanged(false);
                        mManualSyncRequest = false;
                        return;
                    }

                    Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                    boolean syncActive = ContentResolver.isSyncActive(
                            account, ScheduleContract.CONTENT_AUTHORITY);
                    boolean syncPending = ContentResolver.isSyncPending(
                            account, ScheduleContract.CONTENT_AUTHORITY);
                    if (!syncActive && !syncPending) {
                        mManualSyncRequest = false;
                    }
                    onRefreshingStateChanged(syncActive || (mManualSyncRequest && syncPending));
                }
            });
        }
    };

    protected void onRefreshingStateChanged(boolean refreshing) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    protected void enableDisableSwipeRefresh(boolean enable) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setEnabled(enable);
        }
    }

    protected void registerHideableHeaderView(View hideableHeaderView) {
        if (!mHideableHeaderViews.contains(hideableHeaderView)) {
            mHideableHeaderViews.add(hideableHeaderView);
        }
    }

    protected void deregisterHideableHeaderView(View hideableHeaderView) {
        if (mHideableHeaderViews.contains(hideableHeaderView)) {
            mHideableHeaderViews.remove(hideableHeaderView);
        }
    }

    public LUtils getLUtils() {
        return mLUtils;
    }

    public int getThemedStatusBarColor() {
        return mThemedStatusBarColor;
    }

    public void setNormalStatusBarColor(int color) {
        mNormalStatusBarColor = color;
        if (mDrawerLayout != null) {
            mDrawerLayout.setStatusBarBackgroundColor(mNormalStatusBarColor);
        }
    }

    protected void onActionBarAutoShowOrHide(boolean shown) {
        if (mStatusBarColorAnimator != null) {
            mStatusBarColorAnimator.cancel();
        }
        mStatusBarColorAnimator = ObjectAnimator.ofInt(
                (mDrawerLayout != null) ? mDrawerLayout : mLUtils,
                (mDrawerLayout != null) ? "statusBarBackgroundColor" : "statusBarColor",
                shown ? Color.BLACK : mNormalStatusBarColor,
                shown ? mNormalStatusBarColor : Color.BLACK)
                .setDuration(250);
        if (mDrawerLayout != null) {
            mStatusBarColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ViewCompat.postInvalidateOnAnimation(mDrawerLayout);
                }
            });
        }
        mStatusBarColorAnimator.setEvaluator(ARGB_EVALUATOR);
        mStatusBarColorAnimator.start();

        updateSwipeRefreshProgressBarTop();

        for (View view : mHideableHeaderViews) {
            if (shown) {
                view.animate()
                        .translationY(0)
                        .alpha(1)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
            } else {
                view.animate()
                        .translationY(-view.getBottom())
                        .alpha(0)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
            }
        }
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        return false;
    }
}

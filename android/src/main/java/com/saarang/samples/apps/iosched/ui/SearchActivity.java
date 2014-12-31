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

import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;

import com.saarang.samples.apps.iosched.model.TagMetadata;
import com.saarang.samples.apps.iosched.provider.ScheduleContract;
import com.saarang.samples.apps.iosched.ui.debug.actions.ShowFeedbackNotificationAction;
import com.saarang.samples.apps.iosched.util.AnalyticsManager;
import com.saarang.samples.apps.iosched.util.LogUtils;

public class SearchActivity extends BaseActivity implements SessionsFragment.Callbacks {
    private static final String TAG = LogUtils.makeLogTag("SearchActivity");

    private final static String SCREEN_LABEL = "Search";

    SessionsFragment mSessionsFragment = null;

    SearchView mSearchView = null;
    String mQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.saarang.samples.apps.iosched.R.layout.activity_search);

        Toolbar toolbar = getActionBarToolbar();
        toolbar.setTitle(com.saarang.samples.apps.iosched.R.string.title_search);
        toolbar.setNavigationIcon(com.saarang.samples.apps.iosched.R.drawable.ic_up);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateUpToFromChild(SearchActivity.this,
                        IntentCompat.makeMainActivity(new ComponentName(SearchActivity.this,
                                BrowseSessionsActivity.class)));
            }
        });

        FragmentManager fm = getFragmentManager();
        mSessionsFragment = (SessionsFragment) fm.findFragmentById(com.saarang.samples.apps.iosched.R.id.fragment_container);

        String query = getIntent().getStringExtra(SearchManager.QUERY);
        query = query == null ? "" : query;
        mQuery = query;

        if (mSessionsFragment == null) {
            mSessionsFragment = new SessionsFragment();
            Bundle args = intentToFragmentArguments(
                    new Intent("com.saarang.samples.apps.iosched.SESSION_VIEW", ScheduleContract.Sessions.buildSearchUri(query)));
            mSessionsFragment.setArguments(args);
            fm.beginTransaction().add(com.saarang.samples.apps.iosched.R.id.fragment_container, mSessionsFragment).commit();
        }

        if (mSearchView != null) {
            mSearchView.setQuery(query, false);
        }

        overridePendingTransition(0, 0);
    }

    @Override
    public void onSessionSelected(String sessionId, View clickedView) {
        /* [ANALYTICS:EVENT]
         * TRIGGER:   Click on a session on the Search screen.
         * CATEGORY:  'Search'
         * ACTION:    'selectsession'
         * LABEL:     session ID (for example "3284-fac320-2492048-bf391')
         * [/ANALYTICS]
         */
        AnalyticsManager.sendEvent(SCREEN_LABEL, "selectsession", sessionId);
        getLUtils().startActivityWithTransition(
                new Intent("com.saarang.samples.apps.iosched.SESSION_VIEW",
                        ScheduleContract.Sessions.buildSessionUri(sessionId)),
                clickedView,
                SessionDetailActivity.TRANSITION_NAME_PHOTO);
    }

    @Override
    public void onTagMetadataLoaded(TagMetadata metadata) {}

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        LogUtils.LOGD(TAG, "SearchActivity.onNewIntent: " + intent);
        setIntent(intent);
        String query = intent.getStringExtra(SearchManager.QUERY);
        Bundle args = intentToFragmentArguments(
                new Intent("com.saarang.samples.apps.iosched.SESSION_VIEW", ScheduleContract.Sessions.buildSearchUri(query)));
        LogUtils.LOGD(TAG, "onNewIntent() now reloading sessions fragment with args: " + args);
        mSessionsFragment.reloadFromArguments(args);
        /* [ANALYTICS:EVENT]
         * TRIGGER:   Start a search on the Search Activity
         * CATEGORY:  'Search'
         * ACTION:    'Search'
         * LABEL:     none (we DO NOT log the search query).
         * [/ANALYTICS]
         */
        AnalyticsManager.sendEvent(SCREEN_LABEL, "Search", "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(com.saarang.samples.apps.iosched.R.menu.search, menu);
        final MenuItem searchItem = menu.findItem(com.saarang.samples.apps.iosched.R.id.menu_search);
        if (searchItem != null) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            final SearchView view = (SearchView) searchItem.getActionView();
            mSearchView = view;
            if (view == null) {
                LogUtils.LOGW(TAG, "Could not set up search view, view is null.");
            } else {
                view.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                view.setIconified(false);
                view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        view.clearFocus();
                        if ("zzznotif".equals(s)) {
                            (new ShowFeedbackNotificationAction()).run(SearchActivity.this, null);
                        }
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        if (null != mSessionsFragment) {
                            mSessionsFragment.requestQueryUpdate(s);
                        }
                        return true;
                    }
                });
                view.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        finish();
                        return false;
                    }
                });
            }

            if (!TextUtils.isEmpty(mQuery)) {
                view.setQuery(mQuery, false);
            }
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == com.saarang.samples.apps.iosched.R.id.menu_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

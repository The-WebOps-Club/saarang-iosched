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

import android.app.AlertDialog;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.app.*;
import android.content.CursorLoader;
import android.content.Loader;
import android.widget.CursorAdapter;
import android.widget.PopupMenu;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.saarang.samples.apps.iosched.provider.ScheduleContract;
import com.saarang.samples.apps.iosched.ui.widget.CollectionView;
import com.saarang.samples.apps.iosched.ui.widget.CollectionViewCallbacks;
import com.saarang.samples.apps.iosched.util.AccountUtils;
import com.saarang.samples.apps.iosched.util.ImageLoader;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.saarang.samples.apps.iosched.util.UIUtils;
import com.saarang.samples.apps.iosched.util.LogUtils;

import java.util.ArrayList;

import static com.saarang.samples.apps.iosched.util.LogUtils.LOGD;
import static com.saarang.samples.apps.iosched.util.LogUtils.makeLogTag;

public class PeopleIveMetFragment extends Fragment
         {


}

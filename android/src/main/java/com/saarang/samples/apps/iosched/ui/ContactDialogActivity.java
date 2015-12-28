package com.saarang.samples.apps.iosched.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.saarang.samples.apps.iosched.R;
import com.saarang.samples.apps.iosched.util.ContactObject;

/**
 * Created by Moochi on 12/14/2015.
 */

public class ContactDialogActivity extends Activity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact_dialog);
//        if (getWindow().isFloating()) maxinumDialogWindowHeight(getWindow());


        ImageView profilePic=(ImageView) findViewById(R.id.ivProfilePic);
        TextView name=(TextView) findViewById(R.id.tvName);
        ImageButton phone, email, publicProfile;

        phone=(ImageButton) findViewById(R.id.ibCall);
        email=(ImageButton) findViewById(R.id.ibMail);
//        publicProfile=(ImageButton) findViewById(R.id.ibPublicProfile);

        final ContactObject contact;
        contact = (ContactObject)getIntent().getExtras().get("details");

        profilePic.setImageResource(contact.getProfilePic());
        name.setText(contact.getName());
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhoneNumber(contact.getNumber());
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] s = {contact.getMail()};
                composeEmail(s, "Saarang Queries");
            }
        });

//        publicProfile.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (contact.getPublicProfile()==""){
//                    //TODO: show toast!
//                }
//                else{
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(contact.getPublicProfile()));
//                    startActivity(i);
//                }
//            }
//        });
    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void composeEmail(String[] address, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, address);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
//    public static void maxinumDialogWindowHeight(Window window) {
//        WindowManager.LayoutParams layout = new WindowManager.LayoutParams();
//        layout.copyFrom(window.getAttributes());
//        layout.height = WindowManager.LayoutParams.ROTATION_ANIMATION_JUMPCUT;
//        window.setAttributes(layout);
//    }
}

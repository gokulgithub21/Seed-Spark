package com.gokul.vegetable.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.gokul.vegetable.R;

public class ContactDetailsActivity extends AppCompatActivity {

    private TextView contactPhone;
    private TextView contactEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        contactPhone = findViewById(R.id.contact_phone);
        contactEmail = findViewById(R.id.contact_email);

        contactPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = contactPhone.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        contactEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = contactEmail.getText().toString();
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + email));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject here"); // Optional
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }
}

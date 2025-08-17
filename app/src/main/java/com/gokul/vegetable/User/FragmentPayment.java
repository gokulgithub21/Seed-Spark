package com.gokul.vegetable.User;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.gokul.vegetable.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class FragmentPayment extends Fragment {

    private Button btnConfirmPayment, btnCancelPayment;
    final int UPI_PAYMENT = 0;

    // Set the default UPI ID here
    private static final String DEFAULT_UPI_ID = "samuvel9123@oksbi"; // Replace with your UPI ID
    private static final String MERCHANT_NAME = "GOKUL S"; // Replace with your merchant name
    private static final String TRANSACTION_NOTE = "Payment for your order"; // Replace with transaction note

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        // Initialize buttons
        btnConfirmPayment = view.findViewById(R.id.btnConfirmPayment);
        btnCancelPayment = view.findViewById(R.id.btnCancelPayment);

        // Handle Confirm Payment click
        btnConfirmPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call UPI payment method with the default UPI ID and amount
                String amount = "10";  // Example amount to pay
                payUsingUPI(amount, DEFAULT_UPI_ID, MERCHANT_NAME, TRANSACTION_NOTE);
            }
        });

        // Handle Cancel Payment click
        btnCancelPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelPayment();
            }
        });

        return view;
    }

    private void payUsingUPI(String amount, String upiId, String name, String note) {
        Log.d("UPI", "Initiating payment...");
        Log.d("UPI", "UPI ID: " + upiId);
        Log.d("UPI", "Amount: " + amount);
        Log.d("UPI", "Name: " + name);
        Log.d("UPI", "Note: " + note);

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId) // UPI ID of the receiver
                .appendQueryParameter("pn", name) // Name of the receiver
                .appendQueryParameter("tn", note) // Transaction note
                .appendQueryParameter("am", amount) // Amount to pay
                .appendQueryParameter("cu", "INR") // Currency
                .build();

        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // Check if there is an app that can handle UPI payment
        try {
            startActivityForResult(Intent.createChooser(upiPayIntent, "Pay with"), UPI_PAYMENT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPI_PAYMENT) {
            if (data != null) {
                String response = data.getStringExtra("response");
                handleUPIPaymentResponse(response);
            } else {
                handleUPIPaymentResponse(null);
            }
        }
    }

    private void handleUPIPaymentResponse(String response) {
        if (response == null) {
            response = "discard";
        }

        List<String> dataList = new ArrayList<>();
        dataList.add(response);
        upiPaymentDataOperation(dataList);
    }

    private void upiPaymentDataOperation(List<String> data) {
        if (data == null) return;

        String str = data.get(0);
        Log.d("UPI", "UPI Payment Response: " + str);
        String status = "";
        String approvalRefNo = "";
        String[] responseArray = str.split("&");

        for (String s : responseArray) {
            String[] equalStr = s.split("=");
            if (equalStr.length >= 2) {
                if (equalStr[0].toLowerCase().equals("status".toLowerCase())) {
                    status = equalStr[1].toLowerCase();
                } else if (equalStr[0].toLowerCase().equals("txnRef".toLowerCase()) || equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase())) {
                    approvalRefNo = equalStr[1];
                }
            }
        }

        if (status.equals("success")) {
            Toast.makeText(getContext(), "Transaction Successful. Approval Reference No: " + approvalRefNo, Toast.LENGTH_SHORT).show();
            navigateToHome();
        } else if ("Payment cancelled by user.".equals(status)) {
            Toast.makeText(getContext(), "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Transaction failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to cancel payment and return to the cart
    private void cancelPayment() {
        Toast.makeText(getContext(), "Payment Cancelled", Toast.LENGTH_SHORT).show();

        // Navigate back to the Cart Fragment
        FragmentCartUser fragment = new FragmentCartUser();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerHomeUser, fragment);
        fragmentTransaction.commit();
    }

    // Method to navigate back to home
    private void navigateToHome() {
        // Navigate back to the HomeUser Fragment after payment confirmation
        FragmentHomeUser fragment = new FragmentHomeUser();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerHomeUser, fragment);
        fragmentTransaction.commit();
    }
}

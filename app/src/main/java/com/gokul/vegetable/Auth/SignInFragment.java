package com.gokul.vegetable.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gokul.vegetable.Admin.AdminActivity;
import com.gokul.vegetable.Dashboard.HomeActivity;
import com.gokul.vegetable.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.regex.Pattern;

public class SignInFragment extends Fragment {

    private EditText editTextEmail, editTextPassword;
    private Button button;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private String email, pass;
    private TextView textViewForPass;
    private View viewSignin;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = view.findViewById(R.id.progressBar_login);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        textViewForPass = view.findViewById(R.id.TextForgetPassword);
        viewSignin = view.findViewById(R.id.viewFragSignIn);
        button = view.findViewById(R.id.button_sign_in);

        viewSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtil.hideKeyboard(getActivity());
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = editTextEmail.getText().toString();
                pass = editTextPassword.getText().toString();

                if (email.isEmpty()) {
                    editTextEmail.setError("Please Enter Email");
                    editTextEmail.requestFocus();
                } else if (pass.isEmpty()) {
                    editTextPassword.setError("Please Enter Password");
                    editTextPassword.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String userId = firebaseAuth.getCurrentUser().getUid();
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("AppUsers").child(userId);

                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            // Check if the logged-in user is an admin or a user
                                            boolean isAdmin = snapshot.child("isAdmin").getValue(Integer.class) == 1;
                                            boolean isUser = snapshot.child("isUsers").getValue(Integer.class) == 1;

                                            if (isAdmin) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(getContext(), "Admin Login successful!", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(getContext(), AdminActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            } else if (isUser) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(getContext(), "User Login successful!", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(getContext(), HomeActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            } else {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(getContext(), "Login failed: Unknown user type.", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getContext(), "User not found in database.", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                editTextEmail.setText("");
                                editTextPassword.setText("");

                            } else {
                                Toast.makeText(getContext(), "Username And Password Incorrect...", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        textViewForPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Please wait...");
                progressDialog.show();

                email = editTextEmail.getText().toString();
                if (email.isEmpty()) {
                    progressDialog.dismiss();
                    editTextEmail.setError("Please Enter Email");
                    editTextEmail.requestFocus();
                } else if (!(isValidEmail(email))) {
                    editTextEmail.setError("Please Enter Valid Email..!!!");
                } else {
                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "An email has been sent to you...", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        return view;
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        return email != null && pat.matcher(email).matches();
    }
}

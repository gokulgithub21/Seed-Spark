package com.gokul.vegetable.Auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gokul.vegetable.Admin.AdminActivity;
import com.gokul.vegetable.Dashboard.HomeActivity;
import com.gokul.vegetable.R;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpFragment extends Fragment {

    private Button buttonSignUp;
    private EditText editTextName, editTextEmail, editTextPassword, editTextVfPassword;
    private String name, email, pass, vfpass;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private View viewSignout;
    private DatabaseReference databaseReference;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        buttonSignUp = view.findViewById(R.id.button_sign_up);
        editTextName = view.findViewById(R.id.Address_address);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextVfPassword = view.findViewById(R.id.editTextVerifyPassword);
        viewSignout = view.findViewById(R.id.viewFragSignUp);
        progressBar = view.findViewById(R.id.progressBar);

        viewSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtil.hideKeyboard(getActivity());
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editTextName.getText().toString();
                email = editTextEmail.getText().toString();
                pass = editTextPassword.getText().toString();
                vfpass = editTextVfPassword.getText().toString();

                if (name.isEmpty()) {
                    editTextName.setError("Please Enter Name");
                    editTextName.requestFocus();
                } else if (email.isEmpty()) {
                    editTextEmail.setError("Please Enter Email");
                    editTextEmail.requestFocus();
                } else if (pass.isEmpty()) {
                    editTextPassword.setError("Please Enter Password");
                    editTextPassword.requestFocus();
                } else if (vfpass.isEmpty()) {
                    editTextVfPassword.setError("Please Enter Password");
                    editTextVfPassword.requestFocus();
                } else if (!(isValidEmail(email))) {
                    editTextEmail.setError("Please Enter Valid Email..!!!");
                } else if (!(isValidPassword(pass))) {
                    Toast.makeText(getContext(), "Passwords must contain at least six characters, including uppercase, lowercase letters and numbers", Toast.LENGTH_LONG).show();
                    editTextPassword.setError("Passwords must contain at least six characters, including uppercase, lowercase letters and numbers");
                } else if (!(pass.equals(vfpass))) {
                    Toast.makeText(getContext(), "Password Does Not Match", Toast.LENGTH_SHORT).show();
                    editTextVfPassword.setError("Password Does Not Match");
                } else if (!(email.isEmpty() && pass.isEmpty())) {
                    progressBar.setVisibility(View.VISIBLE);

                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getContext(),
                                                "Registration Error " + task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        databaseReference = FirebaseDatabase.getInstance().getReference().child("AppUsers");
                                        String key = firebaseAuth.getCurrentUser().getUid();

                                        // Store user data
                                        databaseReference.child(key).child("ID").setValue(key);
                                        databaseReference.child(key).child("Name").setValue(name);
                                        databaseReference.child(key).child("Email").setValue(email);
                                        databaseReference.child(key).child("Password").setValue(pass);

                                        DatabaseReference adminReference = FirebaseDatabase.getInstance().getReference().child("Admin");
                                        adminReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                boolean isAdmin = false;

                                                for (DataSnapshot adminSnapshot : snapshot.getChildren()) {
                                                    String adminEmail = adminSnapshot.child("username").getValue(String.class);

                                                    if (email.equals(adminEmail)) {
                                                        isAdmin = true;
                                                        break;
                                                    }
                                                }

                                                // Set user role based on whether they are an admin
                                                if (isAdmin) {
                                                    databaseReference.child(key).child("isAdmin").setValue(1);
                                                    databaseReference.child(key).child("isUsers").setValue(0);
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(getContext(), "Admin Successfully registered", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getContext(), AdminActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                } else {
                                                    databaseReference.child(key).child("isAdmin").setValue(0);
                                                    databaseReference.child(key).child("isUsers").setValue(1);
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(getContext(), "User Successfully registered", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getContext(), HomeActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(getContext(), "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        // Clear fields after registration
                                        editTextName.setText("");
                                        editTextEmail.setText("");
                                        editTextPassword.setText("");
                                        editTextVfPassword.setText("");
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
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        Pattern p = Pattern.compile(regex);

        if (password == null) {
            return false;
        }

        Matcher m = p.matcher(password);

        return m.matches();
    }
}

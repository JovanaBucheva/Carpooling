package com.example.carpooling;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Fragment {

    public Register(){}
    private EditText username;
    private EditText password;

    private EditText email;
    private Button submitButton;
    private DBHelper dbHelper;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view= inflater.inflate(R.layout.fragment_register, container, false);

        username = view.findViewById(R.id.nameInput);
        password = view.findViewById(R.id.passwordInput);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        email = view.findViewById(R.id.emailInput);
        submitButton = view.findViewById(R.id.submitButton);

        dbHelper = new DBHelper(requireContext()); // Initialize DBHelper

        submitButton.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String mail = email.getText().toString().trim();

            if (!user.isEmpty() && !pass.isEmpty() && !mail.isEmpty()) {
                boolean isUnique = dbHelper.checkUserUnique(user, mail);

                if (isUnique) {
                    boolean isInserted = dbHelper.insertUser(user, pass, mail);

                    if (isInserted) {
                        Toast.makeText(getContext(), "Registration Successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error occurred while saving user data.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "This user already exist!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            }
    });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Intent intent = requireActivity().getIntent();

    }
}



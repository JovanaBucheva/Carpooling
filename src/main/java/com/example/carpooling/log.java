package com.example.carpooling;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class log extends Fragment {

    private EditText username;
    private EditText password;
    private Spinner type;
    private Button loginButton;
    private DBHelper dbHelper;

    public log() {
        // Required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Fragment-specific initialization if needed, but avoid accessing views here.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_log, container, false);

        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        type = view.findViewById(R.id.purpose);
        loginButton = view.findViewById(R.id.login);

        dbHelper = new DBHelper(requireContext());

        loginButton.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String purpose = type.getSelectedItem().toString().trim();

            if (!user.isEmpty() && !pass.isEmpty() && !purpose.isEmpty()) {
                int isValid = dbHelper.checkUser(user, pass);

                if (isValid == 1) {
                    if(purpose.equals("Возач")){
                        boolean valid = dbHelper.updateUserType(user,purpose);
                        dbHelper.addDriverToCarsTable(user);
                        dbHelper.setActive(user,0);
                        dbHelper.deleteRoute(user);
                        if(valid){
                            Intent intent = new Intent(getActivity(),driverScreen.class);
                            intent.putExtra("username", user);
                            startActivity(intent);
                        }
                    }else if(purpose.equals("Патник")){
                        boolean valid = dbHelper.updateUserType(user,purpose);
                        if(valid) {
                            Intent intent = new Intent(getActivity(), driverOptionsForPassengers.class);
                            intent.putExtra("username", user);
                            startActivity(intent);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Invalid username or password!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please enter both username and password.", Toast.LENGTH_SHORT).show();
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

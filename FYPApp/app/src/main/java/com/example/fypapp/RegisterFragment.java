package com.example.fypapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    private String mParam2;
    private String mParam1;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }









    // CODE FROM HERE

    // for validation
    private EditText username;
    private EditText password;
    private EditText confirm_password;
    private MaterialButton register_reg_Btn;

    // for firebase auth

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);


            mAuth = FirebaseAuth.getInstance();

            username = view.findViewById(R.id.reg_username);
            password = view.findViewById(R.id.reg_password);
            confirm_password = view.findViewById(R.id.reg_confirm_password);

            register_reg_Btn = view.findViewById(R.id.register_reg);
            register_reg_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    validateEmptyForm();
                }
            });





            // Switching Container's Fragment: from Login Fragment to Register Fragment
            view.findViewById(R.id.login_reg).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //works in the logic of intents but for fragments
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, new LoginFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });






        return view;
    }


    private void validateEmptyForm(){
        // VALIDATES THEN REGISTERS IN FIREBASE
        // if they are empty --> ERROR + ICON
        // if email not valid + if password less than 3 characters + if inputs are different

        //if they are empty --> ERROR + ICON
        Drawable icon = AppCompatResources.getDrawable(requireContext(),
                R.drawable.x_button);
        icon.setBounds(0, 0, 50,50);
        String username_text = username.getText().toString().trim();
        String password_text = password.getText().toString().trim();
        String confirm_password_text = confirm_password.getText().toString().trim();

        Log.d("msg",username_text );
        if(TextUtils.isEmpty( username_text )){
            username.setError("Please enter username",icon);
        }
        if( TextUtils.isEmpty(password_text )){
            password.setError("Please enter your password",icon);
        }
        if( TextUtils.isEmpty(confirm_password_text )){
            confirm_password.setError("Please confirm your password",icon);
        }


        // if email not valid + if password less than 3 characters + if inputs are different
        if(username_text.length()!=0 && password_text.length()!=0 && confirm_password_text.length()!=0){

            if(username_text.matches("^(.+)@(.+)$")){
                if(password_text.length()>=6){
                    if(password_text.equals(confirm_password_text)){

                        firebaseSignup();

                    }
                    else{
                        confirm_password.setError("Password did not match");
                    }

                }
                else{
                    password.setError("Must longer than 6 characters");
                }

            }else{
                username.setError("Please enter valid EMAIL");
            }

        }
    }

    public void firebaseSignup(){
        //For Firebase auth
        register_reg_Btn.setEnabled(false);
        register_reg_Btn.setAlpha(0.5f);
        mAuth.createUserWithEmailAndPassword(username.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(),"Registration successfull",Toast.LENGTH_SHORT).show();
                    Signer.INSTANCE.setUsername(username.getText().toString());
                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    startActivity(intent);
                }else{
                    register_reg_Btn.setEnabled(true);
                    register_reg_Btn.setAlpha(1.0f);
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
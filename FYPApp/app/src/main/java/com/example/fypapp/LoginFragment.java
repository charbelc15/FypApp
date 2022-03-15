package com.example.fypapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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

    private EditText username;
    private EditText password;
    private MaterialButton login_Btn;
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Switching Container's Fragment: from Login Fragment to Register Fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        try {

            mAuth = FirebaseAuth.getInstance();

            username = view.findViewById(R.id.login_username);
            password = view.findViewById(R.id.login_password);

            login_Btn = view.findViewById(R.id.login);
            login_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    validateEmptyForm();    //VALIDATES AND SIGNS IN
                }
            });



            view.findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, new RegisterFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        }catch (Exception e){
            e.printStackTrace();
        }


        return view;
    }

        //VALIDATES AND SIGNS IN
        private void validateEmptyForm(){
            // VALIDATES
            // if they are empty --> ERROR + ICON
            // if email not valid + if password less than 3 characters + if inputs are different

            //if they are empty --> ERROR + ICON
            Drawable icon = AppCompatResources.getDrawable(requireContext(),
                    R.drawable.x_button);
            icon.setBounds(0, 0, 50,50);
            String username_text = username.getText().toString().trim();
            String password_text = password.getText().toString().trim();

            Log.d("msg",username_text );
            if(TextUtils.isEmpty( username_text )){
                username.setError("Please enter username",icon);
            }
            if( TextUtils.isEmpty(password_text )){
                password.setError("Please enter your password",icon);
            }



            // if email not valid + if password less than 3 characters + if inputs are different
            if(username_text.length()!=0 && password_text.length()!=0){

                if(username_text.matches("^(.+)@(.+)$")){
                    if(password_text.length()>=3){

                          firebaseSignIn();

                    }
                    else{
                        password.setError("Must longer than 3 characters");
                    }

                }else{
                    username.setError("Please enter valid EMAIL");
                }

            }
        }
    //SIGNS IN AFTER VALIDATION
    public void firebaseSignIn(){
        //For Firebase auth
        login_Btn.setEnabled(false);
        login_Btn.setAlpha(0.5f);
        mAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(),"Sign In successfull",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    startActivity(intent);
                }else{
                    login_Btn.setEnabled(true);
                    login_Btn.setAlpha(1.0f);
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
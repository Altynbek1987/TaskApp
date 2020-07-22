package com.example.taskapp.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.telecom.PhoneAccount;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskapp.Prefs;
import com.example.taskapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class PhoneFragment extends Fragment {

    private EditText editPhoneNumber, editSmsCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private NavController navController;
    private String code;
    private TextView timer;
    private Button btnContinue, btnStartHome;
    String smsCode;
    private FirebaseAuth firebaseAuth;
    private LinearLayout smsContainer, phoneContainer;

    public PhoneFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.e("TAG", "onVerificationCompleted" + phoneAuthCredential.getSmsCode());
                code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    editSmsCode.setText(code);
                }
//                if (FirebaseAuth.getInstance().getCurrentUser() != null){
//                    navController.navigate(R.id.nav_host_fragment);
//                }
                else {

                    Toast.makeText(getContext(), "Cмс не пришел!", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e("TAG", "onVerificationFailed" + e.getMessage());
                Toast.makeText(getContext(), "Верификация не успешно!", Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.e("TAG", "onCodeSent: " + s);
                smsCode = s;
            }
        };
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        return inflater.inflate(R.layout.fragment_phone, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editPhoneNumber = view.findViewById(R.id.editPhone);
        editSmsCode = view.findViewById(R.id.editSMSCode);
        btnContinue = view.findViewById(R.id.btnContinue);
        btnStartHome = view.findViewById(R.id.btn_startHome);
        smsContainer = view.findViewById(R.id.sms_container);
        phoneContainer = view.findViewById(R.id.phone_container);
        timer = view.findViewById(R.id.timer);
        view.findViewById(R.id.btnContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = editPhoneNumber.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    editPhoneNumber.setError("Вы не ввели номер");
                    return;
                } else if (phone.length() < 9) {
                    editPhoneNumber.setError("Вы не корректно ввели номер");
                    return;
                }
                phoneContainer.setVisibility(View.GONE);
                smsContainer.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Запрос отправлен, подождите", Toast.LENGTH_SHORT).show();
                //Таймердин логикасы
                new CountDownTimer(60000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timer.setText(String.valueOf(millisUntilFinished / 1000));
                        if (code != null) {
                            cancel();
                        }
                    }
                    @Override
                    public void onFinish() {
                        cancel();
                        Toast.makeText(getContext(), "Попробуйте написать номер еще раз", Toast.LENGTH_SHORT).show();
                        phoneContainer.setVisibility(View.VISIBLE);
                        smsContainer.setVisibility(View.GONE);
                    }
                }.start();
                verify();
            }
        });
        view.findViewById(R.id.btn_startHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eSms = editSmsCode.getText().toString();
                if (TextUtils.isEmpty(eSms)) {
                    editSmsCode.setError("Вы не ввели код");
                    return;
                } else if (eSms.length() == 6) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(smsCode, eSms);
                    signInWithPhoneAuthCredential(credential);
                } else {
                    editSmsCode.setError("Вы не правльно ввели код");
                    return;
                }
            }
        });
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    new Prefs(requireActivity()).isShown(true);
                    FirebaseUser user = task.getResult().getUser();
                    // Закрыл PhoneFragment и открыл nav_host_fragment с помошю  NavController controller = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                    //                    controller.popBackStack();
                    NavController controller = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                    controller.popBackStack();
                    Log.d(TAG, "signInWithCredential:success все ок");
                    return;
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    }
                }
            }
        });
    }
    private void verify() {
        String phone = editPhoneNumber.getText().toString().trim();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                requireActivity(),
                callbacks);
    }
}

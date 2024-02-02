package com.example.lockapp;

import android.text.TextUtils;

import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;

public class creditentialValidator {
    public static boolean isValidEmailId(String email){
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public static boolean isEmpty(TextInputEditText text) {
        CharSequence str = text.getText().toString();
        return !TextUtils.isEmpty(str);
    }

    public static boolean isValidLength(TextInputEditText password){
        return password.getText().toString().length() > 5;
    }

    public static boolean isValidEmail(TextInputEditText email){
        return isEmpty(email) && isValidEmailId(email.getText().toString().trim());
    }

}

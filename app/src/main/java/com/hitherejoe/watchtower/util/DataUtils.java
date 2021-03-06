package com.hitherejoe.watchtower.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Base64;

import com.google.gson.Gson;
import com.hitherejoe.watchtower.data.model.ErrorResponse;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;
import timber.log.Timber;

public class DataUtils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    public static ErrorResponse parseRetrofitError(Throwable error) {
        String json = new String(((TypedByteArray) ((RetrofitError) error).getResponse().getBody()).getBytes());
        return new Gson().fromJson(json, ErrorResponse.class);
    }

    public static String base64DecodeToString(String s) {
        try {
            return new String(Base64.decode(s, Base64.DEFAULT), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Timber.e("There was an error decoding the String: " + e);
        }
        return null;
    }

    public static String base64Encode(byte[] b) {
        return Base64.encodeToString(b, Base64.DEFAULT);
    }


    public static boolean isStringDoubleValue(String string) {
        final String Digits     = "(\\p{Digit}+)";
        final String HexDigits  = "(\\p{XDigit}+)";

        // an exponent is 'e' or 'E' followed by an optionally
        // signed decimal integer.
        final String Exp        = "[eE][+-]?"+Digits;
        final String fpRegex    =
                ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
                        "[+-]?(" + // Optional sign character
                        "NaN|" +           // "NaN" string
                        "Infinity|" +      // "Infinity" string

                        // A decimal floating-point string representing a finite positive
                        // number without a leading sign has at most five basic pieces:
                        // Digits . Digits ExponentPart FloatTypeSuffix
                        //
                        // Since this method allows integer-only strings as input
                        // in addition to strings of floating-point literals, the
                        // two sub-patterns below are simplifications of the grammar
                        // productions from the Java Language Specification, 2nd
                        // edition, section 3.10.2.

                        // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                        "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

                        // . Digits ExponentPart_opt FloatTypeSuffix_opt
                        "(\\.("+Digits+")("+Exp+")?)|"+

                        // Hexadecimal strings
                        "((" +
                        // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                        "(0[xX]" + HexDigits + "(\\.)?)|" +

                        // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

                        ")[pP][+-]?" + Digits + "))" +
                        "[fFdD]?))" +
                        "[\\x00-\\x20]*");// Optional trailing "whitespace"

        return  Pattern.matches(fpRegex, string);
    }

}

package com.leotarius.mypayid.Utils;

import android.os.Handler;
import android.text.TextUtils;

public abstract class UtilFunctions {
    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public interface DelayCallback{
        void afterDelay();
    }

    public static void delay(float secs, final DelayCallback delayCallback){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayCallback.afterDelay();
            }
        }, (int)(secs * 1000)); // afterDelay will be executed after (secs*1000) milliseconds.
    }
}


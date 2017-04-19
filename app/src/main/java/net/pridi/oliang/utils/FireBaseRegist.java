package net.pridi.oliang.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import net.pridi.oliang.manager.HttpManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by pridi on 18-Apr-17.
 */

public class FireBaseRegist extends FirebaseInstanceIdService {
    private static final String TAG="IID";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        FirebaseMessaging.getInstance().subscribeToTopic("newpost");
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
        //e9D-LVstc0M:APA91bGl2VQ3jzeXaIkU0_al5AdK80hWG4jnJhDgaOFdaI1xLhEBY92o7_sqj2nR5JVZSe9aSGoMJWhfM5nbiyfDB10iG0vhPqb_oEp9wnfGywaMchPDjDKlTwGs6MygA6c_uxNcQ8x_
    }
//
    private void sendRegistrationToServer(String refreshedToken) {
        TelephonyManager tManager= (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String uuid=tManager.getDeviceId();
        Log.d(TAG,"uuid "+uuid);
        Call<Object> call = HttpManager.getInstance().getService().registerToken(refreshedToken,uuid);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "refreshed and response with success");
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d(TAG, " refreshed but no response ");
            }
        });

    }
}

package net.pridi.oliang;

import android.app.Application;
import android.os.StrictMode;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.okhttp.OkHttpStack;

import okhttp3.OkHttpClient;

/**
 * Created by pridi on 28-Mar-17.
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        //init upload library
        Contextor.getInstance().init(getApplicationContext());
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        // Or, you can define it manually.
        UploadService.NAMESPACE = "net.pridi.oliang";
        OkHttpClient client=new OkHttpClient();
        UploadService.HTTP_STACK=new OkHttpStack(client);
    }

    @Override 
    public void onTerminate() {
        super.onTerminate();
    }

}

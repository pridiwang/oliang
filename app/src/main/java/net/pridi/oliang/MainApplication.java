package net.pridi.oliang;

import android.app.Application;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

import net.gotev.uploadservice.UploadService;

/**
 * Created by pridi on 28-Mar-17.
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //init upload library
        Contextor.getInstance().init(getApplicationContext());
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        // Or, you can define it manually.
        UploadService.NAMESPACE = "net.pridi.oliang";
    }

    @Override 
    public void onTerminate() {
        super.onTerminate();
    }
}

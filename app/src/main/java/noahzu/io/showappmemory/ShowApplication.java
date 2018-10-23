package noahzu.io.showappmemory;

import android.app.Application;

import noahzu.io.library.gather.Sampler;

/**
 * Author: jzu
 * Date: 2018/10/23
 * Function:
 */
public class ShowApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Sampler.getInstance().init(getApplicationContext(),500);
    }
}

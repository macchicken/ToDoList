package comp5216.sydney.edu.au.todolist;

import android.content.Context;

/**
 * Created by Barry on 2015/9/12.
 */
public class TodoListApplication extends com.activeandroid.app.Application{
    private static Context mAppContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext=getApplicationContext();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static Context getContext()
    {
        return mAppContext;
    }
}

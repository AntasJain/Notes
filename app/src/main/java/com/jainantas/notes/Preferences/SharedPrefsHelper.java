package com.jainantas.notes.Preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SharedPrefsHelper {
    static Context context;
    private static final String counter="COUNTER";
    static SharedPreferences.Editor editor;
    private static SharedPreferences mPref;
    public static  void init(Context mContext){
        context=mContext;
        if(mPref==null) {
            mPref = context.getSharedPreferences(mContext.getPackageName(), Activity.MODE_PRIVATE);
            editor=mPref.edit();
        }
    }
    public static String getUser(String userName, String default_user){
        return mPref.getString(userName,default_user);
    }
    public static String getEmail(String eMail, String defaultEmail){
        return mPref.getString(eMail, defaultEmail);
    }
    public static String getId(String id, String defId){
        return mPref.getString(id,defId);
    }

    public static void putUser(String key, String userName){
        editor.putString(key, userName).commit();
        Toast.makeText(context,"LoggedIn",Toast.LENGTH_LONG).show();
    }
    public static void putEmail(String key, String userEmail){
        editor.putString(key,userEmail).commit();
    }
    public static void putId(String key,String userId){
        editor.putString(key,userId).commit();
    }
    public static void delUsage(String id,String eMail, String userName){
        editor.remove(id);
        editor.remove(eMail);
        editor.remove(userName);
        editor.remove(counter);
        editor.clear().commit();

    }
    public static void dontShowAgain(String key,boolean again){
        editor.putBoolean(key,again).commit();
    }
    public static boolean isShow(String key,boolean def){
        return  mPref.getBoolean(key, def);
    }
    public static void setCount(String key, int count){
        editor.putInt(key, count).commit();
    }
    public static int getCount(String key,int def){
        return  mPref.getInt(key, def);
    }

}

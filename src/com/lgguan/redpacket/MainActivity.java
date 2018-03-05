package com.lgguan.redpacket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button btSetting;
	private Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btSetting = (Button) findViewById(R.id.btSetting);
		btSetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(accessibleIntent);
			}
		});
		if (!isAccessibilitySettingsOn(getApplicationContext())) {
		    startActivity(accessibleIntent);
		}
	}

	private long touchTime = 0;//定义按下手机按键的时间
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//覆写手机按键按下方法
        if (keyCode == KeyEvent.KEYCODE_BACK) {//若按下的是返回键
            if (System.currentTimeMillis() - touchTime > 1000) {//若系统当前的时间与按下手机按键的时候间隔大于1S，则提示用户
                Toast.makeText(MainActivity.this, "再按一次返回桌面", Toast.LENGTH_SHORT).show();
                touchTime = System.currentTimeMillis();//设置按下手机按键时间为当前时间
            } else {
            	moveTaskToBack(false);  //不退出程序，转为后台运行
                return true; 
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);//若按下的手机按键不是返回键，则按照手机按键所定义的功能执行
        }
    }

    private boolean isAccessibilitySettingsOn(Context mContext) {  
        int accessibilityEnabled = 0;  
        // TestService为对应的服务  
        String TAG = "RED";
        final String service = getPackageName() + "/" + MyRedPacketService.class.getCanonicalName();  
        Log.i(TAG, "service:" + service);  
        // com.z.buildingaccessibilityservices/android.accessibilityservice.AccessibilityService  
        try {  
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),  
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);  
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);  
        } catch (Settings.SettingNotFoundException e) {  
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());  
        }  
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');  
  
        if (accessibilityEnabled == 1) {  
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");  
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),  
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);  
            // com.z.buildingaccessibilityservices/com.z.buildingaccessibilityservices.TestService  
            if (settingValue != null) {  
                mStringColonSplitter.setString(settingValue);  
                while (mStringColonSplitter.hasNext()) {  
                    String accessibilityService = mStringColonSplitter.next();  
  
                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);  
                    if (accessibilityService.equalsIgnoreCase(service)) {  
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");  
                        return true;  
                    }  
                }  
            }  
        } else {  
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");  
        }  
        return false;  
    }  

}

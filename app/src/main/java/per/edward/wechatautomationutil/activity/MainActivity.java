package per.edward.wechatautomationutil.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import per.edward.wechatautomationutil.helper.OpenAccessibilitySettingHelper;
import per.edward.wechatautomationutil.R;
import per.edward.wechatautomationutil.utils.Constant;

/**
 * 注意事项
 * 1、Android设备必须安装微信app
 * 2、Android Sdk Version
 * <p>
 * Created by Edward on 2018-03-15.
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {

        findViewById(R.id.open_accessibility_setting).setOnClickListener(clickListener);
        findViewById(R.id.open_accessibility_circle).setOnClickListener(clickListener);
        findViewById(R.id.open_accessibility_name).setOnClickListener(clickListener);
        findViewById(R.id.open_accessibility_photo).setOnClickListener(clickListener);
        findViewById(R.id.open_all).setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.open_accessibility_setting:
                    OpenAccessibilitySettingHelper.jumpToSettingPage(getBaseContext());
                    break;
                case R.id.open_accessibility_circle:
                    Intent intent = new Intent(MainActivity.this,AutoCircleFriends.class);
                    startActivity(intent);
                    break;
                case R.id.open_accessibility_name:
                    Intent intent2 = new Intent(MainActivity.this,AutoName.class);
                    startActivity(intent2);
                    break;
                case R.id.open_accessibility_photo:
                    Intent intent3 = new Intent(MainActivity.this,AutoHeadPhoto.class);
                    startActivity(intent3);
                    break;
                case R.id.open_all:
                    Intent intent4 = new Intent(MainActivity.this,AutoAll.class);
                    startActivity(intent4);
                    break;
            }
        }
    };

}

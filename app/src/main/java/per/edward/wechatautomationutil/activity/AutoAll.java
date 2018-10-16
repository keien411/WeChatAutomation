package per.edward.wechatautomationutil.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import per.edward.wechatautomationutil.R;
import per.edward.wechatautomationutil.utils.ChineseUtils;
import per.edward.wechatautomationutil.utils.Constant;

/**
 * @author keien
 * @date 2018/10/1
 */
public class AutoAll extends AppCompatActivity {
    private EditText edit, editIndex, editCount, editPhoneCount, editName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_all);

        initView();
    }

    private void initView() {
        edit = findViewById(R.id.edit);
        editIndex = findViewById(R.id.edit_index);
        editCount = findViewById(R.id.edit_count);
        editPhoneCount = findViewById(R.id.edit_index_photo);
        editName = findViewById(R.id.edit_name);
        findViewById(R.id.btn_save).setOnClickListener(clickListener);
        findViewById(R.id.bt_rand_name).setOnClickListener(clickListener);


        SharedPreferences sharedPreferences = getSharedPreferences(Constant.WECHAT_STORAGE, Activity.MODE_MULTI_PROCESS);
        int index = sharedPreferences.getInt(Constant.INDEX, 0);
        int count = sharedPreferences.getInt(Constant.COUNT, 0);
        int indexPhoto = sharedPreferences.getInt(Constant.INDEXBYPHOTO, 0);
        String content = sharedPreferences.getString(Constant.CONTENT, "");
        String contentByName = sharedPreferences.getString(Constant.CONTENTBYNAME, "");

        edit.setText(content);
        editIndex.setText(String.valueOf(index));
        editCount.setText(String.valueOf(count));
        editPhoneCount.setText(String.valueOf(indexPhoto));
        editName.setText(contentByName);


    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_save:
                    saveData();
                    break;
                case R.id.bt_rand_name:
                    String name = ChineseUtils.getRandomLengthChiness(2, 5);
                    editName.setText(name);
                    break;
            }
        }
    };

    public boolean checkParams() {
        if (TextUtils.isEmpty(editIndex.getText().toString())) {
            Toast.makeText(getBaseContext(), "起始下标不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(editCount.getText().toString())) {
            Toast.makeText(getBaseContext(), "图片总数不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Integer.valueOf(editCount.getText().toString()) > 9) {
            Toast.makeText(getBaseContext(), "图片总数不能超过9张", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(editPhoneCount.getText().toString())) {
            Toast.makeText(getBaseContext(), "头像图片不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(editName.getText().toString())) {
            Toast.makeText(getBaseContext(), "昵称不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveData() {
        if (!checkParams()) {
            return;
        }

        int index = Integer.valueOf(editIndex.getText().toString());
        int count = Integer.valueOf(editCount.getText().toString());
        int countPhone = Integer.valueOf(editPhoneCount.getText().toString());
        String name = editName.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences(Constant.WECHAT_STORAGE, Activity.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constant.CONTENT, edit.getText().toString());
        editor.putInt(Constant.INDEX, index);
        editor.putInt(Constant.COUNT, count);
        editor.putString(Constant.CONTENTBYNAME, name);
        editor.putInt(Constant.INDEXBYPHOTO, countPhone);
        editor.putInt(Constant.ACTION, 0);
        if (editor.commit()) {
            Toast.makeText(getBaseContext(), "保存成功", Toast.LENGTH_LONG).show();
            openWeChatApplication();//打开微信应用
        } else {
            Toast.makeText(getBaseContext(), "保存失败", Toast.LENGTH_LONG).show();
        }
    }

    private void openWeChatApplication() {
        PackageManager packageManager = getBaseContext().getPackageManager();
        Intent it = packageManager.getLaunchIntentForPackage("com.tencent.mm");
        startActivity(it);
    }
}

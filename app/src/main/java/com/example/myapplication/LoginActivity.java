package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etPwd;
    private Boolean bPwdSwitch = false;
    private EditText etAccount;
    private CheckBox cbRememberPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        final ImageView ivPwdSwitch = findViewById(R.id.iv_pwd_switch);
        etPwd = findViewById(R.id.et_pwd);
        etAccount = findViewById(R.id.et_account);
        cbRememberPwd = findViewById(R.id.cb_remember_pwd);
        Button btLogin = findViewById(R.id.bt_login);
        Button btRegister = findViewById(R.id.bt_register);

        //点击之后除登录外，还要检查如果勾选保存密码需要保存
        btLogin.setOnClickListener(this);
        btRegister.setOnClickListener(this);

        //密码明文密文以及图标切换
        ivPwdSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bPwdSwitch = !bPwdSwitch;
                if(bPwdSwitch) {
                    ivPwdSwitch.setImageResource(
                            R.drawable.outline_visibility_24);
                    etPwd.setInputType(
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    ivPwdSwitch.setImageResource(
                            R.drawable.outline_visibility_off_24);
                    etPwd.setInputType(
                            InputType.TYPE_TEXT_VARIATION_PASSWORD |
                            InputType.TYPE_CLASS_TEXT);
                    etPwd.setTypeface(Typeface.DEFAULT);
                }
            }
        });

        //读取SharedPreferences里储存的数据
        String spFileName = getResources()
                .getString(R.string.shared_preferences_file_name);
        String accountKey = getResources()
                .getString(R.string.login_account_name);
        String passwordKey =  getResources()
                .getString(R.string.login_password);
        String rememberPasswordKey = getResources()
                .getString(R.string.login_remember_password);

        SharedPreferences spFile = getSharedPreferences(
                spFileName,
                MODE_PRIVATE);

        //如果SharedPreferences中不存在对应的值，就使用null或false作为默认值
        String account = spFile.getString(accountKey, null);
        String password = spFile.getString(passwordKey, null);
        Boolean rememberPassword = spFile.getBoolean(
                rememberPasswordKey, false);

        //如果不为空且长度不为0,就设置账号密码
        if (account != null && !TextUtils.isEmpty(account)) {
            etAccount.setText(account);
        }
        if (password != null && !TextUtils.isEmpty(password)) {
            etPwd.setText(password);
        }
        cbRememberPwd.setChecked(rememberPassword);
    }


    @Override
    //设置SharedPreferences
    //登录按钮点击事件，如果勾选保存密码，将信息存入SharedPreferences
    public void onClick(View view) {
        if (view.getId() == R.id.bt_login) {
            Toast.makeText(getApplicationContext(),"登录成功",Toast.LENGTH_SHORT).show();
            String spFileName = getResources()
                    .getString(R.string.shared_preferences_file_name);
            String accountKey = getResources()
                    .getString(R.string.login_account_name);
            String passwordKey =  getResources()
                    .getString(R.string.login_password);
            String rememberPasswordKey = getResources()
                    .getString(R.string.login_remember_password);

            //创建一个SharedPreferences对象
            //Context.MODE_PRIVATE是一个访问模式的常量，用于指定SharedPreferences文件的访问权限
            //当前表示只有当前应用程序能访问SharedPreferences，其他程序不能访问修改
            SharedPreferences spFile = getSharedPreferences(
                    spFileName,
                    Context.MODE_PRIVATE);
            //获取编辑对象
            SharedPreferences.Editor editor = spFile.edit();

            if(cbRememberPwd.isChecked()){
                String password = etPwd.getText().toString();
                String account = etAccount.getText().toString();

                editor.putString(accountKey,account);
                editor.putString(passwordKey,password);
                editor.putBoolean(rememberPasswordKey,true);
                //只有调用apply()或commit()方法之后，对SharedPreferences的修改才会生效
                editor.apply();
            } else {
                editor.remove(accountKey);
                editor.remove(passwordKey);
                editor.remove(rememberPasswordKey);
                editor.apply();
            }
        }
        if (view.getId() == R.id.bt_register) {
            String account = etAccount.getText().toString();
            String password = etPwd.getText().toString();

            //将用户信息添加到数据库中
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(dbHelper.getColumnUsername(),account);
            values.put(dbHelper.getColumnPassword(),password);

            long newRowId = db.insert(dbHelper.getTableName(),null,values);


            if (newRowId != -1){
                //表示成功将信息添加到数据库中
                //如果插入成功，数据库会返回插入的行的ID（或者称为记录的ID），如果插入失败，通常会返回-1
                Toast.makeText(getApplicationContext(),"Registration successful. Please log in.",Toast.LENGTH_SHORT).show();
                //清除输入框
                etAccount.setText("");
                etPwd.setText("");
            }else {
                //注册失败
            }
            db.close();
        }
    }

}
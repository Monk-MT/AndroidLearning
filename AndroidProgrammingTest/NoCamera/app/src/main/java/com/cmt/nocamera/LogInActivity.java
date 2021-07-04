package com.cmt.nocamera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cmt.nocamera.db.User;
import com.cmt.nocamera.util.LogUtil;

import org.litepal.LitePal;

import java.util.List;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText usernameEdit;
    private EditText passwordEdit;
    private ImageButton changePasswordVisibility;
    private Button registerButton;
    private Button loginButton;
    private TextView usernameError;
    private TextView passwordError;
    private String userName;
    private String passWord;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //初始化控件
        usernameEdit = findViewById(R.id.username_edit);
        passwordEdit = findViewById(R.id.password_edit);
        registerButton = findViewById(R.id.register_button);
        loginButton = findViewById(R.id.login_button);
        changePasswordVisibility = findViewById(R.id.change_password_visibility);
        usernameError = findViewById(R.id.username_error);
        passwordError = findViewById(R.id.password_error);


        //注册点击事件
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        changePasswordVisibility.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(LogInActivity.this, MainActivity.class); //用户名、密码正确则跳转
        switch (v.getId()) {
//            case R.id.username_edit:
//                break;
//            case R.id.password_edit:
//                break;
            case R.id.change_password_visibility:
                int position = passwordEdit.getSelectionStart();//记住光标开始的位置
                if(passwordEdit.getInputType()!= (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)){//隐藏密码
                    passwordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    changePasswordVisibility.setImageResource(R.drawable.visible);
                }else{//显示密码
                    passwordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    changePasswordVisibility.setImageResource(R.drawable.visible_off);
                }
                passwordEdit.setSelection(position); // 移动光标
                break;
            case R.id.register_button:
                userName = usernameEdit.getText().toString(); // 获取输入的用户名和密码
                passWord = passwordEdit.getText().toString();

                passwordError.setVisibility(View.GONE);
                usernameError.setVisibility(View.GONE);

                users = LitePal.select("userName").find(User.class); // 从数据库中获取所有用户名
                for (User u : users) { // 检查用户名是否存在
                    if (userName.equals(u.getUserName())) {
                        usernameError.setText("用户名已存在");
                        usernameError.setVisibility(View.VISIBLE);
                        if (passWord.length() < 8) { // 检查密码长度
                            passwordError.setText("请输入8位以上的密码");
                            passwordError.setVisibility(View.VISIBLE);
                            return;
                        }
                        return;
                    }
                }
                if (passWord.length() < 8) { // 检查密码长度
                    passwordError.setText("请输入8位以上的密码");
                    passwordError.setVisibility(View.VISIBLE);
                    return;
                }

                User user = new User(); // 保存用户名和密码
                user.setUserName(userName);
                user.setPassword(passWord);
                user.save();

                LogUtil.d("LogInActivity", userName + "--" + passWord);

                startActivity(intent);//注册成功后直接跳转
                finish();
                break;
            case R.id.login_button:
                userName = usernameEdit.getText().toString(); // 获取输入的用户名和密码
                passWord = passwordEdit.getText().toString();

                passwordError.setVisibility(View.GONE);
                usernameError.setVisibility(View.GONE);

                users = LitePal.select("id","userName").find(User.class); // 从数据库中获取所有用户名
                for (User u : users) { // 检查用户名是否存在
                    if (userName.equals(u.getUserName())) { // 如果用户名存在，则检查密码是否正确
                        List<User> users = LitePal.select("password").where("id = ?", String.valueOf(u.getId())).find(User.class);
                        for (User us : users) {
                            if (passWord.equals(us.getPassword())) {
                                startActivity(intent);//用户名、密码正确则跳转
                                finish();
                                return;
                            }
                        }
                        passwordError.setText("密码错误");
                        passwordError.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                usernameError.setText("用户名不存在，请注册");
                usernameError.setVisibility(View.VISIBLE);

                break;
            default:
                break;
        }
    }
}
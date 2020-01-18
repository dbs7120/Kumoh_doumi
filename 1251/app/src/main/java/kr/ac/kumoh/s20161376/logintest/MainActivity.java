package kr.ac.kumoh.s20161376.logintest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    // 비밀번호 정규식
    // 5자리이상 숫자, 영어 조합
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;

    // 이메일과 비밀번호
    private EditText editTextEmail;
    private EditText editTextPassword;

    private String email = "";
    private String password = "";
    private RadioButton option1;
    private RadioButton option2;

    private String current_ssid = "";

    //장애 비장애 구분용스트링 생성, 실제 사용자는 학번만입력되서로그인됨
    //학번 + @student.com || @disabled.com 형식
    private String emailTemp = "";
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        option1 = (RadioButton) findViewById(R.id.option1);
        option2 = (RadioButton) findViewById(R.id.option2);
        option1.setChecked(true);

        // 파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.et_eamil);
        editTextPassword = findViewById(R.id.et_password);
    }

    // WI-FI SSID 식별함수
    public String getWiFiSSID(Context mContext) {
        WifiManager manager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = manager.getConnectionInfo();
        String sSSID = wifiInfo.getSSID();
        return sSSID;
    }


    // 회원가입
    public void signUp(View view) {
        Intent Signup = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(Signup);
    }


    // 로그인 검증 및 연결
    public void signIn(View view) {
        emailTemp = checkType();
        email = editTextEmail.getText().toString() + emailTemp;
        password = editTextPassword.getText().toString();
        current_ssid = getWiFiSSID(getApplication());
        if (isValidEmail() && isValidPasswd() && current_ssid.equals("\"AndroidWifi\"")) {
            //System.out.println(current_ssid);
            loginUser(email, password);
        } else {
            AlertDialog.Builder adb = new AlertDialog.Builder(context);

            adb.setTitle("오류");

            adb.setMessage("학교 구역 내 WIFI가 아닙니다.")
                    .setCancelable(false)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //AlertDialogActivity.this.finish();
//                            ActivityCompat.finishAffinity(MainActivity.this);
//                            System.runFinalizersOnExit(true);
//                            System.exit(0);
                        }
                    });
            AlertDialog alertDialog = adb.create();
            alertDialog.show();
        }
    }

    // 이메일 유효성 검사
    private boolean isValidEmail() {
        if ((email + emailTemp).isEmpty()) {
            // 이메일 공백
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // 이메일 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 로그인 타입 결정 (일반 학생, 장애학생)
    public String checkType() {
        String tempString = "";
        if (option1.isChecked() == true)
            tempString = "@student.com";
        else if (option2.isChecked() == true)
            tempString = "@disabled.com";
        return tempString;
    }

    // 비밀번호 유효성 검사
    private boolean isValidPasswd() {
        if (password.isEmpty()) {
            // 비밀번호 공백
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }


    // 로그인
    // 이너클래스매개타입 상수화필요
    private void loginUser(final String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //로그인 성공
                        if (task.isSuccessful()) {
                            if (email.contains("student") && option1.isChecked() == true) {
                                Intent intent = new Intent(MainActivity.this, StudentMainActivity.class);
                                Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_LONG).show();
                                //intent.putExtra("logininfo", email);  //id넘길려고 navigation에
                                startActivity(intent);

                            } else if (email.contains("disabled")) {
                                Intent intent = new Intent(MainActivity.this, DisableMain.class);
                                Toast.makeText(getApplicationContext(), "로그인 되었습니다.", Toast.LENGTH_LONG).show();
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, R.string.failed_login, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
package kr.ac.kumoh.s20161376.logintest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StudentHelpProceeding extends AppCompatActivity {

    private DatabaseReference mPostReference;

    // DB2부분 변수
    private String ID; // 시간으로 활용
    private String location;
    private String work;

    private String add;
    private String det;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_help_proceeding);

        Intent intent = getIntent();

        add = intent.getExtras().getString("address");
        det = intent.getExtras().getString("help");

        TextView address = (TextView) findViewById(R.id.help_address);
        TextView detail = (TextView) findViewById(R.id.help_detail);

        address.setText(add);
        detail.setText(det);
    }


    public void complete(View view) { //완료버튼 눌렀을때

        //DB2 ID를 위한 시간
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date time = new Date();
        final String timeString = timeFormat.format(time);
        System.out.println(timeString);

        ID = timeString;
        location = add;
        work = det;
        postFirebaseDatabase(true);

        Intent intent = getIntent();
        String det = intent.getExtras().getString("help");

        String toast = "'" + det + "' 도움이 완료되었습니다.";

        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
        finish();
    }

    public void postFirebaseDatabase(boolean add) {
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if (add) {
            FirebasePost post = new FirebasePost(ID, location, work);
            postValues = post.toMap();
        }
        childUpdates.put("/HISTORY_DB/" + ID, postValues);
        mPostReference.updateChildren(childUpdates);
    }

    public void goBack2(View view) {
        finish();
    }


}

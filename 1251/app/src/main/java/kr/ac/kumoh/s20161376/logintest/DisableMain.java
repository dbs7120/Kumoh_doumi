package kr.ac.kumoh.s20161376.logintest;

import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DisableMain extends AppCompatActivity {

    private Button sendbt_location;
    private Button sendbt_location2;
    private Button getSSID;
    private TextView address;
    private EditText editLoc;
    private String editLoc2;
    private String editLoc3;
    String sendW;
    private EditText editWork;
    private TextView ssidText;

    private Double longitude = null;
    private Double latitude = null;


    private String addressString = null;
    private String addressString2 = null;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private DatabaseReference mReference1;
    private DatabaseReference mReference2;
    private ChildEventListener mChild;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();


    private String uid;
    private String destinatonUid;

    ArrayList<String> arrayList;
    ArrayList<String> arrayList2;
    ArrayList<String> arrayList3;

    ArrayAdapter<String> arrayAdapter, arrayAdapter2, arrayAdapter3;


    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disable_main);
        final TextView ttv = findViewById(R.id.textView);
        final TextView ttv2 = findViewById(R.id.textView2);
        final TextView ttv3 = findViewById(R.id.textView3);
        Spinner Bild = findViewById(R.id.build);

        arrayList = new ArrayList<>();
        arrayList.add("디지털관");
        arrayList.add("테크노관");
        arrayList.add("글로벌관");
        arrayList.add("본관");
        arrayList.add("그린에너지관");
        arrayList.add("분식당");
        arrayList.add("학생식당");

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
        Bild.setAdapter(arrayAdapter);
        Bild.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                String u_bd = arrayList.get(i);
                ttv.setText(u_bd);
                editLoc2 = u_bd;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Spinner Floor = findViewById(R.id.floor);


        arrayList2 = new ArrayList<>();

        arrayList2.add("지하1층");
        arrayList2.add("1층");
        arrayList2.add("2층");
        arrayList2.add("3층");
        arrayList2.add("4층");
        arrayList2.add("5층");


        arrayAdapter2 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList2);
        Floor.setAdapter(arrayAdapter2);
        Floor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int k, long id) {
                String u_fl = arrayList2.get(k);
                ttv2.setText(u_fl);

                editLoc3 = u_fl;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Spinner Work = findViewById(R.id.work);

        // Spinner
        arrayList3 = new ArrayList<>();
        arrayList3.add("필기보조");
        arrayList3.add("화장실");
        arrayList3.add("이동");
        arrayList3.add("식사보조");
        arrayAdapter3 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList3);
        Work.setAdapter(arrayAdapter3);
        Work.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int j, long id) {
                String sel3 = arrayList3.get(j);
                ttv3.setText(sel3);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sendbt_location = (Button) findViewById(R.id.btn_loc);
        sendbt_location2 = (Button) findViewById(R.id.btn_trans);
        address = (TextView) findViewById(R.id.et_loc1);


        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        destinatonUid = getIntent().getStringExtra("destinatonUid");


        initDatabase();

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 기본 위치정보 + 사용자 추가위치 정보 불러옴
        sendbt_location.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(DisableMain.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DisableMain.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
                    }
                    return;
                }
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                getLocation(latitude, longitude);

                String cut = addressString;
                int idx1 = cut.indexOf("\"");
                String cut1 = cut.substring(idx1 + 1);
                int idx2 = cut1.indexOf("\"");
                String cut2 = cut1.substring(0, idx2);
                addressString = cut2;
//                위치 테스트(콘솔)
//                System.out.println(cut1);
//                System.out.println(cut2);
                address.setText("" + cut2);
            }
        });


        // 파이어베이스로 데이터 저장(유저키, 위치, 내용)
        sendbt_location2.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
                String f_edloc = ttv.getText().toString() + ttv2.getText().toString();
                addressString2 = addressString + ", " + f_edloc;

                String tokenID = FirebaseInstanceId.getInstance().getToken();
                mReference = mDatabase.getReference("UserProfile");

                if (!TextUtils.isEmpty(tokenID)) {
                    senddata Senddata = new senddata();
                    Senddata.firebaseKey = tokenID;
                    Senddata.userAddress = addressString + f_edloc;
                    System.out.println(addressString2);
                    Senddata.userWork = ttv3.getText().toString();

                    mReference.child(tokenID).setValue(Senddata);

                    Toast.makeText(DisableMain.this, "전송 완료", Toast.LENGTH_SHORT).show();
                    getStudentToken();
                    Intent intent = new Intent(DisableMain.this, HelpProceeding.class);
                    startActivity(intent);
                } else
                    Toast.makeText(DisableMain.this, "전송 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 위치정보(내부에서 위도 및 경도를 통해 Google Geocoder지도 정보 불러옴)
    public void getLocation(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.KOREAN);
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                addressString = addresses.get(0).toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(addressString);
    }

    // DB초기화 및 생성
    private void initDatabase() {
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("log");
        mReference.child("log").setValue("check");

        mChild = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mReference.addChildEventListener(mChild);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReference.removeEventListener(mChild);
    }


    // Student 토큰값 받아서 DataSnapshot으로 읽어 존재 만큼 sendServerToFirebase 실행
    public void getStudentToken() {
        mReference2 = mDatabase.getReference("StudentUserProfile");
        mReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String str = messageData.getValue().toString();
                    System.out.println(str);
                    int index1 = str.indexOf("firebaseKey=");
                    int index2 = str.indexOf('}');
                    String StudentKey = str.substring(index1 + 12, index2);
                    System.out.println(StudentKey);
                    sendServerToFirebase(StudentKey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Gson okhttp 라이브러리 필요
    // JSON방식으로 데이터 푸시 전송
    // POSTMAN 체크
    public void sendServerToFirebase(String studentToken) {
        Gson gson = new Gson();
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = studentToken;
        notificationModel.notification.title = "test";
        notificationModel.notification.text = "도움수신";

        okhttp3.RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel));

        okhttp3.Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "key=AAAAU6aVW9E:APA91bE6EdhFEnWKzv1itT9bcBNNsC45Ax0xj5H7dyzLiWMfmTjajxRXIK5zecBMEfs8xlKK5aq-SrFfPGTR0Tog5CPKnu_AKUGdCIwBnelRVdFL0eHFPQAl-6I5yPbBFLCNiKWPbboY")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .build();
        okhttp3.OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }
}

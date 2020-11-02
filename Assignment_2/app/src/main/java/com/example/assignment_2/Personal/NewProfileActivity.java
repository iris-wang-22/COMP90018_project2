package com.example.assignment_2.Personal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment_2.R;
import com.example.assignment_2.Util.BitmapUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class NewProfileActivity extends AppCompatActivity {

    private TextView tv_name;
    private EditText et_age;
    private RadioGroup rp_gender;
    private RadioButton rBtn_gender;
    private Button btn_yes;
    private Button btn_no;
    private ImageView iv_image;

    protected static final int CHOOSE_PICTURE = 1;
    protected static final int TAKE_PICTURE = 0;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;

    private DatabaseReference databaseRef;
    private FirebaseAuth firebaseAuth;
    private String username, age, gender, avatar;
    private Bitmap image;

    private static final String TAG = "FIRE_BASE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

        RequestPermission();

        databaseRef = FirebaseDatabase.getInstance().getReference();
        username = getIntent().getStringExtra("username");

        age = getIntent().getStringExtra("age");
        gender = getIntent().getStringExtra("gender");
        avatar = getIntent().getStringExtra("avatar");

        initView();
        database();

        tv_name.setText(username);

        if (age != null){
            et_age.setText(age);
        }

        if(avatar != null){
            image = base64ToBitmap(avatar);
            iv_image.setImageBitmap(image);
        }

        rp_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rBtn_gender = (RadioButton) findViewById(checkedId);

            }
        });

        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoosePicDialog();
            }
        });

    }

    // RequestPermission
    private void RequestPermission(){
        String[] permissions=new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        for (String permission : permissions) {
            if ((ContextCompat.checkSelfPermission(this, permission) != PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, permissions, 101);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i=0;i<permissions.length;i++){
            String permission=permissions[i];
            if(!ActivityCompat.shouldShowRequestPermissionRationale(NewProfileActivity.this,permission)){
                AlertDialog.Builder builder=new AlertDialog.Builder(NewProfileActivity.this);
                builder.setTitle("Tips");
                builder.setMessage("Please go to the settings page to grant permission for this app, otherwise it will be unavailable.");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                return;
            }
            else if(grantResults[i]!=PERMISSION_GRANTED){
                AlertDialog.Builder builder=new AlertDialog.Builder(NewProfileActivity.this);
                builder.setTitle("Tips");
                builder.setMessage("Please grant permission, otherwise you will not be able to use this function.");
                builder.setPositiveButton("Confirm",null);
                builder.create().show();
                return;
            }
        }
    }


    //initialise
    private void initView() {
        tv_name = (TextView) findViewById(R.id.new_tv_username);
        et_age = (EditText) findViewById(R.id.new_ev_age);
        rp_gender = (RadioGroup) findViewById(R.id.new_rp_gender);
        btn_yes = (Button) findViewById(R.id.new_btn_yes);
        btn_no = (Button) findViewById(R.id.new_btn_no);
        iv_image = (ImageView) findViewById(R.id.new_iv);
    }

    /**
     * Database
     */
    private void database() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String age_path = "users/"+username+"/profile/age";
        String gender_path = "users/"+username+"/profile/gender";
        String gender_avatar = "users/"+username+"/profile/avatar";
        final DatabaseReference ageRef = database.getReference(age_path);
        final DatabaseReference genderRef = database.getReference(gender_path);
        final DatabaseReference avatarRef = database.getReference(gender_avatar);


        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //save
                if (et_age != null){
                    age = et_age.getText().toString().trim();
                    ageRef.setValue(age);
                }
                if (rBtn_gender != null){
                    gender = rBtn_gender.getText().toString().trim();
                    genderRef.setValue(gender);
                }
                if (image != null){
                    try {
                        avatar = getImageB64(image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    avatarRef.setValue(avatar);
                }

                Toast.makeText(NewProfileActivity.this, "Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(NewProfileActivity.this, PersonalActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });

        btn_no.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));


    }
    public String getImageB64(Bitmap bmp) throws IOException {

        String imageB64 = null;
        if (bmp != null){
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bmp.recycle();

            bos.flush();
            bos.close();

            byte[] byteArray = bos.toByteArray();
            imageB64 = Base64.encodeToString(byteArray, Base64.URL_SAFE);

        }
        return imageB64;
    }


    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewProfileActivity.this);
        builder.setTitle("How to get the avatar?");
        String[] items = { "Take a picture", "Choose a picture"};
        builder.setNegativeButton("Cancel", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case TAKE_PICTURE:
                        File imageFile=new File(getFilesDir(),"image.jpg");
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri= FileProvider.getUriForFile(NewProfileActivity.this,"com.example.assignment_2.provider",imageFile);
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                    case CHOOSE_PICTURE:
                        Intent openAlbumIntent;
                        if (Build.VERSION.SDK_INT<19){
                            openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            openAlbumIntent.setType("image/*");
                    } else{
                            openAlbumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        }
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                }
            }
        });
        builder.create().show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            switch (requestCode){
                case TAKE_PICTURE:
                    startPhotoZoom(tempUri);
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData());
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null){
                        setImageToView(data);
                    }
                    break;
            }
        }

    }

    /**
     * crop
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY: Ratio of width to height
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY: Crop image width and height
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /**
     * save
     *
     * @param
     *
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            image = extras.getParcelable("data");
            image = BitmapUtils.toRoundBitmap(image, tempUri); // 这个时候的图片已经被处理成圆形的了

            //saveImage(photo);
            //iv_image.setImageDrawable(null);
            iv_image.setImageBitmap(image);
        }
    }

    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.URL_SAFE);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
//
//    private void saveImage(Bitmap bitmap) {
//        File filesDir;
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            //road：storage/sdcard/Android/data/package/files
//            filesDir = this.getExternalFilesDir("");
//        } else {
//            //road：data/data/package/files
//            filesDir = this.getFilesDir();
//        }
//        //System.out.println("--------------------/n"+filesDir);
//
//        FileOutputStream fos = null;
//        try {
//            File file = new File(filesDir + "/icon.png");
//
//            long size = file.length();
//            fos = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            fos.flush();
//            fos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (fos != null) {
//                try {
//                    fos.flush();
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

//
//    //whether the image is local
//    private boolean readImage() {
//        File filesDir;
//        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//            //storage/sdcard/Android/data/package/files
//            filesDir = getExternalFilesDir("");
//        }else{
//            //data/data/package/files
//            filesDir = getFilesDir();
//        }
//        File file = new File(filesDir,"icon.png");
//        if(file.exists()){
//            //storage--->ram
//            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//            iv_image.setImageBitmap(bitmap);
//            return true;
//        }
//        return false;
//    }
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    private String getPath(Uri uri) {
//        int sdkVersion = Build.VERSION.SDK_INT;
//        //higher version of 4.4.2
//        if (sdkVersion >= 19) {
//            Log.e("TAG", "uri auth: " + uri.getAuthority());
//            if (isExternalStorageDocument(uri)) {
//                String docId = DocumentsContract.getDocumentId(uri);
//                String[] split = docId.split(":");
//                String type = split[0];
//                if ("primary".equalsIgnoreCase(type)) {
//                    return Environment.getExternalStorageDirectory() + "/" + split[1];
//                }
//            } else if (isDownloadsDocument(uri)) {
//                final String id = DocumentsContract.getDocumentId(uri);
//                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
//                        Long.valueOf(id));
//                return getDataColumn(this, contentUri, null, null);
//            } else if (isMediaDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//                Uri contentUri = null;
//                if ("image".equals(type)) {
//                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                } else if ("video".equals(type)) {
//                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                } else if ("audio".equals(type)) {
//                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                }
//                final String selection = "_id=?";
//                final String[] selectionArgs = new String[]{split[1]};
//                return getDataColumn(this, contentUri, selection, selectionArgs);
//            } else if (isMedia(uri)) {
//                String[] proj = {MediaStore.Images.Media.DATA};
//                Cursor actualimagecursor = this.managedQuery(uri, proj, null, null, null);
//                int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                actualimagecursor.moveToFirst();
//                return actualimagecursor.getString(actual_image_column_index);
//            }
//        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            // Return the remote address
//            if (isGooglePhotosUri(uri))
//                return uri.getLastPathSegment();
//            return getDataColumn(this, uri, null, null);
//        }
//        // File
//        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            return uri.getPath();
//        }
//        return null;
//    }
//
//    /**
//     * uri路径查询字段
//     *
//     * @param context
//     * @param uri
//     * @param selection
//     * @param selectionArgs
//     * @return
//     */
//    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
//        Cursor cursor = null;
//        final String column = "_data";
//        final String[] projection = {column};
//        try {
//            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
//            if (cursor != null && cursor.moveToFirst()) {
//                final int index = cursor.getColumnIndexOrThrow(column);
//                return cursor.getString(index);
//            }
//        } finally {
//            if (cursor != null)
//                cursor.close();
//        }
//        return null;
//    }
//    private boolean isExternalStorageDocument(Uri uri) {
//        return "com.android.externalstorage.documents".equals(uri.getAuthority());
//    }
//    public static boolean isDownloadsDocument(Uri uri) {
//        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
//    }
//    public static boolean isMediaDocument(Uri uri) {
//        return "com.android.providers.media.documents".equals(uri.getAuthority());
//    }
//    public static boolean isMedia(Uri uri) {
//        return "media".equals(uri.getAuthority());
//    }
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is Google Photos.
//     */
//    public static boolean isGooglePhotosUri(Uri uri) {
//        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
//    }
//    /**
//     * 判断本地是否有该图片,没有则去联网请求
//     * */
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(readImage()){
//            return;
//        }
//    }

}
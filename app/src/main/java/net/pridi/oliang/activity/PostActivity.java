package net.pridi.oliang.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.okhttp.OkHttpStack;
import net.pridi.oliang.Manifest;
import net.pridi.oliang.R;
import net.pridi.oliang.adapter.CatListAdapter;
import net.pridi.oliang.dao.CatItemCollectionDao;
import net.pridi.oliang.dao.PostItemDao;
import net.pridi.oliang.dao.PostNewDao;
import net.pridi.oliang.manager.HttpManager;
import net.pridi.oliang.manager.http.ApiService;
import net.pridi.oliang.manager.http.FileUploadService;
import net.pridi.oliang.manager.http.ServiceGenerator;
import net.pridi.oliang.utils.FileUtils;
import net.pridi.oliang.utils.ProgressRequestBody;
import net.pridi.oliang.view.AndroidPermissions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class PostActivity extends AppCompatActivity implements ProgressRequestBody.UploadCallbacks {
    public static final int PICK_IMAGE = 100;
    public static final int PICK_VDO = 200;
    private static final int REQUEST_VIDEO_CAPTURE = 400;
    private static final int REQUEST_IMAGE_CAPTURE = 300;
    EditText etTitle ;
    EditText etContent;
    private Spinner spCatgeogry;
    private String strImage="";
    private String strVdo="";
    private Toolbar toolbar;
    ProgressBar progressBar;
    ImageView ivThumbImage;
    private CatListAdapter catListAdapter;
    String catname;
    Uri selectedImage;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        initInstance();

    }

    private void initInstance() {

        catListAdapter=new CatListAdapter();
        spCatgeogry= (Spinner) findViewById(R.id.spCategory);

        Call<CatItemCollectionDao> call = HttpManager.getInstance().getService().loadCatList();
        call.enqueue(new Callback<CatItemCollectionDao>() {
            @Override
            public void onResponse(Call<CatItemCollectionDao> call, Response<CatItemCollectionDao> response) {
                if(response.isSuccessful()) {
                    if(response.body()!=null) {
                        catListAdapter.setDao(response.body());
                        spCatgeogry.setAdapter(catListAdapter);
                        spCatgeogry.setSelection(0);
                        //showToast("catname: "+catListAdapter.getDao().getData().get((spCatgeogry.getSelectedItemPosition())).getId());
                    }
                }else{

                }
            }

            @Override
            public void onFailure(Call<CatItemCollectionDao> call, Throwable t) {

            }
        });

        toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etTitle = (EditText) findViewById(R.id.etTitle);
        etContent= (EditText) findViewById(R.id.etContent);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        ivThumbImage= (ImageView) findViewById(R.id.ivThumbImage);
        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNew();
            }
        });

        Button btnImage = (Button) findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);

            }
        });

        Button btnImgCam = (Button) findViewById(R.id.btnImgCam);
        btnImgCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }


                /*String filename="tmp.jpg";
                Intent camIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                File photo = new File(Environment.getExternalStorageDirectory(), filename);
                camIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photo));
                imageUri = Uri.fromFile(photo);
                startActivityForResult(camIntent, REQUEST_IMAGE_CAPTURE);
                */

            }
        });


        Button btnVdo = (Button) findViewById(R.id.btnVdo);
        btnVdo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if(intent.resolveActivity(getPackageManager())!=null) {
                    startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
                }

            }
        });
    }

    private void sendPost(int category,String title,String content,String image,String vdo) {
        Log.d("API"," sending ");
        Call<PostItemDao> call = HttpManager.getInstance().getService().postNewPost(category,title,content,strImage,strVdo);
        Log.d("API"," calling ");
        call.enqueue(new Callback<PostItemDao>() {
            @Override
            public void onResponse(Call<PostItemDao> call, Response<PostItemDao> response) {
                Log.d("API","on Response");
                finish();
            }

            @Override
            public void onFailure(Call<PostItemDao> call, Throwable t) {
                Log.d("API","on Fail "+t.getMessage());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("API"," on Result "+resultCode+ " RESULT_OK "+RESULT_OK);
        Uri u= data.getData();
        //String fileName=u.getLastPathSegment();

        Log.d("API"," before uploadFile resultCode:"+resultCode+" requestCode:"+requestCode);
        if(resultCode==RESULT_OK){
            if(requestCode==PICK_IMAGE) {
                Log.d("API"," uploading Image ");


            }
            if(requestCode==REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imgBitmap = (Bitmap) extras.get("data");
                u=getImageUri(this,imgBitmap);
                Log.d("API"," uploading from Camera Image ");
                ivThumbImage.setImageBitmap(imgBitmap);


            }
            if(requestCode==REQUEST_VIDEO_CAPTURE){
                //pathToStoredVideo = getRealPathFromURIPath(u, PostActivity.this);
                //uploadVideoToServer(pathToStoredVideo);

                Log.d("API"," uploading VDO ");
                /* show clip thumbnail
                MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
                try {
                    metadataRetriever.setDataSource(getRealPathFromURIPath(u,this));
                    String duration=metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    long time=Long.valueOf(duration)/2;
                    Bitmap bitmap = metadataRetriever.getFrameAtTime(time, MediaMetadataRetriever.OPTION_NEXT_SYNC);
                    ivThumbImage.setImageBitmap(bitmap);

                    //now convert to base64
                } catch (Exception ex) {
                }
                */

            }
            uploadFile(u,requestCode);
        }

        Log.d("API"," after uploadFile ");

    }
    private void uploadFile(Uri fileUri, final int requestCode) {
        Log.d("API"," start uploadFile fileUri "+fileUri);
        // create upload service client
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri

        File file = FileUtils.getFile(this, fileUri);
        Log.d("API","file:"+file);
        final String fileName=file.getName();
        Log.d("API"," filename:"+fileName);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContentResolver().getType(fileUri)),
                        file
                );

        ProgressRequestBody fileBody= new ProgressRequestBody(file,this);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("userfile", file.getName(), fileBody);

        // add another part within the multipart request
        String descriptionString = "userfile";
        RequestBody description =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, descriptionString);

        Log.d("API","body "+body.toString() );
        // finally, execute the request

        Call<ResponseBody> call = HttpManager.getInstance().getService().upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success "+response.toString());
                fileName.replace(" ","_");
                if( requestCode ==PICK_IMAGE){
                    strImage=fileName;
                }
                if( requestCode ==REQUEST_IMAGE_CAPTURE){
                    strImage=fileName;
                }
                if( requestCode ==REQUEST_VIDEO_CAPTURE){
                    strVdo=fileName;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    @Override
    public void onProgressUpdate(int percentage) {
        progressBar.setProgress(percentage);
    }

    @Override
    public void onError() {
        // do something;
    }

    @Override
    public void onFinish() {
        progressBar.setProgress(100);
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_post){
            postNew();
            return true;
        }
        if(item.getItemId()== android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void postNew(){
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        int category= catListAdapter.getDao().getData().get((spCatgeogry.getSelectedItemPosition())).getId();
        sendPost(category,title,content,strImage,strVdo);

    }

    private void showToast(String text){
        Toast.makeText(Contextor.getInstance().getContext(),
                text,
                Toast.LENGTH_SHORT).show();
    }

}
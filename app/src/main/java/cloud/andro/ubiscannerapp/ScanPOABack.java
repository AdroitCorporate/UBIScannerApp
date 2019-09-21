package cloud.andro.ubiscannerapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompatExtras;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adroit.photobarcodelib.PhotoBarcodeScanner;
import com.adroit.photobarcodelib.PhotoBarcodeScannerBuilder;
import com.android.internal.http.multipart.MultipartEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.mime.content.ByteArrayBody;
import cz.msebera.android.httpclient.entity.mime.content.ContentBody;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.entity.mime.content.StringBody;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import uk.co.senab.photoview.PhotoViewAttacher;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

public class ScanPOABack extends AppCompatActivity {

    private static final String url_checkphoto = "http://192.168.100.165:90/PhpFiles/UBI/checkingphotos.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PROFILE = "data";
    ProgressDialog prgDialog;
    String ImageName = "POA", ImageAddress1 = "proofofaddress_back_jpeg", ImageAddress2 = "proofofaddress_back_tiff";
    String encodedString;
    RequestParams params = new RequestParams();
    String imgPath, fileName;
    Bitmap bitmap, lesimg;
    private static int RESULT_LOAD_IMG = 1;
    private static int REQUEST_IMAGE_CAPTURE = 1;
    private static String TIME_STAMP="null";

    Handler handler;
    Boolean isSystemUiShown;
    int SYSTEM_UI_HIDE_DELAY = 2000;

    String TempCustomerNumber;
    Button Rescan, Save;
    TextView CustomerID;
    FloatingActionButton fabPicture;
    ImageView imageView;
    PhotoBarcodeScanner photoBarcodeScanner;
    TextView textView;
    Bitmap scaledBitmap, fullBitmapImage, fullScaledBitmap, actualBitmapImage;
    String imagePath, fullImagePath, imagePathForJpeg;
    PhotoViewAttacher photoAttacher;
    private ComponentName adminComponent;
    private DevicePolicyManager devicePolicyManager;
    Bitmap bm, bmp;
    Button Back, Upload;
    Uri imageUri;
    String photourl;

    private Uri filePath;


    private static final String UPLOAD_URL = "http://192.168.100.165:90/PhpFiles/UBI/imageupload.php";

    public String SERVER = "http://192.168.100.165:90/PhpFiles/imageupload.php",
            timestamp;

    private Context mCtx;
    public static final String CHANNNEL_ID = "SimplifiedCodingChannel";
    public static final String CHANNEL_NAME = "SimplifiedCodingChannel";
    public static final String CHANNEL_DESC = "This is a channel for Notifications";

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RESULT_SELECT_IMAGE = 1;
    private static final int REQUEST_PICK_IMAGE = 1365;
    private static final int REQUEST_CROP_IMAGE = 1342;
    public static final int DPM_ACTIVATION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_poaback);

        Back = (Button) findViewById(R.id.button);
        Upload = (Button) findViewById(R.id.button2);


        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ScanPOABack.this, ScanPOAFront.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            }
        });

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(ScanPOAFront.this, ScanPOABack.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);*/
            }
        });

        handler = new Handler();

        View decorView1 = getWindow().getDecorView();
        decorView1.setOnSystemUiVisibilityChangeListener(
                new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            handler.postDelayed(checkSystemUiRunnable, SYSTEM_UI_HIDE_DELAY);
                            isSystemUiShown = true;
                        } else {
                            isSystemUiShown = false;
                        }
                    }
                });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Do the file write
        } else {
            // Request permission from the user
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            Toast.makeText(getApplicationContext(),"Permission Granted",Toast.LENGTH_SHORT).show();

        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx, CHANNNEL_ID);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNNEL_ID, CHANNEL_NAME, importance);
            mChannel.setDescription(CHANNEL_DESC);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.TRANSPARENT);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
            mBuilder.setColor(Color.TRANSPARENT);
        }else{
            mBuilder.setSmallIcon(R.drawable.ic_camera_flash_on);
        }

        SharedPreferences pref = getSharedPreferences("loginData", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        TempCustomerNumber = pref.getString("tempcustomerid", "null");

        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(getPackageName(),getPackageName() + ".DeviceAdministrator");

        // Request device admin activation if not enabled.
        if (!devicePolicyManager.isAdminActive(adminComponent)) {

            Intent activateDeviceAdmin = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            activateDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
            startActivityForResult(activateDeviceAdmin, DPM_ACTIVATION_REQUEST_CODE);

        }

        devicePolicyManager.setCameraDisabled(adminComponent, false);

        CustomerID = (TextView) findViewById(R.id.textView6);
        fabPicture = findViewById(R.id.floatingActionButton3);

        CustomerID.setText(TempCustomerNumber);

        Rescan = (Button) findViewById(R.id.rescan);
        Save = (Button) findViewById(R.id.save);
        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.text);

        fabPicture.setOnClickListener(view -> takePicture());
        Rescan.setOnClickListener(view -> takePicture());
        Save.setOnClickListener(view -> uploadPicture());

        prgDialog = new ProgressDialog(this);

        prgDialog.setCancelable(false);

        new Checkingphotos().execute();

    }

    private class Checkingphotos extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Display progress bar

        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();
            httpParams.put("custid", TempCustomerNumber);
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    url_checkphoto , "POST", httpParams);
            try {

                int success = jsonObject.getInt(TAG_SUCCESS);
                JSONObject user;


                if (success == 1) {

                    JSONArray adverts = jsonObject.getJSONArray(TAG_PROFILE);

                    for (int i = 0; i < adverts.length(); i++) {

                        JSONObject c = adverts.getJSONObject(i);

                        photourl = c.getString("proofofaddress_back_jpeg");

                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {

            runOnUiThread(new Runnable() {
                public void run() {
                    //Populate the Edit Texts once the network activity is finished executing
                }
            });

            if(!"null".equals(photourl) || photourl != null){
                Picasso.with(ScanPOABack.this)
                        .load(photourl)
                        .into(imageView);
            }

        }


    }

    private void takePicture(){
        photoBarcodeScanner = new PhotoBarcodeScannerBuilder(this)
                .withTakingPictureMode()
                .withPictureListener(file -> {
                    //textView.setVisibility(View.INVISIBLE);
                    fabPicture.setVisibility(View.INVISIBLE);
                    Rescan.setVisibility(View.VISIBLE);
                    Save.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageURI(Uri.fromFile(file));


                    Intent intent = new Intent(this, CropImageActivity.class);
                    Uri imageUri = Uri.fromFile(file);//getPickImageResultUri(data);
                    intent.putExtra(CropImageActivity.EXTRA_IMAGE_URI, imageUri.toString());
                    intent.putExtra("image_name",ImageName);
                    startActivityForResult(intent, REQUEST_CROP_IMAGE);
                })
                .build();
        photoBarcodeScanner.start();
    }

    private void uploadPicture(){

        uploadMultipart();
        uploadMultipartForJpeg();
        //new Upload(fullBitmapImage,"IMG_").execute()
        Toast.makeText(getApplicationContext(),"Image Successfully Saved!",Toast.LENGTH_SHORT).show();

        Rescan.setVisibility(View.INVISIBLE);
        Save.setVisibility(View.INVISIBLE);
        fabPicture.setVisibility(View.VISIBLE);
        //imageView.setVisibility(View.INVISIBLE);
        //textView.setVisibility(View.INVISIBLE);

    }

    public void uploadMultipart() {

        //getting the actual path of the image
        String path = imagePath;//"/storage/emulated/0/MyFolder/Images/1566903355103.jpg";//getPath(filePath);

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                    .addFileToUpload(path, "profilepic") //Adding file
                    .addParameter("id", TempCustomerNumber)
                    .addParameter("imageaddress", ImageAddress2)//Adding text parameter to the request
                    .setMaxRetries(3)
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    public void uploadMultipartForJpeg() {

        //getting the actual path of the image
        String path = imagePathForJpeg;//"/storage/emulated/0/MyFolder/Images/1566903355103.jpg";//getPath(filePath);

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                    .addFileToUpload(path, "profilepic") //Adding file
                    .addParameter("id", TempCustomerNumber)
                    .addParameter("imageaddress", ImageAddress1)//Adding text parameter to the request
                    .setMaxRetries(3)
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }





    private class Upload extends AsyncTask<Void,Void,String> {
        private Bitmap image;
        private String name;

        public Upload(Bitmap image,String name){
            this.image = image;
            this.name = name;
        }

        @Override
        protected String doInBackground(Void... params) {
            byte[] data = null;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //compress the image to jpg format
            image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            /*
             * encode image to base64 so that it can be picked by saveImage.php file
             * */

            String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            data = baos.toByteArray();
            //generate hashMap to store encodedImage and the name

            File file = new File(imagePath);
            ContentBody cbFile = new FileBody(file, "image/jpeg");

            HashMap<String,String> detail = new HashMap<>();
            detail.put("id", "1");
            detail.put("name", "test123");
            detail.put("contactno", "123456");
            detail.put("description", "description");
            detail.put("profilepic", String.valueOf(cbFile));

            String response;
            try{
                //convert this HashMap to encodedUrl to send to php file

                String dataToSend = hashMapToUrl(detail);
                //make a Http request and send data to saveImage.php file

                response = Request.post(SERVER,dataToSend);

                //return the response

                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();


                return response;

            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG,"ERROR  "+e);
                return null;
            }

        }



        @Override
        protected void onPostExecute(String s) {
            //show image uploaded
            Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();
        }
    }


    private String hashMapToUrl(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //filePath = data.getData();

        if(photoBarcodeScanner == null){
            photoBarcodeScanner.onActivityResult(requestCode, resultCode, data);
        }

        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == REQUEST_PICK_IMAGE) {
                Intent intent = new Intent(this, CropImageActivity.class);
                imageUri = getPickImageResultUri(data);
                intent.putExtra(CropImageActivity.EXTRA_IMAGE_URI, imageUri.toString());
                startActivityForResult(intent, REQUEST_CROP_IMAGE);
            }
            else if(requestCode == REQUEST_CROP_IMAGE) {


                SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_HHmm_", Locale.US);
                Date now = new Date();

                System.out.println("Image crop success :"+data.getStringExtra(CropImageActivity.CROPPED_IMAGE_PATH));
                File croppedImageFile = new File(new File(data.getStringExtra(CropImageActivity.CROPPED_IMAGE_PATH), formatter.format(now)+ ImageName +".tif").getAbsolutePath());

                File fullImageFile = new File(new File(data.getStringExtra(CropImageActivity.ACTUAL_IMAGE_PATH), "baseimage.tif").getAbsolutePath());


                imagePath = new File(data.getStringExtra(CropImageActivity.CROPPED_IMAGE_PATH), formatter.format(now)+ ImageName +".tif").getAbsolutePath();
                imagePathForJpeg = new File(data.getStringExtra(CropImageActivity.CROPPED_IMAGE_PATH_JPEG), formatter.format(now)+ ImageName +".jpeg").getAbsolutePath();
                fullImagePath = new File(data.getStringExtra(CropImageActivity.ACTUAL_IMAGE_PATH), "baseimage.tif").getAbsolutePath();

                //converting path into bitmap image
              /*  actualBitmapImage = BitmapFactory.decodeFile(imagePath);
                fullBitmapImage = BitmapFactory.decodeFile(fullImagePath);*/

                //saving one bitmap image to another bitmap image
                // lesimg=fullBitmapImage;

                // try catch for internal storage
                /*
                try {
                    saveJPEGImageToExternal(fullImagePath , fullBitmapImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                //setting image path
                Intent result = new Intent();
                result.putExtra("image_path", imagePath);


                //Uri imageUri = Uri.fromFile(file);


                // set cropped image to imageview
                imageView.setImageURI(Uri.fromFile(croppedImageFile));

                //for image zoom in zoom out we use photo attacher.
                photoAttacher= new PhotoViewAttacher(imageView);
                photoAttacher.update();

              /*  imageView.buildDrawingCache();
                bm = imageView.getPointerIcon();*/


                //not use
               /* imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();
                bm = imageView.getDrawingCache();*/


                //compressImage(imagePath);


                //finally setting result
                setResult(Activity.RESULT_OK, result);
                //finish();
            }
        }
        else {
            System.out.println("Image crop failed");
            setResult(Activity.RESULT_CANCELED);
            //finish();
        }
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();

        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.tif"));
        }

        return outputFileUri;
    }

    /*public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 360.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[10 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            //scaledBitmap.compress(Bitmap.CompressFormat.PNG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Shobhit");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }*/

    @Override
    protected void onPause(){
        super.onPause();
        devicePolicyManager.setCameraDisabled(adminComponent, true);
    }
    @Override
    protected void onResume(){
        super.onResume();
        devicePolicyManager.setCameraDisabled(adminComponent, false);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        //              for status bar
        //              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        //              | View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    private final Runnable checkSystemUiRunnable = new Runnable() {
        @Override
        public void run() {
            checkHideSystemUI();
        }
    };

    private void checkHideSystemUI() {
        // Check if system UI is shown and hide it by post a delayed handler
        if (isSystemUiShown) {
            hideSystemUI();
            handler.postDelayed(checkSystemUiRunnable, SYSTEM_UI_HIDE_DELAY);
        }
    }


}

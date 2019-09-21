package cloud.andro.ubiscannerapp;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*import android.support.v7.app.AppCompatActivity;*/

public class CropImageActivity extends AppCompatActivity implements CropImageView.OnSetImageUriCompleteListener, CropImageView.OnGetCroppedImageCompleteListener {

    private static final int DEFAULT_ASPECT_RATIO_VALUES = 100;

    Uri imageUri;

    public static final String CROPPED_IMAGE_PATH = "cropped_image_path";
    public static final String CROPPED_IMAGE_PATH_JPEG = "cropped_image_path_jpeg";
    public static final String ACTUAL_IMAGE_PATH = "actual_image_path";

    public static final String EXTRA_IMAGE_URI = "cropped_image_path";

    public static final String FIXED_ASPECT_RATIO = "extra_fixed_aspect_ratio";
    public static final String EXTRA_ASPECT_RATIO_X = "16";
    public static final String EXTRA_ASPECT_RATIO_Y = "9";

    private static final String ASPECT_RATIO_X = "16";

    private static final String ASPECT_RATIO_Y = "9";

    private CropImageView mCropImageView;

    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;

    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;

    private boolean isFixedAspectRatio = true;

    Bitmap croppedImage, fullBitmapImage;
    Button Cancel, Crop;
    String ImageName;
    //endregion

    // Saves the state upon rotating the screen/restarting the activity
    @Override
    protected void onSaveInstanceState(@SuppressWarnings("NullableProblems") Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }

    // Restores the state upon rotating the screen/restarting the activity
    @Override
    protected void onRestoreInstanceState(@SuppressWarnings("NullableProblems") Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        if(!getIntent().hasExtra(EXTRA_IMAGE_URI)) {
            cropFailed();
            return;
        }

        isFixedAspectRatio = getIntent().getBooleanExtra(FIXED_ASPECT_RATIO , true);
        mAspectRatioX = getIntent().getIntExtra(EXTRA_ASPECT_RATIO_X, DEFAULT_ASPECT_RATIO_VALUES);
        mAspectRatioY = getIntent().getIntExtra(EXTRA_ASPECT_RATIO_Y, DEFAULT_ASPECT_RATIO_VALUES);

        Intent intent = getIntent();
        ImageName = intent.getStringExtra("image_name");

        //Toast.makeText(mCropImageView.getContext(), ImageName, Toast.LENGTH_LONG).show();


        imageUri = Uri.parse(getIntent().getStringExtra(EXTRA_IMAGE_URI));
        // Initialize components of the app
        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);

        Cancel = (Button) findViewById(R.id.cancel);
        Crop = (Button) findViewById(R.id.crop);

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropFailed();
            }
        });

        Crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCropImageView.getCroppedImageAsync(mCropImageView.getCropShape(), 2216, 1220);
            }
        });

        mCropImageView.setAspectRatio(10, 10);
        mCropImageView.setFixedAspectRatio(true);
        //mCropImageView.setScaleType(ImageView.ScaleType.MATRIX);
        mCropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
        mCropImageView.setShowProgressBar(true);

        //mCropImageView.setLayoutParams(params);

        //mCropImageView.setCropRect(new Rect(100, 1500, 100, 100));

        //mCropImageView.setShowCropOverlay(false);

        //mCropImageView.setSnapRadius(100);



        //mCropImageView.setPadding(0,0,0,250);

        //mCropImageView.setCropRect(new Rect(0, 1000, 0, 0));



        //mCropImageView.setPadding(0,0,0,200);

        // If you want to fix the aspect ratio, set it to 'true'
        //mCropImageView.setFixedAspectRatio(isFixedAspectRatio);

        if (savedInstanceState == null) {
            mCropImageView.setImageUriAsync(imageUri);
           /* mCropImageView.setY(100);
            mCropImageView.setScaleX(1);
            mCropImageView.setScaleY(1);*/
        }
    }

    private void cropFailed() {
        Toast.makeText(mCropImageView.getContext(), "Image crop failed", Toast.LENGTH_LONG).show();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_crop_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_crop) {
           *//* mCropImageView.setX(200);
            mCropImageView.setCropRect(new Rect(0, 0, 0, 400));*//*
            //mCropImageView.setCropRect(new Rect(100, 0, 100, 0));
            mCropImageView.getCroppedImageAsync(mCropImageView.getCropShape(), 2216, 1220);
            return true;
        }
        else  if (id == R.id.action_cancel) {
            cropFailed();
            return false;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnGetCroppedImageCompleteListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCropImageView.setOnSetImageUriCompleteListener(null);
        mCropImageView.setOnGetCroppedImageCompleteListener(null);
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            //Toast.makeText(mCropImageView.getContext(), "Image load successful", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(mCropImageView.getContext(), "Image load failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            Toast.makeText(mCropImageView.getContext(), "Unable to load image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onGetCroppedImageComplete(CropImageView view, Bitmap bitmap, Exception error) {
        if (error == null) {

            croppedImage = bitmap;
            try {
                String pathfortif = saveToInternalStorage(this, bitmap);

                String pathforjpeg = saveToInternalStorage1(this, bitmap);

                fullBitmapImage=  MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                String newpath =  saveFullImageToInternalStorage(this, fullBitmapImage);

                Intent resultIntent = new Intent();
                resultIntent.putExtra(CROPPED_IMAGE_PATH, pathfortif);
                resultIntent.putExtra(CROPPED_IMAGE_PATH_JPEG, pathforjpeg);
                resultIntent.putExtra(ACTUAL_IMAGE_PATH, newpath);

                setResult(Activity.RESULT_OK, resultIntent);

                finish();
            } catch (IOException e) {
                e.printStackTrace();
                cropFailed();
            }
        } else {
            cropFailed();
        }
    }

    private String saveToInternalStorage(Context context, Bitmap bitmapImage) throws IOException {
        ContextWrapper cw = new ContextWrapper(context);

        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_HHmm_", Locale.US);
        Date now = new Date();
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,formatter.format(now)+ ImageName +".tif");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            //Bitmap scaledBitmap = getCompressedBitmap(bitmapImage);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 80, fos);



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
        return directory.getAbsolutePath();
    }

    private String saveToInternalStorage1(Context context, Bitmap bitmapImage) throws IOException {
        ContextWrapper cw = new ContextWrapper(context);

        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_HHmm_", Locale.US);
        Date now = new Date();
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,formatter.format(now)+ ImageName +".jpeg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            //Bitmap scaledBitmap = getCompressedBitmap(bitmapImage);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 80, fos);



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
        return directory.getAbsolutePath();
    }


    private String saveFullImageToInternalStorage(Context context, Bitmap bitmapImage) throws IOException {
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("baseImageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"baseimage.tif");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            //Bitmap scaledBitmap = getCompressedBitmap(bitmapImage);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 70, fos);



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
        return directory.getAbsolutePath();
    }


}

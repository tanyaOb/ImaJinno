package com.imajinno.tanya.ap9;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;
import java.io.OutputStream;


import static com.imajinno.tanya.ap9.R.layout.fb_activity;


public class MainActivity extends Activity implements OnClickListener {

    InterstitialAd mInterstitialAd;
    private InterstitialAd interstitial;
    AdRequest adRequest;

    public String image;
    Dialog CC;
    Dialog CeS;
    Dialog ShareFB;
    float brushSize = 20;
    int brushColor = 0xFF660000;
    float previousSize;
    int previousColor;
    public static ImageDrawing MyImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton sizeB = (ImageButton) findViewById(R.id.size_button);
        sizeB.setOnClickListener(this);

        adRequest = new AdRequest.Builder().build();

        // Prepare the Interstitial Ad
        interstitial = new InterstitialAd(MainActivity.this);
// Insert the Ad Unit ID
        interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));
        //interstitial.loadAd(adRequest);
        interstitial.loadAd(adRequest);
        // Prepare an Interstitial Ad Listener
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
// Call displayInterstitial() function
                //displayInterstitial();
            }
        });
    }

    public void displayInterstitial() {
// If Ads are loaded, show Interstitial else show nothing.
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setBrush(View v) {
        MyImage = (ImageDrawing) findViewById(R.id.drawing);
        if ((previousSize != 0) & (previousColor != 0)) {
            brushSize = previousSize;
            brushColor = previousColor;
            MyImage.paintColor = brushColor;
            MyImage.paintBrushSize = brushSize;
            MyImage.setupDrawing();
            MyImage.invalidate();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "You already took pencil!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void Save(View v) {
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Save drawing");
        saveDialog.setMessage("Save drawing to device Gallery?");
        saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String filename = "DrawingApp" + System.currentTimeMillis();
                ContentValues v = new ContentValues();
                v.put(MediaStore.Images.Media.TITLE, filename);
                v.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
                v.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, v);
                ImageDrawing MyImage = (ImageDrawing) findViewById(R.id.drawing);
                MyImage.setDrawingCacheEnabled(true);
                Bitmap CB = MyImage.getDrawingCache();

                try {
                    OutputStream outStream = getContentResolver().openOutputStream(uri);
                    CB.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    MyImage.setDrawingCacheEnabled(false);
                    outStream.flush();
                    outStream.close();
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "File was successfully saved!", Toast.LENGTH_SHORT);
                    toast.show();
                } catch (IOException e) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Error occurred!", Toast.LENGTH_SHORT);
                    toast.show();
                    MyImage.setDrawingCacheEnabled(false);
                }
            }
        });
        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        saveDialog.show();
    }

    @Override
    public void onClick(View view) {
        final ImageDrawing MyImage = (ImageDrawing) findViewById(R.id.drawing);
        final Dialog SZ = new Dialog(this);
        SZ.setTitle("Input size of brush!");
        SZ.setContentView(R.layout.size_dialog);
        SZ.show();
        Button bs = (Button) SZ.findViewById(R.id.sizeOk);
        EditText pb_size = (EditText) SZ.findViewById(R.id.brush_size);
        bs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pb_size = (EditText) SZ.findViewById(R.id.brush_size);
                if (!pb_size.getText().toString().equals("")) {
                    MyImage.setCurrentSize(Float.parseFloat(pb_size.getText().toString()));
                    MyImage.setupDrawing();
                    MyImage.invalidate();
                    SZ.dismiss();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Input size of brush!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

        });
    }

    public void ChooseColor(View view) {
        ImageDrawing MyImage = (ImageDrawing) findViewById(R.id.drawing);
        CC = new Dialog(this);
        CC.setTitle("Choose color");
        CC.setContentView(R.layout.color_dialog);
        CC.show();
        final SeekBar alphaSeekBar = (SeekBar) CC.findViewById(R.id.alphaSeekBar);
        final SeekBar redSeekBar = (SeekBar) CC.findViewById(R.id.redSeekBar);
        final SeekBar greenSeekBar = (SeekBar) CC.findViewById(R.id.greenSeekBar);
        final SeekBar blueSeekBar = (SeekBar) CC.findViewById(R.id.blueSeekBar);
        alphaSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        redSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        greenSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        blueSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        final int color = MyImage.paintColor;
        alphaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));
        Button setColorButton = (Button) CC.findViewById(R.id.setColorButton);
        setColorButton.setOnClickListener(setColorButtonListener);
    }

    private OnSeekBarChangeListener colorSeekBarChanged = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            SeekBar alphaSeekBar = (SeekBar) CC.findViewById(R.id.alphaSeekBar);
            SeekBar redSeekBar = (SeekBar) CC.findViewById(R.id.redSeekBar);
            SeekBar greenSeekBar = (SeekBar) CC.findViewById(R.id.greenSeekBar);
            SeekBar blueSeekBar = (SeekBar) CC.findViewById(R.id.blueSeekBar);
            View colorView = (View) CC.findViewById(R.id.colorView);
            colorView.setBackgroundColor(Color.argb(alphaSeekBar.getProgress(), redSeekBar.getProgress(),
                    greenSeekBar.getProgress(), blueSeekBar.getProgress()));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private OnClickListener setColorButtonListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            ImageDrawing MyImage = (ImageDrawing) findViewById(R.id.drawing);
            SeekBar alphaSeekBar = (SeekBar) CC.findViewById(R.id.alphaSeekBar);
            SeekBar redSeekBar = (SeekBar) CC.findViewById(R.id.redSeekBar);
            SeekBar greenSeekBar = (SeekBar) CC.findViewById(R.id.greenSeekBar);
            SeekBar blueSeekBar = (SeekBar) CC.findViewById(R.id.blueSeekBar);
            MyImage.setCurrentColor(Color.argb(alphaSeekBar.getProgress(), redSeekBar.getProgress(),
                    greenSeekBar.getProgress(), blueSeekBar.getProgress()));
            MyImage.setupDrawing();
            MyImage.invalidate();
            CC.dismiss();
            CC = null;
        }
    };

    public void SetEraser(View view) {
        ImageDrawing MyImage = (ImageDrawing) findViewById(R.id.drawing);
        CeS = new Dialog(this);
        CeS.setTitle("Input eraser width");
        CeS.setContentView(R.layout.erase_dialog);
        CeS.show();
        Button ses = (Button) CeS.findViewById(R.id.setEraseSize);
        previousColor = MyImage.paintColor;
        previousSize = MyImage.paintBrushSize;
        ses.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageDrawing MyImage = (ImageDrawing) findViewById(R.id.drawing);
                final EditText sb = (EditText) CeS.findViewById(R.id.eraser_width);
                if (!sb.getText().toString().equals("")) {
                    Float.parseFloat(sb.getText().toString());
                    MyImage.setCurrentSize(Float.parseFloat(sb.getText().toString()));
                    MyImage.setCurrentColor(Color.WHITE);
                    MyImage.setupDrawing();
                    MyImage.invalidate();
                    CeS.dismiss();
                    CeS = null;
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Input width of eraser!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    public void Share(View view) {
        ImageDrawing MyImage = (ImageDrawing) findViewById(R.id.drawing);
        ShareFB = new Dialog(this);
        ShareFB.setTitle("Share on Social Networks!");
        ShareFB.setContentView(fb_activity);
        ShareFB.show();
        displayInterstitial();
        Button bs = (Button) ShareFB.findViewById(R.id.shareOk);
        ImageButton imb = (ImageButton) ShareFB.findViewById(R.id.fb_address);
        ImageButton imb2 =(ImageButton) ShareFB.findViewById(R.id.vk_address);
        imb.setBackgroundResource(R.drawable.fb_address);
        imb2.setBackgroundResource(R.drawable.vk_address);
        bs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareFB.dismiss();
            }
        });

        imb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/ImaJinno"));
                startActivity(browserIntent);
            }
        });
        imb2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/imajinno"));
                startActivity(browserIntent);
            }
        });
}
}


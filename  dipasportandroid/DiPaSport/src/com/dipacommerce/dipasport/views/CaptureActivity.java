package com.dipacommerce.dipasport.views;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.barcode.AmbientLightManager;
import com.dipacommerce.dipasport.barcode.BeepManager;
import com.dipacommerce.dipasport.barcode.CameraManager;
import com.dipacommerce.dipasport.barcode.CaptureActivityHandler;
import com.dipacommerce.dipasport.barcode.ClipboardInterface;
import com.dipacommerce.dipasport.barcode.DecodeFormatManager;
import com.dipacommerce.dipasport.barcode.DecodeHintManager;
import com.dipacommerce.dipasport.barcode.FinishListener;
import com.dipacommerce.dipasport.barcode.InactivityTimer;
import com.dipacommerce.dipasport.barcode.IntentSource;
import com.dipacommerce.dipasport.barcode.Intents;
import com.dipacommerce.dipasport.barcode.ResultHandler;
import com.dipacommerce.dipasport.barcode.ResultHandlerFactory;
import com.dipacommerce.dipasport.barcode.ViewfinderView;
import com.dipacommerce.dipasport.customer.CustomerManager;
import com.dipacommerce.dipasport.customer.Customers;
import com.dipacommerce.dipasport.data.Constants;
import com.dipacommerce.dipasport.data.URLFormatter;
import com.dipacommerce.dipasport.network.json.IJSONManager;
import com.dipacommerce.dipasport.network.json.IJSONManager.Callback;
import com.dipacommerce.dipasport.network.json.JSONManager;
import com.dipacommerce.dipasport.products.IProduct;
import com.dipacommerce.dipasport.products.ProductInfo;
import com.dipacommerce.dipasport.products.ProductManager;
import com.dipacommerce.dipasport.products.Products;
import com.dipacommerce.dipasport.shoppingcart.ICart;
import com.dipacommerce.dipasport.shoppingcart.OrderProduct;
import com.dipacommerce.dipasport.shoppingcart.ShopImp.OptionsChanged;
import com.dipacommerce.dipasport.shoppingcart.ShoppingCart;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;

/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 * 
 */
public class CaptureActivity extends Activity implements SurfaceHolder.Callback, Callback<JSONObject>, OptionsChanged {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private static final long DEFAULT_INTENT_RESULT_DURATION_MS = 1500L;
    private static final long BULK_MODE_SCAN_DELAY_MS = 1000L;

    public static final int HISTORY_REQUEST_CODE = 0x0000bacc;

    private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES = EnumSet.of(ResultMetadataType.ISSUE_NUMBER, ResultMetadataType.SUGGESTED_PRICE, ResultMetadataType.ERROR_CORRECTION_LEVEL, ResultMetadataType.POSSIBLE_COUNTRY);

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private Result savedResultToShow;
    private ViewfinderView viewfinderView;
    private TextView statusView;
    private View resultView;
    private Result lastResult;
    private boolean hasSurface;
    private boolean copyToClipboard;
    private IntentSource source;
    private String sourceUrl;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private AmbientLightManager ambientLightManager;
    private static String mContent;
    private ProgressDialog mLoading;
    public static ProductInfo mProductInfo;
    private NumberPicker mQuantity;
    private Context mContext;
    private ICart mShoppingCart;
    ResultHandler resultHandler;
    private Bundle mHistory;

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        String languageToLoad = "it"; // your language
        if (DiPaSport.getLanguage() == DiPaSport.LANGUAGE_UK) {
            languageToLoad = "uk"; // your language
        }
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.capture);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        ambientLightManager = new AmbientLightManager(this);

        mLoading = new ProgressDialog(this);
        mLoading.setMessage(getResources().getString(R.string.msg_search_product_info));

        mContext = this;
        mShoppingCart = ShoppingCart.getInstance();

        mShoppingCart.registerOptionsChanged(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.barcode_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.barcode_menu_home) {
            finish();
        } else if (id == R.id.barcode_menu_history) {
            if (!DiPaSport.getCustomerManager().isLogin()) {
                Toast.makeText(CaptureActivity.this, getString(R.string.str_order_confirm_to_pay), Toast.LENGTH_SHORT).show();
            } else {
                Intent barcodeHistory = new Intent(this, HistoryActivity.class);
                startActivity(barcodeHistory);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // CameraManager must be initialized here, not in onCreate(). This is
        // necessary because we don't
        // want to open the camera driver and measure the screen size if we're
        // going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the
        // wrong size and partially
        // off screen.
        cameraManager = new CameraManager(getApplication());

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);

        resultView = findViewById(R.id.result_view);
        statusView = (TextView) findViewById(R.id.status_view);

        handler = null;
        lastResult = null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        resetStatusView();

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            surfaceHolder.addCallback(this);
        }

        ambientLightManager.start(cameraManager);

        inactivityTimer.onResume();

        Intent intent = getIntent();

        source = IntentSource.NONE;
        decodeFormats = null;
        characterSet = null;

        if (intent != null) {

            String action = intent.getAction();
            String dataString = intent.getDataString();

            if (Intents.Scan.ACTION.equals(action)) {

                // Scan the formats the intent requested, and return the result
                // to the calling activity.
                source = IntentSource.NATIVE_APP_INTENT;
                decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
                decodeHints = DecodeHintManager.parseDecodeHints(intent);

                if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
                    int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
                    int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
                    if (width > 0 && height > 0) {
                        cameraManager.setManualFramingRect(width, height);
                    }
                }

                String customPromptMessage = intent.getStringExtra(Intents.Scan.PROMPT_MESSAGE);
                if (customPromptMessage != null) {
                    statusView.setText(customPromptMessage);
                }

            } else if (dataString != null && dataString.contains("http://www.google") && dataString.contains("/m/products/scan")) {

                // Scan only products and send the result to mobile Product
                // Search.
                source = IntentSource.PRODUCT_SEARCH_LINK;
                sourceUrl = dataString;
                decodeFormats = DecodeFormatManager.PRODUCT_FORMATS;

            }

            characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);

        }
    }

    private int getCurrentOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
        case Surface.ROTATION_0:
        case Surface.ROTATION_90:
            return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        default:
            return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        }
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        ambientLightManager.stop();
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_BACK:
            if (source == IntentSource.NATIVE_APP_INTENT) {
                setResult(RESULT_CANCELED);
                finish();
                return true;
            }
            if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK) && lastResult != null) {
                restartPreviewAfterDelay(0L);
                return true;
            }
            break;
        case KeyEvent.KEYCODE_FOCUS:
        case KeyEvent.KEYCODE_CAMERA:
            // Handle these events so they don't launch the Camera app
            return true;
            // Use volume up/down to turn on light
        case KeyEvent.KEYCODE_VOLUME_DOWN:
            cameraManager.setTorch(false);
            return true;
        case KeyEvent.KEYCODE_VOLUME_UP:
            cameraManager.setTorch(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
        // Bitmap isn't used yet -- will be used soon
        if (handler == null) {
            savedResultToShow = result;
        } else {
            if (result != null) {
                savedResultToShow = result;
            }
            if (savedResultToShow != null) {
                Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
                handler.sendMessage(message);
            }
            savedResultToShow = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     * 
     * @param rawResult
     *            The contents of the barcode.
     * @param scaleFactor
     *            amount by which thumbnail was scaled
     * @param barcode
     *            A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        inactivityTimer.onActivity();
        lastResult = rawResult;
        resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);

        boolean fromLiveScan = barcode != null;
        if (fromLiveScan) {
            // Then not from history, so beep/vibrate and we have an image to
            // draw on
            beepManager.playBeepSoundAndVibrate();
            drawResultPoints(barcode, scaleFactor, rawResult);
        }

        switch (source) {
        case NATIVE_APP_INTENT:
        case PRODUCT_SEARCH_LINK:
            handleDecodeExternally(rawResult, resultHandler, barcode);
            break;
        case NONE:
            handleDecodeInternally(rawResult, resultHandler, barcode);

            try {
                // Add to history
                mHistory = new Bundle();
                mHistory.putString(IProduct.HISTORY.CODE, resultHandler.getDisplayContents().toString());
                mHistory.putString(IProduct.HISTORY.NAME, "Product not available");
                mHistory.putLong(IProduct.HISTORY.TIME, System.currentTimeMillis());

                DiPaSport.getDatabaseHandler().addHistory(mHistory, DiPaSport.getCustomerManager().getCustomerInfo().getEmail());
            } catch (Exception e) {
                //Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            // TODO: request to server to get product info
            mLoading.show();
            String productName = resultHandler.getDisplayContents().toString();
            getProductInfo(productName);
            break;
        }
    }

    /**
     * Superimpose a line for 1D or dots for 2D to highlight the key features of
     * the barcode.
     * 
     * @param barcode
     *            A bitmap of the captured image.
     * @param scaleFactor
     *            amount by which thumbnail was scaled
     * @param rawResult
     *            The decoded results which contains the points to draw.
     */
    private void drawResultPoints(Bitmap barcode, float scaleFactor, Result rawResult) {
        ResultPoint[] points = rawResult.getResultPoints();
        if (points != null && points.length > 0) {
            Canvas canvas = new Canvas(barcode);
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.result_points));
            if (points.length == 2) {
                paint.setStrokeWidth(4.0f);
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
            } else if (points.length == 4 && (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A || rawResult.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
                // Hacky special case -- draw two lines, for the barcode and
                // metadata
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
                drawLine(canvas, paint, points[2], points[3], scaleFactor);
            } else {
                paint.setStrokeWidth(10.0f);
                for (ResultPoint point : points) {
                    if (point != null) {
                        canvas.drawPoint(scaleFactor * point.getX(), scaleFactor * point.getY(), paint);
                    }
                }
            }
        }
    }

    private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b, float scaleFactor) {
        if (a != null && b != null) {
            canvas.drawLine(scaleFactor * a.getX(), scaleFactor * a.getY(), scaleFactor * b.getX(), scaleFactor * b.getY(), paint);
        }
    }

    // Put up our own UI for how to handle the decoded contents.
    private void handleDecodeInternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {

        CharSequence displayContents = resultHandler.getDisplayContents();
        mContent = displayContents.toString();

        if (copyToClipboard && !resultHandler.areContentsSecure()) {
            ClipboardInterface.setText(displayContents, this);
        }

        statusView.setVisibility(View.GONE);
        viewfinderView.setVisibility(View.GONE);
        resultView.setVisibility(View.VISIBLE);

        ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
        if (barcode == null) {
            barcodeImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        } else {
            barcodeImageView.setImageBitmap(barcode);
        }

        TextView formatTextView = (TextView) findViewById(R.id.format_text_view);
        String format = String.format("<b>%s</b> %s", getString(R.string.msg_default_format), rawResult.getBarcodeFormat().toString());
        formatTextView.setText(Html.fromHtml(format));

        TextView typeTextView = (TextView) findViewById(R.id.type_text_view);
        format = String.format("<b>%s</b> %s", getString(R.string.msg_default_type), resultHandler.getType().toString());
        typeTextView.setText(Html.fromHtml(format));

        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        TextView timeTextView = (TextView) findViewById(R.id.time_text_view);
        String datetime = formatter.format(new Date(rawResult.getTimestamp()));
        format = String.format("<b>%s</b> %s", getString(R.string.msg_default_type), datetime);
        timeTextView.setText(Html.fromHtml(format));

        TextView metaTextView = (TextView) findViewById(R.id.meta_text_view);
        metaTextView.setVisibility(View.GONE);

        Map<ResultMetadataType, Object> metadata = rawResult.getResultMetadata();
        if (metadata != null) {
            StringBuilder metadataText = new StringBuilder(20);
            for (Map.Entry<ResultMetadataType, Object> entry : metadata.entrySet()) {
                if (DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
                    metadataText.append(entry.getValue()).append('\n');
                }
            }
            if (metadataText.length() > 0) {
                metadataText.setLength(metadataText.length() - 1);
                format = String.format("<b>%s</b> %s", getString(R.string.msg_default_meta), metadataText);
                metaTextView.setText(Html.fromHtml(format));
                metaTextView.setVisibility(View.VISIBLE);
            }
        }

        TextView contentsTextView = (TextView) findViewById(R.id.contents_text_view);
        contentsTextView.setText(displayContents);
        int scaledSize = Math.max(22, 32 - displayContents.length() / 4);
        contentsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);

        Button tryagain = (Button) findViewById(R.id.capture_tryagain);
        if (tryagain != null) {
            tryagain.setOnClickListener(OnTryAgainClick);
        }

        Button ok = (Button) findViewById(R.id.capture_ok);
        if (ok != null) {
            ok.setOnClickListener(OnOkClick);
        }

        mQuantity = (NumberPicker) findViewById(R.id.barcode_number);
        mQuantity.setMinValue(IProduct.QUANTITY_MIN); // min value 0
        mQuantity.setMaxValue(IProduct.QUANTITY_MAX); // max value 50
        mQuantity.setWrapSelectorWheel(false);
        mQuantity.setValue(1);

        Button addtocart = (Button) findViewById(R.id.barcode_addtocart);
        addtocart.setOnClickListener(OnAddProductToCart);
    }

    // Briefly show the contents of the barcode, then handle the result outside
    // Barcode Scanner.
    private void handleDecodeExternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {

        if (barcode != null) {
            viewfinderView.drawResultBitmap(barcode);
        }

        long resultDurationMS;
        if (getIntent() == null) {
            resultDurationMS = DEFAULT_INTENT_RESULT_DURATION_MS;
        } else {
            resultDurationMS = getIntent().getLongExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS, DEFAULT_INTENT_RESULT_DURATION_MS);
        }

        if (resultDurationMS > 0) {
            String rawResultString = String.valueOf(rawResult);
            if (rawResultString.length() > 32) {
                rawResultString = rawResultString.substring(0, 32) + " ...";
            }
            statusView.setText(getString(resultHandler.getDisplayTitle()) + " : " + rawResultString);
        }

        if (copyToClipboard && !resultHandler.areContentsSecure()) {
            CharSequence text = resultHandler.getDisplayContents();
            ClipboardInterface.setText(text, this);
        }

        if (source == IntentSource.NATIVE_APP_INTENT) {

            // Hand back whatever action they requested - this can be changed to
            // Intents.Scan.ACTION when
            // the deprecated intent is retired.
            Intent intent = new Intent(getIntent().getAction());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
            intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
            byte[] rawBytes = rawResult.getRawBytes();
            if (rawBytes != null && rawBytes.length > 0) {
                intent.putExtra(Intents.Scan.RESULT_BYTES, rawBytes);
            }
            Map<ResultMetadataType, ?> metadata = rawResult.getResultMetadata();
            if (metadata != null) {
                if (metadata.containsKey(ResultMetadataType.UPC_EAN_EXTENSION)) {
                    intent.putExtra(Intents.Scan.RESULT_UPC_EAN_EXTENSION, metadata.get(ResultMetadataType.UPC_EAN_EXTENSION).toString());
                }
                Number orientation = (Number) metadata.get(ResultMetadataType.ORIENTATION);
                if (orientation != null) {
                    intent.putExtra(Intents.Scan.RESULT_ORIENTATION, orientation.intValue());
                }
                String ecLevel = (String) metadata.get(ResultMetadataType.ERROR_CORRECTION_LEVEL);
                if (ecLevel != null) {
                    intent.putExtra(Intents.Scan.RESULT_ERROR_CORRECTION_LEVEL, ecLevel);
                }
                @SuppressWarnings("unchecked")
                Iterable<byte[]> byteSegments = (Iterable<byte[]>) metadata.get(ResultMetadataType.BYTE_SEGMENTS);
                if (byteSegments != null) {
                    int i = 0;
                    for (byte[] byteSegment : byteSegments) {
                        intent.putExtra(Intents.Scan.RESULT_BYTE_SEGMENTS_PREFIX + i, byteSegment);
                        i++;
                    }
                }
            }
            sendReplyMessage(R.id.return_scan_result, intent, resultDurationMS);

        } else if (source == IntentSource.PRODUCT_SEARCH_LINK) {

            // Reformulate the URL which triggered us into a query, so that the
            // request goes to the same
            // TLD as the scan URL.
            int end = sourceUrl.lastIndexOf("/scan");
            String replyURL = sourceUrl.substring(0, end) + "?q=" + resultHandler.getDisplayContents() + "&source=zxing";
            sendReplyMessage(R.id.launch_product_query, replyURL, resultDurationMS);

        }
    }

    private void sendReplyMessage(int id, Object arg, long delayMS) {
        if (handler != null) {
            Message message = Message.obtain(handler, id, arg);
            if (delayMS > 0L) {
                handler.sendMessageDelayed(message, delayMS);
            } else {
                handler.sendMessage(message);
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
            }
            decodeOrStoreSavedBitmap(null, null);
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
        resetStatusView();
    }

    private void resetStatusView() {
        resultView.setVisibility(View.GONE);
        statusView.setText(R.string.msg_default_status);
        statusView.setVisibility(View.VISIBLE);
        viewfinderView.setVisibility(View.VISIBLE);
        lastResult = null;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    private OnClickListener OnTryAgainClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private OnClickListener OnOkClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent data = new Intent();
            boolean status = mProductInfo != null ? true: false;
            data.putExtra("status", status);
            setResult(RESULT_OK, data);
            finish();
        }
    };

    private void getProductInfo(String productName) {
        IJSONManager<JSONObject> json = JSONManager.getNewInstance(this);
        json.registerCallback(this);

        CustomerManager customer = Customers.getInstance();
        String userId = customer.getCustomerInfo().getUserId();

        String url = URLFormatter.buildUrlProductDetail(productName, userId);
        url += "&method=advance";

        if (Constants.DEBUG_MODE) {
            Log.i("URLFormatter", url);
        }

        json.setURL(url);
        json.execute();
    }

    @Override
    public void onResults(JSONObject results) {
        ProductManager product = Products.getInstance();
        if (results == null) {
            Toast.makeText(mContext, getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
            return;
        }

        mProductInfo = product.convertJSONProductDetail(results);
        if (mProductInfo != null) {
            Toast.makeText(this, getResources().getString(R.string.msg_product_found), Toast.LENGTH_SHORT).show();
            if (DiPaSport.getCustomerManager().isLogin()) {
                mHistory.putString(IProduct.HISTORY.NAME, mProductInfo.getName());
                DiPaSport.getDatabaseHandler().updateHistory(mHistory, DiPaSport.getCustomerManager().getCustomerInfo().getEmail());
            }
        } else {
            Toast.makeText(this, "This product could not find, please search by manually to view more than similar products", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onErrors(int _errorCode, String _errorMessage) {
        Toast.makeText(this, "This product could not find, please search by manually to view more than similar products", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDone() {
        mLoading.dismiss();
    }

    public static ProductInfo getProductInfor() {
        return mProductInfo;
    }

    private OnClickListener OnAddProductToCart = new OnClickListener() {

        @Override
        public void onClick(View v) {
            CustomerManager customer = DiPaSport.getCustomerManager();
            if (!customer.isLogin()) {
                Toast.makeText(CaptureActivity.this, getString(R.string.str_order_confirm_to_pay), Toast.LENGTH_SHORT).show();
                return;
            }
            
            if(!customer.getCustomerInfo().priceIsAvailable()){
                Toast.makeText(CaptureActivity.this, getString(R.string.str_detail_price_unavailable), Toast.LENGTH_SHORT).show();
                return;
            }

            if (mProductInfo != null) {
                if (mShoppingCart.isExist(mProductInfo.getCode())) {
                    Toast.makeText(mContext, getString(R.string.str_product_existing_incart), Toast.LENGTH_LONG).show();
                } else {
                    int quantity = mQuantity.getValue();
                    OrderProduct order = new OrderProduct();
                    order.setProductInfo(mProductInfo);
                    order.setQuantity(quantity);

                    DiPaSport.getDatabaseHandler().addProduct(order, DiPaSport.getCustomerManager().getCustomerInfo().getUserId());
                    Toast.makeText(mContext, getString(R.string.str_addtocart_msg), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "This product could not find, please search by manually to view more than similar products", Toast.LENGTH_SHORT).show();
            }

            mShoppingCart.display2Options(mContext);
        }
    };

    @Override
    public void onContinueShopping() {
        if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK) && lastResult != null) {
            restartPreviewAfterDelay(0L);
        }
    }

    @Override
    public void onGoToShoppingCart() {
        setResult(TabsFragment.TAB_SHOPPINGCART_INDEX);
        finish();
    }
}

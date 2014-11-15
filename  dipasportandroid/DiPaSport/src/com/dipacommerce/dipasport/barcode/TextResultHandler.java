package com.dipacommerce.dipasport.barcode;

import android.app.Activity;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;

/**
 * This class handles TextParsedResult as well as unknown formats. It's the
 * fallback handler.
 * 
 */
public final class TextResultHandler extends ResultHandler {

    public TextResultHandler(Activity activity, ParsedResult result, Result rawResult) {
        super(activity, result, rawResult);
    }

    @Override
    public int getButtonCount() {
        return 0;
    }

    @Override
    public int getButtonText(int index) {
        return 0;
    }

    @Override
    public void handleButtonPress(int index) {

    }

    @Override
    public int getDisplayTitle() {
        return 0;
    }

}

package com.dipacommerce.dipasport.barcode;

import com.dipacommerce.dipasport.views.CaptureActivity;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;

/**
 * Manufactures Android-specific handlers based on the barcode content's type.
 * 
 */
public final class ResultHandlerFactory {
    private ResultHandlerFactory() {
    }

    public static ResultHandler makeResultHandler(CaptureActivity activity, Result rawResult) {
        ParsedResult result = parseResult(rawResult);
        return new TextResultHandler(activity, result, rawResult);
    }

    private static ParsedResult parseResult(Result rawResult) {
        return ResultParser.parseResult(rawResult);
    }
}

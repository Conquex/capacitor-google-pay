package com.conquex.capacitor.plugins.googlepay;

import com.conquex.capacitor.plugins.googlepay.util.PaymentsUtil;
import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;

import java.util.Optional;
import java.util.concurrent.Executor;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;

@NativePlugin(requestCodes={GooglePayPlugin.LOAD_PAYMENT_DATA_REQUEST_CODE})
public class GooglePayPlugin extends Plugin {
    public void load() {
        paymentsClient = PaymentsUtil.createPaymentsClient(getActivity());
    }

    public static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;

    private PaymentsClient paymentsClient;

    @PluginMethod
    public void available(PluginCall call) {
        this.isReadyToPay(call, false);
    }

    @PluginMethod
    public void paymentConfigured(PluginCall call) {
        this.isReadyToPay(call, true);
    }

    @PluginMethod
    public void requestPayment(PluginCall call) {
        this.paymentRequest(call, call.getString("price"), false);
    }

    @PluginMethod
    public void configurePayment(PluginCall call) {
        this.paymentRequest(call, "", true);
    }

    private void paymentRequest(final PluginCall call, String price, boolean forSetup) {
        Optional<JSONObject> paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(price, call.getString("merchantName"), forSetup);
        if (!paymentDataRequestJson.isPresent()) {
            call.reject("Unavailable");
            return;
        }

        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

        if (request != null) {
            saveCall(call);
            AutoResolveHelper.resolveTask(
                    paymentsClient.loadPaymentData(request),
                    getActivity(), LOAD_PAYMENT_DATA_REQUEST_CODE);
        } else {
            call.resolve();
        }
    }

    private void isReadyToPay(final PluginCall call, boolean existingPaymentMethodRequired) {
        final Optional<JSONObject> isReadyToPayJson = PaymentsUtil.getIsReadyToPayRequest(existingPaymentMethodRequired);
        final JSObject res = new JSObject();
        if (!isReadyToPayJson.isPresent()) {
            res.put("available", false);
            call.resolve(res);
            return;
        }

        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(getActivity(),
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            res.put("available", true);
                            call.resolve(res);
                        } else {
                            res.put("error", task.getException().getMessage());
                            res.put("available", false);
                            call.resolve(res);
                        }
                    }
                });
    }

    @Override
    public void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        PluginCall savedCall = getSavedCall();
        if (savedCall == null) {
            return;
        }
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {

                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handlePaymentSuccess(paymentData, savedCall);
                        return;

                    case Activity.RESULT_CANCELED:
                        savedCall.resolve();
                        return;

                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        savedCall.reject(status.getStatusMessage());
                        return;
                }
        }
        savedCall.resolve();
    }

    private void handlePaymentSuccess(PaymentData paymentData, PluginCall call) {
        final String paymentInfo = paymentData.toJson();
        if (paymentInfo == null) {
            call.resolve();
            return;
        }

        try {
            JSObject res = new JSObject(paymentInfo);
            call.resolve(res);
        } catch (JSONException e) {
            call.reject(e.getMessage());
        }
    }
}

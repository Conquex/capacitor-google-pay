package com.conquex.capacitor.plugins.googlepay.util;

import android.app.Activity;

import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PaymentsUtil {

  public static final BigDecimal CENTS_IN_A_UNIT = new BigDecimal(100d);

  public static PaymentsClient createPaymentsClient(Activity activity) {
    Wallet.WalletOptions walletOptions =
        new Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build();
    return Wallet.getPaymentsClient(activity, walletOptions);
  }

  public static Optional<JSONObject> getIsReadyToPayRequest() {
    try {
      JSONObject isReadyToPayRequest = getBaseRequest();
      isReadyToPayRequest.put(
              "allowedPaymentMethods", new JSONArray().put(getBaseCardPaymentMethod()));

      return Optional.of(isReadyToPayRequest);

    } catch (JSONException e) {
      return Optional.empty();
    }
  }

  private static JSONObject getBaseRequest() throws JSONException {
    return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
  }

  private static JSONObject getGatewayTokenizationSpecification() throws JSONException {
    return new JSONObject() {{
      put("type", WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY);
      put("parameters", new JSONObject() {{
        put("gateway", "checkoutltd");
        put("gatewayMerchantId", "pk_test_38e59da4-9a33-4072-8b8d-da0f9050404f");
      }});
    }};
  }

  private static JSONObject getCardPaymentMethod() throws JSONException {
    JSONObject cardPaymentMethod = getBaseCardPaymentMethod();
    cardPaymentMethod.put("tokenizationSpecification", getGatewayTokenizationSpecification());

    return cardPaymentMethod;
  }

  private static JSONArray getAllowedCardNetworks() {
    return new JSONArray(
            Arrays.asList(
                    WalletConstants.CARD_NETWORK_VISA,
                    WalletConstants.CARD_NETWORK_MASTERCARD));
  }

  private static JSONArray getAllowedCardAuthMethods() {
    return new JSONArray()
            .put("PAN_ONLY")
            .put("CRYPTOGRAM_3DS");
  }

  private static JSONArray getAllowedCountryCodes() {
    return new JSONArray(Arrays.asList("GB"));
  }

  private static JSONObject getTransactionInfo(String price) throws JSONException {
    JSONObject transactionInfo = new JSONObject();
    transactionInfo.put("totalPrice", price);
    transactionInfo.put("totalPriceStatus", "FINAL");
    transactionInfo.put("countryCode", "GB");
    transactionInfo.put("currencyCode", "GBP");
    transactionInfo.put("checkoutOption", "COMPLETE_IMMEDIATE_PURCHASE");

    return transactionInfo;
  }

  private static JSONObject getMerchantInfo() throws JSONException {
    // TODO set merchant Name
    return new JSONObject().put("merchantName", "Example Merchant");
  }

  public static Optional<JSONObject> getPaymentDataRequest(long priceCents) {

    final String price = PaymentsUtil.centsToString(priceCents);

    try {
      JSONObject paymentDataRequest = PaymentsUtil.getBaseRequest();
      paymentDataRequest.put(
              "allowedPaymentMethods", new JSONArray().put(PaymentsUtil.getCardPaymentMethod()));
      paymentDataRequest.put("transactionInfo", PaymentsUtil.getTransactionInfo(price));
      paymentDataRequest.put("merchantInfo", PaymentsUtil.getMerchantInfo());

      /* An optional shipping address requirement is a top-level property of the PaymentDataRequest
      JSON object. */
      paymentDataRequest.put("shippingAddressRequired", true);

      JSONObject shippingAddressParameters = new JSONObject();
      shippingAddressParameters.put("phoneNumberRequired", false);

      JSONArray allowedCountryCodes = new JSONArray(PaymentsUtil.getAllowedCountryCodes());

      shippingAddressParameters.put("allowedCountryCodes", allowedCountryCodes);
      paymentDataRequest.put("shippingAddressParameters", shippingAddressParameters);
      return Optional.of(paymentDataRequest);

    } catch (JSONException e) {
      return Optional.empty();
    }
  }

  private static JSONObject getBaseCardPaymentMethod() throws JSONException {
    JSONObject cardPaymentMethod = new JSONObject();
    cardPaymentMethod.put("type", "CARD");

    JSONObject parameters = new JSONObject();
    parameters.put("allowedAuthMethods", getAllowedCardAuthMethods());
    parameters.put("allowedCardNetworks", getAllowedCardNetworks());
    // Optionally, you can add billing address/phone number associated with a CARD payment method.
    parameters.put("billingAddressRequired", true);

    JSONObject billingAddressParameters = new JSONObject();
    billingAddressParameters.put("format", "FULL");

    parameters.put("billingAddressParameters", billingAddressParameters);

    cardPaymentMethod.put("parameters", parameters);

    return cardPaymentMethod;
  }

  private static String centsToString(long cents) {
    return new BigDecimal(cents)
            .divide(CENTS_IN_A_UNIT, RoundingMode.HALF_EVEN)
            .setScale(2, RoundingMode.HALF_EVEN)
            .toString();
  }
}

      
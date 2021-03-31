import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(GooglePayPlugin)
public class GooglePayPlugin: CAPPlugin {

    @objc func available(_ call: CAPPluginCall) {
        call.success("no ios implementation for GooglePayPlugin")
    }
    @objc func paymentConfigured(_ call: CAPPluginCall) {
        call.success("no ios implementation for GooglePayPlugin")
    }
    @objc func requestPayment(_ call: CAPPluginCall) {
        call.success("no ios implementation for GooglePayPlugin")
    }
    @objc func configurePayment(_ call: CAPPluginCall) {
        call.success("no ios implementation for GooglePayPlugin")
    }
}
import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(GooglePayPlugin)
public class GooglePayPlugin: CAPPlugin {

    @objc func available(_ call: CAPPluginCall) {
        call.success(["available": false])
    }
    @objc func paymentConfigured(_ call: CAPPluginCall) {
        call.success(["available": false])
    }
    @objc func requestPayment(_ call: CAPPluginCall) {
        call.error("GooglePayPlugin not available for ios")
    }
    @objc func configurePayment(_ call: CAPPluginCall) {
        call.error("GooglePayPlugin not available for ios")
    }
}
#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(GooglePayPlugin, "GooglePayPlugin",
           CAP_PLUGIN_METHOD(available, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(paymentConfigured, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(requestPayment, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(configurePayment, CAPPluginReturnPromise);
)

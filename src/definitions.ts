declare module '@capacitor/core' {
  interface PluginRegistry {
    GooglePayPlugin: GooglePayPluginPlugin;
  }
}

export interface GooglePayPluginPlugin {
  available(): Promise<{ available: boolean }>;
  paymentConfigured(): Promise<{ available: boolean }>;
  requestPayment(): Promise<any>;
  configurePayment(): Promise<any>;
}

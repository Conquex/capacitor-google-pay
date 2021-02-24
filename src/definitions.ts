declare module '@capacitor/core' {
  interface PluginRegistry {
    GooglePayPlugin: GooglePayPluginPlugin;
  }
}

export interface GooglePayPluginPlugin {
  isPaymentAvailable(): Promise<{ available: boolean }>;
  hasPaymentsSetup(): Promise<{ available: boolean }>;
  requestPayment(): Promise<any>;
}

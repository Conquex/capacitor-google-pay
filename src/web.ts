import { WebPlugin } from '@capacitor/core';
import { GooglePayPluginPlugin } from './definitions';

export class GooglePayPluginWeb extends WebPlugin implements GooglePayPluginPlugin {
  constructor() {
    super({
      name: 'GooglePayPlugin',
      platforms: ['web'],
    });
  }

  async isPaymentAvailable(): Promise<{ available: boolean }> {
    return new Promise(() => {});
  }

  async requestPayment(): Promise<any> {
    return new Promise(() => {});
  }
  
  async hasPaymentsSetup(): Promise<any> {
    return new Promise(() => {});
  }
}

const GooglePayPlugin = new GooglePayPluginWeb();

export { GooglePayPlugin };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(GooglePayPlugin);

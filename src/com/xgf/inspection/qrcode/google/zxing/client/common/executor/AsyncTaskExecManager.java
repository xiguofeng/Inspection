/*
 * Copyright (C) 2012 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xgf.inspection.qrcode.google.zxing.client.common.executor;

import com.xgf.inspection.qrcode.google.zxing.client.common.PlatformSupportManager;

public final class AsyncTaskExecManager extends PlatformSupportManager<AsyncTaskExecInterface> {

  public AsyncTaskExecManager() {
    super(AsyncTaskExecInterface.class, new DefaultAsyncTaskExecInterface());
    addImplementationClass(11, "com.xgf.inspection.qrcode.google.zxing.client.common.executor.HoneycombAsyncTaskExecInterface");
  }

}
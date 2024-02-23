import 'package:harvest_api/api.dart';
import 'package:platform_detector/enums.dart';
import 'package:platform_detector/platform_detector.dart';

import '../../config/config.dart';

TrabajadoresApi trabajadoresApiPlataform([OAuth? oAuth]) {
  TrabajadoresApi workersApi;
  if (PlatformDetector.platform.name == PlatformName.android) {
    workersApi =
        TrabajadoresApi(ApiClient(basePath: Config.api, authentication: oAuth));
  } else {
    workersApi = TrabajadoresApi(ApiClient(authentication: oAuth));
  }

  return workersApi;
}

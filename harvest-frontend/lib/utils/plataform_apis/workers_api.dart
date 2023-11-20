import 'package:harvest_api/api.dart';
import 'package:platform_detector/enums.dart';
import 'package:platform_detector/platform_detector.dart';

TrabajadoresApi trabajadoresApiPlataform([OAuth? oAuth]) {
  TrabajadoresApi workersApi;
  if (PlatformDetector.platform.name == PlatformName.android) {
    workersApi = TrabajadoresApi(
        ApiClient(basePath: 'http://10.0.2.2:8080', authentication: oAuth));
  } else {
    workersApi = TrabajadoresApi(ApiClient(authentication: oAuth));
  }

  return workersApi;
}

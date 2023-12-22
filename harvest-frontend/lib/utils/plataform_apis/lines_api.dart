import 'package:harvest_api/api.dart';
import 'package:platform_detector/enums.dart';
import 'package:platform_detector/platform_detector.dart';

LineasApi lineasApiPlataform([OAuth? oAuth]) {
  LineasApi lineasApi;
  if (PlatformDetector.platform.name == PlatformName.android) {
    lineasApi = LineasApi(
        ApiClient(basePath: 'http://10.0.2.2:8080', authentication: oAuth));
  } else {
    lineasApi = LineasApi(ApiClient(authentication: oAuth));
  }

  return lineasApi;
}

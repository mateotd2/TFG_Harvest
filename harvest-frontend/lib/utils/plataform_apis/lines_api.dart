import 'package:harvest_api/api.dart';
import 'package:platform_detector/enums.dart';
import 'package:platform_detector/platform_detector.dart';

import '../../config/config.dart';

LineasApi lineasApiPlataform([OAuth? oAuth]) {
  LineasApi lineasApi;
  if (PlatformDetector.platform.name == PlatformName.android) {
    lineasApi = LineasApi(
        ApiClient(basePath: Config.api, authentication: oAuth));
  } else {
    lineasApi = LineasApi(ApiClient(authentication: oAuth));
  }

  return lineasApi;
}

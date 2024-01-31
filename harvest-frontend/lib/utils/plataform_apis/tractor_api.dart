import 'package:harvest_api/api.dart';
import 'package:platform_detector/enums.dart';
import 'package:platform_detector/platform_detector.dart';

import '../../config/config.dart';

TractoristaApi tractorApiPlataform([OAuth? oAuth]) {
  TractoristaApi tractoristaApi;
  if (PlatformDetector.platform.name == PlatformName.android) {
    tractoristaApi = TractoristaApi(
        ApiClient(basePath: Config.api, authentication: oAuth));
  } else {
    tractoristaApi = TractoristaApi(ApiClient(authentication: oAuth));
  }

  return tractoristaApi;
}

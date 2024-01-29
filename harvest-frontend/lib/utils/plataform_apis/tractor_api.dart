import 'package:harvest_api/api.dart';
import 'package:platform_detector/enums.dart';
import 'package:platform_detector/platform_detector.dart';

TractoristaApi tractorApiPlataform([OAuth? oAuth]) {
  TractoristaApi tractoristaApi;
  if (PlatformDetector.platform.name == PlatformName.android) {
    tractoristaApi = TractoristaApi(
        ApiClient(basePath: 'http://10.0.2.2:8080', authentication: oAuth));
  } else {
    tractoristaApi = TractoristaApi(ApiClient(authentication: oAuth));
  }

  return tractoristaApi;
}

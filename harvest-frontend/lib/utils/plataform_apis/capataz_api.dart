import 'package:harvest_api/api.dart';
import 'package:platform_detector/enums.dart';
import 'package:platform_detector/platform_detector.dart';

CapatazApi capatazApiPlataform([OAuth? oAuth]) {
  CapatazApi capatazApi;
  if (PlatformDetector.platform.name == PlatformName.android) {
    capatazApi = CapatazApi(
        ApiClient(basePath: 'http://10.0.2.2:8080', authentication: oAuth));
  } else {
    capatazApi = CapatazApi(ApiClient(authentication: oAuth));
  }

  return capatazApi;
}

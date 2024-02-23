import 'package:harvest_api/api.dart';
import 'package:platform_detector/enums.dart';
import 'package:platform_detector/platform_detector.dart';

import '../../config/config.dart';

CapatazApi capatazApiPlataform([OAuth? oAuth]) {
  CapatazApi capatazApi;
  if (PlatformDetector.platform.name == PlatformName.android) {
    capatazApi =
        CapatazApi(ApiClient(basePath: Config.api, authentication: oAuth));
  } else {
    capatazApi = CapatazApi(ApiClient(authentication: oAuth));
  }

  return capatazApi;
}

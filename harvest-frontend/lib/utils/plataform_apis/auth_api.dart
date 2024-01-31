import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/config/config.dart';
import 'package:platform_detector/enums.dart';
import 'package:platform_detector/platform_detector.dart';

AutenticadoApi autenticadoApiPlataform([OAuth? oAuth]) {
  AutenticadoApi autenticadoApi;
  if (PlatformDetector.platform.name == PlatformName.android) {
    autenticadoApi = AutenticadoApi(
        ApiClient(basePath: Config.api, authentication: oAuth));
  } else {
    autenticadoApi = AutenticadoApi(ApiClient(authentication: oAuth));
  }

  return autenticadoApi;
}

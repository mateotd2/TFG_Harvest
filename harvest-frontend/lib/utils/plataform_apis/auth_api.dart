import 'package:harvest_api/api.dart';
import 'package:platform_detector/enums.dart';
import 'package:platform_detector/platform_detector.dart';

AutenticadoApi autenticadoApiPlataform() {
  AutenticadoApi autenticadoApi;
  if (PlatformDetector.platform.name == PlatformName.android) {
    autenticadoApi =
        AutenticadoApi(ApiClient(basePath: 'http://10.0.2.2:8080'));
  } else {
    autenticadoApi = AutenticadoApi();
  }

  return autenticadoApi;
}

import 'package:harvest_api/api.dart';
import 'package:platform_detector/enums.dart';
import 'package:platform_detector/platform_detector.dart';

CampanhaApi campanhaApiPlataform([OAuth? oAuth]) {
  CampanhaApi campaignApi;
  if (PlatformDetector.platform.name == PlatformName.android) {
    campaignApi = CampanhaApi(
        ApiClient(basePath: 'http://10.0.2.2:8080', authentication: oAuth));
  } else {
    campaignApi = CampanhaApi(ApiClient(authentication: oAuth));
  }

  return campaignApi;
}

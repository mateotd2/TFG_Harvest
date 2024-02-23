import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/config/config.dart';
import 'package:platform_detector/enums.dart';
import 'package:platform_detector/platform_detector.dart';

CampanhaApi campanhaApiPlataform([OAuth? oAuth]) {
  CampanhaApi campaignApi;
  if (PlatformDetector.platform.name == PlatformName.android) {
    campaignApi =
        CampanhaApi(ApiClient(basePath: Config.api, authentication: oAuth));
  } else {
    campaignApi = CampanhaApi(ApiClient(authentication: oAuth));
  }

  return campaignApi;
}

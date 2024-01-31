// import 'package:permission_handler/permission_handler.dart';
// import 'package:platform_detector/enums.dart';
// import 'package:platform_detector/platform_detector.dart';
//
// class PermissionUtil {
//   static List<Permission> androidPermissions = <Permission>[
//     Permission.storage
//   ];
//
//   static List<Permission> iosPermissions = <Permission>[
//     Permission.storage
//   ];
//
//   static Future<Map<Permission, PermissionStatus>> requestAll() async {
//     if (PlatformDetector.platform.name == PlatformName.iOS) {
//       return await iosPermissions.request();
//     }
//     return await androidPermissions.request();
//   }
//
//   static Future<Map<Permission, PermissionStatus>> request(
//       Permission permission) async {
//     final List<Permission> permissions = <Permission>[permission];
//     return await permissions.request();
//   }
//
//   static bool isDenied(Map<Permission, PermissionStatus> result) {
//     var isDenied = false;
//     result.forEach((key, value) {
//       if (value == PermissionStatus.denied) {
//         isDenied = true;
//         return;
//       }
//     });
//     return isDenied;
//   }
//
// }
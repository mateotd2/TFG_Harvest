// import 'package:flutter/material.dart';
// import 'package:flutter_local_notifications/flutter_local_notifications.dart';
//
// class LocalNotificationService {
//   // Instance of Flutternotification plugin
//   static final FlutterLocalNotificationsPlugin _notificationsPlugin =
//   FlutterLocalNotificationsPlugin();
//
//   static void initialize() {
//     // Initialization setting for android
//     const InitializationSettings initializationSettingsAndroid =
//     InitializationSettings(
//         android: AndroidInitializationSettings('app_icon'));
//     _notificationsPlugin.initialize(
//       initializationSettingsAndroid,
//       // to handle event when we receive notification
//       onDidReceiveNotificationResponse: (details) {
//         if (details.input != null) {}
//       },
//     );
//   }
//
//   static Future<void> display() async {
//     // To display the notification in device
//     try {
//       // print(message.notification!.android!.sound);
//       // final id = DateTime
//       //     .now()
//       //     .millisecondsSinceEpoch ~/ 1000;
//
//       const AndroidNotificationDetails androidPlatformChannelSpecifics =
//       AndroidNotificationDetails(
//           'channel_id', 'channel_name',
//           importance: Importance.max,
//           priority: Priority.high,
//           ticker: 'ticker',
//           icon: "@mipmap/ic_launcher" );
//       NotificationDetails notificationDetails = NotificationDetails(
//         android: androidPlatformChannelSpecifics,
//       );
//       await _notificationsPlugin.show(1, "Titulo",
//           "body", notificationDetails);
//     } catch (e) {
//       debugPrint(e.toString());
//     }
//   }
//
// }
//

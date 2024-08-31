import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/utils/plataform_apis/tractor_api.dart';
import 'package:harvest_frontend/utils/provider/sign_in_model.dart';
import 'package:harvest_frontend/utils/snack_bars.dart';
import 'package:logger/logger.dart';
import 'package:media_store_plus/media_store_plus.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:platform_detector/enums.dart';
import 'package:platform_detector/platform_detector.dart';
import 'package:provider/provider.dart';

import 'UI/home.dart';
import 'UI/sign_in.dart';

final FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin =
    FlutterLocalNotificationsPlugin();

final mediaStorePlugin = MediaStore();

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  List<Permission> permissions = [
    Permission.storage,
  ];

  if ((await mediaStorePlugin.getPlatformSDKInt()) >= 33) {
    permissions.add(Permission.photos);
  }

  MediaStore.appFolder = "MediaStorePlugin";

  await permissions.request();

  runApp(ChangeNotifierProvider(
      create: (context) => SignInResponseModel(), // ESTADO
      child: const MyApp()));
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: MainView(),
    );
  }
}

class MainView extends StatefulWidget {
  const MainView({super.key});

  @override
  State<StatefulWidget> createState() => _MainViewState();
}

class _MainViewState extends State<MainView> {
  var logger = Logger();
  final flutterSecureStorage = const FlutterSecureStorage();
  var signin = true;

  Future<void> _cargarStorage() async {
    final valor = await flutterSecureStorage.read(key: 'jwt');
    if (!context.mounted) return;
    final estado = Provider.of<SignInResponseModel>(context, listen: false);

    logger.d("Se intenta carga el dto con el jwt en el estado global");
    if (valor != null) {
      SignInResponseDTO? response =
          SignInResponseDTO.fromJson(jsonDecode(valor));
      estado.addResponse(response!); // Añado al estado la el dto con el jwt
      logger.d("Se carga el dto con el jwt en el estado global");
      signin = false;
    }
  }

  @override
  void initState() {
    super.initState();
    // LocalNotificationService.initialize();
    _isAndroidPermissionGranted();
    _requestPermissions();
    // PermissionUtil.requestAll();

    _cargarStorage();
  }

  Future<void> _isAndroidPermissionGranted() async {
    if (PlatformDetector.platform.name == PlatformName.android) {
      // final bool granted =
      await flutterLocalNotificationsPlugin
              .resolvePlatformSpecificImplementation<
                  AndroidFlutterLocalNotificationsPlugin>()
              ?.areNotificationsEnabled() ??
          false;
    }
  }

  Future<void> _requestPermissions() async {
    // Permisos de notificaciones

    if (PlatformDetector.platform.name == PlatformName.macOs) {
      await flutterLocalNotificationsPlugin
          .resolvePlatformSpecificImplementation<
              IOSFlutterLocalNotificationsPlugin>()
          ?.requestPermissions(
            alert: true,
            badge: true,
            sound: true,
          );
      await flutterLocalNotificationsPlugin
          .resolvePlatformSpecificImplementation<
              MacOSFlutterLocalNotificationsPlugin>()
          ?.requestPermissions(
            alert: true,
            badge: true,
            sound: true,
          );
    } else if (PlatformDetector.platform.name == PlatformName.android) {
      final AndroidFlutterLocalNotificationsPlugin? androidImplementation =
          flutterLocalNotificationsPlugin.resolvePlatformSpecificImplementation<
              AndroidFlutterLocalNotificationsPlugin>();

      // final bool? grantedNotificationPermission =
      await androidImplementation?.requestNotificationsPermission();
    }
  }

  @override
  Widget build(BuildContext context) {
    final appState = Provider.of<SignInResponseModel>(
        context); // Acceso al estado con el SignInResponse

    appState.addListener(() {
      if (appState.lastResponse == null) {
        signin = true;
      } else {
        signin = false;
      }
    });

    if (signin) {
      return const SignIn();
    } else {
      setState(() {
        if (appState.lastResponse!.roles.contains("ROLE_TRACTORISTA")) {
          OAuth auth = OAuth(accessToken: appState.lastResponse!.accessToken);
          final api = tractorApiPlataform(auth);
          // LocalNotificationService.initialize();
          iniciarHiloPolling(api);
        }
      });
      return Home();
    }
  }

  void iniciarHiloPolling(TractoristaApi api) {
    Timer.periodic(Duration(seconds: 90), (timer) async {
      if (mounted) {
        try {
          bool? masTareas =
              await api.checkNewLoadTasks().timeout(Duration(seconds: 5));
          if (masTareas!) {
            logger.d("Tareas nuevas de CARGA");
            // Notificacion
            await _showNotification();
          } else {
            logger.d("Aun no hay nuevas tareas");
          }
        } on TimeoutException {
          snackTimeout(context);
        } catch (e) {
          snackRed(context, "Error comunicandose con el servidor");
          logger.d("Error comunicandose con el servidor");
        }
      }
    });
  }

  Future<void> _showNotification() async {
    const AndroidNotificationDetails androidNotificationDetails =
        AndroidNotificationDetails('tractor', 'Tractorista',
            channelDescription: 'Canal para notificaciones de tractorista',
            importance: Importance.max,
            priority: Priority.high,
            ticker: 'ticker',
            icon: '@mipmap/ic_launcher');
    const NotificationDetails notificationDetails =
        NotificationDetails(android: androidNotificationDetails);
    await flutterLocalNotificationsPlugin.show(
        1, 'Nueva tarea ', 'Nueva tarea para la carga', notificationDetails,
        payload: 'item x');
  }

// static List<Permission> androidPermissions = <Permission>[
//   Permission.storage
// ];
//
// static List<Permission> iosPermissions = <Permission>[
//   Permission.storage
// ];
//
// static Future<Map<Permission, PermissionStatus>> requestAll() async {
//   if (PlatformDetector.platform.name == PlatformName.android) {
//     return await iosPermissions.request();
//   }
//   return await androidPermissions.request();
// }
}

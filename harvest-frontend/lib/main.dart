import 'dart:async';
import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/utils/plataform_apis/tractor_api.dart';
import 'package:harvest_frontend/utils/provider/sign_in_model.dart';
import 'package:harvest_frontend/utils/snack_bars.dart';
import 'package:logger/logger.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:platform_detector/enums.dart';
import 'package:platform_detector/platform_detector.dart';
import 'package:provider/provider.dart';
import 'dart:html' as html; // Solo para Flutter Web

import 'UI/home.dart';
import 'UI/sign_in.dart';

final FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin =
FlutterLocalNotificationsPlugin();

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Manejo de permisos en plataformas nativas
  if (!kIsWeb) {
    List<Permission> permissions = [
      Permission.storage,
    ];
    await permissions.request();
  }

  runApp(ChangeNotifierProvider(
      create: (context) => SignInResponseModel(), // Estado global
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
    String? valor;
    if (kIsWeb) {
      // Usar SharedPreferences en la web
      final prefs = await SharedPreferences.getInstance();
      valor = prefs.getString('jwt');
    } else {
      // Usar flutter_secure_storage en plataformas nativas
      valor = await flutterSecureStorage.read(key: 'jwt');
    }

    if (!context.mounted) return;

    final estado = Provider.of<SignInResponseModel>(context, listen: false);

    if (valor != null) {
      SignInResponseDTO? response =
      SignInResponseDTO.fromJson(jsonDecode(valor));
      estado.addResponse(response!); // Actualizar el estado global
      signin = false;
    }
  }

  @override
  void initState() {
    super.initState();
    _isAndroidPermissionGranted();
    _requestPermissions();
    _cargarStorage();
  }

  Future<void> _isAndroidPermissionGranted() async {
    if (!kIsWeb && PlatformDetector.platform.name == PlatformName.android) {
      final bool granted =
          await flutterLocalNotificationsPlugin
              .resolvePlatformSpecificImplementation<
              AndroidFlutterLocalNotificationsPlugin>()
              ?.areNotificationsEnabled() ??
              false;
      if (!granted) {
        logger.d("Permisos de notificaciones no concedidos en Android.");
      }
    }
  }

  Future<void> _requestPermissions() async {
    if (!kIsWeb) {
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
        flutterLocalNotificationsPlugin
            .resolvePlatformSpecificImplementation<
            AndroidFlutterLocalNotificationsPlugin>();
        await androidImplementation?.requestNotificationsPermission();
      }
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
          iniciarHiloPolling(api);
        }
      });
      return Home();
    }
  }

  void iniciarHiloPolling(TractoristaApi api) {
    Timer.periodic(const Duration(seconds: 90), (timer) async {
      if (mounted) {
        try {
          bool? masTareas =
          await api.checkNewLoadTasks().timeout(const Duration(seconds: 5));
          if (masTareas!) {
            logger.d("Tareas nuevas de CARGA");
            await _showNotification();
          } else {
            logger.d("Aun no hay nuevas tareas");
          }
        } on TimeoutException {
          snackTimeout(context);
        } catch (e) {
          snackRed(context, "Error comunicándose con el servidor");
          logger.d("Error comunicándose con el servidor");
        }
      }
    });
  }

  Future<void> _showNotification() async {
    if (kIsWeb) {
      // Notificaciones en la web
      _showWebNotification();
    } else {
      // Notificaciones en plataformas nativas
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
          1, 'Nueva tarea', 'Nueva tarea para la carga', notificationDetails,
          payload: 'item x');
    }
  }

  void _showWebNotification() {

  }
}

import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/utils/provider/sign_in_model.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import 'UI/sign_in.dart';

void main() {
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
    if(!context.mounted) return;
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

  Future<void> _eliminarStorage() async {
    logger.d("Se intenta eliminar el dto con el jwt en el estado global");
    await flutterSecureStorage.delete(key: 'jwt');

    if(!context.mounted) return;

    final estado = Provider.of<SignInResponseModel>(context, listen: false);
    estado.clearResponse(); // Añado al estado la el dto con el jwt
    logger.d("Se elimina el dto con el jwt en el estado global");
  }

  @override
  void initState() {
    super.initState();

    _cargarStorage();
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
      return TextButton(
          onPressed: _eliminarStorage,
          child: Text(
              "HOLA, USUARIO AUTENTICADO:  ${appState.lastResponse?.username}, PULSAME PARA ELIMINAR EL JWT"));
    }
  }
}

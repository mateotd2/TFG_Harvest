import 'dart:async';

import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/utils/snack_bars.dart';
import 'package:logger/logger.dart';

import '../utils/plataform_apis/auth_api.dart';
import '../utils/provider/sign_in_model.dart';

Future<void> fetchUser(SignInResponseModel responseState, context,
    SignInRequestDTO signInRequest) async {
  // SI ES EJECUCION DE MOVIL UTILIZAR UNA INSTANCIA PERSONALIZADA
  final apiInstance = autenticadoApiPlataform();
  // final SecureStorageJWT _secureStorageJWT = SecureStorageJWT();
  var logger = Logger();

  void processResponse(SignInResponseDTO? value) async {
    logger.d(value);
    responseState.addResponse(value!);
  }

  try {
    final result =
        await apiInstance.signin(signInRequest).timeout(Duration(seconds: 10));
    processResponse(result);
  } on TimeoutException {
    snackTimeout(context);
    logger.d('Comunicacion con el servidor fallida');
  } catch (e) {
    snackGreen(context, 'Fallo en la autenticación');
    logger.d(
        "Error intento de signin de usuario ${signInRequest.username}\nException when calling AutenticadoApi->signin: $e");
  }
}

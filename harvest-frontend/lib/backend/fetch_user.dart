import 'package:harvest_api/api.dart';
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
    final result = await apiInstance.signin(signInRequest);
    processResponse(result);
  } catch (e) {
    logger.d(
        "Error intento de signin de usuario ${signInRequest.username}\nException when calling AutenticadoApi->signin: $e");
  }
}

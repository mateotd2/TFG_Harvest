import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:harvest_api/api.dart';

// Clase que notificara si el Jwt es invalido y sea necesario volver a autenticarse
class SignInResponseModel extends ChangeNotifier {
  SignInResponseDTO? _signInResponseDTO;

  // final SecureStorageJWT _secureStorageJWT = SecureStorageJWT();
  final storage = const FlutterSecureStorage();

  SignInResponseDTO? get lastResponse => _signInResponseDTO;

  Future<void> addResponse(SignInResponseDTO response) async {
    _signInResponseDTO = response;
    await storage.write(key: 'jwt', value: jsonEncode(response.toJson()));
    // _secureStorageJWT.setJwt(jsonEncode(response.toJson()));
    notifyListeners();
  }

  void clearResponse() {
    _signInResponseDTO = null;
    // _secureStorageJWT.deleteJwt();
    notifyListeners();
  }
}

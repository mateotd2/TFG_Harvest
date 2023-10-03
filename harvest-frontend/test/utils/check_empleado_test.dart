import 'package:flutter_test/flutter_test.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/utils/check_empelado.dart';

void main() {
  test('esAdmin False Test', () {
    SignInResponseDTO responseDTO = SignInResponseDTO(
        id: 1,
        username: "username",
        tokenType: "tokenType",
        accessToken: "accessToken",
        roles: ['ROLE_CAPATAZ', 'ROLE_TRACTORISTA']);
    expect(esAdmin(responseDTO), isFalse);
  });
  test('esAdmin True Test', () {
    SignInResponseDTO responseDTO = SignInResponseDTO(
        id: 1,
        username: "username",
        tokenType: "tokenType",
        accessToken: "accessToken",
        roles: ['ROLE_ADMIN']);
    expect(esAdmin(responseDTO), isTrue);
  });
}

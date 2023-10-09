import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:harvest_frontend/utils/provider/sign_in_model.dart';
import 'package:mockito/mockito.dart';

class MockStorage extends Mock implements FlutterSecureStorage {}

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  test('Estado con la respuesta nula', () {
    final responseModel = SignInResponseModel();
    expect(responseModel.lastResponse, isNull);
  });

  // test('Estado con la ultima respuesta',  () async{
  //
  //   final mock = MockStorage();
  //   final responseModel = SignInResponseModel();
  //   responseModel.storage = mock;
  //
  //   var response = SignInResponseDTO(
  //       id: 1,
  //       username: "username",
  //       tokenType: "tokenType",
  //       accessToken: "accessToken",
  //       roles: ["ROLE_ADMIN"]);
  //
  //   when(mock.write(key: 'jwt', value: jsonEncode(response.toJson()))).thenAnswer((_) async  {
  //     final completer = Completer<void>();
  //     return completer.complete();
  //   });
  //
  //   await responseModel.addResponse(response);
  //   expect(responseModel.lastResponse?.username, "username");
  // });
  // test('Estado con la respuesta borrada',  () async{
  //   final responseModel = SignInResponseModel();
  //   var respuestaNotificada = false;
  //   var response = SignInResponseDTO(
  //       id: 1,
  //       username: "username",
  //       tokenType: "tokenType",
  //       accessToken: "accessToken",
  //       roles: ["ROLE_ADMIN"]);
  //
  //   // when(mock.w).
  //   await responseModel.addResponse(response);
  //
  //
  //   expect(responseModel.lastResponse?.username, "username");
  //
  //   responseModel.clearResponse();
  //
  //   expect(responseModel.lastResponse, isNull);
  //
  // });
}

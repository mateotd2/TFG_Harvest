// import 'package:flutter_test/flutter_test.dart';
// import 'package:harvest_api/api.dart';
// import 'package:harvest_frontend/UI/sign_in.dart';
// import 'package:mockito/mockito.dart';
//
// class MockApi extends Mock implements AutenticadoApi{
// }
//
// void main() {
//   testWidgets('Prueba SignIn', (WidgetTester tester) async{
//
//     final mockAPI= MockApi();
//
//
//     SignInRequestDTO request = SignInRequestDTO(username: "username", password: "password");
//     SignInResponseDTO response = SignInResponseDTO(id: 1, username: "username", tokenType: "tokenType", accessToken: "accessToken");
//     when(await mockAPI.signin(request)).thenAnswer((_) => response);
//
//     await tester.pumpWidget(SignIn());
//
//     final user = find.text('Username');
//     final pass = find.text('Password');
//     final boton = find.text('Acceder');
//
//     await tester.enterText(user, 'Username');
//     await tester.enterText(user, 'Password');
//
//     expect(find.text('Username'), findsOneWidget);
//
//     // await tester.tap(boton);
//     //
//     // expect(find, matcher);
//
//
//   });
// }

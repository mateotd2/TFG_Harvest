// import 'package:flutter/material.dart';
// import 'package:flutter_test/flutter_test.dart';
// import 'package:harvest_api/api.dart';
// import 'package:harvest_frontend/backend/fetch_user.dart';
// import 'package:harvest_frontend/utils/provider/sign_in_model.dart';
// import 'package:mockito/mockito.dart';
// import 'package:provider/provider.dart';
//
//
// class MockApi extends Mock implements AutenticadoApi{}
//
//
//
// void main(){
//   testWidgets( 'Test para obtener la respuesta de un SignIn',(WidgetTester widgetTester) async{
//     final mockApi = MockApi();
//     SignInRequestDTO request = SignInRequestDTO(username: "username", password: "password");
//     SignInResponseDTO response = SignInResponseDTO(id: 1, username: "username", tokenType: "tokenType", accessToken: "accessToken");
//     when(mockApi.signin(request)).thenAnswer((_) async => response);
//
//     var resultado;
//
//
//
//       await widgetTester.pumpWidget(
//         MaterialApp(
//           home: Builder(
//             builder: (BuildContext context) {
//               final appState = Provider.of<SignInResponseModel>(context);
//                 resultado = fetchUser(appState, context, request);
//
//                 return Container();
//               },
//           ),
//         )
//       );
//     expect(await resultado, response);
//
//
//   });
// }

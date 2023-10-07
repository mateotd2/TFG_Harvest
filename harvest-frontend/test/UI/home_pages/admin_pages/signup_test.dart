import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/UI/home_pages/admin_pages/signup_emp.dart';
import 'package:harvest_frontend/utils/provider/sign_in_model.dart';
import 'package:mockito/mockito.dart';
import 'package:provider/provider.dart';

import '../../home_test.dart';

void main() {

  testWidgets('Formulario se construye correctamente', (WidgetTester tester) async {
    // Estado y API con token
    var response = SignInResponseDTO(
        id: 1,
        username: "username",
        tokenType: "tokenType",
        accessToken: "accessToken",
        roles: ["ROLE_ADMIN"]);
    // SignInResponseModel provider = SignInResponseModel();

    final mockEstado = MockEstado();

    when(mockEstado.lastResponse).thenAnswer((_) => response);

    await tester.pumpWidget(ChangeNotifierProvider<SignInResponseModel>.value(
        value: mockEstado,
        child: MaterialApp(
          home: SignupEmp(),
        )));

    await tester.pumpAndSettle();

    expect(find.text('Confirmar'), findsOneWidget);


  });

  // testWidgets('Relleno de formulario valido', (WidgetTester tester) async {
  //   var response = SignInResponseDTO(
  //       id: 1,
  //       username: "username",
  //       tokenType: "tokenType",
  //       accessToken: "accessToken",
  //       roles: ["ROLE_ADMIN"]);
  //   // SignInResponseModel provider = SignInResponseModel();
  //
  //   final mockEstado = MockEstado();
  //
  //   when(mockEstado.lastResponse).thenAnswer((_) => response);
  //
  //   await tester.pumpWidget(ChangeNotifierProvider<SignInResponseModel>.value(
  //       value: mockEstado,
  //       child: MaterialApp(
  //         home: SignupEmp(),
  //       )));
  //
  //   await tester.pumpAndSettle();
  //
  //   final emailInput = find.byKey(Key('emailKey'));
  //   final usernameInput = find.byKey(Key('usernameKey'));
  //   final nameInput = find.byKey(Key('nameKey'));
  //   final lastnameInput = find.byKey(Key('lasnameKey'));
  //   final dniInput = find.byKey(Key('dniKey'));
  //   final nssInput = find.byKey(Key('nssKey'));
  //   final phoneInput = find.byKey(Key('phoneKey'));
  //   final dateInput = find.byKey(Key('dateKey'));
  //   final button = find.byKey(Key('buttonKey'));
  //
  //
  //
  //   // expect(emailInput, findsOneWidget);
  //   // expect(usernameInput, findsOneWidget);
  //   // expect(nameInput, findsOneWidget);
  //   // expect(lastnameInput, findsOneWidget);
  //   // expect(dniInput, findsOneWidget);
  //   // expect(nssInput, findsOneWidget);
  //   // expect(phoneInput, findsOneWidget);
  //   // expect(dateInput, findsOneWidget);
  //   // expect(button, findsOneWidget);
  //
  //   // emailInput.
  //
  //   await tester.enterText(emailInput,'test@test.com');
  //   await tester.enterText(nameInput, 'Test');
  //   await tester.enterText(lastnameInput, 'Test');
  //   await tester.enterText(dniInput, '12345678Q');
  //   await tester.enterText(nssInput, '123456789012');
  //   await tester.enterText(phoneInput, '123456789');
  //   await tester.enterText(dateInput, '2000-10-04');
  //   await tester.enterText(usernameInput, 'usernameTest');
  //
  //
  //
  //   // await tester.pumpAndSettle();
  //   // expect(find.text('test@test.com'), findsOneWidget);
  //   // expect(find.text('Test'), findsOneWidget);
  //
  //   await tester.tap(button);
  //   await tester.pump();
  //   // // Fallara al llamar al API
  //   expect(find.text('Error al dar de alta al usuario.'),findsOneWidget);

  // });
}
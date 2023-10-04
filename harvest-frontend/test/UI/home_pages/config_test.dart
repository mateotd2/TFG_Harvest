import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/UI/home_pages/config.dart';
import 'package:harvest_frontend/utils/provider/sign_in_model.dart';
import 'package:mockito/mockito.dart';
import 'package:provider/provider.dart';

class MockEstado extends Mock implements SignInResponseModel {}

void main() {
  testWidgets('Despliegue del dialogo de alerta para cambio de contraseña', (WidgetTester tester) async {

    var response = SignInResponseDTO(
        id: 1,
        username: "username",
        tokenType: "tokenType",
        accessToken: "accessToken",
        roles: ["ROLE_ADMIN"]);


    final mockEstado = MockEstado();

    when(mockEstado.lastResponse).thenAnswer((_) => response);

    await tester.pumpWidget(ChangeNotifierProvider<SignInResponseModel>.value(
        value: mockEstado,
        child: MaterialApp(
          home: Config(),
        ))
    );

    await tester.pumpAndSettle();
    
    final changePasswordButton = find.byKey(Key('passwordKey'));
    
    await tester.tap(changePasswordButton);

    await tester.pump();
    
    expect(find.text('Listo'), findsOneWidget);
    

  });


  testWidgets('Cancelar el cambio de contraseña', (WidgetTester tester) async {

    var response = SignInResponseDTO(
        id: 1,
        username: "username",
        tokenType: "tokenType",
        accessToken: "accessToken",
        roles: ["ROLE_ADMIN"]);


    final mockEstado = MockEstado();

    when(mockEstado.lastResponse).thenAnswer((_) => response);

    await tester.pumpWidget(ChangeNotifierProvider<SignInResponseModel>.value(
        value: mockEstado,
        child: MaterialApp(
          home: Builder(
            builder: (context) {
              return Scaffold(body: Config());
            }
          ),
        ))
    );

    await tester.pumpAndSettle();

    final changePasswordButton = find.byKey(Key('passwordKey'));

    await tester.tap(changePasswordButton);

    await tester.pump();

    final cancelarButton = find.text('Cancelar');

    await tester.tap(cancelarButton);

    await tester.pump();

    expect(find.text('Cancelar'), findsNothing);


  });

  testWidgets('Validaciones de datos de entrada', (WidgetTester tester) async {

    var response = SignInResponseDTO(
        id: 1,
        username: "username",
        tokenType: "tokenType",
        accessToken: "accessToken",
        roles: ["ROLE_ADMIN"]);


    final mockEstado = MockEstado();

    when(mockEstado.lastResponse).thenAnswer((_) => response);

    await tester.pumpWidget(ChangeNotifierProvider<SignInResponseModel>.value(
        value: mockEstado,
        child: MaterialApp(
          home: Config(),
        ))
    );

    await tester.pumpAndSettle();

    final changePasswordButton = find.byKey(Key('passwordKey'));

    await tester.tap(changePasswordButton);

    await tester.pump();

    // final oldPassword = find.byKey(Key('oldPasswordKey'));
    // final newPassword = find.byKey(Key('newPasswordKey'));


    final botonAceptar = find.text('Listo');

    await tester.tap(botonAceptar);

    await tester.pump();

    expect(find.text('Añada una nueva contraseña'), findsOneWidget);
    




  });


  testWidgets('Test de cambio de contraseña fallida', (WidgetTester tester) async {

    var response = SignInResponseDTO(
        id: 1,
        username: "username",
        tokenType: "tokenType",
        accessToken: "accessToken",
        roles: ["ROLE_ADMIN"]);


    final mockEstado = MockEstado();

    when(mockEstado.lastResponse).thenAnswer((_) => response);

    await tester.pumpWidget(ChangeNotifierProvider<SignInResponseModel>.value(
        value: mockEstado,
        child: MaterialApp(
          home: Builder(
              builder: (context) {
                return Scaffold(body: Config());
              }
          ),
        ))
    );

    await tester.pumpAndSettle();

    final changePasswordButton = find.byKey(Key('passwordKey'));

    await tester.tap(changePasswordButton);

    await tester.pump();

    final oldPassword = find.byKey(Key('oldPasswordKey'));
    final newPassword = find.byKey(Key('newPasswordKey'));

    await tester.enterText(oldPassword, 'oldPassword');
    await tester.enterText(newPassword, 'newPasswordddddasfdasfa');



    final botonAceptar = find.text('Listo');

    await tester.tap(botonAceptar);

    await tester.pump();

    expect(find.text('Error en el cambio de contraseña.'), findsOneWidget);

  });


}
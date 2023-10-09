import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/UI/home.dart';
import 'package:harvest_frontend/utils/provider/sign_in_model.dart';
import 'package:mockito/mockito.dart';
import 'package:provider/provider.dart';

class MockEstado extends Mock implements SignInResponseModel {}

Widget createHome() => ChangeNotifierProvider<SignInResponseModel>(
      create: (context) => SignInResponseModel(),
      child: const MaterialApp(
        home: Home(),
      ),
    );

void main() {
  testWidgets('Prueba del menu home Admin', (WidgetTester tester) async {
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
          home: Home(),
        )));

    await tester.pumpAndSettle();

    expect(find.text('HARVEST APP'), findsOneWidget);

    final menu = find.byTooltip('Open navigation menu');
    await tester.tap(menu);
    await tester.pumpAndSettle();

    final tile = find.widgetWithText(ListTile, 'Funcion para Administradores');
    expect(tile, findsOneWidget);

    await tester.tap(tile);
    await tester.pumpAndSettle();

    final tileNoEncontrado =
        find.widgetWithText(ListTile, 'Funcion para Administradores');
    expect(tileNoEncontrado, findsNothing);
  });
  testWidgets('Prueba del menu home Configuracion',
      (WidgetTester tester) async {
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
          home: Home(),
        )));

    await tester.pumpAndSettle();

    final menu = find.byTooltip('Open navigation menu');
    await tester.tap(menu);
    await tester.pumpAndSettle();

    final tile = find.widgetWithText(ListTile, 'Configuracion');
    expect(tile, findsOneWidget);

    await tester.tap(tile);
    await tester.pumpAndSettle();

    final tileNoEncontrado = find.widgetWithText(ListTile, 'Configuracion');
    expect(tileNoEncontrado, findsNothing);
  });
  testWidgets('Prueba del menu home Exit', (WidgetTester tester) async {
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
          home: Home(),
        )));

    await tester.pumpAndSettle();

    final menu = find.byTooltip('Open navigation menu');
    await tester.tap(menu);
    await tester.pumpAndSettle();

    final tile = find.widgetWithText(ListTile, 'Salir');
    expect(tile, findsOneWidget);

    await tester.tap(tile);
    await tester.pumpAndSettle();

    final tileNoEncontrado = find.widgetWithText(ListTile, 'HARVEST APP');
    expect(tileNoEncontrado, findsNothing);
  });

  testWidgets('Prueba del menu home Capataz', (WidgetTester tester) async {
    var response = SignInResponseDTO(
        id: 1,
        username: "username",
        tokenType: "tokenType",
        accessToken: "accessToken",
        roles: ["ROLE_CAPATAZ"]);
    // SignInResponseModel provider = SignInResponseModel();

    final mockEstado = MockEstado();

    when(mockEstado.lastResponse).thenAnswer((_) => response);

    await tester.pumpWidget(ChangeNotifierProvider<SignInResponseModel>.value(
        value: mockEstado,
        child: MaterialApp(
          home: Home(),
        )));

    await tester.pumpAndSettle();

    expect(find.text('HARVEST APP'), findsOneWidget);

    final menu = find.byTooltip('Open navigation menu');
    await tester.tap(menu);
    await tester.pumpAndSettle();

    final tile = find.widgetWithText(ListTile, "Funcion para Capataces");
    expect(tile, findsOneWidget);

    await tester.tap(tile);
    await tester.pumpAndSettle();

    final tileNoEncontrado =
        find.widgetWithText(ListTile, "Funcion para Capataces");
    expect(tileNoEncontrado, findsNothing);
  });

  testWidgets('Prueba del menu home Tractorista', (WidgetTester tester) async {
    var response = SignInResponseDTO(
        id: 1,
        username: "username",
        tokenType: "tokenType",
        accessToken: "accessToken",
        roles: ["ROLE_TRACTORISTA"]);
    // SignInResponseModel provider = SignInResponseModel();

    final mockEstado = MockEstado();

    when(mockEstado.lastResponse).thenAnswer((_) => response);

    await tester.pumpWidget(ChangeNotifierProvider<SignInResponseModel>.value(
        value: mockEstado,
        child: MaterialApp(
          home: Home(),
        )));

    await tester.pumpAndSettle();

    expect(find.text('HARVEST APP'), findsOneWidget);

    final menu = find.byTooltip('Open navigation menu');
    await tester.tap(menu);
    await tester.pumpAndSettle();

    final tile = find.widgetWithText(ListTile, "Funcion para Tractoristas");
    expect(tile, findsOneWidget);

    await tester.tap(tile);
    await tester.pumpAndSettle();

    final tileNoEncontrado =
        find.widgetWithText(ListTile, 'Funcion para Tractoristas');
    expect(tileNoEncontrado, findsNothing);
  });
}

import 'package:flutter/material.dart';
import 'package:harvest_frontend/utils/check_empelado.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../utils/provider/sign_in_model.dart';
import 'home_pages/config.dart';

class Home extends StatefulWidget {
  const Home({super.key});

  @override
  State<StatefulWidget> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  var logger = Logger();

  final PageController _controladorPaginas = PageController();

  // Paginas de cada uno de los roles
  final List<Widget> paginas = [
    Text('PAGINA PRINCIPAL'),
    Config(),
    Text('PAGINA ADMINISTRACION'),
    Text('PAGINA CAPATACES'),
    Text('PAGINA TRACTORISTAS'),
  ];

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);

    final List<Widget> elementosDrawer = [
      Container(
        height: 50,
        child: DrawerHeader(
          child: Text('Harvest App'),
          decoration: BoxDecoration(color: Colors.green),
        ),
      )
    ];
    var pagina = 0;

    elementosDrawer.add(ListTile(
      leading: Icon(Icons.settings),
      title: Text('Configuracion'),
      onTap: () {
        logger.d('Configuracion pulsada');
        pagina++;
        _controladorPaginas.jumpToPage(pagina);
        Navigator.pop(context);
      },
    ));

    if (esAdmin(estado.lastResponse)) {
      elementosDrawer.add(ListTile(
        title: Text('Funcion para Administradores'),
        onTap: () {
          logger.d('Funcion para admins pulsada');
          pagina++;
          _controladorPaginas.jumpToPage(pagina);
          Navigator.pop(context);
        },
        leading: Icon(Icons.admin_panel_settings),
      ));
    }
    if (esCapataz(estado.lastResponse)) {
      elementosDrawer.add(ListTile(
        title: Text('Funcion para Capataces'),
        onTap: () {
          logger.d('Funcion para capataces pulsada');
          pagina++;
          _controladorPaginas.jumpToPage(pagina);
          Navigator.pop(context);
        },
        leading: Icon(Icons.group),
      ));
    }
    if (esTractorista(estado.lastResponse)) {
      elementosDrawer.add(ListTile(
        title: Text('Funcion para Tractoristas'),
        onTap: () {
          logger.d('Funcion para tractorista pulsada');
          pagina++;
          _controladorPaginas.jumpToPage(pagina);
          Navigator.pop(context);
        },
        leading: Icon(Icons.local_shipping_outlined),
      ));
    }

    elementosDrawer.add(ListTile(
      leading: Icon(Icons.exit_to_app),
      title: Text('Salir'),
      onTap: () {
        logger.d('Log out de la aplicacion');
        estado.clearResponse();
      },
    ));

    return Scaffold(
      body: PageView(
        children: paginas,
        controller: _controladorPaginas,
      ),
      drawer: Drawer(
        child: ListView(
          children: elementosDrawer,
        ),
      ),
      appBar: AppBar(
          backgroundColor: Colors.green,
          centerTitle: true,
          title: Text('HARVEST APP')),
    );
  }
}

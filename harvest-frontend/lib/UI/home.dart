import 'package:flutter/material.dart';
import 'package:harvest_frontend/UI/home_pages/admin_pages/admin.dart';
import 'package:harvest_frontend/UI/home_pages/admin_pages/campaign.dart';
import 'package:harvest_frontend/utils/check_empelado.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../utils/provider/sign_in_model.dart';
import 'home_pages/config_pages/config.dart';

class Home extends StatefulWidget {
  const Home({super.key});

  @override
  State<StatefulWidget> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  var logger = Logger();

  final PageController _controladorPaginas = PageController();

  // Paginas de cada uno de los roles
  // final List<Widget> paginas = [
  //   Text('PAGINA PRINCIPAL'),
  //   Text('PAGINA ADMINISTRACION'),
  //   Text('PAGINA CAPATACES'),
  //   Text('PAGINA TRACTORISTAS'),
  //   Config(),
  // ];

  List<Widget> paginas = [];

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

    // Primera pagina( pagina principa,)
    paginas.add(Text('PAGINA PRINCIPAL'));

    elementosDrawer.add(ListTile(
      leading: Icon(Icons.home),
      title: Text('Pagina Principal'),
      onTap: () {
        logger.d('Menu principal pulsado');
        _controladorPaginas.jumpToPage(0);
        Navigator.pop(context);
      },
    ));

    // Pagina de Administradores

    if (esAdmin(estado.lastResponse)) {
      paginas.add(Admin());
      pagina++;
      final paginaAdmin = pagina;
      elementosDrawer.add(ListTile(
        title: Text('Administración'),
        onTap: () {
          logger.d('Funcion para admins pulsada');
          _controladorPaginas.jumpToPage(paginaAdmin);
          Navigator.pop(context);
        },
        leading: Icon(Icons.admin_panel_settings),
      ));
    }
    if (esAdmin(estado.lastResponse)) {
      paginas.add(Campaign());
      pagina++;
      final paginaAdmin = pagina;
      elementosDrawer.add(ListTile(
        title: Text('Gestion de campaña'),
        onTap: () {
          logger.d('Gestión de campaña');
          _controladorPaginas.jumpToPage(paginaAdmin);
          Navigator.pop(context);
        },
        leading: Icon(Icons.flag),
      ));
    }

    // Pagina Capataces

    if (esCapataz(estado.lastResponse)) {
      paginas.add(Text('PAGINA CAPATACES'));
      pagina++;
      final paginaCapataces = pagina;
      elementosDrawer.add(ListTile(
        title: Text('Funcion para Capataces'),
        onTap: () {
          logger.d('Funcion para capataces pulsada');
          _controladorPaginas.jumpToPage(paginaCapataces);
          Navigator.pop(context);
        },
        leading: Icon(Icons.group),
      ));
    }
    // Pagina Tractoristas

    if (esTractorista(estado.lastResponse)) {
      paginas.add(Text('PAGINA TRACTORISTAS'));
      pagina++;
      final paginaTractoristas = pagina;
      elementosDrawer.add(ListTile(
        title: Text('Funcion para Tractoristas'),
        onTap: () {
          logger.d('Funcion para tractorista pulsada');
          _controladorPaginas.jumpToPage(paginaTractoristas);
          Navigator.pop(context);
        },
        leading: Icon(Icons.local_shipping_outlined),
      ));
    }
    pagina++;
    // Pagina Configuracion
    paginas.add(Config());

    elementosDrawer.add(ListTile(
      leading: Icon(Icons.settings),
      title: Text('Configuracion'),
      onTap: () {
        logger.d('Configuración pulsada');
        _controladorPaginas.jumpToPage(pagina);
        Navigator.pop(context);
      },
    ));

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
        allowImplicitScrolling: false,
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

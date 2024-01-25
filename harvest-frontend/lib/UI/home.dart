import 'dart:async';

import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/UI/home_pages/admin_pages/admin.dart';
import 'package:harvest_frontend/UI/home_pages/admin_pages/campaign.dart';
import 'package:harvest_frontend/UI/home_pages/capataz_pages/capataz.dart';
import 'package:harvest_frontend/utils/check_empelado.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../utils/plataform_apis/campanha_api.dart';
import '../utils/provider/sign_in_model.dart';
import '../utils/snack_bars.dart';
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
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final api = campanhaApiPlataform(auth);

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
      PhaseCampaign? phaseCampaign = PhaseCampaign.CAMPAIGN_NOT_STARTED;
      pagina++;
      final paginaCamp = pagina;
      elementosDrawer.add(ListTile(
        title: Text('Gestion de campaña'),
        onTap: () async {
          logger.d('Gestión de campaña');
          try {
            phaseCampaign =
                await api.getPhaseCampaign().timeout(Duration(seconds: 10));
            logger.d("FASE : $phaseCampaign");
            // snackGreen(context, 'Comenzando recolección');
          } on TimeoutException {
            snackTimeout(context);
          } catch (e) {
            snackRed(context, 'Error obteniendo la fase de campaña.');
          }

          _controladorPaginas.jumpToPage(paginaCamp);
          Navigator.pop(context);
        },
        leading: Icon(Icons.flag),
      ));
    }

    // Pagina Capataces

    if (esCapataz(estado.lastResponse)) {
      paginas.add(Capataz());
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

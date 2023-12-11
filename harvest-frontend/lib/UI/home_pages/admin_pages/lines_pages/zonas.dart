import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../../utils/plataform_apis/lines_api.dart';
import '../../../../utils/provider/sign_in_model.dart';

class Zonas extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _ZonasState();
}

class _ZonasState extends State<Zonas> {
  var logger = Logger();

  late Future<List<ZoneDTO>?> zonas;

  Future<List<ZoneDTO>?> obtenerTrabajadores(LineasApi api) {
    return api.getZones().timeout(Duration(seconds: 10));
  }

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final api = lineasApiPlataform(auth);

    setState(() {
      zonas = api.getZones().timeout(Duration(seconds: 10));
    });

    return Scaffold(
      body: RefreshIndicator(
        onRefresh: () async {
          setState(() {
            zonas = api.getZones().timeout(Duration(seconds: 10));
          });
        },
        child: FutureBuilder(
          future: zonas,
          builder:
              (BuildContext context, AsyncSnapshot<List<ZoneDTO>?> snapshot) {
            if (snapshot.connectionState == ConnectionState.done) {
              List<ZoneDTO>? zonasObtenidas = snapshot.data;
              logger.d(zonasObtenidas);

              if (zonasObtenidas == null) {
                return Text("Nada que mostrar :(");
              } else {
                return ListView.builder(
                    itemCount: zonasObtenidas.length,
                    itemBuilder: (context, index) {
                      final zona = zonasObtenidas[index];
                      return ListTile(
                        title: Text(zona.name),
                        trailing: Icon(Icons.arrow_forward_ios_sharp),
                        onTap: () {
                          logger.d("Zona ${zona.name} pulsada");
                        },
                      );
                    });
              }
            } else if (snapshot.hasError) {
              ScaffoldMessenger.of(context).showSnackBar(SnackBar(
                  key: Key('snackKey'),
                  backgroundColor: Colors.red,
                  content: Text('Error obteniendo los trabajadores')));
              Navigator.pop(context);
              return Text("Nada que enseñar :(");
            } else {
              return Center(child: CircularProgressIndicator());
            }
          },
        ),
      ),
    );
  }
}

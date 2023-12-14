import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/UI/home_pages/admin_pages/lines_pages/zone_details.dart';
import 'package:harvest_frontend/utils/snack_bars.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../../utils/plataform_apis/lines_api.dart';
import '../../../../utils/provider/sign_in_model.dart';
import 'add_zone.dart';

class Zonas extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _ZonasState();
}

class _ZonasState extends State<Zonas> {
  var logger = Logger();

  late Future<List<ZoneDTO>?> zonas;

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
                      return Container(
                        color: index % 2 == 0 ? Colors.grey[200] : null,
                        child: ListTile(
                          title: Text(zona.name),
                          trailing: Icon(Icons.arrow_forward_ios_sharp),
                          onTap: () async {
                            logger.d("Zona ${zona.name} pulsada");
                            await Navigator.push(
                                context,
                                MaterialPageRoute(
                                    builder: (context) =>
                                        ZoneDetails(zoneId: zona.id)));
                            setState(() {
                              zonas =
                                  api.getZones().timeout(Duration(seconds: 10));
                            });
                          },
                        ),
                      );
                    });
              }
            } else if (snapshot.hasError) {
              WidgetsBinding.instance.addPostFrameCallback((_) {
                snackRed(context, 'Error obteniendo las zonas');
              });
              return Text("Nada que enseñar :(");
            } else {
              return Center(child: CircularProgressIndicator());
            }
          },
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          logger.d('ADD Zone PULSADO');
          await Navigator.push(
              context, MaterialPageRoute(builder: (context) => AddZone()));
          setState(() {
            zonas = api.getZones().timeout(Duration(seconds: 10));
          });
        },
        key: Key('addZoneKey'),
        child: Icon(Icons.add),
      ),
    );
  }
}

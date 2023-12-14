import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/utils/snack_bars.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../../utils/plataform_apis/lines_api.dart';
import '../../../../utils/provider/sign_in_model.dart';
import 'add_line.dart';
import 'line_details.dart';

class Lines extends StatefulWidget {
  final int? zoneId;

  Lines({required this.zoneId});

  @override
  State<StatefulWidget> createState() => _LinesState();
}

class _LinesState extends State<Lines> {
  var logger = Logger();

  late Future<List<LineDTO>?> lineas;

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final api = lineasApiPlataform(auth);

    setState(() {
      lineas = api.getLines(widget.zoneId!).timeout(Duration(seconds: 10));
    });

    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.green,
        title: Text('Lineas de Zona ${widget.zoneId}'),
      ),
      body: RefreshIndicator(
        onRefresh: () async {
          setState(() {
            lineas =
                api.getLines(widget.zoneId!).timeout(Duration(seconds: 10));
          });
        },
        child: FutureBuilder(
          future: lineas,
          builder:
              (BuildContext context, AsyncSnapshot<List<LineDTO>?> snapshot) {
            if (snapshot.connectionState == ConnectionState.done) {
              List<LineDTO>? lineasObtenidas = snapshot.data;
              logger.d(lineasObtenidas);

              if (lineasObtenidas == null) {
                return Text("Nada que mostrar :(");
              } else {
                return ListView.builder(
                    itemCount: lineasObtenidas.length,
                    itemBuilder: (context, index) {
                      final linea = lineasObtenidas[index];
                      return Container(
                        color: index % 2 == 0 ? Colors.grey[200] : null,
                        child: ListTile(
                          title: Text("Linea ${linea.lineNumber.toString()}"),
                          trailing: Icon(Icons.arrow_forward_ios_sharp),
                          onTap: () async {
                            logger.d("Zona ${linea.name} pulsada");
                            await Navigator.push(
                                context,
                                MaterialPageRoute(
                                    builder: (context) => LineDetails(
                                        lineId: linea.id,
                                        enabled: linea.harvestEnabled)));
                            setState(() {
                              lineas = api
                                  .getLines(widget.zoneId!)
                                  .timeout(Duration(seconds: 10));
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
          logger.d('ADD Line PULSADO');
          await Navigator.push(
              context,
              MaterialPageRoute(
                  builder: (context) => AddLine(
                        zoneId: widget.zoneId,
                      )));
          setState(() {
            lineas =
                api.getLines(widget.zoneId!).timeout(Duration(seconds: 10));
          });
        },
        key: Key('addLineKey'),
        child: Icon(Icons.add),
      ),
    );
  }
}

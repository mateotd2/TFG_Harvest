import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/UI/home_pages/admin_pages/lines_pages/update_line.dart';
import 'package:harvest_frontend/utils/snack_bars.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../../utils/plataform_apis/lines_api.dart';
import '../../../../utils/provider/sign_in_model.dart';

class LineDetails extends StatefulWidget {
  final int? lineId;

  LineDetails({required this.lineId});

  @override
  State<StatefulWidget> createState() => _LineDetailsState();
}

class _LineDetailsState extends State<LineDetails> {
  var logger = Logger();
  late Future<LineDetailsDTO?> linea;

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final api = lineasApiPlataform(auth);
    LineDetailsDTO? lineaObtenida;

    int? lineaId = widget.lineId;

    setState(() {
      linea = api.getLineDetails(lineaId!).timeout(Duration(seconds: 10));
    });

    return Scaffold(
      appBar: AppBar(
        title: Text('Detalles Linea $lineaId'),
      ),
      body: RefreshIndicator(
        onRefresh: () async {
          setState(() {
            linea = api.getLineDetails(lineaId!).timeout(Duration(seconds: 10));
          });
        },
        child: FutureBuilder(
          future: linea,
          builder:
              (BuildContext context, AsyncSnapshot<LineDetailsDTO?> snapshot) {
            if (snapshot.connectionState == ConnectionState.done) {
              lineaObtenida = snapshot.data;
              logger.d(lineaObtenida);
              return Column(
                children: [
                  ListTile(
                    title: Text("Numero de linea:"),
                    subtitle: Text("${lineaObtenida?.lineNumber}",
                        style: TextStyle(fontSize: 18.0)),
                  ),
                  ListTile(
                    title: Text("Longitud:"),
                    subtitle: Text("${lineaObtenida?.distance} metros",
                        style: TextStyle(fontSize: 18.0)),
                  ),

                  ListTile(
                    title: Text("Vid:"),
                    subtitle: Text("${lineaObtenida?.vid?.name}",
                        style: TextStyle(fontSize: 18.0)),
                  ),
                  ListTile(
                    title: Text("Descripcion de vid:"),
                    subtitle: Text("${lineaObtenida?.vid?.description}",
                        style: TextStyle(fontSize: 18.0)),
                  ),
                  // TODO: añadir funcionalidad para Habilitar/Deshabilitar
                  Visibility(
                    visible: lineaObtenida!.harvestEnabled!,
                    replacement: ListTile(
                      title: Text("Recoleccion de linea Deshabilitada",
                          style: TextStyle(fontSize: 18.0)),
                    ),
                    child: ListTile(
                      title: Text("Recolecciond de linea Habilitada",
                          style: TextStyle(fontSize: 18.0)),
                    ),
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      ElevatedButton(
                          onPressed: () {
                            logger.d("Modificar datos de zona pulsado");
                            Navigator.push(
                                context,
                                MaterialPageRoute(
                                    builder: (context) => UpdateLine(
                                        lineId: lineaId,
                                        line: lineaObtenida!)));
                          },
                          child: Text("Actualizar Datos")),
                      SizedBox(width: 96.0),
                      ElevatedButton(
                          style: ElevatedButton.styleFrom(
                              backgroundColor: Colors.red),
                          onPressed: () async {
                            logger.d("Eliminar linea pulsado");
                            await showDialog(
                                context: context,
                                builder: (BuildContext context2) => AlertDialog(
                                      title: Text("Confirmación"),
                                      content: Text(
                                          "Esta seguro de eliminar la  linea?"),
                                      actions: [
                                        TextButton(
                                          onPressed: () {
                                            logger.d("Cancelado");
                                            Navigator.pop(context);
                                          },
                                          child: Text('Cancelar'),
                                        ),
                                        TextButton(
                                          onPressed: () async {
                                            logger.d("Dado de baja");
                                            MessageResponseDTO? response =
                                                await api
                                                    .deleteLine(lineaId!)
                                                    .timeout(
                                                        Duration(seconds: 10));
                                            logger.d(response);
                                            Navigator.pop(context2);
                                          },
                                          child: Text('Eliminar'),
                                        ),
                                      ],
                                    ));
                            Navigator.pop(context);
                          },
                          child: Text("Eliminar Linea")),
                    ],
                  )
                ],
              );
            } else if (snapshot.hasError) {
              snackRed(context, 'Error obteniendo la linea');
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

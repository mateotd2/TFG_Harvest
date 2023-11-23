import 'dart:async';

import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/UI/home_pages/admin_pages/worker_details_update.dart';
import 'package:intl/intl.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../utils/plataform_apis/workers_api.dart';
import '../../../utils/provider/sign_in_model.dart';

class WorkerDetails extends StatefulWidget {
  final int? workerId;

  WorkerDetails({required this.workerId});

  @override
  State<StatefulWidget> createState() => _WorkerDetailsState();
}

class _WorkerDetailsState extends State<WorkerDetails> {
  var logger = Logger();
  late Future<WorkerDTO?> trabajador;

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final apiInstance = trabajadoresApiPlataform(auth);
    WorkerDTO? trabajadorObtenido;
    int? workerId = widget.workerId;

    setState(() {
      trabajador =
          apiInstance.getWorker(workerId!).timeout(Duration(seconds: 10));
    });

    return Scaffold(
      appBar: AppBar(
        title: const Text('Detalles de trabajador'),
      ),
      body: RefreshIndicator(
        onRefresh: () async {
          setState(() {
            trabajador =
                apiInstance.getWorker(workerId!).timeout(Duration(seconds: 10));
          });
        },
        child: FutureBuilder(
          future: trabajador,
          builder: (BuildContext context, AsyncSnapshot<WorkerDTO?> snapshot) {
            if (snapshot.connectionState == ConnectionState.done) {
              trabajadorObtenido = snapshot.data;
              logger.d(trabajadorObtenido);

              return Column(
                children: [
                  ListTile(
                    title: Text("Nombre:"),
                    subtitle: Text(trabajadorObtenido!.name,
                        style: TextStyle(fontSize: 18.0)),
                  ),
                  ListTile(
                    title: Text("Apellidos:"),
                    subtitle: Text(trabajadorObtenido!.lastname,
                        style: TextStyle(fontSize: 18.0)),
                  ),
                  ListTile(
                    title: Text("Numero de telefono:"),
                    subtitle: Text(trabajadorObtenido!.phone,
                        style: TextStyle(fontSize: 18.0)),
                  ),
                  ListTile(
                    title: Text("DNI:"),
                    subtitle: Text(trabajadorObtenido!.dni,
                        style: TextStyle(fontSize: 18.0)),
                  ),
                  ListTile(
                    title: Text("NSS:"),
                    subtitle: Text(trabajadorObtenido!.nss,
                        style: TextStyle(fontSize: 18.0)),
                  ),
                  ListTile(
                    title: Text("Dirección:"),
                    subtitle: Text(trabajadorObtenido!.address,
                        style: TextStyle(fontSize: 18.0)),
                  ),
                  ListTile(
                    title: Text("Fecha de nacimiento:"),
                    subtitle: Text(
                        DateFormat('dd-MM-yyyy')
                            .format(trabajadorObtenido!.birthdate),
                        style: TextStyle(fontSize: 18.0)),
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      ElevatedButton(
                        onPressed: () async {
                          logger.d("Boton Modificar pulstado");
                          await Navigator.push(
                              context,
                              MaterialPageRoute(
                                  builder: (context) => WorkerDetailsUpdate(
                                      worker: trabajadorObtenido!)));
                          // trabajadorObtenido = await apiInstance.getWorker(workerId!).timeout(Duration(seconds: 10));
                          setState(() {
                            trabajador = apiInstance
                                .getWorker(workerId!)
                                .timeout(Duration(seconds: 10));
                          });

                          // Navigator.pop(context);
                        },
                        child: Text("Modificar"),
                      ),
                      SizedBox(width: 96.0),
                      ElevatedButton(
                          onPressed: () async {
                            logger.d("Boton Baja pulsado");

                            await showDialog(
                                // barrierDismissible: false,
                                context: context,
                                // builder: (_) => mostrarAlerta(context,trabajadorObtenido!.id!, apiInstance)
                                // TODO: No funciona el volver a la pantalla del trabajador
                                builder: (BuildContext context2) => AlertDialog(
                                      title: Text("Confirmación"),
                                      content:
                                          Text("Dar de baja al trabajador"),
                                      actions: [
                                        TextButton(
                                          onPressed: () {
                                            logger.d("Cancelado");
                                            Navigator.pop(context);
                                          },
                                          child: Text('Cancelar'),
                                        ),
                                        TextButton(
                                          onPressed: () {
                                            logger.d("Dado de baja");
                                            apiInstance.deleteWorker(
                                                trabajadorObtenido!.id!);
                                            Navigator.pop(context2);
                                          },
                                          child: Text('Baja'),
                                        ),
                                      ],
                                    ));
                            // mostrarAlerta(context,trabajadorObtenido!.id!, apiInstance);
                            // AlertDialog()
                            Navigator.pop(context);
                          },
                          child: Text("Dar de baja trabajador"),
                          style: ElevatedButton.styleFrom(
                              backgroundColor: Colors.red)),
                    ],
                  ),
                ],
              );
            } else if (snapshot.hasError) {
              ScaffoldMessenger.of(context).showSnackBar(SnackBar(
                  key: Key('snackKey'),
                  backgroundColor: Colors.red,
                  content: Text('Error obteniendo el trabajador')));
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

  AlertDialog mostrarAlerta(
      BuildContext context, int id, TrabajadoresApi apiInstance) {
    return (AlertDialog(
      title: Text('Confirmacion'),
      content: Text("¿Dar de baja al trabajador definitivamente?"),
      actions: [
        TextButton(
            onPressed: Navigator.of(context).pop, child: Text('Cancelar')),
        TextButton(
          onPressed: () async {
            try {
              await apiInstance.deleteWorker(id);
              logger.d("Baja del trabajador con id:$id confirmada");
              Navigator.of(context).pop;
            } catch (e) {
              logger.d("Error");
              Navigator.of(context).pop;
            }
          },
          style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
          child: Text("Confirmar"),
        ),
      ],
    ));
  }
}

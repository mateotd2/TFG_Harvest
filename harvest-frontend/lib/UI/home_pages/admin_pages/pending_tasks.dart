import 'dart:async';

import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../utils/plataform_apis/campanha_api.dart';
import '../../../utils/provider/sign_in_model.dart';
import '../../../utils/snack_bars.dart';

enum TypePhase {
  none,
  cleaning,
  pruning,
  harvest,
}

class PendingTasks extends StatefulWidget {
  final TypePhase typePhase;

  PendingTasks({required this.typePhase});

  @override
  State<StatefulWidget> createState() => _PendingTasks();
}

class _PendingTasks extends State<PendingTasks> {
  var logger = Logger();
  late Future<List<ListedTaskDTO>?> tasks;

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final api = campanhaApiPlataform(auth);

    setState(() {
      tasks = api.pendingTasks().timeout(Duration(seconds: 10));
    });

    // TODO MOSTRAR TAREAS PENDIENTES SEGUN LA FASE Y CONFIRMACION PARA PASAR A SIGUIENTE FASE
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.green,
        title: Text('Tareas pendientes'),
      ),
      body: RefreshIndicator(
        onRefresh: () async {
          setState(() {
            tasks = api.pendingTasks().timeout(Duration(seconds: 10));
          });
        },
        child: FutureBuilder(
            future: tasks,
            builder: (BuildContext context, AsyncSnapshot<dynamic> snapshot) {
              if (snapshot.connectionState == ConnectionState.done) {
                snapshot.connectionState == ConnectionState.done;
                List<ListedTaskDTO> tasksObtenidas = snapshot.data;
                logger.d(tasksObtenidas);

                List<Widget> pantalla = [];
                List<Widget> filaBotones = [];
                if (tasksObtenidas.isEmpty) {
                  return Center(child: Text("Nada que mostrar :("));
                } else {
                  pantalla.add(Expanded(
                    child: ListView.builder(
                      itemCount: tasksObtenidas.length,
                      itemBuilder: (BuildContext context, int index) {
                        final task = tasksObtenidas[index];
                        return Container(
                          color: index % 2 == 0 ? Colors.grey[200] : null,
                          child: ListTile(
                            title: Text("Zona: ${task.zoneName}"),
                            subtitle: Text(
                                "Linea: ${task.numeroLinea}  Tipo de Tarea: ${task.tipoTrabajo}"),
                          ),
                        );
                      },
                    ),
                  ));

                  if (widget.typePhase != TypePhase.none) {
                    if (widget.typePhase == TypePhase.harvest) {
                      pantalla
                          .add(Text("Confirmación para finalizar campaña:"));
                    } else {
                      pantalla.add(
                          Text("Confirmación para pasar a siguiente fase:"));
                    }

                    filaBotones.add(ElevatedButton(
                        onPressed: () {
                          Navigator.pop(context);
                        },
                        child: Text("Cancelar"),
                        style: ElevatedButton.styleFrom(
                            backgroundColor: Colors.red)));
                  }

                  // TODO: TEXTO Y BOTONES PARA CONFIRMAR EL CAMBIO DE FASE
                  switch (widget.typePhase) {
                    case TypePhase.none:
                      filaBotones.add(ElevatedButton(
                          onPressed: () {}, child: Text("Pasar Fase")));
                    case TypePhase.cleaning:
                      filaBotones.add(ElevatedButton(
                          onPressed: () async {
                            try {
                              // PendingTasks(TypePhase.cleaning);
                              await api
                                  .startPruning()
                                  .timeout(Duration(seconds: 10));
                              snackGreen(context, 'Pasando a fase de poda.');
                            } on TimeoutException {
                              snackTimeout(context);
                            } catch (e) {
                              snackRed(context,
                                  'Error al intentar pasar a la fase de poda.');
                            }
                            Navigator.pop(context);
                          },
                          child: Text("Pasar Fase")));
                    case TypePhase.pruning:
                      filaBotones.add(ElevatedButton(
                          onPressed: () async {
                            try {
                              await api
                                  .startHarvesting()
                                  .timeout(Duration(seconds: 10));
                              snackGreen(
                                  context, 'Pasando a fase de recolección.');
                            } on TimeoutException {
                              snackTimeout(context);
                            } catch (e) {
                              snackRed(context,
                                  'Error al intentar pasar a la fase de recolección');
                            }
                            Navigator.pop(context);
                          },
                          child: Text("Pasar Fase")));
                    case TypePhase.harvest:
                      filaBotones.add(ElevatedButton(
                          onPressed: () async {
                            try {
                              await api
                                  .endCampaign()
                                  .timeout(Duration(seconds: 10));
                              snackGreen(context, 'Finalizando la campaña.');
                            } on TimeoutException {
                              snackTimeout(context);
                            } catch (e) {
                              snackRed(context,
                                  'Error al intentar finalizar la campaña.');
                            }
                            Navigator.pop(context);
                          },
                          child: Text("Finalizar")));
                  }
                  pantalla.add(Center(
                      child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: filaBotones)));

                  return Column(
                    children: pantalla,
                  );
                }

                // return Text("data");
              } else if (snapshot.hasError) {
                WidgetsBinding.instance.addPostFrameCallback((_) {
                  snackRed(context, 'Error obteniendo las zonas');
                });
                return Center(child: Text("Nada que enseñar :("));
              } else {
                return Center(child: CircularProgressIndicator());
              }
            }),
      ),
    );
  }
}

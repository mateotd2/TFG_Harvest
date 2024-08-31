import 'dart:async';

import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/utils/plataform_apis/capataz_api.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../utils/plataform_apis/campanha_api.dart';
import '../../../utils/provider/sign_in_model.dart';
import '../../../utils/snack_bars.dart';
import '../capataz_pages/task_details.dart';

enum TypePhase {
  none,
  cleaning,
  pruning,
  harvest,
}

class NotCompletedTasks extends StatefulWidget {
  final TypePhase typePhase;

  NotCompletedTasks({required this.typePhase});

  @override
  State<StatefulWidget> createState() => _NotCompletedTasks();
}

class _NotCompletedTasks extends State<NotCompletedTasks> {
  var logger = Logger();
  late Future<List<ListedTaskDTO>?> tasks;

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final campanhaApi = campanhaApiPlataform(auth);
    final capatazApi = capatazApiPlataform(auth);

    List<Widget> pantalla = [];
    List<Widget> filaBotones = [];

    setState(() {
      tasks = capatazApi.pendingTasks().timeout(Duration(seconds: 10));
    });

    return Scaffold(
      body: RefreshIndicator(
        onRefresh: () async {
          setState(() {
            tasks = capatazApi.pendingTasks().timeout(Duration(seconds: 10));
          });
        },
        child: FutureBuilder(
            future: tasks,
            builder: (BuildContext context, AsyncSnapshot<dynamic> snapshot) {
              if (snapshot.connectionState == ConnectionState.done) {
                snapshot.connectionState == ConnectionState.done;
                List<ListedTaskDTO>? tasksObtenidas = snapshot.data;
                logger.d(tasksObtenidas);
              switch (widget.typePhase) {
                case TypePhase.none:
                  break;
                case TypePhase.cleaning:
                  filaBotones.add(ElevatedButton(
                      onPressed: () async {
                        try {
                          // PendingTasks(TypePhase.cleaning);
                          await campanhaApi
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
                          await campanhaApi
                              .startHarvesting()
                              .timeout(Duration(seconds: 10));
                          snackGreen(context, 'Pasando a fase de recolección.');
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
                          await campanhaApi
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



                if (!(tasksObtenidas != null && tasksObtenidas.isNotEmpty)) {
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
                  pantalla.add(Row(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: filaBotones));
                  return Column(
                    children: pantalla,
                  );
                } else {
                  pantalla.add(AppBar(
                    backgroundColor: Colors.green,
                    title: const Text('Tareas Pendientes'),
                  ),);
                  pantalla.add(Expanded(
                    child: ListView.builder(
                      itemCount: tasksObtenidas.length,
                      itemBuilder: (BuildContext context, int index) {
                        final task = tasksObtenidas[index];
                        return Container(
                          color: index % 2 == 0 ? Colors.grey[200] : null,
                          child: ListTile(
                            // onTap: () async {
                            //     bool actualizar = await Navigator.push(
                            //         context,
                            //         MaterialPageRoute(
                            //             builder: (context) => TaskDetails(
                            //                   taskId: task.idTarea,
                            //                   estado: Estado.sinEmpezar,
                            //                 )));
                            //     if (actualizar) {
                            //       setState(() {
                            //         tasks = capatazApi
                            //             .pendingTasks()
                            //             .timeout(Duration(seconds: 10));
                            //       });
                            //     }
                            // },
                            //trailing: Icon(Icons.arrow_forward_ios_sharp),
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
                  snackRed(context, 'Error obteniendo las tareas');
                });

                if (widget.typePhase != TypePhase.none) {
                  return Row(children: filaBotones);
                } else {
                  return Center(child: Text("Nada que mostrar :("));
                }
              } else {
                return Center(child: CircularProgressIndicator());
              }
            }),
      ),
    );
  }
}

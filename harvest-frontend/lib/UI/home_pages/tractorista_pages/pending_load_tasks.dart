import 'dart:async';

import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/UI/home_pages/tractorista_pages/selector_load_tasks.dart';
import 'package:harvest_frontend/utils/plataform_apis/workers_api.dart';
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

class PendingLoadTasks extends StatefulWidget {
  final TypePhase typePhase;

  PendingLoadTasks({required this.typePhase});

  @override
  State<StatefulWidget> createState() => _PendingLoadTasks();
}

class _PendingLoadTasks extends State<PendingLoadTasks> {
  var logger = Logger();
  late Future<List<ListedTaskDTO>?> tasks;
  bool update = false;

  void _actualizarTasks() {
    logger.d("Actualizar Tareas");
    setState(() {
      update = !update;
    });
  }

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final api = campanhaApiPlataform(auth);

    setState(() {
      tasks = api.loadTasks().timeout(Duration(seconds: 10));
    });

    // Con esto muestro el AppBar o no
    bool mostrarAppBar = widget.typePhase != TypePhase.none;

    return Scaffold(
      appBar: mostrarAppBar
          ? AppBar(
              backgroundColor: Colors.green,
              title: Text('Tareas pendientes'),
            )
          : null,
      body: RefreshIndicator(
        onRefresh: () async {
          setState(() {
            tasks = api.loadTasks().timeout(Duration(seconds: 10));
          });
        },
        child: FutureBuilder(
            future: tasks,
            builder: (BuildContext context, AsyncSnapshot<dynamic> snapshot) {
              if (snapshot.connectionState == ConnectionState.done) {
                snapshot.connectionState == ConnectionState.done;
                List<ListedTaskDTO>? tasksObtenidas = snapshot.data;
                logger.d(tasksObtenidas);

                if (!(tasksObtenidas != null && tasksObtenidas.isNotEmpty)) {
                  return Center(child: Text("Nada que mostrar :("));
                }

                return TaksChecked(
                    taskObtenidas: tasksObtenidas, onUpdate: _actualizarTasks);
              } else if (snapshot.hasError) {
                WidgetsBinding.instance.addPostFrameCallback((_) {
                  snackRed(context, 'Error obteniendo las tareas');
                });
                return Center(child: Text("Nada que mostrar :("));
              } else {
                return Center(child: CircularProgressIndicator());
              }
            }),
      ),
    );
  }
}

class TaksChecked extends StatefulWidget {
  final List<ListedTaskDTO> taskObtenidas;
  final VoidCallback onUpdate;

  TaksChecked({required this.taskObtenidas, required this.onUpdate});

  @override
  State<StatefulWidget> createState() => _TaskChecked();
}

class _TaskChecked extends State<TaksChecked> {
  var logger = Logger();

  Map<int, bool> checksTareas = {};
  bool firstBuild = true;
  bool update = false;

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final api = campanhaApiPlataform(auth);
    final apiWorkers = trabajadoresApiPlataform(auth);

    List<ListedTaskDTO> tasksObtenidas = widget.taskObtenidas;
    if (firstBuild) {
      Map<int, bool> firstChecks = {};
      for (var element in tasksObtenidas) {
        firstChecks[element.idTarea!] = false;
      }
      setState(() {
        checksTareas = firstChecks;
      });
      firstBuild = false;
    }
    logger.d(checksTareas);
    return Scaffold(
      body: ListView.builder(
        scrollDirection: Axis.vertical,
        shrinkWrap: true,
        itemCount: tasksObtenidas.length,
        itemBuilder: (BuildContext context, int index) {
          final task = tasksObtenidas[index];

          return Container(
            color: index % 2 == 0 ? Colors.grey[200] : null,
            child: CheckboxListTile(
              value: checksTareas[task.idTarea],
              onChanged: (bool? value) {
                setState(() {
                  checksTareas[task.idTarea!] = value!;
                });
              },
              title: Text("Zona: ${task.zoneName}"),
              subtitle: Text(
                  "Linea: ${task.numeroLinea}  Tipo de Tarea: ${task.tipoTrabajo}"),
            ),
          );
        },
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.centerFloat,
      floatingActionButton: Visibility(
          visible: checksTareas.containsValue(true),
          child: ElevatedButton(
            onPressed: () async {
              List<int> idTareas = [];
              checksTareas.forEach((key, value) {
                if (value) idTareas.add(key);
              });
              logger.d(idTareas);
              List<TractorDTO>? tractores = await api
                  .getAvailableTractors()
                  .timeout(Duration(seconds: 10));
              List<WorkerDTO>? trabajadores = await apiWorkers
                  .getAvailableWorkers()
                  .timeout(Duration(seconds: 10));

              if (tractores!.isEmpty) {
                snackRed(context, "Sin tractores para realizar las tareas");
              } else {
                bool actualizar = await Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (context) => SelectorsLoadTasks(
                              idTareas: idTareas,
                              tractoresDisponibles: tractores,
                              trabajadoresDisponibles: trabajadores!,
                            )));

                if (actualizar) widget.onUpdate();
                // if (actualizar) {
                //   setState(() {
                //     actualizar = !actualizar;
                //   });
                // }
              }
            },
            child: Text("Iniciar Tareas"),
          )),
    );
  }
}

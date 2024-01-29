import 'dart:async';

import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../utils/plataform_apis/campanha_api.dart';
import '../../../utils/provider/sign_in_model.dart';
import '../../../utils/snack_bars.dart';

class InProgressLoadTasks extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _InProgressLoadTasks();
}

class _InProgressLoadTasks extends State<InProgressLoadTasks> {
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
      tasks = api.inProgressLoadTasks().timeout(Duration(seconds: 10));
    });

    return Scaffold(
      body: RefreshIndicator(
        onRefresh: () async {
          setState(() {
            tasks = api.inProgressLoadTasks().timeout(Duration(seconds: 10));
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
  final VoidCallback onUpdate;
  final List<ListedTaskDTO> taskObtenidas;

  TaksChecked({required this.taskObtenidas, required this.onUpdate});

  @override
  State<StatefulWidget> createState() => _TaskChecked();
}

class _TaskChecked extends State<TaksChecked> {
  var logger = Logger();

  Map<int, bool> checksTareas = {};
  bool firstBuild = true;
  int? idTractorSeleccionado;

  @override
  Widget build(BuildContext context) {
    logger.d(idTractorSeleccionado);

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
              enabled: idTractorSeleccionado == null ||
                  task.idTractor == idTractorSeleccionado,
              value: checksTareas[task.idTarea],
              onChanged: (bool? value) {
                setState(() {
                  checksTareas[task.idTarea!] = value!;
                  idTractorSeleccionado ??= task.idTractor;

                  if (!checksTareas.containsValue(true)) {
                    idTractorSeleccionado = null;
                  }
                });
              },
              title: Text("Zona: ${task.zoneName}  Tractor: ${task.idTractor}"),
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
              bool? actualizar = await showDialog<bool>(
                context: context,
                builder: (BuildContext context) {
                  return FinalizarTareasForm(tasksIds: idTareas);
                },
              );
              if (actualizar!) {
                widget.onUpdate();
              }
            },
            child: Text("Finalizar Tareas"),
          )),
    );
  }
}

class FinalizarTareasForm extends StatefulWidget {
  final List<int> tasksIds;

  FinalizarTareasForm({required this.tasksIds});

  @override
  State<StatefulWidget> createState() => _FinalizarTareasForm();
}

class _FinalizarTareasForm extends State<FinalizarTareasForm> {
  final GlobalKey<FormState> _form = GlobalKey<FormState>();
  TextEditingController comentarioController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final api = campanhaApiPlataform(auth);
    var logger = Logger();

    return AlertDialog(
      title: Text("Finalizar Tareas"),
      actions: [
        ElevatedButton(
            onPressed: () {
              logger.d('Finalizacion tareas cancelada');
              Navigator.of(context).pop();
            },
            child: Text('Cancelar')),
        ElevatedButton(
            onPressed: () async {
              if (_form.currentState!.validate()) {
                _form.currentState?.save();
                logger.d('Finalizacion de tareas');
                EndLoadTasksDTO endLoadTaskDTO = EndLoadTasksDTO(
                    idLoadTasks: widget.tasksIds,
                    comment: comentarioController.text);

                logger.d(endLoadTaskDTO);
                try {
                  await api
                      .endLoadTasks(endLoadTaskDTO)
                      .timeout(Duration(seconds: 10));
                  snackGreen(context, "Tarea finalizada");
                  Navigator.of(context).pop(true);
                } on TimeoutException {
                  Navigator.of(context).pop(false);
                } catch (e) {
                  snackRed(context, "No se pudo finalizar la tarea");
                  Navigator.of(context).pop(false);
                }
              }
            },
            child: Text("Aceptar"))
      ],
      content: SingleChildScrollView(
        child: Form(
          key: _form,
          child: Column(
            children: [
              TextField(
                showCursor: true,
                maxLines: 7,
                minLines: 7,
                controller: comentarioController,
                decoration: InputDecoration(
                  border: OutlineInputBorder(
                      // borderSide: BorderSide(color: Colors.blue, width: 2.0),
                      ),
                  label: Text('Comentarios de trabajo:'),
                ),
                autofocus: true,
                key: Key('commentarios'),
                keyboardType: TextInputType.multiline,
                maxLength: 2096,
              ),
            ],
          ),
        ),
      ),
    );
  }
}

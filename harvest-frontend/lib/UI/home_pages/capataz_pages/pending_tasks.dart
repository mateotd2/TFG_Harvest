import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/utils/plataform_apis/capataz_api.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';
import 'package:qr_bar_code_scanner_dialog/qr_bar_code_scanner_dialog.dart';

import '../../../utils/provider/sign_in_model.dart';
import '../../../utils/snack_bars.dart';
import '../admin_pages/not_completed_tasks.dart';
import 'task_details.dart';

class PendingTasks extends StatefulWidget {
  final TypePhase typePhase;

  PendingTasks({required this.typePhase});

  @override
  State<StatefulWidget> createState() => _PendingTasks();
}

class _PendingTasks extends State<PendingTasks> {
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
    final capatazApi = capatazApiPlataform(auth);

    setState(() {
      tasks = capatazApi.pendingTasks().timeout(Duration(seconds: 10));
    });

    return Scaffold(
        body: RefreshIndicator(
            onRefresh: () async {
              setState(() {
                tasks =
                    capatazApi.pendingTasks().timeout(Duration(seconds: 10));
              });
            },
            child: FutureBuilder(
                future: tasks,
                builder:
                    (BuildContext context, AsyncSnapshot<dynamic> snapshot) {
                  if (snapshot.connectionState == ConnectionState.done) {
                    snapshot.connectionState == ConnectionState.done;
                    List<ListedTaskDTO>? tasksObtenidas = snapshot.data;
                    logger.d(tasksObtenidas);

                    if (!(tasksObtenidas != null &&
                        tasksObtenidas.isNotEmpty)) {
                      return Center(child: Text("Nada que mostrar :("));
                    }

                    return TaskSelector(
                        tasksObtenidas: tasksObtenidas,
                        onUpdate: _actualizarTasks);
                  } else if (snapshot.hasError) {
                    WidgetsBinding.instance.addPostFrameCallback((_) {
                      snackRed(context, 'Error obteniendo las tareas');
                    });
                    return Center(child: Text("Nada que mostrar :("));
                  } else {
                    return Center(child: CircularProgressIndicator());
                  }
                })));
  }
}

class TaskSelector extends StatefulWidget {
  final List<ListedTaskDTO> tasksObtenidas;
  final VoidCallback onUpdate;

  TaskSelector({required this.tasksObtenidas, required this.onUpdate});

  @override
  State<StatefulWidget> createState() => _TaskSelector();
}

class _TaskSelector extends State<TaskSelector> {
  var logger = Logger();
  bool firstBuild = true;
  bool update = false;
  final GlobalKey<FormState> _form = GlobalKey<FormState>();

  TextEditingController zonaController = TextEditingController();
  TextEditingController lineaController = TextEditingController();
  List<ListedTaskDTO> tasksObtenidas = [];
  List<ListedTaskDTO> tasksFiltrados = [];

  // QR Reader
  final _qrBarCodeScannerDialogPlugin = QrBarCodeScannerDialog();
  String? code;

  void filtroPorZonaLinea() {
    logger.d(
        "Filtrar con zona: ${zonaController.text} y linea: ${lineaController.text}");
    setState(() {
      tasksFiltrados = tasksObtenidas
          .where((element) =>
              "${element.zoneName?.toUpperCase()}" // Comparo con la zona
                  .contains(zonaController.text.toUpperCase()) &&
              element.numeroLinea
                  .toString() // Comparo con la linea
                  .contains(lineaController.text.toUpperCase()))
          .toList();
    });
  }

  @override
  Widget build(BuildContext context) {
    if (firstBuild) {
      setState(() {
        tasksObtenidas = widget.tasksObtenidas;
        tasksFiltrados = widget.tasksObtenidas;
        firstBuild = false;
      });
    }

    return Scaffold(
      floatingActionButtonLocation: FloatingActionButtonLocation.endTop,
      floatingActionButton: Form(
        key: _form,
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 0, horizontal: 10),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              Expanded(
                child: Container(
                  margin: EdgeInsets.only(right: 15.0, left: 15.0),
                  child: TextFormField(
                    onChanged: (valor) {
                      filtroPorZonaLinea();
                    },
                    showCursor: true,
                    maxLines: 1,
                    minLines: 1,
                    controller: zonaController,
                    decoration: InputDecoration(
                      border: OutlineInputBorder(),
                      label: Text('Zona:'),
                    ),
                    // maxLength: 3,
                  ),
                ),
              ),
              Expanded(
                child: Container(
                  margin: EdgeInsets.only(right: 25.0, left: 15.0),
                  child: TextFormField(
                    onChanged: (valor) {
                      filtroPorZonaLinea();
                    },
                    showCursor: true,
                    maxLines: 1,
                    minLines: 1,
                    controller: lineaController,
                    keyboardType: TextInputType.number,
                    inputFormatters: <TextInputFormatter>[
                      FilteringTextInputFormatter.digitsOnly
                    ],
                    decoration: InputDecoration(
                      border: OutlineInputBorder(),
                      label: Text('Linea:'),
                    ),
                    // maxLength: 3,
                  ),
                ),
              ),
              Container(
                height: 88,
                margin: EdgeInsets.only(right: 0),
                child: IconButton(
                    onPressed: () {
                      _qrBarCodeScannerDialogPlugin.getScannedQrBarCode(
                          context: context,
                          onCode: (code) {
                            logger.d(code);
                            final zonaLinea = code?.split(",");
                            setState(() {
                              // this.code = code;
                              zonaController.text = zonaLinea![0];
                              lineaController.text = zonaLinea[1];
                              filtroPorZonaLinea();
                            });
                          });
                    },
                    icon: Center(
                        child:
                            Icon(Icons.qr_code_scanner, size: 50, weight: 10))),
              )
            ],
          ),
        ),
      ),
      body: ListView.builder(
          padding: EdgeInsets.only(top: 90),
          scrollDirection: Axis.vertical,
          shrinkWrap: true,
          itemCount: tasksFiltrados.length,
          itemBuilder: (BuildContext context, int index) {
            final task = tasksFiltrados[index];

            return Container(
              color: index % 2 == 0 ? Colors.grey[200] : null,
              child: ListTile(
                onTap: () async {
                  bool actualizar = await Navigator.push(
                      context,
                      MaterialPageRoute(
                          builder: (context) => TaskDetails(
                                taskId: task.idTarea,
                                estado: Estado.sinEmpezar,
                              )));
                  if (actualizar) widget.onUpdate();
                },
                trailing: Icon(Icons.arrow_forward_ios_sharp),
                title: Text("Zona: ${task.zoneName}"),
                subtitle: Text(
                    "Linea: ${task.numeroLinea}  Tipo de Tarea: ${task.tipoTrabajo}"),
              ),
            );
          }),
    );
  }
}

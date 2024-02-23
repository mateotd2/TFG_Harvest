import 'dart:async';

import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/utils/plataform_apis/tractor_api.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../utils/provider/sign_in_model.dart';
import '../../../utils/snack_bars.dart';
import '../capataz_pages/task_details.dart';

class EndedLoadTasks extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _EndedLoadTasks();
}

class _EndedLoadTasks extends State<EndedLoadTasks> {
  var logger = Logger();
  late Future<List<ListedTaskDTO>?> tasks;

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final api = tractorApiPlataform(auth);

    setState(() {
      tasks = api.endedLoadTasks().timeout(Duration(seconds: 10));
    });

    return Scaffold(
      body: RefreshIndicator(
        onRefresh: () async {
          setState(() {
            tasks = api.endedLoadTasks().timeout(Duration(seconds: 10));
          });
        },
        child: FutureBuilder(
            future: tasks,
            builder: (BuildContext context, AsyncSnapshot<dynamic> snapshot) {
              if (snapshot.connectionState == ConnectionState.done) {
                snapshot.connectionState == ConnectionState.done;
                List<ListedTaskDTO>? tasksObtenidas = snapshot.data;
                logger.d(tasksObtenidas);

                List<Widget> pantalla = [];
                List<Widget> filaBotones = [];
                if (!(tasksObtenidas != null && tasksObtenidas.isNotEmpty)) {
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
                            onTap: () async {
                              await Navigator.push(
                                  context,
                                  MaterialPageRoute(
                                      builder: (context) =>
                                          TaskDetails(taskId: task.idTarea)));
                            },
                            trailing: Icon(Icons.arrow_forward_ios_sharp),
                            title: Text("Zona: ${task.zoneName}"),
                            subtitle: Text(
                                "Linea: ${task.numeroLinea}  Tipo de Tarea: ${task.tipoTrabajo}"),
                          ),
                        );
                      },
                    ),
                  ));

                  pantalla.add(Center(
                      child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: filaBotones)));

                  return Column(
                    children: pantalla,
                  );
                }
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

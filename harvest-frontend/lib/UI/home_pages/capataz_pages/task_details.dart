import 'dart:async';

import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../utils/plataform_apis/campanha_api.dart';
import '../../../utils/provider/sign_in_model.dart';
import '../../../utils/snack_bars.dart';

enum Estado { sinEmpezar, enProgreso, finalizada }

class TaskDetails extends StatefulWidget {
  final int? taskId;
  final Estado? estado;

  TaskDetails({required this.taskId, this.estado});

  @override
  State<StatefulWidget> createState() => _TaskDetailsState();
}

class _TaskDetailsState extends State<TaskDetails> {
  var logger = Logger();
  late Future<TaskDTO?> tarea;

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final api = campanhaApiPlataform(auth);
    TaskDTO? tareaObtenida;

    setState(() {
      tarea = api.taskDetails(widget.taskId!).timeout(Duration(seconds: 10));
    });

    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.green,
        title: const Text('Detalles de tarea'),
      ),
      body: RefreshIndicator(
        onRefresh: () async {
          setState(() {
            tarea =
                api.taskDetails(widget.taskId!).timeout(Duration(seconds: 10));
          });
        },
        child: FutureBuilder(
          future: tarea,
          builder: (BuildContext context, AsyncSnapshot<TaskDTO?> snapshot) {
            if (snapshot.connectionState == ConnectionState.done) {
              tareaObtenida = snapshot.data;
              logger.d(tareaObtenida);

              Widget boton;

              switch (widget.estado) {
                case Estado.sinEmpezar:
                  boton = ElevatedButton(
                    onPressed: () async {
                      logger.d("Boton comenzar pulsado");
                      WorkersTractorDTO trabajadores =
                          WorkersTractorDTO(idsWorkers: [], idTractor: null);
                      // TODO: Redirigir a eleccion de trabajadores y llamar al api con los ids de trabajadores
                      try {
                        await api
                            .startTask(tareaObtenida!.idTarea!, trabajadores)
                            .timeout(Duration(seconds: 10));
                        Navigator.pop(context, true);
                        snackGreen(context,
                            "Tarea en ${tareaObtenida?.zoneName} linea:${tareaObtenida?.numeroLinea} iniciada");
                      } on TimeoutException {
                        snackTimeout(context);
                        Navigator.pop(context, false);
                      } catch (e) {
                        snackRed(
                            context, 'Error al intentar iniciar la tarea.');
                        Navigator.pop(context, false);
                      }
                    },
                    child: Text("Comenzar Tarea"),
                  );
                  break;
                case Estado.enProgreso:
                  boton = ElevatedButton(
                    onPressed: () async {
                      logger.d("Boton finalizar pulsado");
                      try {
                        // TODO: Redirigir al formulario para añadir el % finalizado y comentarios
                        StopTaskDTO mockStopTask =
                            StopTaskDTO(comment: "comment", percentaje: 100);
                        await api
                            .stopTask(tareaObtenida!.idTarea!, mockStopTask)
                            .timeout(Duration(seconds: 10));
                        Navigator.pop(context, true);
                        snackGreen(context,
                            "Tarea en ${tareaObtenida?.zoneName} linea:${tareaObtenida?.numeroLinea} finalizada");
                      } on TimeoutException {
                        snackTimeout(context);
                        Navigator.pop(context, false);
                      } catch (e) {
                        snackRed(
                            context, 'Error al intentar finalizar la tarea.');
                        Navigator.pop(context, false);
                      }
                    },
                    child: Text("Finalizar Tarea"),
                  );
                  break;
                default:
                  boton = SizedBox();
                  break;
              }

              return Column(
                children: [
                  ListTile(
                    title: Text("Zona:"),
                    subtitle: Text("${tareaObtenida!.zoneName}"),
                  ),
                  ListTile(
                    title: Text("Linea:"),
                    subtitle: Text("${tareaObtenida!.numeroLinea}"),
                  ),
                  ListTile(
                    title: Text("Tipo de Tarea:"),
                    subtitle: Text("${tareaObtenida!.tipoTarea}"),
                  ),
                  Visibility(
                      visible: tareaObtenida?.horaInicio != null,
                      child: ListTile(
                        title: Text("Hora de Inicio:"),
                        subtitle: Text(
                            "${tareaObtenida?.horaInicio?.substring(0, 8)}"),
                      )),
                  Visibility(
                      visible: tareaObtenida?.horaFinalizacion != null,
                      child: ListTile(
                        title: Text("Hora de Finalizacion:"),
                        subtitle: Text(
                            "${tareaObtenida?.horaFinalizacion?.substring(0, 8)}"),
                      )),
                  Visibility(
                      visible: tareaObtenida?.commentarios != null,
                      child: ListTile(
                        title: Text("Comentarios:"),
                        subtitle: Text("${tareaObtenida?.commentarios}"),
                      )),
                  // TODO: Falta mostrar a los trabajadores asignados a la tarea
                  SizedBox(
                    height: 100,
                  ),
                  Center(
                    child: boton,
                  )
                ],
              );
            } else if (snapshot.hasError) {
              snackRed(context, 'Error obteniendo la tarea');
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

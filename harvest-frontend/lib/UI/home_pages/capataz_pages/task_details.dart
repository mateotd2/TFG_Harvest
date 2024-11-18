import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/utils/plataform_apis/capataz_api.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../utils/plataform_apis/campanha_api.dart';
import '../../../utils/plataform_apis/workers_api.dart';
import '../../../utils/provider/sign_in_model.dart';
import '../../../utils/snack_bars.dart';
import 'worker_selector.dart';

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
    final apiCampanha = campanhaApiPlataform(auth);
    final apiTrabajadores = trabajadoresApiPlataform(auth);
    final apiCapataz = capatazApiPlataform(auth);

    TaskDTO? tareaObtenida;

    setState(() {
      tarea = apiCampanha
          .taskDetails(widget.taskId!)
          .timeout(Duration(seconds: 10));
    });

    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.green,
        title: const Text('Detalles de tarea'),
      ),
      body: RefreshIndicator(
        onRefresh: () async {
          setState(() {
            tarea = apiCampanha
                .taskDetails(widget.taskId!)
                .timeout(Duration(seconds: 10));
          });
        },
        child: SingleChildScrollView(
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
                        List<WorkerDTO>? trabajadoresDisponibles =
                            await apiTrabajadores.getAvailableWorkers();
                        List<int> idsTrabajadores = await Navigator.push(
                            context,
                            MaterialPageRoute(
                                builder: (context) => WorkersSelector(
                                    workers: trabajadoresDisponibles)));
                        WorkersDTO trabajadores =
                            WorkersDTO(idsWorkers: idsTrabajadores);

                        try {
                          await apiCapataz
                              .startTask(tareaObtenida!.idTarea!, trabajadores)
                              .timeout(Duration(seconds: 10));

                          snackGreen(context,
                              "Tarea en ${tareaObtenida?.zoneName} linea:${tareaObtenida?.numeroLinea} iniciada");
                          Navigator.pop(context, true);
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
                        PhaseCampaign? fase = await apiCampanha
                            .getPhaseCampaign()
                            .timeout(Duration(seconds: 10));

                        await showDialog(
                            context: context,
                            builder: (BuildContext context) {
                              return FinalizarTareaForm(
                                  phase: fase!,
                                  taskId: tareaObtenida!.idTarea!);
                            });
                      },
                      child: Text("Finalizar Tarea"),
                    );
                    break;
                  default:
                    boton = SizedBox();
                    break;
                }
                String trabajadores = "";
                bool hayTrabajadores = false;
                if (tareaObtenida!.workers.isNotEmpty) {
                  trabajadores = "";
                  hayTrabajadores = true;
                  tareaObtenida?.workers.forEach((element) {
                    trabajadores =
                        ("$trabajadores ${element.name}  ${element.lastname} \n");
                  });
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
                    Visibility(
                      visible: hayTrabajadores,
                      child: ListTile(
                          title: Text("Trabajadores asignados:"),
                          subtitle: Text(trabajadores)),
                    ),
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
      ),
    );
  }
}

class FinalizarTareaForm extends StatefulWidget {
  final int taskId;
  final PhaseCampaign? phase;

  FinalizarTareaForm({required this.taskId, this.phase});

  @override
  State<StatefulWidget> createState() => _FinalizarTareaForm();
}

class _FinalizarTareaForm extends State<FinalizarTareaForm> {
  final GlobalKey<FormState> _form = GlobalKey<FormState>();
  bool solicitarCarga = false;

  TextEditingController comentarioController = TextEditingController();
  TextEditingController porcentajeController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final apiCapataz = capatazApiPlataform(auth);

    var logger = Logger();

    // porcentajeController.text="0";

    return SingleChildScrollView(
      child: AlertDialog(
        title: Text("Finalizar Tarea"),
        actions: [
          ElevatedButton(
              onPressed: () {
                logger.d('Finalizacion tarea cancelada');
                Navigator.of(context).pop();
              },
              child: Text('Cancelar')),
          ElevatedButton(
              onPressed: () async {
                if (_form.currentState!.validate()) {
                  _form.currentState?.save();
                  logger.d('Finalizacion de tarea');
                  StopTaskDTO stopTaskDto = StopTaskDTO(
                      load: solicitarCarga,
                      comment: comentarioController.text,
                      percentaje: int.parse(
                        porcentajeController.text,
                      ));
                  logger.d(stopTaskDto);
                  try {
                    await apiCapataz
                        .stopTask(widget.taskId, stopTaskDto)
                        .timeout(Duration(seconds: 10));
                    snackGreen(context, "Tarea finalizada");
                    Navigator.of(context).pop();
                    Navigator.of(context).pop(true);
                  } on TimeoutException {
                    snackTimeout(context);
                    Navigator.of(context).pop();
                  } catch (e) {
                    snackRed(context, "No se pudo finalizar la tarea");
                    Navigator.of(context).pop();
                  }
                }
              },
              child: Text("Aceptar"))
        ],
        content: Form(
          key: _form,
          child: Column(
            children: [
              TextField(
                scrollPadding: EdgeInsets.all(100),
                showCursor: true,
                maxLines: 7,
                minLines: 7,
                controller: comentarioController,
                decoration: InputDecoration(
                  border: OutlineInputBorder(
                    borderSide: BorderSide(color: Colors.blue, width: 2.0),
                  ),
                  label: Text('Comentarios de trabajo:'),
                ),
                autofocus: true,
                key: Key('commentarios'),
                keyboardType: TextInputType.multiline,
                maxLength: 2096,
              ),
              TextFormField(
                controller: porcentajeController,
                decoration: InputDecoration(
                    label: Text('Porcentaje de trabajo realizado:')),
                key: Key('porcentaje'),
                maxLength: 3,
                keyboardType: TextInputType.number,
                inputFormatters: <TextInputFormatter>[
                  FilteringTextInputFormatter.digitsOnly
                ],
                validator: (valor) {
                  if (valor!.isEmpty) {
                    return 'Ingresar porcentaje completado en la linea ';
                  }
                  valor = valor.trim();
                  if (valor.length > 6 ||
                      (int.tryParse(porcentajeController.text)! <= 0) ||
                      (int.tryParse(porcentajeController.text)! > 100)) {
                    return 'Ingresar porcentaje valido';
                  }
                  return null;
                },
              ),
              Visibility(
                visible: widget.phase == PhaseCampaign.RECOLECCION_CARGA,
                child: CheckboxListTile(
                  title: Text("¿Solicitar Carga?"),
                  value: solicitarCarga,
                  onChanged: (bool? value) {
                    setState(() {
                      solicitarCarga = value!;
                    });
                  },
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

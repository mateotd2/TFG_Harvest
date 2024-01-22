import 'dart:async';
import 'dart:core';

import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/utils/snack_bars.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';
import 'package:time_picker_spinner/time_picker_spinner.dart';

import '../../../../utils/plataform_apis/workers_api.dart';
import '../../../../utils/provider/sign_in_model.dart';

class PasarLista extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _PasarLista();
}

class _PasarLista extends State<PasarLista> {
  var logger = Logger();
  late Future<List<AttendanceDTO>?>
      asistencias; // Se carga con la respuesta de la llamada API de las asistencias registradas en el dia actual
  List<CallDTO> listaAsistencias =
      []; // Se inicializa con las asistencias del dia actual, pero solo se podran actualizar si se realizan cambios
  bool asistenciasNuevas =
      true; // Para que no estar añadiendo a la lista CallDTOs cada vez que carga la pantalla
  bool showButtonUpdate =
      false; // Controla si hay modificaciones para actualizar

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final apiInstance = trabajadoresApiPlataform(auth);

    setState(() {
      asistencias = apiInstance.getAttendances().timeout(Duration(seconds: 10));
    });

    return Scaffold(
      body: RefreshIndicator(
        onRefresh: () async {
          setState(() {
            asistenciasNuevas = true;
            asistencias =
                apiInstance.getAttendances().timeout(Duration(seconds: 10));
            listaAsistencias.clear();
            showButtonUpdate = false;
          });
        },
        child: FutureBuilder(
            future: asistencias,
            builder: (BuildContext context,
                AsyncSnapshot<List<AttendanceDTO>?> snapshot) {
              if (snapshot.hasError) {
                return Center(
                    child: Text("Comunicación con el servidor fallida "));
              }
              if (snapshot.connectionState == ConnectionState.done) {
                List<AttendanceDTO>? asistenciasObtenidas = snapshot.data;

                if (asistenciasNuevas) {
                  asistenciasObtenidas?.forEach((element) {
                    listaAsistencias.add(CallDTO(
                        id: element.id,
                        checkin: element.checkin,
                        checkout: element.checkout,
                        attendance: element.attendance));
                  });
                }
                logger.d(asistenciasObtenidas);
                logger.d(listaAsistencias);
                if (asistenciasObtenidas == null ||
                    asistenciasObtenidas.isEmpty) {
                  return Center(
                      child: Text("Hoy no hay programada ninguna asistencia"));
                } else {
                  return Column(
                    children: [
                      ListTile(
                          title: Text("Trabajador",
                              style: TextStyle(fontSize: 20)),
                          subtitle: Text("Checkin         -   Checkout:",
                              style: TextStyle(fontSize: 23)),
                          trailing: Text("Asistencia",
                              style: TextStyle(fontSize: 16))),
                      Expanded(
                        child: ListView.builder(
                            itemCount: asistenciasObtenidas.length,
                            itemBuilder: (context, index) {
                              final asistencia = asistenciasObtenidas[index];
                              return ListTile(
                                // contentPadding: EdgeInsets.symmetric(vertical: 1),
                                title: Text(
                                    "${asistencia.name} ${asistencia.lastname}",
                                    style: TextStyle(fontSize: 20)),
                                subtitle: Row(
                                  children: [
                                    Text(listaAsistencias[index].checkin,
                                        style: TextStyle(fontSize: 23)),
                                    IconButton.outlined(
                                        onPressed: () async {
                                          logger.d("Reloj CHECKIN Pulsado");
                                          final String initTime =
                                              "${DateTime.now().year}-${DateTime.now().month}-${DateTime.now().day} ${listaAsistencias[index].checkin}.000000";
                                          await _showTimePickerDialog(context,
                                              initTime, "entrada", index);
                                          logger.d(
                                              "Hora Seleccionada: ${listaAsistencias[index].checkin}");
                                        },
                                        icon: Icon(
                                          Icons.access_time_filled_sharp,
                                          size: 30,
                                          color: Colors.lightBlue,
                                        )),
                                    Text(
                                        "- ${listaAsistencias[index].checkout}",
                                        style: TextStyle(fontSize: 23)),
                                    IconButton.outlined(
                                        onPressed: () async {
                                          logger.d("Reloj CHECKIN Pulsado");
                                          final String initTime =
                                              "${DateTime.now().year}-${DateTime.now().month}-${DateTime.now().day} ${listaAsistencias[index].checkout}.000000";
                                          await _showTimePickerDialog(context,
                                              initTime, "salida", index);
                                          logger.d(
                                              "Hora Seleccionada: ${listaAsistencias[index].checkout}");
                                        },
                                        icon: Icon(
                                          Icons.access_time_filled_sharp,
                                          size: 30,
                                          color: Colors.lightBlue,
                                        ))
                                  ],
                                ),
                                trailing: Column(
                                  children: [
                                    Checkbox(
                                        value:
                                            listaAsistencias[index].attendance,
                                        onChanged: (bool? value) async {
                                          await _cambiarCheckBox(
                                              context, index);
                                        }),
                                  ],
                                ),
                              );
                            }),
                      ),
                      Visibility(
                        visible: showButtonUpdate,
                        child: ElevatedButton(
                          onPressed: () async {
                            logger.d("Actualizando asistencias");
                            try {
                              await apiInstance
                                  .callRoll(listaAsistencias)
                                  .timeout(Duration(seconds: 10));
                              snackGreen(context, 'Asistencias Actualizadas');
                              setState(() {
                                showButtonUpdate = false;
                              });
                            } on TimeoutException {
                              snackTimeout(context);
                            } catch (e) {
                              snackRed(context,
                                  'Error al actualizar las asistencias.');
                              // Navigator.pop(context);
                            }
                            // Navigator.pop(context);
                          },
                          child: Text("Actualizar"),
                        ),
                      )
                    ],
                  );
                }
              } else {
                return Center(child: CircularProgressIndicator());
              }
            }),
      ),
    );
  }

  Future<void> _cambiarCheckBox(BuildContext context, int index) async {
    setState(() {
      listaAsistencias[index].attendance = !listaAsistencias[index].attendance;
      asistenciasNuevas = false;
      showButtonUpdate = true;
    });
  }

  Future<void> _showTimePickerDialog(
      BuildContext context, String hora, String tipoEntrada, int index) async {
    logger.d("Hora actual: ${DateTime.now()}");
    logger.d("Se intenta parsear la hora $hora");
    DateTime? nuevaHora = DateTime.parse(hora);
    logger.d("Nueva HORA $nuevaHora");
    TimeOfDay horaInicial =
        TimeOfDay(hour: nuevaHora.hour, minute: nuevaHora.minute);
    logger.d("Hora: $hora");
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('Selecciona la hora de $tipoEntrada:'),
          content: TimePickerSpinner(
            time: nuevaHora,
            is24HourMode: true,
            spacing: 50,
            itemHeight: 80,
            isForce2Digits: true,
            onTimeChange: (time) {
              nuevaHora = time;
            },
          ),
          actions: <Widget>[
            TextButton(
              onPressed: () {
                Navigator.of(context).pop();
              },
              child: Text('Cancelar'),
            ),
            TextButton(
              onPressed: () {
                if (horaInicial.minute != nuevaHora?.minute ||
                    horaInicial.hour != nuevaHora?.hour) {
                  setState(() {
                    asistenciasNuevas = false;

                    switch (tipoEntrada) {
                      case "entrada":
                        DateTime salida = DateTime.parse(
                            "${DateTime.now().year}-${DateTime.now().month}-${DateTime.now().day} ${listaAsistencias[index].checkout}.000000");
                        logger.d("Salida: $salida");
                        if (nuevaHora!.isBefore(salida)) {
                          listaAsistencias[index] = CallDTO(
                              id: listaAsistencias[index].id,
                              checkin:
                                  "${nuevaHora?.hour.toString().padLeft(2, '0')}:${nuevaHora?.minute.toString().padLeft(2, '0')}:00",
                              checkout: listaAsistencias[index].checkout,
                              attendance: listaAsistencias[index].attendance);
                          showButtonUpdate = true;
                        } else {
                          snackRed(context, 'Hora no valida');
                        }

                      case "salida":
                        DateTime? entrada = DateTime.tryParse(
                            "${DateTime.now().year}-${DateTime.now().month}-${DateTime.now().day} ${listaAsistencias[index].checkin}.000000");
                        logger.d("Entrada: $entrada");
                        if (!nuevaHora!.isBefore(entrada!)) {
                          listaAsistencias[index] = CallDTO(
                              id: listaAsistencias[index].id,
                              checkin: listaAsistencias[index].checkin,
                              checkout:
                                  "${nuevaHora?.hour.toString().padLeft(2, '0')}:${nuevaHora?.minute.toString().padLeft(2, '0')}:00",
                              attendance: listaAsistencias[index].attendance);
                          showButtonUpdate = true;
                        } else {
                          snackRed(context, 'Hora no valida');
                        }
                    }
                  });
                }
                Navigator.of(context).pop();
              },
              child: Text('Aceptar'),
            ),
          ],
        );
      },
    );
  }
}

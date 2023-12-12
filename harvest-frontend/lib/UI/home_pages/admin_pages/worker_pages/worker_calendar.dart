import 'dart:async';

import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/utils/snack_bars.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';
import 'package:time_picker_spinner/time_picker_spinner.dart';

import '../../../../utils/plataform_apis/workers_api.dart';
import '../../../../utils/provider/sign_in_model.dart';
import 'day_of_work_form.dart';

class WorkerCalendar extends StatefulWidget {
  late final int? workerId;

  WorkerCalendar({required this.workerId});

  @override
  State<StatefulWidget> createState() => WorkerCalendarState();
}

class WorkerCalendarState extends State<WorkerCalendar> {
  late Future<List<CalendarDTO>?>
      calendar; // Se carga con la respuesta al API del calendario del dia actual
  List<CalendarDTO> nuevoCalendario =
      []; //  Lista con el calendario inicial y las modificaciones realizadas
  var logger = Logger();

  // bool actualizar = false;
  bool showButtonUpdate = false;
  bool calendarioNuevo =
      true; // Para no estar añadiendo a la lista CallDTOs cada vez que carga la pantalla

  // Future<List<CalendarDTO>?> actualizarCalendario(TrabajadoresApi apiInstance, int workerId) async {
  //   return await apiInstance.getCalendar(workerId!).timeout(Duration(seconds: 10));
  // }

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final apiInstance = trabajadoresApiPlataform(auth);
    int? workerId = widget.workerId;
    DateTime hoy = DateTime.now();
    setState(() {
      calendar =
          apiInstance.getCalendar(workerId!).timeout(Duration(seconds: 10));
    });

    return Scaffold(
        floatingActionButton: Visibility(
          visible: !showButtonUpdate,
          child: FloatingActionButton(
            onPressed: () async {
              // Mostrar el diálogo y esperar que se complete
              await showDialog(
                context: context,
                builder: (BuildContext context) {
                  return DayOfWorkForm();
                },
              );

              setState(() {
                calendarioNuevo = true;
                calendar = apiInstance
                    .getCalendar(workerId!)
                    .timeout(Duration(seconds: 10));
                nuevoCalendario.clear();
                showButtonUpdate = false;
              });
            },
            key: Key('addEmpKey'),
            child: Icon(Icons.add),
          ),
        ),
        appBar: AppBar(
          title: const Text('Calendario'),
        ),
        body: RefreshIndicator(
          onRefresh: () async {
            setState(() {
              calendarioNuevo = true;
              calendar = apiInstance
                  .getCalendar(workerId!)
                  .timeout(Duration(seconds: 10));
              nuevoCalendario.clear();
              showButtonUpdate = false;
            });
          },
          child: FutureBuilder(
            future: calendar,
            // future: actualizarCalendario(apiInstance,workerId!),
            builder: (BuildContext context,
                AsyncSnapshot<List<CalendarDTO>?> snapshot) {
              if (snapshot.hasError) {
                return Center(
                    child: Text("Comunicación con el servidor fallida "));
              }
              if (snapshot.connectionState == ConnectionState.done) {
                List<CalendarDTO>? calendarioObtenido = snapshot.data;

                if (calendarioNuevo) {
                  calendarioObtenido?.forEach((element) {
                    nuevoCalendario.add(CalendarDTO(
                        checkin: element.checkin,
                        checkout: element.checkout,
                        day: element.day,
                        attendance: element.attendance,
                        id: element.id));
                  });
                }
                nuevoCalendario
                    .sort((fecha1, fecha2) => fecha2.day.compareTo(fecha1.day));
                logger.d(nuevoCalendario);

                if (calendarioObtenido == null || calendarioObtenido.isEmpty) {
                  return Center(child: Text("Nada en el calendario :( "));
                } else {
                  return Column(
                    children: [
                      Expanded(
                        child: ListView.builder(
                          itemCount: nuevoCalendario.length + 1,
                          itemBuilder: (context, int index) {
                            if (index == 0) {
                              return Container(
                                color: Colors.grey[300],
                                child: Row(
                                  mainAxisAlignment: MainAxisAlignment.start,
                                  crossAxisAlignment: CrossAxisAlignment.center,
                                  children: [
                                    // Container(child: Text('Dia',style: TextStyle(fontSize: 30),)),
                                    Container(
                                        width: 180,
                                        // 80% del ancho de la pantalla
                                        height: 30,
                                        // 50% de la altura de la pantalla
                                        child: Text('   Entrada  ',
                                            style: TextStyle(fontSize: 30))),
                                    Container(
                                        child: Text('   Salida ',
                                            style: TextStyle(fontSize: 30))),
                                  ],
                                ),
                              );
                            } else {
                              final CalendarDTO fila =
                                  nuevoCalendario[index - 1];
                              bool visibilidadBotones = fila.day.isAfter(DateTime(
                                  hoy.year,
                                  hoy.month,
                                  hoy.day)); //Solo se pueden modificar fechas siguientes
                              return Container(
                                color: index % 2 == 0 ? Colors.grey[200] : null,
                                // Cambia el color de fondo intercalando
                                child: Column(
                                  children: [
                                    Text(
                                      "${fila.day.year}-${fila.day.month}-${fila.day.day}",
                                      style: TextStyle(fontSize: 15),
                                    ),
                                    Row(
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceEvenly,
                                      children: [
                                        Text(
                                          fila.checkin,
                                          style: TextStyle(fontSize: 20),
                                        ),
                                        Visibility(
                                          visible: visibilidadBotones,
                                          replacement: Icon(
                                            Icons.access_time_filled_sharp,
                                            size: 30,
                                            color: Colors.grey,
                                          ),
                                          child: IconButton.outlined(
                                              onPressed: () async {
                                                logger
                                                    .d("Reloj CHECKIN Pulsado");
                                                final String initTime =
                                                    "2000-10-10 ${fila.checkin}";
                                                await _showTimePickerDialog(
                                                    context,
                                                    initTime,
                                                    "entrada",
                                                    index - 1);
                                                logger.d(
                                                    "Hora Seleccionada: ${fila.checkin}");
                                              },
                                              icon: Icon(
                                                Icons.access_time_filled_sharp,
                                                size: 30,
                                                color: Colors.lightBlue,
                                              )),
                                        ),
                                        VerticalDivider(
                                          width: 5,
                                          thickness: 1,
                                          indent: 1,
                                          endIndent: 0,
                                          color: Colors.grey,
                                        ),
                                        Text(
                                          fila.checkout,
                                          style: TextStyle(fontSize: 20),
                                        ),
                                        Visibility(
                                          visible: visibilidadBotones,
                                          replacement: Icon(
                                            Icons.access_time_filled_sharp,
                                            size: 30,
                                            color: Colors.grey,
                                          ),
                                          child: IconButton.outlined(
                                              onPressed: () async {
                                                logger.d(
                                                    "Reloj CHECKOUT Pulsado");
                                                final String initTime =
                                                    "2000-10-10 ${fila.checkout}";
                                                await _showTimePickerDialog(
                                                    context,
                                                    initTime,
                                                    "salida",
                                                    index - 1);
                                                logger.d(
                                                    "Hora Seleccionada: ${fila.checkout}");
                                              },
                                              icon: Icon(
                                                Icons.access_time_filled_sharp,
                                                size: 30,
                                                color: Colors.lightBlue,
                                              )),
                                        ),
                                        Visibility(
                                          visible: visibilidadBotones,
                                          replacement: Icon(
                                            Icons.delete,
                                            size: 30,
                                            color: Colors.grey,
                                          ),
                                          child: IconButton.outlined(
                                              onPressed: () async {
                                                logger.d("Delete pulsado");
                                                bool res = await showDialog(
                                                    context: context,
                                                    builder: (context) {
                                                      return AlertDialog(
                                                        title: Text(
                                                            'Confirmación'),
                                                        content: Text(
                                                            '¿Desea eliminar la fecha?'),
                                                        actions: [
                                                          TextButton(
                                                            onPressed: () {
                                                              Navigator.of(
                                                                      context)
                                                                  .pop(false);
                                                            },
                                                            child: Text(
                                                                'Cancelar'),
                                                          ),
                                                          TextButton(
                                                            onPressed: () {
                                                              print(
                                                                  'Acción confirmada');
                                                              Navigator.of(
                                                                      context)
                                                                  .pop(
                                                                      true); // Cerrar el diálogo
                                                            },
                                                            child:
                                                                Text('Aceptar'),
                                                          ),
                                                        ],
                                                      );
                                                    });
                                                if (res == true) {
                                                  try {
                                                    apiInstance
                                                        .deleteDayOfWork(
                                                            workerId!, fila.id!)
                                                        .timeout(Duration(
                                                            seconds: 10));
                                                    setState(() {
                                                      calendarioNuevo = true;
                                                      calendar = apiInstance
                                                          .getCalendar(workerId)
                                                          .timeout(Duration(
                                                              seconds: 10));
                                                      nuevoCalendario.clear();
                                                      showButtonUpdate = false;
                                                    });
                                                    snackGreen(context,
                                                        'Fecha borrada');
                                                  } on TimeoutException {
                                                    snackTimeout(context);
                                                  } catch (e) {
                                                    snackRed(context,
                                                        'Error al borrar la fecha.');
                                                  }
                                                }
                                              },
                                              icon: Icon(
                                                Icons.delete,
                                                size: 30,
                                                color: Colors.red,
                                              )),
                                        )
                                      ],
                                    ),
                                  ],
                                ),
                              );
                            }
                          },
                        ),
                      ),
                      Visibility(
                        visible: showButtonUpdate,
                        child: ElevatedButton(
                          onPressed: () async {
                            logger.d("Actualizando asistencias");
                            try {
                              await apiInstance
                                  .updateCalendar(workerId!, nuevoCalendario)
                                  .timeout(Duration(seconds: 10));
                              snackGreen(context, 'Calendario Actualizadas');
                              setState(() {
                                showButtonUpdate = false;
                              });
                            } on TimeoutException {
                              snackTimeout(context);
                            } catch (e) {
                              snackRed(context,
                                  'Error al actualizar el calendario.');
                            }
                          },
                          child: Text("Actualizar"),
                        ),
                      ),
                    ],
                  );
                }
              } else {
                return Center(child: CircularProgressIndicator());
              }
            },
          ),
        ));
  }

  Future<void> _showTimePickerDialog(
      BuildContext context, String hora, String tipoEntrada, int index) async {
    logger.d("Hora actual: $hora");
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
                    calendarioNuevo = false;
                    switch (tipoEntrada) {
                      case "entrada":
                        DateTime salida = DateTime.parse(
                            "2000-10-10 ${nuevoCalendario[index].checkout}");
                        logger.d("Salida: $salida");
                        if (nuevaHora!.isBefore(salida)) {
                          nuevoCalendario[index] = CalendarDTO(
                              checkin:
                                  "${nuevaHora?.hour.toString().padLeft(2, '0')}:${nuevaHora?.minute.toString().padLeft(2, '0')}:00",
                              checkout: nuevoCalendario[index].checkout,
                              day: nuevoCalendario[index].day,
                              attendance: nuevoCalendario[index].attendance,
                              id: nuevoCalendario[index].id);
                          showButtonUpdate = true;
                        } else {
                          snackRed(context, 'Hora no valida');
                        }

                      case "salida":
                        DateTime? entrada = DateTime.parse(
                            "2000-10-10 ${nuevoCalendario[index].checkin}");
                        logger.d("Entrada: $entrada");
                        if (!nuevaHora!.isBefore(entrada)) {
                          nuevoCalendario[index] = CalendarDTO(
                              attendance: nuevoCalendario[index].attendance,
                              checkin: nuevoCalendario[index].checkin,
                              checkout:
                                  "${nuevaHora?.hour.toString().padLeft(2, '0')}:${nuevaHora?.minute.toString().padLeft(2, '0')}:00",
                              day: nuevoCalendario[index].day,
                              id: nuevoCalendario[index].id);
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

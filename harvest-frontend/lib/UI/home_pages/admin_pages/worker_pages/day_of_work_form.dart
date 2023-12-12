import 'dart:async';

import 'package:date_time_picker/date_time_picker.dart';
import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/utils/snack_bars.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../../utils/plataform_apis/workers_api.dart';
import '../../../../utils/provider/sign_in_model.dart';

class DayOfWorkForm extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => DayOfWorkState();
}

class DayOfWorkState extends State<DayOfWorkForm> {
  var logger = Logger();
  DateTime _fechaTrabajo = DateTime.now();
  late String _horaCheckIn = '08:00';
  late String _horaCheckOut = '14:00';

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final apiInstance = trabajadoresApiPlataform(auth);

    return AlertDialog(
      title: Text('Nuevo dia de trabajo'),
      content: Column(
        children: [
          TextFormField(
            key: Key('dateKey'),
            readOnly: true,
            decoration: InputDecoration(
                labelText: 'Fecha de trabajo',
                suffixIcon: IconButton(
                  icon: Icon(Icons.calendar_month),
                  onPressed: () async {
                    final DateTime? seleccion = await showDatePicker(
                        context: context,
                        initialDate: _fechaTrabajo,
                        firstDate: DateTime(DateTime.now().year),
                        lastDate: DateTime(DateTime.now().year + 3));
                    if (seleccion != null) {
                      setState(() {
                        _fechaTrabajo = seleccion;
                      });
                    }
                  },
                )),
            controller: TextEditingController(
                text: "${_fechaTrabajo.toLocal()}".substring(0, 10)),
          ),
          DateTimePicker(
            type: DateTimePickerType.time,
            decoration: const InputDecoration(
              icon: Icon(Icons.watch_later_outlined),
              hintText: '08:00',
              labelText: 'Hora de entrada',
            ),
            initialValue: _horaCheckIn,
            firstDate: DateTime.now(),
            lastDate: DateTime.now().add(Duration(days: 365)),
            dateLabelText: 'Date',
            onSaved: (val) => {
              setState(() {
                _horaCheckIn = val!;
              })
            },
          ),
          DateTimePicker(
            type: DateTimePickerType.time,
            decoration: const InputDecoration(
              icon: Icon(Icons.watch_later_outlined),
              hintText: '14:00',
              labelText: 'Hora de Salida',
            ),
            initialValue: _horaCheckOut,
            firstDate: DateTime.now(),
            lastDate: DateTime.now().add(Duration(days: 365)),
            dateLabelText: 'Date',
            onSaved: (val) => {
              setState(() {
                _horaCheckOut = val!;
              })
            },
          )
        ],
      ),
      actions: [
        ElevatedButton(
          onPressed: () {
            Navigator.pop(context);
          },
          child: Text('Cancelar'),
        ),
        ElevatedButton(
          onPressed: () async {
            final tomorrow = DateTime(DateTime.now().year, DateTime.now().month,
                DateTime.now().day + 1, 0, 0, 0, 0, 0);
            if (_fechaTrabajo.isAfter(tomorrow) ||
                _fechaTrabajo.isAtSameMomentAs(tomorrow)) {
              try {
                CalendarDTO nuevaFecha = CalendarDTO(
                    checkin: _horaCheckIn,
                    checkout: _horaCheckOut,
                    day: _fechaTrabajo,
                    attendance: false);
                logger.d("Nueva fecha: ${_fechaTrabajo.toLocal()}");
                logger.d("Se registra $nuevaFecha");

                await apiInstance
                    .addDayOfWork(estado.lastResponse!.id, nuevaFecha)
                    .timeout(Duration(seconds: 10));

                snackGreen(context, 'Calendario Actualizadas');
              } on TimeoutException {
                snackTimeout(context);
              } catch (e) {
                snackRed(context, 'Error al añadir fecha al calendario.');
              }
            } else {
              snackRed(context, 'Fecha no valida');
            }
            Navigator.pop(context);
          },
          child: Text('Aceptar'),
        ),
      ],
    );
  }
}

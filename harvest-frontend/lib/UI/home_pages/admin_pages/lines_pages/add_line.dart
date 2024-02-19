import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/utils/snack_bars.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../../utils/icons_util.dart';
import '../../../../utils/plataform_apis/lines_api.dart';
import '../../../../utils/provider/sign_in_model.dart';

class AddLine extends StatefulWidget {
  final int? zoneId;

  AddLine({required this.zoneId});

  @override
  State<StatefulWidget> createState() => _AddLineState();
}

class _AddLineState extends State<AddLine> {
  var logger = Logger();
  final _form = GlobalKey<FormState>();

  DateTime _fechaPlantacion = DateTime.now();

  final TextEditingController _lineNumberController = TextEditingController();
  final TextEditingController _distanceController = TextEditingController();

  late Future<List<TypeVidDTO>?> opciones;

  bool habilitado = true;

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final api = lineasApiPlataform(auth);

    late TypeVidDTO? opcion;

    setState(() {
      opciones = api.getVids().timeout(Duration(seconds: 10));
    });

    return Scaffold(
      appBar: AppBar(
        title: const Text('Añadir Linea'),
        backgroundColor: Colors.green,
      ),
      body: FutureBuilder<List<TypeVidDTO>?>(
          future: opciones,
          builder: (BuildContext context,
              AsyncSnapshot<List<TypeVidDTO>?> snapshot) {
            if (snapshot.connectionState == ConnectionState.done) {
              List<TypeVidDTO>? vidsObtenidas = snapshot.data;
              logger.d(vidsObtenidas);
              opcion = vidsObtenidas![0];
              return SingleChildScrollView(
                child: Form(
                  key: _form,
                  child: Column(
                    children: [
                      TextFormField(
                        maxLength: 4,
                        key: Key('lineNumberKey'),
                        controller: _lineNumberController,
                        keyboardType: TextInputType.number,
                        inputFormatters: <TextInputFormatter>[
                          FilteringTextInputFormatter.digitsOnly
                        ],
                        decoration:
                            InputDecoration(labelText: 'Numero de linea:'),
                        validator: (valor) {
                          if (valor!.isEmpty) {
                            return 'Ingresar numero ';
                          }
                          valor = valor.trim();
                          if (valor.length > 6 ||
                              (int.tryParse(_lineNumberController.text)! <=
                                  0)) {
                            return 'Ingresar numero valido';
                          }
                          return null;
                        },
                      ),
                      TextFormField(
                        maxLength: 5,
                        key: Key('distanceKey'),
                        controller: _distanceController,
                        keyboardType: TextInputType.number,
                        inputFormatters: <TextInputFormatter>[
                          FilteringTextInputFormatter.digitsOnly
                        ],
                        decoration: InputDecoration(labelText: 'Distancia'),
                        validator: (valor) {
                          if (valor!.isEmpty) {
                            return 'Ingresar numero ';
                          }
                          valor = valor.trim();
                          if (valor.length > 6 ||
                              (int.tryParse(_lineNumberController.text)! <=
                                  0)) {
                            return 'Ingresar distancia valida';
                          }
                          return null;
                        },
                      ),
                      DropdownButtonFormField<TypeVidDTO>(
                        value: opcion,
                        items: vidsObtenidas.map((value) {
                          return DropdownMenuItem<TypeVidDTO>(
                            value: value,
                            child: Text(value.name!),
                          );
                        }).toList(),
                        onChanged: (TypeVidDTO? value) {
                          logger.d('Elegido elemento $value');

                          opcion = value!;
                        },
                      ),
                      StatefulBuilder(builder: (context, setState) {
                        return TextFormField(
                          key: Key('dateKey'),
                          readOnly: true,
                          decoration: InputDecoration(
                              labelText: 'Fecha de plantación',
                              suffixIcon: IconButton(
                                icon: Icon(Icons.calendar_month),
                                onPressed: () async {
                                  final DateTime? seleccion =
                                      await showDatePicker(
                                          context: context,
                                          initialDate: _fechaPlantacion,
                                          firstDate: DateTime(1940),
                                          lastDate: DateTime(2050));
                                  if (seleccion != null) {
                                    setState(() {
                                      logger.d(
                                          'Nuevo valor de fecha de nacimiento');
                                      _fechaPlantacion = seleccion;
                                    });
                                  }
                                },
                              )),
                          controller: TextEditingController(
                              text: "${_fechaPlantacion.toLocal()}"
                                  .substring(0, 10)),
                        );
                      }),
                      Row(
                        children: [
                          Text("Habilitar linea para recoleccion?"),
                          Switch(
                            thumbIcon: thumbIcon,
                            value: habilitado,
                            onChanged: (bool value) {
                              setState(() {
                                habilitado = value;
                              });
                            },
                          ),
                        ],
                      ),
                      ElevatedButton(
                          key: Key('okButtonKey'),
                          onPressed: () async {
                            if (_form.currentState!.validate()) {
                              _form.currentState!.save();
                              logger.d('Boton de añadir linea pulsado');

                              LineDTO nuevaLinea = LineDTO(
                                harvestEnabled: habilitado,
                                  lineNumber:
                                      int.parse(_lineNumberController.text),
                                  distance: int.parse(_distanceController.text),
                                  plantingDate: _fechaPlantacion,
                                  idTypeVid: opcion!.id!);

                              logger.d(nuevaLinea);
                              try {
                                MessageResponseDTO? response = await api
                                    .addLine(widget.zoneId!, nuevaLinea)
                                    .timeout(Duration(seconds: 10));
                                logger.d('Respuesta:');
                                logger.d(response);
                                snackGreen(
                                    context, 'Zona registrada correctamente.');
                              } on TimeoutException {
                                snackTimeout(context);
                              } catch (e) {
                                snackRed(context,
                                    'Error al dar intentar registrar la zona.');
                              }
                            }
                            Navigator.pop(context);
                          },
                          child: Text('Registrar Zona')),
                    ],
                  ),
                ),
              );
            } else {
              return Center(child: CircularProgressIndicator());
            }
          }),
    );
  }
}

import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/utils/snack_bars.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../../utils/plataform_apis/lines_api.dart';
import '../../../../utils/provider/sign_in_model.dart';

class UpdateLine extends StatefulWidget {
  final int? lineId;
  final LineDetailsDTO line;

  UpdateLine({required this.lineId, required this.line});

  @override
  State<StatefulWidget> createState() => _UpdateLineState();
}

class _UpdateLineState extends State<UpdateLine> {
  var logger = Logger();
  final _form = GlobalKey<FormState>();

  final TextEditingController _lineNumberController = TextEditingController();
  final TextEditingController _distanceController = TextEditingController();

  late Future<List<TypeVidDTO>?> opciones;

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final api = lineasApiPlataform(auth);
    late TypeVidDTO? opcion;

    setState(() {
      opciones = api.getVids().timeout(Duration(seconds: 10));
    });

    LineDetailsDTO lineInit = widget.line;

    _distanceController.text = lineInit.distance.toString();
    _lineNumberController.text = lineInit.lineNumber.toString();
    DateTime _fechaPlantacion = lineInit.plantingDate!;
    // bool habilitado = lineInit.harvestEnabled!;

    logger.d(lineInit);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Actualizar Linea '),
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
                                      logger
                                          .d('Nuevo valor fecha de plantacion');
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
                      ElevatedButton(
                          key: Key('okButtonKey'),
                          onPressed: () async {
                            if (_form.currentState!.validate()) {
                              _form.currentState!.save();
                              logger.d('Boton de Actualizar linea pulsado');

                              LineDTO lineaActualizada = LineDTO(
                                  lineNumber:
                                      int.parse(_lineNumberController.text),
                                  distance: int.parse(_distanceController.text),
                                  plantingDate: _fechaPlantacion,
                                  idTypeVid: opcion!.id!);

                              logger.d(lineaActualizada);
                              try {
                                MessageResponseDTO? response = await api
                                    .updateLineDetails(
                                        widget.lineId!, lineaActualizada)
                                    .timeout(Duration(seconds: 10));
                                logger.d('Respuesta:');
                                logger.d(response);
                                snackGreen(context,
                                    'Linea actualizada correctamente.');
                              } on TimeoutException {
                                snackTimeout(context);
                              } catch (e) {
                                snackRed(context,
                                    'Error al intentar actualizar la linea.');
                              }
                            }
                            Navigator.pop(context);
                          },
                          child: Text('Actualizar Linea')),
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

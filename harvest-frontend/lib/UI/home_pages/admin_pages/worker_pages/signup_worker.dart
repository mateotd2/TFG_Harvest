import 'dart:async';

import 'package:flutter/material.dart';
import 'package:form_validator/form_validator.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/utils/plataform_apis/workers_api.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../../utils/provider/sign_in_model.dart';
import '../../../../utils/snack_bars.dart';
import '../../../../utils/validators.dart';

class SignupWorker extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _SignupWorkerState();
}

class _SignupWorkerState extends State<SignupWorker> {
  var logger = Logger();
  final _form = GlobalKey<FormState>();
  DateTime _fehaNac = DateTime.now();

  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _lastnameController = TextEditingController();
  final TextEditingController _dniController = TextEditingController();
  final TextEditingController _nssController = TextEditingController();
  final TextEditingController _phoneController = TextEditingController();
  final TextEditingController _addressController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final apiInstance = (trabajadoresApiPlataform(auth));

    final phoneValidator =
        ValidationBuilder(localeName: 'es').phone().maxLength(254).build();

    return Scaffold(
      appBar: AppBar(
        title: const Text('Agregar Trabajador'),
        backgroundColor: Colors.green,
      ),
      body: SingleChildScrollView(
        child: Form(
          key: _form,
          child: Column(
            children: [
              TextFormField(
                key: Key('nameKey'),
                controller: _nameController,
                decoration: InputDecoration(labelText: 'Nombre'),
                validator: (valor) {
                  if (valor!.isEmpty) {
                    return 'Ingresar Nombre';
                  }
                  valor = valor.trim();
                  if (valor.length >= 254 || !isNamesValid(valor)) {
                    return 'Ingresar Nombre valido';
                  }

                  return null;
                },
              ),
              TextFormField(
                key: Key('lasnameKey'),
                controller: _lastnameController,
                decoration: InputDecoration(labelText: 'Apellidos'),
                validator: (valor) {
                  if (valor == null || valor.isEmpty) {
                    return 'Ingresar Apellidos';
                  }
                  valor = valor.trim();
                  if (valor.length >= 254 || !isNamesValid(valor)) {
                    return 'Ingresar Apellidos validos';
                  }
                  return null;
                },
              ),

              TextFormField(
                key: Key('dniKey'),
                controller: _dniController,
                decoration: InputDecoration(labelText: 'DNI'),
                validator: (valor) {
                  if (valor == null || valor.isEmpty) {
                    return 'Ingresar DNI';
                  }

                  valor = valor.trim();
                  if (valor.length >= 254 || !isDNIValid(valor)) {
                    return 'Ingresar DNI valido';
                  }
                  return null;
                },
              ),
              TextFormField(
                key: Key('nssKey'),
                controller: _nssController,
                decoration: InputDecoration(labelText: 'NSS'),
                validator: (valor) {
                  if (valor!.isEmpty) {
                    return 'Ingresar NSS';
                  }
                  valor = valor.trim();
                  if (valor.length > 254 || !isNSSValid(valor)) {
                    return 'Ingresar NSS valido';
                  }
                  return null;
                },
              ),
              TextFormField(
                key: Key('addressKey'),
                controller: _addressController,
                decoration: InputDecoration(labelText: 'address'),
                validator: (valor) {
                  if (valor!.isEmpty) {
                    return 'Ingresar direccion';
                  }
                  valor = valor.trim();
                  if (valor.length > 254) {
                    return 'Ingresar direccion valido';
                  }
                  return null;
                },
              ),
              TextFormField(
                key: Key('phoneKey'),
                controller: _phoneController,
                decoration: InputDecoration(labelText: 'Telefono'),
                validator: phoneValidator,
              ),

              // Esto mantiene el estado del formulario al seleccionar una nueva fecha
              StatefulBuilder(builder: (context, setState) {
                return TextFormField(
                  key: Key('dateKey'),
                  readOnly: true,
                  decoration: InputDecoration(
                      labelText: 'Fecha de Nacimiento',
                      suffixIcon: IconButton(
                        icon: Icon(Icons.calendar_month),
                        onPressed: () async {
                          final DateTime? seleccion = await showDatePicker(
                              context: context,
                              initialDate: _fehaNac,
                              firstDate: DateTime(1940),
                              lastDate: DateTime(2050));
                          if (seleccion != null) {
                            setState(() {
                              logger.d('Nuevo valor de fecha de nacimiento');
                              logger.d(seleccion);
                              _fehaNac = seleccion;
                            });
                          }
                        },
                      )),
                  controller: TextEditingController(
                      text: "${_fehaNac.toLocal()}".substring(0, 10)),
                );
              }),

              ElevatedButton(
                  key: Key('buttonKey'),
                  onPressed: () async {
                    if (_form.currentState!.validate()) {
                      _form.currentState!.save();
                      logger.d('Cambio de informacion de usuario:');

                      WorkerDTO worker = WorkerDTO(
                        name: _nameController.text,
                        lastname: _lastnameController.text,
                        dni: _dniController.text,
                        nss: _nssController.text,
                        phone: _phoneController.text,
                        birthdate: _fehaNac,
                        address: _addressController.text,
                      );
                      logger.d(worker);
                      try {
                        MessageResponseDTO? response = await apiInstance
                            .signUpWorker(worker)
                            .timeout(Duration(seconds: 10));

                        logger.d('Respuesta:');
                        logger.d(response);
                        logger.d('Cambio de datos de usuario Finalizado');
                        snackGreen(context, 'Trabajador agregado.');
                      } on TimeoutException {
                        snackTimeout(context);
                      } catch (e) {
                        snackRed(
                            context, 'Error al dar de alta al trabajador.');
                      }
                    }
                    Navigator.pop(context);
                  },
                  child: const Text('Confirmar'))
            ],
          ),
        ),
      ),
    );
  }
}

import 'dart:async';

import 'package:flutter/material.dart';
import 'package:form_validator/form_validator.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/utils/plataform_apis/workers_api.dart';
import 'package:harvest_frontend/utils/snack_bars.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../../utils/provider/sign_in_model.dart';
import '../../../../utils/validators.dart';

class WorkerDetailsUpdate extends StatefulWidget {
  final WorkerDTO worker;

  WorkerDetailsUpdate({required this.worker});

  @override
  State<StatefulWidget> createState() => WorkerDetailsUpdateState();
}

class WorkerDetailsUpdateState extends State<WorkerDetailsUpdate> {
  var logger = Logger();
  final _form = GlobalKey<FormState>();

  // DateTime _fehaNac = DateTime.now();
  DateTime _fechaNac = DateTime.now();

  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _lastnameController = TextEditingController();
  final TextEditingController _dniController = TextEditingController();
  final TextEditingController _nssController = TextEditingController();
  final TextEditingController _phoneController = TextEditingController();
  final TextEditingController _addressController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _nameController.text = widget.worker.name;
    _lastnameController.text = widget.worker.lastname;
    _addressController.text = widget.worker.address;
    _phoneController.text = widget.worker.phone;
    _nssController.text = widget.worker.nss;
    _dniController.text = widget.worker.dni;
    _fechaNac = widget.worker.birthdate;
  }

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final apiInstance = trabajadoresApiPlataform(auth);

    final phoneValidator =
        ValidationBuilder(localeName: 'es').phone().maxLength(254).build();

    return Scaffold(
        appBar: AppBar(
          title: const Text('Actualizar información.'),
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
                key: Key('addressKey'),
                controller: _addressController,
                decoration: InputDecoration(labelText: 'Direccion'),
                validator: (valor) {
                  if (valor == null || valor.isEmpty) {
                    return 'Ingresar Direccion';
                  }
                  valor = valor.trim();
                  if (valor.length >= 1024) {
                    return 'Ingresar Direccion validos';
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
                      labelText: 'Fecha de nacimiento',
                      suffixIcon: IconButton(
                        icon: Icon(Icons.calendar_month),
                        onPressed: () async {
                          final DateTime? seleccion = await showDatePicker(
                              context: context,
                              initialDate: _fechaNac,
                              firstDate: DateTime(1940),
                              lastDate: DateTime(2050));
                          if (seleccion != null) {
                            setState(() {
                              logger.d('Nuevo valor de fecha de nacimiento');
                              logger.d(seleccion);
                              _fechaNac = seleccion;
                            });
                          }
                        },
                      )),
                  controller: TextEditingController(
                      text: "${_fechaNac.toLocal()}".substring(0, 10)),
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
                          address: _addressController.text,
                          dni: _dniController.text,
                          nss: _nssController.text,
                          phone: _phoneController.text,
                          birthdate: _fechaNac);
                      logger.d(worker);
                      try {
                        await apiInstance
                            .updateWorker(widget.worker.id!, worker)
                            .timeout(Duration(seconds: 10));

                        logger.d('Respuesta:');
                        logger.d('Cambio de datos de usuario Finalizado');

                        snackGreen(context, 'Trabajador actualizado.');
                      } on TimeoutException {
                        snackTimeout(context);
                      } catch (e) {
                        snackRed(context,
                            'Error al actualizar datos de trabajador.');
                      }
                    }
                    Navigator.pop(context);
                  },
                  child: const Text('Confirmar'))
            ],
          ),
        )));
  }
}

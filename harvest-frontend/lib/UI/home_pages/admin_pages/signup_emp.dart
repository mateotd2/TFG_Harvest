

import 'dart:async';

import 'package:flutter/material.dart';
import 'package:form_validator/form_validator.dart';
import 'package:harvest_api/api.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../utils/plataform_apis/auth_api.dart';
import '../../../utils/provider/sign_in_model.dart';
import '../../../utils/validators.dart';

class SignupEmp extends StatefulWidget{
  @override
  State<StatefulWidget> createState() => _SignupEmpState();

}

class _SignupEmpState  extends State<SignupEmp>{

  var logger = Logger();
  final _form = GlobalKey<FormState>();
  DateTime _fehaNac = DateTime.now();



  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _lastnameController = TextEditingController();
  final TextEditingController _dniController = TextEditingController();
  final TextEditingController _nssController = TextEditingController();
  final TextEditingController _usernameController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final TextEditingController _phoneController = TextEditingController();

  @override
  Widget build(BuildContext context) {

    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final apiInstance = autenticadoApiPlataform(auth);

    final emailValidator =
    ValidationBuilder(localeName: 'es').email().maxLength(254).build();
    final phoneValidator =
    ValidationBuilder(localeName: 'es').phone().maxLength(254).build();



    return Scaffold(
      appBar: AppBar(
        title: const Text('Agregar Empleado'),
        backgroundColor: Colors.green,
      ),
      body: SingleChildScrollView(
        child: Form(
          key: _form,
          child: Column(
            children: [

              TextFormField(
                key: Key('emailKey'),
                controller: _emailController,
                decoration: InputDecoration(labelText: 'Email'),
                validator: emailValidator,
              ),
              TextFormField(
                key: Key('usernameKey'),
                controller: _usernameController,
                decoration: InputDecoration(labelText: 'Nombre de usuario'),
                validator: (valor) {
                  if (valor!.isEmpty) {
                    return 'Ingresar Nombre de usuario';
                  }
                  valor = valor.trim();
                  if (valor.length >= 254 || !isNamesValid(valor)) {
                    return 'Ingresar un Nombre de usuario valido';
                  }

                  return null;
                },
              ),
              TextFormField(
                obscureText: true,
                key: Key('passwordKey'),
                controller: _passwordController,
                decoration: InputDecoration(labelText: 'Contraseña'),
                validator: (valor) {
                  if (valor!.isEmpty) {
                    return 'Ingresar Contraseña';
                  }
                  valor = valor.trim();
                  if (valor.length >= 254 || !isNamesValid(valor)) {
                    return 'Ingresar Contraseña valida';
                  }

                  return null;
                },
              ),
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
                      labelText: 'Fecha',
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

                      NewUserDTO user = NewUserDTO(
                          email: _emailController.text,
                          name: _nameController.text,
                          lastname: _lastnameController.text,
                          dni: _dniController.text,
                          nss: _nssController.text,
                          phone: _phoneController.text,
                          birthdate: _fehaNac,
                          username: _usernameController.text,
                          password: _passwordController.text);
                      logger.d(user);
                      try {
                        MessageResponseDTO? response = await apiInstance
                            .signUp(user)
                            .timeout(Duration(seconds: 10));

                        logger.d('Respuesta:');
                        logger.d(response);
                        logger.d('Cambio de datos de usuario Finalizado');

                        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
                            content: Text(
                                'Empleado agregado.')));
                        Navigator.pop(context);

                      }on TimeoutException {
                        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
                            key: Key('snackKey'),
                            backgroundColor: Colors.red,
                            content: Text(
                                'Comunicacion con el servidor fallida')));
                        Navigator.pop(context);
                      } catch (e) {
                        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
                            key: Key('snackKey'),
                            backgroundColor: Colors.red,
                            content: Text(
                                'Error al dar de alta al usuario.')));
                        Navigator.pop(context);
                      }

                    }
                  },
                  child: const Text('Confirmar'))

            ],
          ),

        ),
      ),
    );
  }
}
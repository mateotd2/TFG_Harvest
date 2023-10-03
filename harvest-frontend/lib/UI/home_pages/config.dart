import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/UI/home_pages/update_user.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../utils/plataform_apis/auth_api.dart';
import '../../utils/provider/sign_in_model.dart';

class Config extends StatefulWidget {
  @override
  State<Config> createState() => _ConfigState();
}

class _ConfigState extends State<Config> {
  var logger = Logger();

  final _form = GlobalKey<FormState>();

  String _oldPassword = '';
  String _newPassword = '';

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final apiInstance = autenticadoApiPlataform(auth);
    return Center(
        child: Column(
      children: [
        Text('Mi cuenta:', style: TextStyle(fontSize: 25)),
        InfoCuenta(),
        SizedBox(height: 20),
        ElevatedButton(
            onPressed: () {
              Navigator.push(context,
                  MaterialPageRoute(builder: (context) => UpdateUser()));
            },
            child: Text('Cambiar informacion de cuenta')),
        SizedBox(height: 80),
        Divider(color: Colors.green, thickness: 1),
        Text('Gestion de contraseña:', style: TextStyle(fontSize: 25)),
        SizedBox(height: 20),
        ElevatedButton(
            onPressed: () {
              showDialog(
                  context: context,
                  builder: (BuildContext context) {
                    return AlertDialog(
                      actions: [
                        ElevatedButton(
                            onPressed: () {
                              Navigator.of(context).pop();
                              logger.d('Cambio de contrasña cancelado');
                            },
                            child: Text('Cancelar')),
                        ElevatedButton(
                            onPressed: () async {
                              if (_form.currentState!.validate()) {
                                _form.currentState!.save();
                                logger.d('Cambio de contraseña finalizado');
                                logger.d('Antigua contraseña: $_oldPassword');
                                logger.d('Nueva contraseña:  $_newPassword');
                                ChangePasswordDTO changePass =
                                    ChangePasswordDTO(
                                        oldPassword: _oldPassword,
                                        newPassword: _newPassword);
                                logger.d(changePass);
                                // TODO: Segun el error, mostrar un SnackBar distinto
                                try {
                                  MessageResponseDTO? response =
                                      await apiInstance.changePassword(
                                          estado.lastResponse!.id, changePass);
                                  logger.d('Respuesta:');
                                  logger.d(response);
                                  logger.d('Cambio de contraseña finalizado');

                                  ScaffoldMessenger.of(context).showSnackBar(
                                      SnackBar(
                                          content: Text(
                                              'Cambio de contraseña finalizado.')));
                                } catch (e) {
                                  ScaffoldMessenger.of(context).showSnackBar(
                                      SnackBar(
                                          backgroundColor: Colors.red,
                                          content: Text(
                                              'Error en el cambio de contraseña.')));
                                }
                                Navigator.of(context).pop();
                              }
                            },
                            child: Text('Listo')),
                      ],
                      title: Text('Cambiar Contraseña'),
                      content: ConstrainedBox(
                        constraints: BoxConstraints(maxHeight: 200),
                        child: Form(
                            key: _form,
                            child: Center(
                              child: Column(
                                children: [
                                  TextFormField(
                                      decoration: InputDecoration(
                                          label: Text('Antigua contraseña:')),
                                      onSaved: (pass) =>
                                          _oldPassword = pass ?? '',
                                      obscureText: true),
                                  TextFormField(
                                      decoration: InputDecoration(
                                          label: Text('Nueva contraseña:')),
                                      validator: (pass) {
                                        if (pass!.isEmpty) {
                                          return 'Añada una nueva contraseña';
                                        }
                                        if (pass.length < 12) {
                                          return 'Necesita superar los 12 caracteres';
                                        }
                                        return null;
                                      },
                                      onSaved: (pass) =>
                                          _newPassword = pass ?? '',
                                      obscureText: true),
                                ],
                              ),
                            )),
                      ),
                    );
                  });
            },
            child: Text('Cambiar contraseña')),
      ],
    ));
  }
}

class InfoCuenta extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    var responseState = Provider.of<SignInResponseModel>(context);
    return Text('CUENTA DE ${responseState.lastResponse?.username}');
  }
}

import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/backend/fetch_user.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../utils/provider/sign_in_model.dart';

void main() {
  runApp(const SignIn());
}

class SignIn extends StatefulWidget {
  const SignIn({super.key});

  @override
  SignInState createState() => SignInState();
}

class SignInState extends State<SignIn> {
  final TextEditingController _userController = TextEditingController();
  final TextEditingController _passController = TextEditingController();
  var logger = Logger();
  @override
  Widget build(BuildContext context) {
    var responseState = Provider.of<SignInResponseModel>(context);
    return Scaffold(
      backgroundColor: Colors.white,
      body: SingleChildScrollView(
        child: Column(
          children: <Widget>[
            Padding(
              padding: const EdgeInsets.only(top: 60.0),
              child: Center(
                child: SizedBox(
                    width: 400,
                    height: 300,
                    child: Center(
                        child: Container(
                            decoration: BoxDecoration(
                                color: Colors.green,
                                borderRadius: BorderRadius.circular(20)),
                            width: 300,
                            height: 100,
                            child: const Center(
                              child: Text('Harvest',
                                  style: TextStyle(
                                      fontSize: 40.0,
                                      color: Colors.amber,
                                      fontStyle: FontStyle.italic,
                                      fontWeight: FontWeight.bold)),
                            )))),
              ),
            ),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 15),
              child: TextField(
                controller: _userController,
                decoration: const InputDecoration(
                    border: OutlineInputBorder(),
                    labelText: 'Username',
                    hintText: 'Enter username'),
              ),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  left: 15.0, right: 15.0, top: 30, bottom: 0),
              child: TextField(
                controller: _passController,
                obscureText: true,
                decoration: const InputDecoration(
                    border: OutlineInputBorder(),
                    labelText: 'Password',
                    hintText: 'Enter secure password'),
              ),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  left: 15.0, right: 15.0, top: 60, bottom: 0),
              child: Container(
                height: 50,
                width: 250,
                decoration: BoxDecoration(
                    color: Colors.green,
                    borderRadius: BorderRadius.circular(20)),
                child: TextButton(
                  onPressed: () {
                    // TEST
                    logger.d("PULSADO ");
                    // COGER USUARIO Y CONTRASEÑA DE LOS TEXTFIELDS
                    var signInRequest = SignInRequestDTO(
                        username: _userController.text,
                        password: _passController.text);
                    fetchUser(responseState, context, signInRequest);
                  },
                  child: const Text(
                    'Acceder',
                    style: TextStyle(color: Colors.white, fontSize: 25),
                  ),
                ),
              ),
            )
          ],
        ),
      ),
    );
  }
}

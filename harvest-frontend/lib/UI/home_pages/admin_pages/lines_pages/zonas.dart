import 'package:flutter/material.dart';
import 'package:harvest_frontend/UI/home_pages/admin_pages/signup_emp.dart';
import 'package:logger/logger.dart';

class Zonas extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _ZonasState();
}

class _ZonasState extends State<Zonas> {
  var logger = Logger();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          children: [
            Text('Empleado 1'),
            Text('Empleado 2'),
            Text('Empleado 3')
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          logger.d('ADD USER PULSADO');
          Navigator.push(
              context, MaterialPageRoute(builder: (context) => SignupEmp()));
        },
        key: Key('addEmpKey'),
        child: Icon(Icons.group_add_rounded),
      ),
    );
  }
}

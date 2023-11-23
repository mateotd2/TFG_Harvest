import 'package:flutter/material.dart';
import 'package:harvest_frontend/UI/home_pages/admin_pages/empleados.dart';
import 'package:harvest_frontend/UI/home_pages/admin_pages/pasar_lista.dart';
import 'package:harvest_frontend/UI/home_pages/admin_pages/workers.dart';
import 'package:logger/logger.dart';

class Admin extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _AdminState();
}

class _AdminState extends State<Admin> {
  int _navIndice = 0;
  var logger = Logger();

  void _seleccionItem(int index) {
    setState(() {
      _navIndice = index;
    });
  }

  final List _paginasAdmin = [Empleados(), Trabajadores(), PasarLista()];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      key: Key('adminKey'),
      body: _paginasAdmin[_navIndice],
      bottomNavigationBar: BottomNavigationBar(
        items: [
          BottomNavigationBarItem(icon: Icon(Icons.group), label: 'Empleados'),
          BottomNavigationBarItem(
              icon: Icon(Icons.groups_outlined), label: 'Operarios'),
          BottomNavigationBarItem(
              icon: Icon(Icons.assignment_turned_in_outlined),
              label: 'Pasar Lista')
        ],
        onTap: _seleccionItem,
        currentIndex: _navIndice,
      ),
    );
  }
}

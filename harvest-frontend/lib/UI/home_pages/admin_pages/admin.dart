import 'package:flutter/material.dart';
import 'package:harvest_frontend/UI/home_pages/admin_pages/emp_pages/emps.dart';
import 'package:harvest_frontend/UI/home_pages/admin_pages/worker_pages/pasar_lista.dart';
import 'package:harvest_frontend/UI/home_pages/admin_pages/worker_pages/workers.dart';
import 'package:logger/logger.dart';

import 'lines_pages/zonas.dart';

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

  final List _paginasAdmin = [Emps(), Trabajadores(), Zonas(), PasarLista()];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      key: Key('adminKey'),
      body: _paginasAdmin[_navIndice],
      bottomNavigationBar: BottomNavigationBar(
        type: BottomNavigationBarType.fixed,
        items: [
          BottomNavigationBarItem(icon: Icon(Icons.group), label: 'Empleados'),
          BottomNavigationBarItem(
              icon: Icon(Icons.groups_outlined), label: 'Operarios'),
          BottomNavigationBarItem(
              icon: Icon(Icons.view_column_rounded), label: 'Zonas'),
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

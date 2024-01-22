import 'package:flutter/material.dart';
import 'package:harvest_frontend/UI/home_pages/capataz_pages/ended_tasks.dart';
import 'package:harvest_frontend/UI/home_pages/capataz_pages/in_progress_tasks.dart';
import 'package:harvest_frontend/UI/home_pages/capataz_pages/pending_tasks.dart';
import 'package:logger/logger.dart';

class Capataz extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _CapatazState();
}

class _CapatazState extends State<Capataz> {
  int _navIndice = 0;
  var logger = Logger();

  void _seleccionItem(int index) {
    setState(() {
      _navIndice = index;
    });
  }

  final List _paginasCap = [
    PendingTasks(typePhase: TypePhase.none),
    InProgressTasks(),
    EndedTasks()
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      key: Key('CapatazKey'),
      body: _paginasCap[_navIndice],
      bottomNavigationBar: BottomNavigationBar(
        type: BottomNavigationBarType.fixed,
        items: [
          BottomNavigationBarItem(
              icon: Icon(Icons.assignment), label: '  Tareas \nPendientes'),
          BottomNavigationBarItem(
              icon: Icon(Icons.playlist_play), label: 'En Proceso'),
          BottomNavigationBarItem(
              icon: Icon(Icons.checklist), label: 'Finalizadas'),
        ],
        onTap: _seleccionItem,
        currentIndex: _navIndice,
      ),
    );
  }
}

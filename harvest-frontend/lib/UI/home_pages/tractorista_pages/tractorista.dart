import 'package:flutter/material.dart';
import 'package:harvest_frontend/UI/home_pages/tractorista_pages/pending_load_tasks.dart';
import 'package:logger/logger.dart';

import 'ended_load_tasks.dart';
import 'in_progress_load_tasks.dart';

class Tractorista extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _TractoristaState();
}

class _TractoristaState extends State<Tractorista> {
  int _navIndice = 0;
  var logger = Logger();

  void _seleccionItem(int index) {
    setState(() {
      _navIndice = index;
    });
  }

  final List _paginasTract = [
    PendingLoadTasks(typePhase: TypePhase.none),
    InProgressLoadTasks(),
    EndedLoadTasks()
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      key: Key('TractorKey'),
      body: _paginasTract[_navIndice],
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

import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:logger/logger.dart';

class WorkersSelector extends StatefulWidget {
  final List<WorkerDTO>? workers;

  WorkersSelector({required this.workers});

  @override
  State<StatefulWidget> createState() => _WorkersSelectorState();
}

class _WorkersSelectorState extends State<WorkersSelector> {
  var logger = Logger();
  late List<WorkerDTO> trabajadores;

  List<int> idsSeleccion = [];
  List<WorkerDTO> filtrado = [];

  @override
  void initState() {
    trabajadores = widget.workers!;
    filtrado.addAll(trabajadores);
    super.initState();
  }

  void filtroPorNombre(String nombre) {
    logger.d("Filtrar con cadena: ${nombre.toUpperCase()}");
    setState(() {
      filtrado = trabajadores
          .where((element) =>
              "${element.name.toUpperCase()} ${element.lastname.toUpperCase()}"
                  .contains(nombre.toUpperCase()))
          .toList();
    });
  }

  @override
  Widget build(BuildContext context) {
    logger.d(trabajadores);
    logger.d(idsSeleccion);

    return Scaffold(
        appBar: AppBar(
            backgroundColor: Colors.green,
            title: Text("Seleccionar Trabajadores")),
        body: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Padding(
                padding: const EdgeInsets.all(8.0),
                child: TextField(
                  onChanged: (nombre) {
                    filtroPorNombre(nombre);
                  },
                  decoration: InputDecoration(label: Text("Nombre")),
                )),
            Expanded(
              child: ListView.builder(
                itemCount: filtrado.length,
                itemBuilder: (context, index) {
                  return Container(
                    color: index % 2 == 0 ? Colors.grey[200] : null,
                    child: CheckboxListTile(
                      value: idsSeleccion.contains(filtrado[index].id!),
                      onChanged: (value) {
                        setState(() {
                          if ((value!)) {
                            idsSeleccion.add(filtrado[index].id!);
                          } else {
                            idsSeleccion.remove(filtrado[index].id);
                          }
                        });
                      },
                      title: Text(
                          "${filtrado[index].name} ${filtrado[index].lastname}"),
                      subtitle: Text(
                          "Finaliza su jornada a las ${filtrado[index].horaFinJornada}"),
                    ),
                  );
                },
              ),
            ),
            ElevatedButton(
                onPressed: () {
                  Navigator.pop(context, idsSeleccion);
                },
                child: Text("Iniciar tarea"))
          ],
        ));
  }
}

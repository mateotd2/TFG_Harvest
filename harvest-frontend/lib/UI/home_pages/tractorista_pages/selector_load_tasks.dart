import 'dart:async';

import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../utils/plataform_apis/campanha_api.dart';
import '../../../utils/provider/sign_in_model.dart';
import '../../../utils/snack_bars.dart';

class SelectorsLoadTasks extends StatefulWidget {
  final List<int> idTareas;
  final List<TractorDTO> tractoresDisponibles;
  final List<WorkerDTO> trabajadoresDisponibles;

  SelectorsLoadTasks(
      {required this.tractoresDisponibles,
      required this.trabajadoresDisponibles,
      required this.idTareas});

  @override
  State<StatefulWidget> createState() => _SelectorsLoadTasks();
}

class _SelectorsLoadTasks extends State<SelectorsLoadTasks> {
  var logger = Logger();
  late List<WorkerDTO> trabajadores;

  List<int> idsSeleccion = [];
  List<WorkerDTO> filtrado = [];

  late TractorDTO tractorSeleccionado;

  @override
  void initState() {
    trabajadores = widget.trabajadoresDisponibles;
    filtrado.addAll(trabajadores);
    tractorSeleccionado = widget.tractoresDisponibles.first;
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
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final api = campanhaApiPlataform(auth);

    logger.d(widget.idTareas);
    logger.d(widget.trabajadoresDisponibles);
    logger.d(widget.tractoresDisponibles);

    List<TractorDTO> tractores = widget.tractoresDisponibles;
    return Scaffold(
        appBar: AppBar(
          backgroundColor: Colors.green,
          title: Text('Seleccionar opciones'),
        ),
        body: Column(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            Padding(
                padding: const EdgeInsets.all(8.0),
                child: TextField(
                  onChanged: (nombre) {
                    filtroPorNombre(nombre);
                  },
                  decoration: InputDecoration(
                      prefixIcon: Icon(Icons.search),
                      label: Text("Nombre y/o apellidos")),
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
            Expanded(
              child: Column(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    DropdownButtonFormField<TractorDTO>(
                      decoration: InputDecoration(
                        border: OutlineInputBorder(),
                        // Agrega un borde alrededor del DropdownButton
                        labelText: 'Seleccionar un Tractor',
                      ),
                      value: tractorSeleccionado,
                      items: tractores.map<DropdownMenuItem<TractorDTO>>(
                          (TractorDTO tractor) {
                        return DropdownMenuItem<TractorDTO>(
                          value: tractor,
                          child: Text(
                              "${tractor.licensePlate} ${tractor.brand} ${tractor.model} "),
                        );
                      }).toList(),
                      onChanged: (TractorDTO? value) {
                        setState(() {
                          tractorSeleccionado = value!;
                        });
                      },
                    ),
                    ElevatedButton(
                        onPressed: () async {
                          try {
                            StartLoadTasksDTO startLoadTasksDTO =
                                StartLoadTasksDTO(
                                    idsWorkers: idsSeleccion,
                                    idTractor: tractorSeleccionado.id!,
                                    idLoadTasks: widget.idTareas);
                            await api
                                .startLoadTasks(startLoadTasksDTO)
                                .timeout(Duration(seconds: 10));

                            snackGreen(context, 'Tareas Iniciadas');
                            Navigator.pop(context, true);
                          } on TimeoutException {
                            snackTimeout(context);
                            Navigator.pop(context, false);
                          } catch (e) {
                            snackRed(context,
                                'Error al intentar iniciar las tareas');
                            Navigator.pop(context, false);
                          }
                        },
                        child: Text("Inciar tarea"))
                  ]),
            ),
          ],
        ));
  }
}

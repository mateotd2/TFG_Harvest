import 'dart:async';

import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/UI/home_pages/admin_pages/worker_pages/signup_worker.dart';
import 'package:harvest_frontend/UI/home_pages/admin_pages/worker_pages/worker_details.dart';
import 'package:harvest_frontend/utils/plataform_apis/workers_api.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../../utils/provider/sign_in_model.dart';

class Trabajadores extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _TrabajadoresState();
}

class _TrabajadoresState extends State<Trabajadores> {
  var logger = Logger();
  late Future<List<WorkerDTO>?> trabajadores;

  Future<List<WorkerDTO>?> obtenerTrabajadores(TrabajadoresApi api) {
    return api.getWorkers().timeout(Duration(seconds: 10));
  }

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final apiInstance = trabajadoresApiPlataform(auth);

    setState(() {
      trabajadores = apiInstance.getWorkers().timeout(Duration(seconds: 10));
    });

    return Scaffold(
      body: RefreshIndicator(
        onRefresh: () async {
          setState(() {
            trabajadores =
                apiInstance.getWorkers().timeout(Duration(seconds: 10));
          });
        },
        child: FutureBuilder(
          future: trabajadores,
          builder:
              (BuildContext context, AsyncSnapshot<List<WorkerDTO>?> snapshot) {
            if (snapshot.connectionState == ConnectionState.done) {
              List<WorkerDTO>? trabajadoresObtenidos = snapshot.data;
              logger.d(trabajadoresObtenidos);

              if (trabajadoresObtenidos == null) {
                return Text("Nada que mostrar :(");
              } else {
                return ListView.builder(
                  itemCount: trabajadoresObtenidos.length,
                  itemBuilder: (context, index) {
                    final trabajador = trabajadoresObtenidos[index];
                    return ListTile(
                        title:
                            Text("${trabajador.name} ${trabajador.lastname}"),
                        trailing: Icon(Icons.arrow_forward_ios_sharp),
                        onTap: () async {
                          await Navigator.push(
                              context,
                              MaterialPageRoute(
                                  builder: (context) =>
                                      WorkerDetails(workerId: trabajador.id)));
                          setState(() {
                            trabajadores = apiInstance
                                .getWorkers()
                                .timeout(Duration(seconds: 10));
                          });
                        });
                  },
                );
              }
            } else if (snapshot.hasError) {
              ScaffoldMessenger.of(context).showSnackBar(SnackBar(
                  key: Key('snackKey'),
                  backgroundColor: Colors.red,
                  content: Text('Error obteniendo los trabajadores')));
              Navigator.pop(context);
              return Text("Nada que enseñar :(");
            } else {
              return Center(child: CircularProgressIndicator());
            }
          },
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          logger.d('ADD WORKER PULSADO');
          await Navigator.push(
              context, MaterialPageRoute(builder: (context) => SignupWorker()));
          setState(() {
            trabajadores =
                apiInstance.getWorkers().timeout(Duration(seconds: 10));
          });
        },
        key: Key('addEmpKey'),
        child: Icon(Icons.group_add_rounded),
      ),
    );
  }
}

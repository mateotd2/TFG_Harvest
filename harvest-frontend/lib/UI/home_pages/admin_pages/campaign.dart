import 'dart:async';

import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/UI/home_pages/admin_pages/pending_tasks.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../utils/plataform_apis/campanha_api.dart';
import '../../../utils/provider/sign_in_model.dart';
import '../../../utils/snack_bars.dart';

class Campaign extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _CampaignState();
}

class _CampaignState extends State<Campaign> {
  var logger = Logger();

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final api = campanhaApiPlataform(auth);
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          SizedBox(height: 32.0),
          ElevatedButton(

              onPressed: () {
                showDialog(context: context, builder: (BuildContext context) {
                  return AlertDialog(
                    title: Text("Confirmación"),
                    content: Text("Seguro que desea inicar la campaña anual?"),
                    actions: [
                      ElevatedButton(onPressed: (){
                        Navigator.of(context).pop();
                        logger.d("Cancelado inicio de campaña");
                      }, child: Text('Cancelar')
                        ,style: ElevatedButton.styleFrom(
                              backgroundColor: Colors.red)
                      )
                      ,ElevatedButton(
                        onPressed: () async {
                          try {
                            await api.startCampaign().timeout(Duration(seconds: 10));
                            snackGreen(context, 'Comenzando recolección');
                          } on TimeoutException {
                            snackTimeout(context);
                          } catch (e) {
                            snackRed(context, 'Error al comenzar la campaña.');
                          }
                          Navigator.of(context).pop();
                        }, child:  Text('Aceptar'))
                    ],
                  );
                });
              },


              child: Text("Comenzar campaña")),
          SizedBox(height: 64.0),
          ElevatedButton(
              onPressed: () async {
                await Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder:(context) => PendingTasks( typePhase: TypePhase.cleaning))
                );
              },

              // onPressed: () async {
              //   try {
              //     // PendingTasks(TypePhase.cleaning);
              //     await api.startPruning().timeout(Duration(seconds: 10));
              //     snackGreen(context, 'Pasando a fase de poda.');
              //   } on TimeoutException {
              //     snackTimeout(context);
              //   } catch (e) {
              //     snackRed(
              //         context, 'Error al intentar pasar a la fase de poda.');
              //   }
              // },
              child: Text("Comenzar fase de poda")),
          SizedBox(height: 64.0),
          ElevatedButton(

              onPressed: () async {
                await Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder:(context) => PendingTasks(typePhase: TypePhase.pruning))
                );
              },
              // onPressed: () async {
              //   try {
              //     await api.startHarvesting().timeout(Duration(seconds: 10));
              //     snackGreen(context, 'Pasando a fase de recolección.');
              //   } on TimeoutException {
              //     snackTimeout(context);
              //   } catch (e) {
              //     snackRed(context,
              //         'Error al intentar pasar a la fase de recolección');
              //   }
              // },
              child: Text("Comenzar fase de recoleccion")),
          SizedBox(height: 64.0),
          ElevatedButton(

              onPressed: () async {
                await Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder:(context) => PendingTasks(typePhase: TypePhase.harvest))
                );
              },

              // onPressed: () async {
              //   try {
              //     await api.endCampaign().timeout(Duration(seconds: 10));
              //     snackGreen(context, 'Finalizando la campaña.');
              //   } on TimeoutException {
              //     snackTimeout(context);
              //   } catch (e) {
              //     snackRed(context, 'Error al intentar finalizar la campaña.');
              //   }
              // },
              child: Text("Finalizar campaña")),
        ],
      ),
    );
  }
}

import 'dart:async';

import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
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
              onPressed: () async {
                try {
                  await api.startCampaign().timeout(Duration(seconds: 10));
                  snackGreen(context, 'Comenzando recolección');
                } on TimeoutException {
                  snackTimeout(context);
                } catch (e) {
                  snackRed(context, 'Error al comenzar la campaña.');
                }
              },
              child: Text("Comenzar campaña")),
          SizedBox(height: 64.0),
          ElevatedButton(
              onPressed: () async {
                try {
                  await api.startPruning().timeout(Duration(seconds: 10));
                  snackGreen(context, 'Pasando a fase de poda.');
                } on TimeoutException {
                  snackTimeout(context);
                } catch (e) {
                  snackRed(
                      context, 'Error al intentar pasar a la fase de poda.');
                }
              },
              child: Text("Comenzar fase de poda")),
          SizedBox(height: 64.0),
          ElevatedButton(
              onPressed: () async {
                try {
                  await api.startHarvesting().timeout(Duration(seconds: 10));
                  snackGreen(context, 'Pasando a fase de recolección.');
                } on TimeoutException {
                  snackTimeout(context);
                } catch (e) {
                  snackRed(context,
                      'Error al intentar pasar a la fase de recolección');
                }
              },
              child: Text("Comenzar fase de recoleccion")),
          SizedBox(height: 64.0),
          ElevatedButton(
              onPressed: () async {
                try {
                  await api.endCampaign().timeout(Duration(seconds: 10));
                  snackGreen(context, 'Finalizando la campaña.');
                } on TimeoutException {
                  snackTimeout(context);
                } catch (e) {
                  snackRed(context, 'Error al intentar finalizar la campaña.');
                }
              },
              child: Text("Finalizar campaña")),
        ],
      ),
    );
  }
}

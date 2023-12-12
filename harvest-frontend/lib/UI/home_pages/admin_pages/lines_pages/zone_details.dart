import 'package:flutter/material.dart';
import 'package:harvest_api/api.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../../utils/plataform_apis/lines_api.dart';
import '../../../../utils/provider/sign_in_model.dart';

class ZoneDetails extends StatefulWidget {

  final int? zoneId;

  ZoneDetails({required this.zoneId});

  @override
  State<StatefulWidget> createState() => _ZoneDetailsState();
}

class _ZoneDetailsState extends State<ZoneDetails> {
  var logger = Logger();
  late Future<ZoneDTO?> zona;


  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final api = lineasApiPlataform(auth);
    ZoneDTO? zonaObtenida;

    int? zoneId = widget.zoneId;

    setState(() {
      zona = api.getZone(zoneId!).timeout(Duration(seconds: 10));
    });
    
    return Scaffold(
      appBar: AppBar(title: Text('Detalles Zona $zoneId'),),
      body: RefreshIndicator(
        onRefresh: () async{
          setState(() {
            zona = api.getZone(zoneId!).timeout(Duration(seconds: 10));
          });
        }, child: FutureBuilder(
        future: zona,
        builder: (BuildContext context, AsyncSnapshot<ZoneDTO?> snapshot) {
          if (snapshot.connectionState == ConnectionState.done) {
            zonaObtenida = snapshot.data;
            logger.d(zonaObtenida);
            return Column(
              children: [
                ListTile(
                  title: Text("Metros cuadrados:"),
                  subtitle: Text("${zonaObtenida?.surface} m2",
                      style: TextStyle(fontSize: 18.0)),
                ),
                ListTile(
                  title: Text("Descripción:"),
                  subtitle: Text("${zonaObtenida?.description} m2",
                      style: TextStyle(fontSize: 18.0)),
                ),
                ListTile(
                  title: Text("Formación:"),
                  subtitle: Text("${zonaObtenida?.formation} m2",
                      style: TextStyle(fontSize: 18.0)),
                ),
                ListTile(
                  title: Text("Referencia catastral:"),
                  subtitle: Text("${zonaObtenida?.reference}",
                      style: TextStyle(fontSize: 18.0)),
                ),

                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    ElevatedButton(
                        onPressed: (){
                          logger.d("Modificar datos de zona pulsado");
                        },
                        child: Text("Modificar")),
                    SizedBox(width: 96.0),
                    ElevatedButton(
                        onPressed: () async{
                          logger.d("Mostrar lineas pulsado");
                        },
                        child: Text("Mostrar Lineas")
                    ),

                  ],

                )
              ],

            );

          }else if (snapshot.hasError) {
            ScaffoldMessenger.of(context).showSnackBar(SnackBar(
                key: Key('snackKey'),
                backgroundColor: Colors.red,
                content: Text('Error obteniendo la zona')));
            Navigator.pop(context);
            return Text("Nada que enseñar :(");
          } else {
            return Center(child: CircularProgressIndicator());
          }

        },

      ),

      ),
    );

  }
}
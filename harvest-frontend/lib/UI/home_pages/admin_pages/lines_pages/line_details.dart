import 'dart:async';
import 'dart:ui' as ui;

import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/UI/home_pages/admin_pages/lines_pages/update_line.dart';
import 'package:harvest_frontend/utils/snack_bars.dart';
import 'package:image_gallery_saver/image_gallery_saver.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';
import 'package:qr_flutter/qr_flutter.dart';

import '../../../../utils/plataform_apis/lines_api.dart';
import '../../../../utils/provider/sign_in_model.dart';

class LineDetails extends StatefulWidget {
  final int? lineId;
  final bool? enabled;

  LineDetails({required this.lineId, required this.enabled});

  @override
  State<StatefulWidget> createState() => _LineDetailsState();
}

class _LineDetailsState extends State<LineDetails> {
  var logger = Logger();
  late Future<LineDetailsDTO?> linea;
  late bool? harEnabled;
  bool primeraConstruccion = true;

  GlobalKey globalKey = GlobalKey();

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final api = lineasApiPlataform(auth);
    LineDetailsDTO? lineaObtenida;

    int? lineaId = widget.lineId;

    setState(() {
      linea = api.getLineDetails(lineaId!).timeout(Duration(seconds: 10));
      if (primeraConstruccion) {
        harEnabled = widget.enabled;
      }
    });

    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.green,
        title: Text('Detalles Linea $lineaId'),
      ),
      body: RefreshIndicator(
        onRefresh: () async {
          setState(() {
            linea = api.getLineDetails(lineaId!).timeout(Duration(seconds: 10));
          });
        },
        child: FutureBuilder(
          future: linea,
          builder:
              (BuildContext context, AsyncSnapshot<LineDetailsDTO?> snapshot) {
            if (snapshot.connectionState == ConnectionState.done) {
              lineaObtenida = snapshot.data;
              logger.d(lineaObtenida);

              return SingleChildScrollView(
                child: Column(
                  children: [
                    ListTile(
                      title: Text("Numero de linea:"),
                      subtitle: Text("${lineaObtenida?.lineNumber}",
                          style: TextStyle(fontSize: 18.0)),
                    ),
                    ListTile(
                      title: Text("Longitud:"),
                      subtitle: Text("${lineaObtenida?.distance} metros",
                          style: TextStyle(fontSize: 18.0)),
                    ),
                    ListTile(
                      title: Text("Vid:"),
                      subtitle: Text("${lineaObtenida?.vid?.name}",
                          style: TextStyle(fontSize: 18.0)),
                    ),
                    ListTile(
                      title: Text("Descripcion de vid:"),
                      subtitle: Text("${lineaObtenida?.vid?.description}",
                          style: TextStyle(fontSize: 18.0)),
                    ),
                    Center(
                      child: Row(
                        children: [
                          Text("    Recoleccion de linea:",
                              style: TextStyle(fontSize: 18.0)),
                          Switch(
                            value: harEnabled!,
                            onChanged: (value) async {
                              logger.d("Switch pulsado");
                              try {
                                if (value) {
                                  await api
                                      .enableLine(lineaId!)
                                      .timeout(Duration(seconds: 10));
                                } else {
                                  await api
                                      .disableLine(lineaId!)
                                      .timeout(Duration(seconds: 10));
                                }
                                setState(() {
                                  logger.d("Cambia a valor $value");
                                  primeraConstruccion = false;
                                  harEnabled = value;
                                });
                              } on TimeoutException {
                                snackTimeout(context);
                              } catch (e) {
                                snackRed(context,
                                    'Error al intentar modificar la linea');
                              }
                            },
                          ),
                          Visibility(
                            visible: harEnabled!,
                            child: Text("Habilitada",
                                style: TextStyle(color: Colors.lightBlue)),
                            replacement: Text("Deshabilitada",
                                style: TextStyle(color: Colors.red)),
                          )
                        ],
                      ),
                    ),
                    SizedBox(width: 96.0, height: 48),
                    Column(
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            ElevatedButton(
                                onPressed: () {
                                  logger.d("Modificar datos de zona pulsado");
                                  Navigator.push(
                                      context,
                                      MaterialPageRoute(
                                          builder: (context) => UpdateLine(
                                              lineId: lineaId,
                                              line: lineaObtenida!)));
                                },
                                child: Text("Actualizar Datos")),
                            SizedBox(width: 96.0),
                            ElevatedButton(
                                style: ElevatedButton.styleFrom(
                                    backgroundColor: Colors.red),
                                onPressed: () async {
                                  logger.d("Eliminar linea pulsado");
                                  bool res = await showDialog(
                                      context: context,
                                      builder: (BuildContext context2) =>
                                          AlertDialog(
                                            title: Text("Confirmación"),
                                            content: Text(
                                                "Esta seguro de eliminar la  linea?"),
                                            actions: [
                                              TextButton(
                                                onPressed: () {
                                                  logger.d("Cancelado");
                                                  Navigator.of(context2)
                                                      .pop(false);
                                                },
                                                child: Text('Cancelar'),
                                              ),
                                              TextButton(
                                                onPressed: () async {
                                                  logger.d("Eliminar linea");
                                                  try {
                                                    MessageResponseDTO?
                                                        response = await api
                                                            .deleteLine(
                                                                lineaId!)
                                                            .timeout(Duration(
                                                                seconds: 10));
                                                    logger.d(response);
                                                  } on TimeoutException {
                                                    snackTimeout(context);
                                                  } catch (e) {
                                                    snackRed(context,
                                                        'Error al intentar eliminar la zona');
                                                  }
                                                  Navigator.of(context2)
                                                      .pop(true);
                                                },
                                                child: Text('Eliminar'),
                                              ),
                                            ],
                                          ));
                                  if (res) {
                                    Navigator.pop(context);
                                  }
                                },
                                child: Text("Eliminar Linea")),
                          ],
                        ),
                      ],
                    ),
                    Column(
                      // AQUI MUESTRO UN QR Y CON LA OPCION DE DESCARGALO
                      children: <Widget>[
                        RepaintBoundary(
                          key: globalKey,
                          child: Container(
                            height: 200,
                            width: 200,
                            color: Colors.white,
                            child: CustomPaint(
                              size: Size(200, 200),
                              painter: QrPainter(
                                eyeStyle: const QrEyeStyle(
                                  eyeShape: QrEyeShape.square,
                                  color: Colors.black,
                                ),
                                dataModuleStyle: const QrDataModuleStyle(
                                  dataModuleShape: QrDataModuleShape.square,
                                  color: Colors.black,
                                ),
                                data:
                                    '${lineaObtenida!.zoneName},${lineaObtenida!.lineNumber}',
                                version: QrVersions.auto,
                              ),
                            ),
                          ),
                        ),
                        SizedBox(height: 20),
                        ElevatedButton(
                          onPressed: () async {
                            // ui.Image image = await boundary.toImage(pixelRatio: 5.0);
                            _captureAndSavePng(lineaObtenida!.id.toString());
                          },
                          child: Text('Descargar QR'),
                        ),
                      ],
                    )
                  ],
                ),
              );
            } else if (snapshot.hasError) {
              snackRed(context, 'Error obteniendo la linea');
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

  Future<void> _captureAndSavePng(String name) async {
    try {
      RenderRepaintBoundary boundary =
          globalKey.currentContext!.findRenderObject() as RenderRepaintBoundary;
      ui.Image image = await boundary.toImage(pixelRatio: 5);
      final ByteData? byteData =
          await image.toByteData(format: ui.ImageByteFormat.png);
      final Uint8List pngBytes = byteData!.buffer.asUint8List();

      final result = await ImageGallerySaver.saveImage(
        pngBytes,
        name: "QRCode$name",
        isReturnImagePathOfIOS: true,
        quality: 100,
      );

      logger.d("Image saved: $result");
      snackGreen(context, "Imagen descargada");
    } catch (e) {
      logger.d("Error al intentar guardar la imagen: $e");
      snackRed(context, 'Error al guardar la imagen');
    }
  }
}

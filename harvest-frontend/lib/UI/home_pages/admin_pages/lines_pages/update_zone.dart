import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:harvest_api/api.dart';
import 'package:harvest_frontend/utils/snack_bars.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../../../../utils/plataform_apis/lines_api.dart';
import '../../../../utils/provider/sign_in_model.dart';
import '../../../../utils/validators.dart';

class UpdateZone extends StatefulWidget {
  final int? zoneId;
  final ZoneDTO zone;

  UpdateZone({required this.zoneId, required this.zone});

  @override
  State<StatefulWidget> createState() => _UpdateZoneState();
}

class _UpdateZoneState extends State<UpdateZone> {
  var logger = Logger();
  final _form = GlobalKey<FormState>();

  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _descController = TextEditingController();
  final TextEditingController _surfaceController = TextEditingController();
  final TextEditingController _referenceController = TextEditingController();

  final RegExp _regex = RegExp(r'^[a-zA-Z0-9]{20}$');

  @override
  Widget build(BuildContext context) {
    final estado = Provider.of<SignInResponseModel>(context);
    OAuth auth = OAuth(accessToken: estado.lastResponse!.accessToken);
    final api = lineasApiPlataform(auth);

    ZoneDTO zoneInit = widget.zone;

    ZoneDTOFormationEnum opcion = zoneInit.formation;
    _nameController.text = zoneInit.name;
    _referenceController.text = zoneInit.reference;
    _descController.text = zoneInit.description;
    _surfaceController.text = zoneInit.surface.toString();

    logger.d(zoneInit);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Actualizar Zona '),
        backgroundColor: Colors.green,
      ),
      body: SingleChildScrollView(
        child: Form(
          key: _form,
          child: Column(
            children: [
              TextFormField(
                maxLength: 254,
                key: Key('nameKey'),
                controller: _nameController,
                decoration: InputDecoration(labelText: 'Nombre'),
                validator: (valor) {
                  if (valor!.isEmpty) {
                    return 'Ingresar Nombre';
                  }
                  valor = valor.trim();
                  if (valor.length >= 254 || !isNamesValid(valor)) {
                    return 'Ingresar Nombre valido';
                  }
                  return null;
                },
              ),
              TextFormField(
                maxLength: 1024,
                key: Key('descKey'),
                controller: _descController,
                decoration: InputDecoration(labelText: 'Descripción'),
                validator: (valor) {
                  if (valor!.isEmpty) {
                    return 'Ingresar Descripción';
                  }
                  valor = valor.trim();
                  if (valor.length > 1024) {
                    return 'Ingresar Descripción valido';
                  }
                  return null;
                },
              ),
              TextFormField(
                maxLength: 20,
                key: Key('refKey'),
                controller: _referenceController,
                decoration: InputDecoration(labelText: 'Referencia catastral'),
                validator: (valor) {
                  if (valor!.isEmpty) {
                    return 'Ingresar Referencia';
                  }
                  valor = valor.trim();
                  if (!_regex.hasMatch(valor)) {
                    return 'Ingresar Referencia valida';
                  }
                  return null;
                },
              ),
              TextFormField(
                maxLength: 6,
                key: Key('surfaceKey'),
                controller: _surfaceController,
                keyboardType: TextInputType.number,
                inputFormatters: <TextInputFormatter>[
                  FilteringTextInputFormatter.digitsOnly
                ],
                decoration: InputDecoration(
                    labelText: 'Superficie(en metros cuadrados)'),
                validator: (valor) {
                  if (valor!.isEmpty) {
                    return 'Ingresar numero ';
                  }
                  valor = valor.trim();
                  if (valor.length > 6 ||
                      (int.tryParse(_surfaceController.text)! <= 0)) {
                    return 'Ingresar superficie valida';
                  }
                  return null;
                },
              ),
              DropdownButtonFormField<ZoneDTOFormationEnum>(
                value: opcion,
                items: ZoneDTOFormationEnum.values.map((value) {
                  return DropdownMenuItem<ZoneDTOFormationEnum>(
                    value: value,
                    child: Text(value.toString()),
                  );
                }).toList(),
                onChanged: (ZoneDTOFormationEnum? value) {
                  logger.d('Elegido elemento $value');
                  setState(() {
                    opcion = value!;
                  });
                },
              ),
              ElevatedButton(
                  key: Key('okButtonKey'),
                  onPressed: () async {
                    if (_form.currentState!.validate()) {
                      _form.currentState!.save();
                      logger.d('Boton de añadir zona pulsado');
                      ZoneDTO nuevaZona = ZoneDTO(
                          name: _nameController.text,
                          surface: int.tryParse(_surfaceController.text)!,
                          description: _descController.text,
                          formation: opcion,
                          reference: _referenceController.text);

                      logger.d(nuevaZona);
                      try {
                        MessageResponseDTO? response = await api
                            .updateZone(widget.zoneId!, nuevaZona)
                            .timeout(Duration(seconds: 10));
                        logger.d('Respuesta:');
                        logger.d(response);
                        snackGreen(context, 'Zona actualizada correctamente.');
                      } on TimeoutException {
                        snackTimeout(context);
                      } catch (e) {
                        snackRed(context,
                            'Error al dar intentar actualizar la zona.');
                      }
                    }
                    Navigator.pop(context);
                  },
                  child: Text('Actualizar Zona')),
            ],
          ),
        ),
      ),
    );
  }
}

import 'package:harvest_api/api.dart';

bool esAdmin(SignInResponseDTO? responseDTO) {
  if (responseDTO!.roles.contains("ROLE_ADMIN")) {
    return true;
  } else {
    return false;
  }
}

bool esCapataz(SignInResponseDTO? responseDTO) {
  if (responseDTO!.roles.contains("ROLE_CAPATAZ")) {
    return true;
  } else {
    return false;
  }
}

bool esTractorista(SignInResponseDTO? responseDTO) {
  if (responseDTO!.roles.contains("ROLE_TRACTORISTA")) {
    return true;
  } else {
    return false;
  }
}

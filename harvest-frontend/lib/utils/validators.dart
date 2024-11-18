// bool isEmailValid(String email) {
//   final regEx = RegExp(
//       r'^[a-zA-Z0-9]+(?:\.[a-zA-Z0-9]+)*@[a-zA-Z0-9]+(?:\.[a-zA-Z0-9]+)*$');
//
//   return regEx.hasMatch(email);
// }
//
// bool isPhoneValid(String phone) {
//   final regEx = RegExp(r'(\+34|0034|34)?[ -]*(6|7)[ -]*([0-9][ -]*){8}');
//   return regEx.hasMatch(phone);
// }

bool isDNIValid(String dni) {
  final regEx = RegExp(r'^[0-9]{8}[A-Z]$');
  return regEx.hasMatch(dni);
}

bool isNSSValid(String nss) {
  final regEx = RegExp(r'^[0-9]{12}$');
  return regEx.hasMatch(nss);
}

bool isNamesValid(String nss) {
  final regEx = RegExp(r'^[a-zA-Z]+');
  return regEx.hasMatch(nss);
}

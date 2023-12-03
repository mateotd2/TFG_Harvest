import 'package:flutter/material.dart';

TimeOfDay stringToTimeOfDay(String string) {
  return TimeOfDay(
      hour: int.parse(string.split(":")[0]),
      minute: int.parse(string.split(":")[1]));
}

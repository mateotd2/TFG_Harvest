import 'package:flutter/material.dart';

TimeOfDay StringToTimeOfDay(String string) {
  return TimeOfDay(
      hour: int.parse(string.split(":")[0]),
      minute: int.parse(string.split(":")[1]));
}

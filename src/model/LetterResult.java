package model;

public enum LetterResult {
    CORRECT,    // letra correcta en posición correcta
    MISPLACED,  // letra en la palabra, posición incorrecta
    ABSENT      // letra no está en la palabra
}
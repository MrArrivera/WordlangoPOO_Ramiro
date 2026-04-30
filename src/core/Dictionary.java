package core;

import model.Language;
import model.WordEntry;

import java.io.*;
import java.util.*;

public class Dictionary {

    private final HashMap<String, WordEntry> words = new HashMap<>();

    public Dictionary(String filePath) {
        loadFromFile(filePath);
    }

    // --- Carga desde archivo ---
    private void loadFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream(filePath),
                        "UTF-8"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length < 3) continue;

                String word       = parts[0].trim().toLowerCase();
                String definition = parts[1].trim();
                Language lang     = parts[2].trim().equals("es")
                        ? Language.SPANISH
                        : Language.ENGLISH;

                words.put(word, new WordEntry(definition, word.length(), lang));
            }
            System.out.println("Diccionario cargado: " + words.size() + " palabras.");

        } catch (IOException e) {
            System.err.println("Error al cargar el diccionario: " + e.getMessage());
        }
    }

    // --- Verificar si una palabra existe ---
    public boolean contains(String word) {
        return words.containsKey(word.toLowerCase());
    }

    // --- Obtener definición ---
    public String getDefinition(String word) {
        WordEntry entry = words.get(word.toLowerCase());
        return entry != null ? entry.getDefinition() : "Definición no encontrada.";
    }

    // --- Agregar palabra nueva al diccionario ---
    public void addWord(String word, String definition, Language language) {
        word = word.toLowerCase();
        words.put(word, new WordEntry(definition, word.length(), language));
    }

    // --- Obtener palabra aleatoria por longitud e idioma ---
    public String getRandomWord(int length, Language language) {
        List<String> candidates = new ArrayList<>();

        // Lambda #1: filter con stream para obtener candidatos
        words.entrySet().stream()
                .filter(e -> e.getValue().getLength() == length
                        && e.getValue().getLanguage() == language)
                .forEach(e -> candidates.add(e.getKey()));

        if (candidates.isEmpty()) {
            throw new IllegalStateException(
                    "No hay palabras de longitud " + length + " en el diccionario.");
        }

        Random random = new Random();
        return candidates.get(random.nextInt(candidates.size()));
    }

    // --- Obtener una consonante de la palabra secreta (para pistas) ---
    public char getRandomConsonant(String secretWord) {
        String consonants = "bcdfghjklmnñpqrstvwxyz";
        List<Character> found = new ArrayList<>();

        Iterator<Character> it = new ArrayList<Character>() {{
            for (char c : secretWord.toCharArray()) add(c);
        }}.iterator();

        while (it.hasNext()) {
            char c = it.next();
            if (consonants.indexOf(c) >= 0 && !found.contains(c)) {
                found.add(c);
            }
        }

        if (found.isEmpty()) return '?';
        return found.get(new Random().nextInt(found.size()));
    }

    // --- Obtener una vocal de la palabra secreta (para pistas) ---
    public char getRandomVowel(String secretWord) {
        String vowels = "aeiouáéíóúü";
        List<Character> found = new ArrayList<>();

        Iterator<Character> it = new ArrayList<Character>() {{
            for (char c : secretWord.toCharArray()) add(c);
        }}.iterator();

        while (it.hasNext()) {
            char c = it.next();
            if (vowels.indexOf(c) >= 0 && !found.contains(c)) {
                found.add(c);
            }
        }

        if (found.isEmpty()) return '?';
        return found.get(new Random().nextInt(found.size()));
    }

    // --- Tamaño del diccionario ---
    public int size() {
        return words.size();
    }

    public boolean existsInLanguage(String word, Language language) {
        word = word.toLowerCase();
        // Lambda: buscar en el mapa si existe la palabra en ese idioma específico
        String finalWord = word;
        return words.entrySet().stream()
                .anyMatch(e -> e.getKey().equals(finalWord)
                        && e.getValue().getLanguage() == language);
    }
}
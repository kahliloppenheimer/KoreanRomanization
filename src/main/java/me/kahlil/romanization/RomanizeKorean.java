package me.kahlil.romanization;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.crizin.KoreanRomanizer;

public class RomanizeKorean {

  private static final Map<String, String> CHAR_SUBSTITUTIONS =
      Map.of(
          "…", "...",
          "’", "'",
          "‘", "'",
          "•", " ",
          "–", "-",
          "\u00AD", "-",
          "\t", " ",
          "\\n\\s+", "\n");

  private static final Set<Pattern> IGNORED_LINES =
      Set.of(
          // These are chapter header in the book format and should be ignored.
          Pattern.compile("^CAPITOLO[ \t]*[0-9]+.*", Pattern.MULTILINE));

  private static final Pattern MULTI_LINE =
      Pattern.compile(
          "^(?!CAPITOLO)([0-9a-zA-ZéÀóíá¿¡βðɵ,.'/:;()?!_+=<>{}@#$%&^*`\\- \\[\\]]+)\\s*\\n\\s*([0-9a-zA-Z,.'/:;()?!_+=<>{}@#$%&^*`\\- \\[\\]~\uAC00-\uD7A3]+)[ \t]*$",
          Pattern.MULTILINE);

  private static final Predicate<String> HAS_ENGLISH =
      Pattern.compile(".*[a-zA-Z]+.*").asPredicate();

  private static final Predicate<String> HAS_KOREAN =
      Pattern.compile(".*[\uAC00-\uD7A3]+.*").asPredicate();

  private static final Pattern LINE_REGEX =
      Pattern.compile(
          "^(?!CAPITOLO)([0-9a-zA-ZéÀóíá¿¡βðɵ,.'/:;()?!_+=<>{}@#$%&^*`\\- \\[\\]]+)[ \t]*([0-9a-zA-Z,.'/:;()?!_+=<>{}@#$%&^*`\\- \\[\\]~\uAC00-\uD7A3]+)[ \t]*$",
          Pattern.MULTILINE);

  private static int romanizedCount = 0;

  public static void main(String[] args) throws IOException {
    String inputFile = args[0];
    System.out.println("Input file: " + inputFile);

    String normalized = normalize(Files.readString(Path.of(inputFile)));
    System.out.println("Normalized line count: " + normalized.split("\n").length);

    long linesWithEnglish = Arrays.stream(normalized.split("\n+")).filter(HAS_ENGLISH).count();
    System.out.println("Lines with english: " + linesWithEnglish);

    long linesWithKorean = Arrays.stream(normalized.split("\n+")).filter(HAS_KOREAN).count();
    System.out.println("Lines with Korean: " + linesWithKorean);

    long ignoredLines =
        Arrays.stream(normalized.split("\n+"))
            .filter(
                line ->
                    IGNORED_LINES.stream()
                        .anyMatch(ignoredLine -> ignoredLine.matcher(line).matches()))
            .count();
    System.out.println("Lines ignored: " + ignoredLines);

    // lines w/ multiple matches
    System.out.println("Lines w/ multiple translations: ");
    Arrays.stream(normalized.split("\n+"))
        .map(
            line ->
                LINE_REGEX
                    .matcher(line)
                    .results()
                    .map(MatchResult::group)
                    .collect(Collectors.toList()))
        .filter(list -> list.size() > 1)
        .forEach(System.out::println);

    String romanized =
        Arrays.stream(normalized.split("\\n+"))
            .map(
                line -> {
                  if (IGNORED_LINES.stream()
                      .anyMatch(ignoredLine -> ignoredLine.matcher(line).matches())) {
                    return line;
                  }
                  return addRomanizationToLine(line);
                })
            .collect(Collectors.joining("\n\n"));

    System.out.println("Romanized line count: " + romanizedCount);
    String outputFilePath = args[0].split("\\.")[0] + "-romanization.txt";
    // Delete if it already exists and write over it.
    File outputFile = new File(outputFilePath);
    if (outputFile.exists()) {
      outputFile.delete();
    }
    Files.writeString(Path.of(outputFilePath), romanized);
  }

  private static String normalize(String contents) {
    for (String incorrect : CHAR_SUBSTITUTIONS.keySet()) {
      contents = contents.replaceAll(incorrect, CHAR_SUBSTITUTIONS.get(incorrect));
    }
    // Replace multi-line with single line
    return MULTI_LINE
        .matcher(contents)
        .replaceAll(
            result -> {
              String english = result.group(1).trim();
              String hangul = result.group(2).trim();
              return String.format("%s\t%s", english, hangul);
            });
  }

  private static String addRomanizationToLine(String line) {
    Matcher matcher = LINE_REGEX.matcher(line);
    if (!matcher.matches()) {
      System.out.println("Unmatched: " + line);
      return line;
    }
    romanizedCount++;
    String english = matcher.group(1).trim();
    String whitespace = line.contains("\t") ? "\n" : " ";
    String hangul = matcher.group(2).trim();
    String romanization = String.format("(%s)", romanize(hangul));

    return String.join(whitespace, List.of(english, hangul, romanization));
  }

  private static String romanize(String korean) {
    return KoreanRomanizer.romanize(korean).toLowerCase();
  }
}

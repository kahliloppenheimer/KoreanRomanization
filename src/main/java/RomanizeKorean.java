import com.github.kimkevin.hangulparser.HangulParserException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
          "\t", " ",
          "\\n\\s+", "\n"
      );

  private static final String ENGLISH_PATTERN = "0-9a-zA-Z,.'/:;()?!_+=<>{}@#$%&^*`\\- \\[\\]";
  private static final String KOREAN_PATTERN = ENGLISH_PATTERN + "~\uAC00-\uD7A3";
  private static final Pattern MULTI_LINE =
      Pattern.compile(String.format("([%s]+)\\s*\\n\\s*([%s]+)", ENGLISH_PATTERN, KOREAN_PATTERN));

  private static final Predicate<String> HAS_ENGLISH = Pattern.compile(".*[a-zA-Z]+.*").asPredicate();

  private static final Predicate<String> HAS_KOREAN = Pattern.compile(".*[\uAC00-\uD7A3]+.*").asPredicate();

  private static final Pattern LINE_REGEX =
      Pattern.compile(String.format("([%s]+)\\s*([%s]+)\\s+", ENGLISH_PATTERN, KOREAN_PATTERN));

  private static int romanizedCount = 0;

  public static void main(String[] args) throws IOException {
    String inputFile = args[0];
    System.out.println("Input file: " + inputFile);

    String normalized = normalize(Files.readString(Path.of(inputFile)));
    System.out.println("Normalized line count: " + normalized.split("\n").length);

    long linesWithEnglish = Arrays.stream(normalized.split("\n+"))
        .filter(HAS_ENGLISH)
        .count();
    System.out.println("Lines with english: " + linesWithEnglish);

    long linesWithKorean = Arrays.stream(normalized.split("\n+"))
        .filter(HAS_KOREAN)
        .count();
    System.out.println("Lines with Korean: " + linesWithKorean);

    LINE_REGEX.matcher(normalized).results()
        .map(MatchResult::group)
        .filter(line -> !HAS_KOREAN.test(line))
        .filter(line -> !HAS_ENGLISH.test(line))
        .forEach(System.out::println);

    String romanized = LINE_REGEX.matcher(normalized).results()
        .map(MatchResult::group)
        .map(RomanizeKorean::addRomanizationToLine)
        .collect(Collectors.joining("\n\n"));

    System.out.println("Romanized line count: " + romanizedCount);
    File outputFile = new File(args[0].split("\\.")[0] + "-translations.txt");
    if (outputFile.exists()) {
      outputFile.delete();
    }

    Files.writeString(Path.of(args[0].split("\\.")[0] + "-translations.txt"), romanized);
  }

  private static String normalize(String contents) {
    for (String incorrect : CHAR_SUBSTITUTIONS.keySet()) {
      contents = contents.replaceAll(incorrect, CHAR_SUBSTITUTIONS.get(incorrect));
    }
    // Replace multi-line with single line
    return MULTI_LINE.matcher(contents)
        .replaceAll(result -> {
          String english = result.group(1).trim();
          String hangul = result.group(2).trim();
          return String.format("%s\t%s", english, hangul);
        });
  }

  private static String addRomanizationToLine(String line) {
    Matcher matcher = LINE_REGEX.matcher(line);
    if (!matcher.matches()) {
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

  private static boolean hasEnglishAndKorean(String line) throws HangulParserException {
    return true;
  }

}

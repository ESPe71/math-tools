package de.penetti.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class Base64Test {
  @Test
  void testBase64() {
    String str = "Enrico S. Penetti";
    String encoded = Base64.encode(str);
    String decoded = Base64.decode(encoded);
    assertEquals(str, decoded);
  }

  @Test
  void testBase64Cat() throws IOException, URISyntaxException {
    URL catUrl = getClass().getResource("/cat.jpg");
    Path catFile = Paths.get(catUrl.toURI());

    Files.deleteIfExists(Paths.get("target/cat.txt"));
    Files.deleteIfExists(Paths.get("target/cat.jpg"));

    Base64.main("-encode", catFile.toString(), "target/cat.txt");

    byte[] decodedCat = Base64.decode(Paths.get("target/cat.txt"));
    byte[] originalCat = Files.readAllBytes(catFile);

    assertTrue(Arrays.equals(originalCat, decodedCat));


    Base64.main("-decode", "target/cat.txt", "target/cat.jpg");
    decodedCat = Files.readAllBytes(Paths.get("target/cat.jpg"));

    assertTrue(Arrays.equals(originalCat, decodedCat));
  }
}
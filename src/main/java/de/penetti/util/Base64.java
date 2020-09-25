package de.penetti.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;

public class Base64 {

  public static String encode(String data) {
    byte[] encoded = encode(data.getBytes());
    String encodedString = new String(encoded, 0, encoded.length);
    return encodedString;
  }
  public static byte[] encode(byte[] data) {
    java.util.Base64.Encoder encoder = java.util.Base64.getUrlEncoder();
    return encoder.encode(data);
  }
  public static byte[] encode(Path file) throws IOException {
    return encode(Files.readAllBytes(file));
  }
  public static void encode(Path inFile, Path outFile) throws IOException {
    Files.write(outFile, encode(inFile));
  }

  public static String decode(String data) {
    byte[] decoded = decode(data.getBytes());
    String decodedString = new String(decoded, 0, decoded.length);
    return decodedString;
  }
  public static byte[] decode(byte[] data) {
    java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
    return decoder.decode(data);
  }
  public static byte[] decode(Path file) throws IOException {
    return decode(Files.readAllBytes(file));
  }
  public static void decode(Path inFile, Path outFile) throws IOException {
    Files.write(outFile, decode(inFile));
  }

  public static void main(String... args) throws IOException {
    if(args.length != 3) {
      System.out.println("<-encode | -decode> <inFile> <outFile>");
      return;
    }

    String cmd = args[0];
    Path inFile = Paths.get(args[1]);
    Path outFile = Paths.get(args[2]);

    if(Files.notExists(inFile)) {
      throw new FileNotFoundException(inFile.toString());
    }
    if(Files.exists(outFile)) {
      throw new FileAlreadyExistsException(outFile.toString());
    }

    if(cmd.equals("-encode")) {
      encode(inFile, outFile);
    }
    else if(cmd.equals("-decode")) {
      decode(inFile, outFile);
    }
    else {
      System.out.println("<-encode | -decode> <inFile> <outFile>");
      return;
    }
  }
}

package com.example.qa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QaGen {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java -jar qa-gen.jar <JavaFile> [--model name] [--run]");
            return;
        }

        Path source = Path.of(args[0]);
        String model = "qwen3:1.7b";
        boolean runTests = false;

        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--model") && i + 1 < args.length) model = args[++i];
            else if (args[i].equals("--run")) runTests = true;
        }

        // 1. Read the class
        String code = Files.readString(source);
        String className = source.getFileName().toString().replace(".java", "");
        String pkg = findPackage(code);

        // 2. Build the prompt
        String prompt = """
            Write a JUnit 5 test class named %sTest for the Java class below.
            Rules:
            - Package: %s
            - Use org.junit.jupiter.api.Test and static imports from org.junit.jupiter.api.Assertions
            - Cover normal cases AND edge cases (zero, negative numbers, large values, exceptions)
            - Return ONLY Java code, no explanations

            %s
            """.formatted(className, pkg.isEmpty() ? "(none)" : pkg, code);

        // 3. Call Ollama
        System.out.println("Asking " + model + " ... (can take 30-120 seconds on CPU)");
        String raw = askOllama(model, prompt);
        String testCode = extractCode(raw);

        // 4. Save to src/test/java/<package>/<Class>Test.java
        Path outDir = Path.of("src/test/java", pkg.replace('.', '/'));
        Files.createDirectories(outDir);
        Path outFile = outDir.resolve(className + "Test.java");
        Files.writeString(outFile, testCode);
        System.out.println("Saved: " + outFile);

        // 5. Optionally run the tests
        if (runTests) runMaven();
    }

    static String askOllama(String model, String prompt) throws Exception {
        ObjectNode body = MAPPER.createObjectNode();
        body.put("model", model);
        body.put("prompt", prompt);
        body.put("stream", false);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_URL))
                .timeout(Duration.ofMinutes(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(body)))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Ollama error " + response.statusCode() + ": " + response.body());
        }
        JsonNode json = MAPPER.readTree(response.body());
        return json.get("response").asText();
    }

    // Models often wrap code in ```java ... ```; remove that
    static String extractCode(String text) {
        text = text.replaceAll("(?s)<think>.*?</think>", "").trim();   // add this line
        Matcher m = Pattern.compile("```(?:java)?\\s*(.*?)```", Pattern.DOTALL).matcher(text);
        return m.find() ? m.group(1).trim() + "\n" : text.trim() + "\n";
    }

    static String findPackage(String code) {
        Matcher m = Pattern.compile("^\\s*package\\s+([\\w.]+)\\s*;", Pattern.MULTILINE).matcher(code);
        return m.find() ? m.group(1) : "";
    }

    static void runMaven() throws Exception {
        String mvn = System.getProperty("os.name").toLowerCase().contains("win") ? "mvn.cmd" : "mvn";
        System.out.println("Running tests...");
        int exit = new ProcessBuilder(mvn, "test").inheritIO().start().waitFor();
        System.out.println(exit == 0 ? "ALL TESTS PASSED" : "SOME TESTS FAILED (see output above)");
    }
}

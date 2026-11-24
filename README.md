Ollama Java QA

My first QA project. A small Java program that asks a local AI model (Ollama) to write JUnit tests for a Java class. It works offline, on my laptop, with no GPU.

How it works
Give it a Java file, like Calculator.java.
It sends the code to Ollama with the prompt "write JUnit tests".
The AI replies with test code.
The program saves it as CalculatorTest.java.
I run the tests and check the results.
What you need
Java 17+
Maven (or IntelliJ)
Ollama and a small model:
ollama pull qwen3:1.7b
How to run
mvn clean package -DskipTests
java -jar target/qa-gen.jar src/main/java/com/example/Calculator.java --model qwen3:1.7b
mvn test

In IntelliJ, put this in Program arguments:

src/main/java/com/example/Calculator.java --model qwen3:1.7b

It can take a few minutes. The console looks stuck, but it is just waiting for the AI.

Result

Model qwen3:1.7b wrote 19 tests. 14 passed, 5 failed.

All 5 failures were wrong tests, not wrong code. The AI made maths mistakes (like -4 x -4 = -16). I fixed them by hand and added one overflow test. Now all 20 pass.

What I learned
AI writes good test structure but gets expected values wrong.
A human must check AI tests.
add and multiply can overflow with big numbers. Math.addExact and Math.multiplyExact would be safer.
Next
Try more models
Send failed tests back to the AI to fix
Run tests with GitHub Actions

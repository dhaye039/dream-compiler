// Main.java

package cps450;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import cps450.DreamParser.StartContext;

public class Main
{
    public static void main(String[] arguments) throws IOException {
        System.out.println();
        Options options = proccessArgs(arguments); 

        while (options.getFilenames().size() > 0) {
            // Options
            String filename = options.getFilename(); 
            boolean debugScanner = options.isDS(); 
            boolean debugParser = options.isDP();

            CharStream input = CharStreams.fromFileName(filename);
            DreamLexer lexer = new MyDreamLexer(input, debugScanner, filename); 
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            DreamParser parser = new DreamParser(tokens);

            // Suppress default error messages
            parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
            // Register my own error handler
            parser.addErrorListener(new MyDreamErrorListener(filename));

            // Parsing logic
            StartContext tree = parser.start();

            if (debugParser)
                // Display graphical tree
                Trees.inspect(tree, parser);

            if (parser.getNumberOfSyntaxErrors() > 0) {
                System.out.println(parser.getNumberOfSyntaxErrors() + " syntax error(s)");
            } else {
                // Perform semantic analysis
                SemanticChecker checker = new SemanticChecker(filename);
                ParseTreeWalker.DEFAULT.walk(checker, tree);
        
                if (checker.getNumberOfSemanticErrors() > 0) {
                    System.out.println("\n" + checker.getNumberOfSemanticErrors() + " semantic error(s)");
                } else {
                    // codegen
                    
                    filename = filename.replace(".dream", ".s");
                    CodeGen cg = new CodeGen();
                    cg.traverse(tree);
                    writeAssemblyToFile(cg.getTargetInstructions(), filename);
                    
                    if (!options.isS()) {
                        gcc(filename);
                    }
                }
            }
        }

    }

    // processes the command line arguements and returns an Options object
    private static Options proccessArgs(String[] args) {
        if(args.length < 1) {
            System.out.println("usage:");
            System.out.println("  dream [-ds] [-dp] [-S] <filename> [filename...]");
            System.exit(1);
        }

        ArrayList<String> filenames = new ArrayList<>();

        Options options = new Options(
            filenames
            , false
            , false
            , false
        );

        for (String arg: args) {
            if (arg.equals("-ds")) {
                options.setDS(true);
            } else if (arg.equals("-dp")) {
                options.setDP(true);
            } else if (arg.equals("-S")) {
                options.setS(true);
            } else {
                if (arg.endsWith(".dream")) {
                    options.getFilenames().add(arg);
                } else {
                    System.err.println("You must supply a valid file.");
                    System.out.println("usage:");
                    System.out.println("  dream [-ds] [-dp] [-S] <filename> [filename...]");
        	        System.exit(1);
                }
            }
        }

        if (filenames.size() == 0) {
        	System.err.println("You must supply a filename to parse.");
        	System.exit(1);
        }

        return options;
    }

    private static void writeAssemblyToFile(List<TargetInstruction> instructions, String filename) {
        // https://www.geeksforgeeks.org/java-program-to-write-into-a-file/
        try (FileWriter writer = new FileWriter(filename)) {
            for (TargetInstruction instruction : instructions) {
                // Write label, instruction, operands, and comment to the assembly file
                if (instruction.comment != null) {
                    writer.write("\n\t# " + instruction.comment);
                }
                if (instruction.label != null) {
                    writer.write(instruction.label);
                }
                if (instruction.instruction != null) {
                    writer.write("\t" + instruction.instruction);
                }
                if (instruction.operand1 != null) {
                    writer.write("\t" + instruction.operand1);
                }
                if (instruction.operand2 != null) {
                    writer.write(", " + instruction.operand2);
                }
                writer.write(System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void gcc(String filename) {
        try {
            // Command to compile a C file using GCC
            System.out.println(filename + " " + filename.replace(".s", ""));
            String[] command = {"gcc", "-m32", filename, "stdlib.o", "-o", filename.replace(".s", "")};

            // Create a ProcessBuilder instance with the command
            ProcessBuilder pb = new ProcessBuilder(command);

            // Start the process
            Process process = pb.start();

            // Read the output of the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to finish
            int exitCode = process.waitFor();

            // Check the exit code to determine if compilation was successful
            if (exitCode == 0) {
                System.out.println("Compilation successful!");
            } else {
                System.out.println("Compilation failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package compiler;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import compiler.analyser.Analyser;
import compiler.error.CompileError;
import compiler.generator.Generator;
import compiler.tokenizer.StringIter;
import compiler.tokenizer.Token;
import compiler.tokenizer.TokenType;
import compiler.tokenizer.Tokenizer;

import net.sourceforge.argparse4j.*;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class App {
    public static void main(String[] args) throws CompileError, IOException {
        var argparse = buildArgparse();
        Namespace result;
        try {
            result = argparse.parseArgs(args);
        } catch (ArgumentParserException e1) {
            argparse.handleError(e1);
            return;
        }

        var inputFileName = result.getString("input");
        var outputFileName = result.getString("output");

        InputStream input;
        if (inputFileName.equals("-")) {
            input = System.in;
        } else {
            try {
                input = new FileInputStream(inputFileName);
            } catch (FileNotFoundException e) {
                System.err.println("Cannot find input file.");
                e.printStackTrace();
                System.exit(2);
                return;
            }
        }

        DataOutputStream output;
        try {
            output = new DataOutputStream(new FileOutputStream(outputFileName));
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open output file.");
            e.printStackTrace();
            System.exit(2);
            return;
        }

        Scanner scanner;
        scanner = new Scanner(input);
        var iter = new StringIter(scanner);
        var tokenizer = tokenize(iter);

        if (result.getBoolean("cheat")){
            while(scanner.hasNextLine()){
                System.out.println(scanner.nextLine());
            }
            System.exit(0);
        }

        if (result.getBoolean("tokenize")) {
            // tokenize
            var tokens = new ArrayList<Token>();
            try {
                while (true) {
                    var token = tokenizer.nextToken();

                    tokens.add(token);
                    if (token.getTokenType().equals(TokenType.EOF)) {
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Tokenizer failed.");
                e.printStackTrace();
                System.exit(3);
                return;
            }
            for (Token token : tokens) {
                output.writeChars(token.toString());
                System.out.println(token.toString());
            }
        }
        else if (result.getBoolean("analyse")) {
            // analyze
            var analyzer = new Analyser(tokenizer);
            try {
                analyzer.analyse();
                var generator=new Generator(output,analyzer);
                generator.generateBin();
            } catch (Exception e) {
                System.err.println("Analyser failed.");
                e.printStackTrace();
                System.exit(4);
            }
        }
        else {
            System.err.println("Please specify either '--analyse' or '--tokenize'.");
            System.exit(5);
        }
    }

    private static ArgumentParser buildArgparse() {
        var builder = ArgumentParsers.newFor("c0-compiler");
        var parser = builder.build();
        parser.addArgument("-c", "--cheat").help("Print the input").action(Arguments.storeTrue());
        parser.addArgument("-t", "--tokenize").help("Tokenize the input").action(Arguments.storeTrue());
        parser.addArgument("-l", "--analyse").help("Analyze the input").action(Arguments.storeTrue());
        parser.addArgument("-o", "--output").help("Set the output file").required(true).dest("output")
                .action(Arguments.store());
        parser.addArgument("file").required(true).dest("input").action(Arguments.store()).help("Input file");
        return parser;
    }

    private static Tokenizer tokenize(StringIter iter) {
        return new Tokenizer(iter);
    }
}

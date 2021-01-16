package miniplc0java;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.vm.MiniVm;

public class MiniVmTest {

    private String RunVm(List<Instruction> instructions) {
        var utf8 = java.nio.charset.StandardCharsets.UTF_8;
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        var output = new PrintStream(ostream, true, utf8);

        var vm = new MiniVm(instructions, output);
        vm.Run();
        output.close();

        var outString = ostream.toString(utf8);
        return outString;
    }



}

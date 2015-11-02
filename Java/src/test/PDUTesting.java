package test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import network.pdu.ByteSequenceBuilder;
import network.pdu.OpCode;
import network.pdu.types.NicksPDU;
import network.pdu.types.UCNickPDU;
import network.pdu.types.UJoinPDU;
import network.pdu.types.ULeavePDU;

import org.junit.Test;

import com.sun.imageio.plugins.common.InputStreamAdapter;

public class PDUTesting {

    @Test
    public void testNicksPDU() {

        byte[] bytes = buildWorkingNicksPDU();
 
        NicksPDU nicksPDU;
        try {
            nicksPDU = new NicksPDU(new ByteArrayInputStream(bytes));
            assertTrue((nicksPDU.getError() == null));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

       

        bytes = buildNotWokringNcksPDU();
        
        try {
            nicksPDU = new NicksPDU(new ByteArrayInputStream(bytes));
            assertFalse(nicksPDU.getError() != null);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private byte[] buildWorkingNicksPDU() {

        String nick1 = "OP";
        String nick2 = "KALLE";
        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.NICKS.value);
        builder.append((byte) 2);
        builder.appendShort((short) (int) 9);

        builder.append(nick1.getBytes());
        builder.append((byte)0);
        builder.append(nick2.getBytes());
        builder.append((byte) 0);
        builder.pad();

        return builder.toByteArray();

    }

    private byte[] buildNotWokringNcksPDU() {
        String nick1 = "OPS";
        String nick2 = "KALLE";
        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.NICKS.value);
        builder.append((byte) 2);
        builder.appendShort((short) 9);

        builder.append(nick1.getBytes());
        builder.append((byte)0);
        builder.append(nick2.getBytes());
        builder.append((byte) 0);
        builder.pad();

        return builder.toByteArray();

    }
//
//    @Test
//    public void testULeavePDU() {
//        byte[] bytes = buildWorkingULeavePPDU();
//
//        ULeavePDU pdu = new ULeavePDU(bytes);
//        assertTrue(pdu.isValid());
//
//        bytes = buildNotWorkingULeavePPDU();
//        pdu = new ULeavePDU(bytes);
//        assertFalse(pdu.isValid());
//
//    }
//
//
//    private byte[] buildWorkingULeavePPDU() {
//        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.ULEAVE.value);
//
//        builder.append((byte) 3);
//        builder.pad();
//        builder.appendInt(0);
//        builder.append("TIM".getBytes());
//        builder.pad();
//
//        return builder.toByteArray();
//    }
//
//    private byte[] buildNotWorkingULeavePPDU() {
//        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.ULEAVE.value);
//
//        builder.append((byte) 3);
//        builder.pad();
//        builder.appendInt(0);
//        builder.append("TIMS".getBytes());
//        builder.pad();
//
//        return builder.toByteArray();
//    }
//
//    @Test
//    public void testUJoinPDU() {
//        byte[] bytes = buildWorkingUJoinPDU();
//        UJoinPDU pdu = new UJoinPDU(bytes);
//
//        assertTrue(pdu.isValid());
//
//        bytes = buildNotWorkingUJoinPDU();
//        pdu = new UJoinPDU(bytes);
//
//        assertFalse(pdu.isValid());
//
//
//    }
//
//    private byte[] buildWorkingUJoinPDU() {
//
//        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.UJOIN.value);
//        builder.append((byte) "TIM".length());
//        builder.pad();
//        builder.appendInt(0);
//        builder.append("TIM".getBytes());
//        builder.pad();
//
//
//        return builder.toByteArray();
//    }
//
//    private byte[] buildNotWorkingUJoinPDU() {
//
//        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.UJOIN.value);
//        builder.append((byte) "TIM".length());
//        builder.pad();
//        builder.appendInt(0);
//        builder.append("TIMS".getBytes());
//        builder.pad();
//
//        return builder.toByteArray();
//
//    }
//
//    @Test
//    public void testUCNickPDU() {
//        byte[] bytes = buildWorkingUCNickPDU1();
//        UCNickPDU pdu = new UCNickPDU(bytes);
//        assertTrue(pdu.isValid());
//
//        bytes = buildWorkingUCNickPDU2();
//        assertTrue(pdu.isValid());
//
//        bytes = buildNotWorkingUCNickPDU1();
//        pdu = new UCNickPDU(bytes);
//        assertFalse(pdu.isValid());
//
//        bytes = buildNotWorkingUCNickPDU2();
//        pdu = new UCNickPDU(bytes);
//        assertFalse(pdu.isValid());
//
//        bytes = buildNotWorkingUCNickPDU3();
//        pdu = new UCNickPDU(bytes);
//        assertFalse(pdu.isValid());
//
//    }
//
//    private byte[] buildWorkingUCNickPDU1() {
//        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.UCNICK.value);
//        builder.append((byte) "TIM".length());
//        builder.append((byte) "TIM".length());
//        builder.pad();
//        builder.appendInt(0);
//
//        builder.append("TIM".getBytes());
//        builder.pad();
//        builder.append("TIM".getBytes());
//        builder.pad();
//        return builder.toByteArray();
//    }
//
//    private byte[] buildWorkingUCNickPDU2() {
//        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.UCNICK.value);
//        builder.append((byte) "TIM".length());
//        builder.append((byte) "TIMS".length());
//        builder.pad();
//        builder.appendInt(0);
//
//        builder.append("TIM".getBytes());
//        builder.pad();
//        builder.append("TIMS".getBytes());
//        builder.pad();
//        return builder.toByteArray();
//    }
//
//
//    private byte[] buildNotWorkingUCNickPDU1() {
//        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.UCNICK.value);
//        builder.append((byte) "TIM".length());
//        builder.append((byte) "TIMSS".length());
//        builder.pad();
//        builder.appendInt(0);
//
//        builder.append("TIM".getBytes());
//        builder.pad();
//        builder.append("TIMSS".getBytes());
//        //builder.pad();                            //Missing pad
//        return builder.toByteArray();
//    }
//
//    private byte[] buildNotWorkingUCNickPDU2() {
//        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.UCNICK.value);
//        builder.append((byte) "TIM".length());
//        builder.append((byte) "TIMS".length());
//        builder.pad();
//        builder.appendInt(0);
//
//        builder.append("TIM".getBytes());
//        //builder.pad();                              //Missing pad
//        builder.append("TIMS".getBytes());
//        builder.pad();
//        return builder.toByteArray();
//    }
//
//    private byte[] buildNotWorkingUCNickPDU3() {
//
//        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.UCNICK.value);
//        builder.append((byte) "TIM".length());
//        builder.append((byte) "TIMSS".length());       //Wrongsize
//        builder.pad();
//        builder.appendInt(0);
//
//        builder.append("TIM".getBytes());
//        builder.pad();
//        builder.append("TIMS".getBytes());
//        builder.pad();
//        return builder.toByteArray();
//    }
//
//    @Test
//    public void testMessagePDU() {
//        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.MESSAGE.value);
//     //   builder.append(bytes)
//    }
//

}

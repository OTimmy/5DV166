package test;

import static org.junit.Assert.*;

import model.network.pdu.ByteSequenceBuilder;
import model.network.pdu.OpCode;
import model.network.pdu.types.NicksPDU;
import model.network.pdu.types.UCNickPDU;
import model.network.pdu.types.UJoinPDU;
import model.network.pdu.types.ULeavePDU;

import org.junit.Test;

public class PDUTesting {

    @Test
    public void testNicksPDU() {

        //Working PDU

        byte[] bytes = buildWorkingPDU();
        NicksPDU nicksPDU = new NicksPDU(bytes);

        assertTrue(nicksPDU.isValid());

        bytes = buildNotWokringPDU();
        nicksPDU = new NicksPDU(bytes);
        assertFalse(nicksPDU.isValid());

    }

    private byte[] buildWorkingPDU() {

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

    private byte[] buildNotWokringPDU() {
        String nick1 = "OPS";
        String nick2 = "KALLE";
        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.NICKS.value);
        builder.append((byte) 2);
        builder.appendShort((short) 5);

        builder.append(nick1.getBytes());
        builder.append((byte)0);
        builder.append(nick2.getBytes());
        builder.append((byte) 0);
        builder.pad();

        return builder.toByteArray();

    }


    @Test
    public void testULeavePDU() {
        byte[] bytes = buildWorkingULeavePPDU();

        ULeavePDU pdu = new ULeavePDU(bytes);
        assertTrue(pdu.isValid());

        bytes = buildNotWorkingULeavePPDU();
        pdu = new ULeavePDU(bytes);
        assertFalse(pdu.isValid());

    }


    private byte[] buildWorkingULeavePPDU() {
        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.ULEAVE.value);

        builder.append((byte) 3);
        builder.pad();
        builder.appendInt(0);
        builder.append("TIM".getBytes());
        builder.pad();

        return builder.toByteArray();
    }

    private byte[] buildNotWorkingULeavePPDU() {
        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.ULEAVE.value);

        builder.append((byte) 3);
        builder.pad();
        builder.appendInt(0);
        builder.append("TIMS".getBytes());
        builder.pad();

        return builder.toByteArray();
    }

    @Test
    public void testUJoinPDU() {
        byte[] bytes = buildWorkingUJoinPDU();
        UJoinPDU pdu = new UJoinPDU(bytes);

        assertTrue(pdu.isValid());

        bytes = buildNotWorkingUJoinPDU();
        pdu = new UJoinPDU(bytes);

        assertFalse(pdu.isValid());


    }

    private byte[] buildWorkingUJoinPDU() {

        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.UJOIN.value);
        builder.append((byte) "TIM".length());
        builder.pad();
        builder.appendInt(0);
        builder.append("TIM".getBytes());
        builder.pad();

        return builder.toByteArray();
    }

    private byte[] buildNotWorkingUJoinPDU() {

        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.UJOIN.value);
        builder.append((byte) "TIM".length());
        builder.pad();
        builder.appendInt(0);
        builder.append("TIMS".getBytes());
        builder.pad();

        return builder.toByteArray();

    }

    @Test
    public void testUCNickPDU() {
        byte[] bytes = buildWorkingUCNickPDU();

        UCNickPDU pdu = new UCNickPDU(bytes);

        assertTrue(pdu.isValid());

    }

    private byte[] buildWorkingUCNickPDU() {
        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.UCNICK.value);
        builder.append((byte) "TIM".length());
        builder.append((byte) "TIMS".length());
        builder.pad();
        builder.appendInt(0);

        builder.append("TIM".getBytes());
        builder.append("TIMS".getBytes());

        return builder.toByteArray();
    }

}

package model.network.pdu;

import java.io.IOException;
import java.io.InputStream;

import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;

import com.sun.org.apache.xpath.internal.compiler.OpCodes;

public abstract class PDU {

    private final static int OP_CODE_INDEX = 0;
//    private final int REG = 0;
//    private final int ACK = 1;
//    private final int ALIVE = 2;
//    private final int GETLIST = 3;
//    private final int SLIST = 4;
//    private final int NOTREG = 100;
//    private final int MESSAGE = 10;


    public OpCode op;

    public static PDU fromInputStream(InputStream inStream) throws IOException {
        int length = inStream.available();
        byte[] bytes = new byte[length];

        inStream.read(bytes,0,length);

        OpC

        if(bytes[0] == OpCode.ACK.value) {

        } else if (bytes[0] == OpCode.REG.value) {

        }

        switch(bytes[0]) {
        case op.ACK.value:
            break;
        default:
            break;

        }


        return null;
    }


    public abstract byte[] toByteArray();

    public abstract int getSize();


}

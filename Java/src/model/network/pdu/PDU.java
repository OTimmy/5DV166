package model.network.pdu;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.network.pdu.types.SListPDU;

//import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;

import com.sun.org.apache.xpath.internal.compiler.OpCodes;

public abstract class PDU {

    private final static int OP_CODE_INDEX = 0;


    public OpCode op;

    public static PDU fromInputStream(InputStream inStream) throws IOException {
        int length = inStream.available();
        byte[] bytes = new byte[length];

        inStream.read(bytes,0,length);

		OpCode op = OpCode.getOpCodeBy(bytes[0]);
		
        switch(op) {

        	case GETLIST:
        
        	case SLIST: return (new  SListPDU(bytes));
        		
        	case MESSAGE:
        }

        return null;
    }


    public abstract byte[] toByteArray();

    public abstract int getSize();


}

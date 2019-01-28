/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ecodigital.wiresharktoapdu.apdu;

import com.ecodigital.wiresharktoapdu.Hex;

/**
 *
 * @author gluque
 */
public class RequestApdu {
    
    
    public static final byte OUT    = (byte)0x00;
    public static final byte IN     = (byte)0x01;
    public static final byte[] apdu0000 = new byte[]{(byte)0x00, (byte)0x00};
    public static final byte[] apdu0001 = new byte[]{(byte)0x00, (byte)0x01};
    
    private final byte NULL = (byte)0xFF;
    private byte[] data;
    private final int headApdu = 10;

    public RequestApdu(byte[] data) {
        this.data = data;
    }
    
    public byte[] getAbData() {
        if(data.length > 10) {
            return Hex.subArray(data, headApdu, data.length - headApdu);
        }
        return new byte[]{};
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    
    public byte bMessageType() {
        if(this.data.length > 10) {
           return this.data[0];
        }
        return NULL;
    }
    
    /**
     * Size of abData field of this message (4 byte)
     * @return 
     */
    public byte[] dwLength() {
        if(this.data.length > 10) {
           return new byte[]{this.data[1], this.data[2], this.data[3], this.data[4]};
        }
        return null;
    }
    
    /**
     * Identifies the slot number for this command
     * @return 
     */
    public byte bSlot() {
        if(this.data.length > 10) {
           return this.data[5];
        }
        return NULL;
    }
    
    /**
     * Sequence number for command.
     * @return 
     */
    public byte bSeq() {
        if(this.data.length > 10) {
           return this.data[6];
        }
        return NULL;
    }
    
    /**
     * Used to extend the CCIDs Block Waiting Timeout for
     * this current transfer. The CCID will timeout the block
     * after “this number multiplied by the Block Waiting Time”
     * has expired.
     * @return 
     */
    public byte bBWI() {
        if(this.data.length > 10) {
           return this.data[7];
        }
        return NULL;
    }
    
    /**
     * 0000h
     * the command APDU begins and ends with this
     * command,
     * 
     * 0001h
     * the command APDU begins with this command, and
     * continue in the next PC_to_RDR_XfrBlock,
     * 
     * 0002h
     * this abData field continues a command APDU and ends
     * the APDU command,
     * 
     * 0003h
     * the abData field continues a command APDU and
     * another block is to follow,
     * 
     * 0010h
     * empty abData field, continuation of response APDU is
     * expected in the next RDR_to_PC_DataBlock.

     * @return 
     */
    public byte[] wLevelParameter() {
        if(this.data.length > 10) {
           return new byte[]{this.data[8], this.data[9]};
        }
        return null;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ecodigital.wiresharktoapdu.apdu;

import com.ecodigital.wiresharktoapdu.Hex;
import java.nio.ByteBuffer;

/**
 *
 * @author gluque
 */
public class ResponseApdu {
    public static final byte OUT    = (byte)0x00;
    public static final byte IN     = (byte)0x01;
    public static final byte[] apdu0000 = new byte[]{(byte)0x00, (byte)0x00};
    public static final byte[] apdu0001 = new byte[]{(byte)0x00, (byte)0x01};
    
    private final byte NULL = (byte)0xFF;
    private byte[] data;
    private final int headApdu = 10;

    public ResponseApdu(byte[] data) {
        this.data = data;
    }
    
    public byte[] getAbData() {
        if(data.length > 10) {
            return Hex.subArray(data, headApdu, data.length - headApdu);
        }
        return new byte[]{};
    }
    
    /**
     * Agrega nueva data, respuesta a completar es agregada aqui.
     * @param newData 
     */
    public void appendData(byte[] newData) {
        ByteBuffer buffer = ByteBuffer.allocate(data.length + newData.length);
        buffer.put(this.data);
        buffer.put(newData);
        byte[] tmp = buffer.array();
        data = tmp;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    
    
    
    /**
     * Indicates that a data block is being sent from
     * the CCID
     * @return 
     */
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
     * Sequence number for the corresponding
     * command.
     * @return 
     */
    public byte bSeq() {
        if(this.data.length > 10) {
           return this.data[6];
        }
        return NULL;
    }
    
    /**
     * Slot status register as defined in § 6.2.6
     * @return 
     */
    public byte bStatus() {
        if(this.data.length > 10) {
           return this.data[7];
        }
        return NULL;
    }
    
    /**
     * Slot error register as defined in § 6.2.6
     * @return 
     */
    public byte bError() {
        if(this.data.length > 10) {
           return this.data[8];
        }
        return NULL;
    }
    
    /**
     * 00h the response APDU begins and ends in
     * this command
     * 
     * 01h the response APDU begins with this
     * command and is to continue
     * 
     * 02h this abData field continues the response
     * APDU and ends the response APDU
     * 
     * 03h this abData field continues the response
     * APDU and another block is to follow
     * 10h empty abData field, continuation of the
     * command APDU is expected in next
     * PC_to_RDR_XfrBlock command
     * @return 
     */
    public byte bChainParameter() {
        if(this.data.length > 10) {
           return this.data[9];
        }
        return NULL;
    }
    
    
    public byte getSW1() {
        if(data.length >= 12) {
            return data[ data.length - 2 ];
        }
        return (byte)0xFF;
    }
    
    public byte getSW2() {
        if(data.length >= 12) {
            return data[ data.length - 1 ];
        }
        return (byte)0xFF;
    }
    
}

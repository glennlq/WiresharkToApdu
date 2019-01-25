/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ecodigital.wiresharktoapdu;

/**
 *
 * @author gluque
 */
public class CommandByte {
    private byte out_in;
    private byte[] data;

    public CommandByte(byte out_in, byte[] data) {
        this.out_in = out_in;
        this.data = data;
    }

    public byte getOut_in() {
        return out_in;
    }

    public void setOut_in(byte out_in) {
        this.out_in = out_in;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    
    
    
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ecodigital.wiresharktoapdu.apdu;

import com.ecodigital.wiresharktoapdu.apdu.RequestApdu;
import com.ecodigital.wiresharktoapdu.apdu.ResponseApdu;

/**
 *
 * @author gluque
 */
public class CommandTpdu {
    
    public static final byte OUT    = (byte)0x00;
    public static final byte IN     = (byte)0x01;
    
    public byte out_in = (byte)0xFF;
    public  byte[] data;
    
    private RequestApdu requestApdu;
    private ResponseApdu responseApdu;

    public CommandTpdu(byte out_in, byte[] data) {
        this.out_in = out_in;
        this.data = data;
        if(out_in == OUT) {
            requestApdu = new RequestApdu(data);
        } else if(out_in == IN){
            responseApdu = new ResponseApdu(data);
        }
    }
    
    public CommandTpdu(CommandTpdu newCommand){
        this.out_in = newCommand.out_in;
        this.data = newCommand.data;
        if(out_in == OUT) {
            requestApdu = new RequestApdu(data);
        } else if(out_in == IN){
            responseApdu = new ResponseApdu(data);
        }
    }

    public boolean isRequest() {
        return (out_in == OUT);
    }
    
    public boolean isResponse() {
        return (out_in == IN);
    }

    
    public RequestApdu getRequestApdu() {
        return requestApdu;
    }

    public void setRequestApdu(RequestApdu requestApdu) {
        this.requestApdu = requestApdu;
    }

    public ResponseApdu getResponseApdu() {
        return responseApdu;
    }

    public void setResponseApdu(ResponseApdu responseApdu) {
        this.responseApdu = responseApdu;
    }
    
}

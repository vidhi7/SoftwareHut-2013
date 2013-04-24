/*
 * 
 * Simulity Labs Ltd.
 * 
 * Copyright (c) Simulity Labs Ltd. All rights reserved.
 *
 * This source code is the property of Simulity Labs Ltd. Redistribution and
 * use in source (source code) or binary (object code) forms with or without 
 * modification, for commercial, educational or research purposes is not
 * permitted without the prior written consent of Simulity Labs Limited 
 *
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE, UNLESS PRIOR WRITTEN CONSENT STATES OTHERWISE.
 * 
 *
 */
package com.simulity.javacard.fortuneapplet;

import javacard.framework.*;
import sim.access.SIMSystem;
import sim.access.SIMView;
import sim.toolkit.*;

/**
 *
 * @author Christopher Burke <christopher.burke@simulity.com>
 */
public class FortuneApplet extends Applet implements ToolkitConstants, ToolkitInterface, ISO7816 {

    /**
     * Storage Buffers
     */
    private byte[] swap_buffer;
    private byte[] smsc;
    /**
     * SMS Transmission Constants
     */
    private final byte FDI_LENGTH = (byte) 0x0F;
    private final byte FDI_SIZE_OFFSET = (byte) 0x0E;
    private final byte SMSF_SMS_SUBMIT_NOVP = (byte) 0x01;
    private final byte SMSF_SMS_SUBMIT_NOVP_UDH = (byte) 0x41;
    private final byte SMSF_SMS_MR = (byte) 0x00;
    private final byte TP_PID = (byte) 0x00;
    /**
     * Applet Constants
     */
    private byte CLA = (byte) 0x0A; // The Applet CLASS byte
    private byte INS_INCOMING = (byte) 0x01; // Instruction byte for Incoming 
    private byte P1 = (byte) 0x00; // P1 Constant
    private byte P2 = (byte) 0x00; // P2 Constant
    /**
     * 'Fortune' buffer
     */
    private byte[] MENU_ENTRY = new byte[]{
        (byte) 'F', (byte) 'o', (byte) 'r', (byte) 't',
        (byte) 'u', (byte) 'n', (byte) 'e'
    };
    private byte[] MSG_FAILURE = new byte[]{
        (byte) 'F', (byte) 'a', (byte) 'i', (byte) 'l', (byte) 'u', (byte) 'r', (byte) 'e',
        (byte) ' ', (byte) 't', (byte) 'o', (byte) ' ', (byte) 'T', (byte) 'r', (byte) 'a',
        (byte) 'n', (byte) 's', (byte) 'm', (byte) 'i', (byte) 't', (byte) ' ', (byte) 'S',
        (byte) 'M', (byte) 'S'
    };
    /**
     * The MSISDN that SMS are transmitted to for reporting.
     */
    private final byte[] SMS_TRANSMIT_MSISDN = new byte[]{
        //+447860033047  ( Coded as contents of EF_ADN (GSM 11.11) )
        (byte) 0x08, // 0x08 == Length of Bytes that Follow 
        (byte) 0x0C, // 0x0C == len(447860033047)
        (byte) 0x91, // 0x91 == International Numbering Plan Identifier
        (byte) 0x44, // MSISDN Follows (nibbles swapped)
        (byte) 0x87,
        (byte) 0x06,
        (byte) 0x30,
        (byte) 0x03,
        (byte) 0x74
    };
    /**
     * The Content of the SMS Transmission
     */
    private static byte[] SMS_TRANSMIT_CONTENT;
    /**
     * The ToolKit Registry, used for provisioning events on the handset.
     */
    private ToolkitRegistry toolkitRegistry = ToolkitRegistry.getEntry();

    /**
     * Applet Constructor
     *
     * @param bArray
     * @see install
     * @param bOffset
     * @see install
     * @param parametersLength
     * @see install
     */
    public FortuneApplet(byte[] bArray, short bOffset, short parametersLength) {
        toolkitRegistry = ToolkitRegistry.getEntry();

        /**
         * Add support for Formatted SMS-PP Messages
         */
        toolkitRegistry.setEvent(EVENT_FORMATTED_SMS_PP_ENV);

        /**
         * Add the menu item for the applet
         */
        toolkitRegistry.initMenuEntry(
                MENU_ENTRY,
                (short) 0, // offset in array 
                (short) 7, // length of 'Fortune'
                PRO_CMD_SELECT_ITEM, false, (byte) 0, (short) 0);
        
        /**
         * Construct the swap and sms buffers to transient byte arrays
         */
        swap_buffer = JCSystem.makeTransientByteArray((short) 250, JCSystem.CLEAR_ON_RESET);
        SMS_TRANSMIT_CONTENT = JCSystem.makeTransientByteArray((short) 15, JCSystem.CLEAR_ON_RESET);
        smsc = JCSystem.makeTransientByteArray((short) 32, JCSystem.CLEAR_ON_RESET);
    }

    /**
     * The Install method, called by the JCVM
     *
     * @param bArray
     * @param bOffset: offset in bArray
     * @param bLength: length of bArray
     * @throws ISOException
     */
    public static void install(byte[] bArray, short bOffset, byte bLength) throws ISOException {

        short parametersLength, workOffset = bOffset;

        // Skip the instance AID
        workOffset += (short) (bArray[workOffset] + 1);

        // Skip the application privileges
        workOffset += (short) (bArray[workOffset] + 1);

        // Get the application parameters length
        parametersLength = (short) (bArray[workOffset] & 0xff);

        // Create the instance and register it with the given instance AID
        new FortuneApplet(bArray, (short) (workOffset + 1), parametersLength) /*
                 * This method is used by the applet to register this applet
                 * instance with the Java Card runtime environment and assign
                 * the specified AID bytes as its instance AID bytes.
                 */.register(bArray, (short) (bOffset + 1), bArray[bOffset]);
        
        //        SMS_TRANSMIT_CONTENT[0] = (byte) 0x06; // Length
        // FASW == Fortune Applet Software Hut -- Identifies this applet on the server
        // for message routing 

    }

    /**
     * The process method, called by the JCVM. The processBuffer method is only
     * needed in here for the simulated testing environment, and has no effect
     * when the applet is installed onto a card and used in the production
     * system.
     *
     * @param apdu The incoming APDU object
     * @throws ISOException should there be any errors
     */
    public void process(APDU apdu) throws ISOException {
        apdu.setIncomingAndReceive();
        byte[] buffer = apdu.getBuffer();
        Util.arrayCopy(buffer, (short) 0, swap_buffer, (short) 0, (short) 250);
        processBuffer(swap_buffer);
    }

    /**
     * Process an APDU buffer in the Applet
     *
     * Example APDU for Simulator:
     *
     * apdu
     * 0A01000020466F74696E7565204170706C657420666F7220536F6674776172652048757421
     *
     * @param apduBuffer
     * @throws ISOException
     */
    private void processBuffer(byte[] apduBuffer) throws ISOException {
        if (!selectingApplet()) {
            if (apduBuffer[OFFSET_CLA] != CLA) {
                throw new ISOException(SW_CLA_NOT_SUPPORTED);
            } else {
                if (apduBuffer[OFFSET_INS] != INS_INCOMING) {
                    throw new ISOException(SW_INS_NOT_SUPPORTED);
                } else {
                    if ((apduBuffer[OFFSET_P1] != P1) && apduBuffer[OFFSET_P2] != P2) {
                        throw new ISOException(SW_INCORRECT_P1P2);
                    } else {
                        // If it passes validation, display Fortune Menu
                        short payloadLength = (short) (apduBuffer[OFFSET_LC] & 0xFF);
                        ProactiveHandler theHandler = ProactiveHandler.getTheHandler();
                        theHandler.initDisplayText((byte) 0x81, DCS_8_BIT_DATA, apduBuffer, (short) 5, payloadLength);
                        System.out.println(new String(apduBuffer));
                        if (theHandler.send() == RES_CMD_PERF) {
                            displayFortuneMenu();
                        }
                    }
                }
            }

        }
    }

    /**
     * If we want to do any additional processing after the fortune message, it
     * should go here.
     */
    private void displayFortuneMenu() {
        return;
    }

    /**
     * When an event is fired, this logic is called.
     *
     * @param event
     */
    public void processToolkit(byte event) {
        
        SMS_TRANSMIT_CONTENT[0] = (byte) 'F';
        SMS_TRANSMIT_CONTENT[1] = (byte) 'A';
        SMS_TRANSMIT_CONTENT[2] = (byte) 'S';
        SMS_TRANSMIT_CONTENT[3] = (byte) 'H';
        SMS_TRANSMIT_CONTENT[4] = (byte) ' ';
        SMS_TRANSMIT_CONTENT[5] = (byte) '0'; // 0 == Send a Fortune message to this handset
        
        switch (event) {
            case EVENT_MENU_SELECTION:
                if (send(SMS_TRANSMIT_CONTENT, (short) 6, DCS_DEFAULT_ALPHABET) != RES_CMD_PERF) {
                    ProactiveHandler theHandler = ProactiveHandler.getTheHandler();
                    theHandler.initDisplayText((byte) 0x01, DCS_8_BIT_DATA, MSG_FAILURE, (short) 0, (short) MSG_FAILURE.length);
                    if (theHandler.send() == RES_CMD_PERF) {
                        ISOException.throwIt(SW_UNKNOWN);
                    }
                }
                break;
            case EVENT_FORMATTED_SMS_PP_ENV:
                processIncomingEnvelope();
                break;
        }
    }

    /**
     * Process an incoming envelope
     */
    private void processIncomingEnvelope() {
        EnvelopeHandler envHndlr = EnvelopeHandler.getTheHandler();
        envHndlr.copyValue(envHndlr.getSecuredDataOffset(), swap_buffer, (short) 0, envHndlr.getSecuredDataLength());
        processBuffer(swap_buffer);
    }

    /**
     * Sends the message. In case of a concatenated sms failing, the first sms
     * to fail will abort the operation and return the error code.
     *
     * @param dcs Data Coding Scheme to use. if DCS is GSM_DEFAULT_ALPHABET
     * packing will be applied.
     * @return response byte from handler.send()
     */
    public byte send(byte[] message, short messageLength, byte dcs) {
        return sendMessage(message, messageLength, null, dcs);
    }

    /**
     * Sends a single SMS.
     *
     * @param message byte[] containing message data (in 8bit or 16bit form)
     * @param msgLength length of the message
     * @param udh udh to prefix onto the message (may be null)
     * @param dcs data coding scheme
     * @return response from handler.send()
     */
    public byte sendMessage(byte[] message, short msgLength, byte[] udh, byte dcs) {

        short toOffsetSms = 0;
        short toOffsetUdh;
        boolean pack = (dcs == DCS_DEFAULT_ALPHABET);
        boolean udhi = (udh != null);

        if (udhi) {
            swap_buffer[toOffsetSms++] = SMSF_SMS_SUBMIT_NOVP_UDH;//set udhi to 1
        } else {
            swap_buffer[toOffsetSms++] = SMSF_SMS_SUBMIT_NOVP;//sms submit
        }
        swap_buffer[toOffsetSms++] = SMSF_SMS_MR;                                           //no specific ref number
        toOffsetSms = Util.arrayCopy(SMS_TRANSMIT_MSISDN, (short) 1, swap_buffer, toOffsetSms, SMS_TRANSMIT_MSISDN[0]);//msisdn
        swap_buffer[toOffsetSms++] = TP_PID;                                                //protocl identifier
        swap_buffer[toOffsetSms++] = dcs;                                                   //data coding scheme
        swap_buffer[toOffsetSms++] = (byte) (msgLength & 0xFF);                              //data length (octets or septets)

        toOffsetUdh = toOffsetSms;//udh goes here (start of user data)
        ////payload below, either copy or pack
        if (pack) {
            toOffsetSms += pack(message, (short) (0), swap_buffer, toOffsetSms, (short) (msgLength & 0xFF));
        } else {
            toOffsetSms = Util.arrayCopy(message, (short) (0), swap_buffer, toOffsetSms, (short) (msgLength & 0xFF));
        }

        //copy udh as 8 bit, overwriting blank padding bits in message
        if (udhi) {
            Util.arrayCopy(udh, (short) 0, swap_buffer, toOffsetUdh, (short) (udh.length));
        }
        
//        displayArrayAsHex(swap_buffer, (short) 0, toOffsetSms, (byte) 0x81);

        //read smsc address
        SIMView simView = SIMSystem.getTheSIMView();
        simView.select(SIMView.FID_MF);
        simView.select(SIMView.FID_DF_TELECOM);
        simView.select(SIMView.FID_EF_SMSP, smsc, (short) 0, FDI_LENGTH);
        short dataOffset = smsc[FDI_SIZE_OFFSET];
        dataOffset -= FDI_LENGTH;
        simView.readRecord((byte) 0x01, SIMView.REC_ACC_MODE_ABSOLUTE_CURRENT, dataOffset++, smsc, (short) 0, (short) 1);
        short recordLength = (short) (smsc[0] & 0xFF);
        simView.readRecord((byte) 0x01, SIMView.REC_ACC_MODE_ABSOLUTE_CURRENT, dataOffset, smsc, (short) 0, recordLength);

        // build proactive command
        ProactiveHandler handler = ProactiveHandler.getTheHandler();
        handler.clear();
        handler.init(PRO_CMD_SEND_SHORT_MESSAGE, (byte) 0, DEV_ID_NETWORK);
        handler.appendTLV(TAG_ADDRESS, smsc, (short) 0, recordLength);
        handler.appendTLV(TAG_SMS_TPDU, swap_buffer, (short) 0, toOffsetSms);

        return handler.send();
    }

    /**
     * Pack a 8 bit GSM alphabet message to 7 bits format
     *
     * @param src initial message source
     * @param offsetSrc offset to the message source
     * @param dst packed message target
     * @param offsetDst offset the the message target
     * @param length length of the message to be packed
     *
     * @return size of the packed message
     */
    public static short pack(byte[] src, short offsetSrc, byte[] dst, short offsetDst, short length) {

        short countSrc = (short) 0;
        short countDst = (short) 0;
        short countCurrent;
        byte leftover = (byte) 0;

        while (countSrc < length) {
            countCurrent = (byte) (countSrc & 7);
            if (countCurrent == 0) {
                leftover = src[(short) (offsetSrc)];
            } else {
                dst[offsetDst] = (byte) ((src[offsetSrc] << (8 - countCurrent)) | leftover);
                leftover = (byte) (src[offsetSrc] >> countCurrent);
                offsetDst++;
                countDst++;
            }
            countSrc++;
            offsetSrc++;
        }

        if ((length % 8) != 0) {
            dst[offsetDst] = leftover;
            countDst++;
        }

        return countDst;
    }
    public static byte[] hexDisplay = new byte[256];

    public static byte displayArrayAsHex(byte[] array, short offset, short length, byte qualifier) {

        if (length > (short) 128) {
            length = (short) 128;
        }
        byaToAsciiBya(array, offset, hexDisplay, (short) 0, length);

        ProactiveHandler.getTheHandler().clear();
        ProactiveHandler.getTheHandler().initDisplayText((byte) 0x01, DCS_8_BIT_DATA, hexDisplay, (short) 0, (short) (length * 2));
        return ProactiveHandler.getTheHandler().send();
    }

    public static void displayAsciiArray(byte[] array, short offset, short length) {
        ProactiveHandler.getTheHandler().clear();
        ProactiveHandler.getTheHandler().initDisplayText((byte) 0x01, DCS_8_BIT_DATA, array, offset, (short) (length));
        ProactiveHandler.getTheHandler().send();
    }

    public static byte getLeftNibble(byte by) {
        return (byte) (by & (byte) 0xF0);
    }

    public static byte getRightNibble(byte by) {
        return (byte) (by & 0x0F);
    }

    public static byte nibbleToAscii(byte nibble) {

        if ((nibble >= (byte) 0) && (nibble <= (short) 9)) {
            return (byte) (nibble + (byte) 48);
        }

        if ((nibble >= (byte) 0x0A) && (nibble <= (byte) 0x0F)) {
            return (byte) ((nibble - (byte) 0x0A) + (byte) 65);
        }

        return nibble;
    }

    public static short byaToAsciiBya(byte[] srcBuffer, short srcOff, byte[] dstBuffer, short dstOff, short length) {

        if ((short) (dstBuffer.length - dstOff) < (short) (length * 2)) {
            return (short) 0xFF;
        }

        for (short i = srcOff; i < (short) (length + srcOff); i++) {
            byte byThis = srcBuffer[i];
            byte byLeft = nibbleToAscii((byte) (getLeftNibble(byThis) >> 4 & 0x0F));
            byte byRight = nibbleToAscii(getRightNibble(byThis));

            if (byLeft != (byte) 0xFF && byRight != (byte) 0xFF) {
                dstBuffer[dstOff++] = byLeft;
                dstBuffer[dstOff++] = byRight;
            }
        }

        return (short) (length * 2);
    }
}

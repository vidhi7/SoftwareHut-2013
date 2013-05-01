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
package com.simulity.javacard.server.fortune.fortuneserver;

import com.simulity.api.hubb.ByteString;
import com.simulity.servletutil.comm.CardSettings;
import com.simulity.servletutil.comm.SendSms;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Christopher Burke <christopher.burke@simulity.com>
 */
public class FortuneServer extends HttpServlet {

    public static void main(String[] args) {
        Map<String, String[]> hm = new HashMap<String, String[]>();
        hm.put("fortuneRequest", new String[] {"0"});
        hm.put("msisdn", new String[] {"353866018263"});
        doRequestLogic(hm);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doRequestLogic(req.getParameterMap());
        resp.getWriter().write("OK");
        resp.getWriter().flush();
    }

    private static void doRequestLogic(Map<String, String[]> requestParameters) {
        if (requestParameters.containsKey("fortuneRequest") && requestParameters.containsKey("msisdn")) {
            String[] fortuneRequestStringArray = requestParameters.get("fortuneRequest");
            if (fortuneRequestStringArray.length == 0) {
                Logger.getLogger(FortuneServer.class.getName()).log(Level.WARNING, "Request was attempted with no parameters");
            } else {
                String fortuneRequest = fortuneRequestStringArray[0]; // get the bytecode data
                Logger.getLogger(FortuneServer.class.getName()).log(Level.INFO, "Received the ByteCode: {0}", fortuneRequest);
                if (fortuneRequest.equalsIgnoreCase("0")) {
                    Logger.getLogger(FortuneServer.class.getName()).log(Level.INFO, "Request is for Fortune Data");

                    String outStream = null;
                    String errStream = null;
                    String fortune = null;
                    try {
                        // run the Fortune command
                        Process p = Runtime.getRuntime().exec("/usr/local/bin/fortune");

                        BufferedReader stdIn = new BufferedReader(new InputStreamReader(p.getInputStream()));

                        BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                        while ((outStream = stdIn.readLine()) != null) {
                            Logger.getLogger(FortuneServer.class.getName()).log(Level.INFO, "STDOUT: {0}", outStream);
                            fortune += outStream;
                        }

                        while ((errStream = stdErr.readLine()) != null) {
                            Logger.getLogger(FortuneServer.class.getName()).log(Level.INFO, "STDERR: {0}", errStream);
                        }

                    } catch (IOException ex) {
                        Logger.getLogger(FortuneServer.class.getName()).log(Level.WARNING, "IOException when attmpeting to process Fortune command.", ex);
                    }

                    if (fortune != null) {
                        if (0 != fortune.length()) {
                            
                            ByteString bs = null;
                            
                            try {
                                
                                // regex by alex to replace any patters of > 2 spaces with 1 space
                                
                                fortune = fortune.trim().replaceAll(" +", " ");
                                
                                if(fortune.length() > 70) {
                                    fortune = fortune.substring(0, 67) + "...";
                                }
                                
                                byte[] fortuneAscii = fortune.getBytes("ASCII");
                                bs = new ByteString(fortuneAscii);
                            } catch (UnsupportedEncodingException ex) {
                                Logger.getLogger(FortuneServer.class.getName()).log(Level.SEVERE, null, ex);
                                doRequestLogic(requestParameters);
                            }
                            
                            
                            if(bs == null) {
                                doRequestLogic(requestParameters);
                            }
                            
                            // transmit the fortune message to the handset
                            CardSettings cs = new CardSettings(
                                    "0000", 
                                    "NONE", 
                                    "NONE", 
                                    "1", 
                                    "NONE", 
                                    "NONE", 
                                    "1", 
                                    "555559", 
                                    "0000000000", 
                                    "0", 
                                    "0", 
                                    requestParameters.get("msisdn")[0]);
                            
                            String transmit = "0A010000" + ((bs.toBytes().length < 0x0F) ? "0" + Integer.toHexString(bs.toBytes().length) : Integer.toHexString(bs.toBytes().length)) + bs.toHex();
                            
                            Logger.getLogger(FortuneServer.class.getName()).log(Level.INFO, "Transmitting APDU: {0}", transmit);
                            
                            if (SendSms.sendMessage(transmit, cs, "http://simulity.co.uk/ota/ram/command")) {
                                Logger.getLogger(FortuneServer.class.getName()).log(Level.INFO, "The SMS {0} transmitted successfully.", fortune);
                            } else {
                                Logger.getLogger(FortuneServer.class.getName()).log(Level.WARNING, "The SMS failed to transmit.");
                            }
                        } else {
                            Logger.getLogger(FortuneServer.class.getName()).log(Level.WARNING, "The Fortune length was zero.");
                        }
                    } else {
                        Logger.getLogger(FortuneServer.class.getName()).log(Level.WARNING, "The Fortune data was not set.");
                    }
                } else {
                    Logger.getLogger(FortuneServer.class.getName()).log(Level.WARNING, "The request is not supported.");
                }
            }
        }
    }
}

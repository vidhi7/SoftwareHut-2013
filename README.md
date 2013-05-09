SoftwareHut-2013
================
Christopher Burke - Software Engineer - Simulity Labs Ltd

10th May 2013

---

> This project was completed by the 2013 SoftwareHut team at Bangor University. The students successfully created a JavaCard 2.2.1 Application which sent a message to a Java server which in turn requested a 'Fortune' message and sends the message using 03.48 OTA to the handset, which displays the message. They have completely satisfied the requirements for the project, and I am very happy with the results. - Christopher Burke

###The Students: 

* Andrei Alexandru Brasoveanu
* Delvin Varghese

###Introduction: What is Fortune? 
"fortune is a simple program that displays a pseudorandom message from a database of quotations that first appeared in Version 7 Unix. The most common version on modern systems is the BSD fortune, originally written by Ken Arnold. Distributions of fortune are usually bundled with a collection of themed files, containing sayings like those found on fortune cookies (hence the name), quotations from famous people, jokes, or poetry." *Source: [Wikipedia](http://en.wikipedia.org/wiki/Fortune_(Unix))*

If you happen to use a *nix-like operating system, such as Mac OS X or a Linux distribution, you will be able to install the fortune game on your machine via a package manager such as MacPorts or apt. This will allow you to play with the fortune game yourself, however, as an example I have provided some terminal output of fortune below. 

    cb@mobox:~$ fortune
    Let us endeavor so to live that when we come to die even the undertaker will be sorry. -- Mark Twain, "Pudd'nhead Wilson's Calendar"

    cb@mobox:~$ fortune
    All the troubles you have will pass away very quickly.

    cb@mobox:~$ fortune
    Communicate!  It can't make things any worse.

    cb@mobox:~$ fortune
    Avert misunderstanding by calm, poise, and balance.

    cb@mobox:~$ fortune
    Don't go surfing in South Dakota for a while.

---

###Introduction: What is JavaCard? 
"Java Card refers to a technology that allows Java-based applications (applets) to be run securely on smart cards and similar small memory footprint devices. Java Card is the [smallest] of Java platforms targeted for embedded devices. Java Card gives the user the ability to program the devices and make them application specific. It is widely used in SIM cards (used in GSM mobile phones) and ATM cards." *Source: [Wikipedia](http://en.wikipedia.org/wiki/Java_Card)*

Therefore, a motivated group students with a good understanding of Java should be capable of writing JavaCard applets, in Java. However, there are some differences between the desktop editition of Java that you may be used to, and the version of Java that you'll be writing for JavaCard. This is due to the restrictive nature of the JavaCard platform, and instruction set. 

###Introduction: Differences and Difficulties of JavaCard 
While all of this information will be explained in further detail to the group that is selected for the project; some of the notible differences are limited primitive types, and lack of literals. For example, the primitive types accepted by JavaCard are: 

* byte
* short
* boolean

Meaning that the unsupported primitive types are: 

* int
* long
* float
* double 
* char

Furthermore, String and Integer literals are not supported. Therefore, one cannot construct a String by simply enclosing data in speech ("") marks. Instead, 'Strings' typically are represented as hexadecimal ASCII byte arrays. An example, is the String `String s = "Hello, World!"` which would be represented in hex as: 

    "48 65 6C 6C 6F 2C 20 57 6F 72 6C 64 21"

Therefore, the construction of the above data in Java could be done as follows: 

    byte[] sBya = new byte[] {
        (byte) 0x48, 
        (byte) 0x65, 
        (byte) 0x6C,
        (byte) 0x6C, // and so on... 
    }

This constrained environment should provide a new and interesting challenge to any student. 

----

###Implementation
While the route to implementation is ultimately up to the group of students, the project should take some sort of methodical approach. Bonus points for the use of a proven software development methodolgy such as Agile or Waterfall. Ultimately, a flow of `planning -> implementation -> testing -> review -> repeat` until completion is suggested. 

####Planning
The students should first plan how they will implement the project. An example (and suggested!) route to implementation will be **strongly** advised however it is ultimately the perogative of the group. 

####Implementation
Once the planning has been completed, the students should then begin to work on implementing the project. There will be a chance every week to meet with the project supervisor, to assist with any challenges faced and to run the applet in an emulated environment and install the applet onto a SIM card (so the student can test their weekly revision on the handset). 

####Testing
Due to the (likely!) dynamic nature of the applet, there should be evidence of different testing approaches to validate the applet and it's logic. 

####Completion
Upon completion, a review will be made of how the planning of the project first appeared, to how many revisions and changes to the planning were made (this is expected, and is not a bad thing!), to how the planning matches up to the implementation. 

####Assistance
I will be on hand as a supervisor to the project to help the student group, and will be able to provide the following: 

* NetBeans Project Setup
* Maven / Ant Build System for compilation 
* Repository so the student can request assistance easily 
* Some sample modules, running our OS, with a set electronic profile
* Access to one of our HTTP APIs to send data from a server, to the handset. 

**Note:** *(the student will be able to install / remove applets on the chip, using one of our tools) - this will allow the student to view and debug their code, and a card reader - which will all be provided once the project reaches a milestone for the applet to be installed*

The ideal development environment will be Linux or Mac OS X, however Windows is supported (but not recommended). The ideal env will allow the students to have access to the fortune game, and run the complete client and server in a single development environment. 

---

###Recommendation
Before applying for this project, it is advised that you first attempt to understand bits, bytes, and bitwise operations. Some example methods and questions to think about have been defined below; if your team can figure them out then you should be fine, however if you can't this project might be very tricky (but that doesn't mean you shouldn't go for it, a good challenge is always fun!) Brain teasers should always be attempted without the convenience of a computer, or they're just too easy. If you would like the solutions to these questions, please send me an E-Mail with what you believe the solutions are at <christopher.burke@simulity.com> 

---

#####One: 
    /**
      * What does this code block do? 
      * Does this code compile? 
      * What is an unsigned, versus a signed byte? 
      * What changes could be applied to get this code
      *     output 0 - 255 to the command line without using ints?
      */
     public static void main(String[] args) {
         for(byte i = 0; i < 256; i++)
             System.out.println(i);
     }

---

#####Two:
How many `byte` type values could be stuffed into a `long` type (in Java)?    

---

#####Three:
    /** 
     * What does this method do? 
     * Would this method compile? 
     * Dangers of this method?
     * How would one validate such a method? 
     * Would this method be suitable in production code? 
     * How could this method be improved? 
     */
     byte m1(byte b1, byte b2) {
        b2 == 0 ? return a : return m1((a ^ b), (a & b) << 1));
     }

---

#####Four:
Write a program to swap the nibbles of a hexadecimal byte. 

**Note:** *nibbles are the high and low order of the binary data, i.e. `0xFB` nibble swapped would be `0xBF`*

    HEX  -- BIN
    ------------ - - -`
    0xF0 == 11110000
    0x0B == 1011

---

#####Five: 
The below method coverts and 8Bit messages to GSM7, but what is it actually doing? I.e. what happens to the data? This is a tricker challenge.

    /**
     * Converts an 8Bit message to GSM7 bit ASCII encoding
     *
     * http://en.wikipedia.org/wiki/GSM_03.38
     *
     * @param byaSrc 8bit byte array of data to convert
     * @return 7bit GSM7 encoded 
     */
    public static Byte[] conv8bitToGsm7(byte[] byaSrc) {

        List<Byte> dstByaList = new ArrayList<Byte>();
        byte buf = (byte) 0x00;

        for (int i = 0; i < byaSrc.length; i++) {
            byte b = byaSrc[i];
            byte c = (byte) (i & 7);
            if (c == 0) {
                buf = b;
            } else {
                dstByaList.add((byte) ((b << (8 - c) | buf)));
                buf = (byte) (b >> c);
            }
        }

        if ((byaSrc.length % 8) != 0) {
            dstByaList.add(buf);
        }
        return dstByaList.toArray(new Byte[dstByaList.size()]);
    }

# Software Requirements
###for JavaCard Development Simulity
Version 1.0

####Table of Contents
1.  Introduction	
	1.1	Purpose	
	1.2	Document Conventions	
	1.3	Intended Audience and Reading Suggestions	
	1.4	Project Scope	
	1.5	References	
2.	Overall Description	
	2.1	Product Perspective	
	2.2	Product Features	
	2.3	User Classes and Characteristics	
	2.4	Operating Environment	
	2.5	Design and Implementation Constraints	
	2.6	User Documentation	
	2.7	Assumptions and Dependencies	
3.	System Features	<br>
	3.1	System Feature 
	3.2 System Feature	
4.	External Interface Requirements<br>	
	4.1	User Interfaces<br>	
	4.2	Hardware Interfaces	<br>
	4.3	Software Interfaces	<br>
	4.4	Communications Interfaces	
5.	Other Nonfunctional Requirements	
	5.1	Performance Requirements	
	5.2	Safety Requirements	<br>
	5.3	Security Requirements	<br>
	5.4	Software Quality Attributes	
6.	Other Requirements	
Appendix A: Glossary	
Appendix B: Analysis Models	
Appendix C: Issues List	




#### 1. Introduction
#####1.1 Purpose
Developing a simple 'fortune 
cookie' (cf. unix 'fortune') application which is to be deployed with JavaCard technology.

From Wikipedia: fortune is a simple program that displays a pseudo random message from a database of quotations that first appeared in Version 7 Unix.
#####1.2 Document Conventions
Abbreviations<br>
WAP : Wireless Application Protocol<br>
HTTP : Hypertext Transfer Protocol<br>
SIM : Subscriber identity module<br>
*nix : Unix based<br>
SMS : Short Message Service<br>
MSISDN : Mobile Subscriber Integrated Services Digital Network-Number<br>
SMSC : short message service center<br>
SMSGW : SMS Gateway
#####1.3	Intended Audience and Reading Suggestions
The document is intended for readers involved in the project directly or indirectly. The technical value of the document is intended for someone with knowledge of programming and networking 
#####1.4	Project Scope
The scope of the project is understanding technologies such as JavaCard,Kannel,WAP,HTTP.
#####1.5	References
1.[http://en.wikipedia.org/wiki/Fortune_(Unix)](http://en.wikipedia.org/wiki/Fortune_(Unix) "fortune on Wikipedia")<br/>
2.[http://www.kannel.org/](http://www.kannel.org/ "Kannel")
####2.	Overall Description
#####2.1	Product Perspective
Diagram of project's major components<br/>
![](http://postimage.org/image/e6rbz5ncx/)
#####2.2	Product Features
The product sole feature is to display a message when requested.
#####2.3	User Classes and Characteristics
N/A
#####2.4	Operating Environment
The environment targeted is that of a SIM card. The specific technology that will be used for implementation is JavaCard.
#####2.5	Design and Implementation Constraints
Kannel implementation will be required.For this part of the project, development will have to be done on a *nix based operating system.
Kannel requires the following software environment:

C compiler and development libraries and related tools.
The gnome-xml (a.k.a. libxml) library, version 2.2.0 or newer. 
POSIX threads (pthread.h).
GNU Bison 1.28 if you modify the WMLScript compiler.
DocBook markup language tools (jade, jadetex, DocBook style-sheets, etc; see README.docbook).(see ref.2)
####3.	System Features	
N/A
####4.	External Interface Requirements
#####4.1	User Interfaces
The user interface will be a simple CLI allowing the user to ask for quotes out of the data base.
#####4.2	Hardware Interfaces
Kannel will be used to implement the connection between the application and the Database server which holds the messages.Kannel is a compact and very powerful open source WAP and SMS gateway, used widely across the globe both for serving trillions of short messages (SMS), WAP Push service indications and mobile internet connectivity.(see ref.2)
#####4.3	Software Interfaces
N/A
#####4.4	Communications Interfaces
The communication between the application is performed through SMS technology on one side and HTTP on the other. The "middle man" between these are SMSC and SMSGW servers.
####5.	Other Nonfunctional Requirements
#####5.1	Performance Requirements
N/A
#####5.2	Safety Requirements
N/A
#####5.3	Security Requirements
The application will use a MSISDN which is basically the phone number of the user, this information has to be handled under the Data Protection Act (1998).
#####5.4	Software Quality Attributes
N/A
####6.	Other Requirements	N/A
####Appendix A: Glossary
#####Java Card 
Java Card refers to a technology that allows Java-based applications (applets) to be run securely on smart cards and similar small memory footprint devices. Java Card is the tiniest of Java platforms targeted for embedded devices.[http://en.wikipedia.org/wiki/Javacard](http://en.wikipedia.org/wiki/Javacard)

#####Kannel
In computing, Kannel is an open source WAP gateway.It provides the essential part of the WAP infrastructure as open source software to everyone so that the market potential for WAP services, both from wireless operators and specialized service providers, will be realized as efficiently as possible.[http://en.wikipedia.org/wiki/Kannel_(telecommunications)](http://en.wikipedia.org/wiki/Kannel_(telecommunications))

#####SMS
Short Message Service (SMS) is a text messaging service component of phone, web, or mobile communication systems, using standardized communications protocols that allow the exchange of short text messages between fixed line or mobile phone devices.[http://en.wikipedia.org/wiki/SMS](http://en.wikipedia.org/wiki/SMS)

#####SMSC
A short message service center (SMSC) is a network element in the mobile telephone network. Its purpose is to store, forward, convert and deliver SMS messages.
The full designation of an SMSC according to 3GPP is Short Message Service - Service Centre (SMS-SC)[http://en.wikipedia.org/wiki/Short_message_service_center](http://en.wikipedia.org/wiki/Short_message_service_center)

#####SMSGW
SMSGW service allows members to send text messages to mobile phones, using any standard e-mail program or web browser.[http://www.smsgw.com/customers/support#faq_1](http://www.smsgw.com/customers/support#faq_1)

#####HTTP
The Hypertext Transfer Protocol (HTTP) is an application protocol for distributed, collaborative, hypermedia information systems.HTTP is the foundation of data communication for the World Wide Web.
Hypertext is a multi-linear set of objects, building a network by using logical links (the so-called hyperlinks) between the nodes (e.g. text or words). HTTP is the protocol to exchange or transfer hypertext.[http://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol](http://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol)




Appendix B: Analysis Models N/A	
Appendix C: Issues List	N/A
>>>>>>> Draft for software req.

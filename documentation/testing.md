##Test Plan

<p>The project main testing will be split into to half security and stress testing of the server.
For stress testing the server a virtual client coded with Node.JS will be created , this client send up to 1000 request/s to the server which should be able to cope.</p>
<p>In terms of security redundancy checks on SMS and check to see that the VPN is not accessible from unauthorised users.
Some traditional testing methods were not available for us due to the nature of our project, using embedded devices and JavaCard made it impossible do to normal testing of the applet.</p>

##Testing log

r/s: requests/second

Virtual Client Test 1:
Client set to 300 r/s, test duration 10 min. , server managed;

Virtual Client Test 2:
Client set to 400 r/s, test duration 10 min. , server managed;

Virtual Client Test 3:
Client set to 500 r/s, test duration 10 min. , server managed;

Virtual Client Test 4:
Client set to 600 r/s, test duration 10 min. , server managed;

Virtual Client Test 5:
Client set to 700 r/s, test duration 10 min. , server managed;

Virtual Client Test 6:
Client set to 800 r/s, test duration 10 min. , server managed;

Virtual Client Test 7:
Client set to 900 r/s, test duration 10 min. , server managed;

Virtual Client Test 8:
Client set to 1000 r/s, test duration 10 min. , server managed just;

Maximum server load 1000 r/s.


Security test


Because of the project using proprietary material provided by Simulity security testing was implemented by them. We know only that the test were successful and what they were aimed at shown in the fallowing list:

- protection of data transmitted from outside influence;
- protection of any personal data of users;
- protection of the VPN(Vitrual Pravate Network).






    









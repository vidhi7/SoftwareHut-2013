# Server Requirements 

To test server :

1. Assuming you have mysql instaled on your machine run fortune_database.sql found in resources foulder .
2. Change database.properties with your user and pass.
3. You can use TestConnection class to test database conectivity and index.jsp to test servlet.


##Server pseudo code

<code>
public class FortuneServer extends HTTPServlet {

 - init => each request prerequists login
 - doGet => entry point for processing
}
</code>

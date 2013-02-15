/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package omadaprogramming.datagenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import omadaprogramming.server.DataSource;
import omadaprogramming.server.PullQuote;

/**
 *
 * @author alex
 */
public class DataReader {

    public static void main(String[] args) {
        String command = new String();
        StringBuffer buffer = new StringBuffer();
        InputStream in = DataReader.class.getResourceAsStream("/database.properties");
        
        try {

            //Conects too database
            DataSource.init(in);
            Connection conn = DataSource.getConnection();
            Statement stat = conn.createStatement();
            FileReader reader = new FileReader(new File("src/main/resources/fortunes/art"));
            BufferedReader bufferReader = new BufferedReader(reader);


            while ((command = bufferReader.readLine()) != null) {
                buffer.append(command);
            }
            bufferReader.close();

            //Uses the default % to split the messages in the database
            StringBuffer commands = new StringBuffer(buffer.toString().replaceAll("\"", "''"));            
            String[] split = commands.toString().split("%");
            
            //Get the id of last entry
            int lastID = 0;
            ResultSet rs = stat.executeQuery("SELECT * FROM Quotes");
            while(rs.next()){
                lastID = rs.getInt(1);
            }
                       
            
            
            
            //Execute command
            int id = lastID + 1;
            
            String sql = "INSERT INTO Quotes VALUE (";
            String m = "\"";
            System.out.println(split.length);
            for (int i = 0; i < split.length; i++) {
                if (!split[i].trim().equals("")) {
                    //stat.execute("DELETE FROM Quotes WHERE Quote_ID = '13'");
                    stat.executeUpdate(sql + m + id + m + "," + m + split[i] + m + ");");
                    System.out.println("Trying::>" + sql + m + id + m + "," + m + split[i] + m + ");");
                    id++;
                    
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}

/*
 * Copyright (C) 2013 Omada Programming(Brasoveanu Andrei Alexandru, Dominic Lee,Delvin Varghese, Konstantinos Akrivos)
 * Permission is hereby granted, free of charge, 
 * to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, 
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit 
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Software.
 * 
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
 * This class uses default fortune data files It parses them and turn each
 * message into a SQL statement that is executed afterwards
 *
 * @author Brasoveanu Andrei Alexandru
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
            //Alsoo changes all " with ''
            StringBuffer commands = new StringBuffer(buffer.toString().replaceAll("\"", "''"));
            String[] split = commands.toString().split("%");

            //Execute command            

            String sql = "INSERT INTO Quotes (Quote) VALUE (";
            String m = "\"";
            System.out.println(split.length);
            for (int i = 0; i < split.length; i++) {
                if (!split[i].trim().equals("")) {
                    //stat.executeUpdate(sql + m + split[i] + m + ");");
                    System.out.println("Trying::>" + sql + m + split[i] + m + ");");


                }
            }
            stat.execute("CREATE TABLE Quotes_deduped like Quotes;");
            stat.execute("INSERT Quotes_deduped SELECT * FROM QUotes GROUP BY Quote;");
            stat.execute("RENAME TABLE Quotes TO Quotes_with_dupes;");
            stat.execute("RENAME TABLE Quotes_deduped TO Quotes;");
            stat.execute("DROP TABLE Quotes_with_dupes;");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

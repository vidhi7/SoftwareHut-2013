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
package omadaprogramming.server;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

/**
 * Pulls a quote out of database and return it as string
 *
 * @author Brasoveanu Andrei Alexandu
 */
public class PullQuote {

    //Create connection
    private static Statement stat;

    /**
     *
     * @return message A quote from database
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws SQLException
     */
    public static String getQuote() throws ClassNotFoundException, IOException, SQLException {
        InputStream in = PullQuote.class.getResourceAsStream("/database.properties");

        DataSource.init(in);
        Connection conn = DataSource.getConnection();
        stat = conn.createStatement();
        //Random generator = new Random();
        //int id = 1 + generator.nextInt(10);
        ResultSet result = stat.executeQuery("SELECT Quote FROM Quotes ORDER BY RAND() LIMIT 1");
        String message = "Quote:";
        while (result.next()) {

            message += "\n" + result.getString("Quote");

        }
        return message;

    }
}

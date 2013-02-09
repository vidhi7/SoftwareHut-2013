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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Creates a connection with the data base by using parameters in
 * database.properties
 *
 * @author Brasoveanu Andrei Alexandru
 *
 */
public class DataSource {

    private static String url;
    private static String username;
    private static String password;

    public static void init(InputStream fileName) throws IOException, ClassNotFoundException {
        //Gets the properties 
        Properties props = new Properties();
        
        props.load(fileName);

        String driver = props.getProperty("driver");
        url = props.getProperty("url");
        username = props.getProperty("username");
        if (username == null) {
            username = "";
        }
        password = props.getProperty("password");
        if (password == null) {
            password = "";
        }
        if (driver != null) {
            Class.forName(driver);
        }
    }

    /**
     * Creates connection
     *
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}

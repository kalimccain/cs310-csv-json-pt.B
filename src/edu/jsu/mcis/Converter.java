package edu.jsu.mcis;

import java.io.*;
import java.util.*;
import com.opencsv.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Converter {
    
    /*
    
        Consider the following CSV data:
        
        "ID","Total","Assignment 1","Assignment 2","Exam 1"
        "111278","611","146","128","337"
        "111352","867","227","228","412"
        "111373","461","96","90","275"
        "111305","835","220","217","398"
        "111399","898","226","229","443"
        "111160","454","77","125","252"
        "111276","579","130","111","338"
        "111241","973","236","237","500"
        
        The corresponding JSON data would be similar to the following (tabs and
        other whitespace have been added for clarity).  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings, and which values should be encoded as integers!
        
        {
            "colHeaders":["ID","Total","Assignment 1","Assignment 2","Exam 1"],
            "rowHeaders":["111278","111352","111373","111305","111399","111160",
            "111276","111241"],
            "data":[[611,146,128,337],
                    [867,227,228,412],
                    [461,96,90,275],
                    [835,220,217,398],
                    [898,226,229,443],
                    [454,77,125,252],
                    [579,130,111,338],
                    [973,236,237,500]
            ]
        }
    
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
    
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including example code.
    
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String results = "";
        
        try {
            //parse the string to list of string arrays
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> full = reader.readAll(); 
            //create an iterator
            Iterator<String[]> iterator = full.iterator();
            
            // INSERT YOUR CODE HERE
            JSONObject jsonObject = new JSONObject();
            JSONArray column_header = new JSONArray();
            JSONArray row_header = new JSONArray();
            JSONArray csvdata;
            JSONArray info = new JSONArray();
            String[] rows = iterator.next();
            
            for(int i=0;i<rows.length;i++){
                column_header.add(rows[i]);
            }
            
            while(iterator.hasNext()){
                rows = iterator.next();
                csvdata = new JSONArray();
                
                for(int i=0;i<rows.length;i++){
                    if(i == 0){
                        row_header.add(rows[i]);
                    }
                    else {
                        int dataToint = Integer.parseInt(rows[i]);
                        csvdata.add(dataToint);
                    }
                }
                
                info.add(csvdata);
            }
            
            jsonObject.put("colHeaders", column_header);
            jsonObject.put("rowHeaders", row_header);
            jsonObject.put("data", info);
            
            results = JSONValue.toJSONString(jsonObject);
        }        
        catch(Exception e) { return e.toString(); }
        
        return results.trim();
        
    }
    
    public static String jsonToCsv(String jsonString) {
        
        String results = "";
        
        try {

            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer, ',', '"', '\n');
            
            // INSERT YOUR CODE HERE 
            //create JSONParser
            JSONParser parser = new JSONParser();
            //invoke JSONParser to parse json string
            JSONObject jsonObject = (JSONObject)parser.parse(jsonString);
             //get headers 
            JSONArray columnJSONArray = (JSONArray)jsonObject.get("colHeaders");
            JSONArray rowJSONArray = (JSONArray)jsonObject.get("rowHeaders");
            JSONArray infoJSONArray = (JSONArray)jsonObject.get("data");
            
            String[] colHeaders = new String[5];
            
            for(int i=0;i<columnJSONArray.size(); ++i){
                colHeaders[i]= (String)columnJSONArray.get(i);
            }
            
            csvWriter.writeNext(colHeaders);
            
            for(int i =0; i<rowJSONArray.size();++i){
                JSONArray currentLine = (JSONArray)infoJSONArray.get(i);
                String[] current = new String[5];
                
                current[0] = (String)rowJSONArray.get(i);
                
                for (int j=0; j<currentLine.size(); ++j){
                    current[j+1] = Long.toString((long)currentLine.get(j));
                }
                
                csvWriter.writeNext(current);
            }
            
            results = writer.toString();
        }
        
        
        catch(Exception e) { return e.toString(); }
        
        return results.trim();
        
    }
    public static String getJSONArray(String jsonString){
       Connection conn = null;
       PreparedStatement pstSelect = null; pstUpdate = null;
       ResultSet resultset = null;
       ResultSetMetaData metadata= null;
       JSONArray jsonArray;
       
       String query, key, value;
       
       boolean hasresults;
       int resultCount, columnCount, updateCount = 0;
       
       try{
           
           String server = ("jdbc:mysql://localhost/p2_test");
           String username = "root";
           String password = "Roro91809";
           System.out.println("Connecting to "+ server+ "...");
           
           Class.forName("com.mysql.jdbc.Driver").newInstance();
           
           conn = DriverManager.getConnection(server,username,password);
           
           if (conn.isValid(0)){
               System.out.println("Connected Successfully");
               
               query = "INSERT INTO people (firstname, middleinitial,lastname,address, city, state, zip) "
                       + "      VALUES (?, ?, ?, ?, ?, ?, ?)";
               pstUpdate = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
               pstUpdate.setString(1, newFirstName);
               pstUpdate.setString(2, newMiddleInitial);
               pstUpdate.setString(3, newLastName);
               pstUpdate.setString(4, newAddress);
               pstUpdate.setString(5, newCity);
               pstUpdate.setString(6, newState);
               pstUpdate.setString(7, newZip);
               
               updateCount = pstUpdate.executeUpdate();
               
               if (updateCount>0){
                   resultset = pstUpdate.getGeneratedKeys();
                   
                   if(resultset.next()){
                       System.out.print("Update Successful! New Key: ");
                       System.out.println(resultset.getInt(1));
                   }
               }
               
               query = "SELECT * FROM people";
               pstSelect = conn.prepareStatement(query);
               
               System.out.println("Submitting Query ...");
               
               hasresults = pstSelect.execute();
               
               System.out.println("Getting Results ...");
               
               while (hasresults || pstSelect.getUpdateCount() != -1){
                   if (hasresults){
                       
                       resultset = pstSelect.getResultSet();
                       metadata = resultset.getMetaData();
                       columnCount = metadata.getColumncount();
                       
                       for (int i=1; i<columnCount;++i){
                           key = metadata.getColumnLabel(i);
                           System.out.format("%20s", key);
                       }
                       
                       while (resultset.next()){
                           System.out.println();
                           JSONObject jsonObject = new JSONObject();
                           
                           for (int i =0;i<=columnCount;i++){
                               String columnNames = ();
                           }
                           value.add(jsonObject);
                       }
                   }
                   
                   else {
                       resultCount = pstSelect.getUpdateCount();
                       
                       if (resultCount == -1){
                           break;
                       }
                   }
                   hasresults = pstSelect.getMoreResults();
               }
           }
           conn.close();
           return jsonArray;
       } catch (SQLException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
}
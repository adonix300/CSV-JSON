package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> listCSV = parseCSV(columnMapping, fileName);
        String json = listToJson(listCSV);
        writeString(json, "output.json");

        List<Employee> listXML = parseXML("data.xml");
        String json2 = listToJson(listXML);
        writeString(json2, "xml.json");

        String json3 = readString("xml.json");
        List<Employee> listJSON = jsonToList(json3);
        for (Employee employee : listJSON) {
            System.out.println(employee);
        }
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> employees = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            return employees = csvToBean.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));

            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element element = (Element) node;


                    int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = element.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());

                    Employee employee = new Employee(id, firstName, lastName, country, age);
                    employees.add(employee);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    private static String listToJson(List<Employee> list) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static void writeString(String jsonString, String fileName) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(jsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readString(String fileName) {
        StringBuilder jsonString = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return jsonString.toString();
    }

    private static List<Employee> jsonToList(String json) {
        List<Employee> employees = new ArrayList<>();
        JSONParser parser = new JSONParser();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            JSONArray jsonArray = (JSONArray) parser.parse(json);
            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                Employee employee = gson.fromJson(jsonObject.toString(), Employee.class);
                employees.add(employee);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return employees;
    }
}
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Main {
    public static void main(String[] args) throws IOException {
        String fileName = "data2.xml";
        List<Employee> list = parseXML(fileName);
        String json = listToJson(list);
        writeString(json);
    }


    public static List<Employee> parseXML(String fileName){
        File xmlFile = new File(fileName);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        List<Employee> employeeList = new ArrayList<>();

        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            // Получение узлов с именем "employee" в виде объекта Document
            NodeList nodeList = document.getElementsByTagName("employee");

            // Создание из списка объектов Document объектов Employee
            for (int i = 0; i < nodeList.getLength(); i++) {
                employeeList.add(getEmployee(nodeList.item(i)));
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return employeeList;
    }

    // Создание из узла документа объекта класса Employee
    private static Employee getEmployee(Node node) {
        Employee employee = new Employee();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            employee.setId(Long.parseLong(getTagValue("id", element)));
            employee.setFirstName(getTagValue("firstName", element));
            employee.setLastName(getTagValue("lastName", element));
            employee.setCountry(getTagValue("country", element));
            employee.setAge(Integer.parseInt(getTagValue("age", element)));
        }
        return employee;
    }

    // Получение значения элемента по указанному тегу
    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        return gson.toJson(list);
    }

    public static void writeString(String s) {
        try (FileOutputStream fos = new FileOutputStream("data.json")) {
            byte[] bytes = s.getBytes();
            fos.write(bytes, 0, bytes.length);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}

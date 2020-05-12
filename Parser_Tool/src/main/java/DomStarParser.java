import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class DomStarParser {

    private List<Star> myStars;
    private Document dom;

    public DomStarParser() {
        //create a list to hold the employee objects
        myStars = new ArrayList<>();
    }

    public List<Star> getMyStars() {
        return myStars;
    }

    public List<Star> runStarParser() {

        //parse the xml file and get the dom object
        parseXmlFile();

        //get each star element and create a Star object
        parseDocument();

        //Iterate through the list and print the data
        //printData();

        return getMyStars();

    }

    private void parseXmlFile() {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse("actors63.xml");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void parseDocument() {
        //get the root elememt
        Element docEle = dom.getDocumentElement();

        //get a nodelist of <film> elements
        NodeList nl = docEle.getElementsByTagName("actor");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {

                //get the employee element
                Element el = (Element) nl.item(i);

                //get the Employee object
                Star s = getStar(el);

                //add it to list
                myStars.add(s);
            }
        }
    }

    /**
     * I take an employee element and read the values in, create
     * an Employee object and return it
     *
     * @param starEl
     * @return
     */
    private Star getStar(Element starEl) {
        String name = getTextValue(starEl, "stagename"); // getting title
        int dob = getIntValue(starEl, "dob");

        //Create a new Employee with the value read from the xml nodes
        Star s = new Star(name, dob);

        return s;
    }

    /**
     * I take a xml element and the tag name, look for the tag and get
     * the text content
     * i.e for <employee><name>John</name></employee> xml snippet if
     * the Element points to employee node and tagName is name I will return John
     *
     * @param ele
     * @param tagName
     * @return
     */
    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            try {
                textVal = el.getFirstChild().getNodeValue();
            } catch (NullPointerException npe) {
                textVal = null;

            }
        }
        return textVal;
    }

    /**
     * Calls getTextValue and returns a int value
     *
     * @param ele
     * @param tagName
     * @return
     */
    private int getIntValue(Element ele, String tagName) {
        //in production application you would catch the exception
        int result = 0;
        try {
            result = Integer.parseInt(getTextValue(ele, tagName));
        } catch (NumberFormatException nfe) {
            // no birth year, needs to be turned to NULL when inserting
            result = 0;
        }

        return result;
    }

    /**
     * Iterate through the list and print the
     * content to console
     */
    private void printData() {

        System.out.println("No of Stars '" + myStars.size() + "'.");

        Iterator<Star> it = myStars.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

//    public static void main(String[] args) {
//        //create an instance
//        DomCastParser dpm = new DomCastParser();
//
//        //call run example
//        dpm.runStarParser();
//    }

}


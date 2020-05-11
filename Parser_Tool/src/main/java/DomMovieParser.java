import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;


public class DomMovieParser {

    private List<Movie> myMovies;
    private HashMap<String, String> genreCode = new HashMap<>();
    private Document dom;

    public DomMovieParser() {
        //create a list to hold the employee objects
        myMovies = new ArrayList<>();
    }

    public static void main(String[] args) {
        //create an instance
        DomMovieParser dpm = new DomMovieParser();

        //call run example
        dpm.runExample();
    }

    public void inititalizeGenreCode() {
        // use to decode genre
        this.genreCode.put("ctxx", "Unknown");
        this.genreCode.put("actn", "Violence");
        this.genreCode.put("camp", "Now-Camp");
        this.genreCode.put("comd", "Comedy");
        this.genreCode.put("disa", "Disaster");
        this.genreCode.put("epic", "Epic");
        this.genreCode.put("horr", "Horror");
        this.genreCode.put("noir", "Black");
        this.genreCode.put("scfi", "Science Fiction");
        this.genreCode.put("west", "Western");
        this.genreCode.put("advt", "Adventure");
        this.genreCode.put("cart", "Cartoon");
        this.genreCode.put("docu", "Documentary");
        this.genreCode.put("faml", "Family");
        this.genreCode.put("musc", "Music");
        this.genreCode.put("porn", "Pornography");
        this.genreCode.put("surl", "Sureal");
        this.genreCode.put("avGa", "Avant Garde");
        this.genreCode.put("cnr", "Cops and Robbers");
        this.genreCode.put("dram", "Drama");
        this.genreCode.put("hist", "History");
        this.genreCode.put("myst", "Mystery");
        this.genreCode.put("romt", "Romantic");
        this.genreCode.put("susp", "Thriller");
    }

    public List<Movie> getMyMovies() {
        return myMovies;
    }

    public void runExample() {

        inititalizeGenreCode();
        //parse the xml file and get the dom object
        parseXmlFile();

        //get each employee element and create a Employee object
        parseDocument();

        //Iterate through the list and print the data
        printData();

    }

    private void parseXmlFile() {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse("mains243.xml");

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
        NodeList nl = docEle.getElementsByTagName("film");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {

                //get the employee element
                Element el = (Element) nl.item(i);

                //get the Employee object
                Movie m = getMovie(el);

                //add it to list
                myMovies.add(m);
            }
        }
    }

    /**
     * I take an employee element and read the values in, create
     * an Employee object and return it
     *
     * @param movieEl
     * @return
     */
    private Movie getMovie(Element movieEl) {

        String id = getTextValue(movieEl, "fid");
        if (id == null) {
            // some movie id is tagged with <filmed> for some reason
            id = getTextValue(movieEl, "filmed");
        }
        String title = getTextValue(movieEl, "t"); // getting title
        int year = getIntValue(movieEl, "year");
        String director = getTextValue(movieEl, "dirn");
        String cat = getTextValue(movieEl, "cat");
        String genre = "";
        if (cat == null) {
            genre = "Unknown";
        } else {
            genre = this.genreCode.get(cat.trim().toLowerCase());
        }
        //Create a new Employee with the value read from the xml nodes
        Movie m = new Movie(id, title, year, director, genre);

        return m; // temporary return
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
                // no title in XML
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
            // no release year
            result = 0;
        }

        return result;
    }

    /**
     * Iterate through the list and print the
     * content to console
     */
    private void printData() {

        System.out.println("No of Movies '" + myMovies.size() + "'.");

        Iterator<Movie> it = myMovies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

}


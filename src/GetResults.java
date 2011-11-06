import java.io.IOException;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class GetResults 
{
	static XPathFactory factory = null;
	static XPath xpath = null;
	static XPathExpression expr = null;
	
	public int get(String site, String query) throws ParserConfigurationException, SAXException, IOException, 
XPathExpressionException
	{
		// Build the request.
		String requestURL = BuildRequest(site,query);

		// Send the request to the Live Search Service and get the response.
		Document doc = GetResponse(requestURL);
		int total = 0;
		
		if(doc != null)
		{
			// Display the response obtained from the Live Search Service.
			total = DisplayResponse(doc);
		}

		return total;
	}
	private static String BuildRequest(String site, String query)
	{
		// Replace the following string with the AppId you received from the
		// Live Search Developer Center.
	
		String AppId = "48231A5D0F1ED59F6D6B94A861758AF829869513";
		String requestString = "http://api.search.live.net/xml.aspx?"

			// Request fields
			+ "AppId=" + AppId
			+ "&Query= Site:"
			+ site + " "
			+ query + " "
			+ "&Sources=Web"
			+ "&Version=2.0"
			+ "&Market=en-us"
			+ "&Adult=Moderate"
			+ "&Web.Count=10"
			+ "&Web.Offset=0"
			+ "&Web.Options=DisableHostCollapsing+DisableQueryAlterations";


		return requestString;
	}

	private static Document GetResponse(String requestURL) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = null;
		DocumentBuilder db = dbf.newDocumentBuilder();

		if (db != null)
		{              
			doc = db.parse(requestURL);
		}

		return doc;
	}

	private static int DisplayResponse(Document doc) throws XPathExpressionException
	{
		factory = XPathFactory.newInstance();
		xpath = factory.newXPath();
		xpath.setNamespaceContext(new APINameSpaceContext());
		NodeList errors = (NodeList) xpath.evaluate("//api:Error",doc,XPathConstants.NODESET);
		int total = 0;
		
		if(errors != null && errors.getLength() > 0 )
		{
			// There are errors in the response. Display error details.
			DisplayErrors(errors);
		}
		else
		{
			total = DisplayResults(doc);
		}
		return total;
	}

	private static int DisplayResults(Document doc) throws XPathExpressionException 
	{
		String version = (String)xpath.evaluate("//@Version",doc,XPathConstants.STRING);
		String searchTerms = (String)xpath.evaluate("//api:SearchTerms",doc,XPathConstants.STRING);
		int total = Integer.parseInt((String)xpath.evaluate("//web:Web/web:Total",doc,XPathConstants.STRING));
		int offset = Integer.parseInt((String)xpath.evaluate("//web:Web/web:Offset",doc,XPathConstants.STRING));
		NodeList results = (NodeList)xpath.evaluate("//web:Web/web:Results/web:WebResult",doc,XPathConstants.NODESET); 

		// Display the results header.
		System.out.println("Live Search API Version " + version);
		System.out.println("Web results for " + searchTerms);
		System.out.println("Displaying " + (offset+1) + " to " + (offset + results.getLength()) + " of " + total + " results ");
		System.out.println();

		// Display the Web results.
		StringBuilder builder = new StringBuilder();

		for(int i = 0 ; i < results.getLength(); i++)
		{
			NodeList childNodes = results.item(i).getChildNodes();

			for (int j = 0; j < childNodes.getLength(); j++) 
			{
				if(!childNodes.item(j).getLocalName().equalsIgnoreCase("DisplayUrl"))
				{
					String fieldName = childNodes.item(j).getLocalName();

					if(fieldName.equalsIgnoreCase("DateTime"))
					{
						fieldName = "Last Crawled";
					}

					builder.append(fieldName + ":" + childNodes.item(j).getTextContent());
					builder.append("\n");
				}
			}

			builder.append("\n");
		}
	
		//System.out.println(builder.toString());
		
		return total;
	}

	private static void DisplayErrors(NodeList errors) 
	{
		System.out.println("Live Search API Errors:");
		System.out.println();

		for (int i = 0; i < errors.getLength(); i++) 
		{
			NodeList childNodes = errors.item(i).getChildNodes();

			for (int j = 0; j < childNodes.getLength(); j++) 
			{
				System.out.println(childNodes.item(j).getLocalName() + ":" + childNodes.item(j).getTextContent());
			}

			System.out.println();
		}
	}
}
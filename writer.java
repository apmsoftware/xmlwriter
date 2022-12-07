package xmlWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

public class writer {

	private static final String xmlFilePath = "C:\\Users\\Aarya\\Downloads\\xmlfile.xml";
	private static final String inputFilePath = "C:\\Users\\Aarya\\Downloads\\methods.txt";
	private static final String lookupFolderPath = "C:\\Users\\Aarya\\eclipse-workspace\\TestNG Suits\\src\\com\\testng\\suits";

	static ArrayList<String> methodNames = new ArrayList<String>();
	static HashMap<String, ArrayList<String>> map = new HashMap<>();

	public static void main(String[] args) throws ParserConfigurationException, TransformerException {
		// TODO Auto-generated method stub
		System.out.println("This will generate test suite from input file methods");

		FillMethodNames();
		SearchFileContents();
		createXmlFile();
	}

	private static void FillMethodNames() {
		// TODO Auto-generated method stub
		try {
			File inpFile = new File(inputFilePath);
			Scanner scanner = new Scanner(inpFile);
			while (scanner.hasNextLine()) {
				String line = (String) scanner.nextLine();
				methodNames.add(line.trim());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void createXmlFile() throws ParserConfigurationException, TransformerException {
		// TODO Auto-generated method stub
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();

		DOMImplementation domImpl = document.getImplementation();
		DocumentType doctype = domImpl.createDocumentType("doctype", "suite SYSTEM",
				"http://testng.org/testng-1.0.dtd");

		// suite element
		Element root = document.createElement("suite");
		document.appendChild(root);

		// set an attribute to root element
		Attr attrRoot = document.createAttribute("name");
		attrRoot.setValue("Suitename");
		root.setAttributeNode(attrRoot);

		// test element
		Element test = document.createElement("test");

		root.appendChild(test);

		// set an attribute to test element
		Attr attrTest = document.createAttribute("name");
		attrTest.setValue("Testname");
		test.setAttributeNode(attrTest);

		// classes element
		Element classes = document.createElement("classes");
		test.appendChild(classes);

		for (String key : map.keySet()) {
			// System.out.println( key );
			String classname = key;

			Element classEle = document.createElement("class");
			classes.appendChild(classEle);
			// set an attribute to class element
			Attr attrClass = document.createAttribute("name");
			attrClass.setValue(classname);
			classEle.setAttributeNode(attrClass);

			// methods element
			Element methods = document.createElement("methods");
			classEle.appendChild(methods);

			for (String methodname : map.get(key)) {
				// include element
				Element include = document.createElement("include");
				methods.appendChild(include);
				// set an attribute to class element
				Attr attrInclude = document.createAttribute("name");
				attrInclude.setValue(methodname);
				include.setAttributeNode(attrInclude);
			}
		}

		// create the xml file
		// transform the DOM Object to an XML File
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource domSource = new DOMSource(document);
		StreamResult streamResult = new StreamResult(new File(xmlFilePath));

		// If you use
		// StreamResult result = new StreamResult(System.out);
		// the output will be pushed to the standard output ...
		// You can use that for debugging

		transformer.transform(domSource, streamResult);

		System.out.println("Done creating XML File");
	}

	private static void SearchFileContents() {
		// TODO Auto-generated method stub
		
		File directoryPath = new File(lookupFolderPath);
		String contents[] = directoryPath.list();
		
		for (int i = 0; i < contents.length; i++) {
			if (contents[i].contains(".java")) {

				String file = contents[i];
				System.out.println(file);

				try {
					Scanner scanner = new Scanner(new File(lookupFolderPath + "\\" + file));

					while (scanner.hasNextLine()) {
						String line = (String) scanner.nextLine();
						// System.out.println(line);

						if (line.contains("package")) {
							file = line.split("\\s+")[1].replaceAll(";$", "") + "." + file.replaceAll(".java", "");
						}

						for (String method : methodNames) {
							if (line.contains(method)) {
								System.out.println(line);

								if (map.containsKey(file)) {
									ArrayList<String> values = map.get(file);
									values.add(method);
									map.put(file, values);
								} else {
									ArrayList<String> values = new ArrayList<String>();
									values.add(method);
									map.put(file, values);
								}
							}
						}
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}

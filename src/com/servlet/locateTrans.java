package com.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@WebServlet("/locateTrans")
public class locateTrans extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//final String apiKey = "gO2Eoip9hdgf3exFNc8KHuUOmZSRUctee1jwg4WNu5pvDzifwOM4BWJbdZIzn6A8w3uPYJIX0sAGnqeyw6PSeA%3D%3D";
	final String apiKey = "52ODSHWNllc5f%2Fyh6e%2F3S4X%2F7EjXNUOu8LtdMpcVzO0eMkl0a3qoS9gAlfjXM%2Fo9Oh2d8VOaosDioM3WsvQALg%3D%3D\r\n";	// �� apikey
       
    public locateTrans() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");	//response��ü�� �ѱ�ó���� ���� ���ڵ�
		request.setCharacterEncoding("UTF-8");	//request��ü�� �ѱ�ó���� ���� ���ڵ�
		
		String word = request.getParameter("locate");	//index.jsp�� �˻�� ������ ����
		
		PrintWriter out = response.getWriter();	
		
		if (word == "") {
			out.println("�˻�� �Է����ּ���.");	//�˻��� �Է�X
		} 
		else {
			out.println("<h3>�˻��� : " + word + "</h3><br>");	//�˻��� �Էµ�
			
			word = URLEncoder.encode(word, "UTF-8");	//�ѱ� �˻�� url �Ķ���ͷ� �����ϱ����� ���ڵ�

			//�ش� �˻��� ������ ����tm ��ǥ �˻� API ���� url
			String requestUrl = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt?umdName="	
					+ word + "&pageNo=1&numOfRows=10&ServiceKey=" + apiKey;
			
			BufferedReader br = null;	
	        //DocumentBuilderFactory ����
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        factory.setNamespaceAware(true);
	        DocumentBuilder builder;
	        Document doc = null;
	        
	        try {
	            URL url = new URL(requestUrl);
	            HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
	            
	            //���� �б�
	            br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(), "UTF-8"));
	            String result = "";
	            String line;
	            while ((line = br.readLine()) != null) {
	                result = result + line.trim() + " ";// result = URL�� XML��ü�� ���� ��
	            }
	            
	            // xml �Ľ��ϱ�
	            InputSource is = new InputSource(new StringReader(result));
	            builder = factory.newDocumentBuilder();
	            doc = builder.parse(is);
	            XPathFactory xpathFactory = XPathFactory.newInstance();
	            XPath xpath = xpathFactory.newXPath();
	            XPathExpression expr = xpath.compile("//items/item");	//�Ľ��� ������ <item>�±� �ȿ� �����Ƿ� <item>�±��� ��θ� ����
	            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
	            for (int i = 0; i < nodeList.getLength(); i++) {
	                NodeList child = nodeList.item(i).getChildNodes();
	                for (int j = 0; j < child.getLength(); j++) {
	                    Node node = child.item(j);
	                    if (node.getNodeName() == "sidoName" )	//�ش� �±׸� ����� ����
	                    	out.print("�ش����� ������ǥ : " + node.getTextContent() + " ");
	                    else if (node.getNodeName() == "sggName" )	//�ش� �±׸� ����� ����
	                    	out.print(node.getTextContent() + " ");
	                    else if (node.getNodeName() == "umdName" )	//�ش� �±׸� ����� ����
	                    	out.print(node.getTextContent() + "<br>");
	                    else if (node.getNodeName() == "tmX" )	//�ش� �±׸� ����� ����
	                    	out.print("tmX : " + node.getTextContent() + "<br>");
	                    else if (node.getNodeName() == "tmY" )	//�ش� �±׸� ����� ����
	                    	out.print("tmY : " + node.getTextContent() + "<br><br>");
	                    /*
	                    System.out.println("���� ��� �̸� : " + node.getNodeName());
	                    System.out.println("���� ��� Ÿ�� : " + node.getNodeType());
	                    System.out.println("���� ��� �� : " + node.getTextContent());
	                    System.out.println("���� ��� ���ӽ����̽� : " + node.getPrefix());
	                    System.out.println("���� ����� ���� ��� : " + node.getNextSibling());
	                    System.out.println("");*/
	                }
	            }
	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	        }
			
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

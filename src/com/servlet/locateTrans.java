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
	final String apiKey = "52ODSHWNllc5f%2Fyh6e%2F3S4X%2F7EjXNUOu8LtdMpcVzO0eMkl0a3qoS9gAlfjXM%2Fo9Oh2d8VOaosDioM3WsvQALg%3D%3D\r\n";	// 내 apikey
       
    public locateTrans() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");	//response객체의 한글처리를 위해 인코딩
		request.setCharacterEncoding("UTF-8");	//request객체의 한글처리를 위해 인코딩
		
		String word = request.getParameter("locate");	//index.jsp의 검색어를 가져와 저장
		
		PrintWriter out = response.getWriter();	
		
		if (word == "") {
			out.println("검색어를 입력해주세요.");	//검색어 입력X
		} 
		else {
			out.println("<h3>검색어 : " + word + "</h3><br>");	//검색어 입력됨
			
			word = URLEncoder.encode(word, "UTF-8");	//한글 검색어를 url 파라미터로 전달하기위해 인코딩

			//해당 검색어 지번의 기준tm 좌표 검색 API 접근 url
			String requestUrl = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt?umdName="	
					+ word + "&pageNo=1&numOfRows=10&ServiceKey=" + apiKey;
			
			BufferedReader br = null;	
	        //DocumentBuilderFactory 생성
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        factory.setNamespaceAware(true);
	        DocumentBuilder builder;
	        Document doc = null;
	        
	        try {
	            URL url = new URL(requestUrl);
	            HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
	            
	            //응답 읽기
	            br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(), "UTF-8"));
	            String result = "";
	            String line;
	            while ((line = br.readLine()) != null) {
	                result = result + line.trim() + " ";// result = URL로 XML전체를 읽은 값
	            }
	            
	            // xml 파싱하기
	            InputSource is = new InputSource(new StringReader(result));
	            builder = factory.newDocumentBuilder();
	            doc = builder.parse(is);
	            XPathFactory xpathFactory = XPathFactory.newInstance();
	            XPath xpath = xpathFactory.newXPath();
	            XPathExpression expr = xpath.compile("//items/item");	//파싱할 정보가 <item>태그 안에 있으므로 <item>태그의 경로를 지정
	            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
	            for (int i = 0; i < nodeList.getLength(); i++) {
	                NodeList child = nodeList.item(i).getChildNodes();
	                for (int j = 0; j < child.getLength(); j++) {
	                    Node node = child.item(j);
	                    if (node.getNodeName() == "sidoName" )	//해당 태그명 존재시 동작
	                    	out.print("해당지번 기준좌표 : " + node.getTextContent() + " ");
	                    else if (node.getNodeName() == "sggName" )	//해당 태그명 존재시 동작
	                    	out.print(node.getTextContent() + " ");
	                    else if (node.getNodeName() == "umdName" )	//해당 태그명 존재시 동작
	                    	out.print(node.getTextContent() + "<br>");
	                    else if (node.getNodeName() == "tmX" )	//해당 태그명 존재시 동작
	                    	out.print("tmX : " + node.getTextContent() + "<br>");
	                    else if (node.getNodeName() == "tmY" )	//해당 태그명 존재시 동작
	                    	out.print("tmY : " + node.getTextContent() + "<br><br>");
	                    /*
	                    System.out.println("현재 노드 이름 : " + node.getNodeName());
	                    System.out.println("현재 노드 타입 : " + node.getNodeType());
	                    System.out.println("현재 노드 값 : " + node.getTextContent());
	                    System.out.println("현재 노드 네임스페이스 : " + node.getPrefix());
	                    System.out.println("현재 노드의 다음 노드 : " + node.getNextSibling());
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

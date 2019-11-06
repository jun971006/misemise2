package com.servlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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

public class accessApi {	//API�� ������ ������ �����ϴ� �޼ҵ��� ���� Ŭ����
	//�������������� API�� Ű��
	final String apiKey = "gO2Eoip9hdgf3exFNc8KHuUOmZSRUctee1jwg4WNu5pvDzifwOM4BWJbdZIzn6A8w3uPYJIX0sAGnqeyw6PSeA%3D%3D";
	
	
	
	public String rocate2addr(String requestUrl) {	//����ġ ������ -> �����ּ� �޼ҵ�
		BufferedReader br = null;
    	//DocumentBuilderFactory ����
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder builder;
    	Document doc = null;
    	String retrnStr = null;
    	
    	factory.setNamespaceAware(true);
		try {
			
			
			URL url = new URL(requestUrl);
            HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
            
            //���� �б�
            br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(), "UTF-8"));
            String result = "";
            String line;
            while ((line = br.readLine()) != null) {
                result = result + line.trim() + " ";// result = URL�� XML�� ���� ��
            }
            
         	// xml �Ľ��ϱ�
            InputSource is = new InputSource(new StringReader(result));
            builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            XPathExpression expr = xpath.compile("/"); //�Ľ��� ������ �����ϴ� �±��� ��θ� ����
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                NodeList child = nodeList.item(i).getChildNodes();
                for (int j = 0; j < child.getLength(); j++) {
                    Node node = child.item(j);
                    if (node.getNodeName() == "result" );	//�ش� �±׸� ����� ����
                    	retrnStr = node.getTextContent();
                }
            }
			
		} catch (Exception e) {
            System.out.println(e.getMessage());
        }
		
		return retrnStr;
	}
	
	
	
	public String addr2TmRoc(String recentLoc) throws UnsupportedEncodingException { //������ �� �����ּ�(��⵵ �ǿս� ���ϵ�) -> ���� tm��ǥ �޼ҵ�
		recentLoc = URLEncoder.encode(recentLoc, "UTF-8");	//�ѱ��ּ��� url�Ķ���� ����� ���� ���ڵ�
		
		//���� url
		String requestUrl = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt?umdName="
				+ recentLoc + "&pageNo=1&numOfRows=10&ServiceKey=" + apiKey;
		
		String tmXY = null;	//tm��ǥ�� ������ ����
		
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
                result = result + line.trim() + " ";// result = URL�� XML�� ���� ��
            }
            
            // xml �Ľ��ϱ�
            InputSource is = new InputSource(new StringReader(result));
            builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            XPathExpression expr = xpath.compile("//items/item");	//�Ľ��� �±� ���
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                NodeList child = nodeList.item(i).getChildNodes();
                for (int j = 0; j < child.getLength(); j++) {
                    Node node = child.item(j);
                    if (node.getNodeName() == "tmX" ) //�ش� �±� �����
                    	tmXY = node.getTextContent() + " ";
                    else if (node.getNodeName() == "tmY" )	//�ش� �±� �����
                    	tmXY = tmXY + node.getTextContent();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        return tmXY;
        
	}
	
	
	//�ش� tm��ǥ���� ���� ����� ������ �˻�
	public String tmLoc2nearestStation(String tmX, String tmY) throws UnsupportedEncodingException {	
		tmX = URLEncoder.encode(tmX, "UTF-8");	//url�Ķ���� ����� ���� ���ڵ�
		tmY = URLEncoder.encode(tmY, "UTF-8");
		
		String station = null; 
		
		String requestUrl = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList"
        		+ "?tmX=" + tmX + "&tmY=" + tmY + "&ServiceKey=" + apiKey;
		
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
                result = result + line.trim() + " ";// result = URL�� XML�� ���� ��
            }
            
            // xml �Ľ��ϱ�
            InputSource is = new InputSource(new StringReader(result));
            builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            XPathExpression expr = xpath.compile("//items/item");
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < 1; i++) {
                NodeList child = nodeList.item(i).getChildNodes();
                for (int j = 0; j < child.getLength(); j++) {
                    Node node = child.item(j);
                    if ( node.getNodeName() == "stationName" ) {	//���� ����� ������ �� ����
                    	station = node.getTextContent();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        return station;
		
	}
	
	
	//�����Ҹ��� ���� ���������� ���� �޼ҵ�
	public String station2nowInfo(String station) throws UnsupportedEncodingException {
		
		station = URLEncoder.encode(station, "UTF-8");
		String buffer = null;	//���������� ����
		
		String requestUrl = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?stationName="
				+ station + "&dataTerm=month&pageNo=1&numOfRows=10&ServiceKey=" + apiKey + "&ver=1.3";
		
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
                result = result + line.trim() + " ";// result = URL�� XML�� ���� ��
            }
            
            // xml �Ľ��ϱ�
            InputSource is = new InputSource(new StringReader(result));
            builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            XPathExpression expr = xpath.compile("//items/item");
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < 1; i++) {
                NodeList child = nodeList.item(i).getChildNodes();
                for (int j = 0; j < child.getLength(); j++) {
                    Node node = child.item(j);
                    if (node.getNodeName() == "dataTime" ) 
                    	buffer = "&nbsp;&nbsp;���� �ð� : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "mangName" ) 
                    	buffer = buffer + "&nbsp;&nbsp;������ ���� : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "so2Value" ) 
                    	buffer = buffer + "&nbsp;&nbsp;��Ȳ�갡�� �� : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "coValue" ) 
                    	buffer = buffer + "&nbsp;&nbsp;�ϻ�ȭź�� �� : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "o3Value" ) 
                    	buffer = buffer + "&nbsp;&nbsp;���� �� : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "no2Value" ) 
                    	buffer = buffer + "&nbsp;&nbsp;�̻�ȭ���� �� : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "pm10Value" ) 
                    	buffer = buffer + "&nbsp;&nbsp;�̼�����(PM10) �� : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "pm10Value24" ) 
                    	buffer = buffer + "&nbsp;&nbsp;�̼�����(PM10) 24�ð� �����̵��� : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "pm25Value" ) 
                    	buffer = buffer + "&nbsp;&nbsp;�̼�����(PM2.5) �� : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "pm25Value24" ) 
                    	buffer = buffer + "&nbsp;&nbsp;�̼�����(PM2.5) 24�ð� �����̵��� : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "khaiValue" ) 
                    	buffer = buffer + "&nbsp;&nbsp;���մ��ȯ���ġ : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "so2Grade" ) 
                    	buffer = buffer + "&nbsp;&nbsp;��Ȳ�갡�� ���� : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "coGrade" ) 
                    	buffer = buffer + "&nbsp;&nbsp;�ϻ�ȭź�� ���� : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "o3Grade" ) 
                    	buffer = buffer + "&nbsp;&nbsp;���� ���� : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "no2Grade" ) 
                    	buffer = buffer + "&nbsp;&nbsp;�̻�ȭ���� ���� : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "pm10Grade" ) 
                    	buffer = buffer + "&nbsp;&nbsp;�̼�����(PM10) 24�ð� ��� : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "pm25Grade" ) 
                    	buffer = buffer + "&nbsp;&nbsp;�̼�����(PM2.5) 24�ð� ��� : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "pm10Grade1h" ) 
                    	buffer = buffer + "&nbsp;&nbsp;�̼�����(PM10) 1�ð� ��� : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "pm25Grade1h" ) 
                    	buffer = buffer + "&nbsp;&nbsp;�̼�����(PM2.5) 1�ð� ��� : " + node.getTextContent() + "<br>";
                    
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        return buffer;
		
	}
	
	
	public String date2forecast(String date) throws UnsupportedEncodingException {
		date = URLEncoder.encode(date, "UTF-8");
		String buffer = null;	//���������� ����
		
		String requestUrl = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMinuDustFrcstDspth?"
				+ "searchDate=" + date + "&ServiceKey=" + apiKey;
		
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
                result = result + line.trim() + " ";// result = URL�� XML�� ���� ��
            }
            
            // xml �Ľ��ϱ�
            InputSource is = new InputSource(new StringReader(result));
            builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            XPathExpression expr = xpath.compile("//items/item");
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for(int i=0; i<nodeList.getLength();i++) {
            	NodeList child = nodeList.item(i).getChildNodes();
            	for(int j=0; j<child.getLength(); j++) {
            		Node node = child.item(j);
            		if(node.getNodeName()=="dataTime") {
            			buffer = buffer + "&nbsp;&nbsp;�뺸 �ð� : " + node.getTextContent() + "<br>";
            		}
            		else if(node.getNodeName()=="informOverall") {
            			buffer = buffer + "&nbsp;&nbsp;���� ��� : " + node.getTextContent() + "<br>";
            		}
            		else if(node.getNodeName()=="informCode") {
            			buffer = buffer + "&nbsp;&nbsp;�뺸 �ڵ� : " + node.getTextContent() + "<br>";
            		}
            		else if(node.getNodeName()=="informGrade") {
            			buffer = buffer + "&nbsp;&nbsp;���� ��� : " + node.getTextContent() + "<br>";
            		}
            		else if(node.getNodeName()=="informData") {
            			buffer = buffer + "&nbsp;&nbsp;���� �뺸 �ð� : " + node.getTextContent() + "<br>";
            		}
            	}
            	buffer = buffer + "<br>";
            }            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
		
		
		return buffer;
	}
	public String place2forecast(String place) throws UnsupportedEncodingException {
		place = URLEncoder.encode(place, "UTF-8");
		String buffer = null;	//���������� ����
		
		String requestUrl = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureLIst?"
				+ "itemCode=PM10&dataGubun=HOUR&searchCondition=MONTH&pageNo=1&numOfRows=20&ServiceKey=" + apiKey;
		
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
                result = result + line.trim() + " ";// result = URL�� XML�� ���� ��
            }
            
            // xml �Ľ��ϱ�
            InputSource is = new InputSource(new StringReader(result));
            builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            XPathExpression expr = xpath.compile("//items/item");
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for(int i=0; i<nodeList.getLength();i++) {
            	NodeList child = nodeList.item(i).getChildNodes();
            	for(int j=0; j<child.getLength(); j++) {
            		Node node = child.item(j);
            		if(node.getNodeName()=="dataTime") {
            			buffer = buffer + "&nbsp;&nbsp;�뺸 �ð� : " + node.getTextContent() + "<br>";
            		}
            		else if(node.getNodeName()=="gwangju") {
            			buffer = buffer + "&nbsp;&nbsp; : " + node.getTextContent() + "<br>";
            		}
            	}
            	buffer = buffer + "<br>";
            }            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
		

		return buffer;
	}
	
	public String area2English(String place) throws UnsupportedEncodingException {
		//place = URLEncoder.encode(place, "UTF-8");
		String EngArea = null;
		if(place=="����Ư����") {
			EngArea="seoul";
		}else if(place=="��⵵") {
			EngArea="gyeonggi";
		}else if(place=="�λ걤����") {
			EngArea="busan";
		}else if(place=="��걤����") {
			EngArea="ulsan";
		}else if(place=="�뱸������") {
			EngArea="daegu";
		}else if(place=="������") {
			EngArea="gangwon";
		}else if(place=="���ֵ�") {
			EngArea="jeju";
		}else if(place=="����Ư����ġ��") {
			EngArea="sejong";
		}else if(place=="���󳲵�") {
			EngArea="jeonam";
		}else if(place=="����ϵ�") {
			EngArea="jeonbuk";
		}else if(place=="��󳲵�") {
			EngArea="gyeongnam";
		}else if(place=="���ϵ�") {
			EngArea="gyeongbuk";
		}else if(place=="��û����") {
			EngArea="chungnam";
		}else if(place=="��û�ϵ�") {
			EngArea="chungbuk";
		}else if(place=="����������") {
			EngArea="daejeon";
		}else if(place=="���ֱ�����") {
			EngArea="gwangju";
		}else if(place=="��õ������") {
			EngArea="incheon";
		}
		
		
		return EngArea;
	}
}

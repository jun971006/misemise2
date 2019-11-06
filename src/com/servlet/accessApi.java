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

public class accessApi {	//API에 접속해 정보를 추출하는 메소드의 집합 클래스
	//공공데이터포털 API의 키값
	final String apiKey = "gO2Eoip9hdgf3exFNc8KHuUOmZSRUctee1jwg4WNu5pvDzifwOM4BWJbdZIzn6A8w3uPYJIX0sAGnqeyw6PSeA%3D%3D";
	
	
	
	public String rocate2addr(String requestUrl) {	//현위치 경위도 -> 지번주소 메소드
		BufferedReader br = null;
    	//DocumentBuilderFactory 생성
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder builder;
    	Document doc = null;
    	String retrnStr = null;
    	
    	factory.setNamespaceAware(true);
		try {
			
			
			URL url = new URL(requestUrl);
            HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
            
            //응답 읽기
            br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(), "UTF-8"));
            String result = "";
            String line;
            while ((line = br.readLine()) != null) {
                result = result + line.trim() + " ";// result = URL로 XML을 읽은 값
            }
            
         	// xml 파싱하기
            InputSource is = new InputSource(new StringReader(result));
            builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            XPathExpression expr = xpath.compile("/"); //파싱할 정보가 존재하는 태그의 경로를 지정
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                NodeList child = nodeList.item(i).getChildNodes();
                for (int j = 0; j < child.getLength(); j++) {
                    Node node = child.item(j);
                    if (node.getNodeName() == "result" );	//해당 태그명 존재시 동작
                    	retrnStr = node.getTextContent();
                }
            }
			
		} catch (Exception e) {
            System.out.println(e.getMessage());
        }
		
		return retrnStr;
	}
	
	
	
	public String addr2TmRoc(String recentLoc) throws UnsupportedEncodingException { //번지를 뺀 지번주소(경기도 의왕시 월암동) -> 기준 tm좌표 메소드
		recentLoc = URLEncoder.encode(recentLoc, "UTF-8");	//한글주소의 url파라미터 사용을 위한 인코딩
		
		//접근 url
		String requestUrl = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getTMStdrCrdnt?umdName="
				+ recentLoc + "&pageNo=1&numOfRows=10&ServiceKey=" + apiKey;
		
		String tmXY = null;	//tm좌표를 저장할 변수
		
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
                result = result + line.trim() + " ";// result = URL로 XML을 읽은 값
            }
            
            // xml 파싱하기
            InputSource is = new InputSource(new StringReader(result));
            builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            XPathExpression expr = xpath.compile("//items/item");	//파싱할 태그 경로
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                NodeList child = nodeList.item(i).getChildNodes();
                for (int j = 0; j < child.getLength(); j++) {
                    Node node = child.item(j);
                    if (node.getNodeName() == "tmX" ) //해당 태그 존재시
                    	tmXY = node.getTextContent() + " ";
                    else if (node.getNodeName() == "tmY" )	//해당 태그 존재시
                    	tmXY = tmXY + node.getTextContent();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        return tmXY;
        
	}
	
	
	//해당 tm좌표에서 가장 가까운 측정소 검색
	public String tmLoc2nearestStation(String tmX, String tmY) throws UnsupportedEncodingException {	
		tmX = URLEncoder.encode(tmX, "UTF-8");	//url파라미터 사용을 위한 인코딩
		tmY = URLEncoder.encode(tmY, "UTF-8");
		
		String station = null; 
		
		String requestUrl = "http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList"
        		+ "?tmX=" + tmX + "&tmY=" + tmY + "&ServiceKey=" + apiKey;
		
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
                result = result + line.trim() + " ";// result = URL로 XML을 읽은 값
            }
            
            // xml 파싱하기
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
                    if ( node.getNodeName() == "stationName" ) {	//가장 가까운 측정소 명 선택
                    	station = node.getTextContent();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        return station;
		
	}
	
	
	//측정소명을 통해 대기오염정보 추출 메소드
	public String station2nowInfo(String station) throws UnsupportedEncodingException {
		
		station = URLEncoder.encode(station, "UTF-8");
		String buffer = null;	//대기오염정보 저장
		
		String requestUrl = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?stationName="
				+ station + "&dataTerm=month&pageNo=1&numOfRows=10&ServiceKey=" + apiKey + "&ver=1.3";
		
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
                result = result + line.trim() + " ";// result = URL로 XML을 읽은 값
            }
            
            // xml 파싱하기
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
                    	buffer = "&nbsp;&nbsp;측정 시간 : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "mangName" ) 
                    	buffer = buffer + "&nbsp;&nbsp;측정망 정보 : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "so2Value" ) 
                    	buffer = buffer + "&nbsp;&nbsp;아황산가스 농도 : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "coValue" ) 
                    	buffer = buffer + "&nbsp;&nbsp;일산화탄소 농도 : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "o3Value" ) 
                    	buffer = buffer + "&nbsp;&nbsp;오존 농도 : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "no2Value" ) 
                    	buffer = buffer + "&nbsp;&nbsp;이산화질소 농도 : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "pm10Value" ) 
                    	buffer = buffer + "&nbsp;&nbsp;미세먼지(PM10) 농도 : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "pm10Value24" ) 
                    	buffer = buffer + "&nbsp;&nbsp;미세먼지(PM10) 24시간 예측이동농도 : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "pm25Value" ) 
                    	buffer = buffer + "&nbsp;&nbsp;미세먼지(PM2.5) 농도 : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "pm25Value24" ) 
                    	buffer = buffer + "&nbsp;&nbsp;미세먼지(PM2.5) 24시간 예측이동농도 : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "khaiValue" ) 
                    	buffer = buffer + "&nbsp;&nbsp;통합대기환경수치 : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "so2Grade" ) 
                    	buffer = buffer + "&nbsp;&nbsp;아황산가스 지수 : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "coGrade" ) 
                    	buffer = buffer + "&nbsp;&nbsp;일산화탄소 지수 : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "o3Grade" ) 
                    	buffer = buffer + "&nbsp;&nbsp;오존 지수 : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "no2Grade" ) 
                    	buffer = buffer + "&nbsp;&nbsp;이산화질소 지수 : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "pm10Grade" ) 
                    	buffer = buffer + "&nbsp;&nbsp;미세먼지(PM10) 24시간 등급 : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "pm25Grade" ) 
                    	buffer = buffer + "&nbsp;&nbsp;미세먼지(PM2.5) 24시간 등급 : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "pm10Grade1h" ) 
                    	buffer = buffer + "&nbsp;&nbsp;미세먼지(PM10) 1시간 등급 : " + node.getTextContent() + "<br>";
                    else if (node.getNodeName() == "pm25Grade1h" ) 
                    	buffer = buffer + "&nbsp;&nbsp;미세먼지(PM2.5) 1시간 등급 : " + node.getTextContent() + "<br>";
                    
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        return buffer;
		
	}
	
	
	public String date2forecast(String date) throws UnsupportedEncodingException {
		date = URLEncoder.encode(date, "UTF-8");
		String buffer = null;	//대기오염정보 저장
		
		String requestUrl = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMinuDustFrcstDspth?"
				+ "searchDate=" + date + "&ServiceKey=" + apiKey;
		
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
                result = result + line.trim() + " ";// result = URL로 XML을 읽은 값
            }
            
            // xml 파싱하기
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
            			buffer = buffer + "&nbsp;&nbsp;통보 시간 : " + node.getTextContent() + "<br>";
            		}
            		else if(node.getNodeName()=="informOverall") {
            			buffer = buffer + "&nbsp;&nbsp;예보 요약 : " + node.getTextContent() + "<br>";
            		}
            		else if(node.getNodeName()=="informCode") {
            			buffer = buffer + "&nbsp;&nbsp;통보 코드 : " + node.getTextContent() + "<br>";
            		}
            		else if(node.getNodeName()=="informGrade") {
            			buffer = buffer + "&nbsp;&nbsp;예보 등급 : " + node.getTextContent() + "<br>";
            		}
            		else if(node.getNodeName()=="informData") {
            			buffer = buffer + "&nbsp;&nbsp;예보 통보 시간 : " + node.getTextContent() + "<br>";
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
		String buffer = null;	//대기오염정보 저장
		
		String requestUrl = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureLIst?"
				+ "itemCode=PM10&dataGubun=HOUR&searchCondition=MONTH&pageNo=1&numOfRows=20&ServiceKey=" + apiKey;
		
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
                result = result + line.trim() + " ";// result = URL로 XML을 읽은 값
            }
            
            // xml 파싱하기
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
            			buffer = buffer + "&nbsp;&nbsp;통보 시간 : " + node.getTextContent() + "<br>";
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
		if(place=="서울특별시") {
			EngArea="seoul";
		}else if(place=="경기도") {
			EngArea="gyeonggi";
		}else if(place=="부산광역시") {
			EngArea="busan";
		}else if(place=="울산광역시") {
			EngArea="ulsan";
		}else if(place=="대구광역시") {
			EngArea="daegu";
		}else if(place=="강원도") {
			EngArea="gangwon";
		}else if(place=="제주도") {
			EngArea="jeju";
		}else if(place=="세종특별자치시") {
			EngArea="sejong";
		}else if(place=="전라남도") {
			EngArea="jeonam";
		}else if(place=="전라북도") {
			EngArea="jeonbuk";
		}else if(place=="경상남도") {
			EngArea="gyeongnam";
		}else if(place=="경상북도") {
			EngArea="gyeongbuk";
		}else if(place=="충청남도") {
			EngArea="chungnam";
		}else if(place=="충청북도") {
			EngArea="chungbuk";
		}else if(place=="대전광역시") {
			EngArea="daejeon";
		}else if(place=="광주광역시") {
			EngArea="gwangju";
		}else if(place=="인천광역시") {
			EngArea="incheon";
		}
		
		
		return EngArea;
	}
}

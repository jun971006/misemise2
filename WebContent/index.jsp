<%@ page import="java.io.BufferedReader"%>
<%@ page import="javax.xml.parsers.DocumentBuilder"%>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page import="java.net.URL"%>
<%@ page import="java.net.HttpURLConnection"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.io.InputStreamReader"%>
<%@ page import="java.io.StringReader"%>
<%@ page import="java.io.InputStreamReader"%>
<%@ page import="javax.xml.xpath.XPath"%>
<%@ page import="javax.xml.xpath.XPathConstants"%>
<%@ page import="javax.xml.xpath.XPathExpression"%>
<%@ page import="javax.xml.xpath.XPathFactory"%>
<%@ page import="org.w3c.dom.Node"%>
<%@ page import="org.w3c.dom.NodeList"%>
<%@ page import="org.xml.sax.InputSource"%>
<%@page import="com.servlet.accessApi" %>


<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%@ page import="java.util.*"%>
<%@ page import="java.text.SimpleDateFormat"%>	

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>미세미세 따라하기</title>
</head>
<body>
	<h1>미세미세 따라하기</h1>
	<h3>내위치</h3>
	<!-- <div id="msg"></div> -->

	<!-- 지오로케이션  -->
	<!-- 
	<script type="text/javascript">
	
		if (!!navigator.geolocation) {
			navigator.geolocation.watchPosition(successCallback,
					errorCallback);
		} else {
			alert("이 브라우저는 위치정보(Geolocation)를 지원하지 않습니다");
		}

		function successCallback(position) { //위치정보 성공 콜백
			
			var lat = position.coords.latitude;
			var lng = position.coords.longitude;

			//document.getElementById("msg").innerHTML = "위도: " + lat + "\r\r, 경도: "
			//		+ lng;
					
		}

		function errorCallback(error) { //위치정보 실패 콜백
			alert(error.message);
		}
	</script> -->
	<%
		String station = null;	//현위치에서 가장 가까운 측정소명
		String tmX = null;		//현위치 지번의 기준 tm좌표 X
		String tmY = null;		//현위치 지번의 기준 tm좌표 Y 
		
		String lat = request.getParameter("lat");	//start.jsp에서 url파라미터로 보낸 경도 받아오기
		String lng = request.getParameter("lng");	//start.jsp에서 url파라미터로 보낸 위도 받아오기
		String requestUrl = "http://apis.vworld.kr/coord2jibun.do?x=" + 	//경위도 -> 지번주소 변경 API에 접속할 url
				lng + "&y=" + lat + "&apiKey=92E4D429-2636-3C8E-88BA-D37598CCBADB"; 		// 내 apikey
		
		accessApi api = new accessApi();	//accessApi.java파일의 accessApi클래스 객체 생성(메서드 사용을 위해)
		
		String recentRoc = api.rocate2addr(requestUrl);	//경위도 -> 지번주소 변경값을 저장
		out.println("&nbsp;&nbsp;"+recentRoc + "<br>");	//현위치 지번주소를 출력
		
		recentRoc = recentRoc.substring(0, recentRoc.lastIndexOf(" "));	//지번주소의 번지를 제외한 나머지 주소 ex)경기도 의왛시 월암동 22-1 -> 경기도 의왕시 월암동
			
		String place = recentRoc.substring(0, 2);
		out.println("&nbsp;&nbsp;"+place + "<br>");	//현위치 지번주소를 출력
		
		tmX = api.addr2TmRoc(recentRoc);	//현위치 지번주소(경기도 의왕시 월암동) -> 해당주소 기준 tm좌표   로 변경(반환값은 "tmX tmY"문자열)
		tmY = tmX.substring(tmX.lastIndexOf(" "));	//합쳐진 tmX tmY 문자열을 나누어 저장
		tmX = tmX.substring(0, tmX.lastIndexOf(" "));	//합쳐진 tmX tmY 문자열을 나누어 저장
		
		station = api.tmLoc2nearestStation(tmX, tmY);	//현위치 지번주소(경기도 의왕시 월암동)의 기준 tm좌표로 가장 가까운 측정소명 검색
		
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		Date time = new Date();
		String date = format1.format(time);
		
		
		 
		out.println(date);
		
		
		
		
	%> 
	<form action="start.jsp" method="get">	
		&nbsp;&nbsp;<input type="submit" value="위치정보 세로고침">
	</form>
		
	<h3>내위치 대기정보</h3>	
	<%	
		out.print(api.station2nowInfo(station));	//해당 측정소명의 대기오염정보를 출력
	%>		
		
	<h3>대기상태 검색(읍면동)</h3>
	<form action="locateTrans" method="post">
		<input type="text" name="locate">
		<input type="submit" value="Search">
	</form>
	
	<h3>대기질 예보통보 조회</h3>
	<%
		out.print(api.date2forecast(date));	
	%>
	
	<h3>DAILY 예보통보 조회</h3>
	<%
		out.print(api.place2forecast(place));	
	%>
</body>
</html>
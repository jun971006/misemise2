<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>미세미세 따라하기</title>
</head>
<body>
	<script type="text/javascript">
		function getLocation() {
			if (navigator.geolocation) { // GPS를 지원하면
				navigator.geolocation.getCurrentPosition(function(position) {
					
					var lat = position.coords.latitude;
					var lng = position.coords.longitude;
					
					window.location.replace("index.jsp?lat=" + lat + "&lng=" + lng);	//경위도를 index.jsp로 분기하면서  url파라미터로 전송
					
				}, function(error) {
					console.error(error);
				}, {
					enableHighAccuracy : true,	//조금 더 정확한 위치정보
					maximumAge : 0,
					timeout : Infinity
				});
			} else {
				alert('GPS를 지원하지 않습니다');
			}
		}
		getLocation();
	</script>	
	
</body>
</html>
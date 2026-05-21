package com.kh.opendata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OpenDataProjectApplicationTests {

	// 필드부
	// > 인증키 정보를 코드 String 변수 상에 대놓고 쓰면 가독성이 떨어짐
	//   인증키는 왠만해서는 한번 발급되면 절대 안바뀜 (재발급 요청만 안한다면)
	// > 인증키 정보는 상수 필드로 빼둘것임!!
	public static final String SERVICEKEY = "KWDm5Y8Ln%2F8smfC1iRl0iqF9v40STW5%2FKn3WpSVeuOmoYKpTlwupMMfV1AlKGcAOmyknzWXpkGknaK4ut9oLpw%3D%3D";
	
	@Test
	void contextLoads() throws IOException {
		
		// https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?serviceKey=KWDm5Y8Ln%2F8smfC1iRl0iqF9v40STW5%2FKn3WpSVeuOmoYKpTlwupMMfV1AlKGcAOmyknzWXpkGknaK4ut9oLpw%3D%3D&returnType=json&numOfRows=2&pageNo=1&sidoName=%EC%84%B8%EC%A2%85&ver=1.0
		// > 이쪽으로 요청을 보내서 응답데이터 받아오기 (자바코드로)
		
		// 1. OpenAPI 서버로 요청하고자 하는 url 주소를 String 으로 만들기
		String url = "https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty";
		
		// GET 방식이므로 뒤에 딸린 쿼리스트링 (?key=value&key=value&...) 을 이어 붙이기
		url += "?serviceKey=" + SERVICEKEY;
		url += "&returnType=json";
		url += "&numOfRows=5";
		url += "pageNo=1";
		url += "&sidoName=서울";
		
		// System.out.println(url);
		// > Unauthorized : 인증키를 정확히 쓸 것!! 오류 발생에 주의!!
		
		// 2. 자바 구문을 이용해서 OpenAPI 요청하기
		URL requestUrl = new URL(url);
		
		HttpURLConnection urlConnection = (HttpURLConnection)requestUrl.openConnection();
		
		urlConnection.setRequestMethod("GET");
		
		// 3. 응답데이터를 받아올 입력스트림 객체를 얻어내기
		// > 요청한 서버와의 "입력스트림" 을 열어야 함!! (응답데이터를 읽어들일것)
		// > 응답데이터에 한글이 포함되어있으므로 문자스트림 사용 예정
		//   + 속도 향상을 위해 보조스트림도 사용할 예정
		BufferedReader br = new BufferedReader(
							new InputStreamReader(urlConnection.getInputStream()));
		
		// 4. br.readLine() 로 while 문을 통해 읽어들이기
		
		
		
	}

}
package com.kh.ajax.controller;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AjaxController {

	// Ajax 요청이 들어오면 기존 동기식 방식 때와 동일하게
	// Controller 클래스와 메소드를 작성해준다!!
	
	// Ajax 요청 시 전달값 뽑기 1. HttpServletRequest 객체 이용
	/*
	@GetMapping("jqAjax1")
	public void ajaxMethod1(HttpServletRequest request) {
		
		// System.out.println("잘 호출되나?");
		
		String input = request.getParameter("input");
		System.out.println("요청 시 전달값 : " + input);
	}
	*/
	
	// Ajax 요청 시 전달값 뽑기 2. @RequestParam 어노테이션 이용
	/*
	@GetMapping("jqAjax1")
	public void ajaxMethod2(@RequestParam(value="input") String param1) {
		
		System.out.println("요청 시 전달값 : " + param1);
	}
	*/
	
	// Ajax 요청 시 전달값 뽑기 3. 키값과 동일한 이름의 매개변수 정의
	// (@RequestParam 어노테이션 생략하는 방법)
	
	// Ajax 요청 시 전달값 뽑기 4. 커맨드 객체 방식
	// > 마찬가지로 요청 시 전달값들도 기존의 동기식 방식이랑 동일하게 뽑아낸다.
	
	// ---------------------------
	
	// Ajax 요청 시 응답 데이터 전달하기 1. ServletResponse 객체 이용
	/*
	@GetMapping("jqAjax1")
	public void ajaxMethod1(String input, HttpServletResponse response) throws IOException {
		
		// System.out.println(input);
		// > 이 전달값을 가지고 DB 를 거쳐 왔다 라는 가정 하에 응답할 데이터 셋팅
		
		String responseData = "문자열의 길이는 " + input.length() + "입니다.";
		
		// 1. 응답데이터의 형식 및 인코딩 설정
		response.setContentType("text/html; charset=UTF-8");
		
		// 2. 요청했었던 jsp 와의 "출력스트림" 객체 생성해두기
		PrintWriter out = response.getWriter();
		
		// 3. 만들어진 출력 통로를 통해 응답데이터를 넘겨주기
		out.print(responseData);
	}
	*/
	
	// Ajax 요청 시 응답 데이터 전달하기 2. 응답할 데이터를 문자열로 반환해주기
	// > 단, 문자열을 그냥 리턴하면 DispatcherServlet 이 리턴된 문자열을
	//   응답데이터가 아닌 "응답페이지" 정보로 인식하게 된다!!
	// > 그래서 지금부터 내가 리턴하는 문자열이 "응답페이지" 정보가 아니라 "응답데이터" 임을 알려주는
	//   어노테이션을 반드시 붙여줘야 한다!!
	@ResponseBody // 지금부터 내가 리턴하는 문자열은 응답데이터임을 알려주는 어노테이션
	@GetMapping("jqAjax1")
	public String ajaxMethod1(String input) {
		
		// > 마찬가지로 DB 를 거쳐 왔다라는 가정 하에 응답데이터를 셋팅하면
		String responseData = "문자열의 길이는 " + input.length() + "입니다.";
		
		return responseData;
	}
	
	@ResponseBody
	@PostMapping("jqAjax2")
	public String ajaxMethod2(String name, int age) {
		
		// System.out.println(name);
		// System.out.println(age);
		
		return name + "님은 " + age + "살 입니다.";
	}
	
	// -------------------------
	
	// 다수의 응답데이터가 있을 경우
	/*
	@GetMapping("jqAjax3")
	public void ajaxMethod3(int math, int eng, HttpServletResponse response) throws IOException {
		
		// System.out.println(math);
		// System.out.println(eng);
		
		// 응답데이터로 두 점수의 총합과 평균을 보내볼 것!!
		int total = math + eng;
		double avg = (math + eng) / 2;
		
		response.setContentType("text/html, charset=UTF-8");
		
		PrintWriter out = response.getWriter();
		
		out.print(total);
		out.print(avg);
		// > response 객체에서 제공하는 출력스트림을 통해 여러개의 응답 데이터를 내보낼 경우
		//   그 응답데이터를 받아주는 success function 에서 여러개의 응답 데이터가
		//   하나의 문자열로 연이어져서 도착하게 된다!! (스트림은 선입선출이라서)
	
	}
	*/
	
	/*
	@ResponseBody
	@GetMapping("jqAjax3")
	public String ajaxMethod(int math, int eng) {
		
		int total = math + eng;
		double avg = (math + eng) / 2;
		
		return total + ", " + avg;
	}
	*/
	
	@ResponseBody
	@GetMapping(value="jqAjax3", produces="application/json; charset=UTF-8")
	public String ajaxMethod3(int math, int eng, HttpServletResponse response) throws IOException {
		
		int total = math + eng;
		double avg = total / 2;
		
		/*
		 * * JSON (JavaScript Object Notation)
		 * - 자바스크립트 객체 표기법
		 * - Ajax 응답 시 데이터 전송에 사용되는 포맷 형식 중 하나
		 * - 여러개의 데이터를 하나로 뭉칠 수 있음 (JS 의 객체 형식으로 가공할 수 있음)
		 * - 기본적으로 자바에서는 제공해주지 않기 때문에 JSON 을 이용하려면 라이브러리 (.jar) 를 연동해야함!!
		 *   (json-simple.jar 추가)
		 *   
		 * * JSON 처리 시 사용되는 클래스 종류 (응답 데이터가 여러개일 경우 사용)
		 * 1. JSONArray : [value, value, ...]
		 * - 즉, 자바스크립트의 배열 형식으로 묶어서 한번에 응답데이터들을 보내겠다.
		 * 
		 * 2. JSONObject : {key : value, key : value, ...}
		 * - 즉, 자바스크립트의 배열 형식으로 묶어서 한번에 응답데이터들을 보내겠다.
		 */
		
		// - JSONArray 이용
		/*
		JSONArray jArr = new JSONArray(); // []
		jArr.add(total); // [total]
		jArr.add(avg); // [total, avg]
		// > JSONArray 는 자바의 ArratList 와 같은 구조
		//				 자바스크립트의 배열과 같은 구조
		
		// response.setContentType("text/html; charset=UTF-8");
		// > "[185, 92.0]" 라는 문자열로 응답데이터가 넘어감!! (배열인척 하는 문자열)
		// > JSON 형식을 응답데이터로 넘기고자 할 경우에는 content type 을 아래와 같이 지정해야 함!!
		// response.setContentType("application/json; charset=UTF-8");
		// response.getWriter().print(jArr);
	
		return jArr.toJSONString(); // "[185, 92.0]"
		*/
		
		// - JSONObject 이용
		JSONObject jObj = new JSONObject(); // {}
		jObj.put("total", total); // {total : 185}
		jObj.put("avg", avg); // {total : 185, avg : 92.0}
		// > JSONObject 는 자바의 HashMap 과 같은 구조
		//				  자바스크립트의 객체 같은 구조
		
		// response.setContentType("application/json; charset=UTF-8");
		// response.getWriter().print(jObj);
		
		return jObj.toJSONString(); // "{total : 185, avg : 92.0}"
		// > 굳이 복잡하게 HttpServletResponse 객체를 쓰는 대신에
		//   JSON 의 문자열 형식을 리턴해서 응답데이터를 넘길 수 있다!! (produces 설정 필수)
	}


}
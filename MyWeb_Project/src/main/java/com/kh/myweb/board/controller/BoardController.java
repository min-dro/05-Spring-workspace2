package com.kh.myweb.board.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.kh.myweb.board.model.service.BoardService;
import com.kh.myweb.board.model.vo.Attachment;
import com.kh.myweb.board.model.vo.Board;
import com.kh.myweb.common.model.vo.PageInfo;
import com.kh.myweb.common.template.Pagination;
import com.kh.myweb.common.template.XssDefencePolicy;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("board")
public class BoardController {
	
	@Autowired
	private BoardService boardService;

	@GetMapping("list")
	public ModelAndView selectBoardList(@RequestParam(value="cpage", defaultValue="1") int currentPage, ModelAndView mv) {
		
		// System.out.println("현재 요청한 페이지 : " + currentPage);
		
		// 일반게시판 목록 조회 기능 구현 (+ 페이징 처리)
		
		// * 페이징 처리 (Pagination)
		// > 리스트 조회 시 한 페이지 당 조회할 건수가 너무 많을 때
		//   한 페이지 당 n 개씩 끊어서 보여질 수 있도록 처리해주는 효과
		
		// --- 페이징 처리 ---
		// 필요한 변수는 총 7개!!
		// > 기본적으로 구할 수 있는 4개의 변수 + 그 4개의 변수를 통해 계산해서 도출해야 하는 3개의 변수
		
		// > 기본적으로 구할 수 있는 4개의 변수
		int listCount; // 현재 총 게시글의 갯수 (단, 삭제되지 않은 일반게시글의 갯수)
		// int currentPage; // 현재 사용자가 보고자 하는 페이지 (즉, 사용자가 요청한 페이지)
		// > 이미 매개변수로 요청 시 전달값으로 받아내고 있음!!
		int pageLimit; // 페이지 하단에 보여질 페이징바의 페이지 최대 갯수
		int boardLimit; // 한 페이지에 보여질 게시글의 최대 갯수 (즉, 한 페이지당 몇개씩 볼거냐)
		
		// 위의 4개의 변수들로 계산해서 구해야 하는 3개의 변수
		int maxPage; // 가장 마지막 페이지가 몇 번 페이지인지 (즉, 총 페이지 수)
		int startPage; // 페이지 하단에 보여질 페이징바의 시작수
		int endPage; // 페이지 하단에 보여질 페이징바의 끝수
		
		// * listCount : 총 게시글의 갯수
		// > BOARD 테이블의 유효한 데이터의 갯수를 COUNT 함수로 세오기!!
		listCount = boardService.selectListCount();
		
		// * currentPage : 현재 사용자가 요청한 페이지
		// > 이미 위에서 매개변수로 요청 시 전달값으로 cpage 라는 키값으로 넘겨받았음!!
		
		// * pageLimit : 페이지 하단에 보여질 페이징바의 페이지 최대 갯수
		// > 한 페이지 당 페이지 목록들을 몇 개 단위씩 보여질건지 임의의 값으로 지정하기
		//   (알고리즘 수식 계산의 편의를 위해 10으로 셋팅할 것)
		pageLimit = 10;
		
		// * boardLimit : 한페이지에 보여질 게시글의 최대 갯수
		// > 한 페이지 당 게시글이 몇 개 씩 보여질건지 임의의 값으로 지정하기
		//   (알고리즘 수식 계산의 편의를 위해 10으로 셋팅할 것)
		boardLimit = 10;
		
		// 매번 listCount, currentPage, pageLimit, boardLimit 를 통해
		// maxPage, startPage, endPage 를 일일이 계산하는 코드를 작성하기 귀찮음!!
		// > 마찬가지로 공통 코드 작업을 해둘 것!!
		
		// Pagination 클래스를 생성하고 그 안에 getPageInfo 라는 메소드를 만들것!!
		PageInfo pi = Pagination.getPageInfo(listCount, currentPage, 
											 pageLimit, boardLimit);
		
		// System.out.println(listCount);
		// System.out.println(currentPage);
		// System.out.println(pageLimit);
		// System.out.println(boardLimit);
		// System.out.println(maxPage);
		// System.out.println(startPage);
		// System.out.println(endPage);
		
		// * 페이징 처리의 원리
		// > 게시글들을 최신순으로 정렬 후 페이지 구간별로 끊어서 조회해오는 것!!
		//   이떄 위에서 구한 7 개의 변수들이 쿼리문에 영향을 미치기 때문에,
		//	 7 개의 변수를 DAO 전달값으로 넘겨줄 것임!!
		
		// > 위의 7 개의 변수를 각 필드로 갖고있는 VO 클래스를 하나 만들 것!!
		// > PageInfo 라는 VO 클래스를 만들어서 각 변수를 필드로 가공해서 한번에 매개변수로 넘길 예정
		//   (한번 만들어 두면 공지사항, 일반게시판, 사진게시판 등 목록 조회 시
		//    페이징 처리가 필요할 때 마다 계속 재활용해서 쓸 수 있게됨!!)
		
		/*
		PageInfo pi = new PageInfo(listCount, currentPage, pageLimit, boardLimit,
								   maxPage, startPage, endPage);
		*/
		
		// pi 를 전달하면서 Service 로 요청 후 결과 받기
		ArrayList<Board> list = boardService.selectBoardList(pi);
		
		/*
		for(Board b : list) {
			
			System.out.println(b);
		}
		*/
		// > 107 ~ 98 / 97 ~ 88 / 87 ~ 78 / ...
		//	 (최신 게시글 기준으로 위에서부터 10개씩)
		
		// 현재 사용자가 요청한 페이지 (currentPage) 에 보여질 게시글 리스트를 
		// 응답데이터로 넘기기
		mv.addObject("list", list);
		
		// 또한, 요청 페이지 하단에 보여질 페이징바를 만드려면 위의 7개의 변수를 담은
		// pi 도 응답데이터로 넘겨줘야함!!
		mv.addObject("pi", pi);
		
		// 우선 응답페이지를 만들어서 띄워보기
		mv.setViewName("board/boardListView");
		// > /WEB-INF/views/board/boardListView.jsp
		
		return mv;
	}

	@GetMapping("search")
	public ModelAndView searchBoardList(String condition, String keyword, 
								@RequestParam(value="cpage", defaultValue="1") int currentPage,
								ModelAndView mv) {
		
		// System.out.println(condition);
		// > 검색 조건 : "writer" / "title" / "content"
		
		// System.out.println(keyword);
		// > 사용자가 입력한 검색어 : "ad" / "10" / "입니다"
		
		// 위의 두 값을 가지고 검색을 하되, 페이징 처리까지 해 줘야 한다!!
		// > 기본적으로 검색 시 검색 결과를 중 1번 페이지가 보여져야함!!
		//   또한 검색 결과들 중 n번 페이지를 사용자가 요청할 수도 있음
		//   (currentPage 값도 얻어내야함, 또한 7개의 변수도 마저 셋팅해야함)
		
		// 우선 총 검색된 게시글의 갯수를 먼저 구할 것!!
		// > condition, keyword 두개 다 넘기면서 쿼리문을 실행하고 와야 함!!
		// 1. condition, keyword 라는 두개의 필드를 가진 VO 를 만들고 객체로 만들어서 넘기기
		// 2. condition, keyword 라는 값을 HashMap 에 담아서 넘기기
		
		// > HashMap 이용해보기!!
		HashMap<String, String> map = new HashMap<>();
		map.put("condition", condition);
		map.put("keyword", keyword);
		
		int searchCount = boardService.selectSearchCount(map);
		
		// int currentPage;
		
		int pageLimit = 10;
		int boardLimit = 10;
		
		// System.out.println(searchCount);
		// System.out.println(currentPage);
		
		// 위의 searchCount, currentPage, pageLimit, boardLimit 를 가지도
		// maxPage, startPage, endPage 를 계산해서 구해야함!!
		// > 그리고 이걸 모두 PageInfo 로 한번에 가공해야함!!
		PageInfo pi = Pagination.getPageInfo(searchCount, currentPage,
											 pageLimit, boardLimit);
		
		// 위의 HashMap 과 PageInfo 둘 다 넘기면서 검색용 쿼리문을 실행해서 결과를 받아야함!!
		ArrayList<Board> list = boardService.selectBoardList(map, pi);
		
		/*
		for(Board b : list) {
			
			System.out.println(b);
		}
		*/
		
		// 위에서 구해진 list 와 pi 를 응답데이터로 넘기면서 결과 화면 포워딩
		// > 기존의 게시글 목록 페이지 (boardListView.jsp) 를 재활용
		
		// 이 때, 검색 결과 창에 검색 조건, 검색어가 그대로 노출되었으면 좋겠음!!
		// 검색 결과 창에서 페이징바를 클릭하면 다음 페이지로 넘어가면 검색이 풀리는 이슈도 있음
		
		// 해결방법)
		// > 응답 데이터로 condition, keyword 를 다시 넘겨 주면 됨!!
		
		/*
		mv.addObject("list", list);
		mv.addObject("pi", pi);
		mv.addObject("conditon", condition);
		mv.addObject("keyword", keyword);
		
		mv.setViewName("board/boardListView");
		*/
		
		// * ModelAndView 객체의 addObject 메소드는 return 타입이 ModelAndView 다!!
		// > 즉, mv 객체 자기자신을 리턴한다.
		mv.addObject("list", list)
		  .addObject("pi", pi)
		  .addObject("condition", condition)
		  .addObject("keyword", keyword)
		  .setViewName("board/boardListView");;
		// > 그래서 위와 같이 메소드 체이닝이 가능하다!! (호출 순서 주의)
		  
		return mv;
	}
	
	@GetMapping("enrollForm")
	public String enrollForm() {
		
		return "board/boardEnrollForm";
		// > WEB-INF/views/board/boardEnrollForm.jsp
	}
	
	@PostMapping("insert")
	public String insertBoard(Board b, MultipartFile upfile, Model model, HttpSession session) {
		// > 스프링에서 요청 시 전달값 중 첨부파일을 받고싶다면
		//   MultipartFile 타입의 매개변수 객체로 받아준다!!
		//   (단, 입력 form 에서 추가적인 속성을 작성해야함)
		// > 이 때, 파일 업로드와 관련된 Spring 설정을 추가해야한다!! (application.properties 파일)
		
		// 요청 시 전달값들을 VO 로 받기 (커맨드 객체 방식)
		// System.out.println(b);
		// > category, boardTitle, boardContent, boardWriter
		
		// System.out.println(upFile);
		// > MultipartFile 객체의 getOriginalFilename() 이라는 메소드를 호출해보자!!
		//   넘어온 첨부파일의 원래 파일 명을 문자열로 리턴해주는 메소드임
		// System.out.println(upfile.getOriginalFilename());
		// > "bono.jpg" / "kuromi.png" / "" (빈 문자열)
		//    넘어온 첨부파일이 있다면 원본파일명, 넘어온 첨부파일이 없다면 빈 문자열이 리턴됨!!
		
		// upfile 로 넘겨받은 첨부파일의 정보를 Attachment VO 객체로 가공
		Attachment at = null;
		
		// 요청 시 넘어온 첨부파일이 있는지 부터 검사
		if(!upfile.getOriginalFilename().equals("")) {
			// > 넘어온 첨부파일이 있을 경우
			
			// 파일명 수정 작업 후 서버로 업로드
			// (파일의 이름을 바꿔서 서버 컴퓨터에 해당 파일을 다운로드 하기)
			// > 서버 컴퓨터의 어떤 폴더 안에 업로드된 파일들을 모두 다 저장하는 과정에서
			//   우연치 않게 동일한 이름의 파일 여러개가 업로드 될 수 있으므로!! 이름이 겹치지 않도록!!
			
			// 예) "bono.jpg" -> "2026051315345612345.jpg"
			// > 우리는 최대한 수정한 파일명들이 겹치지 않게끔
			//   년월일시분초 + 랜덤수5자리 + 확장자명 으로 파일명을 수정해줄 예정!!
			
			// 1. 원본파일명 뽑아오기
			String originName = upfile.getOriginalFilename(); // "bono.jpg"
			
			// 2. 시간 형식을 문자열로 뽑아내기
			String currentTime = new SimpleDateFormat("yyyyMMddHHmmss")
									.format(new Date()); // "20260513153954"
			
			// 3. 뒤에 붙을 5자리 랜덤수 뽑기
			// > 10000 ~ 99999 사이로
			int ranNum = (int)(Math.random() * 90000 + 10000); // 13159
			
			// 4. 원본파일명으로부터 확장자명 뽑기
			String ext = originName.substring(originName.lastIndexOf(".")); // ".jpg"
			// > 혹시라도 파일명 중간에 .이 있을 수도 있어서 마지막 .의 위치를 기준으로 substring
			
			// 5. 2 + 3 + 4 모두 이어 붙이기
			String changeName = currentTime + ranNum + ext;
			// > "2026051314395413159.jpg"
			
			// 6. 업로드 하고자 하는 서버 폴더의 물리적인 경로를 알아내기
			// > 이 프로젝트 폴더 내부에 저장할 것!!
			//   (이 프로젝트 1개 == 웹사이트 1개 == applicationScope)
			// > applicationScope 내장객체 (ServletContent 타입) 로부터 저장할 경로를 알아내기
			// > application 내장객체는 session 으로 부터 얻어낼 수 있다!!
			
			// 업로드된 파일들은 webapp 폴더의 resources/board_upfiles 폴더에 저장할 예정!!
			String savePath = session.getServletContext()
							  .getRealPath("/resources/board_upfiles/");
			// 앞의 / 는 webapp 폴더를 나타내고, 뒤의 / 는 해당 폴더 "내부" 를 뜻함
			//   (폴더 안에 파일이 저장되므로)
			// > "C드라이브" 에서 부터 시작되는 board_upfiles 폴더의 풀 경로 (절대경로)
			
			// 7. 경로와 수정파일명 합체 후 파일 업로드 하기
			try {
				upfile.transferTo(new File(savePath + changeName));
			} catch(IOException e) {
				e.printStackTrace();
			}
			// > 실제 파일이 업로드 된다!!
			
			// 8. 첨부파일에 대한 벙보를 Attachment at 객체에 담기
			at = new Attachment();
			at.setOriginName(originName); // 원본명 (사용자가 넘긴 이름)
			at.setChangeName(changeName); // 수정명 (실제 서버에 업로드 되어있는 이름)
			at.setFilePath("resources/board_upfiles");
			
		}
		
		// System.out.println(b);
		// System.out.println(at);
		// > 넘어온 첨부파일이 없다면 at == null
		//   넘어온 첨부파일이 있다면 at != null (originName, changeName, filePath)
		// 단, XSS 공격 방지 처리 하고 넘어가기
		b.setBoardTitle(XssDefencePolicy.defence(b.getBoardTitle()));
		b.setBoardContent(XssDefencePolicy.defence(b.getBoardContent()));
		// Service 로 b, at 모두 다 넘기면서 요청 후 결과 받기 하나의 트랜잭션으로 묶기 위함)
		int result = boardService.insertBoard(b, at);
		
		if(result > 0) {
			
			session.setAttribute("alertMsg", "게시글을 등록했습니다.");
			
			return "redirect:/";
		} else {
			model.addAttribute("errorMsg", "게시글을 등록하지 못했습니다.");
			
			return "common/errorPage";
		}
	}
		
}
package com.kh.myweb.board.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.myweb.board.model.dao.BoardDao;
import com.kh.myweb.board.model.vo.Attachment;
import com.kh.myweb.board.model.vo.Board;
import com.kh.myweb.common.model.vo.PageInfo;

@Service
public class BoardService {

	@Autowired
	private SqlSessionTemplate sqlSession;
	
	@Autowired
	private BoardDao boardDao;
	
	public int selectListCount() {
	
		return boardDao.selectListCount(sqlSession);
	}
	
	public ArrayList<Board> selectBoardList(PageInfo pi) {
		
		return boardDao.selectBoardList(sqlSession, pi);
	}

	public int selectSearchCount(HashMap<String, String> map) {
		
		// map 에 담겨 넘어온 값 중 검색 조건을 나타내는 condition 값이
		// "writer" 또는 "title" 또는 "content" 와 일치하는지를 따져서
		// 각 경우의 수 마다 알맞은 조건에 따른 검색된 결과의 갯수를
		// 각각 조회해와야 한다!! (조건문을 통해)
		// > 원칙이긴 하나 길고 복잡해짐!!
		
		// 그래서 우리는 map 을 곧바로 dao 로 넘기면서 한번만 호출할 것!!
		// > 마이바티스의 동적 SQL 을 이용함
		return boardDao.selectSearchCount(sqlSession, map);
	}

	public ArrayList<Board> selectBoardList(HashMap<String, String> map, 
											PageInfo pi) {

		return boardDao.searchBoardList(sqlSession, map, pi);
	}

	@Transactional
	public int insertBoard(Board b, Attachment at) {

		// 넘어온 첨부파일이 있든 없든 간에 무조건 BOART 테이블에 먼저 INSERT 는 해야함!
		// > BOARD 테이블에 INSERT 구문을 실행시켜줄 DAO 만 먼저 다녀오기!!
		int result = boardDao.insertBoard(sqlSession, b);
		// > BOARD 에 INSERT 가 잘 되었다면 1, 아니라면 0
		
		// 넘어온 첨부파일이 있을 경우에는 ATTACHMENT 테이블에 INSERT 를 또 해야함!!
		// > 넘어온 첨부파일이 없으면 이 과정은 패스!!
		if(at != null) {
			// > 넘어온 첨부파일이 있을 경우
			
			int result2 = boardDao.insertAttachment(sqlSession, at);
			// > ATTACHMENT 에 INSERT 가 잘 되었다면 1, 아니라면 0
		}
		
		// result1 과 result2 가 모두 1이라면 최종 성공
		// 하나하도 0이라면 실패		
		
	}
	
}

	
	
	
	
	

	
	

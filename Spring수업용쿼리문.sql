
-- 1. 로그인용 쿼리문
-- 사용자로부터 입력받은 아이디와 비번
-- 단, STATUS = 'N' 인 경우는 통과되면 안됨!! (탈퇴된 회원이기 때문)
SELECT *
  FROM MEMBER
 WHERE USER_ID = ?
   AND USER_PWD = ?
   AND STATUS = 'Y'
   
-- 2. 회원가입용 쿼리문
INSERT INTO MEMBER(USER_NO
                 , USER_ID
                 , USER_PWD
                 , USER_NAME
                 , PHONE
                 , EMAIL
                 , ADDRESS)
            VALUES(SEQ_UNO.NEXTVAL
                 , ?
                 , ?
                 , ?
                 , ?
                 , ?
                 , ?)
                 
-- 3. 회원 정보 변경용 쿼리문
-- 아이디 (고정, 식별자 역할), 이름, 전화번호, 이메일, 주소
-- + MODIFY_DATE (회원정보수정일) 까지 같이 수정
UPDATE MEMBER
   SET USER_NAME = ?
     , PHONE = ?
     , EMAIL = ?
     , ADDRESS = ?
     , MODIFY_DATE = SYSDATE
 WHERE USER_ID = ?
   AND STATUS = 'Y'
--> 탈퇴되지 않은 회원의 경우만 정확히 수정하기 위해

-- 4. 비밀번호 변경용 쿼리문
-- 현재 비밀번호, 변경할 비밀번호 를 입력받아서 넘겼었음!!
UPDATE MEMBER
   SET USER_PWD = ?
     , MODIFY_DATE = SYSDATE
 WHERE USER_ID = ?
   AND STATUS = 'Y'

-- 5. 회원 탈퇴용 쿼리문
-- DELETE 문이 아닌 UPDATE 문으로 처리할 예정!!
-- 특히 외래키 이슈 때문!!
UPDATE MEMBER
   SET STATUS = 'N'
     , MODIFY_DATE = SYSDATE
 WHERE USER_ID = ?
   AND STATUS = 'Y'
   
-------------------------------------------------------------------------

-- 6. 공지사항 목록 조회용 쿼리문
SELECT NOTICE_NO
     , NOTICE_TITLE
     , USER_ID
     , COUNT
     , CREATE_DATE
  FROM NOTICE
  JOIN MEMBER ON (NOTICE_WRITER = USER_NO)
 WHERE N.STATUS = 'Y'
 ORDER BY NOTICE_NO DESC
 
-- 7. 공지사항 작성용 쿼리문
INSERT INTO NOTICE(NOTICE_NO
                 , NOTICE_TITLE
                 , NOTICE_CONTENT
                 , NOTICE_WRITER)
            VALUES(SEQ_NNO.NEXTVAL
                 , ?
                 , ?
                 , ?)
                 
-- 8. 공지사항 조회수 증가용 쿼리문
-- 기존의 조회수에 1 증가한값을 다시 대입
UPDATE NOTICE
   SET COUNT = COUNT + 1
 WHERE NOTICE_NO = ?
   AND STATUS = 'Y'
   
-- 9. 공지사항 상세조회용 쿼리문
-- 제목, 작성자의 아이디, 작성일, 내용이 출력되야함
-- 눈에 노출되지는 않지만 상세보기 페이지에서 추후 수정, 삭제까지 이어갈려면
-- PRIMARY KEY 제약조건에 해당하는 컬럼인 NOTICE_NO 까지 같이 조회해야함!!
SELECT NOTICE_NO
     , NOTICE_TITLE
     , NORICE_CONTENT
     , USER_ID
     , CREATE_DATE
  FROM NOTICE N
  JOIN MEMBER ON (NOTICE_WRITER = USER_NO)
 WHERE NOTICE_NO = ?
   AND N.STATUS = 'Y'
   
-- 10. 공지사항 수정용 쿼리문
UPDATE NOTICE
   SET NOTICE_TITLE = ?
     , NOTICE_CONTENT = ?
 WHERE NOTICE_NO = ?
   AND STATUS = 'Y'
   
-- 11. 공자사항 삭제용 쿼리문
-- 회원탈퇴와 마찬가지로 공지사항 삭제 또한 UPDATE 로 처리할 것
UPDATE NOTICE
   SET STATUS = 'N'
 WHERE NOTICE_NO = ?
   AND STATUS = 'Y'
   
--------------------------------------------------------------------------------

-- 12. 일반게시글의 총 갯수를 구하는 쿼리문
-- 단, 일반게시글로 삭제되지 않은 것들만 세와야함!!
SELECT COUNT(*)
  FROM BOARD
 WHERE BOARD_TYPE = 1
   AND STATUS = 'Y'
   
-- 13. 페이징 처리가 적용된 게시글의 목록을 조회하는 쿼리문
-- 페이징 처리의 원리 : 우선 게시글들을 최신순으로 정렬 후 위에서부터 boardLimit 만큼 게시글을 조회해옴
-- 글번호, 카테고리명, 글제목, 작성자의 아이디, 조회수, 작성일
SELECT *
FROM
    (
    SELECT ROWNUM RNUM, A.*
      FROM (
        SELECT BOARD_NO
             , CATEGORY_NAME AS CATEGORY
             , BOARD_TITLE
             , USER_ID
             , COUNT
             , CREATE_DATE
          FROM BOARD B
          JOIN CATEGORY C USING (CATEGORY_NO)
          JOIN MEMBER ON (BOARD_WRITER = USER_NO)
          WHERE BOARD_TYPE = 1 
            AND B.STATUS = 'Y'
          ORDER BY BOARD_NO DESC
        ) A
    )
WHERE RNUM BETWEEN ? AND ?
--> 우선 최신순으로 정렬이 먼저 일어나도록 인라인뷰 사용!!
--> ROWNUM 컬럼 특성 상 1부터 시작하는 값만 조회 가능하기 때문에 해결하기 위해서는 ROWNUM 컬럼에 별칭 부여!!
--  (단, WHERE 절에서는 SELECT 절에서 제시한 별칭 사용 불가!! 그래서 인라인뷰를 한겹 더 쓴것!!)

--> 14. 검색된 게시글의 갯수를 구하는 쿼리문
SELECT COUNT(*)
    FROM BOARD B
    JOIN MEMBER ON (BOARD_WRITER = USER_NO)
 WHERE BOARD_TYPE = 1
    AND B.STATUS = 'Y' -- 전체 107 건
--    AND USER_ID LIKE '%' || ? || '%' -- 작성자로 검색 (writer=ad) - 총 54건
--    AND BOARD_TITLE LIKE '%' || ? || '%'-- 제목으로 검색 (title=10) - 총 9건
    AND BOARD_CONTENT LIKE '%' || ? || '%' -- 내용으로 검색 (content=입니다) - 총 107건
--> 하단 AND 절은 사용자가 어떤 condition 값을 넘겼냐에 따라 바뀌고,
--  ? (위치홀더, 구멍) 자리에는 사용자가 입력한 keyword 가 들어가야 한다!











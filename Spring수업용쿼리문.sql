
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
-- 메뉴 목록
CREATE  TABLE   MENUS (
     MENU_ID     VARCHAR2(6)   PRIMARY KEY
    ,MENU_NAME   VARCHAR2(100)
    ,MENU_SEQ    NUMBER(5)
);

INSERT  INTO   MENUS  VALUES ('MENU01', 'JAVA', 1 );
COMMIT;

-- 회원정보
CREATE   TABLE   TUSER (
     USERID      VARCHAR2(12)  PRIMARY KEY
   , PASSWD      VARCHAR2(12)  NOT NULL
   , USERNAME    VARCHAR2(30)  NOT NULL
   , EMAIL       VARCHAR2(320)
   , UPOINT      NUMBER(10)    DEFAULT 1000  
   , INDATE      DATE          DEFAULT SYSDATE
);

-- 멀티 게시판 - 구조변경
DROP    TABLE  BOARD;
CREATE  TABLE  BOARD (
	  BNO	       NUMBER(8,0)           PRIMARY  KEY
	, MENU_ID      VARCHAR2(6)
	      REFERENCES   MENUS ( MENU_ID )   
    , TITLE	       VARCHAR2(240 BYTE)    NOT NULL
    , CONTENT	   VARCHAR2(4000 BYTE)
    , WRITER	   VARCHAR2(12 BYTE)      
    , REGDATE	   DATE                  DEFAULT  SYSDATE
    , HIT	       NUMBER(9,0)           DEFAULT  0  
);

--- 자료실 용 추가
---------------------------------------
--- 자료실을 위한 FIELS 테이블 생성
---------------------------------------
CREATE  TABLE  FILES
(
     FILE_NUM    NUMBER(6,0)    NOT NULL    -- 파일번호
   , BNO         NUMBER(6,0)    NOT NULL    -- 게시글 번호
   , FILENAME    VARCHAR2(300)  NOT NULL    -- 파일이름
   , FILEEXT     VARCHAR2(300)  NOT NULL    -- 파일확장자
   , SFILENAME   VARCHAR2(300)  NOT NULL    -- 저장된 실제 파일명
   
   , CONTRAINT   FILES_PK       ORIMARY KEY   -- 기본기(복합키)
   (
       FILE_NUM,
       BNO
   )
   , CONSTRAINT   FK_BOARD_FILES_BNO
     FOREIGN KEY  (BNO)
     REFERENCES   BOARD(BNO)
     ON  DELETE   CASCADE
);




















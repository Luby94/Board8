package com.board.pds.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdsVo {
	// Board 자료  
	private  int     bno;	
	private  String  title;
	private  String  content;
	private  String  writer;
	private  String  regdate;
	private  int     hit;
	
	// File 정보 - 파일 자료수
	private  int     filescount;
	
	// 메뉴정보
	private  String  menu_id;
	private  String  menu_name;
	private  int     menu_seq;
}






package com.board.controller;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.board.domain.BoardVo;
import com.board.domain.BoardVo;
import com.board.domain.Pagination;
import com.board.domain.PagingResponse;
import com.board.domain.SearchVo;
import com.board.mapper.BoardPagingMapper;
import com.board.menus.domain.MenuVo;
import com.board.menus.mapper.MenuMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/BoardPaging")
public class BoardPagingController {
	
	@Autowired
	private   MenuMapper          menuMapper;
	
	@Autowired
	private   BoardPagingMapper   boardPagingMapper;
	
//  /BoardPaging/List?nowpage=1&menu_id=MENU01&title=&writer=
	@RequestMapping("/List")
	public   ModelAndView   list(int nowpage, BoardVo  boardVo) {
		
		log.info("boardVo : {}", boardVo );
		
		// 메뉴 목록
		List<MenuVo>  menuList   =  menuMapper.getMenuList();
				
		// 게시물 목록  페이징
		 // 조건에 해당하는 데이터가 없는 경우, 
		 // 응답 데이터에 비어있는 리스트와 null을 담아 반환
		  // count : 현재 Menu_id 의 데이터 총 자료수를 알려준다 - 페이지번호 출력하기 위해
		  // menu_id=MENU01&title=&writer=
		  // title 과 writer 는 검색기능에 필요
        int count = boardPagingMapper.count( boardVo );
        PagingResponse<BoardVo> response = null;
        if (count < 1) {
        	response =  new PagingResponse<>(Collections.emptyList(), null);
        }

        // 페이징을 위한 초기설정값
        SearchVo    searchVo   =  new SearchVo();
        searchVo.setPage(nowpage);
        searchVo.setPageSize(10); // 기본10개 -> 20개
        
        // Pagination 객체를 생성해서 페이지 정보 계산 후 SearchDto 타입의 객체인 params에 계산된 페이지 정보 저장
        Pagination pagination = new Pagination(count, searchVo);
        searchVo.setPagination(pagination);
        
        String      menu_id   =  boardVo.getMenu_id();
        String      title     =  boardVo.getTitle();
        String      writer    =  boardVo.getWriter();        
        int         offset    =  searchVo.getOffset();
        int         pageSize  =  searchVo.getPageSize();

        // 계산된 페이지 정보의 일부(limitStart, recordSize)를 기준으로 리스트 데이터 조회 후 응답 데이터 반환
        List<BoardVo> list = boardPagingMapper.getBoardPagingList(
        		menu_id, title, writer, offset, pageSize);
        response =  new PagingResponse<>(list, pagination);
		
		System.out.println( response );
				
		ModelAndView  mv         =  new ModelAndView();
		
		mv.addObject("menuList",   menuList ); // pagingmenus.jsp
		mv.addObject("nowpage",    nowpage );  // pagingmenus.jsp, list.jsp
		
		mv.addObject("menu_id",    menu_id );   // list.jsp
		mv.addObject("response",   response );  // list.jsp 
		mv.addObject("searchVo",   searchVo );  // list.jsp
		mv.setViewName("boardpaging/list");
		return   mv;
		
	}
	
	//  /BoardPaging/WriteForm?menu_id=${ menu_id }&nowpage=${nowpage}
	@RequestMapping("/WriteForm")
	public  ModelAndView   writeForm(String menu_id, int nowpage) {
		
		// 메누 목록 조회
		List<MenuVo> menuList = menuMapper.getMenuList();
		
		ModelAndView  mv  = new ModelAndView();
		mv.addObject( "menu_id", menu_id  );
		mv.addObject( "nowpage", nowpage  );
		mv.addObject( "menuList", menuList );
		mv.setViewName("boardpaging/write");
		return mv;	
		
	}
	
	//  /BoardPaging/Write
	@RequestMapping("/Write")
	public  ModelAndView   write(
			 int nowpage, BoardVo boardVo) {
		
		boardPagingMapper.insertBoard( boardVo );
		
		ModelAndView  mv  = new ModelAndView();	
		String fmt = "redirect:/BoardPaging/List?menu_id={0}&nowpage={1}";
		String loc = MessageFormat.format(
				fmt, boardVo.getMenu_id(), nowpage );
		mv.setViewName(loc);
		return mv;	
		
	}
	
//  /BoardPaging/View?bno=1&menu_id=MENU01
	@RequestMapping("/View")
	//public  ModelAndView  view( int bno, String menuid ) {
	public  ModelAndView  view(int nowpage, BoardVo  boardVo ) {
		
		// 메뉴목록 조회(menus.jsp 용)
		List<MenuVo>  menuList =  menuMapper.getMenuList(); 
		
		// 조회수 증가( 현재 bno 의 HIT = HIT + 1 )
		boardPagingMapper.incHit( boardVo );
		
		//  bno 로 조회한 게시글 정보
		BoardVo       vo       =  boardPagingMapper.getBoard( boardVo  );   
		
		// vo.content 안의 \n 을 '<br>' 로 변경한다
		String   content  =  vo.getContent();  
		if(content != null) {
			content           =  content.replace("\n", "<br>");		
			vo.setContent(  content  );
		}
				
		ModelAndView  mv  =  new  ModelAndView();
		mv.addObject("menuList",  menuList );
		mv.addObject("nowpage",   nowpage );
		mv.addObject("vo", vo);
		mv.setViewName("boardpaging/view");
		return  mv;
		
	}
	
	//  /BoardPaging/Delete?bno=3&menu_id=MENU01
	@RequestMapping("/Delete")
	public   ModelAndView  delete(int nowpage, BoardVo  boardVo) {
		
		// 게시글 삭제
		boardPagingMapper.deleteBoard( boardVo );
		
		String   menu_id = boardVo.getMenu_id();
		
		// 다시 조회
		ModelAndView  mv  = new ModelAndView();	
		String fmt = "redirect:/BoardPaging/List?menu_id={0}&nowpage={1}";
		String loc = MessageFormat.format(
				fmt, boardVo.getMenu_id(), nowpage );
		mv.setViewName(loc);
		return mv;	
	}
		
	//   /BoardPaging/UpdateForm?bno=8&menu_id=MENU01&nowpage=1
	@RequestMapping("/UpdateForm")
	public  ModelAndView   updateForm(int nowpage, BoardVo boardVo ) {
		
		List<MenuVo>  menuList  =  menuMapper.getMenuList();
		
		BoardVo       vo        =  boardPagingMapper.getBoard( boardVo  );
		
		ModelAndView  mv        =  new ModelAndView();
		mv.addObject("menuList", menuList );
		mv.addObject("vo",       vo       );
		mv.addObject("nowpage",  nowpage  );
		mv.setViewName("boardpaging/update");  //update.jsp
		return  mv;
	}
	
	//  /BoardPaging/Update 
	@RequestMapping("/Update")
	public  ModelAndView  update( int nowpage, BoardVo boardVo ) {
		
		// 수정
		boardPagingMapper.updateBoard( boardVo   );
		
		String        menu_id =  boardVo.getMenu_id(); 
		
		ModelAndView  mv  = new ModelAndView();	
		String fmt = "redirect:/BoardPaging/List?menu_id={0}&nowpage={1}";
		String loc = MessageFormat.format(
				fmt, boardVo.getMenu_id(), nowpage );
		mv.setViewName(loc);
		return mv;		
	}
	
	
}

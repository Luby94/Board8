package com.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.board.domain.BoardVo;
import com.board.mapper.BoardMapper;
import com.board.menus.domain.MenuVo;
import com.board.menus.mapper.MenuMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/Board")
public class BoardController {

	@Autowired
	private   MenuMapper    menuMapper;
	
	@Autowired
	private   BoardMapper   boardMapper;
	
	//  /Board/List?menu_id=MENU01
	@RequestMapping("/List")
	//public   ModelAndView   list(@Param String  menu_id) {
	public   ModelAndView   list( MenuVo  menuVo ) {
		
		log.info("=============== menuVo : {}", menuVo );
		
		// 메뉴 목록
		List<MenuVo>  menuList   =  menuMapper.getMenuList();
		
		// 게시물 목록
		List<BoardVo> boardList  =  boardMapper.getBoardList( menuVo  ); 
		//System.out.println( boardList );
		
		MenuVo        mVo        =  menuMapper.getMenu( menuVo.getMenu_id() ); 
		String        menu_id    =  mVo.getMenu_id();
		String        menu_name  =  mVo.getMenu_name();
				
		ModelAndView  mv         =  new ModelAndView();
		mv.addObject("menu_id",    menu_id   );	
		mv.addObject("menu_name",  menu_name );
		mv.addObject("menuList",   menuList );
		mv.addObject("boardList",  boardList );
		mv.setViewName("board/list");
		return   mv;
		
	}
	
	//  /Board/WriteForm?menu_id=MENU01
	@RequestMapping("/WriteForm")
	public  ModelAndView   writeForm( MenuVo  menuVo  ) {
		
		// 메뉴 목록 조회
		List<MenuVo>  mList =  menuMapper.getMenuList(); 
		System.out.println( "[==MenuList==] :" + mList  );
		
		// ?menu_id=MENU01  넘어온 menu_id 를 처리
		String        menu_id  =  menuVo.getMenu_id(); 
		
		ModelAndView  mv       =  new ModelAndView();
		mv.addObject("menuList", mList  );
		mv.addObject("menu_id",  menu_id );
		mv.setViewName("board/write");
		return mv;	
		
	}
	
	//  /Board/Write
	//    menu_id=MENU01, title=aaa, writer=aaa, content=aaa
	@RequestMapping("/Write")
	public  ModelAndView   write( BoardVo boardVo  )   {
		
		// 넘어온 값 Board 저장
		boardMapper.insertBoard( boardVo );
				
		String        menu_id =  boardVo.getMenu_id();
		
		ModelAndView  mv      =  new ModelAndView();
		mv.setViewName("redirect:/Board/List?menu_id=" + menu_id);
		return        mv;
		
	}
	
	//  /Board/View?bno=1&menu_id=MENU01
	@RequestMapping("/View")
	//public  ModelAndView  view( int bno, String menuid ) {
	public  ModelAndView  view( BoardVo  boardVo ) {
		
		// 메뉴목록 조회(menus.jsp 용)
		List<MenuVo>  menuList =  menuMapper.getMenuList(); 
		
		// 조회수 증가( 현재 bno 의 HIT = HIT + 1 )
		boardMapper.incHit( boardVo );
		
		//  bno 로 조회한 게시글 정보
		BoardVo       vo       =  boardMapper.getBoard( boardVo  );   
		
		// vo.content 안의 \n 을 '<br>' 로 변경한다
		String   content  =  vo.getContent();  
		if(content != null) {
			content           =  content.replace("\n", "<br>");		
			vo.setContent(  content  );
		}
				
		ModelAndView  mv  =  new  ModelAndView();
		mv.addObject("menuList",  menuList );
		mv.addObject("vo", vo);
		mv.setViewName("board/view");
		return  mv;
		
	}
	
	//  /Board/Delete?bno=3&menu_id=MENU01
	@RequestMapping("/Delete")
	public   ModelAndView  delete(BoardVo  boardVo) {
		
		// 게시글 삭제
		boardMapper.deleteBoard( boardVo );
		
		String   menu_id = boardVo.getMenu_id();
		
		// 다시 조회
		ModelAndView   mv = new ModelAndView();
		mv.setViewName("redirect:/Board/List?menu_id=" + menu_id);
		return    mv;
	}
		
	//   /Board/UpdateForm?bno=8&menu_id=MENU01
	@RequestMapping("/UpdateForm")
	public  ModelAndView   updateForm( BoardVo boardVo ) {
		
		List<MenuVo>  menuList  =  menuMapper.getMenuList();
		
		BoardVo       vo        =  boardMapper.getBoard( boardVo  );
		
		ModelAndView  mv        =  new ModelAndView();
		mv.addObject("menuList", menuList );
		mv.addObject("vo",       vo );
		mv.setViewName("board/update");  //update.jsp
		return  mv;
	}
	
	//  /Board/Update 
	@RequestMapping("/Update")
	public  ModelAndView  update( BoardVo boardVo ) {
		
		// 수정
		boardMapper.updateBoard( boardVo   );
		
		String        menu_id =  boardVo.getMenu_id(); 
		
		ModelAndView  mv      =  new ModelAndView();
		mv.setViewName("redirect:/Board/List?menu_id=" + menu_id);
		return  mv;		
	}
	
}








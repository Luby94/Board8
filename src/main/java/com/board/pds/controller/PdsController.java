package com.board.pds.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.board.domain.BoardVo;
import com.board.mapper.BoardPagingMapper;
import com.board.menus.domain.MenuVo;
import com.board.menus.mapper.MenuMapper;
import com.board.pds.domain.FilesVo;
import com.board.pds.domain.PdsPagination;
import com.board.pds.domain.PdsPagingResponse;
import com.board.pds.domain.PdsSearchVo;
import com.board.pds.domain.PdsVo;
import com.board.pds.service.PdsService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/Pds")
public class PdsController {

	// application.properties 속성 가져오기
	@Value("${part4.upload-path}")	// import : springframework -> not lombok
	private String uploadPath;		// ${} 안에 있는 경로를 uploadPath 안에 넣어줌
	
	@Autowired
	private MenuMapper menuMapper;

	@Autowired
	private PdsService pdsService;
	
	@Autowired
	private BoardPagingMapper boardPagingMapper;

	// /Pds/List?nowpage=1&menu_id=MENU01
	// 파라미터를 받는 vo 가 없으므로 HashMap 이용해서 파라미터 처리
	// hashmap 으로 인자처리할때는 @RequestParam 필수
	@RequestMapping("/List")
	public ModelAndView list(
			@RequestParam HashMap<String, Object> map,
			int nowpage,
			BoardVo  boardVo,
			PdsVo pdsVo
			) {

		// map:{nowpage=1, menu_id=MENU01}
		log.info("========Pds/List===========");
		log.info("map : {}", map);
		log.info("========Pds/List===========");

		// 메뉴 목록
		List<MenuVo> menuList = menuMapper.getMenuList();
		log.info("========Pds/List===========");
		log.info("menuList : {}", menuList);
		log.info("========Pds/List===========");

		// 게시물 목록 페이징
		int count = boardPagingMapper.count(pdsVo);
		PdsPagingResponse<PdsVo> response = null;
		if (count < 1) {
			response = new PdsPagingResponse<>(Collections.emptyList(), null);
		}

		// 페이징을 위한 초기설정값
		PdsSearchVo searchVo = new PdsSearchVo();
		searchVo.setPage(nowpage);

		// Pagination 객체를 생성해서 페이지 정보 계산 후 SearchDto 타입의 객체인 params에 계산된 페이지 정보 저장
		PdsPagination pagination = new PdsPagination(count, searchVo);
		searchVo.setPdspagination(pagination);

		String menu_id = pdsVo.getMenu_id();
		String title = pdsVo.getTitle();
		String writer = pdsVo.getWriter();
		int offset = searchVo.getOffset();
		int pageSize = searchVo.getPageSize();

		// pdsService - db (mapper) + 추가적인 로직(비지니스)
		// 자료실 목록 조회 : Board + Files
		//List<PdsVo> pdsList = pdsService.getPdsList(map);
		// 계산된 페이지 정보의 일부(limitStart, recordSize)를 기준으로 리스트 데이터 조회 후 응답 데이터 반환
		List<PdsVo> pdsList = pdsService.getPdsPagingList(menu_id, title, writer, offset, pageSize);
		log.info("========Pds/List===========");
		log.info("pdsList : {}", pdsList);
		log.info("========Pds/List===========");
		
		response = new PdsPagingResponse<>(pdsList, pagination);

		ModelAndView mv = new ModelAndView();
		mv.addObject("menuList", menuList); // 메뉴 목록
		mv.addObject("pdsList", pdsList); // 자료실 목록 Board + Files
		mv.addObject("map", map);
		mv.addObject("nowpage", nowpage);
		mv.addObject("searchVo", searchVo);
		mv.addObject("response", response);
		mv.setViewName("pds/list"); // pds/list.jsp
		return mv;
	}

	// /Pds/View?bno=1009&nowpage=1&menu_id=MENU01
	@RequestMapping("/View")
	public ModelAndView view(@RequestParam HashMap<String, Object> map) {

		// 메뉴 목록
		List<MenuVo> menuList = menuMapper.getMenuList();
		log.info("========Pds/View===========");
		log.info("menuList : {}", menuList);
		log.info("========Pds/View===========");

		// 조회수 증가
		pdsService.setReadCountUpdate(map);

		// 조회할 자료실의 게시물정보 : BoardVo -> PdsVo
		// PdsVo ( BoardVo + FilesVo )
		PdsVo pdsVo = pdsService.getPds(map);
		log.info("========Pds/View===========");
		log.info("pdsVo : {}", pdsVo);
		log.info("========Pds/View===========");

		// 조회할 파일정보 : FilesVo -> PdsVo
		// Bno 에 해당되는 파일들의 정보
		List<FilesVo> fileList = pdsService.getFileList(map);
		log.info("========Pds/View===========");
		log.info("fileList : {}", fileList);
		log.info("========Pds/View===========");

		ModelAndView mv = new ModelAndView();
		mv.addObject("menuList", menuList);
		mv.addObject("vo", pdsVo);
		mv.addObject("fileList", fileList);
		mv.addObject("map", map); // map : 넘겨줬을때 바뀌어진 값까지 넘겨줌
		mv.setViewName("pds/view");
		return mv;
	}

	// /Pds/WriteForm?nowpage=1&menu_id=MENU01
	@RequestMapping("/WriteForm")
	public ModelAndView writeForm(@RequestParam HashMap<String, Object> map) { // map : 메뉴정보, 현재 페이지 정보 받기위함

		List<MenuVo> menuList = menuMapper.getMenuList();
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("map", map);
		mv.addObject("menuList", menuList);
		mv.setViewName("pds/write");
		return mv;
	}
	
	// /Pds/Write  - 자료실 저장 (글 + 파일들)
	@RequestMapping("/Write")
	public ModelAndView write(
			@RequestParam HashMap<String, Object> map,
			@RequestParam(value="nowpage") int nowpage,
			@RequestParam(value="upfile", required=false) MultipartFile[] uploadFiles
			) {
		// required=false : 입력하지 않을 수 있다(필수입력 아닐 수 있음)
		// value="upfile" : write.jsp 의 <input type="file" name="upfile" class="upfile" multiple /> 의 name="upfile"
		
		// 넘어온 정보
		log.info("=========Pds/Write===========");
		log.info("map : {}", map);
		log.info("nowpage : {}", nowpage);
		log.info("files : {}", uploadFiles);
		log.info("=========Pds/Write===========");

		// 저장
		
		// 1. map 정보
		// 새글 저장 → Board table 저장
		
		// 2. MultipartFile [] 정보 활용
		// 2-1. 실제 폴더에 파일 저장 → uploadPath (d:\dev\data 폴더)
		// 2-2. 저장된 파일정보를 db 에 저장 → Files table 저장
		pdsService.setWrite(map, uploadFiles);
		// ① Board 에 map 저장
		// ② Files 에 저장된 파일정보를 저장
		// ③ 파일 : 디스크에 저장
		
		//String menu_id = "MENU01";
		// setViewName 설정1
		//String fmt = "redirect:/Pds/List?menu_id=%s&nowpage=%d";
		//String loc = String.format(fmt, map.get("menu_id"), map.get("nowpage"));
		//mv.setViewName( loc );
		// setViewName 설정2
		//String loc  = "redirect:/Pds/List"; 
		//	   loc += "?menu_id=" + map.get("menu_id");
		//     loc += "&nowpage=" + map.get("nowpage");
		//mv.setViewName( loc );
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("map", map);
		mv.addObject("nowpage", nowpage);
		//mv.setViewName("redirect:/Pds/List?menu_id=" + map.get("menu_id") + "&nowpage=" + nowpage);  →  주소줄 nowpage 2개 생김
		mv.setViewName("redirect:/Pds/List?menu_id=" + map.get("menu_id"));
		return mv;
		
	}
	
	// 자료실 글 삭제
	// /Pds/Delete?bno=${ vo.bno }&menu_id=${ vo.menu_id}&nowpage=${map.nowpage}
	@RequestMapping("/Delete")
	public ModelAndView delete( @RequestParam HashMap<String, Object> map ) {
		
		// 1. PdsServiceImpl.java 에서 '1. 해당파일 삭제' 작업 위해
		// 1-1. List<FilesVo> fileList = map.get("fileList"); 해야함
		// 1-2. map 에 fileList 있는지 확인
		log.info("======Pds/Delete======");
		log.info("map : {}", map);
		log.info("======Pds/Delete======");
		// 1-3. map : {bno=500, menu_id=MENU01, nowpage=1}
		// 1-4. map 에 fileList 없음 → List<FilesVo> fileList = pdsMapper.getFileList(map); 로 수정
		
		// 삭제
		pdsService.setDelete( map );
		
		ModelAndView mv = new ModelAndView();
		String loc = "redirect:/Pds/List?menu_id=" + map.get("menu_id") + "&nowpage=" + map.get("nowpage");
		mv.setViewName( loc );
		return mv;
		
	}
	
	// 자료실 글 수정
	// /Pds/UpdateForm?bno=${ vo.bno }&menu_id=${ vo.menu_id }&nowpage=${map.nowpage}
	@RequestMapping("/UpdateForm")
	public ModelAndView updateform( @RequestParam HashMap<String, Object> map ) {
		
		log.info("======Pds/UpdateForm======");
		log.info("map1: {}", map);
		log.info("======Pds/UpdateForm======");
		// map: {bno=497, menu_id=MENU01, nowpage=1}
		
		// 메뉴 목록
		List<MenuVo> menuList = menuMapper.getMenuList();
		
		// 수정할 게시글 조회
		PdsVo pdsVo = pdsService.getPds(map);
		log.info("======Pds/UpdateForm======");
		log.info("pdsVo: {}", pdsVo);
		log.info("======Pds/UpdateForm======");
		// pdsVo: PdsVo(bno=497, title=ㄱㄱㄱ2233, content=ㄱㄱㄱ2233, writer=ㄱㄱㄱ, regdate=2024-05-28 15:00:51, hit=27, filescount=0, menu_id=MENU01, menu_name=null, menu_seq=0) 
		
		// 수정할 파일들 정보(fileList)
		List<FilesVo> fileList = pdsService.getFileList(map);
		log.info("======Pds/UpdateForm======");
		log.info("fileList: {}", fileList);
		log.info("======Pds/UpdateForm======");
		// fileList: [FilesVo(file_num=25, bno=497, filename=disp.png, fileext=.png, sfilename=2024\05\28\6af26487-9407-4617-b434-f5b52398ee32_disp.png)]
		
		map.put("fileList", fileList);
		log.info("======Pds/UpdateForm======");
		log.info("map2: {}", map);
		log.info("======Pds/UpdateForm======");

		ModelAndView mv = new ModelAndView();
		mv.addObject("map", map);
		mv.addObject("menuList", menuList);
		mv.addObject("vo", pdsVo);
		mv.addObject("fileList", fileList);
		mv.setViewName( "pds/update" );
		return mv;
		
	}
	
	// /Pds/Update
	@RequestMapping("/Update")
	public ModelAndView update( 
			@RequestParam HashMap<String, Object> map,
			@RequestParam(value="upfile", required=false) MultipartFile [] uploadFiles
			) {
		
		log.info("======Pds/Update======");
		log.info("map: {}", map);
		log.info("uploadFiles: {}", uploadFiles);
		log.info("======Pds/Update======");
		// map: {bno=497, menu_id=MENU01, nowpage=1, title=ㄱㄱㄱ2233, content=ㄱㄱㄱ2233}
		// uploadFiles: org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$StandardMultipartFile@23df6c82
		
		List<FilesVo> fileList = pdsService.getFileList(map);
		log.info("======Pds/Update======");
		log.info("fileList: {}", fileList);
		log.info("======Pds/Update======");
		
		//map.put("fileList", fileList);
		map.put("fileList", fileList);
	    if (!fileList.isEmpty()) {
	        map.put("file_num", fileList.get(0).getFile_num());
	    }
	    log.info("======Pds/Update======");
	    log.info("map: {}", map);
	    log.info("======Pds/Update======");
	    
	    map.put("bno", map.get("bno")); // Ensure bno is put correctly
	    log.info("======Pds/Update======");
	    log.info("map: {}", map);
	    log.info("======Pds/Update======");
		
		pdsService.setUpdate( map, uploadFiles );
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("map", map);
		//String loc = "redirect:/Pds/List?menu_id=" + map.get("menu_id") + "&nowpage=" + map.get("nowpage");
		String loc = "redirect:/Pds/View?menu_id="
							+ map.get("menu_id")
							+ "&nowpage="
							+ map.get("nowpage")
							+ "&bno="
							+ map.get("bno");
		mv.setViewName( loc );
		return mv;
		
	}
	
	
	//-----------------------------------------------------------------------------------------------------
	// 파일 다운로드
	//   ※ Controller : ModelAndView 를 return 함
	// 메소드는 파일을 리턴한다, ModelAndView 가 아니라 : @ResponseBody (모델구조 아닌 것은 @ResponseBody 붙여줘야함)
	// Controller + @ResponseBody = RestController
	// 서버에서 파일을 보내주어야 함, binary data
	//-----------------------------------------------------------------------------------------------------
	
	@GetMapping("/filedownload/{file_num}")
	@ResponseBody
	public void downloadFile(
			@PathVariable(value="file_num") Long file_num,
			HttpServletResponse res
		) throws UnsupportedEncodingException {
		
		// 파일을 조회 (Files)
		FilesVo fileInfo = pdsService.getFileInfo( file_num );
		// ≒ pdsService.getPds
		
		// 파일경로 (Path → import nio, Paths → ctrl+shift+o)
		Path saveFilePath = Paths.get( uploadPath + java.io.File.separator + fileInfo.getSfilename() );
		
		// 해당경로에 파일이 없으면
		//if( !saveFilePath ) {	// 빨간줄 에러 → 아래와 같이 해야함
		if( !saveFilePath.toFile().exists() ) {
			throw new RuntimeException( "file not found" );
		}
		
		// 파일헤더 설정
		//setFileHeader(res, fileInfo);	// add throw
		setFileHeader(res, fileInfo);
		
		// 파일복사
		fileCopy( res, saveFilePath );
		
	}
	
	// 파일복사
	private void fileCopy(HttpServletResponse res, Path saveFilePath) {
		
		FileInputStream fis = null;
		
		//fis = new FileInputStream( saveFilePath );	// .toFile() 필요
		//fis = new FileInputStream( saveFilePath.toFile() );	// F2 → Surround with try/catch
		try {
			fis = new FileInputStream( saveFilePath.toFile() );
			FileCopyUtils.copy( fis, res.getOutputStream() );	// F2 → add catch  clause to surrounding try → io 추가
			res.getOutputStream().flush();
		}  catch (Exception e) {
			throw new RuntimeException(e);	// 사용자 정의 예외
		} finally {
			//fis.close();	// F2 → Surround with try/catch
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	// 다운받을 파일의 header 정보
	private void setFileHeader(HttpServletResponse res, FilesVo fileInfo) throws UnsupportedEncodingException {
		
	        res.setHeader("Content-Disposition", "attachment; filename=\""
	        				+ URLEncoder.encode( (String) fileInfo.getFilename(), "UTF-8") + "\";");
	        res.setHeader("Content-Transfer-Encoding", "binary");
	        res.setHeader("Content-Type", "application/download; utf-8");
	        res.setHeader("Pragma", "no-cache;");
	        res.setHeader("Expires", "-1;");
	        
	    }

}


















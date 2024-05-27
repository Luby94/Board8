package com.board.pds.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.board.domain.BoardVo;
import com.board.pds.domain.FilesVo;
import com.board.pds.domain.PdsVo;
import com.board.pds.mapper.PdsMapper;
import com.board.pds.service.PdsService;

@Service
public class PdsServiceImpl implements PdsService {

	@Value("${part4.upload-path}")
	private String uploadPath;
	
	@Autowired
	private  PdsMapper  pdsMapper;
	
	@Override
	public List<PdsVo> getPdsList(HashMap<String, Object> map) {
		// map { "menu_id" : "MENU01" , "nowpage" : 1 }
		// db 조회 결과 돌려준다
		List<PdsVo> pdsList = pdsMapper.getPdsList(map); 
		System.out.println("pdsService pdsList:" + pdsList);
		return      pdsList;
	}

	@Override
	public PdsVo getPds(HashMap<String, Object> map) {
		
		PdsVo pdsVo = pdsMapper.getPds(map);
		
		return pdsVo;
	}

	@Override
	public List<FilesVo> getFileList(HashMap<String, Object> map) {
		
		List<FilesVo> fileList = pdsMapper.getFileList( map );
		
		return fileList;
	}

	@Override
	public List<PdsVo> getPdsPagingList(String menu_id, String title, String writer, int offset, int pageSize) {
		
		List<PdsVo> list = pdsMapper.getPdsPagingList(menu_id, title, writer, offset, pageSize);
		
		return list;
	}

	@Override
	public void setWrite(HashMap<String, Object> map, MultipartFile[] uploadFiles) {
		
		System.out.println( "=====PdsServiceImpl_map_1: " + map );
		//=====PdsServiceImpl_map_1: {menu_id=MENU01, nowpage=1, title=aa, writer=aa, content=aaa}
		System.out.println( "=====PdsServiceImpl_uploadFiles: " + uploadFiles );
		//=====PdsServiceImpl_uploadFiles: [Lorg.springframework.web.multipart.MultipartFile;@23aaa474
		
		// 파일 저장 + 자료실 글쓰기
		
		// uploadFiles [] 을 d:\dev\data
		map.put("uploadPath", uploadPath);
		
		// PdsFile class : 파일처리 전담 클래스 생성
		// 1. 파일저장
		// 2. 저장된 파일정보 가져온다 → 나중에 DB 에 저장)
		PdsFile.save( map, uploadFiles );
		
		// map 이 중요한 역할을 함
		System.out.println( "=====PdsServiceImpl_map_2: " + map );
		//=====PdsServiceImpl_map_1: {menu_id=MENU01, nowpage=1, title=aa, writer=aa, content=aaa}
		//=====PdsServiceImpl_map_2: {menu_id=MENU01, nowpage=1, title=aa, writer=aa, content=aaa, uploadPath=D:/dev/data/, fileList=}

		// db 저장
		// 3. Board 에 글 저장
		pdsMapper.setWrite( map );
		
		// 4. Files 에 저장된 파일정보를 저장
		List<FilesVo> fileList = (List<FilesVo>) map.get("fileList");
		if( fileList.size() != 0 ) {
			pdsMapper.setFileWrite( map );
		}
		
	}

}




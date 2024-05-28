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

	@Value("${part4.upload-path}")	// application.properties 에 有 , 이 값(= D:/dev/data/)을 uploadPath 에 넣음
	private String uploadPath;
	
	@Autowired
	private  PdsMapper  pdsMapper;
	
	// 자료실 목록 보기
	@Override
	public List<PdsVo> getPdsList(HashMap<String, Object> map) {
		// map { "menu_id" : "MENU01" , "nowpage" : 1 }
		// db 조회 결과 돌려준다
		List<PdsVo> pdsList = pdsMapper.getPdsList(map);

		return      pdsList;
	}

	// 자료실 내용 보기
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

	// 자료실 목록 보기 - 페이징
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

	@Override
	public void setReadCountUpdate(HashMap<String, Object> map) {
		// 조회수 증가
		pdsMapper.setReadCountUpdate(map);
		
	}

	// 파일을 조회 (Files)
	@Override
	public FilesVo getFileInfo(Long file_num) {
		
		FilesVo filesVo = pdsMapper.getFileInfo( file_num );
		
		return filesVo;
	}

	// 자료실 글 삭제
	@Override
	public void setDelete(HashMap<String, Object> map) {
		
		// 1. 해당 파일 삭제
		//List<FilesVo> fileList = map.get("fileList");
		List<FilesVo> fileList = pdsMapper.getFileList(map);
		System.out.println( "=====delete fileList: " + fileList );
		// 실제 물리적인 파일 삭제
		PdsFile.delete( uploadPath, fileList );
		
		// 2. Files Table 정보 삭제
		pdsMapper.deleteUploadFile( map );
		
		// 3. Board Table 정보 삭제
		pdsMapper.setDelete( map );
		
	}

	// 자료실 글 수정
	@Override
	public void setUpdate(HashMap<String, Object> map, MultipartFile[] uploadFiles) {
		
		/*
		 * List<FilesVo> sfileList = (List<FilesVo>) map.get("fileList"); int file_num =
		 * -1; // 초기값 설정 for (FilesVo file : sfileList) {
		 * System.out.println(file.getFile_num()); file_num = file.getFile_num(); }
		 * System.out.println( "=====setUpdate_file_num: " + file_num );
		 */
		
		// 업로드된 파일을 물리저장소 저장
		map.put("uploadPath", uploadPath);
		System.out.println( "=====setUpdate_map1: " + map );
		
		PdsFile.save( map, uploadFiles );	// 파일 저장하고 fileList 에 저장된 정보 map 에 담겨져서 return
		System.out.println( "=====setUpdate_map2: " + map );
		
		// Files 정보를 추가 (fileList)
		List<FilesVo> fileList = (List<FilesVo>) map.get("fileList");
		System.out.println( "=====setUpdate_fileList: " + fileList );		
		
		if( fileList.size() != 0 ) {
			pdsMapper.setFileWrite( map );
		}
				
		// Board 정보를 수정
		pdsMapper.setUpdate( map );
		//pdsMapper.setUpdate( map, fileList );
		
	}

}




package com.board.pds.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.board.pds.domain.FilesVo;

public class PdsFile {

	// uploadPath 에 넘어온 파일들을 저장
	public static void save(HashMap<String, Object> map, MultipartFile[] uploadFiles) {
		
		// 저장될 경로 가져온다
		// String uploadPath = map.get("uploadPath");
		// (String) map.get("uploadPath");  →  F2 + add Cast 한거 : 제대로 작동안함
		String uploadPath = String.valueOf( map.get("uploadPath") );
		// uploadPath : PdsServiceImpl 에서 넘겨준거
		// PdsFile.save( map, uploadFiles );
		
		System.out.println( "=====PdsFile_uploadPath: " + uploadPath + " , uploadFiles length: " + uploadFiles.length );
		//=====PdsFile_uploadPath: D:/dev/data/ , uploadFiles length: 2
		
		List<FilesVo> fileList = new ArrayList<>();
		
		for(MultipartFile uploadFile : uploadFiles) {
			
			String originalName = uploadFile.getOriginalFilename();
			System.out.println( "=====PdsFile_originalName: " + originalName);
			//=====PdsFile_originalName: 0514(화) 산업현장 특강.txt
			//=====PdsFile_originalName: disp.png
			
			// c:\download\data\data.abc.txt
			String fileName = originalName.substring( originalName.lastIndexOf("\\") + 1 );	// data.abc.txt
			// .substring( originalName.lastIndexOf("\\") + 1 ); → 마지막 역슬러쉬(\) 에서 끝까지 자르라 → data.abc.txt 뽑을 수 있음
			
			String fileExt = originalName.substring( originalName.lastIndexOf(".") );	// .txt
			
			// d:\dev\data\2024\05\27
			// 날짜 폴더 생성
			String folderPath = makeFolder( uploadPath );
			// uploadPath = 업로드 경로 : d:\dev\data
			// 날짜 \2024\05\27 를 받아서 폴더 생성 → makeFolder 한테 맡김 → makeFolder : private 함수 아래에 생성
			
			// 파일명 중복방지 - 같은 폴더에는 마지막 업로드 된 파일만 저장
			// 중복하지 않는 고유한 문자열 생성 : UUID
			String uuid = UUID.randomUUID().toString();
			
			String saveName  = uploadPath + File.separator + folderPath + File.separator + uuid + "_" + fileName;
			//                d:\dev\data +       \       + 2024\05\27 +        \       + uuid +  _  + data.abc.txt
			
			// saveName2 : Files table 의 sfilename (전체경로가 있으면 안됨 → uploadPath 뺀 것)
			String saveName2 = folderPath + File.separator + uuid + "_" + fileName;
			
			Path savePath = Paths.get(saveName);
			// Path : java.nio.file.Path → import
			// nio : network io (네트워크 전문의 io, 기존의 io 보다 함수가 몇개 추가, Path 도 그중 하나)
			// Paths.get() : 특정 경로의 파일정보를 가져온다
			
			// 파일 저장
			//uploadFile.transferTo(savePath);	// F2 → Surround with try/catch
			//System.out.println( "저장됨" );
			try {
				uploadFile.transferTo(savePath);	// 업로드된 파일을 폴더에 저장(.transferTo 가 저장하는 역할)
				System.out.println( "저장됨" );
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	// try End
			
			// 저장된 파일들의 정보를 map 에 List 로 저장 → PdsServiceImpl 에서 사용하기 위함
			FilesVo vo = new FilesVo(0, 0, fileName, fileExt, saveName2);
			fileList.add( vo );
			
		}	// end for
		
		//map.put("aaa", 1234);
		map.put("fileList", fileList);
		
	}	// save() End

	private static String makeFolder( String uploadPath ) {	// static : 나중에 위 makeFolder 빨간줄 F2 해서 2번째꺼 클릭함 
		
		// d:\dev\data\2024\05\27
		String dateStr = LocalDate.now().format( DateTimeFormatter.ofPattern("yyyy/MM/dd") );
		
		//String folderPath = dateStr.replace("/", "\\");  →  window 기준, mac 에는 안먹힘
		String folderPath = dateStr.replace("/", File.separator);	// mac 에도 먹히는 코딩
		
		File uploadPathFolder = new File( uploadPath, folderPath );
		// uploadPath : d:\dev\data
		// folderPath : \2024\05\27
		
		System.out.println( "=====PdsFile_uploadPathFolder1: " + uploadPathFolder.toPath().getFileSystem() );
		//=====PdsFile_uploadPathFolder1: sun.nio.fs.WindowsFileSystem@5edceeda
		System.out.println( "=====PdsFile_uploadPathFolder2: " + uploadPathFolder.toString() );
		//=====PdsFile_uploadPathFolder2: D:\dev\data\2024\05\27
		
		if( uploadPathFolder.exists() == false ) {
			uploadPathFolder.mkdirs();	// mkdir : make directory
			// mkdir()  : 상위 폴더가 없으면 폴더 전체를 만들지 못한다 (x)
			// mkdirs() : 상위 폴더가 없어도 폴더 전체를 만들어준다
		}
		
		return folderPath;
	}
	
}

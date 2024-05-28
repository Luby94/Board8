package com.board.pds.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.multipart.MultipartFile;

import com.board.pds.domain.FilesVo;
import com.board.pds.domain.PdsVo;

// @Mapper : MyBatis 와 연결하기 위해 존재
@Mapper
public interface PdsMapper {

	List<PdsVo> getPdsList(HashMap<String, Object> map);

	PdsVo getPds(HashMap<String, Object> map);

	List<FilesVo> getFileList(HashMap<String, Object> map);

	List<PdsVo> getPdsPagingList(String menu_id, String title, String writer, int offset, int pageSize);

	void setWrite(HashMap<String, Object> map);

	void setFileWrite(HashMap<String, Object> map);

	void setReadCountUpdate(HashMap<String, Object> map);

	FilesVo getFileInfo(Long file_num);

	void deleteUploadFile(HashMap<String, Object> map);
	void setDelete(HashMap<String, Object> map);
	void deleteUploadFileNum(HashMap<String, Object> map);

	void setUpdate(HashMap<String, Object> map);
	//void setUpdate(HashMap<String, Object> map, List<FilesVo> fileList);


}

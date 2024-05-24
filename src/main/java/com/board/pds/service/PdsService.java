package com.board.pds.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.board.pds.domain.FilesVo;
import com.board.pds.domain.PdsVo;

public interface PdsService {

	List<PdsVo> getPdsList(HashMap<String, Object> map);

	PdsVo getPds(HashMap<String, Object> map);

	List<FilesVo> getFileList(HashMap<String, Object> map);

	List<PdsVo> getPdsPagingList(String menu_id, String title, String writer, int offset, int pageSize);

	void setWrite(HashMap<String, Object> map, MultipartFile[] uploadFiles);

}

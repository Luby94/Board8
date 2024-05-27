package com.board.pds.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

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

}

package com.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.board.domain.BoardVo;
import com.board.pds.domain.PdsVo;

@Mapper
public interface BoardPagingMapper {

	int count(BoardVo boardVo);

	List<BoardVo> getBoardPagingList(
			String menu_id, String title, String writer, int offset, int pageSize);

	void insertBoard(BoardVo boardVo);

	void updateBoard(BoardVo boardVo);

	void deleteBoard(BoardVo boardVo);

	void incHit(BoardVo boardVo);

	BoardVo getBoard(BoardVo boardVo);

	int count(PdsVo pdsVo);

}
